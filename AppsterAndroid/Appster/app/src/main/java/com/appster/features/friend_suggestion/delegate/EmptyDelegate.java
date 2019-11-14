package com.appster.features.friend_suggestion.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.friend_suggestion.EmptyModel;
import com.appster.features.friend_suggestion.viewholder.EmptyViewHolder;

import java.util.List;

/**
 * Created by linh on 19/05/2017.
 */

public class EmptyDelegate extends AbsListItemAdapterDelegate<EmptyModel, DisplayableItem, EmptyViewHolder>{
    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof EmptyModel;
    }

    @NonNull
    @Override
    protected EmptyViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return EmptyViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull EmptyModel item, @NonNull EmptyViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo();
    }
}
