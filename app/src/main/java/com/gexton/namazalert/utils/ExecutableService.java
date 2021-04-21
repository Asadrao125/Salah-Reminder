package com.gexton.namazalert.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;

import java.util.Calendar;

import androidx.core.app.NotificationCompat;

public class ExecutableService extends BroadcastReceiver {
    int h, m, r;
    String val, fajar, zuhar, asar, maghrib, isha;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPref.init(context);
        fajar = SharedPref.read("fajar", "");
        zuhar = SharedPref.read("zuhar", "");
        asar = SharedPref.read("asar", "");
        maghrib = SharedPref.read("maghrib", "");
        isha = SharedPref.read("isha", "");

        val = intent.getStringExtra("val");
        h = intent.getIntExtra("h", 100000);
        m = intent.getIntExtra("m", 100000);
        r = intent.getIntExtra("r", 100000);

        setAlarm(r, val, h, m, context);

        if (val.equals("fajar") && fajar.equals("yes")) {
            completeNotif(context, val);
            AzanControl.getInstance(context).playAzan();
        } else if (val.equals("zuhar") && zuhar.equals("yes")) {
            completeNotif(context, val);
            AzanControl.getInstance(context).playAzan();
        } else if (val.equals("asar") && asar.equals("yes")) {
            completeNotif(context, val);
            AzanControl.getInstance(context).playAzan();
        } else if (val.equals("maghrib") && maghrib.equals("yes")) {
            completeNotif(context, val);
            AzanControl.getInstance(context).playAzan();
        } else if (val.equals("isha") && isha.equals("yes")) {
            completeNotif(context, val);
            AzanControl.getInstance(context).playAzan();
        } else {
            simpleNotif(context, val);
        }
    }

    public void completeNotif(Context context, String value) {
        String v = value.substring(0, 1).toUpperCase() + value.substring(1);
        NotificationHelper notificaitonHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificaitonHelper.getChannelNotification(v);
        notificaitonHelper.getManager().notify(1, nb.build());
    }

    public void simpleNotif(Context context, String value) {
        String v = value.substring(0, 1).toUpperCase() + value.substring(1);
        NotificationHelper notificaitonHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificaitonHelper.simpleNotification(v);
        notificaitonHelper.getManager().notify(2, nb.build());
    }

    private void setAlarm(int requestCode, String namazName, int h, int m, Context context) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);
        c.set(Calendar.SECOND, 0);

        if (context != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ExecutableService.class);
            intent.putExtra("val", namazName);
            intent.putExtra("h", h);
            intent.putExtra("m", m);
            intent.putExtra("r", requestCode);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);

            if (c.before(Calendar.getInstance())) {
                c.add(Calendar.DATE, 1);
            }

            if (am != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT <= 20) {
                    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT > 20 && Build.VERSION.SDK_INT <= 23) {
                    am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }
}