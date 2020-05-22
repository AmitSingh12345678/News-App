package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listView;
    private ProgressBar progressBar;
    private feedAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listview);
        progressBar=findViewById((R.id.progressBar));

        DownloadData downloadData=new DownloadData();

        downloadData.execute("https://www.livemint.com/rss/news");

    }

    class DownloadData extends AsyncTask<String,Void, ArrayList<newsEntry>>{
        private static final String TAG = "DownloadData";
        @Override
        protected void onPostExecute(ArrayList<newsEntry> newsFeed) {
            super.onPostExecute(newsFeed);
            progressBar.setVisibility(View.GONE);
            adapter=new feedAdapter(MainActivity.this,R.layout.feeder,newsFeed);
            listView.setAdapter(adapter);
//            for(newsEntry news:newsFeed){
//                updateImage(news);
//            }
            adapter.get

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<newsEntry> doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Downloading starts with "+strings[0]);
            String newsFeed=downloadNews(strings[0]);
            if(newsFeed==null)
                Log.d(TAG, "doInBackground: Error in downloading");
            parsingNewsFeed parser=new parsingNewsFeed();
            parser.parse(newsFeed);

           return  parser.getNewsFeed();
        }
        private String downloadNews(String newsURL){
            StringBuilder xmlFeed=new StringBuilder();
           try {
               URL url = new URL(newsURL);
               HttpsURLConnection connection=(HttpsURLConnection)url.openConnection();
               int responseCode=connection.getResponseCode();
               Log.d(TAG, "downloadNews: Response Code:"+responseCode);

               InputStream inputStream=connection.getInputStream();
               InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
               BufferedReader reader=new BufferedReader(inputStreamReader);

               int charsRead;
               char[] inputBuffer=new char[1000];
               while(true){
                   charsRead=reader.read(inputBuffer);
                   if(charsRead==-1)
                       break;
                   if(charsRead>0) {
                       xmlFeed.append(inputBuffer,0,charsRead);
                   }
               }
               reader.close();
               return xmlFeed.toString();
           }catch(Exception e){
               Log.d(TAG, "downloadNews: Error while downloading!!\nError Message:"+e.getMessage());
               e.printStackTrace();
           }
           return null;
        }
    }
private void updateImage(newsEntry news){

       downloadingImage downloader= new downloadingImage(news);
       downloader.execute(news.getImageURL());
       adapter.notifyDataSetChanged();

}

}
