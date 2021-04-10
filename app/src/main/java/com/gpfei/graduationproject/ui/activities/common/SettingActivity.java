package com.gpfei.graduationproject.ui.activities.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.utils.DataCleanManager;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.longsh.optionframelibrary.OptionBottomDialog;
import com.longsh.optionframelibrary.OptionMaterialDialog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private TextView tv_title;
    private RelativeLayout rl_setting_menu1;
    private RelativeLayout rl_setting_menu2;
    private RelativeLayout rl_setting_menu3;
    private Button btn_exit;
    private View view_line_setting;
    private TextView cace_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }
    private void initView() {
        iv_back =  findViewById(R.id.iv_back);
        tv_title =  findViewById(R.id.tv_title);
        tv_title.setText("我的设置");
        iv_back.setOnClickListener(this);
        rl_setting_menu1 =  findViewById(R.id.rl_setting_menu1);
        rl_setting_menu1.setOnClickListener(this);
        rl_setting_menu2 =  findViewById(R.id.rl_setting_menu2);
        rl_setting_menu2.setOnClickListener(this);
        rl_setting_menu3 =  findViewById(R.id.rl_setting_menu3);
        rl_setting_menu3.setOnClickListener(this);
        btn_exit =  findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(this);
        view_line_setting = findViewById(R.id.view_line_setting);

        cace_tv =findViewById(R.id.set_cace_tv);

        BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        if (bmobUser != null) {
            //登录后显示控件
            rl_setting_menu1.setVisibility(View.VISIBLE);
            view_line_setting.setVisibility(View.VISIBLE);
            btn_exit.setVisibility(View.VISIBLE);
        }


        caceMuch();//显示缓存大小


    }

    //计算缓存
    private void caceMuch() {
        try {
            cace_tv.setText(DataCleanManager.getTotalCacheSize(cace_tv.getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting_menu1:
                startActivity(new Intent(SettingActivity.this, ModifyPassWordActivity.class));
                break;
            case R.id.rl_setting_menu2:
                final OptionMaterialDialog mMaterialDialog = new OptionMaterialDialog(SettingActivity.this);
                mMaterialDialog.setTitle("提示")
                        .setTitleTextSize((float) 18)
                        .setMessage("确定要清除缓存吗？")
                        .setMessageTextSize((float) 15)
                        .setPositiveButtonTextSize(12)
                        .setNegativeButtonTextSize(12)
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DataCleanManager.clearAllCache(rl_setting_menu2.getContext());//清空缓存
                                ToastUtils.showImageToast(SettingActivity.this, "成功清除缓存");
                                mMaterialDialog.dismiss();
                                caceMuch();//重新计算缓存
                            }
                        })
                        .setNegativeButton("取消",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                    }
                                })
                        .setCanceledOnTouchOutside(true)
                        .setOnDismissListener(
                                new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        //对话框消失后回调
                                    }
                                })
                        .show();
                break;
            case R.id.rl_setting_menu3:
                ToastUtils.showImageToast(SettingActivity.this, "已经是最新版");
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_exit:
                List<String> stringList = new ArrayList<String>();
                stringList.add("退出登录");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(SettingActivity.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            BmobUser.logOut(); //清除缓存用户·对象
                            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            ToastUtils.showImageToast(SettingActivity.this, "已退出登录");
                            SettingActivity.this.finish();
                            optionBottomDialog.dismiss();
                        }
                    }
                });

                break;
        }

    }
}