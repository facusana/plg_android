package com.perlagloria.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.perlagloria.R;
import com.perlagloria.adapter.TeamActivityViewPagerAdapter;
import com.perlagloria.responder.ServerRequestListener;
import com.perlagloria.responder.ServerResponseErrorListener;
import com.perlagloria.util.DimensionUtils;
import com.perlagloria.util.ErrorAlertDialog;
import com.perlagloria.util.FontManager;

public class TeamFragment extends Fragment implements
        ServerResponseErrorListener,
        ServerRequestListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
            setupTabIcons();
        }

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        TeamActivityViewPagerAdapter adapter = new TeamActivityViewPagerAdapter(getChildFragmentManager());
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        statisticsFragment.setServerResponseListener(this);
        statisticsFragment.setServerRequestListener(this);

        FixtureMatchInfoFragment fixtureMatchInfoFragment = new FixtureMatchInfoFragment();
        fixtureMatchInfoFragment.setServerResponseListener(this);
        fixtureMatchInfoFragment.setServerRequestListener(this);

        ScorersFragment scorersFragment = new ScorersFragment();
        scorersFragment.setServerResponseListener(this);
        scorersFragment.setServerRequestListener(this);

        adapter.addFragment(statisticsFragment, getString(R.string.positions));
        adapter.addFragment(fixtureMatchInfoFragment, getString(R.string.sport_event));
        adapter.addFragment(scorersFragment, getString(R.string.scorers));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabOne.setText(getString(R.string.positions));
        tabOne.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_tab_positions_icon, 0, 0);
        tabOne.setCompoundDrawablePadding((int) DimensionUtils.convertDpToPixel(3));
        tabOne.setSelected(true);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getString(R.string.sport_event));
        tabTwo.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_tab_fixture_icon, 0, 0);
        tabTwo.setCompoundDrawablePadding((int) DimensionUtils.convertDpToPixel(3));
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabThree.setText(getString(R.string.scorers));
        tabThree.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_tab_scorers_icon, 0, 0);
        tabThree.setCompoundDrawablePadding((int) DimensionUtils.convertDpToPixel(3));
        tabLayout.getTabAt(2).setCustomView(tabThree);
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
        ErrorAlertDialog.show(getActivity(), message);
    }

    @Override
    public void onRequestStarted() {
        showProgressBar();
    }

    @Override
    public void onRequestFinished() {
        hideProgressBar();
    }
}
