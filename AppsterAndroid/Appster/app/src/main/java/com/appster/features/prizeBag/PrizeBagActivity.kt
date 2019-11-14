package com.appster.features.prizeBag

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.appster.R
import com.appster.activity.BaseToolBarActivity
import com.appster.core.adapter.DisplayableItem
import com.appster.extensions.then
import com.appster.features.prizeBag.redemption.RedemptionActivity
import com.appster.features.prizeBag.viewholders.PrizeBagViewHolder
import com.appster.webview.ActivityViewWeb
import com.apster.common.*
import com.domain.models.PrizeBagModel
import com.pack.utility.StringUtil
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_prize_bag.*
import javax.inject.Inject

class PrizeBagActivity : BaseToolBarActivity(), PrizeBagViewHolder.OnClickListener, PrizeBagContract.PrizeBagView {


    private var prizeBagAdapter: PrizeBagAdapter? = null
    @Inject
    internal lateinit var presenter: PrizeBagContract.PrizeBagActions

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, PrizeBagActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        handleTurnoffMenuSliding()
        setTopBarTile(getString(R.string.prize_bag_title))
        useAppToolbarBackButton()
        eventClickBack.setOnClickListener { onBackPressed() }
        presenter.getListPrizeBag(true)
    }

    override fun getLayoutContentId(): Int {
        return R.layout.activity_prize_bag
    }

    override fun init() {
        initRecyclerView()
        setToolbarColor("#EEEEEE")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == Constants.REQUEST_REDEMPTION) {
            val extras = data?.extras
//            val returnModel = extras?.getParcelable<PrizeBagViewModel>(RedemptionActivity.DATA_RETURN)
//            returnModel?.let { presenter.submitRedemption(0, 3, it) }

            val message = extras?.getCharSequence(RedemptionActivity.MESSAGE_RETURN)
            val title = extras?.getString(RedemptionActivity.TITLE_RETURN)
            if (!message.isNullOrEmpty() && !title.isNullOrEmpty()) visibleMessageSubmitRedemption(title!!, message!!)
        }
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun loadError(errorMessage: String?, code: Int) {
    }

    override fun showProgress() {
        showDialog(this, getString(R.string.connecting_msg))
    }

    override fun hideProgress() {
        dismisDialog()
    }

    override fun visibleMessageSubmitRedemption(title: String, message: CharSequence) {
        presenter.getListPrizeBag(false)
        val builder = DialogbeLiveConfirmation.Builder()
        val confirmation = DialogbeLiveConfirmation(builder)
        builder.title(title)
                .message(message)
                .singleAction(true)
                .build().show(this)
    }

    override fun onRedeemClick(item: PrizeBagModel) {

        if (item.prizeItem?.type == 3) {
            val builder = DialogbeLiveConfirmation.Builder()
            val confirmation = DialogbeLiveConfirmation(builder)
            builder.title(getString(R.string.prize_congrats_message_title))
                    .message(getString(R.string.prize_congrats_message))
                    .confirmText(getString(R.string.prize_redeem_now_text))
                    .cancelText(getString(R.string.prize_redeem_later_text))
                    .singleAction(false)
                    .onConfirmClicked {
                        intent = RedemptionActivity.createIntent(this, item.id)
                        startActivityForResult(intent, Constants.REQUEST_REDEMPTION)
                    }
                    .build().show(this)
        } else if (item.prizeItem?.type == 0 || item.prizeItem?.type == 1 || item.prizeItem?.type == 2) {
            presenter.submitRedemption(item.prizeItem.amount, item.prizeItem.type, PrizeBagViewModel(item.id, "", "", item.prizeItem.image, "", ""))
        }
    }

    override fun onCheckClick(item: PrizeBagModel) {
        val dialog = PrizeBagViewDialog.newInstance(PrizeBagViewModel(item.id, item.name, item.email, item.prizeItem?.image, item.prizeItem?.name, item.prizeItem?.title))
        dialog.show(supportFragmentManager, PrizeBagViewDialog::class.java.name)
    }

    override fun onNextClick(item: PrizeBagModel) {
        val builder = DialogbeLiveConfirmation.Builder()
        val confirmation = DialogbeLiveConfirmation(builder)
        builder.title(getString(R.string.app_name))
                .message(getString(R.string.your_prize_has_been_sent_out))
                .singleAction(true)
                .build().show(this)
    }


    override fun onItemClick(item: PrizeBagModel) {
        if (item.prizeItem?.type == 3 && !item.prizeItem.infoUrl.isNullOrEmpty()) {
            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left)
            val intent = ActivityViewWeb.createIntent(this, item.prizeItem.infoUrl!!, true)
            ActivityCompat.startActivity(this, intent, options.toBundle())
        }
    }

    override fun onRejectedClick(item: PrizeBagModel) {
        val builder = DialogbeLiveConfirmation.Builder()
        val confirmation = DialogbeLiveConfirmation(builder)
        builder.title(getString(R.string.app_name))
                .message(StringUtil.fromHtml(getString(R.string.your_prize_your_item_has_been_rejected) + " <u>" + getString(R.string.hello_email) + "</u>"))
                .singleAction(true)
                .build().show(this)
    }

    override fun visibleListPrizeBag(listItems: List<DisplayableItem>) {
        prizeBagAdapter?.updateItems(listItems)
        checkNullItems()
    }

    private fun initRecyclerView() {
        prizeBagAdapter = PrizeBagAdapter(DiffCallBaseUtils(), ArrayList(), this)
        recyListPrize.adapter = prizeBagAdapter
        recyListPrize.layoutManager = GridLayoutManager(this, 2)
        recyListPrize.addItemDecoration(UiUtils.GridSpacingItemDecoration(2, PixelUtil.dpToPx(this, 15), true))
        checkNullItems()
    }

    private fun checkNullItems() {
        prizeBagAdapter?.apply {
            recyListPrize.visibility = items.isEmpty() then View.INVISIBLE ?: View.VISIBLE
            tvNoData.visibility = items.isEmpty() then View.VISIBLE ?: View.INVISIBLE
        }
    }
}