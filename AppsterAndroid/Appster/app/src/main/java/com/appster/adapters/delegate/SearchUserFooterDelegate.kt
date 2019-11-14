package com.appster.adapters.delegate

import android.view.ViewGroup
import com.appster.adapters.viewholder.SearchUserFooterHolder
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.search.AdapterSearchItemCallBack
import com.appster.search.SearchUserTypeItems
import com.domain.models.SearchUserModel

/**
 * Created by Ngoc on 5/22/2018.
 */
class SearchUserFooterDelegate(private val adapterCallBack: AdapterSearchItemCallBack) : AbsListItemAdapterDelegate<SearchUserModel, DisplayableItem, SearchUserFooterHolder>() {
    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {
        var searchItem: SearchUserModel? = null
        if (item is SearchUserModel) searchItem = item
        return searchItem?.typeModel == SearchUserTypeItems.FOOTER_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup): SearchUserFooterHolder {
        return SearchUserFooterHolder.create(parent)
    }

    override fun onBindViewHolder(item: SearchUserModel, viewHolder: SearchUserFooterHolder, payloads: MutableList<Any>) {
        viewHolder.bindTo(adapterCallBack)
    }
}