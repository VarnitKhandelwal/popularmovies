package com.udacityproject.varnit.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

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

public class FetchMovieListTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private static final String LOG_TAG = FetchMovieListTask.class.getSimpleName();

    private Context mContext;
    private ListTaskCallback mCallback;

    public FetchMovieListTask(Context context, ListTaskCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException {
        // If it can't access the network, it'll be null.
        // Since it's null, just send an empty ArrayList.
        if (movieJsonStr == null) {
            return new ArrayList<>();
        }

        // Json Object Name (JON) for the name/value sets in the movie Json string.
        final String JON_RESULTS = "results";

        // These are the only values we care about in the JSON.
        final String JON_ID = "id";
        final String JON_OVERVIEW = "overview";
        final String JON_RELEASE_DATE = "release_date";
        final String JON_POSTER_PATH = "poster_path";
        final String JON_TITLE = "title";
        final String JON_VOTE_AVERAGE = "vote_average";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(JON_RESULTS);

        // Loop through the JSON Array and get the JSON Movie Objects.
        ArrayList<Movie> movies = new ArrayList<>(movieArray.length());
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject currMovie = movieArray.getJSONObject(i);

            // Retrieve the data we want from the JSON text.
            String id = currMovie.getString(JON_ID);
            String overview = currMovie.getString(JON_OVERVIEW);
            String release_date = currMovie.getString(JON_RELEASE_DATE);
            String poster_path = Utility.getPosterUrl(currMovie.getString(JON_POSTER_PATH));
            String title = currMovie.getString(JON_TITLE);
            float vote_average = (float) currMovie.getDouble(JON_VOTE_AVERAGE);

            // Make a movie object and add it to the list of movies.
            Movie movie = new Movie(id, title, poster_path, overview, vote_average, release_date);
            movie.setFavorite(Utility.isFavorite(mContext, id));
            movies.add(movie);
        }

        return movies;
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;

        try {
            // Parts of the URI to retrieve the list of movies.
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_PARAM = "api_key";

            String sort = params[0];
            String api_key = Utility.getKey(mContext);

            // Build the URI with our parameters.
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sort)
                    .appendQueryParameter(API_PARAM, api_key)
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
    protected void onPostExecute(ArrayList<Movie> movies) {
        super.onPostExecute(movies);
        mCallback.onTaskComplete(movies);
    }

    // After it's finished with the Task, we need to tell MainFragment it's done.
    public interface ListTaskCallback {
        void onTaskComplete(ArrayList<Movie> movies);
    }
}
