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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.DayAdapter;
import com.gpfei.graduationproject.adapters.SelectionAdapter;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.SelectionBean;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.ui.activities.common.JobWebDetailsActivity;
import com.gpfei.graduationproject.ui.activities.hr.JobDetailsActivity;
import com.gpfei.graduationproject.ui.activities.hr.ModifyActivity;
import com.gpfei.graduationproject.ui.activities.hr.ModifyPracticeActivity;
import com.gpfei.graduationproject.utils.DividerItemDecoration;
import com.gpfei.graduationproject.utils.SmileToast;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.longsh.optionframelibrary.OptionCenterDialog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PracticePublishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PracticePublishFragment extends Fragment {

    private RecyclerView rRecyclerview;
    private LinearLayout ll_myapply;
    private PullToRefreshLayout refresh_job;
    private List<SelectionBean> datalist = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_full_time, container, false);
        initView(view);
        queryFullAuthor();
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
                        queryFullAuthor();
                        //????????????
                        refresh_job.finishRefresh();
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
                        ToastUtils.showTextToast(getContext(),"????????????????????????~");
                        //??????????????????
                        refresh_job.finishLoadMore();
                    }
                }, 2000);
            }
        });
    }

    /**
     * ???????????????????????????????????????????????????????????????
     */
    private void queryFullAuthor() {
        if (BmobUser.isLogin()) {
            BmobQuery<SelectionBean> query = new BmobQuery<>();
            query.addWhereEqualTo("author", BmobUser.getCurrentUser(User.class));
            query.order("-updatedAt");
            //??????????????????
            query.include("author");
            query.findObjects(new FindListener<SelectionBean>() {
                @Override
                public void done(List<SelectionBean> object, BmobException e) {
                    if (e == null) {
                        ll_myapply.setVisibility(View.GONE);
                        //?????????????????????????????????????????????????????????
                        datalist.clear();
                        //?????????????????????
                        datalist.addAll(object);
                        rRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                        SelectionAdapter adapter = new SelectionAdapter(getContext(), datalist,"??????");
                        rRecyclerview.setItemAnimator(new DefaultItemAnimator());
                        //???????????????
                        rRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
                        rRecyclerview.setAdapter(adapter);
                        adapter.setOnItemClickLitener(new SelectionAdapter.OnItemClickLitener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //????????????
                                Intent intent = new Intent(getContext(), JobDetailsActivity.class);
                                intent.putExtra("url", datalist.get(position).getUrl_selection());
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int pos) {
                                //???????????????????????????
                                final ArrayList<String> list = new ArrayList<>();
                                list.add("??????");
                                list.add("??????");
                                list.add("??????");
                                final OptionCenterDialog optionCenterDialog = new OptionCenterDialog();
                                optionCenterDialog.show(getContext(), list);
                                optionCenterDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        switch (position) {
                                            case 0:
                                                Intent intent = new Intent(getContext(), ModifyPracticeActivity.class);
                                                intent.putExtra("modify_practice", object.get(pos).getObjectId());
                                                startActivity(intent);
                                                break;
                                            case 1:
                                                SelectionBean selectionBean = new SelectionBean();
                                                selectionBean.setObjectId(object.get(pos).getObjectId());
                                                selectionBean.delete(new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if(e==null){
                                                            ToastUtils.showTextToast(getActivity(),"????????????");
                                                        }else{
                                                            Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                });
                                                break;
                                            default:
                                                break;
                                        }
                                        optionCenterDialog.dismiss();
                                    }
                                });
                            }


                        });
                    } else {
                        Log.e("BMOB", e.toString());
                        Toast.makeText(getActivity(), "????????????" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } else {
            Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
        }

    }
}