package com.appster.manager;

import android.text.TextUtils;

import androidx.collection.SimpleArrayMap;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.message.ChatItemModelClass;
import com.appster.models.event_bus_models.NewMessageEvent;
import com.appster.models.event_bus_models.UserJoinLeaveEvent;
import com.google.gson.Gson;
import com.pack.utility.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import io.agora.AgoraAPI;
import io.agora.IAgoraAPI;
import timber.log.Timber;

public class AgoraChatManager implements AppChatAPI {
    private static volatile AgoraChatManager sAgoraInstance;
    private Gson mGson;
    private LinkedHashSet<String> arrayCurrentUserInStream = new LinkedHashSet<>();
    private LinkedHashSet<String> arrayCurrentBotInStream = new LinkedHashSet<>();
    private SimpleArrayMap<String, String> mCurrentUserSparseArray = new SimpleArrayMap<>();
    private String loginUserName = "";
    private String channelId = "";
    private String groupOwnerName = "";
    private boolean isRoomOwner = false;

    private AgoraChatManager() {
        mGson = new Gson();
    }

    public static AgoraChatManager get() {
        if (sAgoraInstance == null) {
            synchronized (AgoraChatManager.class) {
                if (sAgoraInstance == null) {
                    sAgoraInstance = new AgoraChatManager();
                }
            }
        }
        return sAgoraInstance;
    }

    @Override
    public void login(String appId, String account) {
        if (AppsterApplication.getApplication().getmAgoraAPI().isOnline() == 0) {
            AppsterApplication.getApplication().getmAgoraAPI().callbackSet(new AgoraAPI.CallBack() {
                @Override
                public void onLoginSuccess(int uid, int fd) {
                    Timber.e("onLoginSuccess");
                    super.onLoginSuccess(uid, fd);
                }

                @Override
                public void onLoginFailed(int ecode) {
                    Timber.e("onLoginFailed %s", ecode);
                    super.onLoginFailed(ecode);
                }
            });
            loginUserName = account;
            AppsterApplication.getApplication().getmAgoraAPI().login(appId, account, "_no_need_token", 0, null);
        }
    }

