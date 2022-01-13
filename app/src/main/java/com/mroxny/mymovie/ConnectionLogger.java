package com.mroxny.mymovie;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mroxny.mymovie.MainActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class ConnectionLogger extends AsyncTask<String, Void, String> {

    private static final String unicode = "?useUnicode=yes&characterEncoding=UTF-8";
    private static  String url;
    private static  String user;
    private static  String pass;
    private static final String STATUS_SUCCESS = "Success";
    private static final String STATUS_ERROR = "Error";
    private static final String STATUS_INCORRECT = "Wrong Email or Password";

    private Context context;
    private String res = "";
    private int userId;
    private LoginStatusListener listener;

    public ConnectionLogger(Context context){
        this.context=context;

        this.listener = null;
    }

    // Assign the listener implementing events interface that will receive the events
    public void setOnLoginStatusListener(LoginStatusListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        decData();
    }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);

                String query = params[0];
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();

                int resIndex = 0;
                while (rs.next()) {
                    resIndex++;
                }

                if(resIndex==1){
                    res = STATUS_SUCCESS;
                    rs.first();
                    userId = Integer.parseInt(rs.getString(1).toString());
                }
                else if(resIndex>1) res = STATUS_ERROR;
                else res = STATUS_INCORRECT;

                con.close();
            } catch (Exception e) {
                e.printStackTrace();
                res = STATUS_ERROR;
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            if( listener!= null){
                switch (res){
                    case STATUS_SUCCESS:
                        listener.onLoginSuccess(userId);
                        break;

                    case STATUS_INCORRECT:
                        listener.onLoginFailed(STATUS_INCORRECT);
                        break;

                    case STATUS_ERROR:
                        listener.onLoginFailed(STATUS_ERROR);
                        break;
                }
            }
        }

    public interface LoginStatusListener {
        void onLoginSuccess(int userId);
        void onLoginFailed(String message);
    }

    private void decData(){
        DbLogger db = new DbLogger();

        url = db.getData(2);
        user = db.getData(1);
        pass = db.getData(3);
    }
}

