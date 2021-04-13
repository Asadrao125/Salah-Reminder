package com.gexton.namazalert.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gexton.namazalert.AdsManager.SingletonAds;
import com.gexton.namazalert.R;

import static com.gexton.namazalert.AdsManager.AdsKt.showBanner;

public class ZakaatCalculator extends AppCompatActivity {
    Button btnCalculate;
    EditText edtAmount;
    TextView tvZakaatValue, tvValue;
    ImageView imgBack;
    Button btnDone;
    EditText edtGold, edtGoldPrice;
    RadioButton rbGold, rbSilver, rbCash;
    LinearLayout gold_layout, amount_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaat_calculator);

        btnCalculate = findViewById(R.id.btnCalculate);
        edtAmount = findViewById(R.id.edtAmount);
        tvZakaatValue = findViewById(R.id.tvZakaatValue);
        imgBack = findViewById(R.id.imgBack);
        gold_layout = findViewById(R.id.gold_layout);
        rbGold = findViewById(R.id.rbGold);
        rbSilver = findViewById(R.id.rbSilver);
        rbCash = findViewById(R.id.rbCash);
        edtGold = findViewById(R.id.edtGold);
        edtGoldPrice = findViewById(R.id.edtGoldPrice);
        btnDone = findViewById(R.id.btnDone);
        amount_layout = findViewById(R.id.amount_layout);
        tvValue = findViewById(R.id.tvValue);

        if (rbGold.isChecked()) {
            edtGold.setHint("Gold");
            edtGoldPrice.setHint("Gold Price");
        }

        rbGold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    gold_layout.setVisibility(View.VISIBLE);
                    amount_layout.setVisibility(View.GONE);
                    edtGold.setHint("Gold");
                    edtGoldPrice.setHint("Gold Price");
                }
            }
        });

        rbSilver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    gold_layout.setVisibility(View.VISIBLE);
                    amount_layout.setVisibility(View.GONE);
                    edtGold.setHint("Silver");
                    edtGoldPrice.setHint("Silver Price");
                }
            }
        });

        rbCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    gold_layout.setVisibility(View.GONE);
                    amount_layout.setVisibility(View.VISIBLE);
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double gold = Long.parseLong(edtGold.getText().toString().trim());
                double price = Long.parseLong(edtGoldPrice.getText().toString().trim());
                if (gold != 0.0 && price != 0.0) {
                    double n = gold * price;
                    calculateZakat2(n);
                }
            }
        });

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

    private void calculateZakat2(double amount) {
        double zakaat = ((2.5 * amount) / 100);
        tvValue.setText("Zakaat: " + zakaat);
        tvValue.setVisibility(View.VISIBLE);
    }
}