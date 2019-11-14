package com.appster.features.home.delegates;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.home.StreamCategory;
import com.appster.features.home.viewholders.CategoriesViewHolder;

import java.util.List;

/**
 * Created by thanhbc on 6/2/17.
 */

public class CategoriesItemDelegate extends AbsListItemAdapterDelegate<StreamCategory, DisplayableItem, CategoriesViewHolder> {
    private final CategoriesViewHolder.OnClickListener mListener;


    public CategoriesItemDelegate(@Nullable CategoriesViewHolder.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof StreamCategory;
    }

    @NonNull
    @Override
    protected CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return CategoriesViewHolder.create(parent);
    }

//    @Override
//    protected void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
//        layoutParams.setFullSpan(true);
//    }
    @Override
    protected void onBindViewHolder(@NonNull StreamCategory item, @NonNull CategoriesViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item, mListener);
    }
}
