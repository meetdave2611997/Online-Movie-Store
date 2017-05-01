package com.example.meetdave.myapplication;

/**
 * Created by Meet Dave on 18-03-2017.
 */
public class OrderModel {
    String movieName;
    String movieImage;
    String moviePrice;
    String movieQty;
    String orderID;
    String orderDate;
    String orderStatus;

    public OrderModel(String orderID,String movieName,String moviePrice,String movieQty,String orderDate,String orderStatus,String movieImage){
        this.orderID=orderID;
        this.movieName=movieName;
        this.moviePrice=moviePrice;
        this.movieQty=movieQty;
        this.orderDate=orderDate;
        this.orderStatus=orderStatus;
        this.movieImage=movieImage;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public void setMoviePrice(String moviePrice) {
        this.moviePrice = moviePrice;
    }

    public void setMovieQty(String movieQty) {
        this.movieQty = movieQty;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public String getMoviePrice() {
        return moviePrice;
    }

    public String getMovieQty() {
        return movieQty;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}

