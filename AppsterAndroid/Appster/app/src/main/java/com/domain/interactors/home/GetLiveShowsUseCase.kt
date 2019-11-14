package com.domain.interactors.home

import com.appster.core.adapter.DisplayableItem
import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.repository.LiveShowRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by thanhbc on 5/18/18.
 */
class GetLiveShowsUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler, private val dataRepository: LiveShowRepository) : UseCase<List<DisplayableItem>, Unit>(uiThread, executorThread){
    override fun buildObservable(params: Unit?): Observable<List<DisplayableItem>> {
        return dataRepository.getLiveShows()
    }

}