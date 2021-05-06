package com.gpfei.graduationproject.ui.activities.hr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.HrUser;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.beans.SelectionBean;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.beans.WeekendBean;
import com.gpfei.graduationproject.ui.activities.common.EditUserInfoActivity;
import com.gpfei.graduationproject.ui.activities.common.login.LoginAndRegisterActivity;
import com.gpfei.graduationproject.ui.fragments.hr.HrIndexFragment;
import com.gpfei.graduationproject.utils.SmileToast;
import com.gpfei.graduationproject.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.http.I;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private EditText et_hr_company_job;
    private EditText et_hr_company_salary;
    private EditText et_hr_company_name;
    private EditText et_hr_company_place;
    private EditText et_hr_company_url;
    private TextView tv_title;
    private CheckBox cb_full,cb_part,cb_pratice;


    private Button btn;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        new Thread(new Runnable() {
            @Override
            public void run() {
                equal();
            }
        }).start();
        initView();
    }

    private void initView() {
        cb_full = findViewById(R.id.cb_full);
        cb_part = findViewById(R.id.cb_part);
        cb_pratice = findViewById(R.id.cb_pratice);
        cancel = findViewById(R.id.cancel);
        et_hr_company_job = findViewById(R.id.et_hr_company_job);
        et_hr_company_salary = findViewById(R.id.et_hr_company_salary);
        et_hr_company_name = findViewById(R.id.et_hr_company_name);
        et_hr_company_place = findViewById(R.id.et_hr_company_place);
        et_hr_company_url = findViewById(R.id.et_hr_company_url);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        btn = findViewById(R.id.submit);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("职位发布");
        btn.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    //判断输入是否为空
    private Boolean isEmpty() {
        String job = et_hr_company_job.getText().toString().trim();
        String salary = et_hr_company_salary.getText().toString().trim();
        String name = et_hr_company_name.getText().toString().trim();
        String place = et_hr_company_place.getText().toString().trim();
        String url = et_hr_company_url.getText().toString().trim();
        if (TextUtils.isEmpty(job)) {
            Toast.makeText(this, "职位不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(salary)) {
            Toast.makeText(this, "薪资不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "公司名称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(place)) {
            Toast.makeText(this, "工作地点不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "工作详情不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
            case R.id.cancel:
                finish();
                break;
            case R.id.submit:
                if (isEmpty()){
                    if (cb_part.isChecked()){
                        // 耗时操作要在子线程中操作
                        new Thread() {
                            public void run() {
                                //执行耗时操作
                                savePartJob();
                                //更新主线程UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                        Log.d("兼职", "onClick:兼职 ");
                                    }
                                });
                            }
                        }.start();
                    }
                    if (cb_full.isChecked()){
                        saveFullJob();
                        Log.d("兼职", "onClick:全职 ");
                    }
                    if (cb_pratice.isChecked()){
                        savePraticeJob();
                        Log.d("兼职", "onClick:实习 ");
                    }
                }
                //saveJob();
                break;
        }
    }



    /**
     * 添加一对一关联，当前用户发布职位
     * 全职
     */
    private void saveFullJob() {
        String salary = et_hr_company_salary.getText().toString().trim();
        Double temp = Double.parseDouble(salary)/1000;
        DecimalFormat df = new DecimalFormat("0.0");
        salary = df.format(temp);
        if (BmobUser.isLogin()) {
            DayBean dayBean = new DayBean();
            dayBean.setTitle_day(et_hr_company_job.getText().toString().trim());
            dayBean.setCompany_day(et_hr_company_name.getText().toString().trim());
            dayBean.setAddress_day(et_hr_company_place.getText().toString().trim());
            dayBean.setMoney_day(salary+"K");
            dayBean.setUrl(et_hr_company_url.getText().toString().trim());
            //添加一对一关联，用户关联发表职位
            dayBean.setAuthor(BmobUser.getCurrentUser(HrUser.class));
            dayBean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        SmileToast smileToast = new SmileToast();
                        smileToast.smile("职位发布成功");
                        finish();
                    } else {
                        Log.e("BMOB", e.toString());
                        Toast.makeText(PublishActivity.this, "出现问题" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(PublishActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
        }
    }

    //兼职
    private void savePartJob() {
        String salary = et_hr_company_salary.getText().toString().trim();
        Double temp = Double.parseDouble(salary)/1000;
        DecimalFormat df = new DecimalFormat("0.0");
        salary = df.format(temp);
        if (BmobUser.isLogin()) {
            WeekendBean weekendBean = new WeekendBean();
            weekendBean.setTitle_weekend(et_hr_company_job.getText().toString().trim());
            weekendBean.setCompany_weekend(et_hr_company_name.getText().toString().trim());
            weekendBean.setAddress_weekend(et_hr_company_place.getText().toString().trim());
            weekendBean.setMoney_weekend(salary+"K");
            weekendBean.setUrl(et_hr_company_url.getText().toString().trim());
            //添加一对一关联，用户关联发表职位
            weekendBean.setAuthor(BmobUser.getCurrentUser(HrUser.class));
            weekendBean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        SmileToast smileToast = new SmileToast();
                        smileToast.smile("职位发布成功");
                        finish();
                    } else {
                        Log.e("BMOB", e.toString());
                        Toast.makeText(PublishActivity.this, "出现问题" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(PublishActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
        }
    }
    //实习
    private void savePraticeJob() {
        String salary = et_hr_company_salary.getText().toString().trim();
        Double temp = Double.parseDouble(salary)/1000;
        DecimalFormat df = new DecimalFormat("0.0");
        salary = df.format(temp);        if (BmobUser.isLogin()) {
            SelectionBean selectionBean = new SelectionBean();
            selectionBean.setTitle_selection(et_hr_company_job.getText().toString().trim());
            selectionBean.setCompany_selection(et_hr_company_name.getText().toString().trim());
            selectionBean.setAddress_selection(et_hr_company_place.getText().toString().trim());
            selectionBean.setMoney_selection(salary+"K");
            selectionBean.setUrl_selection(et_hr_company_url.getText().toString().trim());
            //添加一对一关联，用户关联发表职位
            selectionBean.setAuthor(BmobUser.getCurrentUser(HrUser.class));
            selectionBean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        SmileToast smileToast = new SmileToast();
                        smileToast.smile("职位发布成功");
                        finish();
                    } else {
                        Log.e("BMOB", e.toString());
                        Toast.makeText(PublishActivity.this, "出现问题" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(PublishActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * name为football的类别
     */
    private void equal() {
        BmobQuery<HrUser> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("userInfo", BmobUser.getCurrentUser(User.class));
        bmobQuery.findObjects(new FindListener<HrUser>() {
            @Override
            public void done(List<HrUser> object, BmobException e) {
                if (e == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String companyName = object.get(0).getCompany_name();
                            et_hr_company_name.setText(companyName);

                            Log.d("run:>>>>>>>>", companyName);
                        }
                    });
                } else {
                    Log.e("BMOB", e.toString());
                }
            }
        });
    }
}