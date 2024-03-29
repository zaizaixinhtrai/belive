package com.appster.core.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.holders.LoadMoreViewHolder;

import java.util.List;

/**
 * Created by linh on 23/05/2017.
 */

public class LoadMoreDelegate extends AbsListItemAdapterDelegate<LoadMoreItem, DisplayableItem, LoadMoreViewHolder>  {
    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof LoadMoreItem;
    }

    @NonNull
    @Override
    protected LoadMoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return LoadMoreViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull LoadMoreItem item, @NonNull LoadMoreViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo();
    }
}
