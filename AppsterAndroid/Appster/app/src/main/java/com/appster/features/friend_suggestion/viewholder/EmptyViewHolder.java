package com.appster.features.friend_suggestion.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;

/**
 * Created by linh on 19/05/2017.
 */

public class EmptyViewHolder extends RecyclerView.ViewHolder {
    private EmptyViewHolder(View itemView) {
        super(itemView);
    }

    public static EmptyViewHolder create(ViewGroup parent){
        return  new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false));
    }

    public void bindTo(){

    }
}
