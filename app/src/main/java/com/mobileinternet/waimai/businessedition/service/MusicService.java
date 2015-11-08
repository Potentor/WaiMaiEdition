package com.mobileinternet.waimai.businessedition.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;

import com.mobileinternet.waimai.businessedition.R;


public class MusicService extends IntentService {

    private static final String ACTION_FOO = "newOrder";
 //   private static final String ACTION_BAZ = "ads";

//    public static void playNewOrderMusic(Context context) {
//        Intent intent = new Intent(context, MusicService.class);
//        intent.setAction(ACTION_FOO);
//        context.startService(intent);
//    }
//
//    public static void playAdsMusic(Context context) {
//        Intent intent = new Intent(context, MusicService.class);
//        intent.setAction(ACTION_BAZ);
//        context.startService(intent);
//    }

    public MusicService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handleActionFoo();
                    }
                }).start();
            }

//            else if (ACTION_BAZ.equals(action)) {
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        handleActionBaz();
//                    }
//                }).start();
//
//            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.new_order);
        mp.start();
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
//    private void handleActionBaz() {
//
//        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.ads);
//        mp.start();
//
//    }
}
