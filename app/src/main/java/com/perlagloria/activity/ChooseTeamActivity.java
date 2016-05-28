package com.perlagloria.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.perlagloria.R;
import com.perlagloria.fragment.SelectChampionshipFragment;
import com.perlagloria.fragment.SelectDivisionFragment;
import com.perlagloria.fragment.SelectTeamFragment;
import com.perlagloria.fragment.SelectTournamentFragment;
import com.perlagloria.model.Customer;
import com.perlagloria.model.Division;
import com.perlagloria.model.Team;
import com.perlagloria.model.Tournament;
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

public class ChooseTeamActivity extends AppCompatActivity implements
        SelectChampionshipFragment.OnChampionshipPassListener,
        SelectTournamentFragment.OnTournamentPassListener,
        SelectDivisionFragment.OnDivisionPassListener,
        SelectTeamFragment.OnTeamPassListener,
        ServerResponseErrorListener,
        ServerRequestListener {
    public static final int SELECT_CHAMPIONSHIP = 1;
    public static final int SELECT_TOURNAMENT = 2;
    public static final int SELECT_DIVISION = 3;
    public static final int SELECT_TEAM = 4;
    private static final String LOADING_TEST_TAG = "loading_test";
    private int currentState = SELECT_CHAMPIONSHIP;

    private boolean isCheckingProcess;  //check next loading data
    private TextView toolbarTitle;
    private TextView next;

    private Customer selectedChampionship;
    private Tournament selectedTournament;
    private Division selectedDivision;
    private Team selectedTeam;

    private FrameLayout fragmentContainer;
    private RelativeLayout nextButtonContainer;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        if (mainToolbar != null) {
            toolbarTitle = (TextView) mainToolbar.findViewById(R.id.toolbar_title);
            toolbarTitle.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, this));

            setSupportActionBar(mainToolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("");
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        next = (TextView) findViewById(R.id.next);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        nextButtonContainer = (RelativeLayout) findViewById(R.id.next_button_container);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        next.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, this));
        next.setOnClickListener(new NextClickListener());

        isCheckingProcess = false;

        loadFragment();
    }

    public void setToolbarTitle(String text) {
        toolbarTitle.setText(text.toUpperCase());
    }

    private void loadFragment() {
        Fragment targetFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (currentState) {
            case SELECT_CHAMPIONSHIP:
                targetFragment = new SelectChampionshipFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, targetFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        //.addToBackStack(null)
                        .commit();
                break;
            case SELECT_TOURNAMENT:
                targetFragment = SelectTournamentFragment.newInstance(selectedChampionship.getId(), selectedChampionship.getName());
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, targetFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;
            case SELECT_DIVISION:
                targetFragment = SelectDivisionFragment.newInstance(selectedChampionship.getName(), selectedTournament.getId(), selectedTournament.getName());
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, targetFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;
            case SELECT_TEAM:
                targetFragment = SelectTeamFragment.newInstance(selectedChampionship.getName(), selectedTournament.getName(), selectedDivision.getName(), selectedDivision.getId());
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, targetFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    /**
     * Check if item was selected from checkbox list
     *
     * @return true if was selected, false otherwise
     */
    private boolean checkSelection() {
        if (currentState == SELECT_CHAMPIONSHIP && selectedChampionship == null)
            return false;
        if (currentState == SELECT_TOURNAMENT && selectedTournament == null)
            return false;
        if (currentState == SELECT_DIVISION && selectedDivision == null)
            return false;
        if (currentState == SELECT_TEAM && selectedTeam == null)
            return false;

        return true;
    }

    /**
     * Check if next loading data is available and the response isn't empty.
     */
    private void checkIsDataFromServerJArray(String url) {
        showProgressBar();
        isCheckingProcess = true;

        JsonArrayRequest testJsonRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_TEST_TAG, response.toString());
                        hideProgressBar();

                        try {
                            response.getJSONObject(0).getInt("id");   //json is not null
                            loadNext();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onServerResponseError(ErrorAlertDialog.NO_INFO_FROM_SERVER);
                        }
                        isCheckingProcess = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_TEST_TAG, "Error: " + error.getMessage());
                        hideProgressBar();

                        onServerResponseError(ErrorAlertDialog.getVolleyErrorMessage(error));

                        isCheckingProcess = false;
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(testJsonRequest, LOADING_TEST_TAG);
    }

    /**
     * Check if next loading data is available and the response isn't empty.
     */
    private void checkIsDataFromServerJObject(String url) {
        isCheckingProcess = true;
        showProgressBar();

        JsonObjectRequest testJsonRequest = new JsonObjectRequest(url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(LOADING_TEST_TAG, response.toString());
                        hideProgressBar();

                        try {
                            response.getInt("id");   //json is not null
                            loadNext();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onServerResponseError(ErrorAlertDialog.NO_INFO_FROM_SERVER);
                        }
                        isCheckingProcess = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_TEST_TAG, "Error: " + error.getMessage());
                        hideProgressBar();

                        onServerResponseError(ErrorAlertDialog.getVolleyErrorMessage(error));

                        isCheckingProcess = false;
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(testJsonRequest, LOADING_TEST_TAG);
    }

    private void showProgressBar() {
        if (progressBar != null) {
            //fragmentContainer.setVisibility(View.INVISIBLE);
            //nextButtonContainer.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            //fragmentContainer.setVisibility(View.VISIBLE);
            //nextButtonContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentState > SELECT_CHAMPIONSHIP) {
            currentState--;
        }
    }

    @Override
    public void onChampionshipPass(Customer customer) {
        selectedChampionship = customer;
    }

    @Override
    public void onTournamentPass(Tournament tournament) {
        selectedTournament = tournament;
    }

    @Override
    public void onDivisionPass(Division division) {
        selectedDivision = division;
    }

    @Override
    public void onTeamPass(Team team) {
        selectedTeam = team;
    }

    @Override
    public void onServerResponseError(String message) {
        ErrorAlertDialog.show(ChooseTeamActivity.this, message);
    }

    @Override
    public void onRequestStarted() {
        showProgressBar();
    }

    @Override
    public void onRequestFinished() {
        hideProgressBar();
    }

    private void loadNext() {
        if (currentState < SELECT_TEAM && checkSelection()) {                                   //load next fragment
            currentState++;
            loadFragment();
        } else if (currentState == SELECT_TEAM && checkSelection()) {                           //move to the next activity
            SharedPreferences sPref = getSharedPreferences("config", MODE_PRIVATE);             //save selected team
            SharedPreferences.Editor ed = sPref.edit();
            ed.putInt(SharedPreferenceKey.TEAM_ID, selectedTeam.getId());
            ed.putString(SharedPreferenceKey.TEAM_NAME, selectedTeam.getName());
            ed.commit();

            Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
            //intent.putExtra("EXTRA_SESSION_ID", sessionId);
            startActivity(intent);
            finish();
        }
    }

    private class NextClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {   //check if data is available from the server
            if (currentState < SELECT_TEAM && checkSelection() && !isCheckingProcess) { //check data for next fragment
                if (currentState == SELECT_CHAMPIONSHIP)
                    checkIsDataFromServerJArray(ServerApi.loadTournamentUrl + selectedChampionship.getId());
                if (currentState == SELECT_TOURNAMENT)
                    checkIsDataFromServerJArray(ServerApi.loadDivisionUrl + selectedTournament.getId());
                if (currentState == SELECT_DIVISION)
                    checkIsDataFromServerJArray(ServerApi.loadTeamUrl + selectedDivision.getId());
            } else if (currentState == SELECT_TEAM && checkSelection() && !isCheckingProcess) { //check data for next activity (TeamActivity)
                //checkIsDataFromServerJObject(ServerApi.loadFixtureMatchInfoUrl + selectedTeam.getId());
                loadNext();
            }
        }
    }
}
