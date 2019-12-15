package com.example.beeon;


import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;


public class MainActivity extends AppCompatActivity {

    Button mButtonQrScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mButtonQrScan = findViewById(R.id.btn_qr_scan);
        mButtonQrScan.setOnClickListener(new View.OnClickListener() {
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



}
