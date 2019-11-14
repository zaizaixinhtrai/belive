package com.domain.interactors.pointsystem

import com.appster.features.points.Prize
import com.appster.webservice.response.BaseResponse
import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.models.PrizeCollectModel
import com.domain.models.TreatCollectModel
import com.domain.repository.PointSystemRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 *  Created by DatTN on 10/29/2018
 */
class OpenMysteryBoxUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler,
                                                @Named(SchedulerModule.IO) executorThread: Scheduler,
                                                private val repo: PointSystemRepository) : UseCase<PrizeCollectModel, Int>(uiThread, executorThread) {

    override fun buildObservable(boxId: Int): Observable<PrizeCollectModel> = repo.openMysteryBox(boxId)
}