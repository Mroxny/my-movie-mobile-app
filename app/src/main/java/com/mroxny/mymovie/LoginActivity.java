package com.mroxny.mymovie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText UsernameEt, PasswordEt;
    private ProgressBar progressBar;
    private ConnectionLogger connectionLogger;
    private ConstraintLayout constraintLayout;
    private boolean processing;
    private String username,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UsernameEt = findViewById(R.id.login_etUserName);
        PasswordEt = findViewById(R.id.login_etPassword);
        progressBar = findViewById(R.id.login_PB);
        constraintLayout = findViewById(R.id.cl_layout);

        checkAutoLogin();

    }

    public void OnLogin(View view) {
        if(!processing){
            processing = true;

            username = UsernameEt.getText().toString();
            password = PasswordEt.getText().toString();

            PasswordManager pm = new PasswordManager();

            String query = "SELECT Id_uzytkownik, Nazwa " +
                    "FROM uzytkownicy " +
                    "WHERE (LOWER(Email) = LOWER('"+username+"') OR Nazwa = '"+username+"') AND Haslo = '"+ SyntaxSQL.correctStatement(pm.encrypt(password)) +"';";

            setUpConnectionLogger();
            connectionLogger.execute(query);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void checkAutoLogin(){
        if (PreferencesManager.getInt(PreferencesManager.LOGGED_USER_ID,-1,getApplicationContext())!=-1){
            progressBar.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.GONE);
            UsernameEt.setText(PreferencesManager.getString(PreferencesManager.LOGGED_USER_NAME,"",getApplicationContext()));
            PasswordEt.setText(PreferencesManager.getString(PreferencesManager.LOGGED_USER_PASSWORD,"",getApplicationContext()));
            OnLogin(null);
        }
        else{
            progressBar.setVisibility(View.GONE);
            constraintLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpConnectionLogger() {
        connectionLogger = new ConnectionLogger(this);

        connectionLogger.setOnLoginStatusListener(new ConnectionLogger.LoginStatusListener() {
            @Override
            public void onLoginSuccess(int userId) {
                loginSuccess(userId);
            }

            @Override
            public void onLoginFailed(String message) {
                loginFailed(message);
            }
        });
    }

    private void loginSuccess(int userId){
        progressBar.setVisibility(View.GONE);

        PreferencesManager.insertInt(PreferencesManager.LOGGED_USER_ID,userId,getApplicationContext());
        PreferencesManager.insertString(PreferencesManager.LOGGED_USER_NAME, username,getApplicationContext());
        PreferencesManager.insertString(PreferencesManager.LOGGED_USER_PASSWORD, password,getApplicationContext());


        Intent intent = new Intent(this,MoviesActivity.class);
        startActivity(intent);
        processing = false;
        finish();
    }

    private void loginFailed(String message){
        processing = false;
        progressBar.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.VISIBLE);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}