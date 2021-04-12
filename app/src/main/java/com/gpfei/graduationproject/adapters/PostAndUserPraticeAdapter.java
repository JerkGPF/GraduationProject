package com.gpfei.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.PostAndUserPratice;

import java.util.ArrayList;
import java.util.List;

public class PostAndUserPraticeAdapter extends RecyclerView.Adapter<com.gpfei.graduationproject.adapters.PostAndUserPraticeAdapter.MyViewHolder> {
    private List<PostAndUserPratice> datalist = new ArrayList<>();
    private Context context;
    private String string;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private PostAndUserPraticeAdapter.OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(PostAndUserPraticeAdapter.OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public PostAndUserPraticeAdapter(Context context, List<PostAndUserPratice> datalist, String string) {
        this.context = context;
        this.datalist = datalist;
        this.string = string;
    }

    @NonNull
    @Override
    public PostAndUserPraticeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hr_post_user_info, parent, false);
        PostAndUserPraticeAdapter.MyViewHolder holder = new PostAndUserPraticeAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostAndUserPraticeAdapter.MyViewHolder holder, int position) {
        holder.tv_title_day.setText(datalist.get(position).getTitle());
        holder.tv_address_day.setText(datalist.get(position).getAddress());
        holder.tv_money_day.setText(datalist.get(position).getMoney());
        holder.tv_company_day.setText(datalist.get(position).getCompanyName());
        holder.tv_name.setText(datalist.get(position).getUseNname());
        holder.tv_phone.setText(datalist.get(position).getPhoneNumber());

        if (datalist.get(position).getSex()){
            holder.tv_sex.setText("男");
        }else {
            holder.tv_sex.setText("女");
        }

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
        private TextView tv_name;
        private TextView tv_address_day;
        private TextView tv_company_day;
        private TextView tv_phone;
        private TextView tv_sex;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_title_day = itemView.findViewById(R.id.tv_title_day);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_money_day = itemView.findViewById(R.id.tv_money_day);
            tv_address_day = itemView.findViewById(R.id.tv_address_day);
            tv_company_day = itemView.findViewById(R.id.tv_company_day);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            tv_sex = itemView.findViewById(R.id.tv_sex);
        }
    }
}
