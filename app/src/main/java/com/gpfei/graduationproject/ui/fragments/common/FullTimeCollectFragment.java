package com.gpfei.graduationproject.ui.fragments.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.DayAdapter;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.ui.activities.common.JobWebDetailsActivity;
import com.gpfei.graduationproject.utils.DividerItemDecoration;
import com.gpfei.graduationproject.utils.SmileToast;
import com.gpfei.graduationproject.utils.view.SmileToastView;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.longsh.optionframelibrary.OptionCenterDialog;

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
 * Use the {@link FullTimeCollectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FullTimeCollectFragment extends Fragment {
    private RecyclerView rRecyclerview;
    private List<DayBean> datalist = new ArrayList<>();
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
                        ToastUtils.showTextToast(getContext(), "????????????????????????~");
                        //??????????????????
                        refresh_job.finishLoadMore();
                    }
                }, 2000);
            }
        });
    }

    //???????????????id?????????
    private void equal() {
        BmobQuery<SelectAndResume> selectAndResumeBmobQuery = new BmobQuery<>();
        selectAndResumeBmobQuery.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
        selectAndResumeBmobQuery.addWhereEqualTo("collect", true);
        selectAndResumeBmobQuery.findObjects(new FindListener<SelectAndResume>() {
            @Override
            public void done(List<SelectAndResume> object, BmobException e) {
                if (e == null) {
                    String[] strings = new String[object.size()];//????????????string???????????????????????????objectid
                    for (int i = 0; i < object.size(); i++) {
                        System.out.println(object.get(i));
                        strings[i] = object.get(i).getDayBean().getObjectId();
                        System.out.println(object.get(i).getDayBean().toString());
                        Log.d("debug123", object.get(i).getObjectId());
                        System.out.println(">>>>>>" + strings[i]);
                    }
                    //????????????????????????toast??????????????????
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadList(strings);//???objectId???????????????loadlist()
                        }
                    }, 1500);
                } else {
                    Log.e("BMOB", e.toString());
                    Toast.makeText(getActivity(), "????????????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadList(String[] strings) {
        //?????????????????????
        datalist.clear();
        for (int i = 0; i < strings.length; i++) {
            Log.d("debug", strings[i] + ">>>>>:");
            BmobQuery<SelectAndResume> query = new BmobQuery<SelectAndResume>();
            DayBean dayBean = new DayBean();
            dayBean.setObjectId(strings[i]);
            query.addWhereEqualTo("user", BmobUser.getCurrentUser(User.class));
            query.addWhereEqualTo("dayBean", new BmobPointer(dayBean));
            query.addWhereEqualTo("collect", true);
            query.include("dayBean");
            query.findObjects(new FindListener<SelectAndResume>() {
                @Override
                public void done(List<SelectAndResume> list, BmobException e) {
                    if (e == null) {
                        ll_myCollect.setVisibility(View.GONE);
                        for (SelectAndResume sa : list) {
                            //Log.d("debug", sa.toString());
                            Log.d("debug>>", sa.getObjectId());
                            datalist.add(sa.getdayBean());
                            send(datalist);//????????????????????????????????????
                            sendObjectId(sa.getObjectId());//???objectId??????
                        }
                    } else {
                        Toast.makeText(getActivity(), "??????" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        SelectAndResume selectAndResume = new SelectAndResume();
        selectAndResume.setCollect(false);
        selectAndResume.update(objId[i], new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    SmileToast smileToast = new SmileToast();
                    smileToast.smile("????????????");
                } else {
                    Toast.makeText(getActivity(), "????????????" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void send(List<DayBean> ls) {
        rRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DayAdapter adapter = new DayAdapter(getApplicationContext(), ls, "?????????");
        rRecyclerview.setItemAnimator(new DefaultItemAnimator());
        //???????????????
        rRecyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        rRecyclerview.setAdapter(adapter);
        adapter.setOnItemClickLitener(new DayAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                //????????????
                Intent intent = new Intent(getActivity(), JobWebDetailsActivity.class);
                intent.putExtra("url", ls.get(position).getUrl());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int pos) {
                //???????????????????????????
                String yes = "<font color='#2EC667'>" + "???" + "</font>";
                String no = "<font color='#2EC667'>" + "???" + "</font>";
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setMessage("????????????????????????")//????????????????????????
                        //????????????????????????
                        .setNegativeButton(Html.fromHtml(no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(Html.fromHtml(yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //????????????
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