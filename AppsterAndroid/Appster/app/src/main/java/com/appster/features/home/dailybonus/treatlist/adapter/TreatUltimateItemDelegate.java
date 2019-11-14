package com.appster.features.home.dailybonus.treatlist.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.home.dailybonus.treatlist.viewholders.TreatUltimateItemViewHolder;
import com.domain.models.TreatUltimateItem;

import java.util.List;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatUltimateItemDelegate extends AbsListItemAdapterDelegate<TreatUltimateItem,DisplayableItem, TreatUltimateItemViewHolder> {
    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof TreatUltimateItem;
    }

    @NonNull
    @Override
    protected TreatUltimateItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return TreatUltimateItemViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull TreatUltimateItem item, @NonNull TreatUltimateItemViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item);
    }
}
