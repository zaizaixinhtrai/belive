package com.domain.interactors.giftstore

import com.domain.interactors.UseCase
import com.domain.models.GiftStoreModel
import com.domain.repository.GiftStoreRepository
import rx.Observable
import rx.Scheduler

/**
 * Created by thanhbc on 3/27/18.
 */
class GetGiftStoreUseCase(val uiThread: Scheduler, val executorThread: Scheduler,
                          private val giftStoreRepository: GiftStoreRepository) : UseCase<GiftStoreModel, Unit>(uiThread, executorThread) {
    override fun buildObservable(params: Unit?): Observable<GiftStoreModel> {
        return giftStoreRepository.fetchGiftStore()
    }
}