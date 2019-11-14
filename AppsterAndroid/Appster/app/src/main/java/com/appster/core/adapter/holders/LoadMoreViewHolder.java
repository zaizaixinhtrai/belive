package com.appster.core.adapter.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;

/**
 * Created by linh on 23/05/2017.
 */

public class LoadMoreViewHolder extends RecyclerView.ViewHolder {
    private LoadMoreViewHolder(View itemView) {
        super(itemView);
    }

    public static LoadMoreViewHolder create(ViewGroup parent){
        return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item_load_more_recycler_view, parent, false));
    }

    public void bindTo(){

    }
}
