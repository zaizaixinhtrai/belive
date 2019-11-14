package com.domain.interactors.dailybonus

import com.data.di.ApiServiceModule
import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.models.TreatCollectModel
import com.domain.repository.DailyBonusRepository

import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by thanhbc on 11/13/17.
 */

class TreatCollectUseCase @Inject constructor(private val mDailyBonusRepository: DailyBonusRepository,
                                              @Named(SchedulerModule.UI) uiThread: Scheduler,
                                              @Named(SchedulerModule.IO) executorThread: Scheduler) : UseCase<TreatCollectModel, String>(uiThread, executorThread) {

    override fun buildObservable(params: String): Observable<TreatCollectModel> {
        return mDailyBonusRepository.collect()
    }
}
