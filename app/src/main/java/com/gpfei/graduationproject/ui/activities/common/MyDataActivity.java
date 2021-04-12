package com.gpfei.graduationproject.ui.activities.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyDataActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private TextView tv_title;
    private TextView tv_to_edit;
    private ImageView iv_user_head;
    private TextView tv_name,tv_phone,tv_sex,tv_birth,tv_qq,tv_email,tv_induce,tv_experience;
    private PullToRefreshLayout refresh_mydata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_data);
        initView();
        showUserInfo();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("基本资料");
        tv_to_edit = findViewById(R.id.tv_to_edit);
        iv_back.setOnClickListener(this);
        tv_to_edit.setOnClickListener(this);
        iv_user_head = findViewById(R.id.iv_user_head);
        iv_user_head.setOnClickListener(this);

        tv_phone = findViewById(R.id.tv_phone);
        tv_name = findViewById(R.id.tv_name);
        tv_sex = findViewById(R.id.tv_sex);
        tv_birth = findViewById(R.id.tv_birthday);
        tv_qq = findViewById(R.id.tv_qq);
        tv_email = findViewById(R.id.tv_email);
        tv_induce = findViewById(R.id.tv_induce);
        tv_experience = findViewById(R.id.tv_experience);
        refresh_mydata = findViewById(R.id.refresh_mydata);


        refresh_mydata.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showUserInfo();
                        //结束刷新
                        refresh_mydata.finishRefresh();
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showTextToast(getApplicationContext(),"没有更多内容了哟~");
                        //结束加载更多
                        refresh_mydata.finishLoadMore();
                    }
                }, 2000);

            }
        });
    }

    //显示用户资料
    private void showUserInfo() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            //获取头像地址
            if (user.getHead() != null) {
                //圆形头像
                Glide.with(MyDataActivity.this).load(user.getHead().toString()).asBitmap().centerCrop().into(new BitmapImageViewTarget(iv_user_head) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(MyDataActivity.this.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        iv_user_head.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e==null){
                    tv_name.setText(user.getName());
                    tv_phone.setText(user.getMobilePhoneNumber());
                    tv_email.setText(user.getEmail());
                    tv_experience.setText(user.getExperience());
                    tv_induce.setText(user.getProfile());
                    tv_qq.setText(user.getQq().toString());
                    //问题
                    if (user.getSex()==null){
                        ToastUtils.showTextToast(MyDataActivity.this,"性别为空");
                    }else {
                        if (user.getSex()){
                            tv_sex.setText("男");
                        }else {
                            tv_sex.setText("女");
                        }
                    }
                    tv_experience.setText(user.getExperience());
                    tv_birth.setText(user.getBirthday());
                }

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_to_edit:
                startActivity(new Intent(MyDataActivity.this,UpdateUserInfoActivity.class));
                break;
            case R.id.iv_back:
                finish();
                break;
        }

    }
}