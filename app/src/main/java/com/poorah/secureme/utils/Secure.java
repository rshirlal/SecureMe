package com.poorah.secureme.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.poorah.secureme.data.Preferences;

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

}
