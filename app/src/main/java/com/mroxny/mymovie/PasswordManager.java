package com.mroxny.mymovie;

import android.annotation.SuppressLint;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class PasswordManager {

    public static final String KEY = "531zEvYmUu!OlI79";

    public void runTest(){
        try
        {
            String text = "String";
            String key = generateKey(text); // 128 bit key
            int keyLenght = key.getBytes(StandardCharsets.UTF_16BE).length *8;
            System.out.println("Hasło:" + text + ", key: " + key + ", key lenght: " + keyLenght);

            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_16BE), "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");


            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            System.out.println("Zaszyfrowano:" + new String(encrypted));


            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(encrypted));
            System.out.println("Odszyfrowano:" + decrypted);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @SuppressLint("GetInstance")
    public String encrypt(String text){
        String key = generateKey(text);
        Key aesKey;
        Cipher cipher = null;
        try {
            aesKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_16BE), "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            return new String(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public String decrypt(byte[] encryptedText, String key){
        try{
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(encryptedText));

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String generateKey(String s){
        String key;

        if(s.length()<16){
            StringBuilder sBuilder = new StringBuilder(s);
            while (sBuilder.length()!=16) sBuilder.append("!");
            s = sBuilder.toString();
        }

        else if(s.length()>16){
            s = s.substring(0,16);
        }

        key = s;

        return key;
    }

}
