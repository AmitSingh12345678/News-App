package com.example.newsapp;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

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

    public void parse(String xmlData) {
        Boolean inEntry = false;
        String textValue = "";
        newsEntry feed = null;

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();

                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        if ("item".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            feed = new newsEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (inEntry) {
                            if ("item".equalsIgnoreCase(tagName)) {
                                inEntry = false;
                                newsFeed.add(feed);
                            }
                            if ("title".equalsIgnoreCase(tagName))
                                feed.setNewsTitle(stringFormatter(textValue));
                            if ("description".equalsIgnoreCase(tagName))
                                feed.setDescription(textValue);
                            if ("pubDate".equalsIgnoreCase(tagName))
                                feed.setPubDate(textValue.substring(0, textValue.length() - 9));
                            if ("bigimage".equalsIgnoreCase(tagName))
                                feed.setImageURL(textValue);
                            if ("link".equalsIgnoreCase(tagName)) {
                                feed.setLink(textValue);
                            }
                        }
                        break;
                    default://nothing to do
                }

                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.e(TAG, "parser: Error while parsing:" + e.getMessage());
            e.printStackTrace();
        }

    }

    private String stringFormatter(String input) {//to remove these parts <abc...> from string
        StringBuffer s = new StringBuffer(input);
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '<') {
                int from = i;
                while (s.charAt(i) != '>')
                    i++;
                s.replace(from, i + 1, "");
                i = from - 1;
            }
        }
        return s.toString();
    }
}
