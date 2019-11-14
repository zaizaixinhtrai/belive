package com.appster.features.messages.adapter.holder

import android.graphics.Typeface
import android.view.View
import com.appster.adapters.BaseRecyclerViewHolder
import com.appster.adapters.OnItemClickListener
import com.appster.features.messages.MessageItemModelClass
import com.appster.utility.ImageLoaderUtil
import com.apster.common.CommonDefine
import com.pack.utility.SetDateTime
import com.pack.utility.StringUtil
import kotlinx.android.synthetic.main.holder_message_swipe.view.*

/**
 *  Created by DatTN on 10/8/2018
 */
class MessageHolder(view: View, clickListener: OnItemClickListener<MessageItemModelClass>?) : BaseRecyclerViewHolder<MessageItemModelClass>(view, clickListener) {

    init {
        itemView.iv_user_mage.setOnClickListener(this)
        itemView.lo_perform_history_chat.setOnClickListener(this)
        itemView.lo_time.setOnClickListener(this)
        itemView.btn_delete.setOnClickListener(this)
    }

    override fun onBind(data: MessageItemModelClass?) {
        super.onBind(data)
        if (data == null) {
            return
        }
//        Log.e("data", data.toString())
        val context = itemView.context

        // Set user image
        ImageLoaderUtil.displayUserImage(context, data.msg_profile_pic,
                itemView.iv_user_mage)
        itemView.tv_display_name.text = data.msg_display_name
        itemView.tv_notification_time.text = SetDateTime.convertTimeStamp(data.msg_timestamp, context)

        // Set Count unread
        if (data.unread_message_count > 0) {
            itemView.tv_total_message.text = data.unread_message_count.toString()
            itemView.tv_total_message.visibility = View.VISIBLE
            itemView.tv_message.setTypeface(null, Typeface.BOLD)
        } else {
            itemView.tv_total_message.visibility = View.GONE
            itemView.tv_message.setTypeface(null, Typeface.NORMAL)
        }

        val message = data.msg_message
        var messageShow = message
        if (message != null && !message.isEmpty()) {
            if (message.contains(CommonDefine.KEY_USER_SEND_TIME)) {
                val msgSplit = message.split(CommonDefine.KEY_USER_SEND_TIME.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (msgSplit.size > 1) {
                    messageShow = msgSplit[1]
                }
            }
        }
        itemView.tv_message.text = StringUtil.decodeString(messageShow)
    }
}