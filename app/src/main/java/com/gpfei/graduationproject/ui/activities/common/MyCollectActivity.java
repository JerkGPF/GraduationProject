package com.gpfei.graduationproject.ui.activities.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.DayAdapter;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.utils.SmileToastView;
import com.gpfei.graduationproject.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyCollectActivity extends AppCompatActivity {
    private RecyclerView rRecyclerview;
    private List<DayBean> datalist = new ArrayList<>();
    private ImageView iv_back;
    private TextView tv_title;
    private LinearLayout ll_myCollect;
    static SmileToastView smileToastView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        initView();
        equal();

    }
    private void initView() {
        rRecyclerview = findViewById(R.id.rRecyclerview);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        ll_myCollect = findViewById(R.id.ll_myCollect);

        tv_title.setText("我的收藏");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }


    //先将所有的id查出来
    private void equal() {
        BmobQuery<SelectAndResume> selectAndResumeBmobQuery = new BmobQuery<>();
        selectAndResumeBmobQuery.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
        selectAndResumeBmobQuery.addWhereEqualTo("collect", true);
        selectAndResumeBmobQuery.findObjects(new FindListener<SelectAndResume>() {
            @Override
            public void done(List<SelectAndResume> object, BmobException e) {
                if (e == null) {
                    Toast smileToast=new Toast(MyCollectActivity.this);
                    //view布局
                    View smileView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_smile,null,false);
                    TextView text = smileView.findViewById(R.id.toastMessage);
                    text.setText("正在加载中，请稍后");
                    //给customToastView增加动画效果
                    smileToastView = smileView.findViewById(R.id.smileView);
                    smileToastView.startAnim();
                    text.setBackgroundResource(R.drawable.shape_toast_smile_text);
                    text.setTextColor(Color.parseColor("#FFFFFF"));
                    smileToast.setView(smileView);
                    smileToast.setDuration(Toast.LENGTH_SHORT);
                    smileToast.show();

                    //Toast.makeText(MyCollectActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                    String[] strings = new String[object.size()];//创建一个string类型的数组用来存储objectid
                    for (int i = 0; i < object.size(); i++) {
                        System.out.println(object.get(i));
                        strings[i] = object.get(i).getDayBean().getObjectId();
                        System.out.println(object.get(i).getDayBean().toString());
                        System.out.println(">>>>>>" + strings[i]);
                    }
                    //延迟加载，为了让toast动画展示完整
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadList(strings);//将objectId数组传递给loadlist()
                        }
                    },1500);
                } else {
                    Log.e("BMOB", e.toString());
                    Toast.makeText(MyCollectActivity.this, "查询失败，请重试" , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadList(String[] strings) {
        //一对多关联查询
        datalist.clear();
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
                        ll_myCollect.setVisibility(View.GONE);
                        for (SelectAndResume sa : list) {
                            //Log.d("debug", sa.toString());
                            Log.d("debug", sa.getdayBean().toString());
                            datalist.add(sa.getdayBean());
                            send(datalist);//将所有的数据发送给适配器
                        }
                    } else {
                        Toast.makeText(MyCollectActivity.this, "网络" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void send(List<DayBean> ls) {
        rRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DayAdapter adapter = new DayAdapter(getApplicationContext(), ls, "已收藏");
        rRecyclerview.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        rRecyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        rRecyclerview.setAdapter(adapter);
        adapter.setOnItemClickLitener(new DayAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                //点击事件
                Intent intent = new Intent(MyCollectActivity.this, JobWebDetailsActivity.class);
                intent.putExtra("url", ls.get(position).getUrl());
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }

}