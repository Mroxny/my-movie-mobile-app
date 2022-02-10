package com.mroxny.mymovie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricManager;
import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class ImageManager extends AsyncTask<String, Void, String> {

    protected static final String ACTION_DOWNLOAD = "download_image";
    protected static final String ACTION_UPLOAD = "upload_image";

    private static final String URL_PREFIX = "https://mroxny.github.io/MyMovie_assets/img/covers/";


    private FTPClient ftpClient;
    private ImageManager.DataListener listener;
    private String[] fileNames;
    private String fileName;
    private ArrayList<Bitmap> images;
    private Bitmap image;


    public  ImageManager(){
        listener = null;
        fileName = null;
        fileNames = null;
    }

    // Assign the listener implementing events interface that will receive the events
    public void setOnDataListener(ImageManager.DataListener listener) {
        this.listener = listener;
    }

    private boolean connect(String host){
        ftpClient = new FTPClient();

        try {
            ftpClient.connect(host);
            System.out.println("FTPClient connection success");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FTPClient connection failed");
            return false;
        }
    }

    private boolean login(String username, String password){
        boolean login = false;
        try {
            login = ftpClient.login(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(login? "FTPClient login success":"FTPClient login failed");
        return login;
    }

    private Bitmap[] downloadImages(){
        /*System.out.println("Request download");
        try{
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            InputStream file = null;
            Bitmap[] bitmaps = new Bitmap[fileNames.length];
            for(int i = 0; i< fileNames.length; i++){
                file = ftpClient.retrieveFileStream(fileNames[i]);
                if(file != null){

                    Bitmap image = BitmapFactory.decodeStream(file);
                    bitmaps[i] = image;
                }
                else bitmaps[i] = null;

            }
            if(file!= null)file.close();

            return bitmaps;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;

        }
*/
        return null;
    }

    public static ArrayList<Bitmap> downloadImageFromURL(String[] filename) {
        ArrayList<Bitmap> images = new ArrayList<>();
        for(int i = 0; i< filename.length; i++){
            try {
                InputStream is = (InputStream) new URL(URL_PREFIX+filename[i]).getContent();
                images.add(BitmapFactory.decodeStream(is));
            } catch (Exception e) {
                images.add(null);
            }
        }
        return images;
    }

    public static Bitmap downloadImageFromURL(String filename) {

            try {
                InputStream is = (InputStream) new URL(URL_PREFIX+filename).getContent();
                return BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                return null;
            }
    }

    protected void setFileName(String[] names){
        fileNames = names;
    }

    protected void setFileName(String names){
        fileName = names;
    }

    @Override
    protected String doInBackground(String... strings) {
        if(Objects.equals(strings[0], ACTION_DOWNLOAD)){
            System.out.println("Request download");
            if(fileName != null) image = downloadImageFromURL(fileName);
            else images = downloadImageFromURL(fileNames);
            return ACTION_DOWNLOAD;

        }
        else if(Objects.equals(strings[0], ACTION_UPLOAD)){
            System.out.println("Request upload");
            return ACTION_UPLOAD;

        }

        return null;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(fileName != null)listener.onDataLoaded(image);
        else listener.onDataLoaded(images);
    }

    public void sendData (DataListener listener, Bitmap image, ArrayList<Bitmap> images){
        listener.onDataLoaded(image);
        listener.onDataLoaded(images);
    }


    public interface DataListener {
        void onDataLoaded(ArrayList<Bitmap> img);
        void onDataLoaded(Bitmap img);

    }
    public abstract class DataLoader implements DataListener {

        public void onDataLoaded(ArrayList<Bitmap> img) {
        }

        public void onDataLoaded(Bitmap img) {
        }

    }
}
