package com.perlagloria.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.perlagloria.activity.fragment.FixtureMatchInfoFragment;
import com.perlagloria.activity.fragment.FixtureMatchMapFragment;

public class MyTeamFragmentPagerAdapter extends FragmentPagerAdapter {
    public MyTeamFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FixtureMatchInfoFragment();
            case 1:
                return new FixtureMatchMapFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return getString(R.string.next_game);
//                case 1:
//                    return getString(R.string.tactic_map);
//                default:
//                    return null;
//            }

        return null;
    }
}
