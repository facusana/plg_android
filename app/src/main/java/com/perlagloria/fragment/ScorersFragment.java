package com.perlagloria.fragment;


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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.perlagloria.R;
import com.perlagloria.model.Scorer;
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

public class ScorersFragment extends Fragment {
    private static final String LOADING_SCORERS_TAG = "scorers_loading";

    private TextView scorersTitle;
    private TableLayout table;
    private RelativeLayout tableWrapper;


    private TextView playerTVHeader;
    private TextView teamTVHeader;
    private TextView goalsTVHeader;

    private int teamId;
    private ArrayList<Scorer> scorersArrayList;

    private ServerResponseErrorListener serverResponseErrorListener;
    private ServerRequestListener serverRequestListener;

    public ScorersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scorers, container, false);

        scorersTitle = (TextView) rootView.findViewById(R.id.scorers_title);
        table = (TableLayout) rootView.findViewById(R.id.table);
        tableWrapper = (RelativeLayout) rootView.findViewById(R.id.table_wrapper);

        scorersTitle.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));

        SharedPreferences sPref = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        teamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);

        scorersArrayList = new ArrayList<>();

        loadScorersInfo();

        return rootView;
    }

    private void loadScorersInfo() {
        String loadScorersUrl = ServerApi.loadScorersUrl + teamId;
        serverRequestListener.onRequestStarted();

        JsonArrayRequest scorersJsonRequest = new JsonArrayRequest(loadScorersUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_SCORERS_TAG, response.toString());
                        serverRequestListener.onRequestFinished();

                        if (!parseScorersJson(response)) {                                        //case of response parse error
                            serverResponseErrorListener.onServerResponseError(ErrorAlertDialog.NO_INFO_FROM_SERVER);
                        } else {
                            fillTable();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_SCORERS_TAG, "Error: " + error.getMessage());
                        serverRequestListener.onRequestFinished();

                        serverResponseErrorListener.onServerResponseError(ErrorAlertDialog.getVolleyErrorMessage(error));
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(scorersJsonRequest, LOADING_SCORERS_TAG); // Adding request to request queue
    }

    private boolean parseScorersJson(JSONArray response) {
        scorersArrayList.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);

                Scorer scorer = new Scorer(obj.getString("teamName"),
                        obj.getString("playerName"),
                        obj.getInt("goals"));

                scorersArrayList.add(scorer);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    @SuppressLint("DefaultLocale")
    private void fillTable() {
        final TableRow headerRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_scorers_header_item, null);
        table.addView(headerRow);

        playerTVHeader = (TextView) headerRow.findViewById(R.id.playerTVHeader);
        teamTVHeader = (TextView) headerRow.findViewById(R.id.teamTVHeader);
        goalsTVHeader = (TextView) headerRow.findViewById(R.id.goalsTVHeader);

        playerTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        teamTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        goalsTVHeader.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));

        for (int i = 0; i < scorersArrayList.size(); i++) {
            TableRow tableRow = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_scorers_row_item, null);
            TextView playerValue = (TextView) tableRow.findViewById(R.id.playerTV);
            playerValue.setText(String.format("%d %s", (i + 1), scorersArrayList.get(i).getPlayerName()));
            TextView teamValue = (TextView) tableRow.findViewById(R.id.teamTV);
            teamValue.setText(String.valueOf(scorersArrayList.get(i).getTeamName()));
            TextView goalsValue = (TextView) tableRow.findViewById(R.id.goalsTV);
            goalsValue.setText(String.valueOf(scorersArrayList.get(i).getGoals()));

            playerValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
            teamValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
            goalsValue.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));

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
