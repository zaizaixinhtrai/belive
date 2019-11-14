package com.appster.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SimpleArrayMap;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.R;
import com.appster.data.AppPreferences;
import com.appster.features.messages.MessageItemModelClass;
import com.appster.message.ChatItemModelClass;
import com.appster.models.BanUserMessage;
import com.appster.models.event_bus_models.NewMessageEvent;
import com.appster.models.event_bus_models.UserJoinLeaveEvent;
import com.appster.utility.AppsterUtility;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.JSONUtils;
import com.apster.common.LogUtils;
import com.google.gson.Gson;
import com.pack.utility.Data;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.android.ServerPingWithAlarmManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.appster.features.stream.StreamPresenter.CURRENT_STREAM_SLUG;
import static com.appster.message.ChatItemModelClass.CHAT_TYPE_KICK;
import static com.appster.message.ChatItemModelClass.CHAT_TYPE_UNKICK;
import static com.pack.utility.StringUtil.decodeString;
import static com.pack.utility.StringUtil.encodeString;

/**
 * Created by sonnguyen on 10/22/15.
 */
public class AppsterChatManger implements ConnectionListener {

    public static final int LIMIT_NUMBER_MESSAGE_CHAT_GROUP = 1000;
    public static final int LIMIT_NUMBER_PRESENCE_CHAT_GROUP = 500;
    public static final String ADMIN_USERNAME = "appsteradmin";
    private static final String DISPLAY_NAME_PREFIX = "/belivechat/";

    private BeLiveXMPPTCPConnection xmppConnection;
    // Single
    private static AppsterChatManger sChatManagerInstance;
    private String host = Data.Ip_Address;
    private String port = "5222";
    private String service = Data.Ip_Address;
    private MultiUserChat multiUserChat;
    private String username;
    private String password;
    private String displayName;

    static final String TAG = "AppsterChatManger1";
    Gson mGson;
    private boolean isRooOwner = false;
    // For chat history , ww will improve later after we have time
    private String currentUserIDChatWith = "";
    private ArrayList<MessageItemModelClass> messageItems = new ArrayList<>();
    boolean isChatWithUser = false;

    private String groupName = "";

    private LinkedHashSet<String> arrayCurrentUserInStream = new LinkedHashSet<>();
    private LinkedHashSet<String> arrayCurrentBotInStream = new LinkedHashSet<>();
    private int numberRecreateGroup = 0;
    Context mContext;
    CompositeSubscription mCompositeSubscription;
    SimpleArrayMap<String, String> mCurrentUserSparseArray = new SimpleArrayMap<>();

    private PresenceListener presenceListener;
    private boolean mIsXMPPConnected = false;
    private boolean mLoggedIn = false;
    private AtomicBoolean mIsReconnecting = new AtomicBoolean(false);
    private String mCurrentGroupOwner;
    StreamChatGroupListener mStreamChatGroupListener;
    protected String mJoinGroupUserName = "";

    private boolean mIsAutoConnect = true;
    private Toast mDebugToast;

    /**
     * Timeout for receiving reply from server.
     */
    public final static int PACKET_REPLY_TIMEOUT = 10000;

    private final static int PING_INTERVAL_SECONDS = 60;

    public interface StreamChatGroupListener {
        void onChatGroupJoinedSuccesfully();

        void onChatGroupJoinError(String errorMessage);

