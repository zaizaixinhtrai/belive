package com.domain.interactors.main

import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.repository.MainRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Ngoc on 5/17/2018.
 */
class CheckHasLiveVideoUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler, private val mainRepository: MainRepository)
    : UseCase<Boolean, String>(uiThread, executorThread) {
    override fun buildObservable(params: String): Observable<Boolean> =mainRepository.checkHasLiveVideo()
}