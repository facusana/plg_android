package com.perlagloria.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.perlagloria.R;
import com.perlagloria.util.AppController;
import com.perlagloria.util.ErrorAlertDialog;
import com.perlagloria.util.SharedPreferenceKey;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String LOADING_TEST_TAG = "loading_team_test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        SharedPreferences sPref = getSharedPreferences("config", Context.MODE_PRIVATE);
        final int savedTeamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                if (savedTeamId == -1) {    //if team was never selected
                    intent = new Intent(getApplicationContext(), ChooseTeamActivity.class);
                } else {    //check if team is still available on server
                    intent = new Intent(getApplicationContext(), TeamActivity.class);    //if team is available -> move to last screen
                }
                startActivity(intent);
                finish();
            }
        }, 2000);   //show splashscreen during 2 sec


        //checkIsDataFromServerJObject(ServerApi.loadFixtureMatchInfoUrl + savedTeamid);

    }

    private void checkIsDataFromServerJObject(String url) {
        JsonObjectRequest testJsonRequest = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(LOADING_TEST_TAG, response.toString());

                        try {
                            response.getInt("id");   //json is not null

                            Intent intent = new Intent(getApplicationContext(), TeamActivity.class);    //if team is available -> move to last screen
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), R.string.no_info_from_server, Toast.LENGTH_LONG).show();    //no information from the server

                            Intent intent = new Intent(getApplicationContext(), ChooseTeamActivity.class);  //team isn't available -> chose team
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(LOADING_TEST_TAG, "Error: " + error.getMessage());

                        ErrorAlertDialog.show(MainActivity.this, ErrorAlertDialog.getVolleyErrorMessage(error));
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(testJsonRequest, LOADING_TEST_TAG);
    }

}
