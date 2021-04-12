package com.gpfei.graduationproject.ui.activities.hr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.ui.activities.common.EditUserInfoActivity;
import com.gpfei.graduationproject.ui.activities.common.MyDataActivity;
import com.gpfei.graduationproject.ui.activities.common.login.LoginAndRegisterActivity;
import com.gpfei.graduationproject.utils.SmileToast;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class HrCheckUserInfoActivity extends AppCompatActivity {
    private ImageView iv_back;
    private TextView tv_title;
    private TextView tv_name,tv_phone,tv_sex,tv_birth,tv_qq,tv_email,tv_induce,tv_experience;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr_check_user_info);
        initView();
        loadData();
    }

    private void initView() {
        fab = findViewById(R.id.fab);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("求职者信息");

        tv_phone = findViewById(R.id.tv_phone);
        tv_name = findViewById(R.id.tv_name);
        tv_sex = findViewById(R.id.tv_sex);
        tv_birth = findViewById(R.id.tv_birthday);
        tv_qq = findViewById(R.id.tv_qq);
        tv_email = findViewById(R.id.tv_email);
        tv_induce = findViewById(R.id.tv_induce);
        tv_experience = findViewById(R.id.tv_experience);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HrCheckUserInfoActivity.this, "fab被点击了", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        Intent intent = getIntent();
        String objectId = intent.getStringExtra("objectId");
        if (objectId !=null){
            new Thread() {
                public void run() {
                    //执行耗时操作
                    queryOne(objectId);
                }
            }.start();
        }else {
            Toast.makeText(this, "object为空", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 查询一个对象
     */
    private void queryOne(String mObjectId) {
        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(mObjectId, new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null) {
                    tv_name.setText(myUser.getName());
                    tv_phone.setText(myUser.getMobilePhoneNumber());
                    tv_email.setText(myUser.getEmail());
                    tv_experience.setText(myUser.getExperience());
                    tv_induce.setText(myUser.getProfile());
                    tv_qq.setText(myUser.getQq().toString());
                    //问题
                    if (myUser.getSex()==null){
                        ToastUtils.showTextToast(HrCheckUserInfoActivity.this,"性别为空");
                    }else {
                        if (myUser.getSex()){
                            tv_sex.setText("男");
                        }else {
                            tv_sex.setText("女");
                        }
                    }
                    tv_experience.setText(myUser.getExperience());
                    tv_birth.setText(myUser.getBirthday());

                    Log.d("cehngg",myUser.getUsername());
                } else {
                    Toast.makeText(HrCheckUserInfoActivity.this, "错误"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}