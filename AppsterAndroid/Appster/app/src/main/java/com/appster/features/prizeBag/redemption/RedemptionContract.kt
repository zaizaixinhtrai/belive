package com.appster.features.prizeBag.redemption

import com.appster.features.mvpbase.BaseContract
import com.appster.features.prizeBag.PrizeBagViewModel

interface RedemptionContract{
    interface RedemptionView : BaseContract.View {
        fun visibleMessageSubmitRedemption(title: String, message: CharSequence)
    }

    interface RedemptionActions : BaseContract.Presenter<RedemptionContract.RedemptionView> {
        fun submitRedemption(amount: Int, prizeItemType: Int, item: PrizeBagViewModel)
    }
}