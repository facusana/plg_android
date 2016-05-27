package com.perlagloria.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.perlagloria.R;
import com.perlagloria.util.FontManager;
import com.perlagloria.util.ServerApi;
import com.perlagloria.util.SharedPreferenceKey;

public class FixtureMatchMapFragment extends Fragment {

    private int teamId;
    private TextView mapTitle;
    private SubsamplingScaleImageView map;

    public FixtureMatchMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fixture_match_map, container, false);

        mapTitle = (TextView) rootView.findViewById(R.id.map_title);
        map = (SubsamplingScaleImageView) rootView.findViewById(R.id.map_image);

        mapTitle.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));

        loadFixtureMatchMapImage();

        return rootView;
    }

    private void loadFixtureMatchMapImage() {
        SharedPreferences sPref = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        teamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);


        Glide.with(this)
                .load(ServerApi.loadFixtureMatchMapImageUrl + teamId)
                .asBitmap()
                .thumbnail(0.5f)
                //.error(R.drawable.image_not_found)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        map.setImage(ImageSource.bitmap(bitmap));
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        Toast.makeText(getActivity(), "Error during loading", Toast.LENGTH_LONG).show();
                        //productImage.setImage(ImageSource.resource(R.drawable.image_not_found));
                        //productImage.setZoomEnabled(false);
                    }
                });
    }

}
