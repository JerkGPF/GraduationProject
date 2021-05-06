package com.gpfei.graduationproject.ui.activities.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.gpfei.graduationproject.utils.VersionUtils;

import org.jetbrains.annotations.NotNull;

import constant.UiType;
import listener.Md5CheckResultListener;
import listener.UpdateDownloadListener;
import model.UiConfig;
import model.UpdateConfig;
import update.UpdateAppUtils;

public class AboutActivity extends AppCompatActivity {
    private ImageView iv_back;
    private TextView tv_title;
    private TextView tv_version;
    private TextView tv_update;

    private String apkUrl = "http://files.fuengby.top/2021/05/05/72f2b736409e0360801ebba0ff981c26.apk";
    private String updateTitle = "发现新版本V2.0.0";
    private String updateContent = "1、随便测试\n2、支持自定义UI\n3、增加md5校验\n4、更多功能等你探索";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        UpdateAppUtils.init(this);
        initView();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("关于我们");
        tv_version = findViewById(R.id.tv_version);
        //设置版本号
        String versions = "version " + VersionUtils.getVersionCode(AboutActivity.this);
        tv_version.setText(versions);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_update = findViewById(R.id.tv_update);
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateConfig updateConfig = new UpdateConfig();
                updateConfig.setCheckWifi(true);
                updateConfig.setNeedCheckMd5(true);
                updateConfig.setNotifyImgRes(R.mipmap.ic_launcher_round);
                updateConfig.setServerVersionCode(2);

                UiConfig uiConfig = new UiConfig();
                uiConfig.setUiType(UiType.PLENTIFUL);
                uiConfig.setUpdateBtnBgColor(R.color.colorPrimary);


                UpdateAppUtils
                        .getInstance()
                        .apkUrl(apkUrl)
                        .updateTitle(updateTitle)
                        .updateContent(updateContent)
                        .uiConfig(uiConfig)
                        .updateConfig(updateConfig)
                        .setMd5CheckResultListener(new Md5CheckResultListener() {
                            @Override
                            public void onResult(boolean b) {

                            }
                        })
                        .setUpdateDownloadListener(new UpdateDownloadListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onDownload(int i) {

                            }

                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onError(@NotNull Throwable throwable) {

                            }
                        })
                        .update();
//                ToastUtils.showImageToast(AboutActivity.this, "已经是最新版本了哟~");
            }
        });
    }
}