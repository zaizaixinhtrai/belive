package com.appster.features.prizeBag.redemption

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.appster.AppsterApplication
import com.appster.R
import com.appster.activity.BaseToolBarActivity
import com.appster.extensions.then
import com.appster.features.prizeBag.PrizeBagViewModel
import com.jakewharton.rxbinding.widget.RxTextView
import com.pack.utility.EmailUtil
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.redeemption_activity.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RedemptionActivity : BaseToolBarActivity(), RedemptionContract.RedemptionView {


    private var bagItemId: Int? = 0
    @Inject
    internal lateinit var presenter: RedemptionContract.RedemptionActions

    companion object {
        const val MESSAGE_RETURN = "message_return"
        const val TITLE_RETURN = "title_return"
        const val BAG_ITEM_ID = "bagItemId"
        @JvmStatic
        fun createIntent(context: Context, bagItemId: Int): Intent {
            val intent = Intent(context, RedemptionActivity::class.java)
            intent.putExtra(BAG_ITEM_ID, bagItemId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        handleTurnoffMenuSliding()
        setTopBarTile(getString(R.string.redemption_title))
        useAppToolbarBackButton()
        eventClickBack.setOnClickListener { onBackPressed() }
        bagItemId = intent?.getIntExtra(BAG_ITEM_ID, 0)
    }

    override fun getLayoutContentId(): Int {
        return R.layout.redeemption_activity
    }

    override fun init() {
        setToolbarColor("#EEEEEE")
        setEdtUserIdWatcher(etEmail)
        setEdtUserIdWatcher(etUserName)
        cbConfirm.setOnCheckedChangeListener { _, _ -> enableSubmit() }
        etUserId.setText(AppsterApplication.mAppPreferences.userModel.userName)
        btSubmit.setOnClickListener { submit() }
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun loadError(errorMessage: String?, code: Int) {
        handleError(errorMessage, code)
    }

    override fun showProgress() {
        showDialog(this, getString(R.string.connecting_msg))
    }

    override fun hideProgress() {
        dismisDialog()
    }

    override fun visibleMessageSubmitRedemption(title: String, message: CharSequence) {
        intent.putExtra(MESSAGE_RETURN, message)
        intent.putExtra(TITLE_RETURN, title)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setEdtUserIdWatcher(editText: EditText) {
        mCompositeSubscription.add(RxTextView.textChangeEvents(editText)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { enableSubmit() }
                .observeOn(Schedulers.computation())
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }, { t: Throwable? -> Timber.e(t, "Failed to") }))
    }

    private fun submit() {
        val returnModel = PrizeBagViewModel(bagItemId!!, etUserName.text.toString(), etEmail.text.toString(), "", "", "")
//        intent.putExtra(DATA_RETURN, returnModel)
//        setResult(Activity.RESULT_OK, intent)
//        finish()

        presenter.submitRedemption(0, 3, returnModel)
    }

    private fun enableSubmit() {

        val isAbleSubmit = EmailUtil.isEmail(etEmail.text.toString()) && etUserName.text.toString().trim().isNotEmpty() && cbConfirm.isChecked
        btSubmit.isEnabled = isAbleSubmit

        val background = btSubmit.background
        if (background is GradientDrawable) {
            @ColorRes val color = isAbleSubmit then R.color.color_ff5167
                    ?: R.color.color_d8d8d8
            background.setColor(ContextCompat.getColor(applicationContext, color))
        }
    }
}