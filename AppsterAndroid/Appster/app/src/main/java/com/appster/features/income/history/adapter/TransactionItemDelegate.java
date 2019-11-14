package com.appster.features.income.history.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.income.history.viewholder.TransactionItemViewHolder;
import com.domain.models.TransactionHistoryModel;

import java.util.List;

/**
 * Created by thanhbc on 10/24/17.
 */

public class TransactionItemDelegate extends AbsListItemAdapterDelegate<TransactionHistoryModel,DisplayableItem, TransactionItemViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof TransactionHistoryModel;
    }

    @NonNull
    @Override
    protected TransactionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return TransactionItemViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull TransactionHistoryModel item, @NonNull TransactionItemViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item);
    }
}
