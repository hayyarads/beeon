package com.example.beeon;


import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MainActivity extends AppCompatActivity {

    Button mButtonQrScan;
    Button mButtonControl;
    LinearLayout mLineardate;
    ImageView mImageposter;
    TextView TextViewQueue;

    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mButtonQrScan = findViewById(R.id.btn_qr_scan);
        mButtonControl = findViewById(R.id.button_control);
        mButtonQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        mLineardate = findViewById(R.id.linear_dates);

        TextViewQueue = findViewById(R.id.tetview_queue);

        mButtonControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Boshqaruv.class);
                startActivity(intent);
            }
        });
        mImageposter = findViewById(R.id.poster);

        mImageposter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    private void checkPermission() {
        Permissions.check(this/*context*/, Manifest.permission.CAMERA, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                Intent intent = new Intent(MainActivity.this,QrScanActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

     mImageposter.setImageDrawable(getResources().getDrawable(R.drawable.qr));
     mLineardate.setVisibility(View.VISIBLE);
    counter++;
    TextViewQueue.setVisibility(View.VISIBLE);

    TextViewQueue.setText("#"+counter);

    }
}
