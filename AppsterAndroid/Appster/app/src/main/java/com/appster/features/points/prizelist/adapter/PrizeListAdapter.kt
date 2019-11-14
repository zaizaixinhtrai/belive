package com.appster.features.points.prizelist.adapter

import com.appster.adapters.OnItemClickListener
import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.ListDisplayableDelegationAdapter
import com.appster.features.points.MysteryBox
import com.appster.features.points.MysteryBoxViewType
import com.appster.features.points.Prize
import com.appster.features.points.adapter.delegate.DailyBonusDelegate
import com.appster.features.points.adapter.delegate.MysteryDelegate
import com.appster.features.points.adapter.holder.MysteryHolder
import com.appster.features.points.prizelist.PrizeViewType
import com.appster.features.points.prizelist.adapter.delegate.PrizeDelegate
import com.appster.features.points.prizelist.adapter.delegate.PrizeHeaderDelegate

/**
 *  Created by DatTN on 10/29/2018
 */
class PrizeListAdapter (items: List<DisplayableItem>, onItemClickListener: OnItemClickListener<Prize>) :
        ListDisplayableDelegationAdapter(null) {


    init {
        delegatesManager.addDelegate(PrizeHeaderDelegate(onItemClickListener))
        delegatesManager.addDelegate(PrizeDelegate(onItemClickListener))
        setItems(items)
    }
}