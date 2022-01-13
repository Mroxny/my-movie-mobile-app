package com.mroxny.mymovie;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class OutputManager extends AsyncTask<String, Void, String> {


    private static final String unicode = "?useUnicode=yes&characterEncoding=UTF-8";
    private static final String STATUS_SUCCESS = "Success";
    private static final String STATUS_NO_RESULTS = "No results";
    private static final String STATUS_ERROR = "Error";

    private static  String url;
    private static  String user;
    private static  String pass;

    private Context context;
    private String[][] resultArr = new String[0][0];
    private DataListener listener;

    public OutputManager(Context context){
        this.context = context;
        this.listener = null;
    }

    // Assign the listener implementing events interface that will receive the events
    public void setOnDataListener(DataListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        decData();
    }

    @Override
    protected String doInBackground(String... params) {
        String res = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            String query = params[0];
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();

            int rows = 0;
            while (rs.next()) {
                rows++;
            }
            rs.first();

            if(rows>=1){
                res = STATUS_SUCCESS;
                resultArr = new String[rows][rsmd.getColumnCount()];

                for(int row=0; row<resultArr.length;row++){
                    for(int col=0; col<resultArr[row].length;col++){
                        resultArr[row][col] = rs.getString(col+1).toString();
                    }
                    rs.next();
                }
            }
            else{
                res = STATUS_NO_RESULTS;
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            res = STATUS_ERROR;
        }
        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        //Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

        if( listener!= null) listener.onDataLoaded(result.equals(STATUS_SUCCESS)?resultArr:null);
    }

    public interface DataListener {
        void onDataLoaded(String[][] data);
    }


    private void decData(){
        DbLogger db = new DbLogger();

        url = db.getData(2);
        user = db.getData(1);
        pass = db.getData(3);
    }
}
