package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.adapters.viewholder.chatGroupViewHolder.LiteralTextViewHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by linh on 05/06/2017.
 */

public class LiteralTextDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass,DisplayableItem, LiteralTextViewHolder> {

    ChatGroupDelegateAdapter.ChatGroupClickListener mChatGroupClickListener;

    public LiteralTextDelegate(ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener) {
        mChatGroupClickListener = chatGroupClickListener;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        ChatItemModelClass chatItemModelClass  = (ChatItemModelClass) item;
        return ChatItemModelClass.CHAT_TYPE_MESSAGE.equals(chatItemModelClass.getType());
    }

    @NonNull
    @Override
    protected LiteralTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return LiteralTextViewHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull LiteralTextViewHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item, mChatGroupClickListener);
    }
}
