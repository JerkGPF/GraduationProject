package com.gpfei.graduationproject.ui.activities.hr.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.ui.activities.common.MainActivity;
import com.gpfei.graduationproject.ui.activities.common.login.LoginAndRegisterActivity;
import com.gpfei.graduationproject.ui.activities.hr.HrMainActivity;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HrLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HrLoginFragment extends Fragment implements View.OnClickListener {
    private EditText et_phone_login;
    private EditText et_pwd_login;
    private Button btn_login;
    private TextView tv_HR;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hr_login, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        et_phone_login = view.findViewById(R.id.et_phone_login);
        et_pwd_login = view.findViewById(R.id.et_pwd_login);
        btn_login = view.findViewById(R.id.btn_login);
        tv_HR = view.findViewById(R.id.tv_HR_login);
        btn_login.setOnClickListener(this);
        tv_HR.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (et_phone_login.getText().toString().isEmpty()) {
                    ToastUtils.showTextToast(getContext(), "账号不能为空哟~");
                } else if (et_phone_login.getText().toString().length() < 6) {
                    ToastUtils.showTextToast(getContext(), "账号不能小于6位~");
                } else if (et_pwd_login.getText().toString().isEmpty()) {
                    ToastUtils.showTextToast(getContext(), "密码不能为空~");
                } else if (et_pwd_login.getText().toString().length() < 6) {
                    ToastUtils.showTextToast(getContext(), "密码不能小于6位哟~~");
                } else {
                    String username = et_phone_login.getText().toString().trim();
                    String passworld = et_pwd_login.getText().toString().trim();
                    //登录
                    new Thread(){
                        @Override
                        public void run() {
                            EMClient.getInstance().login(username, passworld, new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    BmobUser bmobUser = new BmobUser();
                                    bmobUser.setUsername(username);
                                    bmobUser.setPassword(passworld);
                                    bmobUser.login(new SaveListener<Object>() {
                                        @Override
                                        public void done(Object o, BmobException e) {
                                            if (e == null) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtils.showImageToast(getContext(), "登录成功");
                                                        Intent intent = new Intent(getContext(), HrMainActivity.class);
                                                        //清空栈底
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtils.showTextToast(getContext(), "登录超时！请重试");
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onError(int i, String s) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "登录失败"+s, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onProgress(int i, String s) {

                                }
                            });
                        }
                    }.start();

                }
                break;
            case R.id.tv_HR_login:
                Intent intent = new Intent(getContext(), LoginAndRegisterActivity.class);
                //清空栈底
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }

    }
}