package com.appster.features.messages

import android.os.Handler
import com.appster.AppsterApplication
import com.appster.R
import com.appster.features.mvpbase.BasePresenter
import com.appster.manager.AppsterChatManger
import com.appster.models.event_bus_models.NewMessageEvent
import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.request_models.DeleteMessageRequestModel
import com.appster.webservice.request_models.GetListMessageRequestModel
import com.apster.common.Constants
import com.pack.utility.CheckNetwork
import com.pack.utility.StringUtil
import rx.Observable
import javax.inject.Inject

/**
 * Created by DatTN on 10/5/2018
 *
 * Handle logic for message list screen
 */
class MessageListPresenter @Inject
constructor(private val mChatManager: AppsterChatManger,
            private val mWebService: AppsterWebserviceAPI) :
        BasePresenter<MessageListContract.View>(), MessageListContract.UserActions {

    private var mNextIndexList = 0
    private var mIsLoading = false
    private var mIsEnd = false

    private var mMessageItems = mutableListOf<MessageItemModelClass>()

    //region -------implement methods-------
    override fun loadMessages(showLoading: Boolean) {
        // loadMessages method needs context to perform checking connection.
        // checkViewAttached makes sure a base view is attached to this presenter, it means the view#viewContext will be called correctly
        checkViewAttached()
        if (CheckNetwork.isNetworkAvailable(getContext())) {
            mNextIndexList = 0

            loadMessageList(showLoading)
            mChatManager.setIsChatWithUser(false)
        } else {
            view?.showDialog(R.string.app_name, R.string.no_internet_connection)
        }
    }

    override fun refreshMessages() {
        mNextIndexList = 0
        loadMessageList(false)
    }

    override fun loadMoreMessages(delayTime: Long) {
        if (mIsEnd) {
            return
        }
        view?.showProgress()
        val handler = Handler()
        handler.postDelayed({ loadMessageList(false) }, delayTime)
    }

    override fun openMessage(messageItemModelClass: MessageItemModelClass) {
        if (CheckNetwork.isNetworkAvailable(getContext())) {
            messageItemModelClass.unread_message_count = 0
            view?.apply {
                notifyMessageUpdated(messageItemModelClass)
                openMessage(messageItemModelClass)
            }
        } else {
            view?.showDialog(R.string.app_name, R.string.no_internet_connection)
        }
    }

    override fun deleteMessage(messageItemModelClass: MessageItemModelClass, position: Int) {
        view?.showLoadingDialog(R.string.connecting_msg)

        val request = DeleteMessageRequestModel()
        request.receiver_user_id = messageItemModelClass.msg_user_id

        addSubscription(mWebService.deleteMessage("Bearer " + AppsterApplication.mAppPreferences.userToken, request)
                .subscribe({ deleteMessageResponseModel ->
                    view?.hideLoadingDialog()
                    if (deleteMessageResponseModel == null) return@subscribe

                    if (deleteMessageResponseModel.code == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        mMessageItems.removeAt(position)
                        view?.notifyMessageDeleted(messageItemModelClass, position)
                        if (mIsEnd) checkUnreadMessage(mMessageItems)
                        if (mMessageItems.size == 0) {
                            view?.showEmptyView()
                        }

                    } else {
                        view?.loadError(deleteMessageResponseModel.message,
                                deleteMessageResponseModel.code)
                    }
                }, { error ->
                    view?.apply {
                        hideLoadingDialog()
                        loadError(error.message, Constants.RETROFIT_ERROR)
                    }
                }))
    }

    override fun receiveNewMessage(messageEvent: NewMessageEvent) {
        AppsterApplication.mAppPreferences.numberUnreadMessage = 1
        loadMessages(false)
    }

    override fun resume() {
        if (mIsEnd) {
            checkUnreadMessage(mMessageItems)
        }
    }
    //endregion -------implement methods-------

    //region -------inner methods-------
    private fun loadMessageList(showLoading: Boolean) {
        if (showLoading) {
            view?.showLoadingDialog(R.string.connecting_msg)
        }
        mIsLoading = true
        val request = GetListMessageRequestModel()
        request.limit = Constants.PAGE_LIMITED
        request.nextId = mNextIndexList

        addSubscription(mWebService.getListMessage("Bearer " + AppsterApplication.mAppPreferences.userToken, request)
                .subscribe({ getListMessageResponseModel ->
                    mIsLoading = false
                    view?.apply {
                        hideRefreshLayoutIfNeeded()
                        hideLoadingDialog()
                        hideProgress()
                    }

                    if (getListMessageResponseModel.code == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        if (getListMessageResponseModel.data.result != null) {
                            if (mNextIndexList == 0) {
                                mMessageItems.clear()
                            }
                            mMessageItems.addAll(getListMessageResponseModel.data.result)
                            removeUnreadCount()
                            // Show all the message items so far. Normally, this is not a best practice because we should not take about the previous loaded items
                            // But our adapter is smart enough and with a little help from diff util, it is OK to notify the whole list
                            view?.showMessages(mMessageItems, mNextIndexList == 0)
                            // handle notification indicator new message
                            mNextIndexList = getListMessageResponseModel.data.nextId
                            mIsEnd = getListMessageResponseModel.data.isEnd
                            if (mIsEnd) checkUnreadMessage(mMessageItems)
                        }

                        view?.apply {
                            if (mMessageItems.size == 0) {
                                showEmptyView()
                            } else {
                                hideEmptyView()
                            }
                        }

                    } else {
                        view?.loadError(getListMessageResponseModel.message, getListMessageResponseModel.code)
                    }
                    view?.notifyDataLoaded()
                }, { error ->
                    mIsLoading = false
                    view?.apply {
                        hideRefreshLayoutIfNeeded()
                        hideLoadingDialog()
                        hideProgress()
                        loadError(error.message, Constants.RETROFIT_ERROR)
                        notifyDataLoaded()
                    }
                }))
    }

    private fun removeUnreadCount() {
        val hasChatWithUserId = mChatManager.currentUserIDChatWith
        if (StringUtil.isNullOrEmptyString(hasChatWithUserId)) return
        for (i in mMessageItems.indices) {
            if (mMessageItems[i].msg_user_id == hasChatWithUserId) {
                mMessageItems[i].unread_message_count = 0
                break
            }
        }

        mChatManager.currentUserIDChatWith = ""
    }

    private fun checkUnreadMessage(messageItems: List<MessageItemModelClass>?) {
        if (messageItems == null) return
        if (messageItems.isEmpty()) {
            AppsterApplication.mAppPreferences.numberUnreadMessage = 0
            return
        }
        addSubscription(Observable.from(messageItems)
                .all { it != null && it.unread_message_count == 0 }
                .filter { it }
                .subscribe { AppsterApplication.mAppPreferences.numberUnreadMessage = 0 })
    }
    //endregion -------inner methods-------
}
