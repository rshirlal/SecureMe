package com.poorah.secureme.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.poorah.secureme.R;
import com.poorah.secureme.data.Preferences;

public class SecurityPIN extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    TextView mPinNo0, mPinNo1, mPinNo2, mPinNo3, mPinNo4, mPinNo5, mPinNo6, mPinNo7, mPinNo8, mPinNo9;
    TextView mDel, mOk;
    TextView mPinDisplay1, mPinDisplay2, mPinDisplay3, mPinDisplay4;
    Preferences mPreferences;
    String mPINEntered;

    public static final String VERIFY_OR_RESET_PIN = "verify_or_reset";

    public static final int VERIFY_PIN = 1;
    public static final int RESET_PIN = 0;

    int mCallType;
    int mResultStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_pin);
        mCallType = VERIFY_PIN;
        Bundle data = getIntent().getExtras();
        if (data != null) {
            mCallType = data.getInt(VERIFY_OR_RESET_PIN, VERIFY_PIN);
        }
        mPINEntered = "";
        initializeViews();
        setupOnClickListeners();
        updateDisplayText();

        mResultStatus = RESULT_CANCELED;
        mPreferences = new Preferences(this);

    }

    private void initializeViews() {
        mPinNo0 = (TextView) findViewById(R.id.pin0);
        mPinNo1 = (TextView) findViewById(R.id.pin1);
        mPinNo2 = (TextView) findViewById(R.id.pin2);
        mPinNo3 = (TextView) findViewById(R.id.pin3);
        mPinNo4 = (TextView) findViewById(R.id.pin4);
        mPinNo5 = (TextView) findViewById(R.id.pin5);
        mPinNo6 = (TextView) findViewById(R.id.pin6);
        mPinNo7 = (TextView) findViewById(R.id.pin7);
        mPinNo8 = (TextView) findViewById(R.id.pin8);
        mPinNo9 = (TextView) findViewById(R.id.pin9);

        mDel = (TextView) findViewById(R.id.pinDel);
        mOk = (TextView) findViewById(R.id.pinOK);
        mOk.setVisibility(View.INVISIBLE);

        mPinDisplay1 = (TextView) findViewById(R.id.pinDisplay1);
        mPinDisplay2 = (TextView) findViewById(R.id.pinDisplay2);
        mPinDisplay3 = (TextView) findViewById(R.id.pinDisplay3);
        mPinDisplay4 = (TextView) findViewById(R.id.pinDisplay4);
    }

    private void setupOnClickListeners() {
        mPinNo0.setOnClickListener(this);
        mPinNo1.setOnClickListener(this);
        mPinNo2.setOnClickListener(this);
        mPinNo3.setOnClickListener(this);
        mPinNo4.setOnClickListener(this);
        mPinNo5.setOnClickListener(this);
        mPinNo6.setOnClickListener(this);
        mPinNo7.setOnClickListener(this);
        mPinNo8.setOnClickListener(this);
        mPinNo9.setOnClickListener(this);

        mDel.setOnClickListener(this);
        mOk.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == mDel.getId()) {
            // Remove the last char from current entry
            removePINArray();
        } else if (v.getId() == mOk.getId()) {
            // Build and process the pin entered
            buildAndProcessPIN();

        } else {
            // This means this should be one of the numbers
            if (v instanceof TextView) {
                String currentPinNoString = ((TextView) v).getText().toString();

                if (TextUtils.isDigitsOnly(currentPinNoString)) {
                    // Ok Everything enterd is digits only
                    int currentPinNo = Integer.parseInt(currentPinNoString);
                    if (currentPinNo < 0 || currentPinNo > 9) {
                        Toast.makeText(this, "Invaid entry", Toast.LENGTH_LONG).show();
                    } else {
                        // Valid PIN entry
                        updatePINArray(currentPinNo);
                    }
                }
            }


        }
    }

    private void removePINArray() {
        mPINEntered = "";
        updateDisplayText();
    }

    private void updatePINArray(int pinNo) {
        if (mPINEntered.length() < 4) {
            StringBuilder builder = new StringBuilder(mPINEntered);
            builder.append(Integer.toString(pinNo));
            mPINEntered = builder.toString();
        }
        updateDisplayText();
        if(mPINEntered.length() == 4){
            mOk.performClick();
        }

    }

    private void updateDisplayText() {
        mPinDisplay1.setBackground(ContextCompat.getDrawable(this,mPINEntered.length() >= 1 ? R.drawable.white_circle_textview_filled : R.drawable.white_circle_textview));
        mPinDisplay2.setBackground(ContextCompat.getDrawable(this,mPINEntered.length() >= 2 ? R.drawable.white_circle_textview_filled : R.drawable.white_circle_textview));
        mPinDisplay3.setBackground(ContextCompat.getDrawable(this,mPINEntered.length() >= 3 ? R.drawable.white_circle_textview_filled : R.drawable.white_circle_textview));
        mPinDisplay4.setBackground(ContextCompat.getDrawable(this,mPINEntered.length() >= 4 ? R.drawable.white_circle_textview_filled : R.drawable.white_circle_textview));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mResultStatus = RESULT_CANCELED;
        finish();
    }

    private void buildAndProcessPIN() {

        if (mPINEntered.length() == 4){
            // This means the PIN has been entered
            if(mCallType == RESET_PIN){
                mPreferences.setPIN(mPINEntered);
                mResultStatus = RESULT_OK;
                finish();
                return;
            }else if(mCallType == VERIFY_PIN){
                // Validate the PIN and if it's all good
                // finish, else display a message
                if(mPreferences.getPIN().equalsIgnoreCase(mPINEntered)){
                    mResultStatus = RESULT_OK;
                    finish();
                    return;
                }
            }
        }
        displayInvalidPINMessage();

    }

    private void clearTextAndUpdateDisplay(){
        mPINEntered = "";
        updateDisplayText();
    }

    private void displayInvalidPINMessage(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.content("Invalid PIN entered");
        builder.positiveText("OK");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                clearTextAndUpdateDisplay();
            }
        });
        builder.cancelable(true);
        builder.canceledOnTouchOutside(false);
        builder.cancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                clearTextAndUpdateDisplay();
            }
        });

        MaterialDialog dialog = builder.build();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                clearTextAndUpdateDisplay();
            }
        });
        dialog.show();
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        Bundle dataToReturn = getIntent().getExtras();
        returnIntent.putExtras(dataToReturn);
        setResult(mResultStatus, returnIntent);
        super.finish();
    }
}
