package com.appster.features.friend_suggestion.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.features.friend_suggestion.viewholder.SuggestedFriendViewHolder;
import com.appster.models.SearchModel;

import java.util.List;

/**
 * Created by linh on 18/05/2017.
 */

public class SuggestedFriendDelegate extends AbsListItemAdapterDelegate<SearchModel,DisplayableItem, SuggestedFriendViewHolder> {


    final SuggestedFriendViewHolder.OnClickListener itemCallBack;

    public SuggestedFriendDelegate(SuggestedFriendViewHolder.OnClickListener itemCallBack) {
        this.itemCallBack = itemCallBack;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return item instanceof SearchModel;
    }

    @NonNull
    @Override
    protected SuggestedFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return SuggestedFriendViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchModel model, @NonNull SuggestedFriendViewHolder holder, @NonNull List<Object> payloads) {
        holder.bindTo(model,itemCallBack);
    }

}

