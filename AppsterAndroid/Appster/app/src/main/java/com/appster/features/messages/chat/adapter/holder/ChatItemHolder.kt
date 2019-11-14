package com.appster.features.messages.chat.adapter.holder

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.appster.AppsterApplication
import com.appster.R
import com.appster.adapters.BaseRecyclerViewHolder
import com.appster.customview.CustomTypefaceSpan
import com.appster.customview.autolinktextview.AutoLinkMode
import com.appster.customview.autolinktextview.AutoLinkTextView
import com.appster.features.messages.chat.adapter.ChatAdapter
import com.appster.message.ChatItemModelClass
import com.appster.utility.AppsterUtility
import com.appster.utility.CustomTabUtils
import com.appster.utility.ImageLoaderUtil
import com.apster.common.CommonDefine
import com.apster.common.CopyTextUtils
import com.pack.utility.StringUtil
import kotlinx.android.synthetic.main.holder_chat_item.view.*
import kotlinx.android.synthetic.main.incoming_message.view.*
import kotlinx.android.synthetic.main.outgoing_message.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Created by DatTN on 10/10/2018
 */
class ChatItemHolder(view: View,
                     private val mOnMediaClickListener: ChatAdapter.OnMediaClickListener?) :
        BaseRecyclerViewHolder<ChatItemModelClass>(view, null) {

    private var mGlobalDateStamp = ""
    private var mHasShowCopyPopup = false
    var ownerImageUrl = ""
    var senderImageUrl = ""
    var friendDisplayName = ""

    fun bindChatItem(chatItem: ChatItemModelClass?, previousChatItem: ChatItemModelClass?) {
        super.onBind(chatItem)
        val chatItemModelClass = chatItem ?: return
        val context = itemView?.context ?: return
        // Message = "0" means you are chatting with yourself.
        // This case is not going to happen in prod but what if there is an API error. who knows!
        // We, friendly clients, should handle this case well
        if ("0" == chatItemModelClass.msg) {
            itemView.visibility = View.GONE
            return
        }
        itemView.visibility = View.VISIBLE
        itemView.tvSenderMessage.addAutoLinkMode(AutoLinkMode.MODE_URL)
        itemView.tvOwnerMessage.addAutoLinkMode(AutoLinkMode.MODE_URL)

        var msgImageData = ""
        var dayTime: String? = ""
        var message = ""
        var urlThumb = ""
        var time = ""
        var flagSendMedia = 1

        // set image null
        itemView.ivOwnerImageMessage.setImageBitmap(null)
        itemView.ivSenderImageMessage.setImageBitmap(null)

        // Check send Image
        if (chatItemModelClass.msg != null && chatItemModelClass.msg.contains(CommonDefine.KEY_USER_SEND_IMAGE)) {
            val parts = chatItemModelClass.msg.split(CommonDefine.KEY_USER_SEND_IMAGE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size > 1) {
                msgImageData = parts[1]
                dayTime = parts[0]
                flagSendMedia = 1
            }
        } else if (chatItemModelClass.msg != null && chatItemModelClass.msg.contains(CommonDefine.KEY_USER_SEND_VIDEO)) {
            // Check send video
            val parts = chatItemModelClass.msg.split(CommonDefine.KEY_USER_SEND_VIDEO.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size > 1) {
                msgImageData = parts[1]
                dayTime = parts[0]
                flagSendMedia = 2
            }

        } else {
            dayTime = chatItemModelClass.msg
        }

        // Get time
        if (!StringUtil.isNullOrEmptyString(dayTime) && dayTime!!.contains(CommonDefine.KEY_USER_SEND_TIME)) {
            val timeMessage = dayTime.split(CommonDefine.KEY_USER_SEND_TIME.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (timeMessage.isNotEmpty()) {
                time = timeMessage[0]
            }
            if (timeMessage.size > 1) {
                message = timeMessage[1]
            }
        }

        var visibility = View.GONE
        if (adapterPosition == 0) {
            visibility = View.VISIBLE
        } else {
            if (chatItemModelClass.time != null && previousChatItem != null && previousChatItem.time != null) {
                if (!AppsterUtility.sameDate(chatItemModelClass.time, previousChatItem.time)) {
                    visibility = View.VISIBLE
                }
            }
        }
        itemView.lo_date_chat_divider.visibility = visibility
        if (chatItemModelClass.time != null) {
            itemView.tv_chat_date_time.text = AppsterUtility.convertRelativeTimeSpanStringDay(context, chatItemModelClass.time)
        }

        // Get media
        if (!StringUtil.isNullOrEmptyString(msgImageData)) {
            if (msgImageData.contains(CommonDefine.KEY_USER_SEND_THUMBIMAGE)) {
                val arrImage = msgImageData.split(CommonDefine.KEY_USER_SEND_THUMBIMAGE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (arrImage.size > 1) {
                    urlThumb = arrImage[1]
                }
            }
        }

        val isOwnerMessage = chatItemModelClass.userIdSend == AppsterApplication.mAppPreferences.userModel.userId
        if (isOwnerMessage) {
            bindOwnerMessage(context, chatItemModelClass, time, message, urlThumb, flagSendMedia)
        } else {
            bindSenderMessage(context, chatItemModelClass, time, message, urlThumb, flagSendMedia)
        }

        itemView.lo_sender_message_container.setOnClickListener {
            mOnMediaClickListener?.onItemClicked()
        }
        itemView.lo_owner_message_container.setOnClickListener {
            mOnMediaClickListener?.onItemClicked()
        }
    }

    private fun bindOwnerMessage(context: Context, chatItem: ChatItemModelClass, time: String, message: String, urlThumb: String, flagSendMedia: Int) {
        itemView.lo_sender_message_container.visibility = View.GONE
        itemView.lo_owner_message_container.visibility = View.VISIBLE
        // Set time
        setTime(itemView.tvOwnerMessageTime, time)
        // Set message
        if (!StringUtil.isNullOrEmptyString(message)) {
            var msgSentGift = ""
            if (message.contains(CommonDefine.KEY_USER_SEND_GIFT)) {
                val msgGift = message.split(CommonDefine.KEY_USER_SEND_GIFT.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (msgGift.size > 1) {
                    msgSentGift = msgGift[1]
                }
                itemView.lo_sender_message_container.visibility = View.GONE
                itemView.lo_owner_message_container.visibility = View.VISIBLE
                val charSequence = createText(AppsterApplication.mAppPreferences.userModel.displayName,
                        ContextCompat.getColor(context, R.color.color_98d7de),
                        String.format(context.getString(R.string.message_sent_a_item), msgSentGift),
                        ContextCompat.getColor(context, R.color.red_notification_bar), " ")
                itemView.tvOwnerMessage.text = charSequence
                ableCopyText(itemView.tvOwnerMessage, charSequence.toString())
            } else {
                ableClickTextView(itemView.tvOwnerMessage, StringUtil.decodeString(message))
            }
            itemView.tvOwnerMessage.visibility = View.VISIBLE
        } else {
            itemView.tvOwnerMessage.visibility = View.GONE
        }

        // Set image send
        if (!StringUtil.isNullOrEmptyString(urlThumb)) {
            ImageLoaderUtil.displayMediaImage(context, urlThumb, itemView.ivOwnerImageMessage)
            itemView.ivOwnerImageMessage.visibility = View.VISIBLE
            // View detail Image
            itemView.ivOwnerImageMessage.setOnClickListener {
                mOnMediaClickListener?.onClickViewImage(chatItem)
            }

            if (flagSendMedia == 2) {
                itemView.ivOwnerVideoIndicator.visibility = View.VISIBLE
                itemView.ivOwnerVideoIndicator.setOnClickListener {
                    mOnMediaClickListener?.onClickViewVideo(chatItem)
                }
            } else {
                itemView.ivOwnerVideoIndicator.visibility = View.GONE
            }
        } else {
            itemView.ivOwnerImageMessage.visibility = View.GONE
            itemView.ivOwnerVideoIndicator.visibility = View.GONE
            itemView.tvOwnerMessage.visibility = View.VISIBLE
        }

        // Set owner image
        ImageLoaderUtil.displayUserImage(context, ownerImageUrl,
                itemView.ivOwnerUserImage)
    }

    private fun bindSenderMessage(context: Context, chatItem: ChatItemModelClass, time: String, message: String, urlThumb: String, flagSendMedia: Int) {
        itemView.lo_sender_message_container.visibility = View.VISIBLE
        itemView.lo_owner_message_container.visibility = View.GONE
        // Set time
        setTime(itemView.tvSenderMessageTime, time)
        // Set message
        if (!StringUtil.isNullOrEmptyString(message)) {
            var msgSentGift = ""
            if (message.contains(CommonDefine.KEY_USER_SEND_GIFT)) {
                val msgGift = message.split(CommonDefine.KEY_USER_SEND_GIFT.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (msgGift.size > 1) {
                    msgSentGift = msgGift[1]
                }
                itemView.lo_sender_message_container.visibility = View.VISIBLE
                itemView.lo_owner_message_container.visibility = View.GONE
                val charSequence = createText(friendDisplayName, ContextCompat.getColor(context, R.color.color_98d7de),
                        String.format(context.getString(R.string.message_sent_a_item), msgSentGift),
                        ContextCompat.getColor(context, R.color.red_notification_bar), " ")
                itemView.tvSenderMessage.text = charSequence
                ableCopyText(itemView.tvSenderMessage, charSequence.toString())
            } else {
                ableClickTextView(itemView.tvSenderMessage, StringUtil.decodeString(message))
            }
            itemView.tvSenderMessage.visibility = View.VISIBLE

        } else {
            itemView.tvSenderMessage.visibility = View.GONE
        }

        // Set image send
        if (!StringUtil.isNullOrEmptyString(urlThumb)) {
            ImageLoaderUtil.displayMediaImage(context, urlThumb, itemView.ivSenderImageMessage)
            itemView.ivSenderImageMessage.visibility = View.VISIBLE
            // View detail Image
            itemView.ivSenderImageMessage.setOnClickListener {
                mOnMediaClickListener?.onClickViewImage(chatItem)
            }
            if (flagSendMedia == 2) {
                itemView.ivSenderVideoIndicator.visibility = View.VISIBLE
                itemView.ivSenderVideoIndicator.setOnClickListener {
                    mOnMediaClickListener?.onClickViewVideo(chatItem)
                }
            } else {
                itemView.ivSenderVideoIndicator.visibility = View.GONE
            }

        } else {
            itemView.ivSenderImageMessage.visibility = View.GONE
            itemView.ivSenderVideoIndicator.visibility = View.GONE
            itemView.tvSenderMessage.visibility = View.VISIBLE
        }

        // Set sender image
        ImageLoaderUtil.displayUserImage(context, senderImageUrl,
                itemView.ivSenderUserImage)
        itemView.ivSenderUserImage.setOnClickListener {
            mOnMediaClickListener?.onSenderAvatarClick()
        }
    }

    private fun setTime(txtView: TextView?, time: String?) {
        if (time == null || time == "") {
            return
        }

        if (time.contains(".")) {
            val created = time.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val split = created[0]
            var dv: Long = 0
            try {
                dv = java.lang.Long.valueOf(split) * 1000// its need to be in milisecond
            } catch (e: Exception) {

            }

            val df = Date(dv)
            val time1 = SimpleDateFormat("HH:mm").format(df)
            txtView?.text = time1

        } else {
            var dv: Long = 0
            try {
                dv = java.lang.Long.valueOf(time) * 1000// its need to be in milisecond
            } catch (e: Exception) {

            }

            val df = Date(dv)
            val dateCurrent = SimpleDateFormat(itemView?.context?.getString(R.string.formatter_chat_date)).format(df)
            mGlobalDateStamp = dateCurrent

            val time1 = SimpleDateFormat("HH:mm").format(df)
            txtView?.text = time1
        }
    }

    private fun createText(userName: String, userNameColor: Int, giftMessage: String, giftColor: Int, divider: String): CharSequence {
        val builder = SpannableStringBuilder()
                .append(formatString(userName, userNameColor, Typeface.BOLD))
                .append(divider)
                .append(formatString(giftMessage, giftColor, Typeface.BOLD))

        return builder.subSequence(0, builder.length)
    }

    private fun formatString(text: String, color: Int, style: Int): SpannableString {
        val context = itemView?.context ?: return SpannableString("")
        if (TextUtils.isEmpty(text)) {
            return SpannableString("")
        }

        val spannableString = SpannableString(text)
        spannableString.setSpan(ForegroundColorSpan(color), 0, text.length, 0)
        spannableString.setSpan(StyleSpan(style), 0, text.length, 0)
        val font = Typeface.createFromAsset(context.assets, "fonts/opensansbold.ttf")

        spannableString.setSpan(CustomTypefaceSpan("", font), 0, text.length, 0)

        return spannableString
    }

    private fun ableCopyText(textView: AutoLinkTextView?, message: String) {
        val context = textView?.context ?: return
        textView.setOnLongClickListener { v ->
            CopyTextUtils.showOptionCopyText(context, v, message) { menu -> }
            true
        }
    }

    private fun ableClickTextView(textView: AutoLinkTextView?, message: String) {
        val context = textView?.context ?: return
        textView.setAutoLinkText(message)
        textView.setAutoLinkOnClickListener { autoLinkMode, matchedText ->
            if (!mHasShowCopyPopup) {
                CustomTabUtils.openChromeTab(context as Activity, matchedText)
            }
        }
        textView.setOnLongClickListener { v ->
            mHasShowCopyPopup = true
            CopyTextUtils.showOptionCopyText(context, v, message) { mHasShowCopyPopup = false }
            true
        }
    }
}