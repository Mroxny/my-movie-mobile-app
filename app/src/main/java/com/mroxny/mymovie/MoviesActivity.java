package com.mroxny.mymovie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


import java.util.ArrayList;

public class MoviesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Movie> movies;
    private ArrayList<Bitmap> images;
    private MovieListAdapter mAdapter;
    private SwipeRefreshLayout swipeLayout;
    private SideMenuManager sideMenuManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        images = null;

        setSideMenu();
        setUpSwipeRefresh();
    }

    private void setAddMovieButton(){
        FloatingActionButton addMovieButton = findViewById(R.id.nav_toggle);
        addMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMovieDialog addMovie = new AddMovieDialog(MoviesActivity.this);
                addMovie.show(getSupportFragmentManager(), "add movie dialog");
                addMovie.setOnAddMovieListener(new AddMovieDialog.AddMovieDialogListener() {
                    @Override
                    public void applyTexts(String title, String orgTitle, int year, String dir, String prod) {
                        addMovie( title,  orgTitle,  year,  dir,  prod);
                    }
                });
            }
        });
    }

    private void addMovie(String title, String orgTitle, int year, String dir, String prod){

        String query = "INSERT INTO `filmy` ( `Tytul`, `OrgTytul`, `RokProd`, `Rezyser`, `Wytwornia`, `Img`, `Zatwierdzony`) " +
                "VALUES ('"+SyntaxSQL.correctStatement(title)+"', '"+SyntaxSQL.correctStatement(orgTitle)+"', "+year+", '"+SyntaxSQL.correctStatement(dir)+"', '"+SyntaxSQL.correctStatement(prod)+"', '', 1);";

        InputManager inputManager = new InputManager(this);
        inputManager.execute(query);
        onRefresh();


    }

    private void setSideMenu(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        FloatingActionButton toggle = findViewById(R.id.nav_toggle);

        sideMenuManager = new SideMenuManager(this,drawer,navigationView,toggle);
        sideMenuManager.setSideMenu();
    }

    private void createMovieList(){
        String query = "SELECT filmy.*, " +
                "COUNT(oceny.film_Id) 'Liczba ocen', " +
                "IF(AVG(Ocena) IS NULL, 0, AVG((OcenaZdjecia+OcenaFabula+OcenaAktorzy+OcenaAudio)/4)) 'Srednia' " +
                "FROM `filmy` LEFT JOIN oceny ON (oceny.film_Id = filmy.Id_film) " +
                "WHERE Zatwierdzony = 1 " +
                "GROUP BY Id_film " +
                "ORDER BY filmy.Tytul ASC;";

        createMovieList(query);
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

            buildRecyclerView();
            if (!checkImages()) addImagesToMovies();
            else mAdapter.setImages(images);
            swipeLayout.setRefreshing(false);
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    private void addImagesToMovies(){
        String[] fileNames = new String[movies.size()];
        ImageManager im = new ImageManager();

        for(int i = 0; i < movies.size(); i++) {

            if (movies.get(i).getImage().length() > 0) fileNames[i] = movies.get(i).getImage();
            else fileNames[i] = "";
        }

            im.setFileName(fileNames);
            im.execute(ImageManager.ACTION_DOWNLOAD);
            im.setOnDataListener(new ImageManager.DataListener() {
                @Override
                public void onDataLoaded(ArrayList<Bitmap> img) {
                    images = img;
                    mAdapter.setImages(images);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onDataLoaded(Bitmap img) {

                }
            });

    }

    private boolean checkImages(){
        if (images == null) return false;
        else if(images.size() != movies.size()) return false;
        else return true;
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
        Drawable noImageIcon = getDrawable(R.drawable.ic_noimage);
        mAdapter = new MovieListAdapter(movies, noImageIcon);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(position -> {

            try{
                Movie movie = movies.get(position);
                Intent intent = new Intent(MoviesActivity.this, SingleMovieActivity.class);
                intent.putExtra("Movie", movie);
                startActivity(intent);
                finish();

            }
            catch (Exception e){
                e.printStackTrace();
            }

        });
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