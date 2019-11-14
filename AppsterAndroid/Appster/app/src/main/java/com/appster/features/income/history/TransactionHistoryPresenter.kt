package com.appster.features.income.history

import com.appster.base.ActivityScope
import com.appster.core.adapter.DisplayableItem
import com.appster.extensions.cleanCurrencyValue
import com.appster.features.mvpbase.BasePresenter
import com.apster.common.Constants
import com.data.exceptions.BeLiveServerException
import com.domain.interactors.transaction_history.GetTotalCashoutAmountUsecase
import com.domain.interactors.transaction_history.GetTransactionHistoryUsecase
import com.pack.utility.StringUtil

import java.util.ArrayList

import javax.inject.Inject

import timber.log.Timber

@ActivityScope
class TransactionHistoryPresenter @Inject constructor(view: TransactionHistoryContract.View,
                                                      private val mCashoutAmountUsecase: GetTotalCashoutAmountUsecase,
                                                      private val mTransactionHistoryUsecase: GetTransactionHistoryUsecase) : BasePresenter<TransactionHistoryContract.View>(), TransactionHistoryContract.UserActions {
    private var mNextPageId = 0
    private val mTransactionItems = ArrayList<DisplayableItem>()

    init {
        attachView(view)
    }

    override fun getHistoryTransactions() {
        checkViewAttached()
        addSubscription(mTransactionHistoryUsecase.execute(GetTransactionHistoryUsecase.Params.loadPage(0, mNextPageId))
                .filter { displayableItemBasePagingModel -> displayableItemBasePagingModel != null }
                .doOnNext { displayableItemBasePagingModel -> mNextPageId = displayableItemBasePagingModel.nextId }
                .subscribe({ transactionHistoryModelBasePagingModel ->
                    view?.apply {
                        mTransactionItems.addAll(transactionHistoryModelBasePagingModel.data)
                        onTransactionHistoryReceived(mTransactionItems, transactionHistoryModelBasePagingModel.isEnd)
                    }
                }, { error ->
                    Timber.e("error=%s", error.message)
                    view?.apply {
                        var errorCode = Constants.RETROFIT_ERROR
                        if (error is BeLiveServerException) {
                            errorCode = error.code
                        }
                        loadError(error.message, errorCode)
                    }
                }))
    }

    override fun getCumulativeCashoutAmount() {
        checkViewAttached()
        addSubscription(mCashoutAmountUsecase.execute("")
                .filter { displayableItemBasePagingModel -> displayableItemBasePagingModel != null }
                .subscribe({ totalCashoutModel ->
                    view?.apply {
                        onCumulativeCashoutAmoutReceived(StringUtil.replaceCurrencyString(cleanCurrencyValue(totalCashoutModel.totalCashout)))
                    }
                }, { error ->
                    view?.apply { handleRetrofitError(error) }
                }))
    }

}