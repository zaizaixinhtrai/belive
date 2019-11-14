package com.appster.features.searchScreen.delegates;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.searchScreen.Header;
import com.appster.features.searchScreen.viewholders.TitleViewHolder;

import java.util.List;

/**
 * Created by thanhbc on 5/17/17.
 */

public class TitleItemDelegate extends AbsListItemAdapterDelegate<Header, DisplayableItem, TitleViewHolder> {

    boolean isAlignLeft;

    public TitleItemDelegate() {
    }

    public TitleItemDelegate(boolean isAlignLeft) {
        this.isAlignLeft = isAlignLeft;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof Header;
    }

    @NonNull
    @Override
    protected TitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return TitleViewHolder.create(parent, isAlignLeft);
    }

    @Override
    protected void onBindViewHolder(@NonNull Header header, @NonNull TitleViewHolder holder, @NonNull List<Object> payloads) {
        holder.bindTo(header);
    }

}
