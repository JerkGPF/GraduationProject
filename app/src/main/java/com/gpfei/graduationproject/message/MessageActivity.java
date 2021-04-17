package com.gpfei.graduationproject.message;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.User;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    public static TextView tv_message;
    EditText et_message;
    Button btn_send;
    boolean isConnect = false;
    boolean isOpenConversation = false;

    BmobIMConversation mBmobIMConversation;

    String receiveObjId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        tv_message = (TextView) findViewById(R.id.tv_message);
        et_message = (EditText) findViewById(R.id.et_message);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);

        receiveObjId = (String) getIntent().getExtras().get("authorid");

        connect();


    }

    @Override
    public void onClick(View v) {
      if (v.getId() == R.id.btn_send){
            if (!isConnect){
                Toast.makeText(this, "未连接状态不能打开会话", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isOpenConversation){
                User user = BmobUser.getCurrentUser(User.class);
                BmobIMUserInfo info =new BmobIMUserInfo();
                info.setAvatar("填写接收者的头像");
                info.setUserId(receiveObjId);
                info.setName("填写接收者的名字");
                BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
                    @Override
                    public void done(BmobIMConversation c, BmobException e) {
                        if(e==null){
                            isOpenConversation = true;
                            //在此跳转到聊天页面或者直接转化
                            mBmobIMConversation=BmobIMConversation.obtain(BmobIMClient.getInstance(),c);
                            tv_message.append(user.getUsername()+"："+et_message.getText().toString()+"\n");
                            BmobIMTextMessage msg =new BmobIMTextMessage();
                            msg.setContent(et_message.getText().toString());
                            mBmobIMConversation.sendMessage(msg, new MessageSendListener() {
                                @Override
                                public void done(BmobIMMessage msg, BmobException e) {
                                    if (e != null) {
                                        et_message.setText("");
                                    }else{
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(MessageActivity.this, "开启会话出错", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                BmobIMTextMessage msg =new BmobIMTextMessage();
                msg.setContent(et_message.getText().toString());
                tv_message.append("发送者："+et_message.getText().toString()+"\n");
                mBmobIMConversation.sendMessage(msg, new MessageSendListener() {
                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e != null) {
                            et_message.setText("");
                        }else{
                        }
                    }
                });
            }


        }
    }

    private void connect() {
        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        User user = BmobUser.getCurrentUser(User.class);
        if (!TextUtils.isEmpty(user.getObjectId())) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        isConnect = true;
                        Log.d("我的id",uid);
                    } else {
                        //连接失败
                        Log.d("我的id", e.getMessage());
                    }
                }
            });
        }
    }

}