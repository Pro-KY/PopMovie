package com.example.proky.popmovie;

public class Film {

    private int id;
    //private String title;
    private String poster_path;
    //private String release_date;
    // String duration;
    //private double rating;
    //private String overview;

    public Film (int id, String poster) {
        this.id = id;
        //this.title = title;
        this.poster_path = poster;
        /*
        this.release_date = release_date;
        this.rating = rating;
        this.overview = overview;
        */
    }

    public int getId() {
        return id;
    }

    /*
    public String getTitle() {
        return title;
    }
    */

    public String getPosterPath() {
        return poster_path;
    }

    /*
    public String getReleaseDate() {
        return release_date;
    }

    public double getRating() {
        return rating;
    }

    public String getOverview() {
        return overview;
    }
    */
}
