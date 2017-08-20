package com.poorah.secureme.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.poorah.secureme.R;
import com.poorah.secureme.activity.MainActivity;
import com.poorah.secureme.data.SecureMeContract;
import com.poorah.secureme.dialogs.SecretMaintenance;
import com.poorah.secureme.utils.Secure;

/**
 * Created by rahul on 11/8/17.
 */

public class SecurityMasterAdapter extends CursorRecyclerViewAdapter<SecurityMasterAdapter.SecurityMasterViewHolder>{

    private Context mContext;
    private MaterialDialog mPasswordDialog;
    private Secure mSecure;
    private TextView mContentView;
    private String mHiddenText;


    public SecurityMasterAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
        mSecure = new Secure(mContext);
        mHiddenText = mContext.getResources().getString(R.string.hidden_text);
    }

    @Override
    public void onBindViewHolder(final SecurityMasterViewHolder viewHolder, final Cursor cursor) {
        viewHolder.mNameView.setText(cursor.getString(cursor.getColumnIndex(SecureMeContract.SecurityMaster.COLUMN_NAME)));
        viewHolder.mTypeView.setText(cursor.getString(cursor.getColumnIndex(SecureMeContract.SecurityMaster.COLUMN_TYPE)));
        viewHolder.mUserView.setText(cursor.getString(cursor.getColumnIndex(SecureMeContract.SecurityMaster.COLUMN_USERNAME)));
        final int secretID = cursor.getInt(cursor.getColumnIndex(SecureMeContract.SecurityMaster._ID));
        final String secret = cursor.getString(cursor.getColumnIndex(SecureMeContract.SecurityMaster.COLUMN_SECRET));
        viewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSecret(mSecure.decrypt(secret));
            }
        });

        viewHolder.mEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).editSecret(secretID);

            }
        });

        viewHolder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                confirmIfOkToDelete(secretID);
                return true;
            }
        });

    }

    private void confirmIfOkToDelete(final int secretID){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder.content("Are you sure you want to delete this record?");
        builder.positiveText("Yes");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String selection = SecureMeContract.SecurityMaster._ID+ " = ?";
                String[] selectionArgs = {Long.toString(secretID)};
                int rowsDeleted = mContext.getContentResolver().delete(SecureMeContract.SecurityMaster.CONTENT_URI,selection,selectionArgs);
                if(rowsDeleted == 1){
                    Toast.makeText(mContext,"Record deleted",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext,"Records not deleted",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.negativeText("Cancel");
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        MaterialDialog dialog = builder.build();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    private void showSecret(final String decryptedSecret){

        if(mPasswordDialog == null){
            MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(mContext);
            dialogBuilder.positiveText("Close");
            dialogBuilder.neutralText("Copy");
            dialogBuilder.onNeutral(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    copyContentToClipboard(decryptedSecret);
                }
            });
            dialogBuilder.title("Secret");
            mPasswordDialog = dialogBuilder.build();
            mPasswordDialog.setCanceledOnTouchOutside(true);
            mContentView = mPasswordDialog.getContentView();
        }

        if(mPasswordDialog.isShowing()) mPasswordDialog.hide();
        mPasswordDialog.setContent(mHiddenText);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = mContentView.getText().toString();
                mContentView.setText(currentText.equalsIgnoreCase(mHiddenText) ? decryptedSecret : mHiddenText);
            }
        });
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPasswordDialog.show();
            }
        });

    }

    private void copyContentToClipboard(String decryptedSecret){
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(mContext.getResources().getString(R.string.clipboard_label),decryptedSecret);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(mContext,mContext.getResources().getString(R.string.copied_to_clipboard),Toast.LENGTH_LONG).show();
    }



    @Override
    public SecurityMasterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.secret_cardview, parent, false);
        SecurityMasterViewHolder viewHolder = new SecurityMasterViewHolder(view);
        return viewHolder;
    }



    public class SecurityMasterViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mNameView, mTypeView, mUserView;
        public ImageView mEditView;


        public SecurityMasterViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById( R.id.card_view);
            mNameView = (TextView) itemView.findViewById(R.id.secret_name);
            mTypeView = (TextView) itemView.findViewById(R.id.secret_type);
            mUserView = (TextView) itemView.findViewById(R.id.username);
            mEditView = (ImageView) itemView.findViewById(R.id.edit);
        }
    }
}
