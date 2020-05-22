package com.example.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class downloadingImage extends AsyncTask<String,Void, Bitmap> {
    private static final String TAG = "downloadingImage";
    newsEntry news;

    public downloadingImage(newsEntry news) {
        this.news=news;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        news.setImageBitmap(bitmap);

    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap;
        String imageURL=strings[0];
        try {
            if(imageURL!=null) {
                InputStream inputStream = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                Log.d(TAG, "doInBackground: Image download completed");
                return bitmap;
            }
            else
                Log.d(TAG, "doInBackground: ImageURL is null");
        }catch(Exception e){
            Log.e(TAG, "doInBackground:Error while downloading images "+e.getMessage());
        }
        return null;
    }
}
