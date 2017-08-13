package com.poorah.secureme.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.poorah.secureme.R;
import com.poorah.secureme.adapter.SecurityMasterAdapter;
import com.poorah.secureme.callbacks.PINCheckListener;
import com.poorah.secureme.data.SecureMeContract;
import com.poorah.secureme.dialogs.SecretMaintenance;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{



    RecyclerView mRecyclerView;
    SecurityMasterAdapter mSecurityMasterAdapter;
    FloatingActionButton mAddNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.secret_lists);
        mRecyclerView.setLayoutManager(layoutManager);
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
        mSecurityMasterAdapter = new SecurityMasterAdapter(this,null);
        mRecyclerView.setAdapter(mSecurityMasterAdapter);
        getSupportLoaderManager().restartLoader(0, null, this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == EDIT_SECRET_AFTER_INTENT){
                new SecretMaintenance(this,data.getIntExtra(SECRET_ID_SELECTED,-1)).addOrMaintainSecret();
            }else if(requestCode == NEW_SECRET_AFTER_INTENT){
                new SecretMaintenance(MainActivity.this).addOrMaintainSecret();
            }
        }
    }

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
    private static final String SECRET_ID_SELECTED = "secret_id_selected";
    private final int EDIT_SECRET_AFTER_INTENT = 1;
    private final int NEW_SECRET_AFTER_INTENT = 2;


    public void editSecret(int secretID){
        Intent pinCheckerIntent = new Intent(this,SecurityPIN.class);
        pinCheckerIntent.putExtra(SECRET_ID_SELECTED,secretID);
        pinCheckerIntent.putExtra(SecurityPIN.GET_OR_SET_PIN,SecurityPIN.GET_PIN);
        startActivityForResult(pinCheckerIntent,EDIT_SECRET_AFTER_INTENT);
    }

    public void addSecret(){
        Intent pinCheckerIntent = new Intent(this,SecurityPIN.class);
        pinCheckerIntent.putExtra(SECRET_ID_SELECTED,-1);
        pinCheckerIntent.putExtra(SecurityPIN.GET_OR_SET_PIN,SecurityPIN.GET_PIN);
        startActivityForResult(pinCheckerIntent,NEW_SECRET_AFTER_INTENT);
    }






}
