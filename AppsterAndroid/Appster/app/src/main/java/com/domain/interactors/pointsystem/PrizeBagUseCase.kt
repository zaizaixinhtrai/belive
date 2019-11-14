package com.domain.interactors.pointsystem

import com.appster.core.adapter.DisplayableItem
import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.repository.PointSystemRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

class PrizeBagUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler,
                                          @Named(SchedulerModule.IO) executorThread: Scheduler,
                                          private val repo: PointSystemRepository) : UseCase<List<DisplayableItem>, Unit>(uiThread, executorThread) {
    override fun buildObservable(mysteryBoxId: Unit): Observable<List<DisplayableItem>> = repo.loadPrizeBagList()
}