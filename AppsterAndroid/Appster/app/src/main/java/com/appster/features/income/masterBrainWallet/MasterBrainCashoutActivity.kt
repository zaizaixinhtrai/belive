package com.appster.features.income.masterBrainWallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.core.app.ActivityOptionsCompat
import com.appster.R
import com.appster.activity.BaseActivity
import com.appster.extensions.cleanCurrencyValue
import com.appster.features.income.history.TransactionHistoryActivity
import com.appster.utility.CustomTabUtils
import com.apster.common.DialogManager
import com.domain.models.LiveShowWalletModel
import com.pack.utility.StringUtil
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.master_brain_cash_out_activity.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * Created by Ngoc on 6/22/2018.
 */
class MasterBrainCashoutActivity : BaseActivity(), MasterBrainCashoutContract.View {

    private var liveShowWalletModel: LiveShowWalletModel? = null
    @Inject
    lateinit var presenter: MasterBrainCashoutContract.UserActions
    private val isCalledGoogleForm: AtomicBoolean by lazy { AtomicBoolean(false) }

    companion object {
        @JvmStatic
        fun createIntent(context: Context, walletGroup: Int): Intent {
            return Intent(context, MasterBrainCashoutActivity::class.java).apply {
                putExtra(ARG_WALLET_GROUP_OPTION, walletGroup)
            }
        }

        private const val ARG_WALLET_GROUP_OPTION = "a_wgo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.master_brain_cash_out_activity)
        init()
    }

    override fun onResume() {
        super.onResume()
        tvCash.text = ""
        presenter.getLiveShowWallet(isCalledGoogleForm.get(), intent.getIntExtra(ARG_WALLET_GROUP_OPTION, 0))
        isCalledGoogleForm.set(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showUserWallet(model: LiveShowWalletModel) {
        liveShowWalletModel = model
        checkAbleCashOut()
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun loadError(errorMessage: String?, code: Int) {
        handleError(errorMessage, code)
        tvCash.text = String.format("S$%s", 0)
    }

    override fun showProgress() {
        DialogManager.getInstance().showDialog(this, getString(R.string.connecting_msg))
    }

    override fun hideProgress() {
        DialogManager.getInstance().dismisDialog()
    }

    fun init() {
        imvClose.setOnClickListener { finish() }
        imvHistory.setOnClickListener { navigationTransactionHistory() }
        btCashout.setOnClickListener { cashOut() }
    }

    private fun checkAbleCashOut() {
        liveShowWalletModel?.apply {
            if (withDrawable) {
//                btCashout.loadImg(R.drawable.cash_out_enabled)
                btCashout.setImageResource(R.drawable.cash_out_enabled)
            } else {
//                btCashout.loadImg(R.drawable.cash_out_disabled)
                btCashout.setImageResource(R.drawable.cash_out_disabled)
                message.isNotEmpty().apply { tvMessage.text = message }
            }
            tvCash.text = StringUtil.replaceCurrencyString(tvCash.cleanCurrencyValue(amount))
        }
    }

    private fun cashOut() {
        liveShowWalletModel?.apply {
            if (withDrawable) {
                openGoogleCashUutForm(cashoutUrl)
                isCalledGoogleForm.set(true)
            }
        }
    }

    private fun openGoogleCashUutForm(paymentUrl: String?) {
        if (TextUtils.isEmpty(paymentUrl)) return
        CustomTabUtils.openChromeTab(this, paymentUrl)
    }

    private fun navigationTransactionHistory() {
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left)
        startActivity(TransactionHistoryActivity.createIntent(this), options.toBundle())
    }
}