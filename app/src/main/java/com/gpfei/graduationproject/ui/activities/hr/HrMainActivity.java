package com.gpfei.graduationproject.ui.activities.hr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.ui.activities.MessageActivity;
import com.gpfei.graduationproject.ui.fragments.common.FindFragment;
import com.gpfei.graduationproject.ui.fragments.common.HomeFragment;
import com.gpfei.graduationproject.ui.fragments.common.MessageFragment;
import com.gpfei.graduationproject.ui.fragments.common.UserFragment;
import com.gpfei.graduationproject.ui.fragments.hr.HrIndexFragment;
import com.gpfei.graduationproject.ui.fragments.hr.HrMessageFragment;
import com.gpfei.graduationproject.ui.fragments.hr.HrMyFragment;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.ArrayList;
import java.util.List;

public class HrMainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioButton rb_index, rb_message, rb_my;
    private RadioGroup rg_bottom_bar;


    private HrIndexFragment indexFragment;
    private HrMessageFragment messageFragment;
    private HrMyFragment myFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr_main);

        initView();
    }


    private void initView() {
        rb_index = findViewById(R.id.rb_index);
        rb_message = findViewById(R.id.rb_message);
        rb_my = findViewById(R.id.rb_my);
        rg_bottom_bar = findViewById(R.id.rg_bottom_bar);

        //设置大小比例
        Drawable drIndex = getResources().getDrawable(R.drawable.bottom_bar_home_selector);
        Drawable drMessage = getResources().getDrawable(R.drawable.bottom_bar_message_selector);
        Drawable drMy = getResources().getDrawable(R.drawable.bottom_bar_my_selector);
        //改变图片的比例大小
        Rect rIndex = new Rect(0, 0, drIndex.getMinimumWidth() * 2 / 9, drIndex.getMinimumHeight() * 2 / 9);
        Rect rMessage = new Rect(0, 0, drMessage.getMinimumWidth() * 2 / 9, drMessage.getMinimumHeight() * 2 / 9);
        Rect rMy = new Rect(0, 0, drMy.getMinimumWidth() * 2 / 9, drMy.getMinimumHeight() * 2 / 9);
        drIndex.setBounds(rIndex);
        drMessage.setBounds(rMessage);
        drMy.setBounds(rMy);
        rb_index.setCompoundDrawables(null, drIndex, null, null);
        rb_message.setCompoundDrawables(null, drMessage, null, null);
        rb_my.setCompoundDrawables(null, drMy, null, null);

        //注册RadioGroup的事件监听
        rg_bottom_bar.setOnCheckedChangeListener(this);
        rb_index.setChecked(true);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        //创建碎片的事务
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //先隐藏掉所以的碎片
        hideAllFragmen(fragmentTransaction);
        switch (checkedId){
            case R.id.rb_index:
                if (indexFragment==null){
                    indexFragment=new HrIndexFragment();
                    fragmentTransaction.add(R.id.hr_fraglayout,indexFragment);
                }else {
                    fragmentTransaction.show(indexFragment);
                }
                break;
            case R.id.rb_message:
                if (messageFragment==null){
                    messageFragment=new HrMessageFragment();
                    messageFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
                        @Override
                        public void onListItemClicked(EMConversation conversation) {
                            startActivity(new Intent(getApplicationContext(), MessageActivity.class).putExtra(EaseConstant.EXTRA_USER_ID,conversation.conversationId()));
                        }
                    });
                    fragmentTransaction.add(R.id.hr_fraglayout,messageFragment);
                }else {
                    fragmentTransaction.show(messageFragment);
                }
                break;
            case R.id.rb_my:
                if (myFragment==null){
                    myFragment=new HrMyFragment();
                    fragmentTransaction.add(R.id.hr_fraglayout,myFragment);
                }else {
                    fragmentTransaction.show(myFragment);
                }
                break;
        }

        //提交事务
        fragmentTransaction.commit();

    }

    private void hideAllFragmen(FragmentTransaction fragmentTransaction) {
        if (indexFragment != null) {
            fragmentTransaction.hide(indexFragment);
        }
        if (messageFragment != null) {
            fragmentTransaction.hide(messageFragment);
        }
        if (myFragment != null) {
            fragmentTransaction.hide(myFragment);
        }
    }


}