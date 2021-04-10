package com.gpfei.graduationproject.ui.fragments.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.WeekendAdapter;
import com.gpfei.graduationproject.beans.PartAndResume;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.beans.WeekendBean;
import com.gpfei.graduationproject.ui.activities.common.JobWebDetailsActivity;
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
import cn.bmob.v3.listener.UpdateListener;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PartTimeColloectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartTimeColloectFragment extends Fragment {

    private RecyclerView rRecyclerview;
    private List<WeekendBean> datalist = new ArrayList<>();
    private LinearLayout ll_myCollect;
    private PullToRefreshLayout refresh_job;
    String[] objId;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_time_collect, container, false);
        initView(view);
        equal();
        return view;
    }
    private void initView(View view) {
        ll_myCollect = view.findViewById(R.id.ll_myCollect);
        rRecyclerview = view.findViewById(R.id.rRecyclerview);
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
        BmobQuery<PartAndResume> partAndResumeBmobQuery = new BmobQuery<>();
        partAndResumeBmobQuery.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
        partAndResumeBmobQuery.addWhereEqualTo("collect", true);
        partAndResumeBmobQuery.findObjects(new FindListener<PartAndResume>() {
            @Override
            public void done(List<PartAndResume> object, BmobException e) {
                if (e == null) {
                    String[] strings = new String[object.size()];//创建一个string类型的数组用来存储objectid
                    for (int i = 0; i < object.size(); i++) {
                        strings[i] = object.get(i).getWeekendBean().getObjectId();
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
                    Toast.makeText(getActivity(), "查询失败，请重试" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loadList(String[] strings) {
        //一对多关联查询
        datalist.clear();
        for (int i = 0; i < strings.length; i++) {
            Log.d("debug", strings[i] + ">>>>>:");
            BmobQuery<PartAndResume> query = new BmobQuery<PartAndResume>();
            WeekendBean weekendBean = new WeekendBean();
            weekendBean.setObjectId(strings[i]);
            query.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
            query.addWhereEqualTo("weekendBean", new BmobPointer(weekendBean));
            query.addWhereEqualTo("collect", true);
            query.include("weekendBean");
            query.findObjects(new FindListener<PartAndResume>() {
                @Override
                public void done(List<PartAndResume> list, BmobException e) {
                    if (e == null) {
                        ll_myCollect.setVisibility(View.GONE);
                        for (PartAndResume sa : list) {
                            datalist.add(sa.getWeekendBean());
                            send(datalist);//将所有的数据发送给适配器
                            sendObjectId(sa.getObjectId());//将objectId发出

                        }
                    } else {
                        Toast.makeText(getActivity(), "网络" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void sendObjectId(String objectId) {
        objId = new String[datalist.size()];
        for (int i = 0; i < datalist.size(); i++) {
            objId[i] = objectId;
        }
    }

    private void handle(int i) {
        PartAndResume partAndResume = new PartAndResume();
        partAndResume.setCollect(false);
        partAndResume.update(objId[i], new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    SmileToast smileToast = new SmileToast();
                    smileToast.smile("取消完成");
                } else {
                    Toast.makeText(getActivity(), "取消失败" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void send(List<WeekendBean> ls) {
        rRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        WeekendAdapter adapter = new WeekendAdapter(getApplicationContext(), ls, "已收藏");
        rRecyclerview.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        rRecyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        rRecyclerview.setAdapter(adapter);
        adapter.setOnItemClickLitener(new WeekendAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                //点击事件
                Intent intent = new Intent(getActivity(), JobWebDetailsActivity.class);
                intent.putExtra("url", ls.get(position).getUrl());
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(View view, int pos) {
                //长按弹出列表提示框
                String yes = "<font color='#2EC667'>" + "是" + "</font>";
                String no = "<font color='#2EC667'>" + "否" + "</font>";
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setMessage("确定取消收藏吗？")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton(Html.fromHtml(no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(Html.fromHtml(yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //逻辑处理
                                handle(pos);
                                datalist.clear();
                                equal();
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }

        });

    }

}