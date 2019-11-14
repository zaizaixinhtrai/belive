package com.appster.features.messages.chat

import android.graphics.Bitmap
import androidx.annotation.StringRes
import com.appster.features.mvpbase.BaseContract
import com.appster.message.ChatItemModelClass
import com.appster.models.event_bus_models.NewMessageEvent
import com.appster.sendgift.GiftItemModel

interface ChatContract {

    interface View : BaseContract.View {
        /**
         * Show a message dialog.
         *
         * Although the presenter has a way to access the context.
         * It needs to be independent as much as possible, that's why this method received 2 string resource ids instead of 2 strings
         *
         * @param titleRes the title string resource id
         * @param contentRes the content string resource id
         */
        fun showDialog(@StringRes titleRes: Int, @StringRes contentRes: Int)

        /**
         * Trigger when receive user has been suspended
         */
        fun notifyUserSuspended()

        /**
         * Trigger when receive user has blocked you
         */
        fun notifyUserBlockedYou(removeLastItem: Boolean = false)

        /**
         * Show a loading dialog
         *
         * @param contentRes the content string resource id
         */
        fun showLoadingDialog(contentRes: Int)

        /**
         * Hide the loading dialog
         */
        fun hideLoadingDialog()

        /**
         * Display a list of chat items on the screen. Views are freely to choose their own way to display this list
         *
         * @param chatItems the items which is loaded from server
         */
        fun showChat(chatItems: List<ChatItemModelClass>)

        /**
         * Require the view list to scroll to the last item in the list
         *
         * @param fastScroll true to require the list to scroll fast. Otherwise, it needs to slowly scroll
         */
        fun scrollToTheLatestChatItem(fastScroll: Boolean = false)

        /**
         * Trigger when there is a new chat item is sent to receive user (it is not necessary if the receive user get the message or not)
         */
        fun notifyNewChatItem(chatItem: com.appster.message.ChatItemModelClass)

        /**
         * Trigger when the chat item is sync successfully with server
         */
        fun notifyChatSyncToServer()

        /**
         * Require view to clear image data when image message is sent
         */
        fun clearImagePhoto(bitmap: Bitmap)

        /**
         * Clear the composer (edittext for example)
         */
        fun clearComposer()
    }

    interface UserActions : BaseContract.Presenter<ChatContract.View> {
        /**
         * Load the latest chat
         */
        fun loadLastChat()

        /**
         * Send a text message to receiver user
         */
        fun sendChat(content: String)

        /**
         * Send an image message to server
         */
        fun sendImageToServer(bitmap: Bitmap)

        /**
         * Send a video message to server
         */
        fun sendVideoToServer(videoPath: String)

        /**
         * Send a request to indicate that the conversation is ended
         */
        fun leaveChat()

        /**
         * Sync the message with server. Normally, all of the sendXXX method needs to call this method to ensure the message is sync with server.
         * Otherwise, the message will be lost
         */
        fun syncChatToServer(body: String)

        fun isUserBlocked(): Boolean
        fun completeSendGift(giftItem: GiftItemModel)
        /**
         * Load the previous chat history
         */
        fun loadPreviousChat()

        fun reconnectIfNeeded()
        fun initUserData(chatUserId: String, chatUserName: String, chatUserDisplayName: String, chatUserAvatar: String = "", chatUserGender: String = "")
        /**
         * Handle task when receive a new message from receive user
         */
        fun receiveNewMessage(newMessage: NewMessageEvent)
    }
}
