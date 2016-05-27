package com.perlagloria.util;

import android.app.Activity;
import android.content.DialogInterface;

import com.android.volley.VolleyError;
import com.perlagloria.R;

public class ErrorAlertDialog {
    public static final String NO_CONNECTION = "No connection could be established when performing a request";
    public static final String TIMED_OUT = "Connection or the socket timed out";
    public static final String SERVER_ERROR = "Server responded with an error response";
    public static final String PARSE_ERROR = "Server's response could not be parsed";
    public static final String NETWORK_ERROR = "There was a network error when performing a request";
    public static final String AUTH_FAILURE_ERROR = "There was an authentication failure when performing a request";
    public static final String UNKNOWN_ERROR = "Unknown error";

    public static final String NO_PRODUCTS_FOUND = "Nothing was found";

    public static void show(Activity parent, String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(parent, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(message);
        builder.setNegativeButton(parent.getResources().getString(R.string.alert_dialog_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //stub
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public static void show(Activity parent, String message, DialogInterface.OnClickListener negativeButtonListener) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(parent, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(message);
        builder.setNegativeButton(parent.getString(R.string.alert_dialog_close), negativeButtonListener);
        builder.setCancelable(false);
        builder.show();
    }

    public static void show(Activity parent, String message,
                            DialogInterface.OnClickListener negativeButtonListener, String negativeButtonText,
                            DialogInterface.OnClickListener positiveButtonListener, String positiveButtonText) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(parent, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(message);
        builder.setNegativeButton(negativeButtonText, negativeButtonListener);
        builder.setPositiveButton(positiveButtonText, positiveButtonListener);
        builder.setCancelable(false);
        builder.show();
    }

    public static String getVolleyErrorMessage(VolleyError error) {
        if (error instanceof com.android.volley.NoConnectionError) {
            return NO_CONNECTION;
        } else if (error instanceof com.android.volley.TimeoutError) {
            return TIMED_OUT;
        } else if (error instanceof com.android.volley.ServerError) {
            return SERVER_ERROR;
        } else if (error instanceof com.android.volley.ParseError) {
            return PARSE_ERROR;
        } else if (error instanceof com.android.volley.NetworkError) {
            return NETWORK_ERROR;
        } else if (error instanceof com.android.volley.AuthFailureError) {
            return AUTH_FAILURE_ERROR;
        } else {
            return UNKNOWN_ERROR;
        }
    }
}
