package com.appster.customview.taggableedittext

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.ListDisplayableDelegationAdapter
import com.apster.common.BaseDiffCallback

class UserTagAdapter(diffCallback: BaseDiffCallback<*>?,
                     items: List<DisplayableItem>,
                     listener: UserTagViewHolder.OnClickListener?): ListDisplayableDelegationAdapter(diffCallback) {

    private val itemDelegate: UserTagItemDelegate = UserTagItemDelegate(listener)

    init {
        this.delegatesManager.addDelegate(itemDelegate)
        setItems(items)
    }

    fun updateQuery(query: String) {
        itemDelegate.setQuery(query)
    }
}