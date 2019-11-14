package com.appster.features.home.dailybonus.treatmachine

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import butterknife.Bind
import com.appster.R
import com.appster.customview.CustomFontButton
import com.appster.dialog.NoTitleDialogFragment
import com.appster.features.home.dialog.DailyTreatRevealPrizeDialog
import com.appster.utility.RxUtils
import com.appster.webservice.AppsterWebServices
import com.apster.common.DialogManager
import com.domain.models.DailyBonusCheckDaysModel
import com.domain.models.TreatCollectModel
import com.pack.utility.DialogInfoUtility
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by thanhbc on 11/7/17.
 */

class DailyTreatMachineDialog : NoTitleDialogFragment(), DailyTreatContract.View {


    internal val mCompositeSubscription: CompositeSubscription by lazy { CompositeSubscription() }
    internal val mPresenter: DailyTreatContract.UserActions by lazy { DailyTreatPresenter(AppsterWebServices.get()) }
    @Bind(R.id.ivCloseMachine)
    lateinit var ivCloseMachine: ImageView
    @Bind(R.id.btCollect)
    lateinit var btCollect: CustomFontButton
    @Bind(R.id.imBackground)
    lateinit var imBackground: ImageView
    @Bind(R.id.fmCloseMachine)
    lateinit var fmCloseMachine: FrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        mPresenter.attachView(this)
        ivCloseMachine.setOnClickListener { dismiss() }
        btCollect.setOnClickListener({ collectClick() })
        mPresenter.checkDays()
        fmCloseMachine.setOnClickListener { dismiss() }
    }

    private fun collectClick() {
        mCompositeSubscription.add(Observable.defer { Observable.just<Int>(1) }
                .throttleFirst(500L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { isFragmentUIActive }
                .subscribe({ itemPos -> mPresenter.collect() },
                        { error -> Timber.e(error) }))
    }

    override fun getRootLayoutResource(): Int {
        return R.layout.dialog_daily_login
    }

    override fun isDimDialog(): Boolean {
        return true
    }

    override fun dimAmount(): Float {
        return 0.9f
    }

    override fun getWindowAnimation(): Int {
        return R.style.DialogFadeAnimation
    }

    override fun show(manager: androidx.fragment.app.FragmentManager, tag: String) {
        try {
            super.show(manager, tag)
        } catch (e: IllegalStateException) {
            Timber.e(e)
        }

    }

    override fun getViewContext(): Context? {
        return context
    }

    override fun loadError(errorMessage: String, code: Int) {
        context?.let {
            val errorDialog = DialogInfoUtility()
            errorDialog.showMessage(getString(R.string.app_name), errorMessage, context) { _ -> dismiss() }
        }
    }

    override fun showProgress() {
        context?.let { DialogManager.getInstance().showDialog(context, resources.getString(R.string.connecting_msg), false) }
    }

    override fun hideProgress() {
        context?.let {
            DialogManager.getInstance().dismisDialog()
        }
    }

    override fun onTreatCollectResult(collectModel: TreatCollectModel?) {
        hideProgress()
        collectModel?.let {
            val manager = fragmentManager
            val transaction = manager?.beginTransaction()
            transaction?.remove(this@DailyTreatMachineDialog)
            val dialog = DailyTreatRevealPrizeDialog.newInstance(collectModel)
            dialog.show(transaction, DailyTreatRevealPrizeDialog::class.java.name)
        }
    }

    override fun onCheckDayOrderResult(dailyBonusCheckDaysModel: DailyBonusCheckDaysModel?) {
        dailyBonusCheckDaysModel?.let {
            //            imBackground.loadImg(returnDailyBackground(dailyBonusCheckDaysModel.dayType))
            imBackground.setImageResource(returnDailyBackground(dailyBonusCheckDaysModel.dayType))
        }
    }

    private fun returnDailyBackground(dayOder: Int): Int {
        when (dayOder) {
            1 -> return R.drawable.daily_login_day_1
            2 -> return R.drawable.daily_login_day_2
            3 -> return R.drawable.daily_login_day_3
            4 -> return R.drawable.daily_login_day_4
            5 -> return R.drawable.daily_login_day_5
            6 -> return R.drawable.daily_login_day_6
            7 -> return R.drawable.daily_login_day_7
        }
        return 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription)
    }

    companion object {
        @JvmStatic
        fun newInstance(): DailyTreatMachineDialog {
            return DailyTreatMachineDialog()
        }
    }
}
