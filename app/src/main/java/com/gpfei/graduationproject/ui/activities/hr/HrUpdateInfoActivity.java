package com.gpfei.graduationproject.ui.activities.hr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.HrUser;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.ui.activities.hr.login.HrLoginAndRegisterActivity;

import java.util.Calendar;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class HrUpdateInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_title;
    private ImageView iv_back;
    private TextView tv_compamy_birthday;
    private EditText et_company_name,et_company_phone,
                     et_company_email,et_company_introduce,et_company_free;
    private Button btn_submit;
    private LinearLayout ll_select_company_age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr_update_info);
        initView();
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("完善公司基本资料");
        iv_back = findViewById(R.id.iv_back);
        tv_compamy_birthday = findViewById(R.id.tv_show_company_birthday);
        et_company_name = findViewById(R.id.et_company_name);
        et_company_phone = findViewById(R.id.et_edit_company_phone);
        et_company_email = findViewById(R.id.et_edit_company_email);
        et_company_introduce = findViewById(R.id.et_company_introduce);
        et_company_free = findViewById(R.id.et_company_free);
        btn_submit = findViewById(R.id.btn_submit);
        ll_select_company_age = findViewById(R.id.ll_select_company_age);


        ll_select_company_age.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_select_company_age:
                showDatePickDlg();
                break;
            case R.id.btn_submit:
                saveInfo();
                break;
        }
    }
    /**
     * 添加一对一关联，保存公司信息
     */
    private void saveInfo() {
        if (BmobUser.isLogin()) {
            HrUser hrUser = new HrUser();
            hrUser.setCompany_name(et_company_name.getText().toString().trim());
            hrUser.setCompany_birthday(tv_compamy_birthday.getText().toString().trim());
            hrUser.setCompany_phone(et_company_phone.getText().toString().trim());
            hrUser.setCompany_email(et_company_email.getText().toString().trim());
            hrUser.setCompany_introduce(et_company_introduce.getText().toString().trim());
            hrUser.setCompany_free(et_company_free.getText().toString().trim());
            //添加一对一关联，用户关联帖子
            hrUser.setUserInfo(BmobUser.getCurrentUser(User.class));
            hrUser.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(HrUpdateInfoActivity.this, "公司基本信息完善成功", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(HrUpdateInfoActivity.this, HrLoginAndRegisterActivity.class);
                        startActivity(intent);
                    } else {
                        Log.e("BMOB", e.toString());
                        Toast.makeText(HrUpdateInfoActivity.this, "信息完善出现问题" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
        }
    }

        private void showDatePickDlg () {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(HrUpdateInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    tv_compamy_birthday.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
    }
}