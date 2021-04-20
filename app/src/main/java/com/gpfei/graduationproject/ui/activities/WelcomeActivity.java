package com.gpfei.graduationproject.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.ui.activities.common.MainActivity;
import com.gpfei.graduationproject.ui.activities.common.login.LoginAndRegisterActivity;
import com.gpfei.graduationproject.ui.activities.hr.HrMainActivity;
import com.gpfei.graduationproject.utils.SmileToast;
import com.hyphenate.chat.EMClient;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;


/**
 * 开屏广告，延迟两秒进入Main
 * 先查询出是否为HR，然后判断跳转不同的activity
 */

public class WelcomeActivity extends AppCompatActivity {

    private final long SPLASH_LENGTH = 2000;
    Timer timer = new Timer();
    private Boolean isHR = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobUser bmobUser = BmobUser.getCurrentUser(User.class);
                if (bmobUser!=null){
                    //查找Person表里面id为6b6c11c537的数据
                    BmobQuery<User> bmobQuery = new BmobQuery<User>();
                    bmobQuery.getObject(bmobUser.getObjectId(), new QueryListener<User>() {
                        @Override
                        public void done(User object, BmobException e) {
                            if(e==null){
                                isHR = object.getHR();
                            }else{

                            }
                        }
                    });
                }

            }
        }).start();
        timer.schedule(new TimerTask() {
            /**
             * 首先对环信服务器进行判断，
             *然后对Bmob进行登录判读
             */
            @Override
            public void run() {
               // startActivity(new Intent(WelcomeActivity.this,MessageActivity.class));
                new Thread(){
                    @Override
                    public void run() {
                        if (EMClient.getInstance().isLoggedInBefore()){
                            //环信已登录
                            BmobUser bmobUser = BmobUser.getCurrentUser(User.class);
                            if (bmobUser!=null){
                                if (isHR){
                                    Intent intent = new Intent(WelcomeActivity.this, HrMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                                        Toast.makeText(WelcomeActivity.this, "Bmob未登录", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }else {
                            //环信未登录
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SmileToast smileToast = new SmileToast();
                                    smileToast.smile("请先登录");
                                    startActivity(new Intent(WelcomeActivity.this,LoginAndRegisterActivity.class));
                                }
                            });
                        }
                    }
                }.start();
            }
        }, SPLASH_LENGTH);
    }
}