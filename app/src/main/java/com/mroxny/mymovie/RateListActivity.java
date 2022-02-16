package com.mroxny.mymovie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class RateListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Movie movie;
    private int activityId;
    private ArrayList<Rate> rates;
    private ArrayList<Bitmap> images;
    private RecyclerView mRecyclerView;
    private RateListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_list);

        movie = (Movie) getIntent().getSerializableExtra("Movie");
        activityId = getIntent().getIntExtra("ActivityId",-1);      // 0 = MoviesActivity // 1 = SingleMovieActivity

        setUpSwipeRefresh();

    }
    private void setUpSwipeRefresh(){
        swipeLayout = findViewById(R.id.rl_swipe);

        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.black);
        swipeLayout.post(this::createRateList);
    }

    @Override
    public void onRefresh() {
        createRateList();
    }

    private void createRateList(){
        if(movie!=null){
            swipeLayout.setRefreshing(true);
            rates = new ArrayList<>();


            String query = "SELECT oceny.Id_ocena, filmy.Tytul, uzytkownicy.Id_uzytkownik, uzytkownicy.Nazwa, uzytkownicy.Profil ,OcenaZdjecia, OcenaFabula, OcenaAktorzy, OcenaAudio, Ocena " +
                    "FROM oceny " +
                    "INNER JOIN filmy ON (filmy.Id_film = oceny.film_Id) " +
                    "INNER JOIN uzytkownicy ON (uzytkownicy.Id_uzytkownik = oceny.uzytkownik_Id) " +
                    "WHERE filmy.Id_film = "+movie.getId()+"";

            OutputManager outputManager = new OutputManager(this);
            outputManager.execute(query);
            outputManager.setOnDataListener(new OutputManager.DataListener() {
                @Override
                public void onDataLoaded(String[][] data) {

                    for(int row = 0; row< data.length; row++){
                        addProductToList(data, row);
                    }
                    if (!checkImages()) addUserProfiles();
                    else mAdapter.setImages(images);
                    buildRecyclerView();
                    swipeLayout.setRefreshing(false);
                }
            });

        }
    }


    private void addUserProfiles(){
        String[] fileNames = new String[rates.size()];
        ImageManager im = new ImageManager();

        for(int i = 0; i < rates.size(); i++) {

            if (rates.get(i).getUserProfile().length() > 0) fileNames[i] = rates.get(i).getUserProfile();
            else fileNames[i] = "";
        }

        im.downloadProfile(fileNames);
        im.setOnDataListener(new ImageManager.DataListener() {
            @Override
            public void onDataLoaded(ArrayList<Bitmap> img) {
                images = img;
                mAdapter.setImages(images);
            }
        });

    }

    private boolean checkImages(){
        if (images == null) return false;
        else return images.size() == rates.size();
    }

    private void addProductToList(String[][] data, int row) {
        int id, userId, ratePictures, ratePlot, rateCast, rateAudio, rate;
        String movieTitle, userName, userProfile;

        id = Integer.parseInt(data[row][0]);
        movieTitle = data[row][1];
        userId = Integer.parseInt(data[row][2]);
        userName = data[row][3];
        userProfile = data[row][4];
        ratePictures = Integer.parseInt(data[row][5]);
        ratePlot = Integer.parseInt(data[row][6]);
        rateCast = Integer.parseInt(data[row][7]);
        rateAudio = Integer.parseInt(data[row][8]);
        rate = Integer.parseInt(data[row][9]);

        Rate oRate = new Rate(id, movieTitle, userId, userName, userProfile, ratePictures, ratePlot, rateCast, rateAudio, rate);
        rates.add(oRate);
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RateListAdapter(rates);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RateListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(RateListActivity.this, SingleRateActivity.class);
                intent.putExtra("Rate",rates.get(position));
                startActivity(intent);
            }
        });


    }

    public void onBack(View v){
        Intent intent;
        switch (activityId){
            case 0:
                intent= new Intent(this,MoviesActivity.class);
            break;
            case 1: default:
                intent= new Intent(this,SingleMovieActivity.class);
                intent.putExtra("Movie", movie);
                break;
        }
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        //onBack(null);   <- When calling this activity from MovieList
        super.onBackPressed();
    }
}