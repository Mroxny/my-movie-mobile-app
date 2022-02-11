package com.mroxny.mymovie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddMovieActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextOrgTitle;
    private EditText editTextYear;
    private EditText editTextDir;
    private EditText editTextProd;

    private TextView tvError;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        setVariables();
    }

    public void submit(View view){

        if(editTextTitle.length()<=0 || editTextDir.length()<=0){
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(getResources().getString(R.string.error_fill_fields));
        }
        else if(editTextYear.length()<4){
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(getResources().getString(R.string.error_wrong_year));
        }
        else{
            tvError.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);


            String title = editTextTitle.getText().toString();
            String orgTitle = editTextOrgTitle.getText().toString();
            orgTitle = title.equals(orgTitle)?"":orgTitle;
            int year = Integer.parseInt(editTextYear.getText().toString());
            String dir = editTextDir.getText().toString();
            String prod = editTextProd.getText().toString();

            addMovie(title, orgTitle, year, dir, prod);
        }

    }

    public void goBack(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setVariables(){
        editTextTitle = findViewById(R.id.am_title);
        editTextOrgTitle = findViewById(R.id.am_orgTitle);
        editTextYear = findViewById(R.id.am_year);
        editTextDir = findViewById(R.id.am_dir);
        editTextProd = findViewById(R.id.am_prod);
        tvError = findViewById(R.id.am_error);
        progressBar = findViewById(R.id.am_progress);

    }

    private void addMovie(String title, String orgTitle, int year, String dir, String prod){

        String query = "INSERT INTO `filmy` ( `Tytul`, `OrgTytul`, `RokProd`, `Rezyser`, `Wytwornia`, `Img`, `Zatwierdzony`) " +
                "VALUES ('"+SyntaxSQL.correctStatement(title)+"', '"+SyntaxSQL.correctStatement(orgTitle)+"', "+year+", '"+SyntaxSQL.correctStatement(dir)+"', '"+SyntaxSQL.correctStatement(prod)+"', '', 1);";

        InputManager inputManager = new InputManager(this);
        inputManager.execute(query);
        inputManager.setOnDataListener(new InputManager.DataListener() {
            @Override
            public void onDataUploaded(String mes) {
                progressBar.setVisibility(View.VISIBLE);

                if(Integer.parseInt(mes)==1){
                    Toast.makeText(AddMovieActivity.this,getResources().getString(R.string.info_added_movie),Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AddMovieActivity.this, MoviesActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(AddMovieActivity.this,getResources().getString(R.string.error_unknown),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}