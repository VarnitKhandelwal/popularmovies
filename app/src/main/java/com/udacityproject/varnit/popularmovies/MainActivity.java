package com.udacityproject.varnit.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Application Launcher Icon (res > minmap_(size) > movie_icon.png)
 * Icon Source: DesignBolts
 * URL: http://www.designbolts.com/2015/01/21/free-flat-long-shadow-multimedia-icons-1024-px-pngs-vector-ai-file/
 */

public class MainActivity extends AppCompatActivity implements MainFragment.FragmentCallback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    private boolean mSavedInsStateNull;
    private static final int PREF_CHANGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // If the layout is using sw600dp, it'll have R.id.movie_detail_container.
        if (findViewById(R.id.movie_detail_container) != null) {
            mSavedInsStateNull = savedInstanceState == null;
            mTwoPane = true;
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, PREF_CHANGE_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PREF_CHANGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                fragment.updateMovies();
            }
        }
    }

    @Override
    public void onItemClick(Movie movie) {
        // MainFragment can't decide how to handle different
        // screen sizes, so let MainActivity to that for us.
        if (mTwoPane) {
            DetailFragment fragment = Utility.getDetailFragWithArgs(movie, true);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(Movie.PARCEL_TAG, movie);
            startActivity(intent);
        }
    }

    @Override
    public void onFetchTaskComplete(Movie defaultMovie) {
        if (mTwoPane && mSavedInsStateNull) {
            if (defaultMovie != null) {
                DetailFragment fragment = Utility.getDetailFragWithArgs(defaultMovie, true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
    }
}
