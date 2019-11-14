package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.adapters.viewholder.chatGroupViewHolder.JoinedMessageHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by linh on 05/06/2017.
 */

public class JoinedMessageDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass,DisplayableItem, JoinedMessageHolder> {

    ChatGroupDelegateAdapter.ChatGroupClickListener mChatGroupClickListener;

    public JoinedMessageDelegate(ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener) {
        mChatGroupClickListener = chatGroupClickListener;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        String type = ((ChatItemModelClass)item).getType();
        return ChatItemModelClass.CHAT_TYPE_USER_JOIN_LIST.equals(type)
                || ChatItemModelClass.CHAT_TYPE_BOT_JOIN_LIST.equals(type);
    }

    @NonNull
    @Override
    protected JoinedMessageHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return JoinedMessageHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull JoinedMessageHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item, mChatGroupClickListener);
    }
}
