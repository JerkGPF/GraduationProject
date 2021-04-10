package com.gpfei.graduationproject.ui.activities.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.longsh.optionframelibrary.OptionBottomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private TextView tv_title;
    private ImageView iv_user_head;
    private RelativeLayout rl_modify_user_head;
    private RelativeLayout rl_id;
    private RelativeLayout rl_modify_user_nick;
    private RelativeLayout rl_modify_user_motto;
    private RelativeLayout rl_to_mydata;
    private TextView tv_username;
    private TextView tv_nick;
    private TextView tv_motto;

    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_ALBUM = 2;
    public static final int REQUEST_CROP = 3;
    public static final int REQUEST_INFO = 4;
    public static final int CAMERA_PERMISSION_CODE = 5;
    public static final int ALBUM_PERMISSION_CODE = 6;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    private File mImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        initView();
    }

    //初始化
    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_username = findViewById(R.id.tv_username);
        tv_nick = findViewById(R.id.tv_nick);
        tv_motto = findViewById(R.id.tv_motto);
        tv_title.setText("我的资料");
        iv_user_head = findViewById(R.id.iv_user_head);
        iv_back.setOnClickListener(this);
        rl_modify_user_head = findViewById(R.id.rl_modify_user_head);
        rl_modify_user_head.setOnClickListener(this);
        rl_modify_user_nick = findViewById(R.id.rl_modify_user_nick);
        rl_modify_user_nick.setOnClickListener(this);
        rl_modify_user_motto =  findViewById(R.id.rl_modify_user_motto);
        rl_modify_user_motto.setOnClickListener(this);
        rl_to_mydata =  findViewById(R.id.rl_to_mydata);
        rl_to_mydata.setOnClickListener(this);
        rl_id = findViewById(R.id.rl_id);
        rl_id.setOnClickListener(this);
        //显示用户信息
        showUserInfo();
    }
    //显示用户资料
    private void showUserInfo() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            //获取头像地址
            if (user.getHead() != null) {
                //圆形头像
                Glide.with(MyInfoActivity.this).load(user.getHead().toString()).asBitmap().centerCrop().into(new BitmapImageViewTarget(iv_user_head) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(MyInfoActivity.this.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        iv_user_head.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
            //获取用ID
            if (user.getUsername() != null) {
                tv_username.setText(user.getUsername().toString());
            }
            //获取用昵称
            if (user.getNick() != null) {
                tv_nick.setText(user.getNick().toString());
            }
            //获取用个性签名
            if (user.getMotto() != null) {
                tv_motto.setText(user.getMotto().toString());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_modify_user_head:
                methodRequiresTwoPermission();//调用权限
                List<String> stringList = new ArrayList<String>();
                stringList.add("拍照");
                stringList.add("从相册选择");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(MyInfoActivity.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            //Android6.0以上要获取动态权限
                            //先判断该页面是否已经授予拍照权限
                            if (ContextCompat.checkSelfPermission(MyInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                //获取拍照权限
                                ActivityCompat.requestPermissions(MyInfoActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                            } else {
                                //拍照
                                selectCamera();
                            }
                        }
                        if (position == 1) {
                            //调用相册
                            selectAlbum();
                        }
                        optionBottomDialog.dismiss();
                    }
                });

                break;
            case R.id.rl_id:
                ToastUtils.showTextToast(MyInfoActivity.this,"用户ID不支持修改哟(＾Ｕ＾)");
                break;
            case R.id.rl_modify_user_nick:
                Intent intent = new Intent(MyInfoActivity.this, ModifyUserInfoActivity.class);
                intent.putExtra("tag", "nick");
                startActivityForResult(intent, REQUEST_INFO);
                break;
            case R.id.rl_modify_user_motto:
                Intent intent1 = new Intent(MyInfoActivity.this, ModifyUserInfoActivity.class);
                intent1.putExtra("tag", "motto");
                startActivityForResult(intent1, REQUEST_INFO);
                break;
            case R.id.rl_to_mydata:
                startActivity(new Intent(MyInfoActivity.this, MyDataActivity.class));
                break;

        }

    }
    @AfterPermissionGranted(1)//添加注解，是为了首次执行权限申请后，回调该方法
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //已经申请过权限，直接调用相机
            // openCamera();
        } else {
            EasyPermissions.requestPermissions(this, "需要获取权限",
                    1, perms);
        }
    }

    //选择相机
    private void selectCamera() {
        mImageFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //如果是7.0以上，使用FileProvider，否则会报错
            fileUri = FileProvider.getUriForFile(MyInfoActivity.this, "com.gpfei.graduationproject.fileprovider", mImageFile);
        } else {
            fileUri = Uri.fromFile(mImageFile);
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    //选择相册
    private void selectAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_INFO:
                if (resultCode == 200) {
                    //刷新数据
                    showUserInfo();
                }
                break;
            case REQUEST_CAMERA://相机

                break;

            case REQUEST_CROP://裁剪照片

                break;
            case REQUEST_ALBUM://相册

                break;
        }
    }
    //裁剪照片
    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFile);
        startActivityForResult(intent, REQUEST_CROP);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                selectCamera();
                //ToastUtils.showImageToast(MyInfoActivity.this, "权限申请OK！");
                break;
        }
    }
}