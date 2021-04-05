package com.gpfei.graduationproject.ui.fragments.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.ui.activities.common.AboutActivity;
import com.gpfei.graduationproject.ui.activities.common.FeedBackActivity;
import com.gpfei.graduationproject.ui.activities.common.HelpActivity;
import com.gpfei.graduationproject.ui.activities.common.MyApplyActivity;
import com.gpfei.graduationproject.ui.activities.common.MyAttentionActivity;
import com.gpfei.graduationproject.ui.activities.common.MyCollectActivity;
import com.gpfei.graduationproject.ui.activities.common.MyDataActivity;
import com.gpfei.graduationproject.ui.activities.common.MyInfoActivity;
import com.gpfei.graduationproject.ui.activities.common.MyIntegralActivity;
import com.gpfei.graduationproject.ui.activities.common.SettingActivity;
import com.gpfei.graduationproject.ui.activities.common.SignInActivity;
import com.gpfei.graduationproject.ui.activities.common.login.LoginAndRegisterActivity;
import com.gpfei.graduationproject.utils.ToastUtils;

import cn.bmob.v3.BmobUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener {
    private ImageView iv_user_head;
    private RelativeLayout rl_menu_item1;
    private RelativeLayout rl_menu_item2;
    private RelativeLayout rl_menu_item3;
    private RelativeLayout rl_menu_item4;
    private RelativeLayout rl_menu_item5;
    private RelativeLayout rl_menu_item6;
    private LinearLayout ll_menu1;
    private LinearLayout ll_menu2;
    private LinearLayout ll_menu3;
    private LinearLayout ll_menu4;
    private TextView tv_username;
    private TextView tv_motto;
    private RelativeLayout rl_user;
    private LinearLayout ll_class;
    private static  final int INFO_CODE=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rl_menu_item1 = (RelativeLayout) view.findViewById(R.id.rl_menu_item1);
        rl_menu_item1.setOnClickListener(this);
        rl_menu_item2 = (RelativeLayout) view.findViewById(R.id.rl_menu_item2);
        rl_menu_item2.setOnClickListener(this);
        rl_menu_item3 = (RelativeLayout) view.findViewById(R.id.rl_menu_item3);
        rl_menu_item3.setOnClickListener(this);
        rl_menu_item4 = (RelativeLayout) view.findViewById(R.id.rl_menu_item4);
        rl_menu_item4.setOnClickListener(this);
        rl_menu_item5 = (RelativeLayout) view.findViewById(R.id.rl_menu_item5);
        rl_menu_item5.setOnClickListener(this);
        rl_menu_item6 = (RelativeLayout) view.findViewById(R.id.rl_menu_item6);
        rl_menu_item6.setOnClickListener(this);
        ll_menu1 = (LinearLayout) view.findViewById(R.id.ll_menu1);
        ll_menu1.setOnClickListener(this);
        ll_menu2 = (LinearLayout) view.findViewById(R.id.ll_menu2);
        ll_menu2.setOnClickListener(this);
        ll_menu3 = (LinearLayout) view.findViewById(R.id.ll_menu3);
        ll_menu3.setOnClickListener(this);
        ll_menu4 = (LinearLayout) view.findViewById(R.id.ll_menu4);
        ll_menu4.setOnClickListener(this);
        tv_username = (TextView) view.findViewById(R.id.tv_username);
        iv_user_head = (ImageView) view.findViewById(R.id.iv_user_head);
        tv_motto = (TextView) view.findViewById(R.id.tv_motto_user);
        rl_user = (RelativeLayout) view.findViewById(R.id.rl_user);
        rl_user.setOnClickListener(this);
        ll_class = (LinearLayout) view.findViewById(R.id.ll_class);

        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            //获取头像地址
            if (user.getHead() != null) {
                Glide.with(getContext()).load(user.getHead().toString()).asBitmap().centerCrop().into(new BitmapImageViewTarget(iv_user_head) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        iv_user_head.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
            //获取用户名
            if (user.getNick() == null && user.getUsername() != null) {
                tv_username.setText(user.getUsername().toString());
            } else if (user.getNick() != null) {
                tv_username.setText(user.getNick().toString());
            }
            //获取个性签名
            if (user.getMotto() != null) {
                tv_motto.setText(user.getMotto().toString());
            }
            //显示等级
            ll_class.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onClick(View v) {
        BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        switch (v.getId()) {
            case R.id.rl_user:
                //判断登录是否为空
                if (bmobUser != null) {
                    Intent intent = new Intent(getContext(), MyInfoActivity.class);
                    startActivityForResult(intent, INFO_CODE);
                } else {
                    startActivity(new Intent(getContext(), LoginAndRegisterActivity.class));
                }
                break;
            case R.id.ll_menu1:
                if (bmobUser != null) {
                    startActivity(new Intent(getContext(), MyDataActivity.class));
                } else {
                    startActivity(new Intent(getContext(), LoginAndRegisterActivity.class));
                    ToastUtils.showTextToast(getContext(), "请先登录!");
                }
                break;
            case R.id.ll_menu2:
                if (bmobUser != null) {
                    startActivity(new Intent(getContext(), MyApplyActivity.class));
                } else {
                    startActivity(new Intent(getContext(), LoginAndRegisterActivity.class));
                    ToastUtils.showTextToast(getContext(), "请先登录!");
                }
                break;
            case R.id.ll_menu3:
                if (bmobUser != null) {
                    startActivity(new Intent(getContext(), MyIntegralActivity.class));
                } else {
                    startActivity(new Intent(getContext(), LoginAndRegisterActivity.class));
                    ToastUtils.showTextToast(getContext(), "请先登录!");
                }
                break;
            case R.id.ll_menu4:
                if (bmobUser != null) {
                    startActivity(new Intent(getContext(), SignInActivity.class));
                } else {
                    startActivity(new Intent(getContext(), LoginAndRegisterActivity.class));
                    ToastUtils.showTextToast(getContext(), "请先登录!");
                }
                break;
            case R.id.rl_menu_item1:
                if (bmobUser != null) {
                    startActivity(new Intent(getContext(), MyAttentionActivity.class));
                } else {
                    startActivity(new Intent(getContext(), LoginAndRegisterActivity.class));
                    ToastUtils.showTextToast(getContext(), "请先登录!");
                }
                break;
            case R.id.rl_menu_item2:
                if (bmobUser != null) {
                    startActivity(new Intent(getContext(), MyCollectActivity.class));
                } else {
                    startActivity(new Intent(getContext(), LoginAndRegisterActivity.class));
                    ToastUtils.showTextToast(getContext(), "请先登录!");
                }
                break;
            case R.id.rl_menu_item3:
                startActivity(new Intent(getContext(), FeedBackActivity.class));
                break;
            case R.id.rl_menu_item4:
                Intent intent4 = new Intent(getContext(), HelpActivity.class);
                startActivity(intent4);
                break;
            case R.id.rl_menu_item5:
                Intent intent5 = new Intent(getContext(), AboutActivity.class);
                startActivity(intent5);
                break;
            case R.id.rl_menu_item6:
                Intent intent6 = new Intent(getContext(), SettingActivity.class);
                startActivity(intent6);
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==INFO_CODE){
            //刷新用户信息
            MyUser user = BmobUser.getCurrentUser(MyUser.class);
            //获取用户名
            if (user.getNick() == null && user.getUsername() != null) {
                tv_username.setText(user.getUsername().toString());
            } else if (user.getNick() != null) {
                tv_username.setText(user.getNick().toString());
            }
            //获取个性签名
            if (user.getMotto() != null) {
                tv_motto.setText(user.getMotto().toString());
            }
        }
    }
}