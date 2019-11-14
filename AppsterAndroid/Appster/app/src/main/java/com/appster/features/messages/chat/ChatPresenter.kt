package com.appster.features.messages.chat

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import com.appster.AppsterApplication
import com.appster.R
import com.appster.features.mvpbase.BasePresenter
import com.appster.manager.AppsterChatManger
import com.appster.manager.ShowErrorManager
import com.appster.message.ChatItemModelClass
import com.appster.models.event_bus_models.NewMessageEvent
import com.appster.sendgift.GiftItemModel
import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.request_models.*
import com.apster.common.CommonDefine
import com.apster.common.Constants
import com.apster.common.Utils
import com.pack.utility.CheckNetwork
import com.pack.utility.Data
import com.pack.utility.StringUtil
import com.pack.utility.VideoUtil
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.stringprep.XmppStringprepException
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ChatPresenter @Inject constructor(private val mChatManager: AppsterChatManger,
                                        private val mWebService: AppsterWebserviceAPI) : BasePresenter<ChatContract.View>(), ChatContract.UserActions {

    private var mChattingUserId: String = ""
    private var mChattingUserName: String = ""
    private var mChattingUserDisplayName: String = ""
    private var mChattingUserAvatar: String = ""
    private var mChattingUserGender: String = ""

    private var mIsUserSuspended = false
    private var mUserBlockedYou = false
    private var mIsEnd = false
    private var mNextIndex = 0
    private var mIsFirstLoad = true
    private var mIsLoadingPrevious = false
    var allowToReceiveMessageInSetting = AppsterApplication.mAppPreferences.userModel.messaging == 0

    override fun initUserData(chatUserId: String, chatUserName: String, chatUserDisplayName: String, chatUserAvatar: String, chatUserGender: String) {
        mChattingUserId = chatUserId
        mChattingUserName = chatUserName
        mChattingUserDisplayName = chatUserDisplayName
        mChattingUserAvatar = chatUserAvatar
        mChattingUserGender = chatUserGender
    }

    override fun sendImageToServer(bitmap: Bitmap) {
        view?.showLoadingDialog(R.string.connecting_msg)
        val request = ChatPostImageResquestModel(Utils.getFileFromBitMap(getContext(), bitmap))

        addSubscription(mWebService.chatPostImage("Bearer " + AppsterApplication.mAppPreferences.userToken, request.build())
                .subscribe({ chatPostImageResponseModel ->
                    view?.hideLoadingDialog()

                    if (chatPostImageResponseModel == null) return@subscribe
                    if (chatPostImageResponseModel.code != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        view?.loadError(chatPostImageResponseModel.message, chatPostImageResponseModel.code)
                        return@subscribe
                    }
                    if (!mChatManager.isConneted) {
                        return@subscribe
                    }
                    val strSend = (getTimeStamp()
                            + CommonDefine.KEY_USER_SEND_TIME
                            + CommonDefine.KEY_USER_SEND_IMAGE
                            + chatPostImageResponseModel.data.image_url
                            + CommonDefine.KEY_USER_SEND_THUMBIMAGE
                            + chatPostImageResponseModel.data.thumbnail_url)

                    val to = mChattingUserName + "@" + Data.Ip_Address
                    val msg: Message
                    try {
                        msg = Message(JidCreate.from(to), Message.Type.chat)
                        msg.body = strSend
                        mChatManager.sendMessage(msg)
                        // Add message to list
                        val chatItemModelClass = ChatItemModelClass()
                        chatItemModelClass.userIdSend = AppsterApplication.mAppPreferences.userModel.userId
                        chatItemModelClass.msg = strSend
                        chatItemModelClass.chatDisplayName = mChattingUserDisplayName
                        view?.apply {
                            notifyNewChatItem(chatItemModelClass)
                            scrollToTheLatestChatItem()
                        }
                        syncChatToServer(strSend)
                    } catch (e: XmppStringprepException) {
                        e.printStackTrace()
                    }

                }, { error ->
                    view?.apply {
                        hideLoadingDialog()
                        loadError(error.message, Constants.RETROFIT_ERROR)
                    }
                }))
        view?.clearImagePhoto(bitmap)
    }

    override fun sendVideoToServer(videoPath: String) {
        view?.showLoadingDialog(R.string.connecting_msg)
        val imageThumbnail = VideoUtil.createVideoThumbnail(videoPath)
        val video = File(videoPath)

        val request = ChatPostVideoResquestModel(Utils.getFileFromBitMap(getContext(), imageThumbnail), video)
        addSubscription(mWebService.chatPostVideo("Bearer " + AppsterApplication.mAppPreferences.userToken, request.build())
                .subscribe({ chatPostVideoResponseModel ->
                    view?.hideLoadingDialog()

                    if (chatPostVideoResponseModel == null) return@subscribe
                    if (chatPostVideoResponseModel.code != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        view?.loadError(chatPostVideoResponseModel.message, chatPostVideoResponseModel.code)
                        return@subscribe
                    }
                    if (!mChatManager.isConneted) {
                        Log.e("isConnected", mChatManager.isConneted.toString())
                        return@subscribe
                    }

                    val strSend = (getTimeStamp()
                            + CommonDefine.KEY_USER_SEND_TIME
                            + CommonDefine.KEY_USER_SEND_VIDEO
                            + chatPostVideoResponseModel.data.video_url
                            + CommonDefine.KEY_USER_SEND_THUMBIMAGE
                            + chatPostVideoResponseModel.data.image_url)

                    val to = mChattingUserName + "@" + Data.Ip_Address
                    val msg: Message
                    try {
                        msg = Message(JidCreate.from(to), Message.Type.chat)
                        msg.body = strSend
                        mChatManager.sendMessage(msg)
                        // Add message to list
                        val chatItemModelClass = ChatItemModelClass()
                        chatItemModelClass.userIdSend = AppsterApplication.mAppPreferences.userModel.userId
                        chatItemModelClass.msg = strSend
                        chatItemModelClass.chatDisplayName = mChattingUserDisplayName
                        view?.apply {
                            notifyNewChatItem(chatItemModelClass)
                            scrollToTheLatestChatItem()
                        }
                        syncChatToServer(strSend)
                    } catch (e: XmppStringprepException) {
                        e.printStackTrace()
                    }

                }, { error ->
                    view?.apply {
                        hideLoadingDialog()
                        loadError(error.message, Constants.RETROFIT_ERROR)
                    }
                }))
        view?.clearImagePhoto(imageThumbnail)
    }

    override fun leaveChat() {
        val model = LeaveCurrentConversationRequestModel()
        model.receiver_user_id = mChattingUserId
        addSubscription(mWebService.leaveConversation("Bearer " + AppsterApplication.mAppPreferences.userToken, model)
                .subscribe({
                    //do nothing
                }, { error -> Timber.e(error.message) }))
    }

    override fun syncChatToServer(body: String) {
        val request = SaveChatRequestModel()
        request.receiver_user_id = mChattingUserId
        request.message = body

        addSubscription(mWebService.savedChat("Bearer " + AppsterApplication.mAppPreferences.userToken, request)
                .subscribe({ saveChatResponseModel ->
                    if (saveChatResponseModel == null) return@subscribe

                    if (saveChatResponseModel.code == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        view?.notifyChatSyncToServer()
                    } else {
                        if (saveChatResponseModel.code == ShowErrorManager.message_turn_off) {
                            mUserBlockedYou = true
                            view?.notifyUserBlockedYou(true)
                        } else {
                            if (saveChatResponseModel.code == ShowErrorManager.user_not_found) {
                                mIsUserSuspended = true
                                view?.notifyUserSuspended()
                            } else {
                                view?.loadError(saveChatResponseModel.message, saveChatResponseModel.code)
                            }
                        }
                    }
                    // Cheating wait to know that need reload message
                    mChatManager.setIsChatWithUser(true)
                }, {
                    view?.loadError(it.message, Constants.RETROFIT_ERROR)
                }))
    }

    override fun isUserBlocked(): Boolean {
        return mIsUserSuspended
    }

    override fun loadLastChat() {
        checkViewAttached()
        if (mChattingUserId.isEmpty()) {
            view?.showDialog(R.string.app_name, R.string.user_not_found)
            return
        }
        mChatManager.currentUserIDChatWith = mChattingUserId
        if (CheckNetwork.isNetworkAvailable(getContext())) {
            loadHistory(true)
        } else {
            view?.showDialog(R.string.app_name, R.string.no_internet_connection)
        }
    }

    override fun completeSendGift(giftItem: GiftItemModel) {
        if (!mChatManager.isConneted) {
            Log.e("isConnected", mChatManager.isConneted.toString())
            return
        }
        // Add message to list
        val chatItemModelClass = ChatItemModelClass()
        chatItemModelClass.userIdSend = AppsterApplication.mAppPreferences.userModel.userId
        chatItemModelClass.chatDisplayName = mChattingUserDisplayName
        val strSend = (getTimeStamp()
                + CommonDefine.KEY_USER_SEND_TIME
                + CommonDefine.KEY_USER_SEND_GIFT
                + giftItem.giftName)
        val to = mChattingUserName + "@" + Data.Ip_Address
        val msg: Message
        try {
            msg = Message(JidCreate.from(to), Message.Type.chat)
            msg.body = strSend
            chatItemModelClass.msg = strSend
            mChatManager.sendMessage(msg)
            view?.apply {
                notifyNewChatItem(chatItemModelClass)
                scrollToTheLatestChatItem()
            }
            syncChatToServer(strSend)
        } catch (e: XmppStringprepException) {
            e.printStackTrace()
        }

    }

    override fun loadPreviousChat() {
        if (mIsLoadingPrevious || mIsFirstLoad) {
            return
        }
        if (mIsEnd) {
            view?.hideProgress()
            return
        }
        if (CheckNetwork.isNetworkAvailable(getContext())) {
            mIsLoadingPrevious = true
            loadHistory(false)
        } else {
            view?.showDialog(R.string.app_name, R.string.no_internet_connection)
        }
    }

    override fun sendChat(content: String) {
        if (mIsUserSuspended) {
            view?.notifyUserSuspended()
            return
        }
        if (mUserBlockedYou) {
            view?.notifyUserBlockedYou()
            return
        }
        if (!mChatManager.isConneted) {
            return
        }
        if (!CheckNetwork.isNetworkAvailable(getContext())) {
            view?.showDialog(R.string.app_name, R.string.no_internet_connection)
            return
        }
        val to = mChattingUserName + "@" + Data.Ip_Address

        var textSend = content
        if (TextUtils.isEmpty(textSend)) {
            return
        }
        textSend = StringUtil.encodeString(textSend)

        val msg = Message(JidCreate.from(to), Message.Type.chat)
        val bodySend = getTimeStamp() + CommonDefine.KEY_USER_SEND_TIME + textSend
        msg.body = bodySend

        mChatManager.sendMessage(msg)

        val chatItemModelClass = ChatItemModelClass()
        chatItemModelClass.userIdSend = AppsterApplication.mAppPreferences.userModel.userId
        chatItemModelClass.msg = bodySend

        view?.apply {
            notifyNewChatItem(chatItemModelClass)
            scrollToTheLatestChatItem()
            clearComposer()
        }
        // Saved Chat
        syncChatToServer(bodySend)
    }

    override fun reconnectIfNeeded() {
        mChatManager.reconnectIfNeed()
    }

    override fun receiveNewMessage(newMessage: NewMessageEvent) {
        if (newMessage.data != null && newMessage.data.userName == mChattingUserName) {
            if (!allowToReceiveMessageInSetting) {
                view?.apply {
                    notifyNewChatItem(newMessage.data)
                    scrollToTheLatestChatItem()
                }
            }
            saveTotalGoldFans(newMessage.data)
        }
    }
    //endregion -------implement methods-------

    //region -------inner methods-------
    private fun getTimeStamp(): String {
        val unixTime = System.currentTimeMillis() / 1000L
        return unixTime.toString()
    }

    private fun loadHistory(showLoading: Boolean) {
        if (mIsEnd) {
            view?.hideProgress()
            return
        }
        view?.apply {
            if (showLoading) {
                showLoadingDialog(R.string.connecting_msg)
            }
            if (mIsLoadingPrevious) {
                // we are loading the previous chat.
                showProgress()
            }
        }
        val request = ChatHistoryRequest()
        request.receiver_user_id = mChattingUserId
        request.nextId = mNextIndex
        request.limit = Constants.PAGE_LIMITED

        addSubscription(mWebService.getHistoryChat("Bearer " + AppsterApplication.mAppPreferences.userToken, request)
                .subscribe({ chatHistoryResponseModel ->
                    view?.hideLoadingDialog()
                    if (mIsLoadingPrevious) {
                        mIsLoadingPrevious = false
                        view?.hideProgress()
                    }
                    if (chatHistoryResponseModel == null) return@subscribe
                    if (chatHistoryResponseModel.code != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        if (chatHistoryResponseModel.code == ShowErrorManager.message_turn_off) {
                            mUserBlockedYou = true
                            view?.notifyUserBlockedYou()
                        } else {
                            if (chatHistoryResponseModel.code == ShowErrorManager.user_not_found) {
                                mIsUserSuspended = true
                                view?.notifyUserSuspended()
                            } else {
                                view?.loadError(chatHistoryResponseModel.message, chatHistoryResponseModel.code)
                            }
                        }
                        return@subscribe
                    }
                    mNextIndex = chatHistoryResponseModel.data.nextId
                    if (chatHistoryResponseModel.data.result.chatHistory != null) {
                        if (chatHistoryResponseModel.data.result.chatHistory.size > 0 && TextUtils.isEmpty(mChattingUserGender)) {
                            for (i in 0 until chatHistoryResponseModel.data.result.chatHistory.size) {
                                if (chatHistoryResponseModel.data.result.chatHistory[i].userIdSend == mChattingUserId) {
                                    mChattingUserGender = chatHistoryResponseModel.data.result.chatHistory[i].gender
                                    mChattingUserAvatar = chatHistoryResponseModel.data.result.chatHistory[i].profilePic
                                    break
                                }
                            }
                        }
                        view?.showChat(chatHistoryResponseModel.data.result.chatHistory)
                        if (mIsFirstLoad) {
                            view?.scrollToTheLatestChatItem(true)
                            mIsFirstLoad = false
                        }
                    }
                    mIsEnd = chatHistoryResponseModel.data.isEnd
                }, {
                    if (mIsLoadingPrevious) {
                        mIsLoadingPrevious = false
                        view?.hideProgress()
                    }
                    view?.apply {
                        hideLoadingDialog()
                        loadError(it.message, Constants.RETROFIT_ERROR)
                    }
                }))
    }

    private fun saveTotalGoldFans(data: ChatItemModelClass?) {
        if (data != null) {
            if (ChatItemModelClass.CHAT_TYPE_GIFT == data.type) {
                AppsterApplication.mAppPreferences.userModel.totalGoldFans = java.lang.Long.parseLong(data.receiverStars)
            }
        }
    }
//endregion -------inner methods-------
}
