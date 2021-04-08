package com.gpfei.graduationproject.ui.activities.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.DayAdapter;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyCollectActivity extends AppCompatActivity {
    private RecyclerView rRecyclerview;
    private List<DayBean> datalist = new ArrayList<>();
    private ImageView iv_back;
    private TextView tv_title;
    private LinearLayout ll_myCollect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        loadList();
        initView();
    }



    private void initView() {
        rRecyclerview = findViewById(R.id.rRecyclerview);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ll_myCollect = findViewById(R.id.ll_myCollect);
        tv_title.setText("我的收藏");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadList() {
        //一对多关联查询
        BmobQuery<SelectAndResume> query = new BmobQuery<SelectAndResume>();
        DayBean dayBean = new DayBean();
        //dayBean.setObjectId(datalist.get(0).getObjectId());
        dayBean.setObjectId("de0f3d182c");
        query.addWhereEqualTo("dayBean", new BmobPointer(dayBean));
        query.include("dayBean");
        query.findObjects(new FindListener<SelectAndResume>() {
            @Override
            public void done(List<SelectAndResume> list, BmobException e) {
                if (e == null) {
                    ll_myCollect.setVisibility(View.GONE);
                    //添加前清除集合数据先，防止数据添加重复
                    datalist.clear();
                    //添加数据到集合
                    for (int i = 0; i < list.size(); i++) {
                        System.out.println("布尔" + list.get(i).getCollect());
                        if (list.get(i).getCollect()) {
                            datalist.add(list.get(i).getDayBean());
                            System.out.println("dataList:+++++++++" + datalist.toString());
                        }
                    }
                    rRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    DayAdapter adapter = new DayAdapter(getApplicationContext(), datalist,"收藏");
                    rRecyclerview.setItemAnimator(new DefaultItemAnimator());
                    //添加分割线
                    rRecyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
                    rRecyclerview.setAdapter(adapter);
                    adapter.setOnItemClickLitener(new DayAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            //点击事件
                            Intent intent = new Intent(MyCollectActivity.this, JobWebDetailsActivity.class);
                            intent.putExtra("url", datalist.get(position).getUrl());
                            startActivity(intent);
                        }
                        @Override
                        public void onItemLongClick(View view, int position) {

                        }
                    });
                    System.out.println("小说" + list.toString());
                    for (int i = 0; i < list.size(); i++) {
                        System.out.println("标题：" + list.get(i).getDayBean().getTitle_day());
                    }
                } else {
                    Toast.makeText(MyCollectActivity.this, "网络" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}