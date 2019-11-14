package com.appster.features.points.adapter.delegate

import android.os.CountDownTimer
import android.util.Log
import android.view.ViewGroup
import com.appster.R
import com.appster.adapters.OnItemClickListener
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.extensions.inflate
import com.appster.features.points.DailyBonus
import com.appster.features.points.MysteryBox
import com.appster.features.points.MysteryBoxViewType
import com.appster.features.points.adapter.PointsAdapter
import com.appster.features.points.adapter.holder.DailyBonusHolder

/**
 *  Created by DatTN on 10/23/2018
 */
class DailyBonusDelegate(private val onItemClickListener: OnItemClickListener<out MysteryBox>,
                         private val onPrizeItemClickListener: PointsAdapter.OnPrizeItemClicked) :
        AbsListItemAdapterDelegate<DailyBonus, DisplayableItem, DailyBonusHolder>() {

    var totalCountDown = 0L

    private var mCountDownTimer: CountDownTimer? = null
    private var mCurrentHolder: DailyBonusHolder? = null

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {
        return item is DailyBonus && item.viewType == MysteryBoxViewType.VIEW_DAILY
    }

    override fun onCreateViewHolder(parent: ViewGroup): DailyBonusHolder {
        mCurrentHolder = DailyBonusHolder(parent.inflate(R.layout.points_daily_bonus_holder, false), onItemClickListener, onPrizeItemClickListener)
        return mCurrentHolder!!
    }

    override fun onBindViewHolder(item: DailyBonus, viewHolder: DailyBonusHolder, payloads: MutableList<Any>) {
        viewHolder.onBind(item)
        setupCountDownTimer()
    }

    fun setupCountDownTimer() {
        if (mCountDownTimer != null || mCurrentHolder == null) {
            return
        }
        mCountDownTimer = object : CountDownTimer(totalCountDown * 1000, 1000) {
            override fun onFinish() {
                mCurrentHolder?.onCountDownFinished()
            }

            override fun onTick(millisUntilFinished: Long) {
                mCurrentHolder?.onCountDownTick(millisUntilFinished)
            }

        }.start()
    }

    fun cancelCountDown() {
        mCountDownTimer?.cancel()
        mCountDownTimer = null
    }
}