package com.mroxny.mymovie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mroxny.mymovie.ConnectionLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private InputManager inputManager;
    private OutputManager outputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);

    }

    public void OnInsert(View v){

        EditText et = findViewById(R.id.main_user);
        String name = et.getText().toString();
        String query = "INSERT INTO `uzytkownicy` (`Nazwa`, `Email`, `Haslo`) VALUES ('"+name+"', '"+name+"@email.pl', '123');";

        setUpInputManager();
        inputManager.execute(query);

        query = "SELECT * FROM uzytkownicy;";

        setUpOutputManager();
        outputManager.execute(query);

    }

    private void setUpInputManager(){
        inputManager = new InputManager(this);
    }

    private void setUpOutputManager(){
        outputManager = new OutputManager(this);

        outputManager.setOnDataListener(new OutputManager.DataListener() {
            @Override
            public void onDataLoaded(String[][] data) {
                String res="";
                if(data[0].length>0){
                    for(int row=0; row<data.length;row++){
                        for(int col=0; col<data[row].length;col++){
                            res += data[row][col] + ", ";
                        }
                        res+="\n";
                    }
                }
                else res="brak wyników";
                textView.setText(res);
            }
        });

    }




}