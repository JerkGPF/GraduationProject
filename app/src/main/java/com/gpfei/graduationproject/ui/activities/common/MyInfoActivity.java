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

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
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

    //?????????
    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_username = findViewById(R.id.tv_username);
        tv_nick = findViewById(R.id.tv_nick);
        tv_motto = findViewById(R.id.tv_motto);
        tv_title.setText("????????????");
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
                        //????????????
                        refreshLayout.finishRefresh();
                        SmileToast smileToast = new SmileToast();
                        smileToast.smile("????????????");
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showTextToast(MyInfoActivity.this, "????????????????????????~");
                        //??????????????????
                        refreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });
        //??????????????????
        showUserInfo();
    }

    //??????????????????
    private void showUserInfo() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            //??????????????????
            if (user.getHead() != null) {
                //????????????
                Glide.with(MyInfoActivity.this).load(user.getHead()).asBitmap().centerCrop().into(new BitmapImageViewTarget(iv_user_head) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(MyInfoActivity.this.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        iv_user_head.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
            //?????????ID
            if (user.getUsername() != null) {
                tv_username.setText(user.getUsername().toString());
            }
            //???????????????
            if (user.getNick() != null) {
                tv_nick.setText(user.getNick().toString());
            }
            //?????????????????????
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
                //Android6.0???????????????????????????
                //????????????????????????????????????????????????
                methodRequiresTwoPermission();//????????????
                List<String> stringList = new ArrayList<String>();
                stringList.add("??????");
                stringList.add("???????????????");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(MyInfoActivity.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            //??????
                            openCamera();
                        }
                        if (position == 1) {
                            //????????????
                            selectAlbum();
                        }
                        optionBottomDialog.dismiss();
                    }
                });

                break;
            case R.id.rl_id:
                ToastUtils.showTextToast(MyInfoActivity.this, "??????ID??????????????????(?????????)");
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

    @AfterPermissionGranted(1)//?????????????????????????????????????????????????????????????????????
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //??????????????????????????????????????????
            // openCamera();
        } else {
            EasyPermissions.requestPermissions(this, "??????????????????",
                    1, perms);
        }
    }

    //????????????
    private void openCamera() {  //??????????????????
        Intent intent = new Intent();
        File file = getOutputMediaFile(); //????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //??????Android7.0???????????????FileProvider??????????????????????????????????????????
            imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);//??????FileProvider????????????content?????????Uri???????????????
        } else { //7.0??????????????????????????????????????????intent????????????????????????????????????????????????????????????OOM???????????????????????????????????????????????????????????????????????????Activity??????????????????????????????????????????
            imageUri = Uri.fromFile(file);
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//??????Action?????????
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//?????????????????????????????????URI
        startActivityForResult(intent, REQUEST_CAPTURE);//????????????
    }

    //????????????
    private void selectAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_ACTIVITY_CODE);
    }

    //????????????
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
            String errorMessage = "????????????????????????????????????";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * ??????????????????????????????????????????
     * @return
     */
    public static String codeGen(){
        char [] codeSequence={'a','b','c','d','e','f','g','h','i','j',
                'k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
                '1','2','3','4','5','6','7','8','9'};
        Random random =new Random();
        StringBuilder sb=new StringBuilder();//??????????????????String??????????????????????????????
        int count=0;//??????????????????????????????????????????
        while(true){
            //????????????????????????????????????????????????????????????????????????
            char c=codeSequence[random.nextInt(codeSequence.length)];
            //???????????????????????????????????????????????????????????????????????????
            if (sb.indexOf(c+"")==-1) {
                sb.append(c);//???????????????????????????
                count++;
                if (count==4) {
                    break;
                }
            }
        }

        return sb.toString();

    }

    //????????????????????????????????????
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

    //????????????
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
            Log.d(TAG, "requestCode: requestCode");
            switch (requestCode) {
                case REQUEST_INFO:
                    if (resultCode == 200) {
                        //????????????
                        showUserInfo();
                        Toast.makeText(this, "200?????????", Toast.LENGTH_SHORT).show();
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
                    //???????????????extras???????????????????????????????????????????????????????????????????????????????????????????????????
                    //??????????????????????????????
                    if (extras == null) {
                        // iv_user_head.setImageBitmap(mBitmap);
                        // storeImage(mBitmap);
                    } else {
                        Log.d(TAG, "RESULT_CROP: ??????");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String picPath = getOutputMediaFile().toString();
                                Log.d(TAG, "run: ??????"+picPath);
                                BmobFile bmobFile = new BmobFile(new File(picPath));
                                bmobFile.uploadblock(new UploadFileListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Log.d(TAG, "done: ??????");
                                            saveFile(bmobFile);
                                            Log.d(TAG, "done: ??????");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    SmileToast smileToast = new SmileToast();
                                                    smileToast.smile("??????????????????");
                                                }
                                            });
                                            Log.d("????????????", "???????????????");
                                            //bmobFile.getFileUrl()--????????????????????????????????????
                                        } else {
                                            Log.d("?????????????????????", e.getMessage());
                                        }

                                    }

                                    @Override
                                    public void onProgress(Integer value) {

                                        // ????????????????????????????????????
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
        //??????Person?????????id???6b6c11c537?????????
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        if (myUser!=null){
            //??????Person?????????id???6b6c11c537????????????address?????????????????????????????????
            myUser.setHeadFile(bmobFile);
            myUser.setHead(bmobFile.getFileUrl());
            myUser.update(myUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        System.out.println("????????????:"+bmobFile.getFileUrl());
                        System.out.println("???????????????"+bmobFile.getFilename());
                        Log.d("??????????????????:", bmobFile.getFileUrl());
                        Log.d("??????????????????:", bmobFile.getFilename());
                    } else {
                        Log.d("?????????????????????", e.getMessage());
                    }
                }
            });
        }else {

        }

    }

}