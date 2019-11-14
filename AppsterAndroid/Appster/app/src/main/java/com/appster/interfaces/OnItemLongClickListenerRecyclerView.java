package com.appster.interfaces;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Ngoc on 4/19/2017.
 */

public interface OnItemLongClickListenerRecyclerView<T> {
    void OnLongClickItem(View v, T data, RecyclerView.ViewHolder viewHolder, int position);
}
