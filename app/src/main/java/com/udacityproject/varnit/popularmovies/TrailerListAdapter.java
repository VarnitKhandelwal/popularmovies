package com.udacityproject.varnit.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Tyler on 11/4/2015.
 */
public class TrailerListAdapter extends ArrayAdapter<String> {
    public TrailerListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public TrailerListAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.trailer_item, parent, false);
        } else {
            view = convertView;
        }

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView text = (TextView) view.findViewById(R.id.text1);

        Picasso.with(getContext())
                .load(R.drawable.ic_play_arrow_black_48dp)
                .into(icon);

        String name = Movie.convertTrailerStr(getItem(position))[0];
        text.setText(name);

        return view;
    }
}