    @Override
    public void joinGroup(String channelId, AppsterChatManger.StreamChatGroupListener listener, String groupOwnerName) {
        if (AppsterApplication.getApplication().getmAgoraAPI().isOnline() == 1) {
            AppsterApplication.getApplication().getmAgoraAPI().callbackSet(new AgoraAPI.CallBack() {
                @Override
                public void onChannelJoined(String channelID) {
                    super.onChannelJoined(channelID);
                    if (listener != null) {
                        listener.onChatGroupJoinedSuccesfully();
                    }
                    Timber.e("onChannelJoined %s ", channelID);
                }

                @Override
                public void onChannelJoinFailed(String channelID, int ecode) {
                    super.onChannelJoinFailed(channelID, ecode);
                    if (listener != null) {
                        listener.onChatGroupJoinError("error code " + ecode);
                    }
                }

                @Override
                public void onChannelUserList(String[] accounts, int[] uids) {
                    super.onChannelUserList(accounts, uids);
                    addAllCurrentListRealUsers(accounts);
                }

                @Override
                public void onChannelUserJoined(String account, int uid) {
                    super.onChannelUserJoined(account, uid);
                    Timber.e("onChannelUserJoined %s - %s", account, uid);
                    handleWatcherList(true, account);
                    if (isRoomOwner) {
                        sendCurrentBotListToViewers();
                    }
                }

                @Override
                public void onChannelUserLeaved(String account, int uid) {
                    super.onChannelUserLeaved(account, uid);
                    removeWatcher(account);
                    handleWatcherList(false, account);
                }

                @Override
                public void onLogout(final int i) {
                    if (i == IAgoraAPI.ECODE_LOGOUT_E_KICKED) { //other login the account
                        Timber.e("Other login account ,you are logout.");

                    } else if (i == IAgoraAPI.ECODE_LOGOUT_E_NET) { //net
                        Timber.e("Logout for Network can not be.");

                    }
                }

                @Override
                public void onError(String s, int i, String s1) {
                    Timber.e("onError s:" + s + " s1:" + s1);
                }

                @Override
                public void onMessageChannelReceive(String channelID, final String account, int uid, final String msg) {
                    Timber.e("onMessageChannelReceive  account = " + account + " uid = " + uid + " msg = " + msg);
                    ChatItemModelClass chatItemModelClass = mGson.fromJson(msg, ChatItemModelClass.class);
                    if (chatItemModelClass.getUserName() != null && (chatItemModelClass.isLiked() || !chatItemModelClass.getUserName().equals(loginUserName))) {

                        chatItemModelClass.setIsGroupMessage(true);
                        if (ChatItemModelClass.CHAT_TYPE_BOT_JOIN_LIST.equals(chatItemModelClass.getType())) {
                            Timber.e("bot join list type");
                            //bots join room
                            final List<String> chatBotUserModels = chatItemModelClass.getChatBotUserModels();
                            if (chatBotUserModels == null) return;
                            if (getArrayCurrentBotInStream().size() < chatBotUserModels.size()) {
                                ArrayList<String> newBotList = new ArrayList<>(chatBotUserModels);
                                newBotList.removeAll(getArrayCurrentBotInStream());
                                for (String botName : newBotList) {
                                    if (!TextUtils.isEmpty(botName) && botName.equals(chatItemModelClass.getUserName())) {
                                        addBotUserViewer(chatItemModelClass.getUserName());
                                    } else {
                                        addBotUserViewer(botName);
                                    }
                                }
                                if (!ChatItemModelClass.INIT_BOT_LIST.equals(chatItemModelClass.getUserName())) {
                                    EventBus.getDefault().post(new NewMessageEvent(chatItemModelClass));
                                }
                                if (listener != null)
                                    listener.onChatGroupWatchersListReceived(getArrayCurrentUserInStream());

                            } else if (!isRoomOwner) {
                                //bots left room
                                ArrayList<String> newBotList = new ArrayList<>(getArrayCurrentBotInStream());
                                newBotList.removeAll(chatBotUserModels);
                                for (int i = 0; i < newBotList.size(); i++) {
                                    removeBotUserViewer(newBotList.get(i));
                                }
                            }
                        } else if (ChatItemModelClass.TYPE_STEAM_TITLE_STICKER.equals(chatItemModelClass.getType())) {
                            EventBus.getDefault().post(new NewMessageEvent(chatItemModelClass));
                        } else {
                            EventBus.getDefault().post(new NewMessageEvent(chatItemModelClass));
                        }
                    } else {
                        Timber.e("receive message but sender and receiver is only one person => not broadcast");
                    }
                }
            });
            this.channelId = channelId;
            this.groupOwnerName = groupOwnerName;
            this.isRoomOwner = isRoomOwner(loginUserName);
            AppsterApplication.getApplication().getmAgoraAPI().channelJoin(channelId);
        } else {
            if (listener != null) {
                listener.onChatGroupJoinError("not login yet");
            }
        }
    }


