package com.domain.interactors.pointsystem

import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.models.EarnPointsModel
import com.domain.repository.PointSystemRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

class EarnPointsUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler,
                                            @Named(SchedulerModule.IO) executorThread: Scheduler,
                                            private val repo: PointSystemRepository) : UseCase<EarnPointsModel, EarnPointsUseCase.Params>(uiThread, executorThread) {

    override fun buildObservable(params: Params): Observable<EarnPointsModel> = repo.earnPoints(params.actionType, params.slug, params.mode)

    class Params private constructor(internal val actionType: String, internal val slug: String, internal val mode: Int) {
        companion object {
            @JvmStatic
            fun load(actionType: String, slug: String, mode: Int): Params {
                return Params(actionType, slug, mode)
            }
        }
    }
}