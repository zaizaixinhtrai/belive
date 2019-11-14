package com.appster.features.home.dailybonus.treatlist.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.home.dailybonus.treatlist.viewholders.TreatTitleItemViewHolder;
import com.domain.models.TreatListItemModel;

import java.util.List;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatTitleItemDelegate extends AbsListItemAdapterDelegate<TreatListItemModel,DisplayableItem, TreatTitleItemViewHolder> {
    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof TreatListItemModel;
    }

    @NonNull
    @Override
    protected TreatTitleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return TreatTitleItemViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull TreatListItemModel item, @NonNull TreatTitleItemViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item);
    }
}
