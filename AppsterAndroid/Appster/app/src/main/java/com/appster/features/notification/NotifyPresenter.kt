package com.appster.features.notification

import com.appster.features.mvpbase.BasePresenter
import com.appster.webservice.request_models.NotificationRequestModel
import com.apster.common.Constants
import com.data.exceptions.BeLiveServerException
import com.domain.interactors.notificaton.NotificationListUseCase
import javax.inject.Inject

class NotifyPresenter @Inject constructor(private val notificationListUseCase: NotificationListUseCase,
                                          notifyView: NotifyContract.View)
    : BasePresenter<NotifyContract.View>(), NotifyContract.UserActions {

    init {
        attachView(notifyView)
    }

    override fun getNotificationList(request: NotificationRequestModel, isReload: Boolean) {
        if (isReload) view?.showProgress()

        addSubscription(notificationListUseCase.execute(NotificationListUseCase.Params.load(request.notification_status, request.nextId, Constants.PAGE_LIMITED))
                .subscribe({ response ->
                    view?.onHandleUiAfterApiReturn()
                    if (isReload) view?.hideProgress()
                    response?.apply {
                        view?.apply {
                            setDataForListView(response.result, response.isEnd, response.nextId)
                        }
                    }
                }, { error ->
                    view?.onHandleUiAfterApiReturn()
                    if (isReload) view?.hideProgress()
                    view?.apply {
                        onHandleUiAfterApiReturn()
                        if (error is BeLiveServerException) {
                            loadError(error.message, error.code)
                        } else {
                            handleRetrofitError(error)
                        }
                    }
                })
        )
    }
}