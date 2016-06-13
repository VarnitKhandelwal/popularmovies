package com.udacityproject.varnit.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Tyler on 11/5/2015.
 */
public class ReviewListAdapter extends ArrayAdapter<String> {
    public ReviewListAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);
        } else {
            view = convertView;
        }

        TextView title = (TextView) view.findViewById(R.id.text1);
        TextView content = (TextView) view.findViewById(R.id.text2);

        String[] review = Movie.convertReviewStr(getItem(position));

        title.setText(review[0]);
        content.setText(review[1]);

        return view;
    }
}
