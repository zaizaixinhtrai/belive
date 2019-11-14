package com.appster.features.income

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.appster.AppsterApplication
import com.appster.R
import com.appster.activity.BaseActivity
import com.appster.dialog.ExchangeRateDialog
import com.appster.features.income.cashout.CashoutActivity
import com.appster.features.income.gem_exchange.StarsToGemActivity
import com.appster.features.income.history.TransactionHistoryActivity
import com.appster.utility.AppsterUtility
import com.appster.webservice.AppsterWebServices
import com.appster.webservice.request_models.CreditsRequestModel
import com.apster.common.Constants
import com.apster.common.DialogManager
import kotlinx.android.synthetic.main.activity_income.*

/**
 * Created by User on 8/19/2016.
 */
class IncomeActivity : BaseActivity() {

    private var mCurrentStars: Long = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income)
        getAppConfigs()
        getCredits()
        iniOnClickView()
    }

    private fun iniOnClickView() {
        imvBack.setOnClickListener { finish() }
        btnStartCash.setOnClickListener { cashOut() }
        btnStartGems.setOnClickListener { convertToGems() }
        btnTransactionHistory.setOnClickListener { onTransactionClicked() }
    }

    private fun getAppConfigs() {
        mCompositeSubscription.add(AppsterWebServices.get().getAppConfigs(AppsterUtility.getAuth())
                .filter { appConfigModelBaseResponse -> appConfigModelBaseResponse.code == Constants.RESPONSE_FROM_WEB_SERVICE_OK && !isFinishing }
                .map { appConfigModelBaseResponse -> appConfigModelBaseResponse.data.enableCashOutAndroid }
                .subscribe({ isShow -> btnStartCash?.visibility = if (isShow) View.VISIBLE else View.GONE })
                { error -> handleError(error.message, Constants.RETROFIT_ERROR) })
    }

    private fun convertStarsToSGD() {
        val d = AppsterApplication.mAppPreferences.userModel.totalGold.toDouble()
        val sdg = d * 1.5 / 1000.0
        txtSgd?.text = String.format(getString(R.string.income_sgd), Math.round(sdg).toString())
    }

    private fun cashOut() {
        if (isMaintenance) return
        if (mCurrentStars == 0L) {
            handleError(getString(R.string.insufficient_stars), 666)
            return
        }
        val options = ActivityOptionsCompat.makeCustomAnimation(this@IncomeActivity, R.anim.push_in_to_right, R.anim.push_in_to_left)
        startActivity(CashoutActivity.createIntent(this), options.toBundle())
        //        Toast.makeText(getApplicationContext(), getString(R.string.feature_will_be_coming_soon), Toast.LENGTH_SHORT).show();
    }

    fun showExchangeRate() {
        val rateDialog = ExchangeRateDialog.getInstance()
        rateDialog.show(this@IncomeActivity, "") { v1 -> rateDialog.dismiss() }
    }

    private fun convertToGems() {
        if (isMaintenance) return
        val options = ActivityOptionsCompat.makeCustomAnimation(this@IncomeActivity, R.anim.push_in_to_right, R.anim.push_in_to_left)
        val intent1 = Intent(this@IncomeActivity, StarsToGemActivity::class.java)
        startActivity(intent1, options.toBundle())
    }

    override fun onResume() {
        super.onResume()
        //        handleTurnoffMenuSliding();
        //        setTopBarTile(getString(R.string.income_menu));
        //        useAppToolbarBackButton(R.drawable.icon_back_btn_white);
        //        getEventClickBack().setOnClickListener(v -> onBackPressed());

        getCredits()
        getAppConfigs()
    }

    private fun onTransactionClicked() {
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left)
        startActivity(TransactionHistoryActivity.createIntent(this), options.toBundle())
    }

    private fun getCredits() {
        DialogManager.getInstance().showDialog(this@IncomeActivity, getString(R.string.connecting_msg))

        val request = CreditsRequestModel()
        mCompositeSubscription.add(AppsterWebServices.get().getUserCredits("Bearer " + AppsterApplication.mAppPreferences.userToken, request)
                .subscribe({ creditsResponseModel ->
                    DialogManager.getInstance().dismisDialog()
                    creditsResponseModel?.apply {
                        if (code == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            val currentStars = data.total_gold
                            AppsterApplication.mAppPreferences.userModel.totalGold = currentStars
                            AppsterApplication.mAppPreferences.userModel.totalBean = data.total_bean

                            currentGold?.text = currentStars.toString()
                            mCurrentStars = currentStars
                            btnStartCash?.background = ContextCompat.getDrawable(this@IncomeActivity, if (mCurrentStars <= 0) R.drawable.income_stars_to_gem_btn_grey else R.drawable.income_stars_to_gem_btn)
                            //convertStarsToSGD()

                        } else {
                            handleError(message, code)
                        }
                    }
                }) { error ->
                    DialogManager.getInstance().dismisDialog()
                    handleError(error.message, Constants.RETROFIT_ERROR)
                })
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, IncomeActivity::class.java)
        }
    }
}
