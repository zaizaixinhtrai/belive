package com.appster.features.prizeBag

import com.appster.core.adapter.DisplayableItem
import com.appster.features.mvpbase.BaseContract

interface PrizeBagContract {
    interface PrizeBagView : BaseContract.View {
        fun visibleListPrizeBag(listItems: List<DisplayableItem>)
        fun visibleMessageSubmitRedemption(title: String, message: CharSequence)
    }

    interface PrizeBagActions : BaseContract.Presenter<PrizeBagContract.PrizeBagView> {
        fun getListPrizeBag(isShowDialog: Boolean)
        fun submitRedemption(amount: Int, prizeItemType: Int, item: PrizeBagViewModel)
    }
}