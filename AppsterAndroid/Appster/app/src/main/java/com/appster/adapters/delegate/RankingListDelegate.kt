package com.appster.adapters.delegate

import android.view.ViewGroup

import com.appster.adapters.viewholder.RankingListHolder
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.features.mvpbase.RecyclerItemCallBack
import com.domain.models.WinnerModel

/**
 * Created by Ngoc on 3/9/2018.
 */

class RankingListDelegate(private val recyclerItemCallBack: RecyclerItemCallBack<WinnerModel>) : AbsListItemAdapterDelegate<WinnerModel, DisplayableItem, RankingListHolder>() {

    override fun isForViewType(item: DisplayableItem, items: List<DisplayableItem>, position: Int): Boolean {
        return item is WinnerModel
    }

    override fun onCreateViewHolder(parent: ViewGroup): RankingListHolder {
        return RankingListHolder.create(parent)
    }

    override fun onBindViewHolder(item: WinnerModel, viewHolder: RankingListHolder, payloads: List<Any>) {
        viewHolder.bindTo(item, recyclerItemCallBack)
    }
}
