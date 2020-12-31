package com.example.newsapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listView;
    private TextView menuTitle;
    private String menu_title = "Top Headlines";
    private String newsURL = "https://www.livemint.com/rss/news";
    private DrawerLayout drawerLayout;
    private ProgressBar progressBar;
    private TextView warningMessage;
    private ImageView warningImage;
    private View divider;
 //   public static final String NEWS="NEWS";

    @Override
    public void onBackPressed() {
        onExitClickListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        listView = findViewById(R.id.listview);
        NavigationView navigationView = findViewById(R.id.navigation_menu);
        menuTitle = findViewById(R.id.menuTitle);
        progressBar = findViewById(R.id.progressBar);
        warningImage = findViewById(R.id.warningImage);
        warningMessage = findViewById(R.id.warningMessage);
        divider = findViewById(R.id.divider);


        if (!internetChecker()) {
            ifNoNet();
        }

        LayoutInflater layoutInflater = getLayoutInflater();
        View footer = layoutInflater.inflate(R.layout.listview_footer, listView, false);
        listView.addFooterView(footer);

        menuTitle.setText(menu_title);
        DownloadData downloadData = new DownloadData();
        downloadData.execute(newsURL);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String currentURL = newsURL;
                switch (item.getItemId()) {
                    case R.id.headlines:
                        newsURL = "https://www.livemint.com/rss/news";
                        break;
                    case R.id.political:
                        newsURL = "https://www.livemint.com/rss/politics";
                        break;
                    case R.id.sports:
                        newsURL = "https://www.livemint.com/rss/sports";
                        break;
                    case R.id.education:
                        newsURL = "https://www.livemint.com/rss/education";
                        break;
                    case R.id.technology:
                        newsURL = "https://www.livemint.com/rss/technology";
                        break;
                    case R.id.science:
                        newsURL = "https://www.livemint.com/rss/science";
                        break;
                    case R.id.market:
                        newsURL = "https://www.livemint.com/rss/markets";
                        break;
                    case R.id.industry:
                        newsURL = "https://www.livemint.com/rss/industry";
                        break;
                    default://nothing to do
                }
                drawerLayout.closeDrawers();
                if (!newsURL.equalsIgnoreCase(currentURL)) {
                    if (internetChecker())
                        ifNet();
                    else
                        ifNoNet();
                    DownloadData downloadData = new DownloadData();
                    downloadData.execute(newsURL);
                    menu_title = item.getTitle().toString();
                    menuTitle.setText(menu_title);
                }
                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//CTRL+Q for more info
        getMenuInflater().inflate(R.menu.refresh_buttin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                if (internetChecker())
                    ifNet();
                else
                    ifNoNet();

                DownloadData downloadData = new DownloadData();
                downloadData.execute(newsURL);
                return true;
            case R.id.exit:
                onExitClickListener();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }


    class DownloadData extends AsyncTask<String, Void, ArrayList<newsEntry>> {
        private static final String TAG = "DownloadData";

        @Override
        protected ArrayList<newsEntry> doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Downloading starts with " + strings[0]);
            String newsFeed = downloadNews(strings[0]);
            if (newsFeed == null) {
                Log.d(TAG, "doInBackground: Error in downloading");
            }
            parsingNewsFeed parser = new parsingNewsFeed();
            parser.parse(newsFeed);

            return parser.getNewsFeed();
        }

        @Override
        protected void onPostExecute(final ArrayList<newsEntry> newsFeed) {
            super.onPostExecute(newsFeed);
            final feedAdapter adapter = new feedAdapter(MainActivity.this, R.layout.feeder, newsFeed);
            listView.setAdapter(adapter);
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    newsEntry news=newsFeed.get(position);
//                    Intent intent=new Intent(MainActivity.this,WebviewActivity.class);
//                    intent.putExtra(NEWS,news);
//                    startActivity(intent);
//
//                }
//            });
        }

        private String downloadNews(String newsURL) {
            StringBuilder xmlFeed = new StringBuilder();
            try {
                URL url = new URL(newsURL);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "downloadNews: Response Code:" + responseCode);

                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                int charsRead;
                char[] inputBuffer = new char[1000];
                while (true) {
                    charsRead = reader.read(inputBuffer);
                    if (charsRead == -1) {
                        break;
                    }
                    if (charsRead > 0) {
                        xmlFeed.append(inputBuffer, 0, charsRead);
                    }
                }
                reader.close();
                return xmlFeed.toString();
            } catch (Exception e) {
                Log.d(TAG, "downloadNews: Error while downloading!!\nError Message:" + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

    private boolean internetChecker() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void ifNoNet() {
        divider.setVisibility(View.INVISIBLE);
        menuTitle.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        warningMessage.setVisibility(View.VISIBLE);
        warningImage.setVisibility(View.VISIBLE);
    }

    private void ifNet() {
        divider.setVisibility(View.VISIBLE);
        menuTitle.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        warningMessage.setVisibility(View.INVISIBLE);
        warningImage.setVisibility(View.INVISIBLE);
    }

    private void onExitClickListener() {
        AlertDialog.Builder a_dialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        a_dialogBuilder.setMessage("Do you want to exit this app?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = a_dialogBuilder.create();
        alertDialog.show();
    }

}
