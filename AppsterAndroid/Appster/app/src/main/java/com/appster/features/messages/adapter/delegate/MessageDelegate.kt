package com.appster.features.messages.adapter.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.appster.R
import com.appster.adapters.OnItemClickListener
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.features.messages.MessageItemModelClass
import com.appster.features.messages.adapter.holder.MessageHolder
import com.appster.layout.recyclerSwipeUtil.SwipeItemMangerImpl
import com.appster.layout.recyclerSwipeUtil.SwipeItemMangerInterface

/**
 *  Created by DatTN on 10/8/2018
 *
 *  A delegate which handles all of the view holder related stuff like: checking view type, create view holder, bind view holder, etc
 *  Normally, these functions are in recycler adapter, but since we have a bunch of view types, which make the adapter become very fat & hard for readability.
 *  It's the main benefit of these delegates.
 */
class MessageDelegate(private val mClickListener: OnItemClickListener<MessageItemModelClass>?,
                      private val swipeItemManger: SwipeItemMangerImpl? = null) :
        AbsListItemAdapterDelegate<MessageItemModelClass, DisplayableItem, MessageHolder>() {

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {
        return item is MessageItemModelClass
    }

    override fun onCreateViewHolder(parent: ViewGroup): MessageHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.holder_message_swipe, parent, false)
        return MessageHolder(itemView, mClickListener)
    }

    override fun onBindViewHolder(item: MessageItemModelClass, viewHolder: MessageHolder, payloads: MutableList<Any>) {
        viewHolder.onBind(item)
        swipeItemManger?.bind(viewHolder.itemView, viewHolder.adapterPosition)
    }
}