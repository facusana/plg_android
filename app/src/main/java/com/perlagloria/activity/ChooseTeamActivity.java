package com.perlagloria.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.perlagloria.R;
import com.perlagloria.activity.fragment.SelectChampionshipFragment;
import com.perlagloria.activity.fragment.SelectDivisionFragment;
import com.perlagloria.activity.fragment.SelectTeamFragment;
import com.perlagloria.activity.fragment.SelectTournamentFragment;
import com.perlagloria.model.Customer;
import com.perlagloria.model.Division;
import com.perlagloria.model.Team;
import com.perlagloria.model.Tournament;
import com.perlagloria.util.AppController;
import com.perlagloria.util.ServerApi;
import com.perlagloria.util.SharedPreferenceKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChooseTeamActivity extends AppCompatActivity implements SelectChampionshipFragment.OnChampionshipPassListener, SelectTournamentFragment.OnTournamentPassListener,
        SelectDivisionFragment.OnDivisionPassListener, SelectTeamFragment.OnTeamPassListener {
    public static final int SELECT_CHAMPIONSHIP = 1;
    public static final int SELECT_TOURNAMENT = 2;
    public static final int SELECT_DIVISION = 3;
    public static final int SELECT_TEAM = 4;
    private static final String LOADING_TEST_TAG = "loading_test";
    private int currentState = SELECT_CHAMPIONSHIP;

    private ImageView triangleImageView;
    private RelativeLayout bottomLayout; //layout with "next" triangle button
    private boolean isCheckingProcess;  //check next loading data
    private TextView toolbarTitle;

    private Customer selectedChampionship;
    private Tournament selectedTournament;
    private Division selectedDivision;
    private Team selectedTeam;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        toolbarTitle = (TextView) mainToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("");

        bottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        bottomLayout.setOnClickListener(new NextClickListener());
        triangleImageView = (ImageView) findViewById(R.id.triangleImageView);
        triangleImageView.setOnClickListener(new NextClickListener());

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        isCheckingProcess = false;

        loadFragment();
    }

    public void setToolbarTitle(String text) {
        toolbarTitle.setText(text);
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
        isCheckingProcess = true;

        JsonArrayRequest testJsonRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        VolleyLog.d(LOADING_TEST_TAG, response.toString());

                        try {
                            response.getJSONObject(0).getInt("id");   //json is not null
                            loadNext();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), R.string.no_info_from_server, Toast.LENGTH_LONG).show();    //no information from the server
                        }
                        isCheckingProcess = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_TEST_TAG, "Error: " + error.getMessage());

                        if (error.getMessage() == null) {                                            //com.android.volley.TimeoutError
                            showConnectionErrorAlertDialog();
                        } else if (error.getMessage().contains("java.net.UnknownHostException") && error.networkResponse == null) { //com.android.volley.NoConnectionError
                            showConnectionErrorAlertDialog();
                        } else {                                                                     //response error, code = error.networkResponse.statusCode
                            Toast.makeText(getApplicationContext(), R.string.server_response_error, Toast.LENGTH_LONG).show();
                        }
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

        JsonObjectRequest testJsonRequest = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(LOADING_TEST_TAG, response.toString());

                        try {
                            response.getInt("id");   //json is not null
                            loadNext();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), R.string.no_info_from_server, Toast.LENGTH_LONG).show();    //no information from the server
                        }
                        isCheckingProcess = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_TEST_TAG, "Error: " + error.getMessage());

                        if (error.getMessage() == null) {                                            //com.android.volley.TimeoutError
                            showConnectionErrorAlertDialog();
                        } else if (error.getMessage().contains("java.net.UnknownHostException") && error.networkResponse == null) { //com.android.volley.NoConnectionError
                            showConnectionErrorAlertDialog();
                        } else {                                                                     //response error, code = error.networkResponse.statusCode
                            Toast.makeText(getApplicationContext(), R.string.server_response_error, Toast.LENGTH_LONG).show();
                        }
                        isCheckingProcess = false;
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(testJsonRequest, LOADING_TEST_TAG);
    }

    public void showConnectionErrorAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(getString(R.string.check_connection_dialog));
        builder.setNegativeButton(getString(R.string.check_connection_dialog_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void showPDialog(String message) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    public void hidePDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
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
            triangleImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_triangle));
        }
    }
}
