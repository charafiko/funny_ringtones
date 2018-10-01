package com.acc.gp.ringtones.funny.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;

import com.acc.gp.ringtones.funny.R;

public class Utils {
    private static Dialog progressDialog;

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
    }

    public static void showAlertDialog(Context context, String strTitle, String strText, String buttonOkText, String buttonCancelText,
                                       boolean cancelable, DialogInterface.OnClickListener okListener,
                                       DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        String errorText = strText;
        alertDialogBuilder.setTitle(strTitle);
        alertDialogBuilder.setMessage(errorText);
        alertDialogBuilder.setPositiveButton(buttonOkText, okListener);
        alertDialogBuilder.setNegativeButton(buttonCancelText, cancelListener);
        alertDialogBuilder.setCancelable(cancelable);
        Dialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void showProgressDialog(Context context) {
        if (progressDialog == null) {
            progressDialog = new Dialog(context, R.style.StyleDialog);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.setCancelable(false);
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
