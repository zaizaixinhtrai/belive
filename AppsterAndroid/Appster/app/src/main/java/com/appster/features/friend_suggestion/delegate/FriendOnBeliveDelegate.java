package com.appster.features.friend_suggestion.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.domain.FriendSuggestionModel;
import com.appster.features.friend_suggestion.viewholder.FriendOnBeliveViewHolder;

import java.util.List;

/**
 * Created by linh on 18/05/2017.
 */

public class FriendOnBeliveDelegate extends AbsListItemAdapterDelegate<FriendSuggestionModel, DisplayableItem, FriendOnBeliveViewHolder> {

    FriendOnBeliveViewHolder.FriendOnBeLiveViewHolderListener listener;

    public FriendOnBeliveDelegate(FriendOnBeliveViewHolder.FriendOnBeLiveViewHolderListener listener) {
        this.listener = listener;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof FriendSuggestionModel;
    }

    @NonNull
    @Override
    protected FriendOnBeliveViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return FriendOnBeliveViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendSuggestionModel item, @NonNull FriendOnBeliveViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item, listener);
    }
}
