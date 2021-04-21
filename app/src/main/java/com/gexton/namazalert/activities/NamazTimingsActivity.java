package com.gexton.namazalert.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gexton.namazalert.AdsManager.SingletonAds;
import com.gexton.namazalert.R;
import com.gexton.namazalert.utils.ExecutableService;
import com.gexton.namazalert.utils.GPSTracker;
import com.gexton.namazalert.utils.SharedPref;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.gexton.namazalert.AdsManager.AdsKt.showBanner;

public class NamazTimingsActivity extends AppCompatActivity {
    GPSTracker gpsTracker;
    SharedPreferences prefs;
    long current_h, current_m;
    ImageView imgFajar, imgZuhar, imgAsar, imgMaghrib, imgISha, imgBack;
    TextView tvSunrise, tvSunset, tvLocation1, c_time;
    TextView tvFajar, tvZuhar, tvAsar, tvMaghrib, tvIsha;
    String MY_PREFS_NAME = "Namaz_Reminder", month, year, date, y, m, dd;
    String address, ff, zz, mm, ii, aa, sun_rise_sun, sunset_sun_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_namaz_timings);

        initialization();
        SharedPref.init(this);
        SingletonAds.Companion.init(this);
        FrameLayout banner_container = findViewById(R.id.ad_view_container);
        showBanner(this, banner_container);

        Calendar calendar = Calendar.getInstance();
        y = String.valueOf(calendar.get(Calendar.YEAR));
        m = String.valueOf(calendar.get(Calendar.MONTH));
        dd = String.valueOf(calendar.get(Calendar.DATE));

        gpsTracker = new GPSTracker(getApplicationContext());

        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                c_time.setText(new SimpleDateFormat("hh:mm ss a", Locale.US).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String fajar = prefs.getString("f", "-- : --");
        String zuhar = prefs.getString("z", "-- : --");
        String asar = prefs.getString("a", "-- : --");
        String maghrib = prefs.getString("m", "-- : --");
        String isha = prefs.getString("i", "-- : --");
        String sun_rise = prefs.getString("s_r", "-- : --");
        String sun_set = prefs.getString("s_s", "-- : --");

        tvFajar.setText(fajar);
        tvZuhar.setText(zuhar);
        tvAsar.setText(asar);
        tvMaghrib.setText(maghrib);
        tvIsha.setText(isha);
        tvSunrise.setText(sun_rise);
        tvSunset.setText(sun_set);

        OffsetDateTime offset = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            offset = OffsetDateTime.now();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            month = String.valueOf(offset.getMonth());
            year = String.valueOf(offset.getYear());
            date = String.valueOf(offset.getDayOfMonth());
        }

        tvSunrise.setText(prefs.getString("s_r", "Set Time"));
        tvSunset.setText(prefs.getString("s_s", "Set Time"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            current_h = LocalDateTime.now().getHour();
            current_m = LocalDateTime.now().getMinute();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imgFajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgFajar.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.n_off).getConstantState()) {
                    SharedPref.write("fajar", "yes");
                    imgFajar.setImageResource(R.drawable.n_on);
                } else {
                    SharedPref.write("fajar", "no");
                    imgFajar.setImageResource(R.drawable.n_off);
                }
            }
        });

        imgZuhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgZuhar.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.n_off).getConstantState()) {
                    SharedPref.write("zuhar", "yes");
                    imgZuhar.setImageResource(R.drawable.n_on);
                } else {
                    SharedPref.write("zuhar", "no");
                    imgZuhar.setImageResource(R.drawable.n_off);
                }
            }
        });

        imgAsar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgAsar.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.n_off).getConstantState()) {
                    SharedPref.write("asar", "yes");
                    imgAsar.setImageResource(R.drawable.n_on);
                } else {
                    SharedPref.write("asar", "no");
                    imgAsar.setImageResource(R.drawable.n_off);
                }
            }
        });

        imgMaghrib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgMaghrib.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.n_off).getConstantState()) {
                    SharedPref.write("maghrib", "yes");
                    imgMaghrib.setImageResource(R.drawable.n_on);
                } else {
                    SharedPref.write("maghrib", "no");
                    imgMaghrib.setImageResource(R.drawable.n_off);
                }
            }
        });

        imgISha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgISha.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.n_off).getConstantState()) {
                    SharedPref.write("isha", "yes");
                    imgISha.setImageResource(R.drawable.n_on);
                } else {
                    SharedPref.write("isha", "no");
                    imgISha.setImageResource(R.drawable.n_off);
                }
            }
        });

        if (SharedPref.read("fajar", "").equals("yes")) {
            imgFajar.setImageResource(R.drawable.n_on);
        } else {
            imgFajar.setImageResource(R.drawable.n_off);
        }

        if (SharedPref.read("zuhar", "").equals("yes")) {
            imgZuhar.setImageResource(R.drawable.n_on);
        } else {
            imgZuhar.setImageResource(R.drawable.n_off);
        }

        if (SharedPref.read("asar", "").equals("yes")) {
            imgAsar.setImageResource(R.drawable.n_on);
        } else {
            imgAsar.setImageResource(R.drawable.n_off);
        }

        if (SharedPref.read("maghrib", "").equals("yes")) {
            imgMaghrib.setImageResource(R.drawable.n_on);
        } else {
            imgMaghrib.setImageResource(R.drawable.n_off);
        }

        if (SharedPref.read("isha", "").equals("yes")) {
            imgISha.setImageResource(R.drawable.n_on);
        } else {
            imgISha.setImageResource(R.drawable.n_off);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    private void getResponse(String addres) {
        if (getApplicationContext() != null) {
            String url = "http://api.aladhan.com/v1/timingsByAddress?address=" + addres;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("response_msg", "onResponse: " + response);
                                JSONObject obj = response.getJSONObject("data").getJSONObject("timings");
                                ff = obj.get("Fajr").toString();
                                sun_rise_sun = obj.get("Sunrise").toString();
                                zz = obj.get("Dhuhr").toString();
                                aa = obj.get("Asr").toString();
                                sunset_sun_set = obj.get("Sunset").toString();
                                mm = obj.get("Maghrib").toString();
                                ii = obj.get("Isha").toString();

                                setAlarm(ff, 109833, "fajar");
                                setAlarm(zz, 675483, "zuhar");
                                setAlarm(aa, 768564, "asar");
                                setAlarm(mm, 980324, "maghrib");
                                setAlarm(ii, 764687, "isha");

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                    String f = "" + ff;
                                    String z = "" + zz;
                                    String a = "" + aa;
                                    String m = "" + mm;
                                    String i = "" + ii;
                                    String s_r = "" + sun_rise_sun;
                                    String s_s = "" + sunset_sun_set;

                                    f = LocalTime.parse(ff).format(DateTimeFormatter.ofPattern("hh : mm a"));
                                    z = LocalTime.parse(zz).format(DateTimeFormatter.ofPattern("hh : mm a"));
                                    a = LocalTime.parse(aa).format(DateTimeFormatter.ofPattern("hh : mm a"));
                                    m = LocalTime.parse(mm).format(DateTimeFormatter.ofPattern("hh : mm a"));
                                    i = LocalTime.parse(ii).format(DateTimeFormatter.ofPattern("hh : mm a"));
                                    s_r = LocalTime.parse(sun_rise_sun).format(DateTimeFormatter.ofPattern("hh : mm a"));
                                    s_s = LocalTime.parse(sunset_sun_set).format(DateTimeFormatter.ofPattern("hh : mm a"));

                                    tvFajar.setText("" + f);
                                    tvZuhar.setText("" + z);
                                    tvAsar.setText("" + a);
                                    tvMaghrib.setText("" + m);
                                    tvIsha.setText("" + i);
                                    tvSunrise.setText("" + s_r);
                                    tvSunset.setText("" + s_s);

                                    if (getApplicationContext() != null) {
                                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                        editor.putString("f", f);
                                        editor.putString("z", z);
                                        editor.putString("a", a);
                                        editor.putString("m", m);
                                        editor.putString("i", i);
                                        editor.putString("s_r", s_r);
                                        editor.putString("s_s", s_s);
                                        editor.putString("location", address);
                                        editor.apply();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                    Toast.makeText(NamazTimingsActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            Volley.newRequestQueue(this).add(jsonObjReq);
        }
    }

    private void initialization() {
        tvFajar = findViewById(R.id.tvFajar);
        tvZuhar = findViewById(R.id.tvZuhar);
        tvAsar = findViewById(R.id.tvAsar);
        tvMaghrib = findViewById(R.id.tvMaghrib);
        tvIsha = findViewById(R.id.tvIsha);
        imgFajar = findViewById(R.id.imgFajar);
        imgZuhar = findViewById(R.id.imgZuhar);
        imgAsar = findViewById(R.id.imgAsar);
        imgMaghrib = findViewById(R.id.imgMaghrib);
        imgISha = findViewById(R.id.imgIsha);
        tvSunrise = findViewById(R.id.tv_sunrise);
        tvSunset = findViewById(R.id.tv_sunset);
        tvLocation1 = findViewById(R.id.tvLocation1);
        c_time = findViewById(R.id.c_time);
        prefs = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        imgBack = findViewById(R.id.imgBack);
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
                    gpsTracker = new GPSTracker(NamazTimingsActivity.this);
                    if (gpsTracker.canGetLocation()) {
                        getNamazTimings();
                    } else {
                        gpsTracker.enableLocationPopup();
                    }
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void getNamazTimings() {
        if (gpsTracker.canGetLocation()) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
                address = addresses.get(0).getAddressLine(0);
                Log.d("Location_Address", "onLocationChanged: " + address);
                getResponse(address);
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                tvLocation1.setText(city + ", " + country);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gpsTracker.enableLocationPopup();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gpsTracker.REQUEST_CHECK_SETTING && resultCode == RESULT_OK) {
            Log.d("req_check_setting_namaz", "onActivityResult: " + gpsTracker.REQUEST_CHECK_SETTING);
            startActivity(new Intent(this, NamazTimingsActivity.class));
            finish();
        }
    }

    private void setAlarm(String namazTime, int requestCode, String namazName) {
        String alaramtime_fajar = namazTime;
        String alaramtimesplit_fajar[] = alaramtime_fajar.split(":");  // 04:05
        int firstval_fajar = Integer.parseInt(alaramtimesplit_fajar[0]);
        int secondval_fajar = Integer.parseInt(alaramtimesplit_fajar[1]);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, firstval_fajar);
        c.set(Calendar.MINUTE, secondval_fajar);
        c.set(Calendar.SECOND, 0);

        if (getApplicationContext() != null) {
            AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), ExecutableService.class);
            intent.putExtra("val", namazName);
            intent.putExtra("h", firstval_fajar);
            intent.putExtra("m", secondval_fajar);
            intent.putExtra("r", requestCode);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, 0);

            if (c.before(Calendar.getInstance())) {
                c.add(Calendar.DATE, 1);
            }

            if (am != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT <= 20) {
                    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT > 20) {
                    am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }
}