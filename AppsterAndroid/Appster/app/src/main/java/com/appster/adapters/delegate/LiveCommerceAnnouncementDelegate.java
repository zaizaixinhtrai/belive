package com.appster.adapters.delegate;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.appster.adapters.viewholder.chatGroupViewHolder.LiveCommerceAnnouncementHolder;
import com.appster.core.adapter.AbsListItemAdapterDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.message.ChatItemModelClass;

import java.util.List;

/**
 * Created by linh on 28/11/2017.
 */

public class LiveCommerceAnnouncementDelegate extends AbsListItemAdapterDelegate<ChatItemModelClass, DisplayableItem, LiveCommerceAnnouncementHolder> {
    @Override
    protected boolean isForViewType(@NonNull DisplayableItem item, @NonNull List<DisplayableItem> items, int position) {
        ChatItemModelClass chatItemModelClass  = (ChatItemModelClass) item;
        return ChatItemModelClass.CHAT_TYPE_LIVE_COMMERCE_ANNOUNCEMENT.equals(chatItemModelClass.getType());
    }

    @NonNull
    @Override
    protected LiveCommerceAnnouncementHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return LiveCommerceAnnouncementHolder.create(parent);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatItemModelClass item, @NonNull LiveCommerceAnnouncementHolder viewHolder, @NonNull List<Object> payloads) {
        viewHolder.bindTo(item);
    }
}
