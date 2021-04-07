package com.google.salahreminder.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import androidx.core.app.NotificationCompat;

public class ExecutableService extends BroadcastReceiver {
    MediaPlayer mp = new MediaPlayer();

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificaitonHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificaitonHelper.getChannelNotification();
        notificaitonHelper.getManager().notify(1, nb.build());
        AzanControl.getInstance(context).playAzan();
    }
}