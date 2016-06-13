package com.udacityproject.varnit.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Movie implements Parcelable {

    // Constant String to ensure that they're all the same.
    public static final String PARCEL_TAG = "movie_tag";

    private String id;
    private String title;
    private String poster_url;
    private String overview;
    private float vote_avg;
    private String release_date;
    private boolean favorite;
    private String runtime;

    private ArrayList<String> trailerList;
    private ArrayList<String> reviewList;

    public Movie () {

    }

    public Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        poster_url = in.readString();
        overview = in.readString();
        vote_avg = in.readFloat();
        release_date = in.readString();
        favorite = in.readInt() == 0;

        if (trailerList == null) trailerList = new ArrayList<>();
        if (reviewList == null) reviewList = new ArrayList<>();

        in.readStringList(trailerList);
        in.readStringList(reviewList);
    }

    public Movie(String id, String title, String poster_url, String overview, float vote_avg, String release_date) {
        this.id = id;
        this.title = title;
        this.poster_url = poster_url;
        this.overview = overview;
        this.vote_avg = vote_avg;
        this.release_date = release_date;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getVote_avg() {
        return vote_avg;
    }

    public void setVote_avg(float vote_avg) {
        this.vote_avg = vote_avg;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getReleaseYear() {
        // Release date format is YYYY-MM-DD, so just get the first four characters.
        return release_date.substring(0, 4);
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public ArrayList<String> getTrailerList() {
        return trailerList;
    }

    public void setTrailerList(ArrayList<String> trailerList) {
        this.trailerList = trailerList;
    }

    public static String[] convertTrailerStr(String trailer) {
        return trailer.split("-", 2);
    }

    public ArrayList<String> getReviewList() {
        return reviewList;
    }

    public void setReviewList(ArrayList<String> reviewList) {
        this.reviewList = reviewList;
    }

    // Reviews are stored in one string as so: author - content.
    // That way, I don't need to make an entire class.
    // This little utility will split it for me, saving some code.
    public static String[] convertReviewStr(String review) {
        return review.trim().split("-", 2);
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(title);
        out.writeString(poster_url);
        out.writeString(overview);
        out.writeFloat(vote_avg);
        out.writeString(release_date);
        out.writeInt(favorite ? 0 : 1);
        out.writeStringList(trailerList);
        out.writeStringList(reviewList);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }


    };
}
