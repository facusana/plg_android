package com.perlagloria.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.perlagloria.R;
import com.perlagloria.activity.ChooseTeamActivity;
import com.perlagloria.adapter.TournamentListAdapter;
import com.perlagloria.model.Tournament;
import com.perlagloria.responder.ServerRequestListener;
import com.perlagloria.responder.ServerResponseErrorListener;
import com.perlagloria.util.AppController;
import com.perlagloria.util.ErrorAlertDialog;
import com.perlagloria.util.FontManager;
import com.perlagloria.util.ServerApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Activities that contain this fragment must implement the
 * {@link SelectTournamentFragment.OnTournamentPassListener} interface
 * to handle interaction events.
 * Use the {@link SelectTournamentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectTournamentFragment extends Fragment implements TournamentListAdapter.OnCheckboxCheckedListener {
    private static final String CUSTOMER_ID = "customerId";
    private static final String CUSTOMER_NAME = "customerName";
    private static final String LOADING_TOURNAMENTS_LIST_TAG = "tournaments_list_loading";
    private int customerId;
    private String customerName;

    private RecyclerView tournamentListRecView;
    private TournamentListAdapter tournamentListAdapter;
    private ArrayList<Tournament> tournamentArrayList;

    private TextView champTextView;
    private TextView champValueTextView;
    private TextView tournTextView;
    private TextView tournValueTextView;

    private OnTournamentPassListener tournamentPassListener;  //pass selected tournament back to the activity

    public SelectTournamentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param customerId   selected customer id.
     * @param customerName selected customer name.
     * @return A new instance of fragment SelectTournamentFragment.
     */
    public static SelectTournamentFragment newInstance(int customerId, String customerName) {
        SelectTournamentFragment fragment = new SelectTournamentFragment();
        Bundle args = new Bundle();
        args.putInt(CUSTOMER_ID, customerId);
        args.putString(CUSTOMER_NAME, customerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerId = getArguments().getInt(CUSTOMER_ID);
            customerName = getArguments().getString(CUSTOMER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_tournament, container, false);
        champTextView = (TextView) rootView.findViewById(R.id.champTextView);
        champValueTextView = (TextView) rootView.findViewById(R.id.champValueTextView);
        champValueTextView.setText(customerName);
        tournTextView = (TextView) rootView.findViewById(R.id.tournTextView);
        tournValueTextView = (TextView) rootView.findViewById(R.id.tournValueTextView);

        ((ChooseTeamActivity) getActivity()).setToolbarTitle(getString(R.string.toolbar_choose_team_title));

        tournamentArrayList = new ArrayList<>();
        tournamentListRecView = (RecyclerView) rootView.findViewById(R.id.container_tournaments);
        tournamentListAdapter = new TournamentListAdapter(getActivity(), tournamentArrayList, this);
        tournamentListRecView.setAdapter(tournamentListAdapter);
        tournamentListRecView.setItemAnimator(null);
        tournamentListRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        champTextView.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_MEDIUM, getActivity()));
        champValueTextView.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
        tournTextView.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_MEDIUM, getActivity()));
        tournValueTextView.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));

        loadTournamentInfo();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            tournamentPassListener = (OnTournamentPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTournamentPassListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tournamentPassListener = null;
    }

    /**
     * Is being executed after any checkbox was checked in recycleview
     */
    @Override
    public void onCheckboxChecked(Tournament tournament) {
        tournValueTextView.setText(tournament.getName());

        if (tournamentPassListener != null) {
            tournamentPassListener.onTournamentPass(tournament);
        }
    }

    private void loadTournamentInfo() {
        String loadTournamentUrl = ServerApi.loadTournamentUrl + customerId;
        ServerRequestListener requestResponder = (ServerRequestListener) getActivity();
        requestResponder.onRequestStarted();

        JsonArrayRequest tournamentsJsonRequest = new JsonArrayRequest(loadTournamentUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_TOURNAMENTS_LIST_TAG, response.toString());
                        ServerRequestListener requestResponder = (ServerRequestListener) getActivity();
                        requestResponder.onRequestFinished();

                        if (!parseTournamentsJson(response)) {                                      //case of response parse error
                            ServerResponseErrorListener responseResponder = (ServerResponseErrorListener) getActivity();
                            responseResponder.onServerResponseError(ErrorAlertDialog.NO_INFO_FROM_SERVER);
                        } else {
                            tournamentListAdapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_TOURNAMENTS_LIST_TAG, "Error: " + error.getMessage());
                        ServerRequestListener requestResponder = (ServerRequestListener) getActivity();
                        requestResponder.onRequestFinished();

                        ServerResponseErrorListener responseResponder = (ServerResponseErrorListener) getActivity();
                        responseResponder.onServerResponseError(ErrorAlertDialog.getVolleyErrorMessage(error));
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(tournamentsJsonRequest, LOADING_TOURNAMENTS_LIST_TAG); // Adding request to request queue
    }

    private boolean parseTournamentsJson(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);

                Tournament tournament = new Tournament(obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("createdDate"),
                        obj.getBoolean("isActive"),
                        false);

                tournamentArrayList.add(tournament);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * Pass data back to the activity (selected tournament)
     */
    public interface OnTournamentPassListener {
        void onTournamentPass(Tournament tournament);
    }
}
