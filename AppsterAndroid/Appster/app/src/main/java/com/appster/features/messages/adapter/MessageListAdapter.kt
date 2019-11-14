package com.appster.features.messages.adapter

import com.appster.R
import com.appster.adapters.OnItemClickListener
import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.EndlessDelegateAdapter
import com.appster.features.messages.MessageItemModelClass
import com.appster.features.messages.adapter.delegate.MessageDelegate
import com.appster.layout.recyclerSwipeUtil.*

/**
 *  Created by DatTN on 10/8/2018.
 *
 * The adapter for the message list screen. this adapter supports load more data & swipe manager.
 * This adapter is built based on the new delegate adapter approach
 */
class MessageListAdapter(mItems: MutableList<DisplayableItem> = mutableListOf(),
                         mClickEvent: OnItemClickListener<MessageItemModelClass>) :
        EndlessDelegateAdapter(null),
        SwipeItemMangerInterface,
        SwipeAdapterInterface {

    companion object {
        val VIEW_MESSAGE = 0
    }

    private var mSwipeItemManger = SwipeItemMangerImpl(this)

    init {
        // All of the delegate will be added here
        delegatesManager.addDelegate(VIEW_MESSAGE, MessageDelegate(mClickEvent, mSwipeItemManger))
        setItems(mItems)
    }

    //region -------implement methods-------
    override fun openItem(position: Int) {
        mSwipeItemManger.openItem(position)
    }

    override fun closeItem(position: Int) {
        mSwipeItemManger.closeItem(position)
    }

    override fun closeAllExcept(layout: SwipeLayout?) {
        mSwipeItemManger.closeAllExcept(layout)
    }

    override fun closeAllItems() {
        mSwipeItemManger.closeAllItems()
    }

    override fun getOpenItems(): MutableList<Int> {
        return mSwipeItemManger.openItems
    }

    override fun getOpenLayouts(): MutableList<SwipeLayout> {
        return mSwipeItemManger.openLayouts
    }

    override fun removeShownLayouts(layout: SwipeLayout?) {
        mSwipeItemManger.removeShownLayouts(layout)
    }

    override fun isOpen(position: Int): Boolean {
        return mSwipeItemManger.isOpen(position)
    }

    override fun getMode(): Attributes.Mode {
        return mSwipeItemManger.mode
    }

    override fun setMode(mode: Attributes.Mode?) {
        mSwipeItemManger.mode = mode
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.lo_swipe
    }

    override fun notifyDatasetChanged() {
        notifyDataSetChanged()
    }
    //endregion -------implement methods-------

    //region -------inner methods------
    fun clear() {
        clearItemOnly()
        notifyDataSetChanged()
    }

    fun clearItemOnly() {
        items.clear()
    }

    fun removeItemAt(index: Int) {
        if (index >= 0 && index <= items.size - 1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }
    //endregion -------inner methods-------
}