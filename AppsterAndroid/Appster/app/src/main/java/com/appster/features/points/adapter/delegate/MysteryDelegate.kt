package com.appster.features.points.adapter.delegate

import android.view.ViewGroup
import com.appster.R
import com.appster.adapters.OnItemClickListener
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.extensions.inflate
import com.appster.features.points.MysteryBox
import com.appster.features.points.MysteryBoxViewType
import com.appster.features.points.adapter.PointsAdapter
import com.appster.features.points.adapter.holder.MysteryHolder

/**
 *  Created by DatTN on 10/23/2018
 */
class MysteryDelegate(private val onItemClickListener: OnItemClickListener<out MysteryBox>,
                      private val onPrizeItemClickListener: PointsAdapter.OnPrizeItemClicked) :
        AbsListItemAdapterDelegate<MysteryBox, DisplayableItem, MysteryHolder>() {

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {
        return item is MysteryBox && item.viewType == MysteryBoxViewType.VIEW_MYSTERY
    }

    override fun onCreateViewHolder(parent: ViewGroup): MysteryHolder {
        return MysteryHolder(parent.inflate(R.layout.points_daily_bonus_holder, false), onItemClickListener, onPrizeItemClickListener)
    }

    override fun onBindViewHolder(item: MysteryBox, viewHolder: MysteryHolder, payloads: MutableList<Any>) {
        viewHolder.onBind(item)
    }
}