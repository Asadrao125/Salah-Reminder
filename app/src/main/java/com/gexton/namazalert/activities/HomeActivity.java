package com.gexton.namazalert.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gexton.namazalert.AdsManager.SingletonAds;
import com.gexton.namazalert.BuildConfig;
import com.gexton.namazalert.R;
import com.gexton.namazalert.utils.GPSTracker;
import com.gexton.namazalert.utils.SharedPref;

import com.hassanjamil.hqibla.CompassActivity;
import com.hassanjamil.hqibla.Constants;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.time4j.SystemClock;
import net.time4j.android.ApplicationStarter;
import net.time4j.calendar.HijriCalendar;
import net.time4j.engine.StartOfDay;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.gexton.namazalert.AdsManager.AdsKt.showBanner;

public class HomeActivity extends AppCompatActivity {
    GPSTracker gpsTracker;
    ImageView imgAbout;
    TextView c_time, tvLocation1, tvHijriDate;
    String address;
    CardView cvNamazTimings, cvDigitalTasbeeh, cvZakaatCalculator, cvQiblaCompass, cvNamesOfAllah, cvSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationStarter.initialize(this, true);
        setContentView(R.layout.activity_home);

        init();
        SharedPref.init(this);
        if (TextUtils.isEmpty(SharedPref.read("fajar", ""))) {
            SharedPref.write("fajar", "no");
            SharedPref.write("zuhar", "no");
            SharedPref.write("asar", "no");
            SharedPref.write("maghrib", "no");
            SharedPref.write("isha", "no");
        }

        SingletonAds.Companion.init(this);
        FrameLayout banner_container = findViewById(R.id.ad_view_container);
        showBanner(HomeActivity.this, banner_container);

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
                Intent intent = new Intent(HomeActivity.this, CompassActivity.class);
                intent.putExtra(Constants.TOOLBAR_TITLE, "Qibla Compass");        // Toolbar Title
                intent.putExtra(Constants.TOOLBAR_BG_COLOR, "#0b8f08");        // Toolbar Background color
                intent.putExtra(Constants.TOOLBAR_TITLE_COLOR, "#FFFFFF");    // Toolbar Title color
                intent.putExtra(Constants.COMPASS_BG_COLOR, "#FFFFFF");        // Compass background color
                intent.putExtra(Constants.ANGLE_TEXT_COLOR, "#000000");        // Angle Text color
                intent.putExtra(Constants.DRAWABLE_DIAL, R.drawable.dial);    // Your dial drawable resource
                intent.putExtra(Constants.DRAWABLE_QIBLA, R.drawable.qibla);    // Your qibla indicator drawable resource
                intent.putExtra(Constants.FOOTER_IMAGE_VISIBLE, View.VISIBLE);    // Footer World Image visibility
                intent.putExtra(Constants.LOCATION_TEXT_VISIBLE, View.VISIBLE); // Location Text visibility
                startActivity(intent);
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
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        imgAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIntent();
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

        checkPermission2();

        getHijriDate();

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

        }
    }

    private void init() {
        gpsTracker = new GPSTracker(getApplicationContext());
        imgAbout = findViewById(R.id.imgAbout);
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

    private void checkPermission2() {
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
                    setCity();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void setCity() {
        gpsTracker = new GPSTracker(HomeActivity.this);
        if (gpsTracker.canGetLocation()) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
                address = addresses.get(0).getAddressLine(0);
                Log.d("city_country", "setCity: " + address);
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                tvLocation1.setText(city + ", " + country);
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }
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

    private void getHijriDate() {
        ChronoFormatter<HijriCalendar> hijriFormat = ChronoFormatter.setUp(HijriCalendar.family(), Locale.ENGLISH)
                .addEnglishOrdinal(HijriCalendar.DAY_OF_MONTH)
                .addPattern(" MMMM yyyy", PatternType.CLDR)
                .build()
                .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA);

        HijriCalendar today = SystemClock.inLocalView().today().transform(HijriCalendar.class, HijriCalendar.VARIANT_UMALQURA);
        System.out.println(hijriFormat.format(today)); // 22nd Rajab 1438

        HijriCalendar todayExact = SystemClock.inLocalView()
                .now(HijriCalendar.family(),
                        HijriCalendar.VARIANT_UMALQURA, StartOfDay.EVENING).toDate();
        System.out.println(hijriFormat.format(todayExact)); // 22nd Rajab 1438 (23rd after 18:00)
        tvHijriDate.setText("" + hijriFormat.format(todayExact));
    }
}