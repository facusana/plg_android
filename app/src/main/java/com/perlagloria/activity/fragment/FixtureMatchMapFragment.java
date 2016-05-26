package com.perlagloria.activity.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.perlagloria.R;
import com.perlagloria.util.ServerApi;
import com.perlagloria.util.SharedPreferenceKey;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FixtureMatchMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FixtureMatchMapFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private int teamId;
    //private ImageView fixtureMatchMapImgView;
    private WebView mapWebView;

    public FixtureMatchMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FixtureMatchMapFragment.
     */
    public static FixtureMatchMapFragment newInstance(String param1) {
        FixtureMatchMapFragment fragment = new FixtureMatchMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fixture_match_map, container, false);

        //fixtureMatchMapImgView = (ImageView) rootView.findViewById(R.id.fixtureMatchMapImgView);
        mapWebView = (WebView) rootView.findViewById(R.id.mapWebView);

        mapWebView.getSettings().setLoadWithOverviewMode(true);
        mapWebView.getSettings().setUseWideViewPort(true);

        loadFixtureMatchMapImage();

        return rootView;
    }

    private void loadFixtureMatchMapImage() {
        SharedPreferences sPref = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        teamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);

        mapWebView.loadUrl(ServerApi.loadFixtureMatchMapImageUrl + teamId);

        //new ImageDownloader(fixtureMatchMapImgView).execute(ServerApi.loadFixtureMatchMapImageUrl + teamId);
    }

}
