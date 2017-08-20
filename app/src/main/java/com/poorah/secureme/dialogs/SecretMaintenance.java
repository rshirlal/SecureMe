package com.poorah.secureme.dialogs;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.poorah.secureme.R;
import com.poorah.secureme.data.SecureMeContract;
import com.poorah.secureme.utils.Secure;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by rahul on 11/8/17.
 */

public class SecretMaintenance {

    private static final String TAG = SecretMaintenance.class.getSimpleName();
    private Context mContext;
    private long mSecretID;
    private Secure mSecure;

    private String mProvider, mType, mUsername, mPassword;

    public SecretMaintenance(Context context){
        mContext = context;
        mSecretID = 0;
        // No secret ID, so that means the user is trying to add
        // a new record, so initialize the fields with empty values.
        mProvider = "";
        mType = "";
        mUsername = "";
        mPassword = "";
        mSecure = new Secure(mContext);
    }

    public SecretMaintenance(Context context, int secretID){
        mContext = context;
        mSecretID = secretID;
        mSecure = new Secure(mContext);

        String selection = SecureMeContract.SecurityMaster._ID+ " = ?";
        String[] selectionArgs = {Long.toString(mSecretID)};
        Cursor cursor = mContext.getContentResolver().query(SecureMeContract.SecurityMaster.CONTENT_URI,null,selection,selectionArgs,null);
        while(cursor.moveToNext()){
            mProvider = cursor.getString(cursor.getColumnIndex(SecureMeContract.SecurityMaster.COLUMN_NAME));
            mType = cursor.getString(cursor.getColumnIndex(SecureMeContract.SecurityMaster.COLUMN_TYPE));
            mUsername = cursor.getString(cursor.getColumnIndex(SecureMeContract.SecurityMaster.COLUMN_USERNAME));
            String password = cursor.getString(cursor.getColumnIndex(SecureMeContract.SecurityMaster.COLUMN_SECRET));
            mPassword = mSecure.decrypt(password);

        }
        cursor.close();

    }

    public void addOrMaintainSecret(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder.customView(R.layout.secret_maintenance,true);
        builder.positiveText(mSecretID == 0 ? "Add" : "Update");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if(mSecretID == 0){
                    // This means we don't have a current secret and this is a new one
                    addNewSecret(dialog.getView());
                }else{
                    updateSecret(dialog.getView());
                }
            }
        });

        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if(dialog.isShowing()) dialog.dismiss();
            }
        });
        MaterialDialog dialog = builder.build();
        View view = dialog.getView();

        ((EditText)view.findViewById(R.id.secret_name)).setText(mProvider);
        ((EditText) view.findViewById(R.id.secret_type)).setText(mType);
        ((EditText) view.findViewById(R.id.username)).setText(mUsername);
        ((EditText) view.findViewById(R.id.password)).setText(mPassword);

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void addNewSecret(View view){

        ContentValues contentValues = getDataFromView(view);
        if(!checkIfOkToInsert(contentValues)) return;
        Uri newUri = mContext.getContentResolver().insert(SecureMeContract.SecurityMaster.CONTENT_URI,contentValues);
        long id = ContentUris.parseId(newUri);
        if(id <= 0){
            Toast.makeText(mContext,"Could not insert the new secret",Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfOkToInsert(ContentValues contentValues){

        if(TextUtils.isEmpty(contentValues.getAsString(SecureMeContract.SecurityMaster.COLUMN_NAME))) {
            Toast.makeText(mContext,"Provider name required",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(contentValues.getAsString(SecureMeContract.SecurityMaster.COLUMN_USERNAME))) {
            Toast.makeText(mContext,"Username required",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(contentValues.getAsString(SecureMeContract.SecurityMaster.COLUMN_SECRET))) {
            Toast.makeText(mContext,"Password required",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateSecret(View view){
        ContentValues contentValues = getDataFromView(view);
        String selection = SecureMeContract.SecurityMaster._ID+ " = ?";
        String[] selectionArgs = {Long.toString(mSecretID)};
        int rowsUpdated = mContext.getContentResolver().update(SecureMeContract.SecurityMaster.CONTENT_URI,contentValues,selection,selectionArgs);
        if(rowsUpdated != 1){
            Toast.makeText(mContext,"Could not update the current secret",Toast.LENGTH_LONG).show();
        }

    }

    private ContentValues getDataFromView(View view){
        String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        String secretName = ((EditText)view.findViewById(R.id.secret_name)).getEditableText().toString();
        String secretType = ((EditText) view.findViewById(R.id.secret_type)).getEditableText().toString();
        String username = ((EditText) view.findViewById(R.id.username)).getEditableText().toString();
        String password = ((EditText) view.findViewById(R.id.password)).getEditableText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SecureMeContract.SecurityMaster.COLUMN_NAME,secretName);
        contentValues.put(SecureMeContract.SecurityMaster.COLUMN_DESCRIPTION,secretName);
        contentValues.put(SecureMeContract.SecurityMaster.COLUMN_TYPE,secretType);
        contentValues.put(SecureMeContract.SecurityMaster.COLUMN_USERNAME,username);
        // Encrypt the password before saving.
        String encrypted = mSecure.encrypt(password);
        contentValues.put(SecureMeContract.SecurityMaster.COLUMN_SECRET,encrypted);
        contentValues.put(SecureMeContract.SecurityMaster.COLUMN_LAST_CHANGED,currentDateTime);
        return contentValues;
    }
}
