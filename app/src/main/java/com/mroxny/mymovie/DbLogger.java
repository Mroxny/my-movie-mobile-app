package com.mroxny.mymovie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DbLogger {

    private byte[] config = {-114, -4, -110, -111, 28, 34, -7, -122, -23, 100, -127, -96, 23, 51, 43, 47, 62, -86, 117, 72, -8, -79, -33, 87, -11, 66, 25, -88, -54, -5, -8, -80};
    private byte[] data = {-74, -100, -27, -43, 22, 75, -65, 81, -78, -45, 63, 36, -43, -36, -91, -85, -12, -20, 99, -113, 36, -98, -97, 85, -100, -35, 85, -6, -3, 94, 119, 58, -114, -4, -110, -111, 28, 34, -7, -122, -23, 100, -127, -96, 23, 51, 43, 47, 62, -86, 117, 72, -8, -79, -33, 87, -11, 66, 25, -88, -54, -5, -8, -80};
    private byte[] log = {-64, -11, 115, 23, -124, -110, 49, 30, 98, -39, -113, 11, -120, 80, -112, -6};

    private SecretKey generateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        return new SecretKeySpec(password.getBytes(), "AES");
    }


    @SuppressLint("GetInstance")
    private String decryptMsg(byte[] cipherText, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        return decryptString;
    }

    public String getData(int i){
        SecretKey secretKey = null;
        String sData= "";

        try {
            secretKey = generateKey("!@#qxFoT#@!OiUlM");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        if(secretKey!=null){
            byte[] bytes;
            switch (i){
                case 1: bytes = config;
                    break;
                case 2: bytes = data;
                    break;
                case 3: bytes = log;
                    break;
                default: bytes = null;
                    break;
            }
            if (bytes!= null){
                try {
                    sData = decryptMsg(bytes,secretKey);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidParameterSpecException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return sData;
    }
}
