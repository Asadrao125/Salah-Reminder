package com.gexton.salahreminder.names_list;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gexton.salahreminder.AdsManager.SingletonAds;
import com.gexton.salahreminder.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.gexton.salahreminder.AdsManager.AdsKt.showBanner;

public class ListData extends AppCompatActivity {

    int pos;
    ImageView image, imgBack;
    TextView name, meaning, description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_data);

        image = (ImageView) findViewById(R.id.imageShow);
        name = (TextView) findViewById(R.id.textName);
        meaning = (TextView) findViewById(R.id.textMeaning);
        description = (TextView) findViewById(R.id.textDescription);
        imgBack = findViewById(R.id.imgBack);

        SingletonAds.Companion.init(this);
        FrameLayout banner_container = findViewById(R.id.ad_view_container);
        showBanner(this, banner_container);

        pos = getIntent().getIntExtra("Position", -1);
        Log.e("position", String.valueOf(pos));

        image.setImageResource(HelperClass.imageLarge[pos]);
        name.setText(HelperClass.names[pos]);
        meaning.setText(HelperClass.meaning[pos]);
        description.setText(HelperClass.description[pos]);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}
