package com.appster.features.prizeBag

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.appster.R
import com.appster.base.ActivityScope
import com.appster.extensions.then
import com.appster.features.mvpbase.BasePresenter
import com.apster.common.Constants
import com.apster.common.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.domain.interactors.pointsystem.PrizeBagUseCase
import com.domain.interactors.pointsystem.SubmitRedemptionUseCase
import com.pack.utility.StringUtil
import com.pack.utility.StringUtil.formatImage
import timber.log.Timber
import javax.inject.Inject

@ActivityScope
class PrizeBagPresenter @Inject constructor(private val prizeBagView: PrizeBagContract.PrizeBagView,
                                            private val prizeBagUseCase: PrizeBagUseCase,
                                            private val submitRedemptionUseCase: SubmitRedemptionUseCase) :
        BasePresenter<PrizeBagContract.PrizeBagView>(), PrizeBagContract.PrizeBagActions {

    override fun attachView(view: PrizeBagContract.PrizeBagView?) {
    }

    override fun detachView() {
    }

    override fun getListPrizeBag(isShowDialog: Boolean) {
        if (isShowDialog) prizeBagView.showProgress()
        addSubscription(prizeBagUseCase.execute(Unit).subscribe({ dataResponse ->
            dataResponse?.let {
                prizeBagView.visibleListPrizeBag(dataResponse)
            }
            if (isShowDialog) prizeBagView.hideProgress()
        }, { error ->
            Timber.e(error.message)
            if (isShowDialog) prizeBagView.hideProgress()
            prizeBagView.loadError(error.message, Constants.RETROFIT_ERROR)
        }))
    }

    override fun submitRedemption(amount: Int, prizeItemType: Int, item: PrizeBagViewModel) {
        prizeBagView.showProgress()
        addSubscription(submitRedemptionUseCase.execute(SubmitRedemptionUseCase.Params.load(item.bagItemId, item.name.toString(), item.email.toString())).subscribe({ dataResponse ->
            dataResponse?.let {
                if (prizeItemType == 2) {
                    handleTypeGift(amount, item.image)
                } else {
                    prizeBagView.visibleMessageSubmitRedemption((prizeItemType == 3)
                    then prizeBagView.viewContext.getString(R.string.prize_bag_view_form_received_title)
                            ?: prizeBagView.viewContext.getString(R.string.prize_bag_view_pick_item_title),
                            (prizeItemType == 3)
                            then StringUtil.fromHtml(prizeBagView.viewContext.getString(R.string.prize_bag_view_form_received_message) + " <b> " +
                                    prizeBagView.viewContext.getString(R.string.prize_bag_seven_woking_day) + " </b>")
                                    ?: redemptionMessage(prizeItemType, amount))
                }
            }
            prizeBagView.hideProgress()
        }, { error ->
            Timber.e(error.message)
            prizeBagView.hideProgress()
            prizeBagView.loadError(error.message, Constants.RETROFIT_ERROR)
        }))
    }

    private fun handleTypeGift(amount: Int, imageGift: String?) {
        val builder = SpannableStringBuilder()
        Glide.with(prizeBagView.viewContext.applicationContext)
                .asBitmap()
                .load(imageGift)
                .into(object : SimpleTarget<Bitmap>(20, 20) {
                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                        builder.append(amount.toString())
                                .append(" ")
                                .append(formatImage(prizeBagView.viewContext, "img", Bitmap.createScaledBitmap(resource, Utils.dpToPx(17f), Utils.dpToPx(17f), false)))
                                .append(" ")
                                .append(prizeBagView.viewContext.getString(R.string.prize_bag_redeem_have_been_created))
                                .setSpan(StyleSpan(Typeface.BOLD), 0, amount.toString().length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        prizeBagView.visibleMessageSubmitRedemption(prizeBagView.viewContext.getString(R.string.prize_bag_view_pick_item_title), builder)
                    }
                })
    }

    private fun redemptionMessage(prizeItemType: Int, amount: Int): CharSequence {
        var typeIcon: Bitmap = BitmapFactory.decodeResource(prizeBagView.viewContext.resources, R.drawable.icon_gift_price_gem)
        val builder = SpannableStringBuilder()

        if (prizeItemType == 1) {
            typeIcon = BitmapFactory.decodeResource(prizeBagView.viewContext.resources, R.drawable.icon_gift_price)
        }

        builder.append(amount.toString())
                .append(" ")
                .append(formatImage(prizeBagView.viewContext, "img", Bitmap.createScaledBitmap(typeIcon, Utils.dpToPx(14f), Utils.dpToPx(14f), false)))
                .append(" ")
                .append(prizeBagView.viewContext.getString(R.string.prize_bag_redeem_have_been_created))
                .setSpan(StyleSpan(Typeface.BOLD), 0, amount.toString().length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return builder

    }
}