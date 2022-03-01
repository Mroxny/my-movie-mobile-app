package com.mroxny.mymovie;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class InputManager extends AsyncTask<String, Void, String> {
    private static final String unicode = "?useUnicode=yes&characterEncoding=UTF-8";

    private static String url;
    private static String user;
    private static String pass;

    private InputManager.DataListener listener;


    Context context;

    public InputManager(Context context){
        this.context=context;
        this.listener = null;
    }

    public void setOnDataListener(InputManager.DataListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        decData();
    }

    @Override
    protected String doInBackground(String... params) {
        String res;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url+unicode, user, pass);

            String query = params[0];
            Statement st = con.createStatement();
            int rs = st.executeUpdate(query);
            res =Integer.toString(rs);

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            res = e.toString();
        }
        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("Input result: "+result);
        if(listener != null) listener.onDataUploaded(result);
    }

    public interface DataListener {
        void onDataUploaded(String mes);
    }

    private void decData(){
        DbLogger db = new DbLogger();

        url = db.getData(2);
        user = db.getData(1);
        pass = db.getData(3);
    }
}
