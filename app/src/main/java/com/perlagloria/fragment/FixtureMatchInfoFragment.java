package com.perlagloria.fragment;


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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.perlagloria.R;
import com.perlagloria.model.Division;
import com.perlagloria.model.FixtureDate;
import com.perlagloria.model.FixtureMatchInfo;
import com.perlagloria.model.Tactic;
import com.perlagloria.model.Team;
import com.perlagloria.responder.ServerRequestListener;
import com.perlagloria.responder.ServerResponseErrorListener;
import com.perlagloria.util.AppController;
import com.perlagloria.util.ErrorAlertDialog;
import com.perlagloria.util.FontManager;
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

    private TextView nextgameTitle;
    private ImageView team1LogoImgView;
    private ImageView team2LogoImgView;
    private TextView versusTV;
    private TextView team1NameTV;
    private TextView team2NameTV;
    private TextView dateNumberTV;
    private TextView dateOfMatchTV;
    private TextView timeOfMatchTV;
    private TextView fieldNumberTV;

    private FixtureMatchInfo fixtureMatchInfo;

    private ServerResponseErrorListener serverResponseErrorListener;
    private ServerRequestListener serverRequestListener;

    public FixtureMatchInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fixture_match_info, container, false);

        nextgameTitle = (TextView) rootView.findViewById(R.id.nextgame_title);
        dateNumberTV = (TextView) rootView.findViewById(R.id.dateNumberTV);
        team1LogoImgView = (ImageView) rootView.findViewById(R.id.team1LogoImgView);
        team2LogoImgView = (ImageView) rootView.findViewById(R.id.team2LogoImgView);
        versusTV = (TextView) rootView.findViewById(R.id.versusTV);
        team1NameTV = (TextView) rootView.findViewById(R.id.team1NameTV);
        team2NameTV = (TextView) rootView.findViewById(R.id.team2NameTV);
        dateOfMatchTV = (TextView) rootView.findViewById(R.id.dateOfMatchTV);
        timeOfMatchTV = (TextView) rootView.findViewById(R.id.timeOfMatchTV);
        fieldNumberTV = (TextView) rootView.findViewById(R.id.fieldNumberTV);

        nextgameTitle.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
        dateNumberTV.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        versusTV.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
        team1NameTV.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
        team2NameTV.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
        dateOfMatchTV.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        timeOfMatchTV.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));
        fieldNumberTV.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));

        SharedPreferences sPref = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        teamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);

        loadFixtureMatchInfo();

        return rootView;
    }

    private void loadFixtureMatchInfo() {
        String loadFixtureMatchInfoUrl = ServerApi.loadFixtureMatchInfoUrl + teamId;
        serverRequestListener.onRequestStarted();

        JsonObjectRequest fixtureMatchInfoJsonRequest = new JsonObjectRequest(loadFixtureMatchInfoUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(LOADING_FIXTURE_MATCH_TAG, response.toString());
                        serverRequestListener.onRequestFinished();

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
                        serverRequestListener.onRequestFinished();

                        serverResponseErrorListener.onServerResponseError(ErrorAlertDialog.getVolleyErrorMessage(error));
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
            Glide.with(team1LogoImgView.getContext())
                    .load(ServerApi.loadTeamImageUrl + fixtureMatchInfo.getHomeTeam().getId())
                    .thumbnail(0.5f)
                    //.error(R.drawable.shirt)
                    .crossFade()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(team1LogoImgView);

            Glide.with(team2LogoImgView.getContext())
                    .load(ServerApi.loadTeamImageUrl + fixtureMatchInfo.getAwayTeam().getId())
                    .thumbnail(0.5f)
                    //.error(R.drawable.shirt)
                    .crossFade()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(team2LogoImgView);

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

            if (fixtureMatchInfo.getFieldNumber() != null) {
                fieldNumberTV.setText(getString(R.string.field_number, fixtureMatchInfo.getFieldNumber()));
                fieldNumberTV.setVisibility(View.VISIBLE);
            } else {
                fieldNumberTV.setVisibility(View.INVISIBLE);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setDummyData() {
        team1LogoImgView.setVisibility(View.GONE);
        team2LogoImgView.setVisibility(View.GONE);
        team1NameTV.setText("");
        team2NameTV.setText("");
        versusTV.setText(R.string.not_available);
        dateNumberTV.setText("");
        dateOfMatchTV.setText("");
        timeOfMatchTV.setText("");
        fieldNumberTV.setText("");
    }

    public void setServerResponseListener(ServerResponseErrorListener serverResponseErrorListener) {
        this.serverResponseErrorListener = serverResponseErrorListener;
    }

    public void setServerRequestListener(ServerRequestListener serverRequestListener) {
        this.serverRequestListener = serverRequestListener;
    }
}
