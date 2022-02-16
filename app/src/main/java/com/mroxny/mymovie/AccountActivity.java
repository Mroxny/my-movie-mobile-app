package com.mroxny.mymovie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    private TextView tvNick, tvError, tvMoviesCount;
    private ImageView ivProfile;
    private ProgressBar contentProgressBar, profileProgressBar;
    private ConstraintLayout content;

    private int userID;
    private int ratedMovies;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userID = PreferencesManager.getInt(PreferencesManager.LOGGED_USER_ID, -1, this);


        setVariables();
        downloadData();
    }

    private void setVariables() {
        tvNick = findViewById(R.id.aa_nickname);
        tvError = findViewById(R.id.aa_error_sign);
        tvMoviesCount = findViewById(R.id.aa_movies);

        contentProgressBar = findViewById(R.id.aa_progress_content);
        profileProgressBar = findViewById(R.id.aa_progress_profile);

        content = findViewById(R.id.aa_content);

        ivProfile = findViewById(R.id.aa_profile);

        contentProgressBar.setVisibility(View.VISIBLE);
        profileProgressBar.setVisibility(View.VISIBLE);

        ivProfile.setVisibility(View.INVISIBLE);
        content.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);

    }

    private void downloadData(){

        if(userID == -1) {
            contentProgressBar.setVisibility(View.GONE);
            tvError.setVisibility(View.VISIBLE);
        }
        else{
            String query ="SELECT uzytkownicy.Nazwa, uzytkownicy.Email, uzytkownicy.Profil, COUNT(oceny.uzytkownik_Id) FROM `uzytkownicy` \n" +
                    "INNER JOIN oceny on (uzytkownicy.Id_uzytkownik = oceny.uzytkownik_Id)\n" +
                    "WHERE uzytkownicy.Id_uzytkownik = "+ userID +";";
            OutputManager om = new OutputManager(this);
            om.execute(query);
            om.setOnDataListener(new OutputManager.DataListener() {
                @Override
                public void onDataLoaded(String[][] data) {
                    loadData(data[0]);
                }
            });
        }
    }

    private void loadData(String[] data){
        contentProgressBar.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);

        userName = data[0];
        tvNick.setText(userName);
        ratedMovies = Integer.parseInt(data[3]);
        tvMoviesCount.setText(data[3]);

        if(data[2]!= null && data[2].length()>0){
            ImageManager im = new ImageManager();
            im.downloadProfile(data[2]);
            im.setOnDataListener(new ImageManager.DataListener() {
                @Override
                public void onDataLoaded(ArrayList<Bitmap> img) {
                    if(img != null){
                        profileProgressBar.setVisibility(View.GONE);
                        ivProfile.setVisibility(View.VISIBLE);

                        ivProfile.setImageBitmap(img.get(0));
                    }
                }
            });
        }


    }

    private void logOut(){
        PreferencesManager.clearData(PreferencesManager.LOGGED_USER_ID, this);
        PreferencesManager.clearData(PreferencesManager.LOGGED_USER_NAME, this);
        PreferencesManager.clearData(PreferencesManager.LOGGED_USER_PASSWORD, this);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void showAlertDialog(View view){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.tag_logout))
                .setMessage(getResources().getString(R.string.info_log_out))

                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                })

                .setNegativeButton(R.string.button_no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void editProfile(View view){
        Toast.makeText(this, getResources().getString(R.string.info_activity_coming_soon), Toast.LENGTH_SHORT).show();
    }

    public void goToMovieList(View view){
        if(ratedMovies>0){
            String query = "SELECT o.*, \n" +
                    "COUNT(op1.film_Id) 'Liczba ocen', \n" +
                    "IF(AVG(op1.Ocena) IS NULL, 0, AVG((op1.OcenaZdjecia+op1.OcenaFabula+op1.OcenaAktorzy+op1.OcenaAudio)/4)) 'Srednia' \n" +
                    "FROM filmy o \n" +
                    "INNER JOIN oceny op1 on o.Id_film = op1.film_Id \n" +
                    "INNER JOIN oceny op2 on o.Id_film = op2.film_Id \n" +
                    "WHERE o.Zatwierdzony = 1 \n" +
                    "AND op2.uzytkownik_Id = "+userID+"\n" +
                    "GROUP BY o.Id_film \n" +
                    "ORDER BY o.Tytul ASC;";

            Intent intent = new Intent(this, MoviesActivity.class);
            intent.putExtra(MoviesActivity.CUSTOM_QUERY_KEY,query);
            intent.putExtra(MoviesActivity.CUSTOM_TAG_KEY,userName);

            startActivity(intent);
        }
        else Toast.makeText(this, getResources().getString(R.string.info_no_rated_movies), Toast.LENGTH_SHORT).show();

    }

    public void goBack(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}