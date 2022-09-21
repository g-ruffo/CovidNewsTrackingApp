package com.veltus.covidnewstracking.Queries;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageUrl extends AsyncTask<String, Void, Bitmap> {
    ImageView bitmapImage;

    /* Downloads the article images to be displayed in the fragment_favorites_feed.xml */
    public DownloadImageUrl(ImageView bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap downloadedBitmap = null;
        try {
            InputStream inputStream = new java.net.URL(urlDisplay).openStream();
            downloadedBitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return downloadedBitmap;
    }

    /* Once image is downloaded, setImageBitmap to ImageView inside MyViewHolder */
    protected void onPostExecute(Bitmap result) {
        bitmapImage.setImageBitmap(result);
    }
}