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
import com.gpfei.graduationproject.beans.User;
import com.gpfei.graduationproject.utils.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
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
     * 查询一对一关联，查询当前用户发表的所有帖子
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
                        //将所有的信息全部遍历出来
                        for (int i = 0; i < object.size(); i++) {
                            System.out.println(object.get(i).getCompany_day());
                            System.out.println(object.get(i).toString());
                        }
                        for (DayBean dayBean : object) {
                            System.out.println(dayBean.getTitle_day());
                        }
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

}