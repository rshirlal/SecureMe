package com.poorah.secureme.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.poorah.secureme.R;
import com.poorah.secureme.adapter.SecurityMasterAdapter;
import com.poorah.secureme.callbacks.PINCheckListener;
import com.poorah.secureme.data.SecureMeContract;
import com.poorah.secureme.dialogs.SecretMaintenance;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    RecyclerView mRecyclerView;
    SecurityMasterAdapter mSecurityMasterAdapter;
    FloatingActionButton mAddNew;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;

    private static final int PIN_VERIFICATION = 1;
    private static final int PIN_RESET = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navigation Drawer
        mToolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.secret_lists);
        mRecyclerView.setLayoutManager(layoutManager);
        mSecurityMasterAdapter = new SecurityMasterAdapter(this,null);
        mRecyclerView.setAdapter(mSecurityMasterAdapter);
        getSupportLoaderManager().restartLoader(0, null, this);

        // FAB for new secret
        mAddNew = (FloatingActionButton) findViewById(R.id.add_new);
        mAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addSecret();
                    }
                });

            }
        });

        // Always begin with PIN Verification
        verifyPIN();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PIN_VERIFICATION){
            if(resultCode != RESULT_OK){
                // Uh Oh!! The user didn't enter the correct PIN
                showInvalidPINMessage();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    /*
            CURSOR LOADERS
         */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                SecureMeContract.SecurityMaster.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSecurityMasterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /*
        MAINTAIN SECRETS
     */
    public void editSecret(int secretID){
        new SecretMaintenance(this,secretID).addOrMaintainSecret();
    }

    public void addSecret(){
        new SecretMaintenance(MainActivity.this).addOrMaintainSecret();
    }

    /*
        PIN VERIFICATION & RESET
     */
    private void verifyPIN(){
        Intent pinCheckerIntent = new Intent(this,SecurityPIN.class);
        pinCheckerIntent.putExtra(SecurityPIN.VERIFY_OR_RESET_PIN,SecurityPIN.VERIFY_PIN);  // Verification
        startActivityForResult(pinCheckerIntent,PIN_VERIFICATION);
    }

    private void resetPIN(){
        Intent pinCheckerIntent = new Intent(this,SecurityPIN.class);
        pinCheckerIntent.putExtra(SecurityPIN.VERIFY_OR_RESET_PIN,SecurityPIN.RESET_PIN);  // Verification
        startActivityForResult(pinCheckerIntent,PIN_RESET);
    }

    private void showInvalidPINMessage(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.app_name);
        builder.content("Invalid PIN Entered");
        builder.canceledOnTouchOutside(false);
        builder.positiveText("Try Again");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                verifyPIN();
                if(dialog.isShowing()) dialog.dismiss();
            }
        });
        builder.negativeText("Exit");
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if(dialog.isShowing()) dialog.dismiss();
                finish();
            }
        });

        builder.build().show();
    }






}
