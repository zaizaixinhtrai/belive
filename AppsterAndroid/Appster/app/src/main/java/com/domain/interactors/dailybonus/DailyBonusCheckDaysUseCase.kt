package com.domain.interactors.dailybonus

import com.domain.interactors.UseCase
import com.domain.models.DailyBonusCheckDaysModel
import com.domain.repository.DailyBonusRepository
import rx.Observable
import rx.Scheduler

/**
 * Created by Ngoc on 6/8/2018.
 */
class DailyBonusCheckDaysUseCase(uiThread: Scheduler,
                                 executorThread: Scheduler,
                                 private val mRepository: DailyBonusRepository)
    : UseCase<DailyBonusCheckDaysModel, String>(uiThread, executorThread) {
    override fun buildObservable(params: String): Observable<DailyBonusCheckDaysModel> = mRepository.checkDays()
}