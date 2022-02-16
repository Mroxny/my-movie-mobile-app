package com.mroxny.mymovie;

import java.io.Serializable;

public class Rate implements Serializable {

    private int id;
    private String movieTitle;
    private int userID;
    private String userName;
    private String userProfile;
    private int ratePictures;
    private int ratePlot;
    private int rateCast;
    private int rateAudio;
    private int rate;



    public Rate (int id, String movieTitle, int userId, String userName, String userProfile, int ratePictures, int ratePlot, int rateCast, int rateAudio, int rate){
        setId(id);
        setMovieTitle(movieTitle);
        setUserID(userId);
        setUserName(userName);
        setUserProfile(userProfile);
        setRatePictures(ratePictures);
        setRatePlot(ratePlot);
        setRateCast(rateCast);
        setRateAudio(rateAudio);
        setRate(rate);
    }

    public void setId(int idNumber){
        this.id = idNumber;
    }

    public int getId(){
        return this.id;
    }

    public void setMovieTitle(String title){
        this.movieTitle = title;
    }

    public String getMovieTitle(){
        return this.movieTitle;
    }

    public void setUserID(int idNumber){
        this.userID = idNumber;
    }

    public int getUserID(){
        return this.userID;
    }

    public void setUserName(String name){
        this.userName = name;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setUserProfile(String path){
        this.userProfile = path;
    }

    public String getUserProfile(){
        return this.userProfile;
    }

    public void setRatePictures(int rate){
        this.ratePictures = rate;
    }

    public int getRatePictures(){
        return this.ratePictures;
    }

    public void setRatePlot(int rate){
        this.ratePlot = rate;
    }

    public int getRatePlot(){
        return this.ratePlot;
    }

    public void setRateCast(int rate){
        this.rateCast = rate;
    }

    public int getRateCast(){
        return this.rateCast;
    }

    public void setRateAudio(int rate){
        this.rateAudio = rate;
    }

    public int getRateAudio(){
        return this.rateAudio;
    }

    public void setRate(int rate){
        this.rate = rate;
    }

    public int getRate(){
        return this.rate;
    }

    public float getAverageRate(){
        float avgRate=0;
        avgRate += getRatePictures();
        avgRate += getRatePlot();
        avgRate += getRateCast();
        avgRate += getRateAudio();
        //avgRate += getRate();
        avgRate = avgRate/4;
        return avgRate;
    }





}

