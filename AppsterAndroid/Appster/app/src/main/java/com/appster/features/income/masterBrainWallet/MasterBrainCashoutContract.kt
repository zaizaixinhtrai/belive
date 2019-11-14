package com.appster.features.income.masterBrainWallet

import com.appster.features.mvpbase.BaseContract
import com.domain.models.LiveShowWalletModel

/**
 * Created by Ngoc on 6/25/2018.
 */
interface MasterBrainCashoutContract {
    interface View : BaseContract.View {
        fun showUserWallet(model: LiveShowWalletModel)
    }

    interface UserActions : BaseContract.Presenter<MasterBrainCashoutContract.View> {
        fun getLiveShowWallet(needDelay: Boolean, walletGroup: Int)
    }
}