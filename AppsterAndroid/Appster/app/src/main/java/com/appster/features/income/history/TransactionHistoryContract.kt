package com.appster.features.income.history

import com.appster.core.adapter.DisplayableItem
import com.appster.features.mvpbase.BaseContract

interface TransactionHistoryContract {

    interface View : BaseContract.View {
        fun onTransactionHistoryReceived(transactions: List<DisplayableItem>, isEndedList: Boolean)
        fun onCumulativeCashoutAmoutReceived(cumulativeAmount: String)
        fun onShowNothingTransaction(isShowing: Boolean)
    }

    interface UserActions : BaseContract.Presenter<TransactionHistoryContract.View> {
        fun getHistoryTransactions()
        fun getCumulativeCashoutAmount()
    }
}
