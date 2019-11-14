package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.adapters.viewholder.chatGroupViewHolder.FollowMessageHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by linh on 05/06/2017.
 */

public class FollowMessageDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass,DisplayableItem, FollowMessageHolder> {
    ChatGroupDelegateAdapter.ChatGroupClickListener mChatGroupClickListener;
    final String hostDisplayName;
    public FollowMessageDelegate(ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener, String hostName) {
        mChatGroupClickListener = chatGroupClickListener;
        hostDisplayName = hostName;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return ChatItemModelClass.CHAT_TYPE_FOLLOW.equals(((ChatItemModelClass)item).getType());
    }

    @NonNull
    @Override
    protected FollowMessageHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return FollowMessageHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull FollowMessageHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item, mChatGroupClickListener,hostDisplayName);
    }
}
