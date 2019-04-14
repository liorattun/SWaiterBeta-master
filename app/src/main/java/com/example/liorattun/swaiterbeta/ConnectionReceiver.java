package com.example.liorattun.swaiterbeta;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionReceiver extends BroadcastReceiver {
    /**
     * A flag which tells if the dialog was already displayed in the activity.
     */
    private boolean hasDisplayed;

    /**
     * A constructor of the ConnectionReceiver class.
     *
     * This function creates a ConnectionReceiver and sets the hasDisplayed flag to false.
     */
    public ConnectionReceiver()
    {
        hasDisplayed = false;
    }

    /**
     * Shows a dialog with an appropriate error message when connection to the internet is lost.
     *
     * @param context the context in which the dialog will be shown.
     * @param intent the intent that is received from the context.
     */
    @Override
    //
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected && !hasDisplayed)
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(context);

            adb.setTitle("error");
            adb.setMessage("there is no internet connection");
            adb.setNegativeButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog ad = adb.create();
            ad.show();

            hasDisplayed = true;
        }
    }
}

