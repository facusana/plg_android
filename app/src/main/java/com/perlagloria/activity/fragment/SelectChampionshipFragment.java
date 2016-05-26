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
import com.perlagloria.adapter.ChampionshipListAdapter;
import com.perlagloria.model.Customer;
import com.perlagloria.util.AppController;
import com.perlagloria.util.ServerApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectChampionshipFragment extends Fragment implements ChampionshipListAdapter.OnCheckboxCheckedListener {
    private static final String LOADING_CUSTOMERS_LIST_TAG = "customers_list_loading";

    private LinearLayoutManager mLayoutManager;
    private RecyclerView championshipListRecView;
    private ChampionshipListAdapter championshipListAdapter;
    private ArrayList<Customer> championshipArrayList;

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
        champValueTextView = (TextView) rootView.findViewById(R.id.champValueTextView);

        ((ChooseTeamActivity) getActivity()).setToolbarTitle(getString(R.string.toolbar_choose_team_title));

        championshipArrayList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());

        championshipListRecView = (RecyclerView) rootView.findViewById(R.id.container_championships);
        championshipListAdapter = new ChampionshipListAdapter(championshipArrayList, this);
        championshipListRecView.setAdapter(championshipListAdapter);
        championshipListRecView.setItemAnimator(null);
        championshipListRecView.setLayoutManager(mLayoutManager);

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
        ((ChooseTeamActivity) getActivity()).showPDialog(getString(R.string.loading_data_progress_dialog));

        JsonArrayRequest customersJsonRequest = new JsonArrayRequest(loadCustomersUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_CUSTOMERS_LIST_TAG, response.toString());
                        ((ChooseTeamActivity) getActivity()).hidePDialog();

                        if (!parseCustomersJson(response)) {                                        //case of response parse error
                            Toast.makeText(getActivity(), R.string.no_info_from_server, Toast.LENGTH_LONG).show();
                        } else {
                            championshipListAdapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_CUSTOMERS_LIST_TAG, "Error: " + error.getMessage());
                        ((ChooseTeamActivity) getActivity()).hidePDialog();

                        if (error.getMessage() == null) {                                            //com.android.volley.TimeoutError
                            ((ChooseTeamActivity) getActivity()).showConnectionErrorAlertDialog();
                        } else if (error.getMessage().contains("java.net.UnknownHostException") && error.networkResponse == null) { //com.android.volley.NoConnectionError
                            ((ChooseTeamActivity) getActivity()).showConnectionErrorAlertDialog();
                        } else {                                                                     //response error, code = error.networkResponse.statusCode
                            Toast.makeText(getActivity(), R.string.server_response_error, Toast.LENGTH_LONG).show();
                        }
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
