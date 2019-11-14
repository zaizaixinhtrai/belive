package com.domain.interactors.pointsystem

import com.appster.features.points.MysteryBoxData
import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.repository.PointSystemRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 *  Created by DatTN on 10/24/2018
 */
class GetMysteryBoxesUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler,
                                                 @Named(SchedulerModule.IO) executorThread: Scheduler,
                                                 private val pointSystemRepository: PointSystemRepository) : UseCase<MysteryBoxData, Unit?>(uiThread, executorThread) {

    override fun buildObservable(params: Unit?): Observable<MysteryBoxData> = pointSystemRepository.loadMysteryBoxData()
}