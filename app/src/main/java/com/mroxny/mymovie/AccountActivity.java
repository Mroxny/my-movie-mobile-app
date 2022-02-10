package com.mroxny.mymovie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountActivity extends AppCompatActivity {

    private TextView tvNick;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        tvNick = findViewById(R.id.ac_nickname);

        tvNick.setText(PreferencesManager.getString(PreferencesManager.LOGGED_USER_NAME, "Error", this));
    }
}