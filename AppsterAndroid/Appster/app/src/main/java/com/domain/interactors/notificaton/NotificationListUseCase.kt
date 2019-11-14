package com.domain.interactors.notificaton

import com.appster.models.NotificationItemModel
import com.appster.webservice.response.BaseDataPagingResponseModel
import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.repository.NotificationRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

class NotificationListUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler,
                                                  @Named(SchedulerModule.IO) executorThread: Scheduler,
                                                  private val repo: NotificationRepository) : UseCase<BaseDataPagingResponseModel<NotificationItemModel>, NotificationListUseCase.Params>(uiThread, executorThread) {
    override fun buildObservable(params: Params): Observable<BaseDataPagingResponseModel<NotificationItemModel>> = repo.getNotificationList(params.notificationStatus, params.nextIndex, params.pageLimit)

    class Params private constructor(internal val notificationStatus: Int, internal val nextIndex: Int, internal val pageLimit: Int) {
        companion object {
            @JvmStatic
            fun load(notificationStatus: Int, nextIndex: Int, pageLimit: Int): Params {
                return Params(notificationStatus, nextIndex, pageLimit)
            }
        }
    }
}