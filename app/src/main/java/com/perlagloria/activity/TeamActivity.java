package com.perlagloria.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.perlagloria.R;
import com.perlagloria.fragment.TeamFragment;
import com.perlagloria.util.ErrorAlertDialog;
import com.perlagloria.util.FontManager;
import com.perlagloria.util.SharedPreferenceKey;

public class TeamActivity extends AppCompatActivity {

    public static final int NAV_DRAWER_STATISTICS_ID = 1;
    public static final int NAV_DRAWER_DIRECTION_ID = 2;
    public static final int NAV_DRAWER_ABOUT_ID = 3;

    private TextView toolbarTitle;

    private Drawer navigationDrawer = null;

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

        initNavigationDrawer(mainToolbar, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = navigationDrawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void initNavigationDrawer(Toolbar toolbar, Bundle savedInstanceState) {
        navigationDrawer = new DrawerBuilder()
                .withActivity(this)
                //.withRootView(R.id.drawer_container)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withActionBarDrawerToggleAnimated(false)
                .withSelectedItem(NAV_DRAWER_STATISTICS_ID)
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .withHasStableIds(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("aa").withIdentifier(NAV_DRAWER_STATISTICS_ID),
                        new PrimaryDrawerItem().withName("bb").withIdentifier(NAV_DRAWER_DIRECTION_ID),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("cc").withIdentifier(NAV_DRAWER_ABOUT_ID)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case NAV_DRAWER_STATISTICS_ID:
                                setNextActiveFragment(new TeamFragment());
                                break;
                            case NAV_DRAWER_DIRECTION_ID:
                                break;
                            case NAV_DRAWER_ABOUT_ID:
                                break;
                        }

                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        //close soft keyboard while opening the navbar
                        if (getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {
                    }
                })
                .build();
    }

    public void setNextActiveFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
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

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        //handle the back press, close the drawer first and if the drawer is closed close the activity
        if (navigationDrawer != null && navigationDrawer.isDrawerOpen()) {
            navigationDrawer.closeDrawer();
        } else if (count == 0) {
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
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
