package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class feedAdapter extends ArrayAdapter {
    private static final String TAG = "adapter";
    private int resource;
    private LayoutInflater layoutInflater;
    private int selectedItem=-1;

    public static final String NEWS="NEWS";

    private List<newsEntry> newsFeed;

    public feedAdapter(@NonNull Context context, int resource, @NonNull List<newsEntry> newsFeed) {
        super(context, resource);
        this.resource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.newsFeed = newsFeed;
    }

    @Override
    public int getCount() {
        return newsFeed.size();
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        viewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(resource, parent, false);
            viewHolder = new viewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (viewHolder) convertView.getTag();
        }

        final newsEntry feed = newsFeed.get(position);

        viewHolder.newsTitle.setText(feed.getNewsTitle());
        viewHolder.newsDescription.setText(feed.getDescription());
        viewHolder.pubDate.setText(feed.getPubDate());
        Glide.with(parent.getContext()).load(feed.getImageURL()).into(viewHolder.newsImage);

        viewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(parent.getContext(),WebviewActivity.class);
                intent.putExtra(NEWS,feed);
                parent.getContext().startActivity(intent);
                /**
                To start a new activity you will need a context to start from, and your current
                 activity "feedAdapter" is not a Context, luckly every view has a Context, so you
                 can do like this.
                 We can pass MainActivity.this as MainActivity extends from AppCombat which means
                 MainActivity also extends from Context class.
                 See more about what we can pass inplace of context.
               */

            }
        });

        return convertView;
    }
    class viewHolder {
        final TextView newsTitle;
        final TextView newsDescription;
        final TextView pubDate;
        final ImageView newsImage;
        final MaterialCardView mCardView;

        public viewHolder(View v) {
            newsTitle = v.findViewById(R.id.title);
            newsDescription = v.findViewById((R.id.description));
            pubDate = v.findViewById(R.id.pubDate);
            newsImage = v.findViewById(R.id.image);
            mCardView=v.findViewById(R.id.cardview);
        }
    }
}
