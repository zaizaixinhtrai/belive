package com.appster.features.messages.chat.adapter

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.ListDisplayableDelegationAdapter
import com.appster.core.adapter.LoadMoreDelegate
import com.appster.core.adapter.LoadMoreItem
import com.appster.features.messages.chat.adapter.delegate.ChatItemDelegate
import com.appster.message.ChatItemModelClass
import timber.log.Timber

/**
 *  Created by DatTN on 10/10/2018
 */
class ChatAdapter(private val mItems: MutableList<DisplayableItem>,
                  mOwnerImageUrl: String,
                  mSenderImageUrl: String,
                  mFriendDisplayName: String,
                  mOnMediaClickListener: OnMediaClickListener?) : ListDisplayableDelegationAdapter(null) {

    companion object {
        val VIEW_CHAT_ITEM = 0
        val VIEW_LOAD_PREVIOUS = 5
    }

    private var chatItemDelegate: ChatItemDelegate = ChatItemDelegate(mOnMediaClickListener)

    init {
        chatItemDelegate.ownerImageUrl = mOwnerImageUrl
        chatItemDelegate.senderImageUrl = mSenderImageUrl
        chatItemDelegate.friendDisplayName = mFriendDisplayName
        delegatesManager.addDelegate(VIEW_CHAT_ITEM, chatItemDelegate)
        delegatesManager.addDelegate(VIEW_LOAD_PREVIOUS, LoadMoreDelegate())
        setItems(mItems)
    }

    fun addItemsAtFirst(items: List<ChatItemModelClass>) {
        if (items.isNotEmpty()) {
            addItems(0, items)
            // the most recently item is showing the date time title. After adding new items, we need to update this item to reflect the change
            notifyItemChanged(items.size)
        }
    }

    fun addItems(index: Int, items: List<ChatItemModelClass>) {
        var aIndex = index
        if (aIndex < 0) {
            aIndex = 0
        } else if (aIndex >= items.size) {
            aIndex = items.size - 1
        }
        mItems.addAll(aIndex, items)
        notifyItemRangeInserted(aIndex, items.size)
    }

    fun addChatItem(chatItem: ChatItemModelClass) {
        var isDuplicated = false
        for (i in mItems.indices) {
            val item = mItems[i]
            if (item is ChatItemModelClass) {
                if (item.msg == chatItem.msg) {
                    Timber.e("isDuplicated %s", chatItem.msg)
                    isDuplicated = true
                    break
                }
            }
        }
        if (!isDuplicated) {
            addItemAt(chatItem)
        }
    }

    fun addItemAt(chatItem: DisplayableItem, index: Int = mItems.size) {
        var nIndex = index
        if (nIndex < 0) {
            nIndex = 0
        } else if (nIndex > mItems.size) {
            nIndex = mItems.size
        }
        mItems.add(nIndex, chatItem)
        notifyItemInserted(nIndex)
    }

    fun removeLastItem() {
        removeItemAt(mItems.size - 1)
    }

    fun removeItemAt(index: Int) {
        if (index < 0 || index >= mItems.size) {
            return
        }
        mItems.removeAt(index)
        notifyItemRemoved(index)
    }

    fun addLoadPreviousItem() {
        addItemAt(LoadMoreItem(), 0)
    }

    /**
     * Remove the first load previous item only
     */
    fun removeLoadPreviousItem() {
        if (mItems.size > 0) {
            val firstItem = mItems[0]
            if (firstItem is LoadMoreItem) {
                removeItemAt(0)
            }
        }
    }

    interface OnMediaClickListener {
        fun onClickViewImage(chatItem: ChatItemModelClass)

        fun onClickViewVideo(chatItem: ChatItemModelClass)

        fun onSenderAvatarClick()

        fun onItemClicked()
    }
}