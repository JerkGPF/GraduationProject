package com.gpfei.graduationproject.ui.activities.common;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.utils.ToastUtils;

import java.util.Calendar;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class UpdateUserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private TextView tv_title;
    private LinearLayout ll_select_age;
    private TextView tv_show_birthday;
    private EditText et_name, et_edit_phone, et_edit_qq,
            et_edit_email, et_introduce, et_exper;
    private RadioButton rb_sex_male, rb_sex_female;
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        initView();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        tv_title =  findViewById(R.id.tv_title);
        tv_title.setText("修改基本资料");
        ll_select_age = findViewById(R.id.ll_select_age);
        iv_back.setOnClickListener(this);
        ll_select_age.setOnClickListener(this);
        tv_show_birthday = findViewById(R.id.tv_show_birthday);


        et_name = findViewById(R.id.et_name);
        et_edit_phone = findViewById(R.id.et_edit_phone);
        et_edit_qq = findViewById(R.id.et_edit_qq);
        et_edit_email = findViewById(R.id.et_edit_email);
        et_introduce = findViewById(R.id.et_introduce);
        et_exper = findViewById(R.id.et_exper);

        rb_sex_male = findViewById(R.id.rb_sex_male);
        rb_sex_female = findViewById(R.id.rb_sex_female);

        btn_submit = findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(this);

        showInfo();


    }

    private void showInfo() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        //查找Person表里面id为6b6c11c537的数据
        BmobQuery<MyUser> bmobQuery = new BmobQuery<MyUser>();
        bmobQuery.getObject(user.getObjectId(), new QueryListener<MyUser>() {
            @Override
            public void done(MyUser object,BmobException e) {
                if(e==null){
                    et_name.setText(user.getName());
                    tv_show_birthday.setText(user.getBirthday());
                    et_edit_phone.setText(user.getMobilePhoneNumber());
                    et_edit_qq.setText(user.getQq());
                    et_edit_email.setText(user.getEmail());
                    et_exper.setText(user.getExperience());
                    et_introduce.setText(user.getProfile());
                    Log.d("run>>>>>>", user.getName());
                    if (user.getSex()){
                        rb_sex_male.setChecked(true);
                    }else if (!user.getSex()){
                        rb_sex_female.setChecked(true);
                    }
                }else{

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_select_age:
                showDatePickDlg();
                break;
            case R.id.btn_submit:
                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                if (user != null) {
                    //插入进行更新
                    user.setName(et_name.getText().toString());
                    user.setBirthday(tv_show_birthday.getText().toString());
                    user.setMobilePhoneNumber(et_edit_phone.getText().toString());
                    user.setQq(et_edit_qq.getText().toString());
                    user.setEmail(et_edit_email.getText().toString());
                    user.setExperience(et_exper.getText().toString());
                    user.setProfile(et_introduce.getText().toString());
                    if (rb_sex_male.isChecked()){
                        user.setSex(true);
                    }else if (rb_sex_female.isChecked()){
                        user.setSex(false);
                    }
                    System.out.println("ssssssssd" + et_name.getText());
                    System.out.println("ssssssssd" + et_introduce.getText());
                    //user.setValue("experience","回家环境开会艰苦环境艰苦环境开会");
                    user.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e==null){
                                ToastUtils.showImageToast(UpdateUserInfoActivity.this, "更新成功！");
                                finish();
                            }else {
                                ToastUtils.showImageToast(UpdateUserInfoActivity.this, "更新失败！"+e.getMessage());

                            }
                        }
                    });
                }

                break;
        }

    }

    protected void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateUserInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tv_show_birthday.setText(year + "-" + (monthOfYear +1) + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

}