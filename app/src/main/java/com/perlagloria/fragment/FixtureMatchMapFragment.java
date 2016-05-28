package com.perlagloria.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private TextView imageNotAvailable;

    public FixtureMatchMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fixture_match_map, container, false);

        mapTitle = (TextView) rootView.findViewById(R.id.map_title);
        map = (SubsamplingScaleImageView) rootView.findViewById(R.id.map_image);
        imageNotAvailable = (TextView) rootView.findViewById(R.id.image_not_available_title);

        mapTitle.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, getActivity()));
        imageNotAvailable.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, getActivity()));

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
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(-90);

                        map.setImage(ImageSource.bitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true)));
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        imageNotAvailable.setVisibility(View.VISIBLE);
                    }
                });
    }

}
