package com.domain.interactors.pointsystem

import com.data.di.SchedulerModule
import com.data.entity.requests.SubmitRedemptionEntity
import com.domain.interactors.UseCase
import com.domain.repository.PointSystemRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

class SubmitRedemptionUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler,
                                                  @Named(SchedulerModule.IO) executorThread: Scheduler,
                                                  private val repo: PointSystemRepository) : UseCase<Boolean, SubmitRedemptionUseCase.Params>(uiThread, executorThread) {

    override fun buildObservable(params: Params): Observable<Boolean> = repo.submitRedemption(params.bagItemId, params.name, params.email)

    class Params private constructor(internal val bagItemId: Int, internal val name: String, internal val email: String) {
        companion object {
            @JvmStatic
            fun load(bagItemId: Int, name: String, email: String): Params {
                return Params(bagItemId, name, email)
            }
        }
    }
}