package com.perlagloria.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.perlagloria.R;
import com.perlagloria.activity.ChooseTeamActivity;
import com.perlagloria.adapter.DivisionListAdapter;
import com.perlagloria.model.Division;
import com.perlagloria.responder.ServerRequestListener;
import com.perlagloria.responder.ServerResponseErrorListener;
import com.perlagloria.util.AppController;
import com.perlagloria.util.ErrorAlertDialog;
import com.perlagloria.util.ServerApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Activities that contain this fragment must implement the
 * {@link SelectDivisionFragment.OnDivisionPassListener} interface
 * to handle interaction events.
 * Use the {@link SelectDivisionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectDivisionFragment extends Fragment implements DivisionListAdapter.OnCheckboxCheckedListener {
    private static final String CUSTOMER_NAME = "customerName";
    private static final String TOURNAMENT_ID = "tournamentId";
    private static final String TOURNAMENT_NAME = "tournamentName";
    private static final String LOADING_DIVISIONS_LIST_TAG = "divisions_list_loading";
    private int tournamentId;
    private String tournamentName;
    private String customerName;

    private LinearLayoutManager mLayoutManager;
    private RecyclerView divisionListRecView;
    private DivisionListAdapter divisionListAdapter;
    private ArrayList<Division> divisionArrayList;

    private TextView champValueTextView;
    private TextView tournValueTextView;
    private TextView divisValueTextView;

    private OnDivisionPassListener divisionPassListener;  //pass selected division back to the activity

    public SelectDivisionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param customerName   selected customer name.
     * @param tournamentId   selected tournament id.
     * @param tournamentName selected tournament name.
     * @return A new instance of fragment SelectDivisionFragment.
     */
    public static SelectDivisionFragment newInstance(String customerName, int tournamentId, String tournamentName) {
        SelectDivisionFragment fragment = new SelectDivisionFragment();
        Bundle args = new Bundle();
        args.putString(CUSTOMER_NAME, customerName);
        args.putInt(TOURNAMENT_ID, tournamentId);
        args.putString(TOURNAMENT_NAME, tournamentName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerName = getArguments().getString(CUSTOMER_NAME);
            tournamentId = getArguments().getInt(TOURNAMENT_ID);
            tournamentName = getArguments().getString(TOURNAMENT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_division, container, false);
        champValueTextView = (TextView) rootView.findViewById(R.id.champValueTextView);
        champValueTextView.setText(customerName);
        tournValueTextView = (TextView) rootView.findViewById(R.id.tournValueTextView);
        tournValueTextView.setText(tournamentName);
        divisValueTextView = (TextView) rootView.findViewById(R.id.divisValueTextView);

        ((ChooseTeamActivity) getActivity()).setToolbarTitle(getString(R.string.toolbar_choose_team_title));

        divisionArrayList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());

        divisionListRecView = (RecyclerView) rootView.findViewById(R.id.container_divisions);
        divisionListAdapter = new DivisionListAdapter(divisionArrayList, this);
        divisionListRecView.setAdapter(divisionListAdapter);
        divisionListRecView.setItemAnimator(null);
        divisionListRecView.setLayoutManager(mLayoutManager);

        loadDivisionInfo();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            divisionPassListener = (OnDivisionPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDivisionPassListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        divisionPassListener = null;
    }

    /**
     * Is being executed after any checkbox was checked in recycleview
     */
    @Override
    public void onCheckboxChecked(Division division) {
        divisValueTextView.setText(division.getName());

        if (divisionPassListener != null) {
            divisionPassListener.onDivisionPass(division);
        }
    }

    private void loadDivisionInfo() {
        String loadDivisionUrl = ServerApi.loadDivisionUrl + tournamentId;
        ServerRequestListener requestResponder = (ServerRequestListener) getActivity();
        requestResponder.onRequestStarted();

        JsonArrayRequest divisionsJsonRequest = new JsonArrayRequest(loadDivisionUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_DIVISIONS_LIST_TAG, response.toString());
                        ServerRequestListener requestResponder = (ServerRequestListener) getActivity();
                        requestResponder.onRequestFinished();

                        if (!parseDivisionsJson(response)) {                                        //case of response parse error
                            Toast.makeText(getActivity(), R.string.no_info_from_server, Toast.LENGTH_LONG).show();
                        } else {
                            divisionListAdapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_DIVISIONS_LIST_TAG, "Error: " + error.getMessage());
                        ServerRequestListener requestResponder = (ServerRequestListener) getActivity();
                        requestResponder.onRequestFinished();

                        ServerResponseErrorListener responseResponder = (ServerResponseErrorListener) getActivity();
                        responseResponder.onServerResponseError(ErrorAlertDialog.getVolleyErrorMessage(error));
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(divisionsJsonRequest, LOADING_DIVISIONS_LIST_TAG); // Adding request to request queue
    }

    private boolean parseDivisionsJson(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);

                Division division = new Division(obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("createdDate"),
                        obj.getBoolean("isActive"),
                        false);

                divisionArrayList.add(division);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * Pass data back to the activity (selected division)
     */
    public interface OnDivisionPassListener {
        void onDivisionPass(Division division);
    }
}
