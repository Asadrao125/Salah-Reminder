package com.gexton.namazalert.activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gexton.namazalert.AdsManager.SingletonAds;
import com.gexton.namazalert.R;
import com.gexton.namazalert.tasbeeh_files.AboutDialog;
import com.gexton.namazalert.tasbeeh_files.NotificationBuilder;
import com.gexton.namazalert.tasbeeh_files.ResetDialog;
import com.gexton.namazalert.tasbeeh_files.SetTargetPicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.gexton.namazalert.AdsManager.AdsKt.showBanner;

public class TasbeehActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private static final String S_MAIN_COUNT = "mainCount"; //utk SharedPreference
    private static final String S_PROG_COUNT = "progressCount"; //utk SharedPreference
    private static final String S_CUMMU_COUNT = "cummulativeCount"; //utk SharedPreference
    private static final String S_TARGET_ZIKR = "targetZikr"; //target zikir counter tepi progress bar
    private static final String TAG = "MainActivity";
    private TextView countText;
    private TextView cummulativeText;
    private TextView targetText;
    private Button buttonCount;
    private Button resetButton;
    private ProgressBar pb;
    public int countZikr = 0;
    public int targetZikr = 10;
    private int progressCounter = 0;
    private int cummulativeRound;
    private long backPressedTimer;
    public View parentLayout;
    private NotificationManagerCompat notificationManager;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasbeeh);
        parentLayout = findViewById(R.id.parent_layout);

        notificationManager = NotificationManagerCompat.from(this);
        countText = findViewById(R.id.text_zikr);
        buttonCount = findViewById(R.id.button_count);
        resetButton = findViewById(R.id.button_reset);
        //progressBar = findViewById(R.id.progressBar);
        pb = findViewById(R.id.pb);
        targetText = findViewById(R.id.textView_progress_target);
        cummulativeText = findViewById(R.id.textView_cummulative_count);
        targetText.setText("Target: " + String.valueOf(targetZikr));
        //progressBar.setMax(targetZikr);
        pb.setMax(targetZikr);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        imgBack = findViewById(R.id.imgBack);

        SingletonAds.Companion.init(this);
        FrameLayout banner_container = findViewById(R.id.ad_view_container);
        showBanner(this, banner_container);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        countText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyText(countText.getText());
                vibrateFeedback(55);
                showSnackBar(parentLayout, "Copied!");
                return true;
            }
        });

        buttonCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementCount();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countZikr != 0)
                    openResetDialog();
            }
        });

        targetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTargetDialog();
            }
        });

        cummulativeText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyText(cummulativeText.getText());
                vibrateFeedback(55);
                showSnackBar(parentLayout, "Copied!");
                return true;
            }
        });

        findViewById(R.id.button_SetTarget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTargetDialog();
            }
        });
    }

    private void copyText(CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", text);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //add action2 kau kat sini
        switch (item.getItemId()) {
            case R.id.action_change_theme:
                changeThemeMode();
                return true;
            case R.id.action_share:
                shareValueToOtherApp();
                return true;
            case R.id.action_item_1: //about
                openAboutDialog();
                return true;
            case R.id.action_item_3: //setTargetValue
                openTargetDialog();
                return true;
            case R.id.action_item_4: //showNotifs
                showOnNotification();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareValueToOtherApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        String message; //customize message here
        message = "As of " + getCurrentDateTime() + ", ";
        if (countZikr == 0)
            message = message + "I didn't make any progress yet";
        else
            message = message + "I made till " + countZikr + ".";

        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void openCustomTabs(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimaryDark));
        builder.setShowTitle(true);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    void changeThemeMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    public void incrementCount() {
        buttonCount.setText("+1");
        countZikr++;
        countText.setText(String.valueOf(countZikr));
        progressCounter++;
        updateProgressBar();

        resetButton.setVisibility(View.VISIBLE);

        if (progressCounter == targetZikr) {
            progressCounter = 0;
            cummulativeRound += 1;
            cummulativeText.setText("Round: " + cummulativeRound);
            vibrateFeedback(170);
        }
    }

    public void resetCount(Boolean proceed) { //attached to reset button kat bawah tu
        if (proceed) {
            countZikr = 0;
            progressCounter = 0;
            countText.setText("0");
            buttonCount.setText("START");
            cummulativeRound = 0;
            cummulativeText.setText("");
            resetButton.setVisibility(View.INVISIBLE);

            if (VERSION.SDK_INT >= VERSION_CODES.N) {
                //progressBar.setProgress(0, true); //set progress bar balik ke 0
                pb.setProgress(0, true);
            } else {
                //progressBar.setProgress(0); //no animation
                pb.setProgress(0);
            }

            showSnackBar(parentLayout, "Reset done");
        } else
            showSnackBar(parentLayout, "Canceled. Nothing changed");

    }

    public void updateProgressBar() {

        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            //progressBar.setProgress(progressCounter, true);
            pb.setProgress(progressCounter, true);
        } else {
            //progressBar.setProgress(progressCounter); //no animation
            pb.setProgress(progressCounter);
        }
    }

    public void openResetDialog() {
        ResetDialog resetDialog = new ResetDialog(this);
        resetDialog.show(getSupportFragmentManager(), "reset dialog");
    }

    public void openAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.show(getSupportFragmentManager(), "about dialog");
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(S_MAIN_COUNT, countZikr);
        editor.putInt(S_PROG_COUNT, progressCounter);
        editor.putInt(S_CUMMU_COUNT, cummulativeRound);
        editor.putInt(S_TARGET_ZIKR, targetZikr);

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        countZikr = prefs.getInt(S_MAIN_COUNT, 0);
        progressCounter = prefs.getInt(S_PROG_COUNT, 0);
        cummulativeRound = prefs.getInt(S_CUMMU_COUNT, 0);
        targetZikr = prefs.getInt(S_TARGET_ZIKR, 10);

        //progressBar.setMax(targetZikr);
        pb.setMax(targetZikr);
        updateProgressBar();

        targetText.setText("Target: " + String.valueOf(targetZikr));

        cummulativeText.setText("Round: " + cummulativeRound);

        if (countZikr > 0) {
            buttonCount.setText("+1");
            resetButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        countText.setText(String.valueOf(countZikr));
    }

    public void openTargetDialog() {
        SetTargetPicker newFragment = new SetTargetPicker();
        newFragment.setValueChangeListener(this);
        newFragment.show(getSupportFragmentManager(), "target picker");
    }

    public void showSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private String getCurrentDateTime() {
        String pattern = "dd/MM/yy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    @Override
    public void onBackPressed() {
        /*long millisToExit = 2000;
        if (backPressedTimer + millisToExit > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTimer = System.currentTimeMillis();*/
        super.onBackPressed();
    }

    public void ViewAndroidBuildNum(View view) {
        Log.d(TAG, "ViewAndroidBuildNum: is" + VERSION.SDK_INT);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (oldVal != newVal) {
            showSnackBar(parentLayout, "Target number changed to " + newVal);
            targetZikr = newVal;
            targetText.setText("Target: " + String.valueOf(targetZikr));
            //progressBar.setMax(targetZikr);
            pb.setMax(targetZikr);
            cummulativeRound = progressCounter = 0;
            cummulativeText.setText("0");
        } else {
            showSnackBar(parentLayout, "Nothing changed. Target value is " + oldVal);
        }
    }

    private void vibrateFeedback(long millis) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(millis);
            }
        }
    }

    public void showOnNotification() {
        finish();
        String title = "Current counter";

        Intent activityIntent = new Intent(this, TasbeehActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, NotificationBuilder.CHANNEL_ID_1)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(String.valueOf(countZikr))
                .setColor(Color.rgb(230, 28, 98))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(1, notification);
    }
}