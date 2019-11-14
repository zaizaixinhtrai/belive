package com.domain.interactors.pointsystem

import com.appster.features.points.Prize
import com.appster.webservice.response.BaseResponse
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
class GetPrizeListUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler,
                                              @Named(SchedulerModule.IO) executorThread: Scheduler,
                                              private val repo: PointSystemRepository) : UseCase<List<Prize>, GetPrizeListUseCase.Param>(uiThread, executorThread) {

    override fun buildObservable(param: GetPrizeListUseCase.Param): Observable<List<Prize>> = repo.loadPrizeList(param.boxType, param.boxId)

    class Param private constructor(internal val boxType: Int, internal val boxId: Int) {
        companion object {
            @JvmStatic
            fun createParam(boxType: Int, boxId: Int): Param {
                return Param(boxType, boxId)
            }
        }
    }
}