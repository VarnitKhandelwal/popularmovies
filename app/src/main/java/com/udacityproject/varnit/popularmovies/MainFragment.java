package com.udacityproject.varnit.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements FetchMovieListTask.ListTaskCallback, FavoritesTask.TaskCompleteCallback {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    private static final String MOVIE_LIST = "movie_list";
    private static final int defaultMovieInd = 0;

    private ArrayList<Movie> movies;
    private MovieAdapter mMovieAdapter;
    private GridView mGridView;
    private FavoritesTask favoritesTask;
    private String sortPref;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST)) {
            movies = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST, movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) view.findViewById(R.id.grid_movie);
        updateMovies(); // Initial fetch.

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Temporary refresh button.
        if (id == R.id.action_refresh) {
            updateMovies();
        } else if (id == R.id.action_clear_fav) {
            if (favoritesTask == null || favoritesTask.getStatus() == AsyncTask.Status.FINISHED) {
                favoritesTask = new FavoritesTask(getContext(), FavoritesTask.CLEAR, this);
            }

            if (favoritesTask.getStatus() != AsyncTask.Status.RUNNING) {
                favoritesTask.execute();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateMovies() {
        sortPref = Utility.getPreferredSort(getContext());

        if (sortPref.equals("favorites.desc")) {
            movies = Utility.getFavoritesList(getContext());
            onTaskComplete(movies);
        } else {
            FetchMovieListTask fetchMovieListTask = new FetchMovieListTask(getContext(), this);
            fetchMovieListTask.execute(sortPref);
        }

    }

    private void initMovieAdapter(final ArrayList<Movie> movies) {
        mMovieAdapter = new MovieAdapter(getContext(), R.layout.image_item, movies);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((FragmentCallback) getActivity()).onItemClick(movies.get(position));
            }
        });
    }

    // FetchMovieListTask
    @Override
    public void onTaskComplete(ArrayList<Movie> movies) {
        this.movies = movies;
        if (mMovieAdapter == null) {
            // If it's null, it's the first time using it so we need to do some initializing.
            initMovieAdapter(movies);
        } else {
            mMovieAdapter.clear();
            mMovieAdapter.addAll(movies);
        }

        mMovieAdapter.notifyDataSetChanged();

        Movie movie = this.movies.isEmpty() ? null : this.movies.get(0);
        ((FragmentCallback) getActivity()).onFetchTaskComplete(movie);
    }

    // FavoritesTask
    @Override
    public void onTaskComplete() {
        updateMovies();
    }

    public interface FragmentCallback {
        void onItemClick(Movie movie);
        void onFetchTaskComplete(Movie defaultMovie);
    }
}
