package com.udacityproject.varnit.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, int resource, List<Movie> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(getContext());
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(getContext())
                .load(getItem(position).getPoster_url())
                .placeholder(R.drawable.default_film)
                .fit() // Trying to get the images right up against each other.
                .centerInside()
                .into(imageView);

        return imageView;
    }
}
