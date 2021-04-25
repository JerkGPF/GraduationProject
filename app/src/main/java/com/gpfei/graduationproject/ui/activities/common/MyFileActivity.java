package com.gpfei.graduationproject.ui.activities.common;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.kotlin.upload.Constants;
import com.gpfei.graduationproject.kotlin.upload.LocalUpdateActivity;
import com.gpfei.graduationproject.utils.SmileToast;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.http.I;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MyFileActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private TextView tv_title,tv_file,tv_action;
    private Button btn_upload;
    private BmobFile fileDownload;
    private String fileUrl ="";//文档地址
    private PullToRefreshLayout refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_file);
        initView();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("附件管理");
        btn_upload = findViewById(R.id.btn_upload);

        tv_file = findViewById(R.id.tv_file);
        tv_action = findViewById(R.id.tv_action);
        btn_upload.setOnClickListener(this);
        tv_file.setOnClickListener(this);
        tv_action.setOnClickListener(this);
        refresh = findViewById(R.id.refresh);
        refresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        show();
                        //结束刷新
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refresh.finishRefresh();
                            }
                        });

                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showTextToast(MyFileActivity.this, "没有更多内容了哟~");
                        //结束加载更多
                        refresh.finishLoadMore();
                    }
                }, 2000);
            }
        });


        show();


    }

    private void show() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //查找Person表里面id为6b6c11c537的数据
                BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
                BmobQuery<MyUser> bmobQuery = new BmobQuery<MyUser>();
                bmobQuery.getObject(bmobUser.getObjectId(), new QueryListener<MyUser>() {
                    @Override
                    public void done(MyUser object,BmobException e) {
                        if(e==null){
                            BmobFile bmobfile = object.getFile();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_file.setText(bmobfile.getFilename());
                                    fileDownload = bmobfile;
                                    fileUrl = bmobfile.getFileUrl();
                                    System.out.println("地址："+fileUrl);
                                }
                            });
                        }else{
                            System.out.println("失败："+e.getMessage());
                        }
                    }
                });
            }
        }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.UPLOAD_FILE_REQUEST && resultCode == Constants.UPLOAD_FILE_RESULT){
            List<String> list = data.getStringArrayListExtra("pathList");
            String filePath = null;
            for(String path:list){
                Log.d("地址：",path);
                filePath = path;
            }
                BmobFile bmobFile = new BmobFile(new File(filePath));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    saveFile(bmobFile);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_file.setText(bmobFile.getFilename());
                                            SmileToast smileToast = new SmileToast();
                                            smileToast.smile("上传成功");
                                        }
                                    });
                                }
                            }).start();
                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                        }else{
                            Log.d("上传文件失败：" , e.getMessage());
                        }

                    }
                    @Override
                    public void onProgress(Integer value) {
                        // 返回的上传进度（百分比）
                    }
                });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_upload:
                Intent intent=new Intent(MyFileActivity.this, LocalUpdateActivity.class);
                intent.putExtra("maxNum",1);//设置最大选择数
                startActivityForResult(intent, Constants.UPLOAD_FILE_REQUEST);
                break;
            case R.id.tv_file:
                //预览简历信息
                startActivity(new Intent(MyFileActivity.this,FileWebDetailsActivity.class).putExtra("fileUrl",fileUrl));
                System.out.println("地址"+fileUrl);
                //download();
                break;
            case R.id.tv_action:
                download();
                //删除和重命名简历
                break;
        }
    }
    //简历下载
    private void download() {
        fileDownload.download(new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    System.out.println("路径"+s);
                    Toast.makeText(MyFileActivity.this, "下载成功,保存路径:"+s, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MyFileActivity.this, "下载失败："+e.getErrorCode()+","+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onProgress(Integer integer, long l) {

            }
        });

    }

    private void saveFile(BmobFile bmobFile) {
        MyUser myUser  = BmobUser.getCurrentUser(MyUser.class);
        if (myUser!=null){
            myUser.setFile(bmobFile);
            myUser.update(myUser.getObjectId(),new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e==null){
                        Log.d("上传文件成功:" , bmobFile.getFileUrl());
                        Log.d("上传文件成功:" , bmobFile.getFilename());
                    }else {
                        Log.d("上传文件失败：" , e.getMessage());
                    }
                }
            });
        }
    }
}