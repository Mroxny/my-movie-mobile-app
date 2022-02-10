package com.mroxny.mymovie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {

    private Movie movie;
    private TextView tvTitle, tvOrgTitle, tvYear, tvDirector, tvProducer, tvAvg, tvRates, tvAvgRate;
    private ImageView ivImage;
    private final RatingBar[] ratings = new RatingBar[4];
    private boolean ratedBefore;
    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;
    private int userId;
    private String[][] data;
    private SwipeRefreshLayout swipeLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        setVariables();
        downloadData();
        setMovieInfo();
        setUpStars();
        setUpSwipeRefresh();


    }
    

    public void onBack (View v){
        saveChanges();
        Intent intent = new Intent(this, MoviesActivity.class);
        startActivity(intent);
        finish();
    }

    public void viewRates(View v){
        if(movie.getRateNumber()>0){
            saveChanges();
            Intent intent = new Intent(this, RateListActivity.class);
            intent.putExtra("Movie",movie);
            intent.putExtra("ActivityId",1);
            startActivity(intent);
            finish();
        }
        else Toast.makeText(this,getResources().getString(R.string.info_no_rates_yet),Toast.LENGTH_SHORT).show();
    }

    private void downloadData(){

        String query = "SELECT OcenaZdjecia, OcenaFabula, OcenaAktorzy, OcenaAudio " +
                "FROM `oceny` " +
                "WHERE film_Id = "+movie.getId()+" AND uzytkownik_Id = "+userId+" " +
                "LIMIT 1;";
        OutputManager outputManager = new OutputManager(this);
        outputManager.execute(query);
        outputManager.setOnDataListener(data -> {
            ratedBefore = data!=null;
            SingleMovieActivity.this.data = data;
            loadStars();
        });
    }

    private void saveChanges(){
        if(canSave()){
            String query;
            if(ratedBefore) query = "UPDATE oceny " +
                    "SET OcenaZdjecia = "+(int)ratings[0].getRating()+", " +
                    "OcenaFabula = "+(int)ratings[1].getRating()+", " +
                    "OcenaAktorzy = "+(int)ratings[2].getRating()+", " +
                    "OcenaAudio = "+(int)ratings[3].getRating()+" " +
                    "WHERE film_Id = "+movie.getId()+" AND uzytkownik_Id = "+userId+";";

            else{
                query= "INSERT INTO `oceny`" +
                        "(`film_Id`, `uzytkownik_Id`, `OcenaZdjecia`, `OcenaFabula`, `OcenaAktorzy`, `OcenaAudio`, `Ocena`)" +
                        "VALUES ("+ movie.getId()+", "+ userId+", "+
                        (int)ratings[0].getRating()+", "+
                        (int)ratings[1].getRating()+", "+
                        (int)ratings[2].getRating()+", "+
                        (int)ratings[3].getRating()+", 0);";
                ratedBefore = true;
            }

            InputManager inputManager = new InputManager(this);
            inputManager.execute(query);
        }
    }

    private boolean canSave(){
        if(data!=null){
            boolean ratedNow = false;
            int index = 0;
            while(!ratedNow && index<ratings.length){
                ratedNow = !((int) ratings[index].getRating() == Integer.parseInt(data[0][index]));
                index++;
            }
            return ratedNow && isEverythingRated();
        }
        else return isEverythingRated();
    }

    private void setVariables(){
        movie = (Movie) getIntent().getSerializableExtra("Movie");
        tvTitle = findViewById(R.id.sm_title);
        tvOrgTitle = findViewById(R.id.sm_orgTitle);
        tvYear = findViewById(R.id.sm_year);
        tvDirector = findViewById(R.id.sm_dir);
        tvProducer = findViewById(R.id.sm_prod);
        tvAvg = findViewById(R.id.sm_avg);
        tvRates = findViewById(R.id.sm_rates);
        tvAvgRate = findViewById(R.id.sm_avgRate);
        tvAvgRate.setVisibility(View.GONE);

        ivImage = findViewById(R.id.sm_img);

        progressBar = findViewById(R.id.sm_progress);
        relativeLayout = findViewById(R.id.sm_relativeLayout);
        relativeLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        userId = PreferencesManager.getInt(PreferencesManager.LOGGED_USER_ID, -1,getApplicationContext());

        ratings[0] = findViewById(R.id.sm_ratePictures);
        ratings[1] = findViewById(R.id.sm_ratePlot);
        ratings[2] = findViewById(R.id.sm_rateCast);
        ratings[3] = findViewById(R.id.sm_rateAudio);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setMovieInfo(){
        tvTitle.setText(movie.getTitle());

        if(movie.getOriginalTitle().length()>0) tvOrgTitle.setText('"'+movie.getOriginalTitle()+'"');
        else tvOrgTitle.setVisibility(View.GONE);

        tvYear.setText(Integer.toString(movie.getYear()));
        tvDirector.setText(movie.getDirector());

        if(movie.getProducer().length()>0) tvProducer.setText(movie.getProducer());
        else tvProducer.setText("???");

        tvAvg.setText(String.format("%.2f",movie.getRateAverage()));
        tvRates.setText(Integer.toString(movie.getRateNumber()));


        ImageManager im = new ImageManager();
        im.setFileName(movie.getImage());
        im.execute(ImageManager.ACTION_DOWNLOAD);
        im.setOnDataListener(new ImageManager.DataListener() {
            @Override
            public void onDataLoaded(ArrayList<Bitmap> img) {

            }

            @Override
            public void onDataLoaded(Bitmap img) {
                if(img != null) ivImage.setImageBitmap(img);
            }
        });
    }

    private void setUpStars(){
        for(RatingBar e: ratings){
            e.setOnRatingBarChangeListener((ratingBar, v, b) -> setAverageRate());
        }
    }

    private void loadStars(){
        relativeLayout.setVisibility(View.VISIBLE);
        tvAvgRate.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);

        if(ratedBefore){
            ratings[0].setRating(Integer.parseInt(data[0][0]));
            ratings[1].setRating(Integer.parseInt(data[0][1]));
            ratings[2].setRating(Integer.parseInt(data[0][2]));
            ratings[3].setRating(Integer.parseInt(data[0][3]));
        }

        setAverageRate();

    }

    private boolean isEverythingRated(){
        boolean isRated=false;
        int index = 0;
        while(!isRated && index<ratings.length){
            isRated = (int)ratings[index].getRating()<1;
            index++;
        }
        return !isRated;
    }


    @SuppressLint("SetTextI18n")
    private void setAverageRate(){

        if(isEverythingRated()){
            float avg = 0;
            for(RatingBar e:ratings) avg += e.getRating();
            tvAvgRate.setText(getResources().getString(R.string.info_average_rate1)+": "+avg/4);
        }
        else tvAvgRate.setText(getResources().getString(R.string.info_average_rate2));

    }

    private void setUpSwipeRefresh(){
        swipeLayout = findViewById(R.id.movie_swipe);

        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.black);
    }

    private void makeMovie(String[][] data){
        int id, year, rateNum;
        float rateAvg;
        String title, orgTitle, dir, prod, img;
        boolean approved;

        id = Integer.parseInt(data[0][0]);
        title = data[0][1];
        orgTitle = data[0][2];
        year = Integer.parseInt(data[0][3]);
        dir = data[0][4];
        prod = data[0][5];
        img = data[0][6];
        approved = data[0][7].equals("1");
        rateNum = Integer.parseInt(data[0][8]);
        rateAvg = Float.parseFloat(data[0][9]);


        movie = new Movie(
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

        setMovieInfo();

    }

    @Override
    public void onRefresh() {
        refreshMovieInfo();
    }

    private void refreshMovieInfo() {
        saveChanges();
        downloadData();
        String query = "SELECT filmy.*, " +
                "COUNT(oceny.film_Id) 'Liczba ocen', " +
                "IF(AVG(Ocena) IS NULL, 0, AVG((OcenaZdjecia+OcenaFabula+OcenaAktorzy+OcenaAudio)/4)) 'Srednia' " +
                "FROM `filmy` LEFT JOIN oceny ON (oceny.film_Id = filmy.Id_film) " +
                "WHERE filmy.Id_film = "+movie.getId()+" ;";

        OutputManager om = new OutputManager(this);
        om.execute(query);
        om.setOnDataListener(new OutputManager.DataListener() {
            @Override
            public void onDataLoaded(String[][] data) {
                swipeLayout.setRefreshing(false);
                if(data!= null) makeMovie(data);
            }
        });

    }

    @Override
    public void onBackPressed() {
        onBack(null);
    }
}