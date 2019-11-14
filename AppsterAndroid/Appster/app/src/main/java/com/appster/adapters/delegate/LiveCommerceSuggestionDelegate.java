package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.adapters.viewholder.chatGroupViewHolder.LiveCommerceSuggestionHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by linh on 28/11/2017.
 */

public class LiveCommerceSuggestionDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass, DisplayableItem, LiveCommerceSuggestionHolder> {

    private final ChatGroupDelegateAdapter.ChatGroupClickListener mChatGroupClickListener;

    public LiveCommerceSuggestionDelegate(ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener) {
        mChatGroupClickListener = chatGroupClickListener;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        ChatItemModelClass chatItemModelClass  = (ChatItemModelClass) item;
        return ChatItemModelClass.CHAT_TYPE_LIVE_COMMERCE_SUGGESTION.equals(chatItemModelClass.getType());
    }

    @NonNull
    @Override
    protected LiveCommerceSuggestionHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return LiveCommerceSuggestionHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull LiveCommerceSuggestionHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item, mChatGroupClickListener);
    }
}
