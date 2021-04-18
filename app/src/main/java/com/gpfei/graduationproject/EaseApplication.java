package com.gpfei.graduationproject;

import android.app.Application;

import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;

import cn.bmob.v3.Bmob;

public class EaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //bmob初始化key
        Bmob.initialize(this, "833216a424abc46085b12199c637bf38");
        EMOptions options = new EMOptions();
        EaseUI.getInstance().init(this,options);
    }
}
