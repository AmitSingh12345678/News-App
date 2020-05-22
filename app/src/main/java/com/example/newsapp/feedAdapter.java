package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class feedAdapter extends ArrayAdapter {
    private static final String TAG = "adapter";
    private int resource;
    private LayoutInflater layoutInflater;

    private List<newsEntry> newsFeed;

    public feedAdapter(@NonNull Context context, int resource, @NonNull List<newsEntry>newsFeed) {
        super(context, resource);
        this.resource=resource;
        this.layoutInflater=LayoutInflater.from(context);
        this.newsFeed=newsFeed;
    }

    @Override
    public int getCount() {
        return newsFeed.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      viewHolder viewHolder;
       if(convertView==null) {
           convertView = layoutInflater.inflate(resource, parent, false);
            viewHolder=new viewHolder(convertView);
            convertView.setTag(viewHolder);
       }
       else{
           viewHolder=(viewHolder)convertView.getTag();
       }

       newsEntry feed=newsFeed.get(position);

        viewHolder.newsTitle.setText(feed.getNewsTitle());
        viewHolder.newsDescription.setText(feed.getDescription());
        viewHolder.pubDate.setText(feed.getPubDate());
        Glide.with(parent.getContext()).load(feed.getImageURL()).into(viewHolder.newsImage);

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    class viewHolder{
       final TextView newsTitle;
       final TextView newsDescription;
       final TextView pubDate;
       final ImageView newsImage;

        public viewHolder(View v){
             newsTitle=v.findViewById(R.id.title);
             newsDescription=v.findViewById((R.id.description));
             pubDate=v.findViewById(R.id.pubDate);
             newsImage=v.findViewById(R.id.image);
        }
    }
}
