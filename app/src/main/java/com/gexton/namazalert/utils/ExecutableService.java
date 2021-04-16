package com.gexton.namazalert.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import androidx.core.app.NotificationCompat;

public class ExecutableService extends BroadcastReceiver {
    MediaPlayer mp = new MediaPlayer();
    String val;
    String fajar, zuhar, asar, maghrib, isha;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPref.init(context);
        fajar = SharedPref.read("fajar", "");
        zuhar = SharedPref.read("zuhar", "");
        asar = SharedPref.read("asar", "");
        maghrib = SharedPref.read("maghrib", "");
        isha = SharedPref.read("isha", "");

        val = intent.getStringExtra("val");

        if (val.equals("fajar") && fajar.equals("yes")) {
            completeNotif(context);
            AzanControl.getInstance(context).playAzan();
        } else if (val.equals("zuhar") && zuhar.equals("yes")) {
            completeNotif(context);
            AzanControl.getInstance(context).playAzan();
        } else if (val.equals("asar") && asar.equals("yes")) {
            completeNotif(context);
            AzanControl.getInstance(context).playAzan();
        } else if (val.equals("maghrib") && maghrib.equals("yes")) {
            completeNotif(context);
            AzanControl.getInstance(context).playAzan();
        } else if (val.equals("isha") && isha.equals("yes")) {
            completeNotif(context);
            AzanControl.getInstance(context).playAzan();
        } else {
            simpleNotif(context);
        }
    }

    public void completeNotif(Context context){
        NotificationHelper notificaitonHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificaitonHelper.getChannelNotification(val);
        notificaitonHelper.getManager().notify(1, nb.build());
    }

    public void simpleNotif(Context context){
        NotificationHelper notificaitonHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificaitonHelper.simpleNotification(val);
        notificaitonHelper.getManager().notify(2, nb.build());
    }

}