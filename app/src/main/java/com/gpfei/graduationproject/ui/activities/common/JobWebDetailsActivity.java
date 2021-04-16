package com.gpfei.graduationproject.ui.activities.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.ui.activities.common.login.LoginAndRegisterActivity;
import com.gpfei.graduationproject.utils.SmileToast;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.longsh.optionframelibrary.OptionCenterDialog;

import java.util.ArrayList;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class JobWebDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private TextView tv_title;
    private WebView mWebView;
    private LinearLayout ll_error_state;
    private ImageView iv_sharing;
    private LinearLayout ll_collect;
    private Button btn_load;
    private TextView tv_collect;
    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_web_details);
        objectId = (String) getIntent().getExtras().get("objectId");
        initView();
        loadWeb();
    }

    private void initView() {

        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        ll_collect = findViewById(R.id.ll_collect);

        ll_error_state = findViewById(R.id.ll_error_state);
        mWebView = findViewById(R.id.mWebView);
        iv_sharing = findViewById(R.id.iv_sharing);
        btn_load = findViewById(R.id.btn_load);
        tv_collect = findViewById(R.id.tv_collect);
        btn_load.setOnClickListener(this);
        tv_title.setText("返回");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_sharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> list = new ArrayList<>();
                list.add("分享");
                list.add("投递");
                list.add("取消");
                final OptionCenterDialog optionCenterDialog = new OptionCenterDialog();
                optionCenterDialog.show(JobWebDetailsActivity.this, list);
                optionCenterDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                ToastUtils.showImageToast(JobWebDetailsActivity.this, "分享成功");
                                break;
                            case 1:
//                                SelectAndResume selectAndResume = new SelectAndResume();
//                                selectAndResume.setDelivery(true);
//                                selectAndResume.update(objectId, new UpdateListener() {
//                                    @Override
//                                    public void done(BmobException e) {
//                                        if(e==null){
//                                            SmileToast smileToast = new SmileToast();
//                                            smileToast.smile("投递成功");
//                                        }else{
//                                            Toast.makeText(JobWebDetailsActivity.this, "投递失败", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//
//                                });
                                ToastUtils.showImageToast(JobWebDetailsActivity.this, "投递成功");
                                break;
                            default:
                                break;
                        }
                        optionCenterDialog.dismiss();
                    }
                });
            }
        });
        ll_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
                if (bmobUser != null) {
                    ToastUtils.showImageToast(JobWebDetailsActivity.this, "收藏成功！");
                } else {
                    ToastUtils.showTextToast(JobWebDetailsActivity.this, "请先登录!");
                    startActivity(new Intent(JobWebDetailsActivity.this, LoginAndRegisterActivity.class));
                }
            }
        });

    }

    private void loadWeb() {
        String url = (String) getIntent().getExtras().get("url");

        if (url != null) {
            mWebView.loadUrl(url);
//            new Thread() {
//                public void run() {
//                    //执行耗时操作
//                    queryOne(objectId);
//                }
//            }.start();
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //加载开始
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //加载结束
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //加载页面的服务器出错时调用
                mWebView.setVisibility(View.GONE);
                ll_error_state.setVisibility(View.VISIBLE);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
    }

    private void queryOne(String objectId) {
        //查找Person表里面id为6b6c11c537的数据
        BmobQuery<SelectAndResume> bmobQuery = new BmobQuery<SelectAndResume>();
        bmobQuery.getObject(objectId, new QueryListener<SelectAndResume>() {
            @Override
            public void done(SelectAndResume object,BmobException e) {
                if(e==null){
                    if (object.getCollect()){
                        tv_collect.setText("已收藏");
                    }else {
                        tv_collect.setText("收藏");
                    }
                }else{
                    Toast.makeText(JobWebDetailsActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load:
                initView();
                loadWeb();
                break;
        }
    }
}