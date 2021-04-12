package com.gpfei.graduationproject.ui.activities.hr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.SelectionBean;
import com.gpfei.graduationproject.utils.SmileToast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyPracticeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_back;
    private EditText et_hr_company_job;
    private EditText et_hr_company_salary;
    private EditText et_hr_company_name;
    private EditText et_hr_company_place;
    private EditText et_hr_company_url;
    private TextView tv_title;
    private Button btn, cancel;

    String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        initView();
    }

    private void initView() {
        cancel = findViewById(R.id.cancel);
        et_hr_company_job = findViewById(R.id.et_hr_company_job);
        et_hr_company_salary = findViewById(R.id.et_hr_company_salary);
        et_hr_company_name = findViewById(R.id.et_hr_company_name);
        et_hr_company_place = findViewById(R.id.et_hr_company_place);
        et_hr_company_url = findViewById(R.id.et_hr_company_url);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        btn = findViewById(R.id.submit);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("职位发布");
        btn.setOnClickListener(this);
        cancel.setOnClickListener(this);
        loadPractice();//全职
    }

    private void loadPractice() {
        objectId = getIntent().getStringExtra("modify_practice");
        //查找Person表里面id为6b6c11c537的数据
        BmobQuery<SelectionBean> bmobQuery = new BmobQuery<SelectionBean>();
        bmobQuery.getObject(objectId, new QueryListener<SelectionBean>() {
            @Override
            public void done(SelectionBean object, BmobException e) {
                if (e == null) {
                    et_hr_company_job.setText(object.getTitle_selection());
                    et_hr_company_salary.setText(object.getMoney_selection());
                    et_hr_company_name.setText(object.getCompany_selection());
                    et_hr_company_place.setText(object.getAddress_selection());
                    et_hr_company_url.setText(object.getUrl_selection());
                } else {
                    Toast.makeText(ModifyPracticeActivity.this, "查询失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
            case R.id.cancel:
                finish();
                break;
            case R.id.submit:
                DayBean dayBean = new DayBean();
                dayBean.setUrl(et_hr_company_url.getText().toString());
                dayBean.setMoney_day(et_hr_company_salary.getText().toString());
                dayBean.setTitle_day(et_hr_company_job.getText().toString());
                dayBean.setCompany_day(et_hr_company_name.getText().toString());
                dayBean.setAddress_day(et_hr_company_place.getText().toString());
                dayBean.update(objectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            SmileToast smileToast = new SmileToast();
                            smileToast.smile("修改成功");
                            finish();
                        } else {
                            Toast.makeText(ModifyPracticeActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                break;
        }

    }
}