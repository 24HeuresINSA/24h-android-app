package com.insadelyon.les24heures.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.insadelyon.les24heures.service.impl.ApplicationVersionServiceImpl;

/**
 * Created by remi on 14/04/15.
 */
public class UpdateReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(activeNetInfo == null)
            activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected = activeNetInfo != null;// && activeNetInfo.isConnectedOrConnecting();
        if (isConnected){
            ApplicationVersionServiceImpl.getInstance().checkApplicationVersion(context);
            Log.i("NET", "connecte" + isConnected);
        }
            else Log.i("NET", "not connecte" +isConnected);
    }
}