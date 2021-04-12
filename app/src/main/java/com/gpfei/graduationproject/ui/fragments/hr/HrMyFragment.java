package com.gpfei.graduationproject.ui.fragments.hr;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.HrUser;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.beans.SignInBean;
import com.gpfei.graduationproject.beans.User;
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
import com.gpfei.graduationproject.ui.activities.common.login.LoginAndRegisterActivity;
import com.gpfei.graduationproject.ui.activities.hr.MyPublishActivity;
import com.gpfei.graduationproject.ui.activities.hr.PostActivity;
import com.gpfei.graduationproject.ui.fragments.common.UserFragment;
import com.gpfei.graduationproject.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HrMyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HrMyFragment extends Fragment implements View.OnClickListener {
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
    private static final int INFO_CODE = 1;

    int intergal;
    String objectId;
    String updatedAt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hr_my, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rl_menu_item1 =  view.findViewById(R.id.rl_menu_item1);
        rl_menu_item1.setOnClickListener(this);
        rl_menu_item2 =  view.findViewById(R.id.rl_menu_item2);
        rl_menu_item2.setOnClickListener(this);
        rl_menu_item3 =  view.findViewById(R.id.rl_menu_item3);
        rl_menu_item3.setOnClickListener(this);
        rl_menu_item4 =  view.findViewById(R.id.rl_menu_item4);
        rl_menu_item4.setOnClickListener(this);
        rl_menu_item5 =  view.findViewById(R.id.rl_menu_item5);
        rl_menu_item5.setOnClickListener(this);
        rl_menu_item6 = view.findViewById(R.id.rl_menu_item6);
        rl_menu_item6.setOnClickListener(this);
        ll_menu1 = view.findViewById(R.id.ll_menu1);
        ll_menu1.setOnClickListener(this);
        ll_menu2 =  view.findViewById(R.id.ll_menu2);
        ll_menu2.setOnClickListener(this);
        ll_menu3 =  view.findViewById(R.id.ll_menu3);
        ll_menu3.setOnClickListener(this);
        ll_menu4 =  view.findViewById(R.id.ll_menu4);
        ll_menu4.setOnClickListener(this);
        tv_username =  view.findViewById(R.id.tv_username);
        iv_user_head =  view.findViewById(R.id.iv_user_head);
        tv_motto =  view.findViewById(R.id.tv_motto_user);
        rl_user =  view.findViewById(R.id.rl_user);
        rl_user.setOnClickListener(this);
        ll_class =  view.findViewById(R.id.ll_class);



        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            //获取用户名
            if (user.getNick() == null && user.getUsername() != null) {
                tv_username.setText(user.getUsername().toString());
            } else if (user.getNick() != null) {
                tv_username.setText(user.getNick().toString());
            }
        }

       queryIntegral();
    }

    //查询出积分
    private void queryIntegral() {
        BmobQuery<SignInBean> query = new BmobQuery<>();
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
        query.order("-updatedAt");
        //包含作者信息
        query.include("intergal");
        query.findObjects(new FindListener<SignInBean>() {
            @Override
            public void done(List<SignInBean> object, BmobException e) {
                if (e == null) {
                    Log.d("object.size()", object.size() + "");
                    Log.d("object.size()", object.get(0).getIntergal() + "");
                    intergal = object.get(0).getIntergal();
                    objectId = object.get(0).getObjectId();
                    updatedAt = object.get(0).getUpdatedAt();
                    updatedAt = updatedAt.substring(0,updatedAt.indexOf(" "));
                    Log.d("updatedAt:",updatedAt+"");
                } else {
                    Log.e("BMOB", e.toString());
                }
            }

        });
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
                    startActivity(new Intent(getContext(), MyPublishActivity.class));
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
            case R.id.ll_menu4://签到
                if (bmobUser != null) {
                    updateIntergal();
                    //startActivity(new Intent(getContext(), SignInActivity.class));
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
                    startActivity(new Intent(getContext(), PostActivity.class));
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

    //更新签到信息
    private void updateIntergal() {
        //添加当前时间
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String createTime = dateFormat.format(now);//格式化然后放入字符串中
        System.out.println(createTime);
        System.out.println(updatedAt.equals(createTime));
        if (updatedAt.equals(createTime)&&intergal!=0){
            ToastUtils.showImageToast(getActivity(),"已经签到过了！");
        }
        if (updatedAt.equals(createTime)&&intergal == 0){
            intergal +=2;
            SignInBean signInBean = new SignInBean();
            signInBean.setIntergal(intergal);
            signInBean.update(objectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null){
                        signIn();
                    }else {
                        ToastUtils.showTextToast(getActivity(),"签到失败了"+e.getMessage());
                    }
                }
            });
        }
        if (!updatedAt.equals(createTime)&& intergal != 0){
            intergal +=2;
            SignInBean signInBean = new SignInBean();
            signInBean.setIntergal(intergal);
            signInBean.update(objectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null){
                        signIn();
                    }else {
                        ToastUtils.showTextToast(getActivity(),"签到失败了"+e.getMessage());
                    }
                }
            });
        }
    }

    private void signIn() {
        final Dialog dialog = new Dialog(HrMyFragment.this.getContext());
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        View contentView = LayoutInflater.from(HrMyFragment.this.getContext()).inflate(R.layout.dialog_sign, null);
        dialog.setContentView(contentView);
        Button cancel = contentView.findViewById(R.id.submit_bt);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //背景透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.mystyle);  //添加动画
        dialog.setCanceledOnTouchOutside(true);
    }

}