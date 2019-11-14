package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.adapters.viewholder.chatGroupViewHolder.SharingMessageHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by linh on 06/06/2017.
 */

public class SharingMessageDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass,DisplayableItem, SharingMessageHolder> {

    ChatGroupDelegateAdapter.ChatGroupClickListener mChatGroupClickListener;

    public SharingMessageDelegate(ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener) {
        mChatGroupClickListener = chatGroupClickListener;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return ChatItemModelClass.CHAT_TYPE_SHARE_STREAM.equals(((ChatItemModelClass)item).getType());
    }

    @NonNull
    @Override
    protected SharingMessageHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return SharingMessageHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull SharingMessageHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item, mChatGroupClickListener);
    }
}
