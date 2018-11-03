package com.example.wwx.weather.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wwx.weather.R;
import com.example.wwx.weather.module.ChooseAreaAdapter.MyViewHolder;

import java.util.List;

/**
 * Created by wwx on 2018/11/2.
 * 选择区域adapter
 */

public class ChooseAreaAdapter extends Adapter<MyViewHolder> {

    private Context context;
    private List<String> list;
    private OnItemClickListener listener;

    ChooseAreaAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_area_item,
                parent, false);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        layoutParams.topMargin = 4;
        view.setLayoutParams(layoutParams);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.txtAdapter.setText(list.get(position));
        holder.txtAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends ViewHolder {

        private TextView txtAdapter;

        MyViewHolder(View itemView) {
            super(itemView);

            txtAdapter = itemView.findViewById(R.id.adapter_txt);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
