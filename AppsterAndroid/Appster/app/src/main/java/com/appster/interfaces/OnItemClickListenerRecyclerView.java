package com.appster.interfaces;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by sonnguyen on 11/17/16.
 */

public interface OnItemClickListenerRecyclerView<T> {
    public void OnclickItem(View v, T data, RecyclerView.ViewHolder viewHolder,int position);
}

