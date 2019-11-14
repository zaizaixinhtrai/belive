package com.appster.features.home.delegates;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.home.BannerModel;
import com.appster.features.home.viewholders.HomeBannerItemViewHolder;

import java.util.List;

/**
 * Created by thanhbc on 5/30/17.
 */

public class HomeBannerDelegate extends AbsListItemAdapterDelegate<BannerModel, DisplayableItem, HomeBannerItemViewHolder> {


    private final HomeBannerItemViewHolder.OnClickListener mHomeBannerItemListener;

    public HomeBannerDelegate(HomeBannerItemViewHolder.OnClickListener listener) {
        this.mHomeBannerItemListener = listener;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof BannerModel;
    }

    @NonNull
    @Override
    protected HomeBannerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return HomeBannerItemViewHolder.create(parent);
    }

//    @Override
//    protected void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
//        layoutParams.setFullSpan(true);
//    }

    @Override
    protected void onBindViewHolder(@NonNull BannerModel item, @NonNull HomeBannerItemViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item, mHomeBannerItemListener);
    }
}
