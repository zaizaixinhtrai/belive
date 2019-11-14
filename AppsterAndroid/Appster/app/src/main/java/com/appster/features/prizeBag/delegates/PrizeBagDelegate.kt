package com.appster.features.prizeBag.delegates

import android.view.ViewGroup
import com.appster.R
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.features.prizeBag.viewholders.PrizeBagViewHolder
import com.domain.models.PrizeBagModel

class PrizeBagDelegate(private val listener: PrizeBagViewHolder.OnClickListener?) : AbsListItemAdapterDelegate<PrizeBagModel, DisplayableItem, PrizeBagViewHolder>() {
    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {
        return item is PrizeBagModel
    }

    override fun onCreateViewHolder(parent: ViewGroup): PrizeBagViewHolder {
        return PrizeBagViewHolder.create(parent)
    }

    override fun onBindViewHolder(item: PrizeBagModel, viewHolder: PrizeBagViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindTo(item, listener)
    }
}