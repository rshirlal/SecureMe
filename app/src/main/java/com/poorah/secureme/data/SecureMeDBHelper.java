package com.poorah.secureme.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rahul on 11/8/17.
 */

public class SecureMeDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERION = 1;

    public static final String DATABASE_NAME = "secure_me.db";

    public SecureMeDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_SECURITY_MASTER = "CREATE TABLE " + SecureMeContract.SecurityMaster.TABLE_NAME + " (" +
                SecureMeContract.SecurityMaster._ID + " INTEGER PRIMARY KEY," +
                SecureMeContract.SecurityMaster.COLUMN_TYPE + " TEXT," +
                SecureMeContract.SecurityMaster.COLUMN_NAME + " TEXT," +
                SecureMeContract.SecurityMaster.COLUMN_DESCRIPTION + " TEXT," +
                SecureMeContract.SecurityMaster.COLUMN_USERNAME + " TEXT," +
                SecureMeContract.SecurityMaster.COLUMN_SECRET + " TEXT," +
                SecureMeContract.SecurityMaster.COLUMN_LAST_CHANGED + " TEXT);";


        final String SQL_CREATE_SECURITY_TYPES = "CREATE TABLE " + SecureMeContract.SecurityTypes.TABLE_NAME + " (" +
                SecureMeContract.SecurityTypes._ID + " INTEGER PRIMARY KEY," +
                SecureMeContract.SecurityTypes.COLUMN_TYPE + " TEXT," +
                SecureMeContract.SecurityTypes.COLUMN_NAME + " TEXT," +
                SecureMeContract.SecurityTypes.COLUMN_LAST_CHANGED + " TEXT);";

        final String SQL_CREATE_SECURITY_SETTINGS = "CREATE TABLE " + SecureMeContract.SecuritySettings.TABLE_NAME + " (" +
                SecureMeContract.SecuritySettings._ID + " INTEGER PRIMARY KEY," +
                SecureMeContract.SecuritySettings.COLUMN_KEY + " TEXT," +
                SecureMeContract.SecuritySettings.COLUMN_VALUE + " TEXT," +
                SecureMeContract.SecurityTypes.COLUMN_LAST_CHANGED + " TEXT);";

        db.execSQL(SQL_CREATE_SECURITY_MASTER);
        db.execSQL(SQL_CREATE_SECURITY_TYPES);
        db.execSQL(SQL_CREATE_SECURITY_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
