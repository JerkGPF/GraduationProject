package com.gpfei.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.gpfei.graduationproject.R;
import com.gpfei.graduationproject.beans.SelectionBean;

import java.util.ArrayList;
import java.util.List;


public class SelectionAdapter extends RecyclerView.Adapter<com.gpfei.graduationproject.adapters.SelectionAdapter.MyViewHolder> {
    private List<SelectionBean> datalist=new ArrayList<>();
    private Context context;
    private String string;


    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public SelectionAdapter(Context context, List<SelectionBean> datalist,String string) {
        this.context=context;
        this.datalist=datalist;
        this.string = string;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_selection,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_title_selection.setText(datalist.get(position).getTitle_selection());
        holder.tv_address_selection.setText(datalist.get(position).getAddress_selection());
        holder.tv_money_selection.setText(datalist.get(position).getMoney_selection()+"");
        holder.tv_company_selection.setText(datalist.get(position).getCompany_selection());
        holder.tv_time_selection.setText(datalist.get(position).getCreatedAt());
        holder.tv_count_s.setText(datalist.get(position).getsCount()+"人看过");
        holder.tv_delivery_day.setText(string);


        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_title_selection;
        private TextView tv_money_selection;
        private TextView tv_time_selection;
        private TextView tv_address_selection;
        private TextView tv_company_selection;
        private TextView tv_count_s;
        private TextView tv_delivery_day;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv_title_selection=(TextView)itemView.findViewById(R.id.tv_title_selection);
            tv_time_selection=(TextView)itemView.findViewById(R.id.tv_time_selection);
            tv_money_selection=(TextView)itemView.findViewById(R.id.tv_money_selection);
            tv_address_selection=(TextView)itemView.findViewById(R.id.tv_address_selection);
            tv_company_selection=(TextView)itemView.findViewById(R.id.tv_company_selection);
            tv_count_s=(TextView)itemView.findViewById(R.id.tv_count_s);
            tv_delivery_day = itemView.findViewById(R.id.tv_delivery_day);

        }
    }


}
