package com.udacityproject.varnit.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Instead of retrieving this information in FetchMovieListTask,
 * I'll avoid network congestion by retrieving more detailed
 * information only when I need to (a user clicks on a movie
 * in MainFragment and launches DetailFragment).
 */
public class FetchMovieDetailTask extends AsyncTask<String, Void, Movie> {

    private static final String LOG_TAG = FetchMovieDetailTask.class.getSimpleName();

    private Context mContext;
    private DetailTaskCallback mCallback;
    private Movie mMovie;

    public FetchMovieDetailTask(Context context, Movie movie, DetailTaskCallback callback) {
        mContext = context;
        mCallback = callback;
        mMovie = movie;
    }

    private Movie getMovieDataFromJson(String movieJsonStr) throws JSONException {
        final String JON_RUNTIME = "runtime";

        final String JON_TRAILERS = "trailers";
        final String JON_YOUTUBE = "youtube";
        final String JON_YT_NAME = "name";
        final String JON_YT_SOURCE = "source";

        final String JON_REVIEWS = "reviews";
        final String JON_RESULTS = "results";
        final String JON_AUTHOR = "author";
        final String JON_CONTENT = "content";

        JSONObject movieJson = new JSONObject(movieJsonStr);

        String runtime = movieJson.getString(JON_RUNTIME);

        JSONObject trailerJson = movieJson.getJSONObject(JON_TRAILERS);
        JSONArray youtubeJsonArray = trailerJson.getJSONArray(JON_YOUTUBE);

        ArrayList<String> trailers = new ArrayList<>();
        if (youtubeJsonArray.length() <= 0) Log.w(LOG_TAG, "youtubeJson.length() <= 0");

        for (int i = 0; i < youtubeJsonArray.length(); i++) {
            JSONObject youtubeJsonObj = youtubeJsonArray.getJSONObject(i);
            String name = youtubeJsonObj.getString(JON_YT_NAME);
            String source = youtubeJsonObj.getString(JON_YT_SOURCE);
            trailers.add(name + " - " + source); // The rest of the URI will be added later.
        }

        JSONObject reviewsJson = movieJson.getJSONObject(JON_REVIEWS);
        JSONArray reviewsResults = reviewsJson.getJSONArray(JON_RESULTS);

        ArrayList<String> reviews = new ArrayList<>();
        if (reviewsResults.length() <= 0) Log.w(LOG_TAG, "reviewResults.length <= 0");

        for (int i = 0; i < reviewsResults.length(); i++) {
            JSONObject reviewJson = reviewsResults.getJSONObject(i);
            String author = reviewJson.getString(JON_AUTHOR);
            String content = reviewJson.getString(JON_CONTENT);
            String reviewStr = author + " - " + content;
            reviews.add(reviewStr);
        }

        mMovie.setRuntime(runtime);
        mMovie.setTrailerList(trailers);
        mMovie.setReviewList(reviews);

        return mMovie;
    }

    @Override
    protected Movie doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;

        try {
            // Parts of the URI to retrieve the list of movies.
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_PARAM = "api_key";
            final String APPEND_PARAM = "append_to_response";

            String movie_id = params[0];
            String api_key = Utility.getKey(mContext);
            String append_param = "trailers,reviews";

            // Build the URI with our parameters.
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(movie_id)
                    .appendQueryParameter(API_PARAM, api_key)
                    .appendQueryParameter(APPEND_PARAM, append_param)
                    .build();

            // Convert to URL and attempt to connect to it.
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the page text in JSON format into a stream and buffer.
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }

            movieJsonStr = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        super.onPostExecute(movie);
        mCallback.onTaskComplete(movie);
    }

    public interface DetailTaskCallback {
        void onTaskComplete(Movie movie);
    }
}
