package com.appster.adapters.delegate

import android.view.ViewGroup

import com.appster.adapters.viewholder.WinnerListViewHolder
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.features.mvpbase.RecyclerItemCallBack
import com.domain.models.WinnerModel

/**
 * Created by Ngoc on 3/14/2018.
 */

class WinnerListDelegate(private val recyclerItemCallBack: RecyclerItemCallBack<WinnerModel>?) : AbsListItemAdapterDelegate<WinnerModel, DisplayableItem, WinnerListViewHolder>() {
    override fun isForViewType(item: DisplayableItem, items: List<DisplayableItem>, position: Int): Boolean {
        return item is WinnerModel
    }

    override fun onCreateViewHolder(parent: ViewGroup): WinnerListViewHolder {
        return WinnerListViewHolder.create(parent)
    }

    override fun onBindViewHolder(item: WinnerModel, viewHolder: WinnerListViewHolder, payloads: List<Any>) {
        viewHolder.bindTo(item, recyclerItemCallBack)
    }
}