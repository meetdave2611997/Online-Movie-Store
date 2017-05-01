package com.example.meetdave.myapplication;

/**
 * Created by Meet Dave on 05-03-2017.
 */
public class MovieListModel {
    String movieName;
    String moviePrice;
    String movieImage;
    String movieID;
    String movieQty;


    public MovieListModel(String movieID,String movieName,String moviePrice,String movieImage){
        this.movieID=movieID;
        this.movieName=movieName;
        this.movieImage=movieImage;
        this.moviePrice=moviePrice;

    }


    public void setMovieQty(String movieQty) {
        this.movieQty = movieQty;
    }

    public String getMovieQty() {
        return movieQty;

    }

    public String getMovieImage() {
        return movieImage;
    }

    public String getMovieID() {
        return movieID;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getMoviePrice() {
        return moviePrice;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setMoviePrice(String moviePrice) {
        this.moviePrice = moviePrice;
    }
}
