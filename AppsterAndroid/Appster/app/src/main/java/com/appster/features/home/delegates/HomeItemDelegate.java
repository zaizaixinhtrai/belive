package com.appster.features.home.delegates;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.home.viewholders.HomeItemViewHolder;
import com.appster.models.HomeItemModel;

import java.util.List;

/**
 * Created by thanhbc on 5/30/17.
 */

public class HomeItemDelegate  extends AbsListItemAdapterDelegate<HomeItemModel, DisplayableItem, HomeItemViewHolder> {

    HomeItemViewHolder.OnClickListener mHomeItemListener;
    boolean mShouldShowCategoryTag;
    boolean mShouldUseDistanceTag;
    public HomeItemDelegate(HomeItemViewHolder.OnClickListener homeItemListener, boolean shouldShowCategoryTag, boolean useDistanceTag) {
        mHomeItemListener = homeItemListener;
        mShouldShowCategoryTag=shouldShowCategoryTag;
        mShouldUseDistanceTag=useDistanceTag;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof HomeItemModel;
    }

    @NonNull
    @Override
    protected HomeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return HomeItemViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull HomeItemModel item, @NonNull HomeItemViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item,mHomeItemListener,mShouldShowCategoryTag,mShouldUseDistanceTag);
    }
}
