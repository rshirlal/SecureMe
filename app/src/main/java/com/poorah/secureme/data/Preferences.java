package com.poorah.secureme.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by rahul on 12/8/17.
 */

public class Preferences {

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private static final String SHARED_PREF_NAME = "secure_me_prefs";
    private static final String CRYPTO_KEY = "crypto_key";
    private static final String SECURE_PIN = "secure_pin";


    public Preferences(Context context){
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
    }

    public void setKey(String key){
        mSharedPreferences.edit().putString(CRYPTO_KEY,key).apply();
    }

    public String getKey(){
        return mSharedPreferences.getString(CRYPTO_KEY,"secure_me");
    }

    public void setPIN(String pinNo){
        if(TextUtils.isDigitsOnly(pinNo)){
            mSharedPreferences.edit().putString(SECURE_PIN,pinNo).apply();
        }
    }

    public String getPIN(){
        return mSharedPreferences.getString(SECURE_PIN,"0000");
    }
}
