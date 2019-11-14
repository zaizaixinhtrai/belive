package com.domain.interactors.pointsystem

import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.repository.PointSystemRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 *  Created by DatTN on 10/29/2018
 */
class GetDailyBonusCountDownUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler,
                                                        @Named(SchedulerModule.IO) executorThread: Scheduler,
                                                        private val repo: PointSystemRepository) : UseCase<Int, Unit?>(uiThread, executorThread) {

    override fun buildObservable(param: Unit?): Observable<Int> = repo.loadDailyBonusCountDown()
}