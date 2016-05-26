package com.perlagloria.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyLog;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    private static final String IMAGE_DOWNLOADER_TAG = "image_downloader_loading";
    private ImageView imageView;

    public ImageDownloader(ImageView imageView) {
        super();
        this.imageView = imageView;
        //this.doInBackground(url);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPreExecute() {
        //simpleWaitDialog = ProgressDialog.show(MainActivity.this, "Wait", "Downloading Image");
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        VolleyLog.d(IMAGE_DOWNLOADER_TAG, result);
        if (result != null) {
            imageView.setImageBitmap(result);
            imageView.setVisibility(View.VISIBLE);
        } else {
            VolleyLog.d("ERROR");
        }

        //simpleWaitDialog.dismiss();
    }

    private Bitmap downloadBitmap(String urlString) {
        final URL url;
        final HttpURLConnection connection;

        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            //input.close();
            //connection.disconnect();

            return myBitmap;
        } catch (Exception e) {
            //getRequest.abort();
            VolleyLog.d("ERROR de imagen", e);
        }
        return null;
    }
}