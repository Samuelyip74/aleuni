package com.aleenterprise.sg.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.aleenterprise.sg.activity.MainActivity;
import com.polestar.naosdk.api.external.NAOERRORCODE;
import com.polestar.naosdk.api.external.NAOGeofenceListener;
import com.polestar.naosdk.api.external.NAOGeofencingHandle;
import com.polestar.naosdk.api.external.NAOGeofencingListener;
import com.polestar.naosdk.api.external.NAOSyncListener;
import com.polestar.naosdk.api.external.NaoAlert;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

// NAO LocationListener : positioning events
// NAO SensorsListener : sensors events (recalibrate device, activate Bluetooth etc...)
// NAO SyncListener : synchronization events (with cloud engine)
public class GeofencingClient implements NAOGeofencingListener, NAOGeofenceListener, NAOSyncListener {

    protected NAOGeofencingHandle handle; // generic service handle

    protected String LBS_API_KEY = "cEtUibrA2IrIasAAM3FQxA";
    protected MainActivity main;
    protected FragmentActivity fmain;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

   /* public GeofencingClient(MainActivity pMainActivity)
    {
        main = pMainActivity;
        // instanciate
        handle = new NAOGeofencingHandle(pMainActivity, LBSService.class, LBS_API_KEY, this, null);
        handle.synchronizeData(this);
        handle.start();
    } */

    public GeofencingClient(FragmentActivity pFragmentActivity)
    {
        fmain = pFragmentActivity;
        // instanciate
        handle = new NAOGeofencingHandle(pFragmentActivity, LBSService.class, LBS_API_KEY, this, null);
        handle.synchronizeData(this);
        handle.start();
    }



    @Override
    public void onSynchronizationSuccess() {
        Log.i("GeofencingClient", "on synchro OK");

    }

    @Override
    public void onSynchronizationFailure(NAOERRORCODE naoerrorcode, String s) {
        Log.i("GeofencingClient", "on synchro KO");
    }


    @Override
    public void onEnterGeofence(int i, String s) {
        Log.i("GeofencingClient", "onEnterGeofence :" + s);
        // Set the icon, scrolling text and timestamp
        /*
        mBuilder = new NotificationCompat.Builder(getApplicationContext());

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Enter Location")
                .setContentText(s)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                //.setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 , mBuilder.build());
        */
    }

    @Override
    public void onExitGeofence(int i, String s) {
        Log.i("GeofencingClient", "onExitGeofence :" + s);
        //Toast.makeText(getApplicationContext(), "Exit "+ s, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onFireNaoAlert(NaoAlert naoAlert) {
        Log.i("GeofencingClient", "NAO Alert :" + naoAlert.getContent());
        //Toast.makeText(getApplicationContext(), "Show "+ naoAlert.getContent().toString(), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onError(NAOERRORCODE naoerrorcode, String s) {

    }
}
