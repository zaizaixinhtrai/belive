package com.appster.features.messages

import androidx.annotation.StringRes
import com.appster.features.mvpbase.BaseContract
import com.appster.models.event_bus_models.NewMessageEvent

interface MessageListContract {

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
         * Show on the UI a list of messages which is loaded from server.
         *
         * Note, that the message list here contains all of the loaded items so far. You need to make sure not to show the duplicated items
         */
        fun showMessages(messages: List<MessageItemModelClass>, clearOldData: Boolean)

        /**
         * Show an empty view when there is no data loaded
         */
        fun showEmptyView()

        /**
         * Hide an empty view because we have items to show on the screen
         */
        fun hideEmptyView()

        /**
         * If the swipe layout is in loading state. Reset it
         */
        fun hideRefreshLayoutIfNeeded()

        // Our adapter use a flag to keep tracking the loading state.
        // Since our presenter does not know anything about view state.
        // We create this method for the presenter to notify the loading state.
        // In my opinion, it is better to merge this method to showMessages & error method. But other methods is too specific which make inline this method no sense
        /**
         * Notify the data task is finish. Either the data is loaded or an error occurs
         */
        fun notifyDataLoaded()

        /**
         * Open a message detail (conversation detail)
         *
         * @param messageItemModelClass the message to open its detail
         */
        fun openMessage(messageItemModelClass: MessageItemModelClass)

        /**
         * Notify a message get updated (its info changed)
         *
         * @param messageItemModelClass the message which is updated and contains the newest information
         */
        fun notifyMessageUpdated(messageItemModelClass: MessageItemModelClass)

        /**
         * Notify a message get deleted
         *
         * @param messageItemModelClass the delete message
         * @param position the position (0-start index) of the message in the list
         */
        fun notifyMessageDeleted(messageItemModelClass: MessageItemModelClass, position: Int)
    }

    interface UserActions : BaseContract.Presenter<MessageListContract.View> {
        /**
         * Load messages from server.
         *
         * @param showLoading: true to indicate that the view should show a loading view when performing the task. false otherwise
         */
        fun loadMessages(showLoading: Boolean)

        /**
         * Reload the message
         */
        fun refreshMessages()

        /**
         * Load more when a load more trigger is started
         */
        fun loadMoreMessages(delayTime: Long)

        /**
         * Open a conversation from a specific message
         *
         * @param messageItemModelClass: the message which we are opening
         */
        fun openMessage(messageItemModelClass: MessageItemModelClass)

        /**
         * Delete a message
         *
         * @param messageItemModelClass: the message which will be deleted
         */
        fun deleteMessage(messageItemModelClass: MessageItemModelClass, position: Int)

        fun receiveNewMessage(messageEvent: NewMessageEvent)

        /**
         * Called by view's on resume (if the attached view supports this life cycle method)
         */
        fun resume()
    }
}
