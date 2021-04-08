package com.google.salahreminder.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.salahreminder.R;
import com.google.salahreminder.utils.ExecutableService;
import com.google.salahreminder.utils.GPSTracker;
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

public class NamazTimingsActivity extends AppCompatActivity {
    GPSTracker gpsTracker;
    SharedPreferences prefs;
    long current_h, current_m;
    ImageView imgBack;
    /* txt_View_Date, txt_View_Day */
    TextView tvSunrise, tvSunset, tvLocation1;
    TextView tvFajar, tvZuhar, tvAsar, tvMaghrib, tvIsha;
    String MY_PREFS_NAME = "Namaz_Reminder", month, year, date, y, m, dd;
    TextView fajar_fajar, zuhur_zuhar, asar_asar, maghrib_maghrib, isha_isha, c_time;
    String address, ff, zz, mm, ii, aa, sun_rise_sun, sunset_sun_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.namaz_timings_layout);

        initialization();

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

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-mmmm-yyyy");
        String todayString = formatter.format(todayDate);
        //txt_View_Date.setText(todayString);

        OffsetDateTime offset = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            offset = OffsetDateTime.now();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            month = String.valueOf(offset.getMonth());
            year = String.valueOf(offset.getYear());
            date = String.valueOf(offset.getDayOfMonth());
        }

        String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
        //txt_View_Day.setText("Today / " + weekday_name);

        Date d = new Date();
        CharSequence s = DateFormat.format("d MMMM yyyy ", d.getTime());
        //txt_View_Date.setText(s);

        tvSunrise.setText(prefs.getString("s_r", "Set Time"));
        tvSunset.setText(prefs.getString("s_s", "Set Time"));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            current_h = LocalDateTime.now().getHour();
            current_m = LocalDateTime.now().getMinute();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    private void getResponse(String addres) {
        if (getApplicationContext() != null) {
            String url = "http://api.aladhan.com/v1/timingsByAddress?address=" + addres;
            ////
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {

                        @Override/* Unnamed Road, Latifabad Unit 7 Latifabad, Hyderabad, Sindh 71000, Pakistan */
                        public void onResponse(JSONObject response) {
                            try {

                                Log.d("response_msg", "onResponse: " + response);
                                ff = response.getJSONObject("data").getJSONObject("timings").get("Fajr").toString();
                                sun_rise_sun = response.getJSONObject("data").getJSONObject("timings").get("Sunrise").toString();
                                zz = response.getJSONObject("data").getJSONObject("timings").get("Dhuhr").toString();
                                aa = response.getJSONObject("data").getJSONObject("timings").get("Asr").toString();
                                sunset_sun_set = response.getJSONObject("data").getJSONObject("timings").get("Sunset").toString();
                                mm = response.getJSONObject("data").getJSONObject("timings").get("Maghrib").toString();
                                ii = response.getJSONObject("data").getJSONObject("timings").get("Isha").toString();

                                /////////////////////////////////////////////
                                String alaramtime_fajar = ff;
                                String alaramtimesplit_fajar[] = alaramtime_fajar.split(":");
                                int firstval_fajar = Integer.parseInt(alaramtimesplit_fajar[0]);    //4
                                int secondval_fajar = Integer.parseInt(alaramtimesplit_fajar[1]);   //05

                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, firstval_fajar);
                                c.set(Calendar.MINUTE, secondval_fajar);
                                c.set(Calendar.SECOND, 0);

                                if (getApplicationContext() != null) {
                                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                    Intent intent = new Intent(getApplicationContext(), ExecutableService.class);

                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 109833, intent, 0);
                                    if (c.before(Calendar.getInstance())) {
                                        c.add(Calendar.DATE, 1);
                                    }

                                    if (alarmManager != null) {
                                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                                                c.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent); //Repeat every 24 hours
                                        Log.d("Fajar", "onResponse: Alarm Set Fajar");
                                    }
                                }
                                ///////////////////////////////////////////////

                                /////////////////////////////////////////////
                                String alaramtime_zuhar = zz;
                                String alaramtimesplit_zuhar[] = alaramtime_zuhar.split(":");
                                int firstval_zuhar = Integer.parseInt(alaramtimesplit_zuhar[0]);    //4
                                int secondval_zuhar = Integer.parseInt(alaramtimesplit_zuhar[1]);   //05

                                Calendar c2 = Calendar.getInstance();
                                c2.set(Calendar.HOUR_OF_DAY, firstval_zuhar);
                                c2.set(Calendar.MINUTE, secondval_zuhar);
                                c2.set(Calendar.SECOND, 0);

                                if (getApplicationContext() != null) {
                                    AlarmManager alarmManager_zuhar = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                    Intent intent_zuhar = new Intent(getApplicationContext(), ExecutableService.class);

                                    PendingIntent pendingIntent_zuhar = PendingIntent.getBroadcast(getApplicationContext(), 675483, intent_zuhar, 0);
                                    if (c2.before(Calendar.getInstance())) {
                                        c2.add(Calendar.DATE, 1);
                                    }

                                    if (alaramtime_zuhar != null) {
                                        alarmManager_zuhar.setRepeating(AlarmManager.RTC_WAKEUP,
                                                c2.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent_zuhar); //Repeat every 24 hours
                                        //Toast.makeText(getApplicationContext(), "Alarm Set Zuhar!", Toast.LENGTH_SHORT).show();
                                        Log.d("Zuhar", "onResponse: Alarm Set Zuhar");
                                    }
                                }
                                ///////////////////////////////////////////////
                                /////////////////////////////////////////////
                                String alaramtime_asar = aa;
                                String alaramtimesplit_asar[] = alaramtime_asar.split(":");
                                int firstval_asar = Integer.parseInt(alaramtimesplit_asar[0]);//4
                                int secondval_asar = Integer.parseInt(alaramtimesplit_asar[1]);   //05

                                Calendar c3 = Calendar.getInstance();
                                c3.set(Calendar.HOUR_OF_DAY, firstval_asar);
                                c3.set(Calendar.MINUTE, secondval_asar);
                                c3.set(Calendar.SECOND, 0);

                                if (getApplicationContext() != null) {
                                    AlarmManager alarmManager_asar = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                    Intent intent_asar = new Intent(getApplicationContext(), ExecutableService.class);

                                    PendingIntent pendingIntent_asar = PendingIntent.getBroadcast(getApplicationContext(), 768564, intent_asar, 0);
                                    if (c3.before(Calendar.getInstance())) {
                                        c3.add(Calendar.DATE, 1);
                                    }

                                    if (alarmManager_asar != null) {
                                        alarmManager_asar.setRepeating(AlarmManager.RTC_WAKEUP,
                                                c3.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent_asar); //Repeat every 24 hours
                                        //Toast.makeText(getApplicationContext(), "Alarm Set Asar!", Toast.LENGTH_SHORT).show();
                                        Log.d("Asar", "onResponse: Alarm Set Asar");
                                    }
                                }
                                ///////////////////////////////////////////////
                                /////////////////////////////////////////////
                                String alaramtime_maghrib = mm;
                                String alaramtimesplit_maghrib[] = alaramtime_maghrib.split(":");
                                int firstval_maghrib = Integer.parseInt(alaramtimesplit_maghrib[0]);//4
                                int secondval_maghrib = Integer.parseInt(alaramtimesplit_maghrib[1]);   //05

                                Calendar c4 = Calendar.getInstance();
                                c4.set(Calendar.HOUR_OF_DAY, firstval_maghrib);
                                c4.set(Calendar.MINUTE, secondval_maghrib);
                                c4.set(Calendar.SECOND, 0);

                                if (getApplicationContext() != null) {
                                    AlarmManager alarmManager_maghrib = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                    Intent intent_maghrib = new Intent(getApplicationContext(), ExecutableService.class);

                                    PendingIntent pendingIntent_zuhar = PendingIntent.getBroadcast(getApplicationContext(), 980324, intent_maghrib, 0);
                                    if (c4.before(Calendar.getInstance())) {
                                        c4.add(Calendar.DATE, 1);
                                    }

                                    if (alaramtime_maghrib != null) {
                                        alarmManager_maghrib.setRepeating(AlarmManager.RTC_WAKEUP,
                                                c4.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent_zuhar); //Repeat every 24 hours
                                        Log.d("Maghrib", "onResponse: Alarm Set Maghrib");
                                    }
                                }
                                ///////////////////////////////////////////////
                                /////////////////////////////////////////////
                                String alaramtime_isha = ii;
                                String alaramtimesplit_isha[] = alaramtime_isha.split(":");
                                int firstval_isha = Integer.parseInt(alaramtimesplit_isha[0]);//4
                                int secondval_isha = Integer.parseInt(alaramtimesplit_isha[1]);   //05

                                Calendar c5 = Calendar.getInstance();
                                c5.set(Calendar.HOUR_OF_DAY, firstval_isha);
                                c5.set(Calendar.MINUTE, secondval_isha);
                                c5.set(Calendar.SECOND, 0);

                                if (getApplicationContext() != null) {
                                    AlarmManager alarmManager_isha = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                    Intent intent_isha = new Intent(getApplicationContext(), ExecutableService.class);

                                    PendingIntent pendingIntent_isha = PendingIntent.getBroadcast(getApplicationContext(), 764687, intent_isha, 0);
                                    if (c5.before(Calendar.getInstance())) {
                                        c5.add(Calendar.DATE, 1);
                                    }

                                    if (alaramtime_isha != null) {
                                        alarmManager_isha.setRepeating(AlarmManager.RTC_WAKEUP,
                                                c5.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent_isha); //Repeat every 24 hours
                                        Log.d("Isha", "onResponse: Alarm Set Isha");
                                    }
                                }
                                ///////////////////////////////////////////////

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

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
        //txt_View_Date = findViewById(R.id.txt_View_Date);
        //txt_View_Day = findViewById(R.id.txt_View_Day);
        tvSunrise = findViewById(R.id.tv_sunrise);
        tvSunset = findViewById(R.id.tv_sunset);
        fajar_fajar = findViewById(R.id.fajar);
        zuhur_zuhar = findViewById(R.id.Zuhar);
        asar_asar = findViewById(R.id.Asar);
        maghrib_maghrib = findViewById(R.id.Maghrib);
        isha_isha = findViewById(R.id.Isha);
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
                    if (gpsTracker.canGetLocation()) {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
                            address = addresses.get(0).getAddressLine(0);
                            Log.d("Location_Address", "onLocationChanged: " + address);
                            getResponse(address);
                            String city = addresses.get(0).getLocality();
                            String country = addresses.get(0).getCountryName();
                            tvLocation1.setText(city + " " + country);
                        } catch (
                                IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
}