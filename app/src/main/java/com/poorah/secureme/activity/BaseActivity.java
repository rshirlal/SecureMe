package com.poorah.secureme.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.poorah.secureme.data.Preferences;

/**
 * Created by rahul on 20/8/17.
 */

public class BaseActivity extends AppCompatActivity {
    private Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = new Preferences(this);
    }

    protected Preferences getPreferences(){
        return mPreferences;
    }

}
