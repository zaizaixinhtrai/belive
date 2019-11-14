package com.appster.adapters;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.ButterKnife;

/**
 * Created by lanna on 2/6/15.
 *
 */
public abstract class BaseRecyclerViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {

    OnItemClickListener onItemClickListener;
    T model;

    public BaseRecyclerViewHolder(View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        initView();
        this.onItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);
    }

    public void initView() {
        ButterKnife.bind(this, itemView);
        onBind(null); // clear info on item view
    }

    public void onBind(T model) {
        this.model = model;
    }

    public T getModel() {
        return model;
    }

    public View findViewById(int resId) {
        return itemView.findViewById(resId);
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(view, model, getPosition());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
