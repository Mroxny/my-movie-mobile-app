package com.mroxny.mymovie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MoviesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Movie> movies;
    private MovieListAdapter mAdapter;
    private SwipeRefreshLayout swipeLayout;
    private FloatingActionButton addMovieButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);


        setAddMovieButton();
        setUpSwipeRefresh();

        /*ImageManager im = new ImageManager();
        im.downloadImage();*/

    }

    private void setAddMovieButton(){
        addMovieButton = findViewById(R.id.addMovie);
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

            for(int row = 0; row< data.length; row++){
                addMovieToList(data,row);
            }

            buildRecyclerView();
            swipeLayout.setRefreshing(false);
        });


    }

    private void addMovieToList(String[][] data, int row){
        int id, year, rateNum;
        float rateAvg;
        String title, orgTitle, dir, prod, img;
        boolean approved;

        id = Integer.parseInt(data[row][0]);
        title = data[row][1];
        orgTitle = data[row][2];
        year = Integer.parseInt(data[row][3]);
        dir = data[row][4];
        prod = data[row][5];
        img = data[row][6];
        approved = data[row][7].equals("1");
        rateNum = Integer.parseInt(data[row][8]);
        rateAvg = Float.parseFloat(data[row][9]);


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

    public void buildRecyclerView() {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MovieListAdapter(movies);

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
}