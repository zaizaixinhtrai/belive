package com.appster.manager;

import com.appster.message.ChatItemModelClass;

public interface AppChatAPI {
    void login(String appId, String account);
    void joinGroup(String channelId, AppsterChatManger.StreamChatGroupListener listener,String groupOwnerName);
    void leaveGroup(String channelId);
    void sendGroupMessage(String channelId,ChatItemModelClass itemModelClass);
}
