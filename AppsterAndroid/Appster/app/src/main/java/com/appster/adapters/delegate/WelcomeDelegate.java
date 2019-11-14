package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.viewholder.chatGroupViewHolder.WelcomeMessageHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by Ngoc on 11/9/2017.
 */

public class WelcomeDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass, DisplayableItem, WelcomeMessageHolder> {

    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        ChatItemModelClass chatItemModelClass = (ChatItemModelClass) item;
        return "welcome_message".equalsIgnoreCase(chatItemModelClass.getChatDisplayName());
    }

    @NonNull
    @Override
    protected WelcomeMessageHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return WelcomeMessageHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull WelcomeMessageHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item);
    }

}
