package com.gpfei.graduationproject.ui.fragments.hr;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.adapters.FindsAadapter;
import com.gpfei.graduationproject.adapters.MessageAdapter;
import com.gpfei.graduationproject.beans.Message;
import com.gpfei.graduationproject.ui.activities.common.HelpActivity;
import com.gpfei.graduationproject.ui.activities.common.MyIntegralActivity;
import com.gpfei.graduationproject.utils.ToastUtils;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HrMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HrMessageFragment extends EaseConversationListFragment {
    @Override
    protected void initView() {
        super.initView();
        hideTitleBar();
        initData();
    }



    private void initData() {
        // run in a second
        final long timeInterval = 1000;
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    // ------- code for task to run
                    conversationListView.refresh();
                    // ------- ends here
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
