package com.gexton.salahreminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gexton.salahreminder.AdsManager.SingletonAds;
import com.gexton.salahreminder.R;
import com.gexton.salahreminder.tasbeeh_files.AboutDialog;
import com.karumi.dexter.BuildConfig;

import static com.gexton.salahreminder.AdsManager.AdsKt.showBanner;

public class SettingsActivity extends AppCompatActivity {
    ImageView imgBack;
    LinearLayout share_to_friend, rate_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imgBack = findViewById(R.id.imgBack);
        share_to_friend = findViewById(R.id.share_to_friend);
        rate_us = findViewById(R.id.rate_us);

        SingletonAds.Companion.init(this);
        FrameLayout banner_container = findViewById(R.id.ad_view_container);
        showBanner(this, banner_container);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        share_to_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIntent();
            }
        });

        rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateApp();
            }
        });

    }

    public void rateApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=PackageName")));
    }

    public void shareIntent() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Namaz Timings");
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    public void openAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.show(getSupportFragmentManager(), "about dialog");
    }

}