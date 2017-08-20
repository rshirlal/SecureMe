package com.poorah.secureme.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.poorah.secureme.data.Preferences;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by rahul on 12/8/17.
 */

public class Secure {

    private SecretKey mSecretKey;
    private final String TAG = getClass().getSimpleName();
    private final String ENCRYPTION_ALGO = "DES";

    public Secure(Context context){
        Preferences preferences = new Preferences(context);
        String key = preferences.getKey();

        try{
            DESKeySpec keySpec = new DESKeySpec(key.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGO);
            mSecretKey = keyFactory.generateSecret(keySpec);
        }catch (Exception e){
            Log.e(TAG,e.getLocalizedMessage());
        }
    }

    public String encrypt(String message)
    {
        try{
            Cipher cipher = null;
            cipher = Cipher.getInstance(ENCRYPTION_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, mSecretKey);
            byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
            return new String(Base64.encode(cipherText,Base64.DEFAULT),"UTF-8");
        }catch (Exception e){
            Log.e(TAG,e.getLocalizedMessage());
            return message;
        }
    }

    public String decrypt(String cipherText)
    {
        try {
            Cipher cipher = null;
            cipher = Cipher.getInstance(ENCRYPTION_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, mSecretKey);
            byte[] cipherByte = Base64.decode(cipherText,Base64.DEFAULT);
            String decryptString = new String(cipher.doFinal(cipherByte), "UTF-8");
            return decryptString;
        }catch (Exception e){
            Log.e(TAG,e.getLocalizedMessage());
            return cipherText;
        }

    }

    public String generateHash(String text){
        MessageDigest mdSha1 = null;
        try
        {
            mdSha1 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e1) {
            Log.e("myapp", "Error initializing SHA1 message digest");
        }
        mdSha1.update(text.getBytes());

        byte[] data = mdSha1.digest();
        try {
            return convertToHex(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private String convertToHex(byte[] data) throws java.io.IOException
    {


        StringBuffer sb = new StringBuffer();
        String hex=null;

        hex=Base64.encodeToString(data, 0, data.length, Base64.DEFAULT);

        sb.append(hex);

        return sb.toString();
    }

}
