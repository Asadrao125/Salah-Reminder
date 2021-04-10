package com.gexton.salahreminder.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Reciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!intent.getStringExtra("val").isEmpty() && intent.getStringExtra("val").equals("stop_azan")) {
            AzanControl.getInstance(context).stopAzan();
        }
    }
}