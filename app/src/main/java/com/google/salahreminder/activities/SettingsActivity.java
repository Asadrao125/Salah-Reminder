package com.google.salahreminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.salahreminder.R;
import com.karumi.dexter.BuildConfig;

public class SettingsActivity extends AppCompatActivity {
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void shareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Let me recommed you this application\nhttps://play.google.com/store/apps/details?id=" +
                        BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void openAboutDialog() {
        com.google.salahreminder.tasbeeh_files.AboutDialog aboutDialog = new com.google.salahreminder.tasbeeh_files.AboutDialog(this);
        aboutDialog.show(getSupportFragmentManager(), "about dialog");
    }

}