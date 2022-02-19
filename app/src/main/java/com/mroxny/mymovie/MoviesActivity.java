package com.mroxny.mymovie;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


import java.util.ArrayList;
import java.util.Collections;

public class MoviesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String CUSTOM_QUERY_KEY = "CustomQuery";
    public static final String CUSTOM_TAG_KEY = "CustomQueryTag";


    private ArrayList<Movie> movies;
    private ArrayList<Bitmap> images;
    private MovieListAdapter mAdapter;
    private SwipeRefreshLayout swipeLayout;
    private SideMenuManager sideMenuManager;
    private String customQuery;
    private String customTag;

    private boolean asc = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        images = null;
        customQuery = null;
        customTag = null;

        String tmpQ = getIntent().getStringExtra(CUSTOM_QUERY_KEY);
        String tmpT = getIntent().getStringExtra(CUSTOM_TAG_KEY);

        if(tmpQ != null){
            customQuery = tmpQ;
            if(tmpT != null) customTag = tmpT;
        }

        setSideMenu();
        setUpSwipeRefresh();
    }




    private void setSideMenu(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Button toggle = findViewById(R.id.nav_toggle);

        sideMenuManager = new SideMenuManager(this, drawer, navigationView, toggle);
        sideMenuManager.setSideMenu();
    }

    private void createMovieList(){
        String defQuery = "SELECT m.*, \n" +
                "COUNT(mo1.film_Id) 'Liczba ocen', \n" +
                "IF(AVG(mo1.Ocena) IS NULL, 0, AVG((mo1.OcenaZdjecia+mo1.OcenaFabula+mo1.OcenaAktorzy+mo1.OcenaAudio)/4)) 'Srednia' \n" +
                "FROM filmy m \n" +
                "LEFT JOIN oceny mo1 on m.Id_film = mo1.film_Id \n" +
                "WHERE m.Zatwierdzony = 1 \n" +
                "GROUP BY m.Id_film \n";

        String finalQuery = (customQuery!= null? customQuery: defQuery) + "ORDER BY m.Tytul" + (asc?" ASC":" DESC")+";";

        createMovieList(finalQuery);
    }


    private void createMovieList(String query) {
        movies = new ArrayList<>();
        swipeLayout.setRefreshing(true);


        OutputManager outputManager = new OutputManager(this);
        outputManager.execute(query);
        outputManager.setOnDataListener(data -> {

            for (String[] datum : data) {
                addMovieToList(datum);
            }
            setSortingButton();
            setSearchBar();
            buildRecyclerView();
            if (!checkImages()) addImagesToMovies();
            else mAdapter.setImages(images);
            swipeLayout.setRefreshing(false);
        });


    }

    private void setSortingButton(){
        Button sortButton = findViewById(R.id.toggle_sort);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asc = !asc;
                sortButton.setScaleY(sortButton.getScaleY()*-1);
                Collections.reverse(movies);
                mAdapter.filterList(movies);
                if(images != null){
                    Collections.reverse(images);
                    mAdapter.setImages(images);
                }

            }
        });
    }

    private void setSearchBar(){

        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.setHint(getResources().getString(R.string.hint_search));
        if (customTag != null)searchBar.setHint(searchBar.getHint() + " ("+customTag+")");
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

    }

    private void addImagesToMovies(){
        String[] fileNames = new String[movies.size()];
        ImageManager im = new ImageManager();

        for(int i = 0; i < movies.size(); i++) {

            if (movies.get(i).getImage().length() > 0) fileNames[i] = movies.get(i).getImage();
            else fileNames[i] = "";
        }


            im.downloadCover(fileNames);
            im.setOnDataListener(new ImageManager.DataListener() {
                @Override
                public void onDataLoaded(ArrayList<Bitmap> img) {
                    images = img;
                    if(!asc) Collections.reverse(images);
                    mAdapter.setImages(images);
                }

            });

    }

    private boolean checkImages(){
        if (images == null) return false;
        else return images.size() == movies.size();
    }

    private void addMovieToList(String[] data){
        int id, year, rateNum;
        float rateAvg;
        String title, orgTitle, dir, prod, img;
        boolean approved;

        id = Integer.parseInt(data[0]);
        title = data[1];
        orgTitle = data[2];
        year = Integer.parseInt(data[3]);
        dir = data[4];
        prod = data[5];
        img = data[6];
        approved = data[7].equals("1");
        rateNum = Integer.parseInt(data[8]);
        rateAvg = Float.parseFloat(data[9]);


        Movie movie = new Movie(
                id,
                title,
                orgTitle,
                year,
                dir,
                prod,
                img,
                approved,
                rateNum,
                rateAvg
        );

        movies.add(movie);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void buildRecyclerView() {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MovieListAdapter(movies);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        View tools = findViewById(R.id.tools);
        @SuppressLint("Recycle") ObjectAnimator hide = ObjectAnimator.ofFloat(tools, "translationY", -200f);
        hide.setDuration(200);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isHidden = false;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!isHidden && dy > 0){
                    isHidden = true;
                    hide.start();
                }
                else if( isHidden && dy <= 0 ){
                    isHidden = false;
                    hide.reverse();
                }
            }
        });
        mAdapter.setOnItemClickListener(position -> {

            try{
                Movie movie = movies.get(position);
                Intent intent = new Intent(MoviesActivity.this, SingleMovieActivity.class);
                intent.putExtra("Movie", movie);
                startActivity(intent);

            }
            catch (Exception e){
                e.printStackTrace();
            }

        });
    }

    private void filter(String text) {
        ArrayList<Movie> filteredList = new ArrayList<>();
        ArrayList<Bitmap> filteredImages = new ArrayList<>();


        for (int i=0; i<movies.size(); i++) {
            if (movies.get(i).getTitle().toLowerCase().contains(text.toLowerCase()) ||movies.get(i).getOriginalTitle().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(movies.get(i));
                filteredImages.add(images.get(i));
            }
        }

        mAdapter.filterList(filteredList);
        mAdapter.setImages(filteredImages);
    }

    private void setUpSwipeRefresh(){
        swipeLayout = findViewById(R.id.movies_swipe);

        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.black);
        swipeLayout.post(this::createMovieList);
    }

    @Override
    public void onRefresh() {
        createMovieList();
    }

    @Override
    public void onBackPressed() {
        if(!sideMenuManager.closeSideMenu())super.onBackPressed();
    }
}