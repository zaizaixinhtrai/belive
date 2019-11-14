package com.appster.adapters.delegate

import android.view.ViewGroup
import com.appster.adapters.viewholder.StreamsRecentHolder
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.features.searchScreen.SearchScreenOnClickListener
import com.data.entity.StreamsRecentEntity
import com.domain.models.ExploreStreamModel

/**
 * Created by Ngoc on 5/23/2018.
 */
class StreamsRecentDelegate(val itemCallBack: SearchScreenOnClickListener) : AbsListItemAdapterDelegate<ExploreStreamModel, DisplayableItem, StreamsRecentHolder>() {
    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {
        return item is ExploreStreamModel
    }

    override fun onCreateViewHolder(parent: ViewGroup): StreamsRecentHolder {
        return StreamsRecentHolder.create(parent)
    }

    override fun onBindViewHolder(item: ExploreStreamModel, viewHolder: StreamsRecentHolder, payloads: MutableList<Any>) {
        viewHolder.bindTo(item, itemCallBack)
    }

}