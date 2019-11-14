package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.viewholder.chatGroupViewHolder.AdminMessageHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by linh on 05/06/2017.
 */

public class AdminMessageDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass,DisplayableItem, AdminMessageHolder> {
    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        ChatItemModelClass chatItemModelClass  = (ChatItemModelClass) item;
        return ChatItemModelClass.CHAT_TYPE_ADMIN_MESSAGE.equals(chatItemModelClass.getType());
    }

    @NonNull
    @Override
    protected AdminMessageHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return AdminMessageHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull AdminMessageHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item);
    }
}
