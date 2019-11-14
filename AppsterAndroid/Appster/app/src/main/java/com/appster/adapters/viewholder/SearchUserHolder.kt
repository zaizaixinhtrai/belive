package com.appster.adapters.viewholder

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.appster.R
import com.appster.extensions.inflate
import com.appster.extensions.loadImg
import com.appster.extensions.toUserName
import com.appster.search.AdapterSearchItemCallBack
import com.apster.common.Constants
import com.domain.models.SearchUserModel
import kotlinx.android.synthetic.main.search_list_item.view.*

/**
 * Created by Ngoc on 5/22/2018.
 */
class SearchUserHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {


    fun bindTo(item: SearchUserModel, adapterCallBack: AdapterSearchItemCallBack) {
        item.apply {
            // Set user image
            itemView.userImage.loadImg(item.userProfilePic)
            itemView.userImage.setOnClickListener { adapterCallBack.showUserProfile(adapterPosition, item) }
            itemView.userName.setOnClickListener { adapterCallBack.showUserProfile(adapterPosition, item) }
            if (isFollow == Constants.IS_FOLLOWING_USER) {
                itemView.btn_follow.background = ContextCompat.getDrawable(itemView.context, R.drawable.btn_following)
            } else {
                itemView.btn_follow.background = ContextCompat.getDrawable(itemView.context, R.drawable.btn_follow)
            }
            itemView.btn_follow.setOnClickListener { adapterCallBack.followUser(adapterPosition, item) }
            itemView.userName.text =username?.toUserName()
        }
    }

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): SearchUserHolder {
            return SearchUserHolder(parent.inflate(R.layout.search_list_item))
        }
    }

}