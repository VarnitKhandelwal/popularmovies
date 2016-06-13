package com.udacityproject.varnit.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private DetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        if (savedInstanceState == null) {
            // Get the Movie from the intent, and send it off to the new DetailFragment.
            fragment = Utility.getDetailFragWithArgs((Movie) getIntent().getParcelableExtra(Movie.PARCEL_TAG), false);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