        void onChatGroupWatchersListReceived(List<String> watchers);
    }

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            // problem loading reconnection manager
            LogUtils.logE(TAG, "ClassNotFoundException - ReconnectionManager");
        }
    }

    static {
        XMPPTCPConnection.setUseStreamManagementResumptionDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
    }

    public void setStreamChatGroupListener(StreamChatGroupListener streamChatGroupListener) {
        mStreamChatGroupListener = streamChatGroupListener;
    }

    public void setNumberRecreateGroup(int numberRecreateGroup) {
        this.numberRecreateGroup = numberRecreateGroup;
    }

    private AppsterChatManger(Context context) {
        mGson = new Gson();
        mContext = context.getApplicationContext();
        mCompositeSubscription = new CompositeSubscription();
        org.jivesoftware.smackx.ping.PingManager.setDefaultPingInterval(PING_INTERVAL_SECONDS);
        SmackConfiguration.setDefaultReplyTimeout(PACKET_REPLY_TIMEOUT);
        /*
            Fix working with Nimbuzz.com
            Smack have error - ServiceDiscoveryManager: Exception while discovering info for
                                feature urn:xmpp:http:upload:0 of  conference....com node: null
            That exception shows that HttpFileUploadManager was unable to discover an service
            So disabling HttpFileUploadManager by default fixed this error
            HttpFileUploadManager will enabled later in ConnectionListener.authenticated() if server support it
         */
        SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager");
    }

    public static AppsterChatManger getInstance(Context context) {
        if (sChatManagerInstance == null) {
            sChatManagerInstance = new AppsterChatManger(context);

        }
        return sChatManagerInstance;
    }

    private void login() {
        try {
            if (!xmppConnection.isAuthenticated()) {
//                xmppConnection.setReplyTimeout(REPLY_TIMEOUT); check on constructor
                xmppConnection.login(username, password);

                Presence presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.available);
                xmppConnection.sendStanza(presence);
            }
        } catch (SmackException.AlreadyLoggedInException e) {
            mLoggedIn = true;
        } catch (Exception e) {
            Timber.e("login error");
            Timber.e(e);
        }
    }


    public void connectToXMPPServer(String userName, String pass, String displayName) {
        if (xmppConnection != null && xmppConnection.isConnected() && xmppConnection.isAuthenticated()) {
            Timber.e("connectToXMPPServer returned!");
            return;
        }
        this.username = userName;
        this.password = pass;
        this.displayName = displayName;

        try {
            XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
            config.setSendPresence(true);
            config.setResource(AppsterApplication.mAppPreferences.getDevicesUDID());
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setXmppDomain(JidCreate.domainBareFrom(service));
            config.setHostAddress(InetAddress.getByName(host));
            config.setPort(Integer.parseInt(port));
            if (BuildConfig.DEBUG) {
                config.setDebuggerEnabled(true);
            }

            xmppConnection = new BeLiveXMPPTCPConnection(config.build());

            mIsAutoConnect = true;

            //remove previous connection listener
            if (!xmppConnection.getConnectionListeners().isEmpty()) {
                for (ConnectionListener listener : xmppConnection.getConnectionListeners()) {
                    xmppConnection.removeConnectionListener(listener);
                }
            }

            xmppConnection.addConnectionListener(this);
            xmppConnection.connect();
            if (!xmppConnection.isAuthenticated()) {
                login();
            }

            addReconnectManager(xmppConnection);
            addDeliveryReceipt(xmppConnection);
            addPingManager(xmppConnection);
            final ChatManager chatManager = ChatManager.getInstanceFor(xmppConnection);

            chatManager.removeListener(chatManagerListener);
            chatManager.addIncomingListener(chatManagerListener);
        } catch (Exception e) {
            Timber.e("connect failed %s", e);
        }
    }

    private void addPingManager(XMPPTCPConnection connection) {
        PingManager pingManager = PingManager.getInstanceFor(connection);
//        pingManager.setPingInterval(60 * 5 /*5min*/); -- check on constructor
        pingManager.registerPingFailedListener(() -> {
            Timber.e("Ping failed!");
            disConnectXMPP();
            reconnectIfNeed();
        });
    }

    private void addReconnectManager(XMPPTCPConnection connection) {
        ReconnectionManager manager = ReconnectionManager.getInstanceFor(connection);
        if (mContext != null) {
            ServerPingWithAlarmManager.onCreate(mContext);
            ServerPingWithAlarmManager.getInstanceFor(connection).setEnabled(true);
        }

        manager.setFixedDelay(4);
        ReconnectionManager.setDefaultReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
        manager.enableAutomaticReconnection();

    }

    private void addDeliveryReceipt(XMPPTCPConnection connection) {
        DeliveryReceiptManager dm = DeliveryReceiptManager
                .getInstanceFor(connection);
        dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
        dm.autoAddDeliveryReceiptRequests();
        dm.addReceiptReceivedListener((fromJid, toJid, receiptId, receipt) -> Timber.e("onReceiptReceived : %s -> %s : %s", fromJid, toJid, receiptId));
    }

    NewMessageEvent convertBanUserModelToNewMessage(BanUserMessage banUserMessage) {

        ChatItemModelClass chatItemModelClass = new ChatItemModelClass();
        chatItemModelClass.setMessageType(banUserMessage.messageType);
        chatItemModelClass.setIsGroupMessage(false);
        chatItemModelClass.setUserName(banUserMessage.userName);
        chatItemModelClass.setBlockUserId(String.valueOf(banUserMessage.userId));
        chatItemModelClass.setMsg(banUserMessage.message);
        chatItemModelClass.slug = banUserMessage.slug;
        return new NewMessageEvent(chatItemModelClass);
    }

    boolean isBanMessage(String body) {
        return JSONUtils.isJSONValid(body);
    }

    public boolean isConneted() {
        return xmppConnection != null && xmppConnection.isConnected();
    }

    String parseSender(String sender) {
        if (!TextUtils.isEmpty(sender)) {
            String[] temp = sender.split("@");
            if (temp != null && temp.length > 0) {
                return temp[0];
            } else {
                return sender;
            }
        }
        return sender;
    }

    public void sendMessage(Message message) {

        if (xmppConnection.isConnected()) {

            try {
                Chat chat = ChatManager.getInstanceFor(xmppConnection).chatWith(JidCreate.entityBareFrom(message.getTo()));
                chat.send(message);
            } catch (SmackException.NotConnectedException e) {
                Timber.e(e);
                reconnectIfNeed();
            } catch (InterruptedException | XmppStringprepException e) {
                Timber.e(e);
            }
        } else {
            reconnectIfNeed();
        }
    }

    public void sendGroupMessage(ChatItemModelClass itemModelClass) throws SmackException.NotConnectedException, InterruptedException {
        if (mIsReconnecting.get()) return;
        if (multiUserChat != null && multiUserChat.isJoined()) {
            Message msg = new Message(multiUserChat.getRoom(), Message.Type.groupchat);
            msg.setBody(mGson.toJson(itemModelClass));

            multiUserChat.sendMessage(msg);
        } else {

            if (numberRecreateGroup < 2) {
                createGroupChat(groupName, false, mCurrentGroupOwner);
                numberRecreateGroup++;
            }
        }
    }

    private void setConfig(MultiUserChat multiUserChat) {
        try {
            Form form = multiUserChat.getConfigurationForm();
            Form submitForm = form.createAnswerForm();

            for (FormField field : form.getFields()) {
                if (!FormField.Type.hidden.name().equals(field.getType().name()) && field.getVariable() != null) {
                    submitForm.setDefaultAnswer(field.getVariable());
                }

            }
            List<String> owners = new ArrayList<>();
            owners.add(String.valueOf(xmppConnection.getUser()));
            submitForm.setAnswer(FormField.FORM_TYPE, "http://jabber.org/protocol/muc#roomconfig");
            submitForm.setAnswer("muc#roomconfig_changesubject", true);
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);

            List<String> values = new ArrayList<>();
            values.add("moderator");
            values.add("participant");
            values.add("visitor");
            submitForm.setAnswer("muc#roomconfig_presencebroadcast", values);
            submitForm.setAnswer("muc#roomconfig_publicroom", true);
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            submitForm.setAnswer("muc#roomconfig_moderatedroom", false);
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            submitForm.setAnswer("muc#roomconfig_changesubject", false);
            submitForm.setAnswer("x-muc#roomconfig_registration", true);
            multiUserChat.sendConfigurationForm(submitForm);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    private boolean mIsJoinedRoomSuccess = false;

    //region chat listeners
    private final MessageListener chatGroupListener = message -> {
        if (message.getBody() != null) {


            ChatItemModelClass chatItemModelClass = mGson.fromJson(message.getBody(), ChatItemModelClass.class);
            chatItemModelClass.setIsGroupMessage(true);
            //ignore message sent by myself
            //except like message
            if (chatItemModelClass.getUserName() != null && (chatItemModelClass.isLiked() || !chatItemModelClass.getUserName().equals(mJoinGroupUserName))) {
                if (ChatItemModelClass.CHAT_TYPE_BOT_JOIN_LIST.equals(chatItemModelClass.getType())) {
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
                        if (mStreamChatGroupListener != null)
                            mStreamChatGroupListener.onChatGroupWatchersListReceived(getArrayCurrentUserInStream());

                    } else if (!isRooOwner) {
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
    };

    private final IncomingChatMessageListener chatManagerListener = new IncomingChatMessageListener() {
        @Override
        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
            if (message.getBody() != null) {
                Timber.e(message.getBody());
                if (isBanMessage(message.getBody())) {
                    final Gson gson = new Gson();
                    BanUserMessage banUserMessage = gson.fromJson(message.getBody(), BanUserMessage.class);
                    Timber.e(banUserMessage.toString());
                    if ((CHAT_TYPE_KICK.equals(banUserMessage.messageType) || CHAT_TYPE_UNKICK.equals(banUserMessage.messageType)) &&
                            AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(banUserMessage.userId)) {
                        EventBus.getDefault().post(convertBanUserModelToNewMessage(banUserMessage));
                    } else if (banUserMessage.messageType.equalsIgnoreCase("banUser")) {
                        mCompositeSubscription.add(Observable.just(banUserMessage.message)
                                .observeOn(AndroidSchedulers.mainThread())
                                .filter(s -> mContext != null)
                                .subscribe(s -> {
                                    Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                                    //slug in prefs
                                    AppsterUtility.saveSharedSetting(mContext, CURRENT_STREAM_SLUG, "");
                                    AppsterApplication.logout(mContext);
                                }, Timber::e));
                    }
                } else {
                    ChatItemModelClass chatItemModelClass = new ChatItemModelClass();
                    chatItemModelClass.setUserName(parseSender(parseSender(String.valueOf(message.getFrom()))));
                    chatItemModelClass.setMsg(message.getBody());
                    EventBus.getDefault().post(new NewMessageEvent(chatItemModelClass));
                }
            }
        }
    };
    //endregion

    /**
     * @param isCreate if true called by host.
     */
    public void createGroupChat(final String usernameXMPP, String displayName, final String groupName, final boolean isCreate, @Nullable StreamChatGroupListener listener) throws XmppStringprepException {

        this.groupName = groupName;
        String groupId = generateGroupName(groupName);
        if (xmppConnection == null || mIsReconnecting.get()) {
            return;
        }

        isRooOwner = isCreate;
        mJoinGroupUserName = usernameXMPP;
        // Create the XMPP address (JID) of the MUC.
        Timber.e("left previous room if any");
        leaveGroupAndRemoveCallback();
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(xmppConnection);
        multiUserChat = manager.getMultiUserChat(JidCreate.entityBareFrom(groupId));
        multiUserChat.addMessageListener(chatGroupListener);

        final String joinRoomUser = String.format(Locale.US, "%s%s%s", usernameXMPP.toLowerCase(Locale.getDefault()), DISPLAY_NAME_PREFIX, encodeString(displayName));
        try {
            try {
                // when app was force closed
                // user receive a live-stream notification
                // user click the notification to open stream
                // the xmpp can join the live-stream room.
                //the we need to reconnect after some delayed time.
                Timber.e("join room %s", groupName);
                if (multiUserChat != null) {
                    Timber.e("!multiUserChat.isJoined()");
                    MucEnterConfiguration.Builder mucEnterConfiguration = multiUserChat.getEnterConfigurationBuilder(Resourcepart.from(joinRoomUser));

                    /* temp turn this off
                    MamManager mamManager = MamManager.getInstanceFor(xmppConnection,JidCreate.entityBareFrom(groupId));
                    boolean isSupported = mamManager.isSupportedByServer();
                    if(isSupported){
                        Timber.e("MamManager isSupported!!!!");
                        MamManager.MamQueryResult mamQueryResult = mamManager.queryArchive(10);
                        for (Forwarded forwardedMessage : mamQueryResult.forwardedMessages) {
                                Timber.e(forwardedMessage.toXML().toString());
                        }
                    }else{
                        Timber.e(";( MamManager is not supported!!!!");
                    }*/
                    if (isCreate) {
                        mucEnterConfiguration.requestMaxStanzasHistory(0);
                    } else {
                        mucEnterConfiguration.requestNoHistory();
                    }

                    if (!multiUserChat.isJoined()) {
                        multiUserChat.createOrJoin(mucEnterConfiguration.build());
                    }
                    Timber.e("getParticipants %s", multiUserChat.getOccupants().toString());
                    addAllCurrentListRealUsers(multiUserChat);
                    Timber.e("join room success ");
                    if (isCreate && !mIsJoinedRoomSuccess) {
                        setConfig(multiUserChat);
                    }
                    presenceListener = presence -> {

                        if (presence != null) {

                            Timber.e(String.valueOf(presence.getFrom()));

                            String joinedGroupUsername = XmppStringUtils.parseResource(String.valueOf(presence.getFrom()));
                            String userDisplayName = "Guest";
                            Log.e(TAG, "Presence Type========================= " + joinedGroupUsername);
                            boolean isContainPrefix = joinedGroupUsername.contains(DISPLAY_NAME_PREFIX);
                            if (isContainPrefix) {
                                String[] userInfo = joinedGroupUsername.split(DISPLAY_NAME_PREFIX);
                                Timber.e("presenceListener %s", Arrays.toString(userInfo));
                                if (userInfo[0] != null && userInfo[1] != null) {
                                    joinedGroupUsername = userInfo[0];
                                    userDisplayName = decodeString(userInfo[1]);
                                }
                            }
                            if (presence.getType() == Presence.Type.available) {//user joined room
                                if (joinedGroupUsername.equalsIgnoreCase(BuildConfig.NAME_USER_DEFAULT_XMPP) || joinedGroupUsername.equalsIgnoreCase(ADMIN_USERNAME)) {
                                    return;
                                }
                                if (isRooOwner) {
                                    sendCurrentBotListToViewers();
                                }

                                if (isContainPrefix) {
                                    handleListWatcher(presence, joinedGroupUsername, userDisplayName);
                                } else {
                                    String finalUsername = joinedGroupUsername;
                                    mCompositeSubscription.add(AppsterWebServices.get().getUserImageAndDisplayName(joinedGroupUsername)
                                            .subscribe(image -> handleListWatcher(presence, finalUsername, image.getDisplayName()), error -> Timber.e(error.getMessage())));
                                }

                            } else {// user left room
                                handleListWatcher(presence, joinedGroupUsername, userDisplayName);
                            }
                        }
                    };

                    multiUserChat.addParticipantListener(presenceListener);
                }
            } catch (Exception e) {
                mIsJoinedRoomSuccess = false;
                if (listener != null) listener.onChatGroupJoinError(e.getMessage());
                Timber.e(e);
                String messageError;
                if (isCreate) {
                    messageError = mContext.getString(R.string.hot_can_not_create_group);
                    mCompositeSubscription.add(Observable.just(messageError)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(errorMessage -> Toast.makeText(mContext.getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show()));

                }

                // sometimes connected openfire, but cannot join room. because the connection.
                // reconnect again
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                    }
                    if (multiUserChat != null && !multiUserChat.isJoined()) {
                        reConnect(true);
                    }
                }).start();

//                else {
//                    messageError = mContext.getString(R.string.viewer_can_not_join_group);
//                }
//                mCompositeSubscription.add(Observable.just(messageError)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(errorMessage -> Toast.makeText(mContext.getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show()));
                return;

            }

            mIsJoinedRoomSuccess = true;

            Timber.e("join room success");
            handleListWatcher(null, "", "");
            if (listener != null) listener.onChatGroupJoinedSuccesfully();
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    private void addAllCurrentListRealUsers(MultiUserChat multiUserChat) {
        if (multiUserChat != null) {
            List<EntityFullJid> lisOccupants = multiUserChat.getOccupants();
            if (lisOccupants != null) {
                for (int i = 0; i < lisOccupants.size() && i < LIMIT_NUMBER_PRESENCE_CHAT_GROUP; i++) {
                    String usr = getUserNameFromXMPPResource(XmppStringUtils.parseResource(String.valueOf(lisOccupants.get(i))));
                    addNewWatcher(usr);
                }
            }
        }
    }

    public void createGroupChat(final String groupName, final boolean isCreate, String groupOwner) {
        mCurrentGroupOwner = groupOwner;
        try {
            createGroupChat(AppsterApplication.mAppPreferences.getUserModel().getUserName(), AppsterApplication.mAppPreferences.getUserModel().getDisplayName(), groupName, isCreate, mStreamChatGroupListener);
        } catch (XmppStringprepException e) {
            Timber.e(e);
        }

    }

    public void createGroupChatAsGuest(final String groupName, final boolean isCreate, String groupOwner) {
        mCurrentGroupOwner = groupOwner;
        try {
            createGroupChat(BuildConfig.NAME_USER_DEFAULT_XMPP, "Guest", groupName, isCreate, null);
        } catch (XmppStringprepException e) {
            Timber.e(e);
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
            LogUtils.logI(TAG, e.getMessage());
        }
    }

    @NonNull
    private String generateGroupName(String groupName) {
        return groupName + "@conference." + service;
    }


    //region connection listener
    @Override
    public void connected(XMPPConnection connection) {
        Timber.e(" connected!");
        mIsXMPPConnected = true;
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Timber.e(" authenticated  sucessfully! resume %s", resumed);
        mIsReconnecting.set(false);
        if (resumed) return;
        mLoggedIn = true;
        if (!groupName.isEmpty()) {

            Timber.e("room not empty rejoin!!!");
            if (isRooOwner) {
                createGroupChat(groupName, true, mCurrentGroupOwner);
            } else {
                createGroupChat(groupName, false, mCurrentGroupOwner);
            }
        }
    }

    @Override
    public void connectionClosed() {
        Timber.e(" connectionClosed!");
        mIsXMPPConnected = false;
        mLoggedIn = false;

        if (mIsAutoConnect) {
            reconnectIfNeed(3000);
        }
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Timber.e(" connectionClosedOnError ! %s", e.getMessage());
        mIsReconnecting.set(false);
        mIsXMPPConnected = false;
        mLoggedIn = false;

        if (mIsAutoConnect) {
            reconnectIfNeed(3000);
        }
    }

    @Override
    public void reconnectingIn(int i) {
        mIsReconnecting.set(true);
        Timber.e("reconnectingIn xmpp in %d", i);
        if (BuildConfig.DEBUG) {
            mCompositeSubscription.add(Observable.just(i)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(countDown -> showDebugToast("reconnectingIn xmpp in " + countDown, Toast.LENGTH_SHORT), error -> Timber.e(error.getMessage())));

        }
    }

    @Override
    public void reconnectionSuccessful() {
        Timber.e("reconnectionSuccessful");
        mIsReconnecting.set(false);
        reconnectIfNeed();
        if (BuildConfig.DEBUG) {
            mCompositeSubscription.add(Observable.just(true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(countDown -> showDebugToast("reconnectionSuccessful", Toast.LENGTH_SHORT), error -> Timber.e(error.getMessage())));

        }
    }

    @Override
    public void reconnectionFailed(Exception e) {
        mIsXMPPConnected = false;
        mLoggedIn = false;
        mIsReconnecting.set(false);
        if (BuildConfig.DEBUG) {
            mCompositeSubscription.add(Observable.just(e.getMessage())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(errorMessage -> showDebugToast("reconnectionFailed " + errorMessage, Toast.LENGTH_SHORT), error -> Timber.e(error.getMessage())));
        }
        if (e instanceof SASLErrorException) {
            if (BuildConfig.DEBUG) {
                mCompositeSubscription.add(Observable.just(e.getMessage())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(errorMessage -> showDebugToast("SASLErrorException - relogin xmpp", Toast.LENGTH_SHORT), error -> Timber.e(error.getMessage())));
            }
            // re-connect again automatically when re-connecting is failed
            // no need to call re-connect manually
//            if (xmppConnection != null) {
//                Timber.e("SASLErrorException ");
//                ChatManager.getInstanceFor(xmppConnection).removeListener(chatManagerListener);
//                Timber.e("removeChatListener ");
//                xmppConnection.disconnect();
//                Timber.e("xmppConnection.disconnect() ");
//                reconnectIfNeed();
//                Timber.e("reconnectIfNeed");
//                RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
//
//            }


        }
    }

    //endregion

    public void destroy() {
        leaveRoom();
        if (xmppConnection != null) {
            ChatManager.getInstanceFor(xmppConnection).removeListener(chatManagerListener);
            RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        }
    }

    public void disConnectXMPP() {
        if (xmppConnection != null) {
            mIsAutoConnect = false;
            ChatManager.getInstanceFor(xmppConnection).removeListener(chatManagerListener);
            xmppConnection.disconnect();
            xmppConnection = null;
            mIsXMPPConnected = false;
            RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        }
    }

    public void disconnectConnection() {
        if (xmppConnection != null) {
            xmppConnection.disconnect();
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public void leaveRoom() {
        this.groupName = "";
        isRooOwner = false;
        mIsJoinedRoomSuccess = false;
        mIsReconnecting.set(false);
        mStreamChatGroupListener = null;
        if (arrayCurrentUserInStream != null) {
            arrayCurrentUserInStream.clear();
            arrayCurrentUserInStream = null;
        }
        leaveGroupAndRemoveCallback();

    }

    private void leaveGroupAndRemoveCallback() {
        if (multiUserChat != null) {
            try {
                multiUserChat.removeMessageListener(chatGroupListener);
                multiUserChat.removeParticipantListener(presenceListener);
                multiUserChat.leave();
                multiUserChat = null;
                presenceListener = null;
                Timber.e("left room");
            } catch (SmackException.NotConnectedException | InterruptedException e) {
                Timber.e(e);
            }
        }
    }

    public void destroyRoom() {
        this.groupName = "";
        if (multiUserChat != null && multiUserChat.isJoined()) {
            try {
                multiUserChat.destroy("destroy_this_Room", multiUserChat.getRoom());
                multiUserChat.removeMessageListener(chatGroupListener);
                multiUserChat.removeParticipantListener(presenceListener);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public void reconnectIfNeed(final int delayMillisec) {
        if (xmppConnection == null || !xmppConnection.isConnected()) {
            new Thread(() -> {
                if (delayMillisec > 0) {
                    try {
                        Thread.sleep(delayMillisec);
                    } catch (InterruptedException e) {
                    }
                }

                reConnect();
            }).start();
        }
    }

    public void reconnectIfNeed() {
        reconnectIfNeed(0);
    }

    private synchronized void reConnect() {
        reConnect(false);
    }

    private synchronized void reConnect(boolean isForceConnect) {
        if (isForceConnect) {
            disConnectXMPP();
        }

        if (AppPreferences.getInstance(mContext).getUserModel() != null) {
            username = AppPreferences.getInstance(mContext).getUserModel().getUserName().toLowerCase(Locale.getDefault());
            password = AppPreferences.getInstance(mContext).getUserModel().getUserName().toLowerCase(Locale.getDefault());
            displayName = AppPreferences.getInstance(mContext).getUserModel().getDisplayName();
            connectToXMPPServer(username, password, displayName);
        }
    }

    public void reconnectIfViaPushNotification() {
        if (xmppConnection == null || !xmppConnection.isConnected()) {
            reConnect();
        }
    }

    private void showDebugToast(String message, int length) {
        if (mDebugToast != null) mDebugToast.cancel();
        mDebugToast = Toast.makeText(mContext, message, length);
        mDebugToast.show();
    }

    public String getCurrentUserIDChatWith() {
        return currentUserIDChatWith;
    }

    public void setCurrentUserIDChatWith(String currentUserIDChatWith) {
        this.currentUserIDChatWith = currentUserIDChatWith;
    }

    public boolean isChatWithUser() {
        return isChatWithUser;
    }

    public void setIsChatWithUser(boolean isChatWithUser) {
        this.isChatWithUser = isChatWithUser;
    }

    public List<String> getListUserIdSendNewMessages() {
        return AppsterApplication.mAppPreferences.getListUserNameSendNewMessages();
    }

    public void saveUserNameSendNewMessage(String userName) {
        AppsterApplication.mAppPreferences.saveUserNameSendNewMessage(userName);
    }

    public void deleteUserNameSendNewMessage(String userName) {
        AppsterApplication.mAppPreferences.deleteUserNameSendNewMessage(userName);
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

    void handleListWatcher(Presence presence, String newUsername, String displayName) {
        boolean isJoin = false;
        if (newUsername.equalsIgnoreCase(ADMIN_USERNAME) || isGuestUser(newUsername)) {
            Timber.e("is guest/admin user returned");
            return;
        }
        if (arrayCurrentUserInStream == null) {
            arrayCurrentUserInStream = new LinkedHashSet<>();
        }
        if (arrayCurrentUserInStream.isEmpty()) {
            Timber.e("arrayCurrentUserInStream.isEmpty()");
            if (multiUserChat != null) {
                List<EntityFullJid> lisOccupants = multiUserChat.getOccupants();
                if (lisOccupants != null) {
                    for (int i = 0; i < lisOccupants.size() && i < LIMIT_NUMBER_PRESENCE_CHAT_GROUP; i++) {
                        String usr = getUserNameFromXMPPResource(XmppStringUtils.parseResource(String.valueOf(lisOccupants.get(i))));
                        if (usr.equals(newUsername)) {
                            addNewWatcher(newUsername);
                            isJoin = true;
                        } else {
                            addNewWatcher(usr);
                        }
                    }
                }
            }
        } else {
            if (presence != null) {
                Presence.Type type = presence.getType();
                String usr = XmppStringUtils.parseResource(String.valueOf(presence.getFrom()));
                usr = getUserNameFromXMPPResource(usr);
                if (type.equals(Presence.Type.available)) {
                    addNewWatcher(usr);
                    removeListWhacherIfOverNumber();
                    isJoin = true;

                } else if (type.equals(Presence.Type.unavailable)) {
                    removeWatcher(usr);
                    isJoin = false;
                }
            }
        }

        if (arrayCurrentUserInStream != null) {
            EventBus.getDefault().post(new UserJoinLeaveEvent(isJoin, newUsername, displayName));
            Timber.e("arrayCurrentUserInStream %s %s", arrayCurrentUserInStream.size(), newUsername);
        }


    }

    @NonNull
    private String getUserNameFromXMPPResource(String usr) {
        String userName = usr;
        boolean isContainPrefix = userName.contains(DISPLAY_NAME_PREFIX);
        if (isContainPrefix) {
            String[] userInfo = userName.split(DISPLAY_NAME_PREFIX);
            if (userInfo[0] != null && userInfo[1] != null) {
                userName = userInfo[0];
            }
        }
        return userName;
    }

    boolean isRoomOwner(String username) {
        return !TextUtils.isEmpty(mCurrentGroupOwner) && mCurrentGroupOwner.equals(username);
    }

    boolean isAppOwner(String username) {
        return (AppsterApplication.mAppPreferences.isUserLogin() && AppsterApplication.mAppPreferences.getUserModel().getUserName().equalsIgnoreCase(username));
    }

    boolean isGuestUser(String username) {
        return BuildConfig.NAME_USER_DEFAULT_XMPP.equals(username);
    }


    public void addBotUserViewer(String botUserName) {
        //since ios send bot list which includes real users
        //so we have to filter room owner, guest, app owner
        if (isAppOwner(botUserName) || isGuestUser(botUserName) || isRoomOwner(botUserName)) {
            return;
        }

        if (arrayCurrentBotInStream == null) {
            arrayCurrentBotInStream = new LinkedHashSet<>();
        }

        if (arrayCurrentUserInStream == null) {
            arrayCurrentUserInStream = new LinkedHashSet<>();
        }

        if (!checkUserIsExits(botUserName)) {
            Timber.e("addBotUserViewer %s", botUserName);
            mCurrentUserSparseArray.put(botUserName, botUserName);
            arrayCurrentBotInStream.add(botUserName);
            arrayCurrentUserInStream.add(botUserName);
            handleListWatcher(null, "", "");
        }
    }

    public void removeBotUserViewer(String botUserName) {
        if (arrayCurrentBotInStream == null || arrayCurrentUserInStream == null) return;

        Timber.e("removeBotUserViewer %s", botUserName);
        arrayCurrentBotInStream.remove(botUserName);
        removeWatcher(botUserName);
        handleListWatcher(null, "", "");
    }

    private boolean checkUserIsExits(String botUserName) {
        return mCurrentUserSparseArray.containsKey(botUserName);
    }

    private void removeListWhacherIfOverNumber() {

        if (arrayCurrentUserInStream != null && arrayCurrentUserInStream.size() > LIMIT_NUMBER_PRESENCE_CHAT_GROUP) {

//            for (int i = arrayCurrentUserInStream.size() - 1; i >= LIMIT_NUMBER_PRESENCE_CHAT_GROUP - 1; i--) {
//                arrayCurrentUserInStream.remove(i);
//            }
        }

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

    private void removeWatcher(String userName) {
        if (arrayCurrentUserInStream != null) {
            arrayCurrentUserInStream.remove(userName);
        }
    }

    public List<String> getArrayCurrentBotInStream() {
        return arrayCurrentBotInStream != null ? new ArrayList<>(arrayCurrentBotInStream) : new ArrayList<>();
    }

}