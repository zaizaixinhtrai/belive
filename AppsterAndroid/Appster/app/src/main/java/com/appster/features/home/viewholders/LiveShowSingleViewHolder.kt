package com.appster.features.home.viewholders

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.airbnb.lottie.LottieComposition
import com.appster.R
import com.appster.extensions.cleanCurrencyValue
import com.appster.extensions.inflate
import com.appster.extensions.loadImg
import com.appster.extensions.then
import com.appster.features.home.ShowStatus
import com.apster.common.Constants
import com.apster.common.UiUtils
import com.domain.models.Balance
import com.domain.models.LiveShowLastModel
import com.domain.models.LiveShowOption
import com.domain.models.StampBalance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pack.utility.StringUtil
import kotlinx.android.synthetic.main.live_balance_item.view.*
import kotlinx.android.synthetic.main.live_option_item.view.*
import kotlinx.android.synthetic.main.live_show_item_single.view.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by thanhbc on 5/22/18.
 */
class LiveShowSingleViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    var showItem: LiveShowLastModel? = null

    companion object {
        private const val STARTING = "starting.json"
        private const val STARTING_VI = "starting_vi.json"
        private const val PLAYNOW = "playnow.json"
        private const val PLAYNOW_VI = "playnow_vi.json"
        private const val WATCHNOW = "watchnow.json"
        private const val WATCHNOW_VI = "watchnow_vi.json"
        @JvmStatic
        fun create(parent: ViewGroup, @LayoutRes layout: Int): LiveShowSingleViewHolder {
            return LiveShowSingleViewHolder(parent.inflate(layout))
        }
    }

    fun bindTo(item: LiveShowLastModel, listener: OnClickListener?) {
        with(itemView) {
            llOptionContainer.removeAllViews()
            tvShowDesc.text = item.showDesc
            tvShowTitle.text = item.showTitle
            ivShowImg.loadImg(item.showImage, (item.showType == 1)
            then R.drawable.masterbrain_default
                    ?: R.drawable.user_image_default)
            showItem = item

            item.balance?.run {
                val view = llOptionContainer.inflate(R.layout.live_balance_item)
                view.tvBalanceValue.text = StringUtil.replaceCurrencyString(itemView.cleanCurrencyValue(amount))
                view.tag = this
                view.setOnClickListener { listener?.onBalanceClicked(it.tag as Balance) }

                llOptionContainer.addView(view)
            }

            if (item.isOgx) {
                item.stampBalance?.run {
                    val view = llOptionContainer.inflate(R.layout.live_balance_item)
                    view.setBackgroundResource(R.drawable.icon_stamps_bal)
                    view.tvBalanceValue.text = StringUtil.replaceCurrencyString(removeDost(amount))
                    view.tag = this
                    view.setOnClickListener { listener?.onStampBalanceClicked(it.tag as StampBalance) }

                    llOptionContainer.addView(view)
                }
            }

            item.options?.forEach {
                val view = llOptionContainer.inflate(R.layout.live_option_item)
                val imgUrl = it.actionImg
                if (imgUrl is String) view.ivOption.loadImg(imgUrl)
                if (imgUrl is Int) view.ivOption.loadImg(imgUrl)
                if (it.optionType == 3 && it.params.isNotEmpty()) {
                    it.params.apply {
                        val type = object : TypeToken<Map<String, String>>() {}.type
                        val myMap = Gson().fromJson(this, type) as Map<String, String>
                        with(view.tvOptionValue) {
                            text = myMap["ReviveCount"]
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        }
                    }
                }
                view.tag = it
                view.setOnClickListener {
                    val opt = it.tag as LiveShowOption
                    if (opt.optionType == 4) {
                        listener?.onFollowClicked(item)
                    } else {
                        listener?.onOptionClicked(it.tag as LiveShowOption)
                    }
                }
                llOptionContainer.addView(view)
            }

            when (item.showStatus) {
                ShowStatus.WAITING -> {
                    Timber.e("check status in ${item.waitingTime}")
                    if (lavShowStatus.isAnimating) {
                        lavShowStatus.pauseAnimation()
                    }
                    if (item.showDateTime != 0L) {
                        try {
                            val dateFormat = SimpleDateFormat("h:mm a", Locale.US)
                            val dtUTC = DateTime(item.showDateTime * 1000L, DateTimeZone.UTC)
                            dateFormat.timeZone = TimeZone.getDefault()

                            Timber.e("final Time ${dateFormat.format(dtUTC.toDate())}")
                            tvShowTime.text = dateFormat.format(dtUTC.toDate())
                        } catch (e: Exception) {
                            Timber.e(e)
                        }

                        tvShowTime.visibility = View.VISIBLE
                        lavShowStatus.visibility = View.INVISIBLE
                    }

                }
                ShowStatus.STARTING -> {
                    Timber.e("check status in ${item.waitingTime}")
                    tvShowTime.visibility = View.INVISIBLE
                    loadJsonString(getAssetByState(ShowStatus.STARTING))

                }
                ShowStatus.PLAY -> {
                    tvShowTime.visibility = View.INVISIBLE
                    loadJsonString(getAssetByState(ShowStatus.PLAY))
                }
                ShowStatus.WATCHING -> {
                    tvShowTime.visibility = View.INVISIBLE
                    loadJsonString(getAssetByState(ShowStatus.WATCHING))
                }
                else -> {
                    tvShowTime.visibility = View.INVISIBLE
                    lavShowStatus.visibility = View.INVISIBLE
                    if (lavShowStatus.isAnimating) {
                        lavShowStatus.pauseAnimation()
                    }
                }
            }

            if (item.options == null || item.options.isEmpty()) llOptionContainer.visibility = View.INVISIBLE

            lavShowStatus.setOnClickListener {
                listener?.onShowActionButtonClicked(item)
            }
        }
    }

    private fun getAssetByState(state: Int): String {
        return when (state) {
            ShowStatus.STARTING -> getStartingFile()
            ShowStatus.PLAY -> getPlayFile()
            else -> getWatchFile()
        }
    }

    private fun getStartingFile(): String {
        return when (UiUtils.getLocalization()) {
            Constants.VIETNAMESE_LANGUAGE_PHONE -> STARTING_VI
            else -> STARTING
        }
    }

    private fun getPlayFile(): String {
        return when (UiUtils.getLocalization()) {
            Constants.VIETNAMESE_LANGUAGE_PHONE -> PLAYNOW_VI
            else -> PLAYNOW
        }
    }

    private fun getWatchFile(): String {
        return when (UiUtils.getLocalization()) {
            Constants.VIETNAMESE_LANGUAGE_PHONE -> WATCHNOW_VI
            else -> WATCHNOW
        }
    }

    private fun loadJsonString(fileName: String?) {
        itemView.lavShowStatus.progress = 0F
        LottieComposition.Factory.fromAssetFileName(itemView.context, fileName) { composition ->
            if (composition != null) {
                setComposition(composition)
            }
        }

    }

    private fun setComposition(composition: LottieComposition) {

        itemView.lavShowStatus.setComposition(composition)
        playAnimation()
    }

    private fun playAnimation() {
        itemView.lavShowStatus.visibility = View.VISIBLE
        itemView.lavShowStatus.playAnimation()
    }

    fun showOptions(visibility: Int) {
        if (itemView.llOptionContainer.visibility != visibility && itemView.llOptionContainer.childCount > 0) {
            itemView.llOptionContainer.visibility = visibility
        }
    }

    fun removeDost(amount: Double): String {
        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false
        return format.format(amount)
    }

    interface OnClickListener {
        fun onOptionClicked(option: LiveShowOption)
        fun onFollowClicked(item: LiveShowLastModel)
        fun onShowActionButtonClicked(item: LiveShowLastModel)
        fun checkStatus(showId: Int)
        fun onBalanceClicked(balance: Balance)
        fun onStampBalanceClicked(stampBalance: StampBalance)
    }
}