package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.viewholder.chatGroupViewHolder.WarningMessageHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by linh on 05/06/2017.
 */

public class WarningMessageDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass,DisplayableItem, WarningMessageHolder> {
    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        ChatItemModelClass chatItemModelClass  = (ChatItemModelClass) item;
        return "warning_message".equalsIgnoreCase(chatItemModelClass.getChatDisplayName());
    }

    @NonNull
    @Override
    protected WarningMessageHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return WarningMessageHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull WarningMessageHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo();
    }
}
