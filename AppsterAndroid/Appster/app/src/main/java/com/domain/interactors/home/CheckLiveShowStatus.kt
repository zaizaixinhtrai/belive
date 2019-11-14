package com.domain.interactors.home

import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.models.LiveShowStatus
import com.domain.repository.LiveShowRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by thanhbc on 5/22/18.
 */
class CheckLiveShowStatus @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler, private val dataRepository: LiveShowRepository)
    : UseCase<LiveShowStatus, CheckLiveShowStatus.Params>(uiThread, executorThread) {
    override fun buildObservable(params: Params?): Observable<LiveShowStatus> {
        return dataRepository.checkShows(params?.showId!!)
    }

    class Params private constructor(internal val showId: Int) {
        companion object {
            @JvmStatic
            fun check(showId: Int): Params {
                return Params(showId)
            }
        }
    }
}