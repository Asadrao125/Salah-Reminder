package com.google.salahreminder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.salahreminder.AdsManager.SingletonAds;
import com.google.salahreminder.R;
import com.google.salahreminder.utils.GPSTracker;
import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.salahreminder.AdsManager.AdsKt.showBanner;
import static com.google.salahreminder.AdsManager.AdsKt.showInterstitial;

public class HomeActivity extends AppCompatActivity {
    GPSTracker gpsTracker;
    ImageView imgAbout, imgShare;
    TextView c_time, tvLocation1, tvHijriDate;
    String address;
    CardView cvNamazTimings, cvDigitalTasbeeh, cvZakaatCalculator, cvQiblaCompass, cvNamesOfAllah, cvSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();

        /*SingletonAds.Companion.init(getApplicationContext());
        FrameLayout banner_container = findViewById(R.id.ad_view_container);
        showBanner(HomeActivity.this, banner_container);*/

        cvNamazTimings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        cvDigitalTasbeeh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TasbeehActivity.class));
            }
        });

        cvZakaatCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ZakaatCalculator.class));
            }
        });

        cvQiblaCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, QiblaCompassActivity.class));
            }
        });

        cvNamesOfAllah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, Start.class));
            }
        });

        cvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(HomeActivity.this, TasbeehActivity.class));
            }
        });

        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                c_time.setText(new SimpleDateFormat("hh:mm ss a", Locale.US).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);

        if (gpsTracker.canGetLocation()) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
                address = addresses.get(0).getAddressLine(0);
                Log.d("Location_Address", "onLocationChanged: " + address);
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                tvLocation1.setText(city + " " + country);
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        gpsTracker = new GPSTracker();
        imgAbout = findViewById(R.id.imgAbout);
        imgShare = findViewById(R.id.imgShare);
        c_time = findViewById(R.id.c_time);
        tvLocation1 = findViewById(R.id.tvLocation1);
        tvHijriDate = findViewById(R.id.tvHijriDate);
        cvNamazTimings = findViewById(R.id.cvNamazTimings);
        cvDigitalTasbeeh = findViewById(R.id.cvDigitalTasbeeh);
        cvZakaatCalculator = findViewById(R.id.cvZakaatCalculator);
        cvQiblaCompass = findViewById(R.id.cvQiblaCompass);
        cvNamesOfAllah = findViewById(R.id.cvNamesOfAllah);
        cvSettings = findViewById(R.id.cvSettings);
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