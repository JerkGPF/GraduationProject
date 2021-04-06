package com.gpfei.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.SelectAndResume;
import com.gpfei.graduationproject.beans.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class DayAdapter extends RecyclerView.Adapter<com.gpfei.graduationproject.adapters.DayAdapter.MyViewHolder> {
    private List<DayBean> datalist = new ArrayList<>();
    private Context context;
    private String string;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public DayAdapter(Context context, List<DayBean> datalist,String string) {
        this.context = context;
        this.datalist = datalist;
        this.string = string;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_day, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_title_day.setText(datalist.get(position).getTitle_day());
        holder.tv_address_day.setText(datalist.get(position).getAddress_day());
        holder.tv_money_day.setText(datalist.get(position).getMoney_day() + "");
        holder.tv_company_day.setText(datalist.get(position).getCompany_day());
        holder.tv_time_day.setText(datalist.get(position).getCreatedAt());
        holder.tv_count_d.setText(datalist.get(position).getdCount() + "人看过");

        holder.tv_delivery_day.setText(string);


        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title_day;
        private TextView tv_money_day;
        private TextView tv_time_day;
        private TextView tv_address_day;
        private TextView tv_company_day;
        private TextView tv_count_d;
        private TextView tv_delivery_day;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_title_day = itemView.findViewById(R.id.tv_title_day);
            tv_time_day = itemView.findViewById(R.id.tv_time_day);
            tv_money_day = itemView.findViewById(R.id.tv_money_day);
            tv_address_day = itemView.findViewById(R.id.tv_address_day);
            tv_company_day = itemView.findViewById(R.id.tv_company_day);
            tv_count_d = itemView.findViewById(R.id.tv_count_d);
            tv_delivery_day = itemView.findViewById(R.id.tv_delivery_day);
        }
    }


}
