package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.activities.OfflineActivity;

public class ConnectivityHelper {
    private static final String TAG = "ConnectivityHelper";
    private BroadcastReceiver internetReceiver;
    private Context context;

    public ConnectivityHelper(Context context)
    {
        this.context = context;
        registerInternetReceiver();
    }

    // https://stackoverflow.com/questions/3767591/check-intent-internet-connection
    public void registerInternetReceiver()
    {
        if (this.internetReceiver != null) return;
        this.internetReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(final Context ctx, final Intent intent)
            {
                if (intent.getExtras() != null) {
                    if(isConnected(ctx)) {
                        Log.i(TAG, "Network connected");
                    } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                        Log.d(TAG, "There's no network connectivity");
                        context.startActivity(new Intent(context, OfflineActivity.class));
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction (ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver (internetReceiver, filter);
    }

    public void unregisterInternetReceiver()
    {
        context.unregisterReceiver (internetReceiver);
        internetReceiver = null;
    }

    public boolean isConnected(Context context) {
        final ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo connection = manager.getActiveNetworkInfo();

        if (connection != null && connection.isConnectedOrConnecting())
            return true;

        return false;
    }
}