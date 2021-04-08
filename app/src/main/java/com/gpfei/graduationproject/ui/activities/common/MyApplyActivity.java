package com.gpfei.graduationproject.ui.activities.common;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.DayAdapter;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.utils.DividerItemDecoration;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyApplyActivity extends AppCompatActivity implements Runnable {
    private RecyclerView rRecyclerview;
    private List<DayBean> datalist = new ArrayList<>();
    private ImageView iv_back;
    private TextView tv_title;
    private LinearLayout ll_myapply;
    String[] strings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_apply);

        initView();
        equal();

    }

    private void initView() {
        rRecyclerview = findViewById(R.id.rRecyclerview);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        ll_myapply = findViewById(R.id.ll_myapply);

        tv_title.setText("我的投递");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                loadList();
//                new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
//                        equal();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                loadList();
//                            }
//                        });
//                    }
//                }.run();
            }
        });
    }

    //先将所有的id查出来
    private void equal() {
        BmobQuery<SelectAndResume> selectAndResumeBmobQuery = new BmobQuery<>();
        selectAndResumeBmobQuery.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
        selectAndResumeBmobQuery.addWhereEqualTo("delivery", true);
        selectAndResumeBmobQuery.findObjects(new FindListener<SelectAndResume>() {
            @Override
            public void done(List<SelectAndResume> object, BmobException e) {
                if (e == null) {
                    strings = new String[object.size()];
                    Toast.makeText(MyApplyActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < object.size(); i++) {
                        //strings = new String[object.size()];
                        System.out.println(object.get(i));
                        strings[i] = object.get(i).getDayBean().getObjectId();
                        System.out.println(object.get(i).getDayBean().toString());
                        System.out.println(">>>>>>" + strings[i]);
                    }
                } else {
                    Log.e("BMOB", e.toString());
                    Toast.makeText(MyApplyActivity.this, "查询失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadList();
            }
        }, 2000);
    }

    private void loadList() {
        //一对多关联查询
        //datalist.clear();
            for (int i = 0; i < strings.length; i++) {
                Log.d("debug", strings[i] + ">>>>>:");
                BmobQuery<SelectAndResume> query = new BmobQuery<SelectAndResume>();
                DayBean dayBean = new DayBean();
                dayBean.setObjectId(strings[i]);
                query.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
                query.addWhereEqualTo("dayBean", new BmobPointer(dayBean));
                query.include("dayBean");
                query.findObjects(new FindListener<SelectAndResume>() {
                    @Override
                    public void done(List<SelectAndResume> list, BmobException e) {
                        if (e == null) {
                            ll_myapply.setVisibility(View.GONE);
                            for (SelectAndResume sa : list) {
                                //Log.d("debug", sa.toString());
                                Log.d("debug", sa.getdayBean().toString());
                                datalist.add(sa.getdayBean());
                            }
                        } else {
                            Toast.makeText(MyApplyActivity.this, "网络" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        }
        rRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DayAdapter adapter = new DayAdapter(getApplicationContext(), datalist, "已投递");
        rRecyclerview.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        rRecyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        rRecyclerview.setAdapter(adapter);
        adapter.setOnItemClickLitener(new DayAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                //点击事件
                Intent intent = new Intent(MyApplyActivity.this, JobWebDetailsActivity.class);
                intent.putExtra("url", datalist.get(position).getUrl());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    public void run() {

    }
}