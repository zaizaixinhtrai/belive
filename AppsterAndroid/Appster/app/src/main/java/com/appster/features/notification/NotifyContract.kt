package com.appster.features.notification

import com.appster.features.mvpbase.BaseContract
import com.appster.models.NotificationItemModel
import com.appster.webservice.request_models.NotificationRequestModel

interface NotifyContract {

    interface View : BaseContract.View {

        fun setDataForListView(data: List<NotificationItemModel>, isEnded: Boolean, nextIndex: Int)
        fun onHandleUiAfterApiReturn();
    }

    interface UserActions : BaseContract.Presenter<NotifyContract.View> {
        fun getNotificationList(request: NotificationRequestModel, isReload: Boolean)
    }
}