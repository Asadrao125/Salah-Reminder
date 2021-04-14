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
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.gexton.namazalert.AdsManager.AdsKt.showBanner;

public class TasbeehActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private static final String S_MAIN_COUNT = "mainCount";
    private static final String S_PROG_COUNT = "progressCount";
    private static final String S_CUMMU_COUNT = "cummulativeCount";
    private static final String S_TARGET_ZIKR = "targetZikr";
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
    public View parentLayout;
    ImageView imgBack;
    CardView cvParent, cvReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasbeeh);

        parentLayout = findViewById(R.id.parent_layout);
        countText = findViewById(R.id.text_zikr);
        buttonCount = findViewById(R.id.button_count);
        resetButton = findViewById(R.id.button_reset);
        pb = findViewById(R.id.pb);
        targetText = findViewById(R.id.textView_progress_target);
        cummulativeText = findViewById(R.id.textView_cummulative_count);
        targetText.setText("Target: " + String.valueOf(targetZikr));
        pb.setMax(targetZikr);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        imgBack = findViewById(R.id.imgBack);
        cvParent = findViewById(R.id.cvParent);
        cvReset= findViewById(R.id.cvReset);

        SingletonAds.Companion.init(this);
        FrameLayout banner_container = findViewById(R.id.ad_view_container);
        showBanner(this, banner_container);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

        cvParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonCount.performClick();
            }
        });

        pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonCount.performClick();
            }
        });

        countText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonCount.performClick();
            }
        });
    }

    /*void changeThemeMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }*/

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
            cummulativeText.setText("Round 0");
            //resetButton.setVisibility(View.INVISIBLE);
            //cvReset.setVisibility(View.GONE);

            if (VERSION.SDK_INT >= VERSION_CODES.N) {
                pb.setProgress(0, true);
            } else {
                pb.setProgress(0);
            }

            showSnackBar(parentLayout, "Reset done");
        } else
            showSnackBar(parentLayout, "Canceled. Nothing changed");

    }

    public void updateProgressBar() {
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            pb.setProgress(progressCounter, true);
        } else {
            pb.setProgress(progressCounter);
        }
    }

    public void openResetDialog() {
        ResetDialog resetDialog = new ResetDialog(this);
        resetDialog.show(getSupportFragmentManager(), "reset dialog");
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

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (oldVal != newVal) {
            showSnackBar(parentLayout, "Target number changed to " + newVal);
            targetZikr = newVal;
            targetText.setText("Target: " + String.valueOf(targetZikr));
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
}