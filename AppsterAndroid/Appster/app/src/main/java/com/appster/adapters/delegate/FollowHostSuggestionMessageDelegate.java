package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.viewholder.FollowHostSuggestionMessageHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

import static com.appster.adapters.ChatGroupDelegateAdapter.ChatGroupClickListener;

/**
 * Created by linh on 09/10/2017.
 */

public class FollowHostSuggestionMessageDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass,DisplayableItem, FollowHostSuggestionMessageHolder> {

    ChatGroupClickListener mListener;

    public FollowHostSuggestionMessageDelegate(ChatGroupClickListener listener) {
        mListener = listener;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        String type = ((ChatItemModelClass)item).getType();
        return ChatItemModelClass.CHAT_TYPE_FOLLOW_HOST_SUGGESTION.equals(type);
    }

    @NonNull
    @Override
    protected FollowHostSuggestionMessageHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return FollowHostSuggestionMessageHolder.create(parent, mListener);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull FollowHostSuggestionMessageHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item);
    }
}
