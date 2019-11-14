package com.appster.features.home.dailybonus.treatlist.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.home.dailybonus.treatlist.viewholders.TreatBigItemViewHolder;
import com.domain.models.TreatMiniItem;

import java.util.List;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatMiniItemDelegate extends AbsListItemAdapterDelegate<TreatMiniItem,DisplayableItem, TreatBigItemViewHolder> {
    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof TreatMiniItem;
    }

    @NonNull
    @Override
    protected TreatBigItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return TreatBigItemViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull TreatMiniItem item, @NonNull TreatBigItemViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item);
    }
}
