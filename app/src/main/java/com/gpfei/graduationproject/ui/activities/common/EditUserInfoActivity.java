package com.gpfei.graduationproject.ui.activities.common;

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
import android.widget.RadioButton;
import android.widget.TextView;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.beans.SignInBean;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.ui.activities.common.login.LoginAndRegisterActivity;
import com.gpfei.graduationproject.utils.ToastUtils;

import java.util.Calendar;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class EditUserInfoActivity extends AppCompatActivity implements View.OnClickListener {
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
        setContentView(R.layout.activity_edit_user_info);
        initView();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("完善基本资料");
        ll_select_age = (LinearLayout) findViewById(R.id.ll_select_age);
        iv_back.setOnClickListener(this);
        ll_select_age.setOnClickListener(this);
        tv_show_birthday = (TextView) findViewById(R.id.tv_show_birthday);


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
                // 耗时操作要在子线程中操作
                new Thread() {
                    public void run() {
                        //执行耗时操作
                        saveSignBean();//签到
                        //更新主线程UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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
                                    user.setHR(false);
                                    if (rb_sex_male.isChecked()) {
                                        user.setSex(true);
                                    } else if (rb_sex_female.isChecked()) {
                                        user.setSex(false);
                                    }
                                    System.out.println("ssssssssd" + et_name.getText());
                                    System.out.println("ssssssssd" + et_introduce.getText());
                                    //user.setValue("experience","回家环境开会艰苦环境艰苦环境开会");
                                    user.update(user.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                ToastUtils.showImageToast(EditUserInfoActivity.this, "更新成功！");
                                                finish();
                                                Intent intent = new Intent(EditUserInfoActivity.this, LoginAndRegisterActivity.class);
                                                startActivity(intent);
                                            } else {
                                                ToastUtils.showImageToast(EditUserInfoActivity.this, "更新失败！" + e.getMessage());

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }.start();
                break;
        }

    }

    /**
     * 添加一对一关联，当前用户签到赋初值0
     */
    private void saveSignBean() {
        SignInBean signInBean = new SignInBean();
        signInBean.setIntergal(0);
        //添加一对一关联，用户关联帖子
        signInBean.setUser(BmobUser.getCurrentUser(User.class));
        signInBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                } else {
                    Log.e("BMOB", e.toString());
                }
            }
        });
    }

    protected void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(EditUserInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tv_show_birthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

}