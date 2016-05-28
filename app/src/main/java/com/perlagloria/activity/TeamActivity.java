package com.perlagloria.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.perlagloria.R;
import com.perlagloria.adapter.TeamActivityViewPagerAdapter;
import com.perlagloria.fragment.FixtureMatchInfoFragment;
import com.perlagloria.fragment.FixtureMatchMapFragment;
import com.perlagloria.fragment.StatisticsFragment;
import com.perlagloria.responder.ServerRequestListener;
import com.perlagloria.responder.ServerResponseErrorListener;
import com.perlagloria.util.DimensionUtils;
import com.perlagloria.util.ErrorAlertDialog;
import com.perlagloria.util.FontManager;
import com.perlagloria.util.SharedPreferenceKey;

public class TeamActivity extends AppCompatActivity implements
        ServerResponseErrorListener,
        ServerRequestListener {

    private TextView toolbarTitle;
    private TabLayout tabLayout;
    private ViewPager viewPager;

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

            SharedPreferences sPref = getSharedPreferences("config", Context.MODE_PRIVATE);
            setToolbarTitle(sPref.getString(SharedPreferenceKey.TEAM_NAME, ""));
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
            setupTabIcons();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        TeamActivityViewPagerAdapter adapter = new TeamActivityViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StatisticsFragment(), getString(R.string.positions));
        adapter.addFragment(new FixtureMatchInfoFragment(), getString(R.string.sport_event));
        adapter.addFragment(new FixtureMatchMapFragment(), getString(R.string.fixture_match_map));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getString(R.string.positions));
        tabOne.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, this));
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_tab_shield_icon, 0, 0);
        tabOne.setCompoundDrawablePadding((int) DimensionUtils.convertDpToPixel(3));
        tabOne.setSelected(true);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getString(R.string.sport_event));
        tabTwo.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, this));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_tab_table_icon, 0, 0);
        tabTwo.setCompoundDrawablePadding((int) DimensionUtils.convertDpToPixel(3));
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText(getString(R.string.fixture_match_map));
        tabThree.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, this));
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_tab_map_icon, 0, 0);
        tabThree.setCompoundDrawablePadding((int) DimensionUtils.convertDpToPixel(3));
        tabLayout.getTabAt(2).setCustomView(tabThree);
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
//        if (progressBar != null) {
//            tabFragmentContainer.setVisibility(View.INVISIBLE);
//            progressBar.setVisibility(View.VISIBLE);
//        }
    }

    private void hideProgressBar() {
//        if (progressBar != null) {
//            tabFragmentContainer.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
//        }
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
