package com.appster.customview.taggableedittext

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.UpdateableItem

data class FollowUserView(val id: String, val userName: String, val nickName: String, val profilePic: String) : UpdateableItem {

    override fun isSameItem(item: DisplayableItem?): Boolean {
        return item is FollowUserView && id == item.id
    }

    override fun isSameContent(item: DisplayableItem): Boolean {
        /*return item is FollowUserView &&
                id == item.id &&
                userName == item.userName &&
                nickName == item.nickName*/
        // always return false so onBind() method can get called
        // -> highlighting query in username & display name can work
        return false
    }

}