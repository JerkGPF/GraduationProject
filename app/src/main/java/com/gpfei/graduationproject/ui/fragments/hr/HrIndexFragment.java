package com.gpfei.graduationproject.ui.fragments.hr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.HrUser;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HrIndexFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HrIndexFragment extends Fragment implements View.OnClickListener {
    private EditText et_hr_company_job;
    private EditText et_hr_company_salary;
    private EditText et_hr_company_name;
    private EditText et_hr_company_place;
    private EditText et_hr_company_url;
    private Button btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hr_index, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        et_hr_company_job = view.findViewById(R.id.et_hr_company_job);
        et_hr_company_salary = view.findViewById(R.id.et_hr_company_salary);
        et_hr_company_name = view.findViewById(R.id.et_hr_company_name);
        et_hr_company_place = view.findViewById(R.id.et_hr_company_place);
        et_hr_company_url = view.findViewById(R.id.et_hr_company_url);
        btn = view.findViewById(R.id.submit);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                saveJob();
                break;
        }
    }

    /**
     * 添加一对一关联，当前用户发布职位
     */
    private void saveJob() {
        if (BmobUser.isLogin()){
            DayBean dayBean = new DayBean();
            dayBean.setTitle_day(et_hr_company_job.getText().toString().trim());
            dayBean.setCompany_day(et_hr_company_name.getText().toString().trim());
            dayBean.setAddress_day(et_hr_company_place.getText().toString().trim());
            dayBean.setMoney_day(et_hr_company_salary.getText().toString().trim());
            dayBean.setUrl(et_hr_company_url.getText().toString().trim());
            //添加一对一关联，用户关联发表职位
            dayBean.setAuthor(BmobUser.getCurrentUser(HrUser.class));
            dayBean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "职位发布成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("BMOB", e.toString());
                        Toast.makeText(getActivity(), "出现问题"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
        }
    }
}