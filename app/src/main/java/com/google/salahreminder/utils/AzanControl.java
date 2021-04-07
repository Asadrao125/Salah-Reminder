package com.google.salahreminder.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.google.salahreminder.R;

public class AzanControl {

    private static AzanControl sInstance;
    private Context mContext;
    private MediaPlayer mMediaPlayer;

    public AzanControl(Context context) {
        mContext = context;
    }

    public static AzanControl getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AzanControl(context);
        }
        return sInstance;
    }

    public void playAzan() {
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.azan);
        mMediaPlayer.start();
    }

    public void stopAzan() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }
}
