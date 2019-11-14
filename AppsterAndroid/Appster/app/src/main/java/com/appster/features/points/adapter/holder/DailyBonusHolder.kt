package com.appster.features.points.adapter.holder

import android.graphics.Color
import android.view.View
import com.appster.R
import com.appster.adapters.OnItemClickListener
import com.appster.features.points.DailyBonus
import com.appster.features.points.MysteryBox
import com.appster.features.points.adapter.PointsAdapter
import com.pack.utility.StringUtil
import kotlinx.android.synthetic.main.points_daily_bonus_holder.view.*

/**
 *  Created by DatTN on 10/23/2018
 */
class DailyBonusHolder(view: View, private val mOnItemClickListener: OnItemClickListener<out MysteryBox>,
                       mOnPrizeItemClickListener: PointsAdapter.OnPrizeItemClicked) : MysteryHolder(view, mOnItemClickListener, mOnPrizeItemClickListener) {

    init {
    }

    override fun onBind(model: MysteryBox?) {
        super.onBind(model)
        if (model == null) {
            return
        }
        itemView?.apply { iv_thumb.setBackgroundResource(R.drawable.icon_free_thumb)
            contr_parent.setBackgroundColor(Color.parseColor("#00ABAC"))}
    }

    override fun handlePriceView(mysteryBox: MysteryBox) {
        val dailyBonus = mysteryBox as DailyBonus
        itemView?.apply {
            if (dailyBonus.countDown > 0) {
                lo_price.setBackgroundResource(R.drawable.bg_mystery_price_stroke)
                iv_price_thumb.visibility = View.GONE
            } else {
                tv_price.text = context.getString(R.string.open)
                lo_price.setOnClickListener(this@DailyBonusHolder)
                lo_price.setBackgroundResource(R.drawable.open_foreground_selector)
            }
        }
    }

    fun onCountDownFinished() {
        itemView?.apply {
            tv_price.text = context.getString(R.string.open)
            lo_price.setOnClickListener(this@DailyBonusHolder)
            lo_price.setBackgroundResource(R.drawable.open_foreground_selector)
            (model as DailyBonus).apply { countDown = 0 }
        }
    }

    fun onCountDownTick(time: Long) {
        itemView?.apply {
            tv_price.text = StringUtil.convertTimeStampToStringTime(time, ':')
            lo_price.setOnClickListener(null)
            lo_price.setBackgroundResource(R.drawable.bg_mystery_price_stroke)
        }
    }
}