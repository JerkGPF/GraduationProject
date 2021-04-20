package com.gpfei.graduationproject.ui.activities.hr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.HrUser;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class HrDataActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private TextView tv_title;
    private TextView tv_to_edit;
    private ImageView iv_company_head;
    private TextView tv_company_name,tv_company_phone,tv_company_birth,
            tv_company_email,tv_company_induce,tv_company_benfits;
    private PullToRefreshLayout refresh_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr_data);
        initView();
        showCompanyInfo();
    }



    private void initView() {
        iv_back =findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("公司基本资料");
        tv_to_edit = findViewById(R.id.tv_to_edit);
        iv_company_head = findViewById(R.id.iv_company_head);
        tv_company_name = findViewById(R.id.tv_company_name);
        tv_company_phone = findViewById(R.id.tv_company_phone);
        tv_company_birth = findViewById(R.id.tv_company_birthday);
        tv_company_email = findViewById(R.id.tv_company_email);
        tv_company_induce = findViewById(R.id.tv_company_induce);
        tv_company_benfits = findViewById(R.id.tv_company_benfits);
        iv_back.setOnClickListener(this);
        tv_to_edit.setOnClickListener(this);
        refresh_data = findViewById(R.id.refresh_data);



        refresh_data.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showCompanyInfo();
                        //结束刷新
                        refresh_data.finishRefresh();
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
                        refresh_data.finishLoadMore();
                    }
                }, 2000);

            }
        });
    }
    private void showCompanyInfo() {
        if (BmobUser.isLogin()) {
            BmobQuery<HrUser> query = new BmobQuery<>();
            query.addWhereEqualTo("userInfo", BmobUser.getCurrentUser(User.class));
            query.order("-updatedAt");
            //包含作者信息
            query.include("author");
            query.findObjects(new FindListener<HrUser>() {
                @Override
                public void done(List<HrUser> object, BmobException e) {
                    if (e == null) {
                        tv_company_name.setText(object.get(0).getCompany_name());
                        tv_company_phone.setText(object.get(0).getCompany_phone());
                        tv_company_birth.setText(object.get(0).getCompany_birthday());
                        tv_company_email.setText(object.get(0).getCompany_email());
                        tv_company_induce.setText(object.get(0).getCompany_introduce());
                        tv_company_benfits.setText(object.get(0).getCompany_free());
                        Log.d("companyInfo", object.get(0).getCompany_email());
                    } else {

                        Log.d("companyInfo", e.toString());
                    }
                }
            });
        } else {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_to_edit:

                startActivity(new Intent(HrDataActivity.this,HrEditInfoActivity.class));
        }

    }
}