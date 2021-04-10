package com.gpfei.graduationproject.ui.fragments.hr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.HrUser;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.ui.activities.common.MyApplyActivity;
import com.gpfei.graduationproject.utils.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HrMyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HrMyFragment extends Fragment {
    private Button btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hr_my, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        btn = view.findViewById(R.id.show);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryAuthor();
            }
        });
    }

    /**
     * 查询一对一关联，查询当前用户发表的所有职位
     */
    private void queryAuthor() {
        if (BmobUser.isLogin()) {
            BmobQuery<DayBean> query = new BmobQuery<>();
            query.addWhereEqualTo("author", BmobUser.getCurrentUser(User.class));
            query.order("-updatedAt");
            //包含作者信息
            query.include("author");
            query.findObjects(new FindListener<DayBean>() {
                @Override
                public void done(List<DayBean> object, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "查询成功", Toast.LENGTH_SHORT).show();
                        String[] str = new String[object.size()];
                        //将所有的信息全部遍历出来
                        for (int i = 0; i < object.size(); i++) {
                            str[i] = object.get(i).getObjectId();
                            System.out.println("dayBean:"+object.get(i).toString());
                        }
                        loadList(str);//将objectId数组传递给loadlist()
                        Toast.makeText(getActivity(), "职位信息"+object.get(0).toString(), Toast.LENGTH_SHORT).show();
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
    private void loadList(String[] str) {
        for (int i = 0; i < str.length; i++) {
            Log.d("debug", str[i] + ">>>>>:");
            BmobQuery<SelectAndResume> query = new BmobQuery<SelectAndResume>();
            DayBean dayBean = new DayBean();
            dayBean.setObjectId(str[i]);
            query.addWhereEqualTo("delivery", true);
            query.addWhereEqualTo("dayBean", new BmobPointer(dayBean));
            query.include("user,dayBean.author");
            query.findObjects(new FindListener<SelectAndResume>() {
                @Override
                public void done(List<SelectAndResume> list, BmobException e) {
                    if (e == null){
                        for (SelectAndResume sa : list) {
                            Log.d("hello", sa.getUser().toString());
                            System.out.println("////////////"+sa.getUser().getUsername());
                            System.out.println("////////////"+sa.getUser().getMobilePhoneNumber());
                            System.out.println("////////////"+sa.getUser().getEmail());
                        }
                        Toast.makeText(getActivity(), "用户姓名"+list.get(0).getUser().getUsername(), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(), "网络故障，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}