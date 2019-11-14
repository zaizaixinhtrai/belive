package com.domain.interactors.transaction_history

import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.models.LiveShowWalletModel
import com.domain.repository.TransactionRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Ngoc on 6/25/2018.
 */
class LiveShowWalletUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler,
                                                private val mRepository: TransactionRepository)
    : UseCase<LiveShowWalletModel, LiveShowWalletUseCase.Params>(uiThread, executorThread) {

    override fun buildObservable(params: Params): Observable<LiveShowWalletModel> = mRepository.liveShowWallet(params.walletGroup)

    data class Params(val walletGroup: Int)
}