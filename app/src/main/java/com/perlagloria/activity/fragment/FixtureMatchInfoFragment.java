package com.perlagloria.activity.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.perlagloria.R;
import com.perlagloria.activity.TeamActivity;
import com.perlagloria.model.Division;
import com.perlagloria.model.FixtureDate;
import com.perlagloria.model.FixtureMatchInfo;
import com.perlagloria.model.Tactic;
import com.perlagloria.model.Team;
import com.perlagloria.util.AppController;
import com.perlagloria.util.ImageDownloader;
import com.perlagloria.util.ServerApi;
import com.perlagloria.util.SharedPreferenceKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FixtureMatchInfoFragment extends Fragment {
    private static final String LOADING_FIXTURE_MATCH_TAG = "fixture_match_loading";
    private int teamId;

    private ImageView team1LogoImgView;
    private ImageView team2LogoImgView;
    private TextView versusTV;
    private TextView team1NameTV;
    private TextView team2NameTV;
    private TextView suspendedTV;
    private TextView dateNumberTV;
    private TextView dateOfMatchTV;
    private TextView timeOfMatchTV;
    private TextView fieldNumberTV;
    private TextView homeGoalsTV;
    private TextView awayGoalsTV;
    private TextView mapCodeTV;

    private FixtureMatchInfo fixtureMatchInfo;

    public FixtureMatchInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fixture_match_info, container, false);

        team1LogoImgView = (ImageView) rootView.findViewById(R.id.team1LogoImgView);
        team2LogoImgView = (ImageView) rootView.findViewById(R.id.team2LogoImgView);
        versusTV = (TextView) rootView.findViewById(R.id.versusTV);
        team1NameTV = (TextView) rootView.findViewById(R.id.team1NameTV);
        team2NameTV = (TextView) rootView.findViewById(R.id.team2NameTV);
        suspendedTV = (TextView) rootView.findViewById(R.id.suspendedTV);
        dateNumberTV = (TextView) rootView.findViewById(R.id.dateNumberTV);
        dateOfMatchTV = (TextView) rootView.findViewById(R.id.dateOfMatchTV);
        timeOfMatchTV = (TextView) rootView.findViewById(R.id.timeOfMatchTV);
        fieldNumberTV = (TextView) rootView.findViewById(R.id.fieldNumberTV);
        homeGoalsTV = (TextView) rootView.findViewById(R.id.homeGoalsTV);
        awayGoalsTV = (TextView) rootView.findViewById(R.id.awayGoalsTV);
        mapCodeTV = (TextView) rootView.findViewById(R.id.mapCodeTV);

        SharedPreferences sPref = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        teamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);

        loadFixtureMatchInfo();

        return rootView;
    }

    private void loadFixtureMatchInfo() {
        String loadFixtureMatchInfoUrl = ServerApi.loadFixtureMatchInfoUrl + teamId;
        ((TeamActivity) getActivity()).showPDialog(getString(R.string.loading_data_progress_dialog));

        JsonObjectRequest fixtureMatchInfoJsonRequest = new JsonObjectRequest(loadFixtureMatchInfoUrl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(LOADING_FIXTURE_MATCH_TAG, response.toString());
                        ((TeamActivity) getActivity()).hidePDialog();

                        if (!parseFixtureMatchInfoJson(response)) { //case of response parse error
                            Toast.makeText(getActivity(), R.string.no_info_for_next_match, Toast.LENGTH_LONG).show();
                            setDummyData();
                        } else {
                            setData();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_FIXTURE_MATCH_TAG, "Error: " + error.getMessage());
                        ((TeamActivity) getActivity()).hidePDialog();

                        if (error.getMessage() == null) {                                            //com.android.volley.TimeoutError
                            ((TeamActivity) getActivity()).showConnectionErrorAlertDialog();
                        } else if (error.getMessage().contains("java.net.UnknownHostException") && error.networkResponse == null) { //com.android.volley.NoConnectionError
                            ((TeamActivity) getActivity()).showConnectionErrorAlertDialog();
                        } else {                                                                     //response error, code = error.networkResponse.statusCode
                            Toast.makeText(getActivity(), R.string.server_response_error, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(fixtureMatchInfoJsonRequest, LOADING_FIXTURE_MATCH_TAG); // Adding request to request queue

    }

    private boolean parseFixtureMatchInfoJson(JSONObject response) {
        try {
            FixtureDate fixtureDate;
            Division fixtureDateDivision;
            Team homeTeam;
            Tactic homeTeamTactic;
            Team awayTeam;
            Tactic awayTeamTactic;

            JSONObject fixtureDateObj = response.getJSONObject("fixtureDate");
            JSONObject fixtureDateDivisionObj = fixtureDateObj.getJSONObject("division");
            fixtureDateDivision = new Division(fixtureDateDivisionObj.getInt("id"),
                    fixtureDateDivisionObj.getString("name"),
                    fixtureDateDivisionObj.getString("createdDate"),
                    fixtureDateDivisionObj.getBoolean("isActive"),
                    false);

            fixtureDate = new FixtureDate(fixtureDateObj.getInt("id"),
                    fixtureDateDivision,
                    fixtureDateObj.getString("date"),
                    fixtureDateObj.getString("dateNumber"),
                    fixtureDateObj.getBoolean("isSuspended"),
                    fixtureDateObj.getString("suspendedReason"));

            JSONObject homeTeamObj = response.getJSONObject("homeTeam");
            JSONObject homeTeamTacticObj = homeTeamObj.getJSONObject("tactic");
            homeTeamTactic = new Tactic(homeTeamTacticObj.getInt("id"),
                    homeTeamTacticObj.getString("code"),
                    homeTeamTacticObj.getString("description"));

            homeTeam = new Team(homeTeamObj.getInt("id"),
                    homeTeamObj.getString("name"),
                    homeTeamObj.getString("createdDate"),
                    homeTeamObj.getBoolean("isActive"),
                    homeTeamObj.getInt("position"),
                    homeTeamObj.getInt("points"),
                    homeTeamObj.getInt("gamesPlayed"),
                    homeTeamObj.getInt("wins"),
                    homeTeamObj.getInt("ties"),
                    homeTeamObj.getInt("losses"),
                    homeTeamObj.getInt("goalsFor"),
                    homeTeamObj.getInt("goalsAgainst"),
                    homeTeamObj.getInt("goalDifference"),
                    homeTeamObj.getInt("avgGoalForPerMatch"),
                    homeTeamObj.getInt("avgGoalAgainstPerMatch"),
                    homeTeamTactic,
                    false);

            JSONObject awayTeamObj = response.getJSONObject("awayTeam");
            JSONObject awayTeamTacticObj = awayTeamObj.getJSONObject("tactic");
            awayTeamTactic = new Tactic(awayTeamTacticObj.getInt("id"),
                    awayTeamTacticObj.getString("code"),
                    awayTeamTacticObj.getString("description"));

            awayTeam = new Team(awayTeamObj.getInt("id"),
                    awayTeamObj.getString("name"),
                    awayTeamObj.getString("createdDate"),
                    awayTeamObj.getBoolean("isActive"),
                    awayTeamObj.getInt("position"),
                    awayTeamObj.getInt("points"),
                    awayTeamObj.getInt("gamesPlayed"),
                    awayTeamObj.getInt("wins"),
                    awayTeamObj.getInt("ties"),
                    awayTeamObj.getInt("losses"),
                    awayTeamObj.getInt("goalsFor"),
                    awayTeamObj.getInt("goalsAgainst"),
                    awayTeamObj.getInt("goalDifference"),
                    awayTeamObj.getInt("avgGoalForPerMatch"),
                    awayTeamObj.getInt("avgGoalAgainstPerMatch"),
                    awayTeamTactic,
                    false);

            String homeGoals = (response.getString("homeGoals").equals("null")) ? "-" : response.getString("homeGoals");
            String awayGoals = (response.getString("awayGoals").equals("null")) ? "-" : response.getString("awayGoals");

            fixtureMatchInfo = new FixtureMatchInfo(response.getInt("id"),
                    fixtureDate,
                    homeTeam,
                    awayTeam,
                    response.getString("lastUpdateDate"),
                    response.getString("fieldNumber"),
                    response.getString("hour"),
                    homeGoals,
                    awayGoals,
                    response.getString("mapCode"));

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void setData() { //throws ParseException {
        try {
            team1NameTV.setText(fixtureMatchInfo.getHomeTeam().getName());
            team2NameTV.setText(fixtureMatchInfo.getAwayTeam().getName());

            Locale spanish = new Locale("es", "ES");
            Configuration c = new Configuration(getResources().getConfiguration());
            c.locale = spanish;

            String strDate = fixtureMatchInfo.getFixtureDate().getDate().substring(0, fixtureMatchInfo.getFixtureDate().getDate().indexOf("T"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
            Date date = sdf.parse(strDate);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().locale);
            String outputDate = sdf2.format(date);

            dateNumberTV.setText(fixtureMatchInfo.getFixtureDate().getDateNumber());
            dateOfMatchTV.setText(outputDate);
            timeOfMatchTV.setText(fixtureMatchInfo.getHour());

            homeGoalsTV.setText(String.valueOf(fixtureMatchInfo.getHomeGoals()));
            awayGoalsTV.setText(String.valueOf(fixtureMatchInfo.getAwayGoals()));

            if (fixtureMatchInfo.getFieldNumber() != null) {
                fieldNumberTV.setText(getString(R.string.field_number, fixtureMatchInfo.getFieldNumber()));
                fieldNumberTV.setVisibility(View.VISIBLE);
            }

            if (fixtureMatchInfo.getMapCode() != null) {
                mapCodeTV.setText(getString(R.string.map_code, fixtureMatchInfo.getMapCode()));
                mapCodeTV.setVisibility(View.VISIBLE);
            }

            if (fixtureMatchInfo.getFixtureDate().getIsSuspended()) {
                suspendedTV.setText(fixtureMatchInfo.getFixtureDate().getSuspendedReason());
                suspendedTV.setVisibility(View.VISIBLE);
            }

            new ImageDownloader(team1LogoImgView).execute(ServerApi.loadTeamImageUrl + fixtureMatchInfo.getHomeTeam().getId());
            new ImageDownloader(team2LogoImgView).execute(ServerApi.loadTeamImageUrl + fixtureMatchInfo.getAwayTeam().getId());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setDummyData() {
        team1NameTV.setText("");
        team2NameTV.setText("");
        versusTV.setText(R.string.not_available);
        dateNumberTV.setText("");
        dateOfMatchTV.setText("");
        timeOfMatchTV.setText("");
        fieldNumberTV.setText("");
        mapCodeTV.setText("");
    }
}
