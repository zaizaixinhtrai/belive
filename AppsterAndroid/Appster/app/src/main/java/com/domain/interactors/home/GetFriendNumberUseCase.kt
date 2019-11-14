package com.domain.interactors.home

import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.models.LiveShowFriendNumberModel
import com.domain.repository.LiveShowRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

class GetFriendNumberUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler, private val dataRepository: LiveShowRepository)
    : UseCase<LiveShowFriendNumberModel, GetFriendNumberUseCase.Params>(uiThread, executorThread) {
    override fun buildObservable(params: Params): Observable<LiveShowFriendNumberModel> {
        return dataRepository.getFriendNumber(params.showId)
    }

    class Params private constructor(internal val showId: Int) {
        companion object {
            @JvmStatic
            fun get(showId: Int): Params {
                return Params(showId)
            }
        }
    }
}