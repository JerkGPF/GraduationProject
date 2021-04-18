package com.gpfei.graduationproject.ui.activities.common.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.SignInBean;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.ui.activities.common.EditUserInfoActivity;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {
    private EditText et_phone;
    private EditText et_pwd;
    private Button btn_register;
    private EditText et_pwd2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        et_phone = (EditText) view.findViewById(R.id.et_phone);
        et_pwd = (EditText) view.findViewById(R.id.et_pwd);
        btn_register = (Button) view.findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        et_pwd2 = (EditText) view.findViewById(R.id.et_pwd2);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_register:
                if (et_phone.getText().toString().isEmpty()) {
                    ToastUtils.showImageToast(getContext(), "账号不能为空哟~");
                } else if (et_phone.getText().toString().length() < 6) {
                    ToastUtils.showImageToast(getContext(), "账号不能小于6位哟~");
                } else if (et_pwd.getText().toString().isEmpty()) {
                    ToastUtils.showImageToast(getContext(), "密码不能为空哟~");
                } else if (et_pwd.getText().toString().length() < 6) {
                    ToastUtils.showImageToast(getContext(), "密码不能小于6位哟~");
                } else if (et_pwd2.getText().toString().isEmpty()) {
                    ToastUtils.showImageToast(getContext(), "确认一下密码~");
                } else if (!et_pwd2.getText().toString().equals(et_pwd.getText().toString())) {
                    ToastUtils.showImageToast(getContext(), "两次密码不一致~");
                } else {
                    String username = et_phone.getText().toString().trim();
                    String password = et_pwd.getText().toString().trim();

                    new Thread() {
                        @Override
                        public void run() {
                            //注册环信
                            try {
                                EMClient.getInstance().createAccount(username, password);
                                //注册Bmob后端云
                                BmobUser bmobUser = new BmobUser();
                                bmobUser.setUsername(username);
                                bmobUser.setPassword(password);
                                bmobUser.signUp(new SaveListener<Object>() {
                                    @Override
                                    public void done(Object o, BmobException e) {
                                        if (e == null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtils.showImageToast(getContext(), "注册成功！！");
                                                    Intent intent = new Intent(getContext(), EditUserInfoActivity.class);
                                                    startActivity(intent);                                                }
                                            });
                                        } else {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtils.showTextToast(getContext(), "注册失败"+e);
                                                }
                                            });
                                        }
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "注册失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }.start();
                }
                break;
        }

    }


}