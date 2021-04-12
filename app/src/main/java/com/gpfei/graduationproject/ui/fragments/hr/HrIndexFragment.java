package com.gpfei.graduationproject.ui.fragments.hr;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.DayAdapter;
import com.gpfei.graduationproject.adapters.HRIndexAdapter;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.HrUser;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.ui.activities.common.JobWebDetailsActivity;
import com.gpfei.graduationproject.ui.activities.common.MyDataActivity;
import com.gpfei.graduationproject.ui.activities.hr.HrCheckUserInfoActivity;
import com.gpfei.graduationproject.ui.activities.hr.PublishActivity;
import com.gpfei.graduationproject.utils.DividerItemDecoration;
import com.gpfei.graduationproject.utils.SmileToast;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HrIndexFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HrIndexFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private List<MyUser> datalist = new ArrayList<>();
    private PullToRefreshLayout refresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hr_index, container, false);
        initView(view);
        equal();
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rRecyclerview);
        refresh = view.findViewById(R.id.refresh);
        refresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        equal();
                        //结束刷新
                        refresh.finishRefresh();
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                SmileToast smileToast = new SmileToast();
                smileToast.smile("加载完成");
                refresh.finishLoadMore();
            }
        });
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                Intent intent = new Intent(getActivity(), PublishActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * isHR为User进行HR的判断
     */
    private void equal() {
        BmobQuery<MyUser> myUserBmobQuery = new BmobQuery<>();
        myUserBmobQuery.addWhereEqualTo("isHR", false);
        myUserBmobQuery.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(final List<MyUser> object, BmobException e) {
                if (e == null) {
                    //添加前清除集合数据先，防止数据添加重复
                    datalist.clear();
                    datalist.addAll(object);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    HRIndexAdapter adapter = new HRIndexAdapter(getContext(), datalist);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    //添加分割线
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickLitener(new HRIndexAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            //点击事件
                            Intent intent = new Intent(getContext(), HrCheckUserInfoActivity.class);
                            intent.putExtra("objectId", datalist.get(position).getObjectId());
                            Log.d("objectId>>>>>>>>>>>>>>>",datalist.get(position).getObjectId());
                            startActivity(intent);
                        }
                        @Override
                        public void onItemLongClick(View view, int position) {

                        }
                    });
                    for (MyUser user:object){
                        Log.d("object>>>>>",user.getUsername());
                    }
                } else {
                    Log.e("BMOB", e.toString());
                    Toast.makeText(getActivity(), "错误"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}