    private void sendCurrentBotListToViewers() {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();

        itemModelClass.setChatBotUserModels(getArrayCurrentBotInStream());
        itemModelClass.setUserName(ChatItemModelClass.INIT_BOT_LIST);
        itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_BOT_JOIN_LIST);
        try {
            sendGroupMessage(itemModelClass);
        } catch (Exception e) {
            Timber.e(e);
//            LogUtils.logI(TAG, e.getMessage());
        }
    }


    private void handleWatcherList(boolean isJoined, String account) {
        if (arrayCurrentUserInStream != null) {
            EventBus.getDefault().post(new UserJoinLeaveEvent(isJoined, account, account));
            Timber.e("arrayCurrentUserInStream %s %s", arrayCurrentUserInStream.size(), account);
        }
    }

    private void addAllCurrentListRealUsers(String[] accounts) {
        for (String account : accounts) {
            addNewWatcher(account);
        }
    }

    private void removeWatcher(String userName) {
        if (arrayCurrentUserInStream != null) {
            arrayCurrentUserInStream.remove(userName);
        }
    }

    public void leaveGroup() {
        if (!StringUtil.isNullOrEmptyString(channelId)) {
            leaveGroup(channelId);
        }
    }

    @Override
    public void leaveGroup(String channelId) {
        if (AppsterApplication.getApplication().getmAgoraAPI().isOnline() == 1) {
            AppsterApplication.getApplication().getmAgoraAPI().channelLeave(channelId);
        }
        clearArrayCurrentBotInStream();
        clearArrayCurrentUserInStream();
    }


    public void sendGroupMessage(ChatItemModelClass itemModelClass) {
        if (!StringUtil.isNullOrEmptyString(channelId)) {
            sendGroupMessage(channelId, itemModelClass);
        }
    }

    @Override
    public void sendGroupMessage(String channelId, ChatItemModelClass itemModelClass) {
        if (AppsterApplication.getApplication().getmAgoraAPI().isOnline() == 1) {
            AppsterApplication.getApplication().getmAgoraAPI().messageChannelSend(channelId, mGson.toJson(itemModelClass), "");
        }
    }

    public void addBotUserViewer(String botUserName) {
        //since ios send bot list which includes real users
        //so we have to filter room owner, guest, app owner
        if (isAppOwner(botUserName)) {
            return;
        }

        if (arrayCurrentBotInStream == null) {
            arrayCurrentBotInStream = new LinkedHashSet<>();
        }

        if (arrayCurrentUserInStream == null) {
            arrayCurrentUserInStream = new LinkedHashSet<>();
        }
        try {
            if (!checkUserIsExits(botUserName)) {
                Timber.e("addBotUserViewer %s", botUserName);
                mCurrentUserSparseArray.put(botUserName, botUserName);
                arrayCurrentBotInStream.add(botUserName);
                arrayCurrentUserInStream.add(botUserName);
                handleWatcherList(true, "");
            }
        } catch (Exception e) {
            Timber.e("error %s", e.getMessage());
        }

    }

    public void removeBotUserViewer(String botUserName) {
        if (arrayCurrentBotInStream == null || arrayCurrentUserInStream == null || botUserName == null)
            return;

        Timber.e("removeBotUserViewer %s", botUserName);
        arrayCurrentBotInStream.remove(botUserName);
        removeWatcher(botUserName);
        if (arrayCurrentUserInStream != null) {
            EventBus.getDefault().post(new UserJoinLeaveEvent(false, botUserName, botUserName));
            Timber.e("arrayCurrentUserInStream %s %s", arrayCurrentUserInStream.size(), botUserName);
        }
    }

    private boolean checkUserIsExits(String botUserName) {
        boolean isUserExist = mCurrentUserSparseArray.containsKey(botUserName);
        Timber.e("checkUserIsExits %s - %s", botUserName, isUserExist);
        return isUserExist;
    }

    public List<String> getArrayCurrentUserInStream() {
        return arrayCurrentUserInStream != null ? new ArrayList<>(arrayCurrentUserInStream) : new ArrayList<>();
    }

    public void clearArrayCurrentUserInStream() {
        try {
            if (arrayCurrentUserInStream != null)
                arrayCurrentUserInStream.clear();
            mCurrentUserSparseArray.clear();
        } catch (ArrayIndexOutOfBoundsException e) {
            Timber.e(e.getMessage());
        }
    }

    public void clearArrayCurrentBotInStream() {
        if (arrayCurrentBotInStream != null)
            arrayCurrentBotInStream.clear();
    }

    public List<String> getArrayCurrentBotInStream() {
        return arrayCurrentBotInStream != null ? new ArrayList<>(arrayCurrentBotInStream) : new ArrayList<>();
    }

    boolean isAppOwner(String username) {
        return (AppsterApplication.mAppPreferences.isUserLogin() && AppsterApplication.mAppPreferences.getUserModel().getUserName().equalsIgnoreCase(username));
    }

    private void addNewWatcher(String userName) {
        if (isAppOwner(userName) || isGuestUser(userName) || isRoomOwner(userName)) {
            return;
        }

        if (arrayCurrentUserInStream == null) {
            arrayCurrentUserInStream = new LinkedHashSet<>();
        }

        mCurrentUserSparseArray.put(userName, userName);
        arrayCurrentUserInStream.add(userName);
    }

    boolean isGuestUser(String username) {
        return BuildConfig.NAME_USER_DEFAULT_XMPP.equals(username);
    }

    boolean isRoomOwner(String username) {
        return !TextUtils.isEmpty(groupOwnerName) && groupOwnerName.equals(username);
    }
}
