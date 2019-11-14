package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.adapters.viewholder.chatGroupViewHolder.LikeMessageHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by linh on 05/06/2017.
 */

public class LikeMessageDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass,DisplayableItem, LikeMessageHolder> {

    ChatGroupDelegateAdapter.ChatGroupClickListener mChatGroupClickListener;

    public LikeMessageDelegate(ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener) {
        mChatGroupClickListener = chatGroupClickListener;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        ChatItemModelClass chatItemModelClass  = (ChatItemModelClass) item;
        return ChatItemModelClass.CHAT_TYPE_LIKE.equals(chatItemModelClass.getType()) || chatItemModelClass.isLiked();
    }

    @NonNull
    @Override
    protected LikeMessageHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return LikeMessageHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull LikeMessageHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item, mChatGroupClickListener);
    }
}
