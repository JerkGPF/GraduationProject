package com.gpfei.graduationproject.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.ui.activities.common.MainActivity;
import com.gpfei.graduationproject.ui.activities.hr.HrMainActivity;

import cn.bmob.v3.Bmob;


/**
 * 开屏广告，延迟两秒进入Main
 */

public class WelcomeActivity extends AppCompatActivity {

    private final long SPLASH_LENGTH = 2000;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //bmob初始化key
        Bmob.initialize(this, "833216a424abc46085b12199c637bf38");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_LENGTH);
    }
}