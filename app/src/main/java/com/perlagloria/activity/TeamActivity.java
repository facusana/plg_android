package com.perlagloria.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.perlagloria.R;
import com.perlagloria.activity.fragment.MyTeamFragment;
import com.perlagloria.activity.fragment.StatisticsFragment;
import com.perlagloria.responder.ServerRequestListener;
import com.perlagloria.responder.ServerResponseErrorListener;
import com.perlagloria.util.DpiUtils;
import com.perlagloria.util.ErrorAlertDialog;
import com.perlagloria.util.FontManager;
import com.perlagloria.util.SharedPreferenceKey;

public class TeamActivity extends AppCompatActivity implements
        ServerResponseErrorListener,
        ServerRequestListener {
    private TextView firstTab;
    private TextView secondTab;
    private FrameLayout tabFragmentContainer;
    private TextView toolbarTitle;

    private ProgressBar progressBar;

    private boolean isFirstTabSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

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

        tabFragmentContainer = (FrameLayout) findViewById(R.id.tab_fragment_container);

        firstTab = (TextView) findViewById(R.id.firstTab);
        firstTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstTabSelected) return;
                isFirstTabSelected = true;

                secondTab.setBackgroundResource(R.drawable.custom_tab_back_unselected);
                firstTab.setBackgroundResource(R.drawable.custom_tab_back_selected);

                FragmentManager fragmentManager = getSupportFragmentManager();
                MyTeamFragment targetFragment = new MyTeamFragment();

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tabFragmentContainer.getLayoutParams();
                params.setMargins(DpiUtils.convertDipToPixels(getApplicationContext(), 20), DpiUtils.convertDipToPixels(getApplicationContext(), 10),
                        DpiUtils.convertDipToPixels(getApplicationContext(), 20), DpiUtils.convertDipToPixels(getApplicationContext(), 0));
                tabFragmentContainer.setLayoutParams(params);

                fragmentManager.beginTransaction()
                        .replace(tabFragmentContainer.getId(), targetFragment)
                        .setTransition(FragmentTransaction.TRANSIT_NONE)
                        .commit();
            }
        });

        secondTab = (TextView) findViewById(R.id.secondTab);
        secondTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFirstTabSelected) return;
                isFirstTabSelected = false;

                firstTab.setBackgroundResource(R.drawable.custom_tab_back_unselected);
                secondTab.setBackgroundResource(R.drawable.custom_tab_back_selected);

                FragmentManager fragmentManager = getSupportFragmentManager();
                StatisticsFragment targetFragment = new StatisticsFragment();

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tabFragmentContainer.getLayoutParams();
                params.setMargins(DpiUtils.convertDipToPixels(getApplicationContext(), 0), DpiUtils.convertDipToPixels(getApplicationContext(), 10),
                        DpiUtils.convertDipToPixels(getApplicationContext(), 0), DpiUtils.convertDipToPixels(getApplicationContext(), 0));
                tabFragmentContainer.setLayoutParams(params);

                fragmentManager.beginTransaction()
                        .replace(tabFragmentContainer.getId(), targetFragment)
                        .setTransition(FragmentTransaction.TRANSIT_NONE)
                        .commit();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        SharedPreferences sPref = getSharedPreferences("config", Context.MODE_PRIVATE);
        setToolbarTitle(sPref.getString(SharedPreferenceKey.TEAM_NAME, "Null"));

        firstTab.performClick();    //select 1st tab
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_team_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionChangeTeam:
                Intent intent = new Intent(getApplicationContext(), ChooseTeamActivity.class);
                startActivity(intent);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setToolbarTitle(String text) {
        toolbarTitle.setText(text.toUpperCase());
    }

    private void showProgressBar() {
        if (progressBar != null) {
            tabFragmentContainer.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            tabFragmentContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onServerResponseError(String message) {
        ErrorAlertDialog.show(TeamActivity.this, message);
    }

    @Override
    public void onRequestStarted() {
        showProgressBar();
    }

    @Override
    public void onRequestFinished() {
        hideProgressBar();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        ErrorAlertDialog.show(TeamActivity.this,
                getString(R.string.sure_exit_dialog),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //stub
                    }
                },
                getString(R.string.sure_exit_dialog_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                },
                getString(R.string.sure_exit_dialog_yes));
    }
}
