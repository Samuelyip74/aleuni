package com.aleenterprise.sg.services;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import com.ale.rainbowsdk.RainbowSdk;
import io.mapwize.mapwizeformapbox.AccountManager;

public class startupservices extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null)
            {
                NotificationChannel channel = new NotificationChannel("channel application id", "notificationname", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);

                channel = new NotificationChannel("call channel id", "notificationname", NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(channel);
            }
        }
        // LBS instance init
        AccountManager.start(this, "3d7e7161d1fc40cc3dfd9950d7812238");

        // Rainbow instance init
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        String applicationId = "cc661c10416f11e9a7c0997308051c7d";
        String applicationSecret = "gLYTmF9RVxwQa3MO77sBfjMPMCO11iKen2ijAu7BAdqXRMegvUaG0zhMKUh4AWus";
        RainbowSdk.instance().initialize(this, applicationId, applicationSecret);
    }
}
