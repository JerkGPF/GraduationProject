package com.gpfei.graduationproject.ui.fragments.common;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.DayAdapter;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.ui.activities.common.JobWebDetailsActivity;
import com.gpfei.graduationproject.ui.activities.common.MyApplyActivity;
import com.gpfei.graduationproject.utils.DividerItemDecoration;
import com.gpfei.graduationproject.utils.SmileToast;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FullTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FullTimeFragment extends Fragment {

    private RecyclerView rRecyclerview;
    private List<DayBean> datalist = new ArrayList<>();
    private LinearLayout ll_myapply;
    private PullToRefreshLayout refresh_job;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_time, container, false);
        initView(view);
        equal();

        return view;
    }

    private void initView(View view) {
        rRecyclerview = view.findViewById(R.id.rRecyclerview);
        ll_myapply = view.findViewById(R.id.ll_myapply);
        refresh_job = view.findViewById(R.id.refresh_job);

        refresh_job.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        equal();
                        //结束刷新
                        refresh_job.finishRefresh();
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
                        ToastUtils.showTextToast(getContext(),"没有更多内容了哟~");
                        //结束加载更多
                        refresh_job.finishLoadMore();
                    }
                }, 2000);
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
                    String[] strings = new String[object.size()];//创建一个string类型的数组用来存储objectid
                    for (int i = 0; i < object.size(); i++) {
                        System.out.println(object.get(i));
                        strings[i] = object.get(i).getDayBean().getObjectId();
                        System.out.println(object.get(i).getDayBean().toString());
                        System.out.println(">>>>>>" + strings[i]);
                    }
                    loadList(strings);//将objectId数组传递给loadlist()
                } else {
                    Log.e("BMOB", e.toString());
                    Toast.makeText(getActivity(), "查询失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            query.addWhereEqualTo("delivery", true);
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
                            send(datalist);//将所有的数据发送给适配器
                        }
                    } else {
                        Toast.makeText(getActivity(), "网络" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void send(List<DayBean> ls) {
        rRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DayAdapter adapter = new DayAdapter(getApplicationContext(), ls, "全职");
        rRecyclerview.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        rRecyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        rRecyclerview.setAdapter(adapter);
        adapter.setOnItemClickLitener(new DayAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                //点击事件
                Intent intent = new Intent(getContext(), JobWebDetailsActivity.class);
                intent.putExtra("url", ls.get(position).getUrl());
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }
}