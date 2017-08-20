package com.poorah.secureme.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.poorah.secureme.R;
import com.poorah.secureme.data.Preferences;
import com.poorah.secureme.utils.Secure;

public class MasterKey extends AppCompatActivity implements View.OnClickListener{

    public static final String MASTER_KEY_CALL_TYPE = "call_type";
    public static final int RESET_MASTERKEY = 1;
    public static final int SET_MASTERKEY = 2;
    public static final int VERIFY_MASTERKEY = 3;

    private int mCallType;
    private EditText mCurrentKey, mNewKey, mConfirmNewKey;
    private Button mOk, mCancel;
    private LinearLayout mCurrentKeyLayout, mNewKeyLayout;

    private Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_key);
        mPreferences = new Preferences(this);
        mCallType = getIntent().getIntExtra(MASTER_KEY_CALL_TYPE,VERIFY_MASTERKEY);

        /*  INITIALIZE THE VIEWS */

        mCurrentKey = (EditText) findViewById(R.id.current_master_key);
        mNewKey = (EditText) findViewById(R.id.new_master_key);
        mConfirmNewKey = (EditText) findViewById(R.id.confirm_new_master_key);
        mOk = (Button) findViewById(R.id.ok_action);
        mCancel = (Button) findViewById(R.id.cancel_action);
        mCurrentKeyLayout = (LinearLayout) findViewById(R.id.current_master_key_layout);
        mNewKeyLayout = (LinearLayout) findViewById(R.id.new_master_key_layout);

        /*  SET VIEW VISIBILITY BASED ON CALL TYPE*/

        if(mCallType == SET_MASTERKEY){
            // No need to display the existing key input as this is only to set
            mCurrentKeyLayout.setVisibility(View.INVISIBLE);
        }

        if(mCallType == VERIFY_MASTERKEY){
            // For verification we would only need to get current
            mNewKeyLayout.setVisibility(View.INVISIBLE);
        }

        /*  SET ON CLICK LISTENERS  */
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        /* Defaut Status */
        setResult(RESULT_CANCELED);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAsFail();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mOk.getId()){
            if(mCallType == VERIFY_MASTERKEY){
                if(verifyMasterKey()){
                    finishAsSuccess();
                }else{
                    finishAsFail();
                }
            }else if(mCallType == SET_MASTERKEY){
                if(matchKeysAndSave()){
                    finishAsSuccess();
                }
            }else if(mCallType == RESET_MASTERKEY){
                if(verifyMasterKey()){
                    // The master key match
                    if(matchKeysAndSave()) finishAsSuccess();
                }
            }
        }else if(v.getId() == mCancel.getId()){
            finishAsFail();
        }

    }

    private boolean matchKeysAndSave(){
        String newkeyEntered = mNewKey.getEditableText().toString();
        String newConfirmKeyEntered = mConfirmNewKey.getEditableText().toString();
        if(TextUtils.isEmpty(newkeyEntered) || TextUtils.isEmpty(newConfirmKeyEntered)){
            Snackbar.make(mOk,"Keys cannot be empty.",Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(newkeyEntered.contentEquals(newConfirmKeyEntered)){
            // So the Keys match. Just save them
            String hashedKey = new Secure(this).generateHash(mNewKey.getEditableText().toString());
            mPreferences.setKey(hashedKey);
            return true;
        }else{
            Snackbar.make(mOk,"Keys do not match.",Snackbar.LENGTH_LONG).show();
            // Clear the existing text
            mNewKey.setText(null);
            mConfirmNewKey.setText(null);
            // Set the cursor back to the new key
            mNewKey.requestFocus();
            return false;
        }
    }

    private boolean verifyMasterKey(){
        String masterKeyAsEntered = mCurrentKey.getEditableText().toString();
        if(TextUtils.isEmpty(masterKeyAsEntered)){
            Snackbar.make(mOk,"Please enter a master key",Snackbar.LENGTH_LONG).show();
            return false;
        }
        String hashedMasterKey = new Secure(this).generateHash(masterKeyAsEntered);
        if(hashedMasterKey.equalsIgnoreCase(mPreferences.getKey())){
            return true;
        }else{
            Snackbar.make(mOk,"Invalid master key",Snackbar.LENGTH_LONG).show();
            mCurrentKey.setText(null);
            return false;
        }
    }

    private void finishAsSuccess(){
        setResult(RESULT_OK);
        finish();
    }

    private void finishAsFail(){
        setResult(RESULT_CANCELED);
        finish();
    }

}
