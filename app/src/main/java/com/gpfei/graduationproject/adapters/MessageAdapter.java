package com.gpfei.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.Message;

import java.util.List;

/**
 * Created by SuHongJin on 2018/7/27.
 */

public class MessageAdapter extends RecyclerView.Adapter<com.gpfei.graduationproject.adapters.MessageAdapter.MyViewHolder> implements View.OnClickListener{
    private Context context;
    private List<Message> datalist;
    /**
     * 点击事件
     * @param context
     * @param datalist
     */
    private com.gpfei.graduationproject.adapters.FindsAadapter.OnItemClickListener mItemClickListener=null;

    public void setmItemClickListener(com.gpfei.graduationproject.adapters.FindsAadapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    public MessageAdapter(Context context, List<Message> datalist) {
        this.context=context;
        this.datalist=datalist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
        MyViewHolder viewholder=new MyViewHolder(view);
        //为每个item添加监听事件
        view.setOnClickListener(this);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_message_title.setText(datalist.get(position).getmTitle());
        holder.tv_message_title_child.setText(datalist.get(position).getmContent());
        Glide.with(context).load(datalist.get(position).getmImage()).into(holder.iv_message_icon);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener!=null){
            mItemClickListener.onItemClick((Integer) v.getTag());
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_message_icon;
        private TextView tv_message_title;
        private TextView tv_message_title_child;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_message_icon=(ImageView)itemView.findViewById(R.id.iv_message_icon);
            tv_message_title=(TextView)itemView.findViewById(R.id.tv_message_title);
            tv_message_title_child=(TextView)itemView.findViewById(R.id.tv_message_title_child);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
