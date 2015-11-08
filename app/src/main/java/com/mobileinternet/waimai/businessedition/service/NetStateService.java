package com.mobileinternet.waimai.businessedition.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.app.Status;

public class NetStateService extends Service {
    public NetStateService() {
    }


    private ConnectivityManager connectivityManager;
    private NetworkInfo info;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();

                if (info != null && info.isAvailable()) {
                    Status.setIsConnectNet(true);

                    if (!Status.hasGetBusinessTime){
                        Intent inBusiTime=new Intent(Share.receiveApplayBusinessTime);
                        sendOrderedBroadcast(inBusiTime, null);
                    }


                    return;
                }

                Status.setIsConnectNet(false);
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
