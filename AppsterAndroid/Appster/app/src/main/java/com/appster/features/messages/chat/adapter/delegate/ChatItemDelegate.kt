package com.appster.features.messages.chat.adapter.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.appster.R
import com.appster.adapters.OnItemClickListener
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.features.messages.adapter.holder.MessageHolder
import com.appster.features.messages.chat.adapter.ChatAdapter
import com.appster.features.messages.chat.adapter.holder.ChatItemHolder
import com.appster.message.ChatItemModelClass

/**
 *  Created by DatTN on 10/10/2018
 */
class ChatItemDelegate(private val mOnMediaClickListener: ChatAdapter.OnMediaClickListener?) :
        AbsListItemAdapterDelegate<ChatItemModelClass, DisplayableItem, ChatItemHolder>() {

    private var mPreviousChatItem: ChatItemModelClass? = null
    var ownerImageUrl = ""
    var senderImageUrl = ""
    var friendDisplayName = ""

    override fun isForViewType(item: DisplayableItem, items: MutableList<DisplayableItem>, position: Int): Boolean {
        if (item is ChatItemModelClass) {
            if (position > 0 && position < items.size) {
                val pItem = items[position - 1]
                if (pItem is ChatItemModelClass) {
                    mPreviousChatItem = pItem
                }
            }
            return true
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup): ChatItemHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.holder_chat_item, parent, false)
        val chatItemHolder = ChatItemHolder(itemView, mOnMediaClickListener)
        chatItemHolder.ownerImageUrl = ownerImageUrl
        chatItemHolder.senderImageUrl = senderImageUrl
        chatItemHolder.friendDisplayName = friendDisplayName
        // TODO update avatar
        return chatItemHolder
    }

    override fun onBindViewHolder(item: ChatItemModelClass, viewHolder: ChatItemHolder, payloads: MutableList<Any>) {
        viewHolder.bindChatItem(item, mPreviousChatItem)
    }
}