package com.domain.models

import com.appster.core.adapter.DisplayableItem
import com.appster.search.SearchUserTypeItems

/**
 * Created by Ngoc on 5/28/2018.
 */
class SearchUserModel : DisplayableItem {
    var userId: String? = null
    var username: String? = null
    var displayName: String? = null
    var userProfilePic: String? = null
    var gender: String? = null
    var isFollow: Int = 0
    var typeModel = SearchUserTypeItems.USER_ITEM
}