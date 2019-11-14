package com.appster.features.income.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.Bind
import com.appster.R
import com.appster.activity.BaseToolBarActivity
import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.recyclerview.LoadMoreRecyclerView
import com.appster.extensions.then
import com.appster.features.income.history.adapter.TransactionAdapterDelegate
import com.appster.interfaces.OnLoadMoreListenerRecyclerView
import com.apster.common.DialogManager
import dagger.android.AndroidInjection
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TransactionHistoryActivity : BaseToolBarActivity(), TransactionHistoryContract.View, OnLoadMoreListenerRecyclerView {

    @Inject
    lateinit var mPresenter: TransactionHistoryContract.UserActions

    lateinit var mTransactionAdapterDelegate: TransactionAdapterDelegate
    @Bind(R.id.tvUserTotalCash)
    lateinit var tvUserTotalCash: TextView
    @Bind(R.id.rcvTransactionHistory)
    lateinit var rcvTransactionHistory: LoadMoreRecyclerView
    @Bind(R.id.no_data)
    lateinit var noData: TextView
    internal var isEndedList = false

    //region-------activity life cycle-------
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        mPresenter.let {
            it.getCumulativeCashoutAmount()
            it.getHistoryTransactions()
        }
    }

    override fun onResume() {
        super.onResume()
        handleTurnoffMenuSliding()
        setTopBarTile(getString(R.string.transaction_hitory))
        useAppToolbarBackButton()
        eventClickBack?.setOnClickListener { onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun getLayoutContentId(): Int {
        return R.layout.transaction_history_layout
    }

    override fun init() {
        mTransactionAdapterDelegate = TransactionAdapterDelegate(null, ArrayList())
        rcvTransactionHistory.let {
            it.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//            val divider = DividerItemDecoration(it.context, DividerItemDecoration.VERTICAL)
//            divider.setDrawable(ContextCompat.getDrawable(baseContext, R.drawable.transaction_history_divider)!!)
//            it.addItemDecoration(divider)
            it.adapter = mTransactionAdapterDelegate
            it.setOnLoadMoreListener(this)
            mTransactionAdapterDelegate.addLoadMoreItem()
        }

    }


    //endregion -------activity life cycle-------

    //region -------inheritance methods-------
    //endregion -------inheritance methods-------

    //region -------implement methods-------
    override fun getViewContext(): Context? {
        return this
    }

    override fun loadError(errorMessage: String, code: Int) {
        handleError(errorMessage, code)
        onShowNothingTransaction(true)
    }

    override fun showProgress() {
        DialogManager.getInstance().showDialog(this, getString(R.string.connecting_msg))
    }

    override fun hideProgress() {
        DialogManager.getInstance().dismisDialog()
    }

    override fun onTransactionHistoryReceived(transactions: List<DisplayableItem>, isEndedList: Boolean) {
        this.isEndedList = isEndedList
        mTransactionAdapterDelegate.removeLoadingItem()
        rcvTransactionHistory.visibility = View.VISIBLE
        mTransactionAdapterDelegate.updateItems(transactions)
        onShowNothingTransaction(mTransactionAdapterDelegate.itemCount == 0)
    }

    override fun onCumulativeCashoutAmoutReceived(cumulativeAmount: String) {
        Timber.e("cumulativeAmount %s", cumulativeAmount)
        tvUserTotalCash.text = cumulativeAmount
    }

    override fun onShowNothingTransaction(isShowing: Boolean) {
        noData.visibility = isShowing then View.VISIBLE ?: View.GONE
    }

    override fun onLoadMore() {
        if (!isEndedList) {
            rcvTransactionHistory.let {
                it.post { mTransactionAdapterDelegate.addLoadMoreItem() }
                it.postDelayed({
                    if (!isFinishing && !isDestroyed) {
                        mPresenter.getHistoryTransactions()
                    }
                }, 2000)
            }

        }
    }

    companion object {

        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, TransactionHistoryActivity::class.java)
        }
    }
    //endregion -------implement methods-------

    //region -------inner methods-------
    //endregion -------inner methods-------


    //region -------inner class-------

    //endregion -------inner class-------
}
