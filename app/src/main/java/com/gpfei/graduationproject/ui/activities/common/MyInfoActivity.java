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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.utils.SmileToast;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.longsh.optionframelibrary.OptionBottomDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private PullToRefreshLayout refreshLayout;
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
    private Uri imageUri;
    private static final int REQUEST_CAPTURE = 2;
    public static final int REQUEST_INFO = 4;
    private static final int REQUEST_PICTURE = 5;
    private static final int RESULT_CROP = 7;
    private static final int GALLERY_ACTIVITY_CODE = 9;
    private Uri localUri = null;

    private static final String TAG = "MyInfoActivity";

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
        rl_modify_user_motto = findViewById(R.id.rl_modify_user_motto);
        rl_modify_user_motto.setOnClickListener(this);
        rl_to_mydata = findViewById(R.id.rl_to_mydata);
        rl_to_mydata.setOnClickListener(this);
        rl_id = findViewById(R.id.rl_id);
        rl_id.setOnClickListener(this);
        refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showUserInfo();
                        //结束刷新
                        refreshLayout.finishRefresh();
                        SmileToast smileToast = new SmileToast();
                        smileToast.smile("加载完成");
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showTextToast(MyInfoActivity.this, "没有更多内容了哟~");
                        //结束加载更多
                        refreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });
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
                Glide.with(MyInfoActivity.this).load(user.getHead()).asBitmap().centerCrop().into(new BitmapImageViewTarget(iv_user_head) {
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
                //Android6.0以上要获取动态权限
                //先判断该页面是否已经授予拍照权限
                methodRequiresTwoPermission();//调用权限
                List<String> stringList = new ArrayList<String>();
                stringList.add("拍照");
                stringList.add("从相册选择");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(MyInfoActivity.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            //拍照
                            openCamera();
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
                ToastUtils.showTextToast(MyInfoActivity.this, "用户ID不支持修改哟(＾Ｕ＾)");
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
    private void openCamera() {  //调用相机拍照
        Intent intent = new Intent();
        File file = getOutputMediaFile(); //工具类稍后会给出
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //针对Android7.0，需要通过FileProvider封装过的路径，提供给外部调用
            imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);//通过FileProvider创建一个content类型的Uri，进行封装
        } else { //7.0以下，如果直接拿到相机返回的intent值，拿到的则是拍照的原图大小，很容易发生OOM，所以我们同样将返回的地址，保存到指定路径，返回到Activity时，去指定路径获取，压缩图片
            imageUri = Uri.fromFile(file);
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAPTURE);//启动拍照
    }

    //选择相册
    private void selectAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_ACTIVITY_CODE);
    }

    //裁剪图片
    private void performCrop(Uri uri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                grantUriPermission("com.android.camera", uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFile().toString());
            startActivityForResult(intent, RESULT_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "你的设备不支持裁剪行为！";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * 随机生成与头像文件名进行拼接
     * @return
     */
    public static String codeGen(){
        char [] codeSequence={'a','b','c','d','e','f','g','h','i','j',
                'k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
                '1','2','3','4','5','6','7','8','9'};
        Random random =new Random();
        StringBuilder sb=new StringBuilder();//动态字符串，String创建的字符串不能修改
        int count=0;//计数器确定产生的是四位验证码
        while(true){
            //随机产生一个下标，通过下标取出字符数组对应的字符
            char c=codeSequence[random.nextInt(codeSequence.length)];
            //假设取出来的字符在动态字符串中不存在，代表没有重复
            if (sb.indexOf(c+"")==-1) {
                sb.append(c);//追加到动态字符串中
                count++;
                if (count==4) {
                    break;
                }
            }
        }

        return sb.toString();

    }

    //建立保存头像的路径及名称
    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile;
        String mImageName ="avatar.png";
//        String mImageName ="avatar"+codeGen()+".png";
        System.out.println("avatar"+mImageName);
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    //保存图像
    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_INFO:
                    if (resultCode == 200) {
                        //刷新数据
                        showUserInfo();
                        Toast.makeText(this, "200执行了", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_CAPTURE:
                    if (null != imageUri) {
                        localUri = imageUri;
                        performCrop(localUri);
                    }
                    break;
                case REQUEST_PICTURE:
                    localUri = data.getData();
                    performCrop(localUri);
                    break;
                case RESULT_CROP:
                    Bundle extras = data.getExtras();
                    Bitmap selectedBitmap = extras.getParcelable("data");
                    //判断返回值extras是否为空，为空则说明用户截图没有保存就返回了，此时应该用上一张图，
                    //否则就用用户保存的图
                    if (extras == null) {
                        // iv_user_head.setImageBitmap(mBitmap);
                        // storeImage(mBitmap);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String picPath = getOutputMediaFile().toString();
                                BmobFile bmobFile = new BmobFile(new File(picPath));
                                bmobFile.uploadblock(new UploadFileListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            saveFile(bmobFile);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    SmileToast smileToast = new SmileToast();
                                                    smileToast.smile("头像更新成功");
                                                }
                                            });
                                            Log.d("上传文件", "上传文件中");
                                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                        } else {
                                            Log.d("上传文件失败：", e.getMessage());
                                        }

                                    }

                                    @Override
                                    public void onProgress(Integer value) {

                                        // 返回的上传进度（百分比）
                                    }
                                });
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv_user_head.setImageBitmap(selectedBitmap);
                                        storeImage(selectedBitmap);
                                    }
                                });
                            }
                        }).start();
                    }
                    break;
                case GALLERY_ACTIVITY_CODE:
                    localUri = data.getData();
                    //  setBitmap(localUri);
                    performCrop(localUri);
                    break;
            }
        }
    }

    private void saveFile(BmobFile bmobFile) {
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        //更新Person表里面id为6b6c11c537的数据，address内容更新为“北京朝阳”
        myUser.setHeadFile(bmobFile);
        myUser.setHead(bmobFile.getFileUrl());
        myUser.update(myUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("上传文件成功:", bmobFile.getFileUrl());
                    Log.d("上传文件成功:", bmobFile.getFilename());
                } else {
                    Log.d("上传文件失败：", e.getMessage());
                }
            }

        });
    }

}