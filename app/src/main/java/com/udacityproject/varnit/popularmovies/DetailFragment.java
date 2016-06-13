package com.udacityproject.varnit.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailFragment
        extends Fragment
        implements FetchMovieDetailTask.DetailTaskCallback,
            View.OnClickListener,
            FavoritesTask.TaskCompleteCallback {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String PARCEL_TAG = "two_panel_tag";

    private boolean mTwoPanel;
    private Movie mMovie;

    // In case I have to reference these outside onCreateView, save them here.
    private Button favoriteBtn;
    private ImageView posterView;
    private TextView titleView;
    private TextView yearView;
    private TextView overviewView;
    private TextView ratingView;
    private TextView runtimeView;
    private ListView trailerList;
    private ListView reviewList;
    private TrailerListAdapter trailerAdpt;
    private ReviewListAdapter reviewAdpt;
    private FavoritesTask favoritesTask;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // MainActivity and DetailActivity should add via arguments.
        if (getArguments() != null) {
            mMovie = getArguments().getParcelable(Movie.PARCEL_TAG);
        }

        if (!mTwoPanel) {
            fetchCurrMovie();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        favoriteBtn = (Button) rootView.findViewById(R.id.detail_favorite_btn);
        posterView = (ImageView) rootView.findViewById(R.id.detail_movie_poster);
        titleView = (TextView) rootView.findViewById(R.id.detail_movie_title);
        yearView = (TextView) rootView.findViewById(R.id.detail_movie_year);
        overviewView = (TextView) rootView.findViewById(R.id.detail_movie_overview);
        ratingView = (TextView) rootView.findViewById(R.id.detail_movie_rating);
        runtimeView = (TextView) rootView.findViewById(R.id.detail_movie_runtime);
        trailerList = (ListView) rootView.findViewById(R.id.detail_trailer_list);
        reviewList = (ListView) rootView.findViewById(R.id.detail_review_list);

        return rootView;
    }

    public void isTwoPane(boolean isTwoPane) {
        mTwoPanel = isTwoPane;
    }

    public void listLoaded(Movie currMovie) {
        // This is only relevent for tablets that
        // will use the Two Panel interface.
        if (mTwoPanel) {
            this.mMovie = currMovie;
            fetchCurrMovie();
        }
    }

    public void fetchCurrMovie() {
        FetchMovieDetailTask fetchTask = new FetchMovieDetailTask(getContext(), mMovie, this);
        fetchTask.execute(mMovie.getId());
    }

    /**
     * Source: http://stackoverflow.com/questions/9587754/how-to-add-two-listview-in-scrollview
     *
     * From what I can tell, what this function does is it expands the ListView
     * to the height of all of it's children. That way, the ListView doesn't need
     * to use the built-in scrolling functionality. This means that the ScrollView
     * the ListView is contained in, will scroll for it instead.
     *
     * Would the header and footer implementation be better, or does this work?
     *
     * @param listView - The ListView to be expanded to the height of all of it's children.
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ArrayAdapter listAdapter = (ArrayAdapter) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onTaskComplete(Movie movie) {
        mMovie = movie;
        updateViews();
    }

    private void updateViews() {
        Picasso.with(getContext())
                .load(mMovie.getPoster_url())
                .into(posterView);

        favoriteBtn.setOnClickListener(this);
        updateFavorites();

        titleView.setText(mMovie.getTitle());
        yearView.setText(mMovie.getReleaseYear());
        overviewView.setText(mMovie.getOverview());
        ratingView.setText(getString(R.string.detail_rating, mMovie.getVote_avg()));
        runtimeView.setText(mMovie.getRuntime() + " min");

        final String[] trailerArray = mMovie.getTrailerList().toArray(new String[mMovie.getTrailerList().size()]);
        trailerAdpt = new TrailerListAdapter(getContext(), R.layout.trailer_item, trailerArray);
        trailerList.setAdapter(trailerAdpt);
        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String trailer = trailerArray[position];
                String source = Movie.convertTrailerStr(trailer)[1]; // Second part is the URI
                String uri = Utility.getYoutubeLink(source);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        String[] reviewArray = mMovie.getReviewList().toArray(new String[mMovie.getReviewList().size()]);
        reviewAdpt = new ReviewListAdapter(getContext(), R.layout.review_item, reviewArray);
        reviewList.setAdapter(reviewAdpt);

        setListViewHeightBasedOnChildren(trailerList);
        setListViewHeightBasedOnChildren(reviewList);
    }

    private void updateFavorites() {
        if (mMovie.isFavorite()) {
            favoriteBtn.setText("Remove from Favorites");
        } else {
            favoriteBtn.setText("Add to Favorites");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.detail_favorite_btn) {
            // If someone pushes the button really fast, this will keep it from
            // having multiple tasks at once. Hopefully...
            if (favoritesTask != null && favoritesTask.getStatus() == AsyncTask.Status.RUNNING) {
                return;
            }

            boolean favorite = !mMovie.isFavorite();
            mMovie.setFavorite(favorite);
            updateFavorites();

            String taskAction = favorite ? FavoritesTask.ADD : FavoritesTask.REMOVE;
            favoritesTask = new FavoritesTask(getContext(), taskAction, this);
            ArrayList<Movie> list = new ArrayList<>(1);
            list.add(mMovie);
            favoritesTask.execute(list);
        }
    }

    @Override
    public void onTaskComplete() {

    }
}
