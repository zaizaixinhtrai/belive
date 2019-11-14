package com.domain.interactors.explore

import com.appster.core.adapter.DisplayableItem
import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.models.BasePagingModel
import com.domain.repository.ExploreStreamRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by thanhbc on 6/13/18.
 */
class GetExploreStreamUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler, val exploreStreamRepository: ExploreStreamRepository)
    : UseCase<BasePagingModel<DisplayableItem>, GetExploreStreamUseCase.Params>(uiThread, executorThread) {
    override fun buildObservable(params: GetExploreStreamUseCase.Params):
            Observable<BasePagingModel<DisplayableItem>> = exploreStreamRepository.getStreams(params.pageId)

    class Params(var pageId: Int = 0) {
        companion object {
            @JvmStatic
            fun loadPage(pageId: Int): Params {
                return Params(pageId)
            }
        }
    }
}