package com.appster.features.points.prizelist.adapter.delegate

import android.view.ViewGroup
import com.appster.R
import com.appster.adapters.OnItemClickListener
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.extensions.inflate
import com.appster.features.points.Prize
import com.appster.features.points.prizelist.adapter.holder.PrizeHolder

/**
 *  Created by DatTN on 10/29/2018
 */
class PrizeDelegate(private val mOnItemClickListener: OnItemClickListener<Prize>) : AbsListItemAdapterDelegate<Prize, DisplayableItem, PrizeHolder>() {

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {
        return item is Prize && position > 0
    }

    override fun onCreateViewHolder(parent: ViewGroup): PrizeHolder {
        return PrizeHolder(parent.inflate(R.layout.holder_prize), mOnItemClickListener)
    }

    override fun onBindViewHolder(item: Prize, viewHolder: PrizeHolder, payloads: MutableList<Any>) {
        viewHolder.onBind(item)
    }

}