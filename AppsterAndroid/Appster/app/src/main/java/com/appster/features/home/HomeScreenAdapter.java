package com.appster.features.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.EndlessDelegateAdapter;
import com.appster.features.home.delegates.CategoriesItemDelegate;
import com.appster.features.home.delegates.HomeBannerDelegate;
import com.appster.features.home.delegates.HomeItemDelegate;
import com.appster.features.home.viewholders.CategoriesViewHolder;
import com.appster.features.home.viewholders.HomeBannerItemViewHolder;
import com.appster.features.home.viewholders.HomeItemViewHolder;
import com.apster.common.BaseDiffCallback;

import java.util.List;

/**
 * Created by thanhbc on 5/30/17.
 */

public class HomeScreenAdapter extends EndlessDelegateAdapter {

    public static final int BANNER = 0;
    public static final int CATEGORY_ITEMS = 1;
    public static final int STREAM_ITEMS = 2;

    public HomeScreenAdapter(@Nullable BaseDiffCallback diffCallback, @NonNull List<DisplayableItem> items,
                             HomeBannerItemViewHolder.OnClickListener bannerItemListener,
                             HomeItemViewHolder.OnClickListener streamItemListener,
                             CategoriesViewHolder.OnClickListener categoryItemListener, boolean shouldShowCategoryTag, boolean useDistanceTag) {
        super(diffCallback);
        this.delegatesManager.addDelegate(BANNER, new HomeBannerDelegate(bannerItemListener));
        this.delegatesManager.addDelegate(CATEGORY_ITEMS, new CategoriesItemDelegate(categoryItemListener));
        this.delegatesManager.addDelegate(STREAM_ITEMS, new HomeItemDelegate(streamItemListener, shouldShowCategoryTag, useDistanceTag));
        setItems(items);
    }
}
