package com.gpfei.graduationproject.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.MyUser;
import com.gpfei.graduationproject.ui.activities.common.MyInfoActivity;

import java.util.ArrayList;
import java.util.List;

public class HRIndexAdapter extends RecyclerView.Adapter<HRIndexAdapter.MyViewHolder> {
    private Context context;
    private List<MyUser> datalist = new ArrayList<>();
    private HRIndexAdapter.OnItemClickLitener mOnItemClickLitener;


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }


    public void setOnItemClickLitener(HRIndexAdapter.OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public HRIndexAdapter(Context context, List<MyUser> datalist) {
        this.context = context;
        this.datalist = datalist;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_hr_index, parent, false);
        HRIndexAdapter.MyViewHolder holder = new HRIndexAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_hr_index_name.setText(datalist.get(position).getName());
        if (datalist.get(position).getSex()){
            holder.tv_hr_index_sex.setText("男");
        }
        holder.tv_hr_index_age.setText(datalist.get(position).getBirthday());
        holder.tv_hr_index_phone.setText(datalist.get(position).getMobilePhoneNumber());
        holder.tv_hr_index_exp.setText(datalist.get(position).getExperience());

        //头像
        Glide.with(context).load(datalist.get(position).getHead()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.iv_head) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.iv_head.setImageDrawable(circularBitmapDrawable);
            }
        });

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_hr_index_name,
                tv_hr_index_sex, tv_hr_index_exp,
                tv_hr_index_phone,tv_hr_index_age;
        private ImageView iv_head;



        public MyViewHolder(View itemView) {
            super(itemView);
            tv_hr_index_name = itemView.findViewById(R.id.tv_hr_index_name);
            tv_hr_index_sex = itemView.findViewById(R.id.tv_hr_index_sex);
            tv_hr_index_exp = itemView.findViewById(R.id.tv_hr_index_exp);
            tv_hr_index_phone = itemView.findViewById(R.id.tv_hr_index_phone);
            tv_hr_index_age = itemView.findViewById(R.id.tv_hr_index_age);
            iv_head = itemView.findViewById(R.id.iv_hr_index_head);
        }
    }

}
