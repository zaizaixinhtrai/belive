package com.appster.models.event_bus_models;

import com.appster.message.ChatItemModelClass;

/**
 * Created by phutang on 11/2/15.
 */
public class NewMessageEvent {
    private ChatItemModelClass data;

    public NewMessageEvent(ChatItemModelClass data) {
        this.data = data;
    }

    public ChatItemModelClass getData() {
        return data;
    }

    public void setData(ChatItemModelClass data) {
        this.data = data;
    }
}
