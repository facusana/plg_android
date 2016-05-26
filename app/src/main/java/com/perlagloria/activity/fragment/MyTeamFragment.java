package com.perlagloria.activity.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.perlagloria.R;
import com.perlagloria.adapter.MyTeamFragmentPagerAdapter;

public class MyTeamFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyTeamFragmentPagerAdapter myFragmentAdapter;

    public MyTeamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_team, container, false);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

        myFragmentAdapter = new MyTeamFragmentPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(myFragmentAdapter);

        tabLayout.setTabsFromPagerAdapter(myFragmentAdapter);
        tabLayout.setTabTextColors(getResources().getColor(R.color.tabNormal), getResources().getColor(R.color.tabSelected));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tabSelected));
        tabLayout.setSelectedTabIndicatorHeight(0);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        TextView tab1 = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        tab1.setText("Equipo");
        tab1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_selector, 0, 0);
        tab1.setSelected(true);

        TextView tab2 = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        tab2.setText("Mapa");
        tab2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_selector, 0, 0);

        tabLayout.getTabAt(0).setCustomView(tab1);
        tabLayout.getTabAt(1).setCustomView(tab2);

        return rootView;
    }
}
