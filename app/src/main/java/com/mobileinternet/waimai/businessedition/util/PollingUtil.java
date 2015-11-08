package com.mobileinternet.waimai.businessedition.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.mobileinternet.waimai.businessedition.app.IApplication;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.service.PollingService;

public class PollingUtil {

    public static void startPollingService(Context context) {
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, PollingService.class);

        IApplication iApplication=(IApplication)((Activity)context).getApplication();
        String shopId=iApplication.getData(Share.shop_id);
        String token=iApplication.getData(Share.token);

        intent.putExtra("shopId",shopId);
        intent.putExtra("token",token);

        intent.setAction(PollingService.ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long triggerAtTime = SystemClock.elapsedRealtime();

        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,
                Share.polling_second * 1000, pendingIntent);
    }


    public static void stopPollingService(Context context) {

        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, PollingService.class);
        intent.putExtra("shopId",CodeUtil.getShopId((Activity)context));
        intent.setAction(PollingService.ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
    }

}
