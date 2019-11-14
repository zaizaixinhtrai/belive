package com.appster.features.home.dailybonus.treatmachine.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.home.dailybonus.treatmachine.viewholders.TreatItemViewHolder;
import com.domain.models.TreatItemModel;

import java.util.List;

/**
 * Created by thanhbc on 11/8/17.
 */

public class TreatItemDelegate extends AbsListItemAdapterDelegate<TreatItemModel,DisplayableItem, TreatItemViewHolder> {
    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof TreatItemModel;
    }

    @NonNull
    @Override
    protected TreatItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return TreatItemViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull TreatItemModel item, @NonNull TreatItemViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item);
    }
}
