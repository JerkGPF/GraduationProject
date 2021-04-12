package com.gpfei.graduationproject.ui.fragments.hr;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.PostAndUserAdapter;
import com.gpfei.graduationproject.adapters.PostAndUserPartAdapter;
import com.gpfei.graduationproject.beans.PartAndResume;
import com.gpfei.graduationproject.beans.PostAndUserPart;
import com.gpfei.graduationproject.beans.PracticeAndResume;
import com.gpfei.graduationproject.beans.SelectionBean;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.beans.WeekendBean;
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
 * Use the {@link PracticePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PracticePostFragment extends Fragment {
    private RecyclerView rRecyclerview;
    private List<PracticeAndResume> datalist = new ArrayList<>();
    private LinearLayout ll_myCollect;
    private PullToRefreshLayout refresh_job;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_practice_post, container, false);
        initView(view);
        queryAuthor();

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
                        queryAuthor();
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
                        ToastUtils.showTextToast(getContext(), "没有更多内容了哟~");
                        //结束加载更多
                        refresh_job.finishLoadMore();
                    }
                }, 2000);
            }
        });

    }

    /**
     * 查询一对一关联，查询当前用户发表的所有职位
     */
    private void queryAuthor() {
        if (BmobUser.isLogin()) {
            BmobQuery<SelectionBean> query = new BmobQuery<>();
            query.addWhereEqualTo("author", BmobUser.getCurrentUser(User.class));
            query.order("-updatedAt");
            //包含作者信息
            query.include("author");
            query.findObjects(new FindListener<SelectionBean>() {
                @Override
                public void done(List<SelectionBean> object, BmobException e) {
                    if (e == null) {
                        String[] str = new String[object.size()];
                        //将所有的信息全部遍历出来
                        for (int i = 0; i < object.size(); i++) {
                            str[i] = object.get(i).getObjectId();
                            System.out.println("dayBean:"+object.get(i).toString());
                        }
                        loadList(object);//将objectId数组传递给loadlist()
                    } else {
                        Log.e("BMOB", e.toString());
                        Toast.makeText(getActivity(), "查询失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
        }
    }
    //查询出当前所有投递本公司的人员信息
    private void loadList(List<SelectionBean> str) {
        //添加前清除集合数据先，防止数据添加重复
        datalist.clear();
        for (int i = 0; i < str.size(); i++) {
            BmobQuery<PracticeAndResume> query = new BmobQuery<PracticeAndResume>();
            SelectionBean selectionBean = new SelectionBean();
            selectionBean.setObjectId(str.get(i).getObjectId());
            query.addWhereEqualTo("delivery", true);
            query.addWhereEqualTo("selectionBean", new BmobPointer(selectionBean));
            query.include("user,selectionBean.author");
            query.findObjects(new FindListener<PracticeAndResume>() {
                @Override
                public void done(List<PracticeAndResume> list, BmobException e) {
                    if (e == null){
                        ll_myCollect.setVisibility(View.GONE);
                        for (PracticeAndResume sa : list) {
//                            sa.getDayBean().getTitle_day();
//                            sa.getDayBean().getAddress_day();
//                            sa.getDayBean().getMoney_day();
//                            sa.getDayBean().getCompany_day();
//                            sa.getMyUser().getName();
//                            sa.getMyUser().getMobilePhoneNumber();
//                            sa.getMyUser().getSex();
                            datalist.add(sa);
                            send(datalist);

                            Log.d("hello", sa.getUser().toString());
                            //System.out.println("////////////"+sa.getUser().toString());
                            System.out.println("////////////"+sa.getUser().getUsername());
                            System.out.println("////////////"+sa.getUser().getMobilePhoneNumber());
                            System.out.println("////////////"+sa.getUser().getEmail());
                        }
                    }else {
                        Toast.makeText(getActivity(), "网络故障，请重试"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void send(List<PracticeAndResume> datalist) {
        List<PostAndUserPart> list = new ArrayList<>();
        for (PracticeAndResume sa:datalist){
            PostAndUserPart postAndUser = new PostAndUserPart();
            postAndUser.setAddress(sa.getSelectionBean().getAddress_selection());
            postAndUser.setCompanyName(sa.getSelectionBean().getCompany_selection());
            postAndUser.setTitle(sa.getSelectionBean().getTitle_selection());
            postAndUser.setMoney(sa.getSelectionBean().getMoney_selection());
            postAndUser.setUseNname(sa.getUser().getName());
            postAndUser.setPhoneNumber(sa.getUser().getMobilePhoneNumber());
            postAndUser.setSex(sa.getUser().getSex());
            list.add(postAndUser);
        }
        rRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        PostAndUserPartAdapter adapter = new PostAndUserPartAdapter(getApplicationContext(), list, "");
        rRecyclerview.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        rRecyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        rRecyclerview.setAdapter(adapter);
        adapter.setOnItemClickLitener(new PostAndUserAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                //点击事件联系对方
            }

            @Override
            public void onItemLongClick(View view, int pos) {

            }
        });
    }
}