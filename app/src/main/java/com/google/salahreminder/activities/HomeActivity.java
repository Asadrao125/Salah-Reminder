package com.google.salahreminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.salahreminder.AdsManager.SingletonAds;
import com.google.salahreminder.R;
import com.google.salahreminder.utils.GPSTracker;
import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import static com.google.salahreminder.AdsManager.AdsKt.showBanner;
import static com.google.salahreminder.AdsManager.AdsKt.showInterstitial;

public class HomeActivity extends AppCompatActivity {
    Button btnNamazTimings, btnTasbeeh, btnZakaatCalculator, btnQiblaCompass;
    GPSTracker gpsTracker;
    ImageView imgAbout, imgShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnNamazTimings = findViewById(R.id.btnNamazTimings);
        btnTasbeeh = findViewById(R.id.btnTasbeeh);
        btnZakaatCalculator = findViewById(R.id.btnZakaatCalculator);
        gpsTracker = new GPSTracker();
        btnQiblaCompass = findViewById(R.id.btnQiblaCompass);
        imgAbout = findViewById(R.id.imgAbout);
        imgShare = findViewById(R.id.imgShare);

        /*SingletonAds.Companion.init(getApplicationContext());
        FrameLayout banner_container = findViewById(R.id.ad_view_container);
        showBanner(HomeActivity.this, banner_container);*/

        btnNamazTimings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        btnTasbeeh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TasbeehActivity.class));
            }
        });

        btnZakaatCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ZakaatCalculator.class));
            }
        });

        btnQiblaCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, QiblaCompassActivity.class));
            }
        });

        imgAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.google.salahreminder.tasbeeh_files.AboutDialog aboutDialog = new com.google.salahreminder.tasbeeh_files.AboutDialog(HomeActivity.this);
                aboutDialog.show(getSupportFragmentManager(), "about dialog");
            }
        });

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIntent();
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

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.SET_ALARM
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    startActivity(new Intent(getApplicationContext(), NamazTimingsActivity.class));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

}