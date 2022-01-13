package com.mroxny.mymovie;

import java.io.Serializable;

public class Movie implements Serializable {

    private int id;
    private String title;
    private String orgTitle;
    private int year;
    private String director;
    private String producer;
    private String image;
    private boolean approved;
    private int rateNum;
    private float rateAvg;

    public Movie (int id,
                  String title,
                  String orgTitle,
                  int year,
                  String director,
                  String producer,
                  String image,
                  boolean approved,
                  int rateNum,
                  float rateAvg){
        setId(id);
        setTitle(title);
        setOriginalTitle(orgTitle);
        setYear(year);
        setDirector(director);
        setProducer(producer);
        setImage(image);
        setApproved(approved);
        setRateNumber(rateNum);
        setRateAverage(rateAvg);
    }
    public Movie(int id, String title, int year, String director, boolean approved, int rateNum, float rateAvg){
        this(id,title,null,year,director,null,null,approved,rateNum,rateAvg);
    }

    public void setId(int idNumber){
        this.id = idNumber;
    }

    public int getId(){
        return this.id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public void setOriginalTitle(String title){
        this.orgTitle = title;
    }

    public String getOriginalTitle(){
        return this.orgTitle;
    }

    public void setYear(int year){
        this.year = year;
    }

    public int getYear(){
        return this.year;
    }

    public void setDirector(String director){
        this.director = director;
    }

    public String getDirector(){
        return this.director;
    }

    public void setProducer(String producer){
        this.producer = producer;
    }

    public String getProducer(){
        return this.producer;
    }

    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return this.image;
    }

    public void setApproved(boolean approved){
        this.approved = approved;
    }

    public boolean getApproved(){
        return this.approved;
    }

    public void setRateNumber(int number){
        this.rateNum = number;
    }

    public int getRateNumber(){
        return this.rateNum;
    }

    public void setRateAverage(float average){
        this.rateAvg = average;
    }

    public float getRateAverage(){
        return this.rateAvg;
    }

}

