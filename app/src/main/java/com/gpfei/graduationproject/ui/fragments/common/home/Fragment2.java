package com.gpfei.graduationproject.ui.fragments.common.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.WeekendAdapter;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.PartAndResume;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.beans.WeekendBean;
import com.gpfei.graduationproject.ui.activities.common.JobWebDetailsActivity;
import com.gpfei.graduationproject.utils.DividerItemDecoration;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.longsh.optionframelibrary.OptionCenterDialog;
import com.mob.MobSDK;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment2 extends Fragment implements View.OnClickListener {
    private RecyclerView rRecyclerview;
    private List<WeekendBean> datalist = new ArrayList<>();
    private RelativeLayout rl_load_view2;
    private Button btn_load2;
    private RelativeLayout rl_network_error2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        initView(view);
        loadList();
        return view;
    }

    private void initView(View view) {
        rRecyclerview = (RecyclerView) view.findViewById(R.id.wRecyclerview);
        rl_load_view2 = (RelativeLayout) view.findViewById(R.id.rl_load_view2);
        btn_load2 = (Button) view.findViewById(R.id.btn_load2);
        btn_load2.setOnClickListener(this);
        rl_network_error2 = (RelativeLayout) view.findViewById(R.id.rl_network_error2);


        MobSDK.submitPolicyGrantResult(true, null);

    }

    //加载列表数据
    private void loadList() {
        //获取后台数据
        BmobQuery<WeekendBean> query = new BmobQuery<WeekendBean>();
        query.findObjects(new FindListener<WeekendBean>() {
            @Override
            public void done(final List<WeekendBean> list, BmobException e) {
                if (e == null) {
                    //添加前清除集合数据先，防止数据添加重复
                    datalist.clear();
                    //添加数据到集合
                    datalist.addAll(list);
                    rRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                    WeekendAdapter adapter = new WeekendAdapter(getContext(), datalist,"兼职");
                    rRecyclerview.setItemAnimator(new DefaultItemAnimator());
                    rRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
                    rRecyclerview.setAdapter(adapter);
                    //隐藏加载view
                    rl_load_view2.setVisibility(View.GONE);
                    rl_network_error2.setVisibility(View.GONE);

                    adapter.setOnItemClickLitener(new WeekendAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            //点击事件
                            Intent intent = new Intent(getContext(), JobWebDetailsActivity.class);
                            intent.putExtra("url", datalist.get(position).getUrl());
                            startActivity(intent);
                        }

                        @Override
                        public void onItemLongClick(View view, int pos) {
                            //长按弹出列表提示框
                            final ArrayList<String> list = new ArrayList<>();
                            list.add("分享");
                            list.add("投递");
                            list.add("收藏");
                            list.add("取消");
                            final OptionCenterDialog optionCenterDialog = new OptionCenterDialog();
                            optionCenterDialog.show(getContext(), list);
                            optionCenterDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    switch (position) {
                                        case 0:
                                            String url = datalist.get(pos).getUrl();
                                            String title = datalist.get(pos).getTitle_weekend();
                                            share(url,title);
                                            break;
                                        case 1:
                                            setMessage(pos, "投递", true);
                                            break;
                                        case 2:
                                            setMessage(pos, "收藏", true);
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
                    rl_load_view2.setVisibility(View.GONE);
                    rl_network_error2.setVisibility(View.VISIBLE);
                    btn_load2.setText("重新加载");
//                    ToastUtils.showTextToast(getContext(), "出故障啦~请检查网络");
                }
            }
        });
    }

    private void share(String url,String title) {
        OnekeyShare oks = new OnekeyShare();
        // title标题，微信、QQ和QQ空间等平台使用
        //oks.setTitle(getString(R.string.share));
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(title+"招聘！");
        // setImageUrl是网络图片的url
        //oks.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
        // url在微信、Facebook等平台中使用
        oks.setUrl(url);
        // 启动分享GUI
        oks.show(MobSDK.getContext());
    }

    //封装投递与收藏状态
    private void setMessage(int pos, String str, Boolean state) {
        if (BmobUser.isLogin()) {
            User user = BmobUser.getCurrentUser(User.class);
            WeekendBean weekendBean = new WeekendBean();
            weekendBean.setObjectId(datalist.get(pos).getObjectId());
            final PartAndResume partAndResume = new PartAndResume();
            partAndResume.setWeekendBean(weekendBean);
            partAndResume.setUser(user);
            //收藏状态执行setMsg
            if (str.equals("收藏")) {
                partAndResume.setCollect(state);
                partAndResume.setDelivery(false);
            } else if (str.equals("投递")) {
                //投递状态执行setDelivery
                partAndResume.setDelivery(state);
                partAndResume.setCollect(false);
            }
            partAndResume.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        ToastUtils.showImageToast(getContext(), str + "成功");
                    } else {
                        ToastUtils.showTextToast(getContext(), str + "失败");
                        Log.e("出错", e.getMessage());
                    }
                }
            });
        } else {
            ToastUtils.showTextToast(getContext(), "请先登录");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load2:
                loadList();
                btn_load2.setText("加载中...");
                break;
        }
    }
}