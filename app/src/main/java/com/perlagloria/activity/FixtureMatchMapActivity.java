package com.perlagloria.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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

public class FixtureMatchMapActivity extends AppCompatActivity {

    private int teamId;
    private TextView mapTitle;
    private ProgressBar progressBar;
    private SubsamplingScaleImageView map;
    private TextView imageNotAvailable;

    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixture_match_map);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        if (mainToolbar != null) {
            toolbarTitle = (TextView) mainToolbar.findViewById(R.id.toolbar_title);
            toolbarTitle.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, this));

            setSupportActionBar(mainToolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("");
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

            }

            SharedPreferences sPref = getSharedPreferences("config", Context.MODE_PRIVATE);
            toolbarTitle.setText(sPref.getString(SharedPreferenceKey.TEAM_NAME, "").toUpperCase());
        }

        progressBar = (ProgressBar) findViewById(R.id.map_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        mapTitle = (TextView) findViewById(R.id.map_title);
        map = (SubsamplingScaleImageView) findViewById(R.id.map_image);
        imageNotAvailable = (TextView) findViewById(R.id.image_not_available_title);

        mapTitle.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_LIGHT, this));
        imageNotAvailable.setTypeface(FontManager.getInstance().getFont(FontManager.Fonts.HELVETICA_NEUE_BOLD, this));

        loadFixtureMatchMapImage();
    }

    private void loadFixtureMatchMapImage() {
        showProgressBar();

        SharedPreferences sPref = getSharedPreferences("config", Context.MODE_PRIVATE);
        teamId = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);

        Glide.with(this)
                .load(ServerApi.loadFixtureMatchMapImageUrl + teamId)
                .asBitmap()
                .thumbnail(0.25f)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(-90);

                        map.setImage(ImageSource.bitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true)));
                        hideProgressBar();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        imageNotAvailable.setVisibility(View.VISIBLE);
                        hideProgressBar();
                    }
                });
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
