package com.perlagloria.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.perlagloria.R;
import com.perlagloria.model.Tactic;
import com.perlagloria.model.Team;
import com.perlagloria.responder.ServerRequestListener;
import com.perlagloria.responder.ServerResponseErrorListener;
import com.perlagloria.util.AppController;
import com.perlagloria.util.ErrorAlertDialog;
import com.perlagloria.util.FontManager;
import com.perlagloria.util.ServerApi;
import com.perlagloria.util.SharedPreferenceKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {
    private static final String LOADING_STATISTICS_TAG = "statistics_loading";

    private TextView positionsTitle;
    private TableLayout table;
    private RelativeLayout tableWrapper;
    private TextView teamTVHeader;
    private TextView pointsTVHeader;
    private TextView gamesPlayedTVHeader;
    private TextView winsTVHeader;
    private TextView tiesTVHeader;
    private TextView lossesTVHeader;
    private TextView goalsForTVHeader;
    private TextView goalsAgainstTVHeader;

    private int teamId;
    private ArrayList<Team> statisticsArrayList;

    private ServerResponseErrorListener serverResponseErrorListener;
    private ServerRequestListener serverRequestListener;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        positionsTitle = (TextView) rootView.findViewById(R.id.positions_title);
        table = (TableLayout) rootView.findViewById(R.id.table);
        tableWrapper = (RelativeLayout) rootView.findViewById(R.id.table_wrapper);

        positionsTitle.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));

        SharedPreferences sPref = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        teamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);

        statisticsArrayList = new ArrayList<>();

        loadStatisticsInfo();

        return rootView;
    }

    private void loadStatisticsInfo() {
        String loadStatisticsUrl = ServerApi.loadStatisticsUrl + teamId;
        serverRequestListener.onRequestStarted();

        JsonArrayRequest statisticsJsonRequest = new JsonArrayRequest(loadStatisticsUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_STATISTICS_TAG, response.toString());
                        serverRequestListener.onRequestFinished();

                        if (!parseStatisticsJson(response)) {                                        //case of response parse error
                            serverResponseErrorListener.onServerResponseError(ErrorAlertDialog.NO_INFO_FROM_SERVER);
                        } else {
                            fillTable();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_STATISTICS_TAG, "Error: " + error.getMessage());
                        serverRequestListener.onRequestFinished();

                        serverResponseErrorListener.onServerResponseError(ErrorAlertDialog.getVolleyErrorMessage(error));
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(statisticsJsonRequest, LOADING_STATISTICS_TAG); // Adding request to request queue
    }

    private boolean parseStatisticsJson(JSONArray response) {
        statisticsArrayList.clear();
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

    @SuppressLint("DefaultLocale")
    private void fillTable() {
        final TableRow headerRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_statistics_header_item, null);
        table.addView(headerRow);

        teamTVHeader = (TextView) headerRow.findViewById(R.id.teamTVHeader);
        pointsTVHeader = (TextView) headerRow.findViewById(R.id.pointsTVHeader);
        gamesPlayedTVHeader = (TextView) headerRow.findViewById(R.id.gamesPlayedTVHeader);
        winsTVHeader = (TextView) headerRow.findViewById(R.id.winsTVHeader);
        tiesTVHeader = (TextView) headerRow.findViewById(R.id.tiesTVHeader);
        lossesTVHeader = (TextView) headerRow.findViewById(R.id.lossesTVHeader);
        goalsForTVHeader = (TextView) headerRow.findViewById(R.id.goalsForTVHeader);
        goalsAgainstTVHeader = (TextView) headerRow.findViewById(R.id.goalsAgainstTVHeader);

        teamTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        pointsTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        gamesPlayedTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        winsTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        tiesTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        lossesTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        goalsForTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        goalsAgainstTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));

        for (int i = 0; i < statisticsArrayList.size(); i++) {
            TableRow tableRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_statistics_row_item, null);
            TextView teamValue = (TextView) tableRow.findViewById(R.id.teamTV);
            teamValue.setText(String.format("%d %s", (i + 1), statisticsArrayList.get(i).getName()));
            TextView pointsValue = (TextView) tableRow.findViewById(R.id.pointsTV);
            pointsValue.setText(String.valueOf(statisticsArrayList.get(i).getPoints()));
            TextView gamesPlayedValue = (TextView) tableRow.findViewById(R.id.gamesPlayedTV);
            gamesPlayedValue.setText(String.valueOf(statisticsArrayList.get(i).getGamesPlayed()));
            TextView winsValue = (TextView) tableRow.findViewById(R.id.winsTV);
            winsValue.setText(String.valueOf(statisticsArrayList.get(i).getWins()));
            TextView tiesValue = (TextView) tableRow.findViewById(R.id.tiesTV);
            tiesValue.setText(String.valueOf(statisticsArrayList.get(i).getTies()));
            TextView lossesValue = (TextView) tableRow.findViewById(R.id.lossesTV);
            lossesValue.setText(String.valueOf(statisticsArrayList.get(i).getLosses()));
            TextView goalForValue = (TextView) tableRow.findViewById(R.id.goalsForTV);
            goalForValue.setText(String.valueOf(statisticsArrayList.get(i).getGoalsFor()));
            TextView goalAgainstValue = (TextView) tableRow.findViewById(R.id.goalsAgainstTV);
            goalAgainstValue.setText(String.valueOf(statisticsArrayList.get(i).getGoalsAgainst()));

            teamValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
            pointsValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
            gamesPlayedValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
            winsValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
            tiesValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
            lossesValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
            goalForValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
            goalAgainstValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));

            if (teamId == statisticsArrayList.get(i).getId()) {
                tableRow.setBackgroundColor(Color.WHITE);
                teamValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSelectedTeam));
                pointsValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSelectedTeam));
                gamesPlayedValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSelectedTeam));
                winsValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSelectedTeam));
                tiesValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSelectedTeam));
                lossesValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSelectedTeam));
                goalForValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSelectedTeam));
                goalAgainstValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSelectedTeam));
            } else {
                tableRow.setBackgroundColor(Color.TRANSPARENT);
                teamValue.setTextColor(Color.WHITE);
                pointsValue.setTextColor(Color.WHITE);
                gamesPlayedValue.setTextColor(Color.WHITE);
                winsValue.setTextColor(Color.WHITE);
                tiesValue.setTextColor(Color.WHITE);
                lossesValue.setTextColor(Color.WHITE);
                goalForValue.setTextColor(Color.WHITE);
                goalAgainstValue.setTextColor(Color.WHITE);
            }

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

    public void setServerResponseListener(ServerResponseErrorListener serverResponseErrorListener) {
        this.serverResponseErrorListener = serverResponseErrorListener;
    }

    public void setServerRequestListener(ServerRequestListener serverRequestListener) {
        this.serverRequestListener = serverRequestListener;
    }
}
