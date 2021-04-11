package com.gpfei.graduationproject.ui.activities.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.SignInBean;
import com.gpfei.graduationproject.beans.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyIntegralActivity extends AppCompatActivity {
    private ImageView iv_back;
    private TextView tv_title;
    private TextView tv_integral;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_integral);
        initView();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_integral = findViewById(R.id.tv_integral);
        tv_title.setText("我的积分");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        queryIntegral();
    }


    /**
     * 查询一对一关联，查询当前用户发表的所有帖子
     */
    private void queryIntegral() {
        if (BmobUser.isLogin()) {
            BmobQuery<SignInBean> query = new BmobQuery<>();
            query.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
            query.order("-updatedAt");
            //包含作者信息
            query.include("intergal");
            query.findObjects(new FindListener<SignInBean>() {
                @Override
                public void done(List<SignInBean> object, BmobException e) {
                    if (e == null) {
                        Log.d("object.size()",object.size()+"");
                        Log.d("object.size()",object.get(0).getIntergal()+"");
                        tv_integral.setText("我的积分: "+object.get(0).getIntergal());
                    } else {
                        Log.e("BMOB", e.toString());
                    }
                }

            });
        } else {
        }


    }

}