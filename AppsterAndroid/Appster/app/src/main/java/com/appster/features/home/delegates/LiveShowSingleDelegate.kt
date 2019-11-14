package com.appster.features.home.delegates

import android.view.ViewGroup
import com.appster.R
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.features.home.viewholders.LiveShowSingleViewHolder
import com.domain.models.LiveShowLastModel

/**
 * Created by thanhbc on 5/22/18.
 */

class LiveShowSingleDelegate (private val listener: LiveShowSingleViewHolder.OnClickListener?) : AbsListItemAdapterDelegate<LiveShowLastModel, DisplayableItem, LiveShowSingleViewHolder>() {

    override fun isForViewType(item: DisplayableItem, items: List<DisplayableItem>, position: Int): Boolean {
        return item is LiveShowLastModel
    }

    override fun onCreateViewHolder(parent: ViewGroup): LiveShowSingleViewHolder {
        return LiveShowSingleViewHolder.create(parent, R.layout.live_show_item_single)
    }

    override fun onBindViewHolder(item: LiveShowLastModel, viewHolder: LiveShowSingleViewHolder, payloads: List<Any>) {
        viewHolder.bindTo(item,listener)
    }
}