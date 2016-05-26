package com.perlagloria.activity.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.perlagloria.R;
import com.perlagloria.activity.TeamActivity;
import com.perlagloria.model.Tactic;
import com.perlagloria.model.Team;
import com.perlagloria.util.AppController;
import com.perlagloria.util.ServerApi;
import com.perlagloria.util.SharedPreferenceKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {
    private static final String LOADING_STATISTICS_TAG = "statistics_loading";

    private TableLayout table;
    private RelativeLayout tableWrapper;
    private TextView teamTVHeader;
    private TextView pointsTVHeader;
    private TextView winsTVHeader;
    private TextView tiesTVHeader;
    private TextView lossesTVHeader;
    private TextView goalsForTVHeader;
    private TextView goalsAgainstTVHeader;

    private int teamId;
    private ArrayList<Team> statisticsArrayList;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        table = (TableLayout) rootView.findViewById(R.id.table);
        teamTVHeader = (TextView) rootView.findViewById(R.id.teamTVHeader);
        tableWrapper = (RelativeLayout) rootView.findViewById(R.id.table_wrapper);

        pointsTVHeader = (TextView) rootView.findViewById(R.id.pointsTVHeader);
        winsTVHeader = (TextView) rootView.findViewById(R.id.winsTVHeader);
        tiesTVHeader = (TextView) rootView.findViewById(R.id.tiesTVHeader);
        lossesTVHeader = (TextView) rootView.findViewById(R.id.lossesTVHeader);
        goalsForTVHeader = (TextView) rootView.findViewById(R.id.goalsForTVHeader);
        goalsAgainstTVHeader = (TextView) rootView.findViewById(R.id.goalsAgainstTVHeader);

        SharedPreferences sPref = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        teamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);

        statisticsArrayList = new ArrayList<>();

        loadStatisticsInfo();

        return rootView;
    }

    private void loadStatisticsInfo() {
        String loadStatisticsUrl = ServerApi.loadStatisticsUrl + teamId;
        ((TeamActivity) getActivity()).showPDialog(getString(R.string.loading_data_progress_dialog));

        JsonArrayRequest statisticsJsonRequest = new JsonArrayRequest(loadStatisticsUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_STATISTICS_TAG, response.toString());
                        ((TeamActivity) getActivity()).hidePDialog();

                        if (!parseStatisticsJson(response)) {                                        //case of response parse error
                            Toast.makeText(getActivity(), R.string.no_info_from_server, Toast.LENGTH_LONG).show();
                        } else {
                            fillTable();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_STATISTICS_TAG, "Error: " + error.getMessage());
                        ((TeamActivity) getActivity()).hidePDialog();

                        if (error.getMessage() == null) {                                            //com.android.volley.TimeoutError
                            ((TeamActivity) getActivity()).showConnectionErrorAlertDialog();
                        } else if (error.getMessage().contains("java.net.UnknownHostException") && error.networkResponse == null || error.getMessage().contains("java.net.ConnectException")) { //com.android.volley.NoConnectionError
                            ((TeamActivity) getActivity()).showConnectionErrorAlertDialog();
                        } else {                                                                     //response error, code = error.networkResponse.statusCode
                            Toast.makeText(getActivity(), R.string.server_response_error, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(statisticsJsonRequest, LOADING_STATISTICS_TAG); // Adding request to request queue
    }

    private boolean parseStatisticsJson(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);

                JSONObject tacticObj = obj.getJSONObject("tactic");
                Tactic tactic = new Tactic(tacticObj.getInt("id"),
                        tacticObj.getString("code"),
                        tacticObj.getString("description"));


                Team team = new Team(obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("createdDate"),
                        obj.getBoolean("isActive"),
                        obj.getInt("position"),
                        obj.getInt("points"),
                        obj.getInt("gamesPlayed"),
                        obj.getInt("wins"),
                        obj.getInt("ties"),
                        obj.getInt("losses"),
                        obj.getInt("goalsFor"),
                        obj.getInt("goalsAgainst"),
                        obj.getInt("goalDifference"),
                        obj.getInt("avgGoalForPerMatch"),
                        obj.getInt("avgGoalAgainstPerMatch"),
                        tactic,
                        false);

                statisticsArrayList.add(team);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private void fillTable() {
        final TableRow headerRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_header_item, null);
        table.addView(headerRow);

        for (int i = 0; i < statisticsArrayList.size(); i++) {
            TableRow tableRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_row_item, null);
            ((TextView) tableRow.findViewById(R.id.teamTV)).setText("" + (i + 1) + " " + statisticsArrayList.get(i).getName());
            ((TextView) tableRow.findViewById(R.id.pointsTV)).setText("" + statisticsArrayList.get(i).getPoints());
            ((TextView) tableRow.findViewById(R.id.winsTV)).setText("" + statisticsArrayList.get(i).getWins());
            ((TextView) tableRow.findViewById(R.id.tiesTV)).setText("" + statisticsArrayList.get(i).getTies());
            ((TextView) tableRow.findViewById(R.id.lossesTV)).setText("" + statisticsArrayList.get(i).getLosses());
            ((TextView) tableRow.findViewById(R.id.goalsForTV)).setText("" + statisticsArrayList.get(i).getGoalsFor());
            ((TextView) tableRow.findViewById(R.id.goalsAgainstTV)).setText("" + statisticsArrayList.get(i).getGoalsAgainst());

            table.addView(tableRow);
        }

        View fakeHeaderView = new View(getContext()) {  //make floating header row
            @SuppressLint("MissingSuperCall")
            @Override
            public void draw(Canvas canvas) {
                headerRow.draw(canvas);
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

                int width = headerRow.getMeasuredWidth();
                int height = headerRow.getMeasuredHeight();

                widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };

        tableWrapper.addView(fakeHeaderView);
    }
}
