package com.appster.features.income.history.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.EndlessDelegateAdapter;
import com.apster.common.BaseDiffCallback;

import java.util.List;

/**
 * Created by thanhbc on 10/24/17.
 */

public class TransactionAdapterDelegate extends EndlessDelegateAdapter {
    public TransactionAdapterDelegate(@Nullable BaseDiffCallback diffCallback, @NonNull List<DisplayableItem> items){
        super(diffCallback);
        this.delegatesManager.addDelegate(new TransactionItemDelegate());
        setItems(items);
    }

}
