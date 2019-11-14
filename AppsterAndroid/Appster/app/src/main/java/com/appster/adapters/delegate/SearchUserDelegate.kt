package com.appster.adapters.delegate

import android.view.ViewGroup
import com.appster.adapters.viewholder.SearchUserHolder
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.search.AdapterSearchItemCallBack
import com.appster.search.SearchUserTypeItems
import com.domain.models.SearchUserModel

/**
 * Created by Ngoc on 5/22/2018.
 */
class SearchUserDelegate(private val adapterCallBack: AdapterSearchItemCallBack) : AbsListItemAdapterDelegate<SearchUserModel, DisplayableItem, SearchUserHolder>() {

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {

        var searchItem: SearchUserModel? = null
        if (item is SearchUserModel) searchItem = item
        return searchItem?.typeModel == SearchUserTypeItems.USER_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup): SearchUserHolder {
        return SearchUserHolder.create(parent)
    }

    override fun onBindViewHolder(item: SearchUserModel, viewHolder: SearchUserHolder, payloads: MutableList<Any>) {
        viewHolder.bindTo(item, adapterCallBack)
    }

}