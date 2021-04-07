package com.google.salahreminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.salahreminder.R;

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