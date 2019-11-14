package com.appster.features.home.delegates

import android.view.ViewGroup
import com.appster.R
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.features.home.viewholders.LiveShowMultiViewHolder
import com.domain.models.LiveShowModel

/**
 * Created by thanhbc on 5/16/18.
 */

class LiveShowMultipleDelegate(private val listener: LiveShowMultiViewHolder.OnClickListener?) : AbsListItemAdapterDelegate<LiveShowModel, DisplayableItem, LiveShowMultiViewHolder>() {

    override fun isForViewType(item: DisplayableItem, items: List<DisplayableItem>, position: Int): Boolean {
        return item is LiveShowModel
    }

    override fun onCreateViewHolder(parent: ViewGroup): LiveShowMultiViewHolder {
        return LiveShowMultiViewHolder.create(parent, R.layout.live_show_item_multi)
    }

    override fun onBindViewHolder(item: LiveShowModel, viewHolder: LiveShowMultiViewHolder, payloads: List<Any>) {
        viewHolder.bindTo(item,listener)
    }
}
