package com.example.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class parsingNewsFeed {
    private static final String TAG = "parsingNewsFeed";
    private ArrayList<newsEntry> newsFeed;

    public parsingNewsFeed() {
        this.newsFeed = new ArrayList<>();
    }

    public ArrayList<newsEntry> getNewsFeed() {
        return newsFeed;
    }

    public void parse(String xmlData){
        Boolean inEntry=false;
        String textValue="";
        newsEntry feed=null;

        try{

            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp=factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));

            int eventType=xpp.getEventType();
            while(eventType!=XmlPullParser.END_DOCUMENT){
                String tagName=xpp.getName();

                switch (eventType){

                    case XmlPullParser.START_TAG:
                        if("item".equalsIgnoreCase(tagName)){
                            inEntry=true;
                            feed=new newsEntry();
                        }
                    break;
                    case XmlPullParser.TEXT:
                        textValue=xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(inEntry){
                            if("item".equalsIgnoreCase(tagName)){
                                inEntry=false;
                                newsFeed.add(feed);
                            }
                            if("title".equalsIgnoreCase(tagName))
                                feed.setNewsTitle(textValue);
                            if("description".equalsIgnoreCase(tagName))
                                feed.setDescription(textValue);
                            if("pubDate".equalsIgnoreCase(tagName))
                                feed.setPubDate(textValue);
                            if("image".equalsIgnoreCase(tagName)) {
                                feed.setImageURL(textValue);
                                feed.setImageBitmap(URLtoBitmap(textValue));
                            }
                            }
                        break;
                    default://nothing to do
                }

                eventType=xpp.next();
            }

        }catch (Exception e) {
            Log.e(TAG, "parser: Error while parsing:" + e.getMessage());
            e.printStackTrace();
        }

    }
    public Bitmap URLtoBitmap(String imageURL){

        Bitmap bitmap=null;
        try{
            if(imageURL!=null) {
                InputStream inputStream = new java.net.URL(imageURL).openStream();
                bitmap= BitmapFactory.decodeStream(inputStream);
                Log.d(TAG, "setImageBitmap: image loading completed");
            }
            else
                Log.d(TAG, "setImageBitmap: Image URL is null");
        }catch (Exception e){
            Log.d(TAG, "setImageBitmap: Error while loading images:"+e.getMessage());
            e.printStackTrace();
        }

        return bitmap;
    }
}
