package com.perlagloria.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.perlagloria.R;
import com.perlagloria.util.AppController;
import com.perlagloria.util.ErrorAlertDialog;
import com.perlagloria.util.FontManager;
import com.perlagloria.util.ServerApi;
import com.perlagloria.util.SharedPreferenceKey;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String LOADING_TEST_TAG = "loading_team_test";
    boolean isIntentLaunched = false;
    private LinearLayout poweredByContainer;
    private TextView poweredByTitle;
    private ImageView champLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        poweredByContainer = (LinearLayout) findViewById(R.id.powered_by_container);
        poweredByTitle = (TextView) findViewById(R.id.powered_by_title);
        champLogo = (ImageView) findViewById(R.id.champ_logo);

        SharedPreferences sPref = getSharedPreferences("config", Context.MODE_PRIVATE);
        final int savedTeamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);

        if (savedTeamId != -1) {
            poweredByTitle.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, MainActivity.this));
            Glide.with(champLogo.getContext())
                    .load(ServerApi.loadCustomerImageByTeamIdUrl + savedTeamId)
                    .thumbnail(0.5f)
                    .crossFade()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            poweredByContainer.setVisibility(View.VISIBLE);

                            if (!isIntentLaunched) {        //prevent double call
                                isIntentLaunched = true;

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(), TeamActivity.class);    //if team is available -> move to last screen
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 2000);   //show splashscreen during 2 sec
                            }
                            return false;
                        }
                    })
                    .into(champLogo);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), ChooseTeamActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);   //show splashscreen during 2 sec
        }


        //checkIsDataFromServerJObject(ServerApi.loadFixtureMatchInfoUrl + savedTeamid);

    }

    private void checkIsDataFromServerJObject(String url) {
        JsonObjectRequest testJsonRequest = new JsonObjectRequest(url,
                null,
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

                            ErrorAlertDialog.show(MainActivity.this, ErrorAlertDialog.NO_INFO_FROM_SERVER, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), ChooseTeamActivity.class);  //team isn't available -> chose team
                                    startActivity(intent);
                                    finish();
                                }
                            });
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
