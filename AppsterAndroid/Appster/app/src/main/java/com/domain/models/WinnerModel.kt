package com.domain.models

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.UpdateableItem

/**
 * Created by Ngoc on 3/9/2018.
 */

class WinnerModel : UpdateableItem {
    var userId: Int = 0
    var userAvatar: String? = null
    var displayName: String? = null
    var userName: String? = null
    var prize: Double = 0.toDouble()
    var prizeString: String? = null
    var orderIndex: Int = 0

    override fun isSameItem(item: DisplayableItem): Boolean {
        return item is WinnerModel && this.hashCode() == item.hashCode()
    }

    override fun isSameContent(item: DisplayableItem): Boolean {
        return this.userId == (item as WinnerModel).userId
    }
}
