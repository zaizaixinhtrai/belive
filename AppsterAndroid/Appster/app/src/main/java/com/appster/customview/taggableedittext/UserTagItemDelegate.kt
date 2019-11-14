package com.appster.customview.taggableedittext

import android.view.ViewGroup
import com.appster.R
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem

class UserTagItemDelegate(private val listener: UserTagViewHolder.OnClickListener?): AbsListItemAdapterDelegate<FollowUserView, DisplayableItem, UserTagViewHolder>() {

    private var mQuery: String = ""

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {
        return item is FollowUserView
    }

    override fun onCreateViewHolder(parent: ViewGroup): UserTagViewHolder {
        return UserTagViewHolder.create(parent, R.layout.item_taggable_user)
    }

    override fun onBindViewHolder(item: FollowUserView, viewHolder: UserTagViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindTo(item, mQuery, listener)
    }

    fun setQuery(newQuery: String) {
        mQuery = newQuery
    }
}