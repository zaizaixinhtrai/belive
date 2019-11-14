package com.domain.models

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.UpdateableItem

/**
 * Created by thanhbc on 6/13/18.
 */
data class ExploreStreamModel(val streamUrl: String?,
                              val slug: String?,
                              val streamImage: String?,
                              val isRecorded: Boolean = false,
                              val userId: Int? = 0,
                              val userName: String?,
                              val streamTitle: String?,
                              val viewCount: Int = 0) : UpdateableItem {
    override fun isSameItem(item: DisplayableItem?): Boolean {
        return item is ExploreStreamModel && this.streamUrl == item.streamUrl && this.isRecorded == item.isRecorded
    }

    override fun isSameContent(item: DisplayableItem?): Boolean {
        return item is ExploreStreamModel && this.hashCode() == item.hashCode()
    }
}