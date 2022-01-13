package com.mroxny.mymovie;

import org.apache.commons.net.ftp.FTPClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageManager {

    public void downloadImage(){
        String filename = "folder.jpg";

        FTPClient client = new FTPClient();
        boolean login = false;

        try {
            client.connect("serwer116297.lh.pl");
            login = client.login("serwer116297", "Feministka0000");

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (login) {
            System.out.println("Login success...");

            // Download file from FTP server.
        }
        else System.out.println("Login failed...");


    }

}
