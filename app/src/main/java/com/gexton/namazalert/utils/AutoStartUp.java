package com.gexton.namazalert.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AutoStartUp extends BroadcastReceiver {
    String ff, aa, zz, mm, ii, address;
    GPSTracker gpsTracker;

    @Override
    public void onReceive(Context context, Intent intent) {
        gpsTracker = new GPSTracker(context);
        System.out.println("-- onRecieve Boot Completed Called");
        Toast.makeText(context, "onRecieve Boot Completed Called.", Toast.LENGTH_SHORT).show();
        if (gpsTracker.canGetLocation) {
            getNamazTimings(context);
        }
    }

    private void getResponse(String addres, Context context) {
        if (context != null) {
            String url = "http://api.aladhan.com/v1/timingsByAddress?address=" + addres;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d("response_msg", "onResponse: " + response);
                        ff = response.getJSONObject("data").getJSONObject("timings").get("Fajr").toString();
                        zz = response.getJSONObject("data").getJSONObject("timings").get("Dhuhr").toString();
                        aa = response.getJSONObject("data").getJSONObject("timings").get("Asr").toString();
                        mm = response.getJSONObject("data").getJSONObject("timings").get("Maghrib").toString();
                        ii = response.getJSONObject("data").getJSONObject("timings").get("Isha").toString();
                        setAlarm(ff, 109833, "fajar", context);
                        setAlarm(zz, 675483, "zuhar", context);
                        setAlarm(aa, 768564, "asar", context);
                        setAlarm(mm, 980324, "maghrib", context);
                        setAlarm(ii, 764687, "isha", context);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                    Toast.makeText(context, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            Volley.newRequestQueue(context).add(jsonObjReq);
        }
    }

    private void setAlarm(String namazTime, int requestCode, String namazName, Context context) {
        String[] alaram_time_split = namazTime.split(":");  // 04:05
        int firstval_fajar = Integer.parseInt(alaram_time_split[0]);
        int secondval_fajar = Integer.parseInt(alaram_time_split[1]);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, firstval_fajar);
        c.set(Calendar.MINUTE, secondval_fajar);
        c.set(Calendar.SECOND, 0);

        if (context != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ExecutableService.class);
            intent.putExtra("val", namazName);
            intent.putExtra("h", firstval_fajar);
            intent.putExtra("m", secondval_fajar);
            intent.putExtra("r", requestCode);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);

            if (c.before(Calendar.getInstance())) {
                c.add(Calendar.DATE, 1);
            }

            if (am != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT <= 20) {
                    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT > 20 && Build.VERSION.SDK_INT <= 23) {
                    am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }

    private void getNamazTimings(Context context) {
        if (gpsTracker.canGetLocation()) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
                address = addresses.get(0).getAddressLine(0);
                Log.d("Location_Address", "onLocationChanged: " + address);
                getResponse(address, context);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}