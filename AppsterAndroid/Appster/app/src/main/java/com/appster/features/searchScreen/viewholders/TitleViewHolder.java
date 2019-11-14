package com.appster.features.searchScreen.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.features.searchScreen.Header;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 5/17/17.
 */

public class TitleViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.txt_header)
    TextView txtHeader;

    public static TitleViewHolder create(ViewGroup parent, boolean isAlignLeft) {
        View itemView;
        if (isAlignLeft){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header_left_align, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_adapter_header_row, parent, false);
        }

        return new TitleViewHolder(itemView);
    }

    public TitleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindTo(Header header) {
        txtHeader.setText(header.title);
    }
}

