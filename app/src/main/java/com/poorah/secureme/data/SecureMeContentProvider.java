package com.poorah.secureme.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by rahul on 11/8/17.
 */

public class SecureMeContentProvider extends ContentProvider {

    private SecureMeDBHelper mSecureMeDBHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int SECURITY_MASTER = 0;

    private static final int SECURITY_TYPES = 1;

    private static final int SECURITY_SETTINGS = 2;

    @Override
    public boolean onCreate() {
        mSecureMeDBHelper = new SecureMeDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case SECURITY_MASTER:
                cursor = mSecureMeDBHelper.getReadableDatabase().query(SecureMeContract.SecurityMaster.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SECURITY_TYPES:
                cursor = mSecureMeDBHelper.getReadableDatabase().query(SecureMeContract.SecurityTypes.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SECURITY_SETTINGS:
                cursor = mSecureMeDBHelper.getReadableDatabase().query(SecureMeContract.SecuritySettings.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI");

        }

        if(cursor != null)
        {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mSecureMeDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match) {
            case SECURITY_MASTER: {

                long id = db.insert(SecureMeContract.SecurityMaster.TABLE_NAME, null, values);

                if( id > 0 )
                {
                    returnUri = SecureMeContract.SecurityMaster.buildUri(id);
                }
                else
                {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case SECURITY_TYPES: {

                long id = db.insert(SecureMeContract.SecurityTypes.TABLE_NAME, null, values);

                if( id > 0 )
                {
                    returnUri = SecureMeContract.SecurityTypes.buildUri(id);
                }
                else
                {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case SECURITY_SETTINGS: {

                long id = db.insertWithOnConflict(SecureMeContract.SecuritySettings.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                if( id > 0 )
                {
                    returnUri = SecureMeContract.SecuritySettings.buildUri(id);
                }
                else
                {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mSecureMeDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;

        switch(match)
        {

            case SECURITY_MASTER:
                rowsDeleted = db.delete(SecureMeContract.SecurityMaster.TABLE_NAME, selection, selectionArgs);
                break;
            case SECURITY_TYPES:
                rowsDeleted = db.delete(SecureMeContract.SecurityTypes.TABLE_NAME, selection, selectionArgs);
                break;
            case SECURITY_SETTINGS:
                rowsDeleted = db.delete(SecureMeContract.SecuritySettings.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mSecureMeDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match)
        {
            case SECURITY_MASTER:
                rowsUpdated = db.update(SecureMeContract.SecurityMaster.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SECURITY_TYPES:
                rowsUpdated = db.update(SecureMeContract.SecurityTypes.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SECURITY_SETTINGS:
            rowsUpdated = db.update(SecureMeContract.SecuritySettings.TABLE_NAME, values, selection, selectionArgs);
            break;
            default:
                throw new UnsupportedOperationException("Unknown Uri");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SecureMeContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, SecureMeContract.PATH_SECURITY_MASTER, SECURITY_MASTER);
        matcher.addURI(authority, SecureMeContract.PATH_SECURITY_TYPES, SECURITY_TYPES);
        matcher.addURI(authority, SecureMeContract.PATH_SECURITY_SETTINGS, SECURITY_SETTINGS);

        return matcher;
    }



}
