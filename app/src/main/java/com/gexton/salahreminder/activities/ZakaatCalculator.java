package com.gexton.salahreminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gexton.salahreminder.AdsManager.SingletonAds;
import com.gexton.salahreminder.R;

import static com.gexton.salahreminder.AdsManager.AdsKt.showBanner;

public class ZakaatCalculator extends AppCompatActivity {
    Button btnCalculate;
    EditText edtAmount;
    TextView tvZakaatValue;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaat_calculator);

        btnCalculate = findViewById(R.id.btnCalculate);
        edtAmount = findViewById(R.id.edtAmount);
        tvZakaatValue = findViewById(R.id.tvZakaatValue);
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

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amount = edtAmount.getText().toString().trim();
                if (TextUtils.isEmpty(amount)) {
                    edtAmount.setError("Empty");
                    edtAmount.requestFocus();
                } else {
                    calculateZakat(amount);
                }
            }
        });
    }

    private void calculateZakat(String amount) {
        double newAmount = Double.parseDouble(amount);
        double zakaat = ((2.5 * newAmount) / 100);
        tvZakaatValue.setVisibility(View.VISIBLE);
        tvZakaatValue.setText("Zakaat Value: " + zakaat);
    }
}