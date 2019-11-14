package com.appster.features.prizeBag.redemption

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
import com.appster.features.prizeBag.PrizeBagViewModel
import com.apster.common.Constants
import com.apster.common.Utils
import com.data.exceptions.BeLiveServerException
import com.domain.interactors.pointsystem.SubmitRedemptionUseCase
import com.pack.utility.StringUtil
import timber.log.Timber
import javax.inject.Inject

@ActivityScope
class RedemptionPresenter @Inject constructor(private val redemptionView: RedemptionContract.RedemptionView,
                                              private val submitRedemptionUseCase: SubmitRedemptionUseCase)
    : BasePresenter<RedemptionContract.RedemptionView>(), RedemptionContract.RedemptionActions {

    override fun submitRedemption(amount: Int, prizeItemType: Int, item: PrizeBagViewModel) {
        redemptionView.showProgress()
        addSubscription(submitRedemptionUseCase.execute(SubmitRedemptionUseCase.Params.load(item.bagItemId, item.name.toString(), item.email.toString())).subscribe({ dataResponse ->
            dataResponse?.let {
                redemptionView.visibleMessageSubmitRedemption((prizeItemType == 3)
                then redemptionView.viewContext.getString(R.string.prize_bag_view_form_received_title)
                        ?: redemptionView.viewContext.getString(R.string.prize_bag_view_pick_item_title),
                        (prizeItemType == 3)
                        then StringUtil.fromHtml(redemptionView.viewContext.getString(R.string.prize_bag_view_form_received_message) + " <b> " +
                                redemptionView.viewContext.getString(R.string.prize_bag_seven_woking_day) + " </b>")
                                ?: redemptionMessage(prizeItemType, amount))
            }
            redemptionView.hideProgress()
        }, { error ->
            Timber.e(error.message)
            redemptionView.hideProgress()

            if (error is BeLiveServerException)
                redemptionView.loadError(error.message, error.code)
            else
                redemptionView.loadError(error.message, Constants.RETROFIT_ERROR)
        }))
    }

    private fun redemptionMessage(prizeItemType: Int, amount: Int): CharSequence {
        var typeIcon = BitmapFactory.decodeResource(redemptionView.viewContext.resources, R.drawable.refill_gem_icon)
        if (prizeItemType == 1) {
            typeIcon = BitmapFactory.decodeResource(redemptionView.viewContext.resources, R.drawable.icon_gift_price)
        } else if (prizeItemType == 2) {
            typeIcon = BitmapFactory.decodeResource(redemptionView.viewContext.resources, R.drawable.ic_profile_bar_gift_selected)
        }
        val builder = SpannableStringBuilder()

        builder.append(amount.toString())
                .append(" ")
                .append(StringUtil.formatImage(redemptionView.viewContext, "img", Bitmap.createScaledBitmap(typeIcon, Utils.dpToPx(14f), Utils.dpToPx(14f), false)))
                .append(" ")
                .append(redemptionView.viewContext.getString(R.string.prize_bag_redeem_have_been_created))
                .setSpan(StyleSpan(Typeface.BOLD), 0, amount.toString().length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return builder
    }

}