package com.appster.features.points.prizelist.adapter.delegate

import android.view.ViewGroup
import com.appster.R
import com.appster.adapters.OnItemClickListener
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.extensions.inflate
import com.appster.features.points.Prize
import com.appster.features.points.prizelist.adapter.holder.PrizeHeaderHolder

/**
 *  Created by DatTN on 10/29/2018
 */
class PrizeHeaderDelegate(private val mOnItemClickListener: OnItemClickListener<Prize>) : AbsListItemAdapterDelegate<Prize, DisplayableItem, PrizeHeaderHolder>() {

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {
        return item is Prize && position == 0
    }

    override fun onCreateViewHolder(parent: ViewGroup): PrizeHeaderHolder {
        return PrizeHeaderHolder(parent.inflate(R.layout.holder_prize_header), mOnItemClickListener)
    }

    override fun onBindViewHolder(item: Prize, viewHolder: PrizeHeaderHolder, payloads: MutableList<Any>) {
        viewHolder.onBind(item)
    }
}