package com.appster.features.points.adapter

import com.appster.adapters.OnItemClickListener
import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.ListDisplayableDelegationAdapter
import com.appster.features.points.MysteryBox
import com.appster.features.points.Prize
import com.appster.features.points.adapter.delegate.DailyBonusDelegate
import com.appster.features.points.adapter.delegate.MysteryDelegate
import com.appster.features.points.adapter.holder.MysteryHolder

/**
 *  Created by DatTN on 10/23/2018
 */
class PointsAdapter(items: List<DisplayableItem>, onItemClickListener: OnItemClickListener<out MysteryBox>, onPrizeItemClickListener: OnPrizeItemClicked) :
        ListDisplayableDelegationAdapter(null) {

    private val mDailyBonusDelegate: DailyBonusDelegate

    init {
        delegatesManager.addDelegate(MysteryDelegate(onItemClickListener, onPrizeItemClickListener))
        mDailyBonusDelegate = DailyBonusDelegate(onItemClickListener, onPrizeItemClickListener)
        delegatesManager.addDelegate(mDailyBonusDelegate)
        setItems(items)
    }

    interface OnPrizeItemClicked {
        fun onPrizeItemClicked(viewHolder: MysteryHolder, prize: Prize)
    }

    fun onViewInVisible() {
        mDailyBonusDelegate.cancelCountDown()
    }

    fun onDailyBonusCountDownUpdated(countDown: Int) {
        // cancel the current count down
        mDailyBonusDelegate.cancelCountDown()
        // and setup the new count down
        mDailyBonusDelegate.totalCountDown = countDown.toLong()
        mDailyBonusDelegate.setupCountDownTimer()
    }
}