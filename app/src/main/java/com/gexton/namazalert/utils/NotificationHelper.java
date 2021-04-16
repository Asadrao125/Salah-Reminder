package com.gexton.namazalert.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.gexton.namazalert.R;
import com.gexton.namazalert.activities.NamazTimingsActivity;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager notificationManager;
    public String text;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @SuppressLint("ResourceAsColor")
    public NotificationCompat.Builder getChannelNotification(String namazName) {

        Intent activityIntent = new Intent(getApplicationContext(), NamazTimingsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Intent brodcastIntent = new Intent(this, Reciever.class);
        brodcastIntent.putExtra("val", "stop_azan");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, brodcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(namazName + " Time Alert")
                .setSmallIcon(R.mipmap.official_logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setColor(R.color.colorPrimary)
                .setContentIntent(contentIntent)
                .addAction(R.mipmap.ic_launcher, "Stop " + namazName + " Azan", actionIntent);
    }

    @SuppressLint("ResourceAsColor")
    public NotificationCompat.Builder simpleNotification(String namazName) {
        Intent activityIntent = new Intent(getApplicationContext(), NamazTimingsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 1, activityIntent, 0);
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(namazName + " Time Alert")
                .setSmallIcon(R.mipmap.official_logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setColor(R.color.colorPrimary)
                .setContentIntent(contentIntent);
    }
}