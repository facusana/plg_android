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
import com.perlagloria.adapter.ChampionshipListAdapter;
import com.perlagloria.model.Customer;
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

public class SelectChampionshipFragment extends Fragment implements ChampionshipListAdapter.OnCheckboxCheckedListener {
    private static final String LOADING_CUSTOMERS_LIST_TAG = "customers_list_loading";

    private RecyclerView championshipListRecView;
    private ChampionshipListAdapter championshipListAdapter;
    private ArrayList<Customer> championshipArrayList;

    private TextView champTextView;
    private TextView champValueTextView;

    private OnChampionshipPassListener championshipPassListener;  //pass selected championship back to the activity

    public SelectChampionshipFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_championship, container, false);

        champTextView = (TextView) rootView.findViewById(R.id.champTextView);
        champValueTextView = (TextView) rootView.findViewById(R.id.champValueTextView);

        ((ChooseTeamActivity) getActivity()).setToolbarTitle(getString(R.string.toolbar_choose_team_title));

        championshipArrayList = new ArrayList<>();
        championshipListRecView = (RecyclerView) rootView.findViewById(R.id.container_championships);
        championshipListAdapter = new ChampionshipListAdapter(getActivity(), championshipArrayList, this);
        championshipListRecView.setAdapter(championshipListAdapter);
        championshipListRecView.setItemAnimator(null);
        championshipListRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        champTextView.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_MEDIUM, getActivity()));
        champValueTextView.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));

        loadChampionshipInfo();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            championshipPassListener = (OnChampionshipPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnChampionshipPassListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        championshipPassListener = null;
    }

    /**
     * Is being executed after any checkbox was checked in recycleview
     */
    @Override
    public void onCheckboxChecked(Customer customer) {
        //Log.d("TAG", "checked!");
        champValueTextView.setText(customer.getName());

        if (championshipPassListener != null) {
            championshipPassListener.onChampionshipPass(customer);
        }
    }

    private void loadChampionshipInfo() {
        String loadCustomersUrl = ServerApi.loadCustomersUrl;
        ServerRequestListener requestResponder = (ServerRequestListener) getActivity();
        requestResponder.onRequestStarted();

        JsonArrayRequest customersJsonRequest = new JsonArrayRequest(loadCustomersUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_CUSTOMERS_LIST_TAG, response.toString());
                        ServerRequestListener requestResponder = (ServerRequestListener) getActivity();
                        requestResponder.onRequestFinished();

                        if (!parseCustomersJson(response)) {                                        //case of response parse error
                            ServerResponseErrorListener responseResponder = (ServerResponseErrorListener) getActivity();
                            responseResponder.onServerResponseError(ErrorAlertDialog.NO_INFO_FROM_SERVER);
                        } else {
                            championshipListAdapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_CUSTOMERS_LIST_TAG, "Error: " + error.getMessage());
                        ServerRequestListener requestResponder = (ServerRequestListener) getActivity();
                        requestResponder.onRequestFinished();

                        ServerResponseErrorListener responseResponder = (ServerResponseErrorListener) getActivity();
                        responseResponder.onServerResponseError(ErrorAlertDialog.getVolleyErrorMessage(error));
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(customersJsonRequest, LOADING_CUSTOMERS_LIST_TAG); // Adding request to request queue
    }

    private boolean parseCustomersJson(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);

                Customer customer = new Customer(obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("createdDate"),
                        obj.getBoolean("isActive"),
                        false);

                championshipArrayList.add(customer);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * Pass data back to the activity (selected championship)
     */
    public interface OnChampionshipPassListener {
        void onChampionshipPass(Customer customer);
    }
}
