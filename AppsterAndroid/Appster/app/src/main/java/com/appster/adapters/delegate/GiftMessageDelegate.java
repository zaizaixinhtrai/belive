package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.adapters.viewholder.chatGroupViewHolder.GiftMessageHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by linh on 05/06/2017.
 */

public class GiftMessageDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass,DisplayableItem, GiftMessageHolder>{

    ChatGroupDelegateAdapter.ChatGroupClickListener mChatGroupClickListener;

    public GiftMessageDelegate(ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener) {
        mChatGroupClickListener = chatGroupClickListener;
    }

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        return ChatItemModelClass.CHAT_TYPE_GIFT.equals(((ChatItemModelClass) item).getType());
    }

    @NonNull
    @Override
    protected GiftMessageHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return GiftMessageHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull GiftMessageHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item, mChatGroupClickListener);
    }
}
