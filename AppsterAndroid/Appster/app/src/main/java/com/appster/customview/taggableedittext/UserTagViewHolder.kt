package com.appster.customview.taggableedittext

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.appster.extensions.inflate
import com.appster.extensions.loadImg
import com.appster.utility.SpannableUtil
import kotlinx.android.synthetic.main.item_taggable_user.view.*

class UserTagViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup, @LayoutRes layout: Int): UserTagViewHolder {
            return UserTagViewHolder(parent.inflate(layout))
        }
    }

    fun bindTo(item: FollowUserView, query: String, listener: OnClickListener?) {
        with(itemView) {
            imgAvatar.loadImg(item.profilePic)
            val displayNameSpan = SpannableUtil.makeHighLightQuery(context, item.nickName, query)
            val userNameSpan = SpannableUtil.makeHighLightQuery(context, item.userName, query)
            txtDisplayName.text = displayNameSpan
            txtUsername.text = userNameSpan
            setOnClickListener { listener?.onFollowUserClick(item) }
        }
    }

    interface OnClickListener {
        fun onFollowUserClick(user: FollowUserView)
    }
}