package com.udacityproject.varnit.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Utility {

    private static final String posterUrl = "http://image.tmdb.org/t/p/";

    // List of the different poster sizes, in case I want to dynamically change the size.
    private static final String imgXXS = "w92";
    private static final String imgXS = "w154";
    private static final String imgS = "w185";
    private static final String imgM = "w342";
    private static final String imgL = "w500";
    private static final String imgXL = "w780";
    private static final String imgOrg = "original";

    public static String getKey(Context context) {
        // GitHub ignores the file that the key is stored in,
        // so I don't have to delete any code before committing.
        return context.getString(R.string.api_key);
    }

    public static String getPreferredSort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.sort_pref_key), context.getString(R.string.sort_default)) + ".desc";
    }

    public static ArrayList<Movie> getFavoritesList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(FavoritesTask.FAV_NAME, Context.MODE_PRIVATE);

        if (prefs.contains(FavoritesTask.FAV_KEY)) {
            String movieJSON = prefs.getString(FavoritesTask.FAV_KEY, "");

            Gson gson = new Gson();
            Type arrayType = new TypeToken<ArrayList<Movie>>(){}.getType();
            return gson.fromJson(movieJSON, arrayType);
        }

        return new ArrayList<>();
    }

    public static void clearFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("favorites_list", 0);
        SharedPreferences.Editor editor = prefs.edit();

        editor.clear();
        editor.commit();
    }

    public static boolean isFavorite(Context context, String id) {
        SharedPreferences prefs = context.getSharedPreferences("favorites_list", 0);
        return prefs.getBoolean(id, false);
    }

    public static void setFavorite(Context context, String id, boolean favorite) {
        SharedPreferences prefs = context.getSharedPreferences("favorites_list", 0);
        SharedPreferences.Editor editor = prefs.edit();

        if (!favorite) {
            editor.remove(id);
        } else {
            editor.putBoolean(id, true);
        }

        editor.commit();
    }

    public static String getPosterUrl(String urlEnd) {
        return posterUrl + imgM + urlEnd;
    }

    public static String getYoutubeLink(String source) {
        final String BASE_URL = "http://www.youtube.com/watch";
        final String VIDEO_PARAM = "v";

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(VIDEO_PARAM, source.trim())
                .build();

        return uri.toString();
    }

    public static DetailFragment getDetailFragWithArgs(Movie movie, boolean isTwoPane) {
        Bundle args = new Bundle();
        args.putParcelable(Movie.PARCEL_TAG, movie);
        args.putBoolean(DetailFragment.PARCEL_TAG, isTwoPane);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
