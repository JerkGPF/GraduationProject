package com.gpfei.graduationproject.ui.activities.hr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.ui.activities.MessageActivity;
import com.gpfei.graduationproject.ui.activities.common.EditUserInfoActivity;
import com.gpfei.graduationproject.ui.activities.common.FileWebDetailsActivity;
import com.gpfei.graduationproject.ui.activities.common.JobWebDetailsActivity;
import com.gpfei.graduationproject.ui.activities.common.MyDataActivity;
import com.gpfei.graduationproject.ui.activities.common.MyFileActivity;
import com.gpfei.graduationproject.ui.activities.common.login.LoginAndRegisterActivity;
import com.gpfei.graduationproject.utils.SmileToast;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.hyphenate.easeui.EaseConstant;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class HrCheckUserInfoActivity extends AppCompatActivity {
    private ImageView iv_back;
    private TextView tv_title;
    private TextView tv_name,tv_phone,tv_sex,tv_birth,tv_qq,tv_email,tv_induce,tv_experience;
    private FloatingActionButton fab,check_file,down_file;
    String username;
    String fileUrl;
    BmobFile downloadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr_check_user_info);
        initView();
        loadData();
    }

    private void initView() {
        fab = findViewById(R.id.fab);
        check_file = findViewById(R.id.check_file);
        down_file = findViewById(R.id.down_file);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("求职者信息");

        tv_phone = findViewById(R.id.tv_phone);
        tv_name = findViewById(R.id.tv_name);
        tv_sex = findViewById(R.id.tv_sex);
        tv_birth = findViewById(R.id.tv_birthday);
        tv_qq = findViewById(R.id.tv_qq);
        tv_email = findViewById(R.id.tv_email);
        tv_induce = findViewById(R.id.tv_induce);
        tv_experience = findViewById(R.id.tv_experience);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //聊天
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("author>>>>>>>>", username);
                Intent chat = new Intent(HrCheckUserInfoActivity.this, MessageActivity.class);
                chat.putExtra(EaseConstant.EXTRA_USER_ID,username);  //对方账号
                chat.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE); //单聊模式
                startActivity(chat);
            }
        });
        //在线简历
        check_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HrCheckUserInfoActivity.this, FileWebDetailsActivity.class).putExtra("fileUrl",fileUrl));
            }
        });
        //下载简历
        down_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadFile.download(new DownloadFileListener() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e==null){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.out.println("路径"+s);
                                            Toast.makeText(HrCheckUserInfoActivity.this, "下载成功,保存路径:"+s, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(HrCheckUserInfoActivity.this, "下载失败："+e.getErrorCode()+","+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onProgress(Integer integer, long l) {

                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void loadData() {
        Intent intent = getIntent();
        String objectId = intent.getStringExtra("objectId");
        if (objectId !=null){
            new Thread() {
                public void run() {
                    //执行耗时操作
                    queryOne(objectId);
                }
            }.start();
        }else {
            Toast.makeText(this, "object为空", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 查询一个对象
     */
    private void queryOne(String mObjectId) {
        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(mObjectId, new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null) {
                    tv_name.setText(myUser.getName());
                    tv_phone.setText(myUser.getMobilePhoneNumber());
                    tv_email.setText(myUser.getEmail());
                    tv_experience.setText(myUser.getExperience());
                    tv_induce.setText(myUser.getProfile());
                    tv_qq.setText(myUser.getQq().toString());
                    //问题
                    if (myUser.getSex()==null){
                        ToastUtils.showTextToast(HrCheckUserInfoActivity.this,"性别为空");
                    }else {
                        if (myUser.getSex()){
                            tv_sex.setText("男");
                        }else {
                            tv_sex.setText("女");
                        }
                    }
                    tv_experience.setText(myUser.getExperience());
                    tv_birth.setText(myUser.getBirthday());

                    username = myUser.getUsername();
                    fileUrl = myUser.getFile().getFileUrl();
                    downloadFile = myUser.getFile();
                    Log.d("cehngg",myUser.getUsername());
                } else {
                    ToastUtils.showTextToast(HrCheckUserInfoActivity.this,"对方还没有上传简历哟~");
                    //Toast.makeText(HrCheckUserInfoActivity.this, "错误"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}