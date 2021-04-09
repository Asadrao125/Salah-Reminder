package com.google.salahreminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.salahreminder.R;
import com.karumi.dexter.BuildConfig;

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
        com.google.salahreminder.tasbeeh_files.AboutDialog aboutDialog = new com.google.salahreminder.tasbeeh_files.AboutDialog(this);
        aboutDialog.show(getSupportFragmentManager(), "about dialog");
    }

}