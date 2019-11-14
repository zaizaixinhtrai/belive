package com.appster.features.income.masterBrainWallet

import com.appster.features.mvpbase.BasePresenter
import com.apster.common.Constants
import com.data.exceptions.BeLiveServerException
import com.domain.interactors.transaction_history.LiveShowWalletUseCase
import com.domain.models.LiveShowWalletModel
import rx.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Ngoc on 6/25/2018.
 */
class MasterBrainCashoutPresenter @Inject constructor(private val liveShowWalletUseCase: LiveShowWalletUseCase, cashOutView: MasterBrainCashoutContract.View) : BasePresenter<MasterBrainCashoutContract.View>(), MasterBrainCashoutContract.UserActions {

    init {
        attachView(cashOutView)
    }

    private fun executeApiLiveShowWallet(walletGroup: Int): Observable<LiveShowWalletModel> = liveShowWalletUseCase.execute(LiveShowWalletUseCase.Params(walletGroup))

    override fun getLiveShowWallet(needDelay: Boolean, walletGroup: Int) {

        view?.showProgress()
        if (needDelay) {
            addSubscription(Observable
                    .timer(3000, TimeUnit.MILLISECONDS)
                    .flatMap { executeApiLiveShowWallet(walletGroup) }
                    .subscribe({ t ->
                        view?.run {
                            showUserWallet(t)
                            hideProgress()
                        }
                    }, {
                        this.handleError(it)
                    }))
        } else {
            addSubscription(executeApiLiveShowWallet(walletGroup).subscribe({ t ->
                view?.run {
                    showUserWallet(t)
                    hideProgress()
                }
            }, {
                this.handleError(it)
            }))
        }
    }

    fun handleError(error: Throwable) {
        view?.apply {
            hideProgress()
            var errorCode = Constants.RETROFIT_ERROR
            if (error is BeLiveServerException) {
                errorCode = error.code
            }
            loadError(error.message, errorCode)
        }
    }
}