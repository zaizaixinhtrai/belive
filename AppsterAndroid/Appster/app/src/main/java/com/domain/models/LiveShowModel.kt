package com.domain.models

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.UpdateableItem

/**
 * Created by thanhbc on 5/16/18.
 */

data class LiveShowModel(val showId: Int,
                         val userId: Int,
                         val userName: String
                         , val showType: Int,
                         var showStatus: Int,
                         val showDateTime: Long,
                         val showTitle: String? = "NEXT SHOW :",
                         val showDesc: String? = "$600",
                         val showImage: String?,
                         var isFollow: Boolean = false,
                         var slug: String? = null,
                         val options: List<LiveShowOption>?,
                         var waitingTime: Int,
                         val balance: Balance?,
                         val isTrivia: Boolean,
                         val streamId: Int?,
                         val countryCode: String?,
                         val stampBalance: StampBalance?,
                         var isOgx: Boolean = false) : UpdateableItem {
    override fun isSameItem(item: DisplayableItem?): Boolean {
        return item is LiveShowModel && this.showId == item.showId
    }

    override fun isSameContent(item: DisplayableItem?): Boolean {
        return item is LiveShowModel &&
                this.showId == item.showId &&
                this.balance?.hashCode() == item.balance?.hashCode() &&
                this.showStatus == item.showStatus &&
                this.showDateTime == item.showDateTime &&
                this.isFollow == item.isFollow &&
                this.showTitle == item.showTitle &&
                this.showDesc == item.showDesc &&
                this.countryCode == item.countryCode &&
                this.options?.hashCode() == item.options?.hashCode() &&
                this.isTrivia == item.isTrivia &&
                this.streamId == item.streamId &&
                this.stampBalance?.hashCode() == item.stampBalance?.hashCode() &&
                this.isOgx == item.isOgx
    }
}

data class LiveShowLastModel(val showId: Int,
                             val userId: Int,
                             val userName: String
                             , val showType: Int,
                             var showStatus: Int,
                             val showDateTime: Long,
                             val showTitle: String? = "NEXT SHOW :",
                             val showDesc: String? = "$600",
                             val showImage: String?,
                             var isFollow: Boolean = false,
                             var slug: String? = null,
                             val options: List<LiveShowOption>?,
                             var waitingTime: Int,
                             val balance: Balance?,
                             val isTrivia: Boolean,
                             val streamId: Int?,
                             val countryCode: String?,
                             val stampBalance: StampBalance?,
                             var isOgx: Boolean = false) : UpdateableItem {
    override fun isSameItem(item: DisplayableItem?): Boolean {
        return item is LiveShowLastModel && this.showId == item.showId
    }

    override fun isSameContent(item: DisplayableItem?): Boolean {
        return item is LiveShowLastModel &&
                this.showId == item.showId &&
                this.balance?.hashCode() == item.balance?.hashCode() &&
                this.showStatus == item.showStatus &&
                this.showDateTime == item.showDateTime &&
                this.isFollow == item.isFollow &&
                this.showTitle == item.showTitle &&
                this.showDesc == item.showDesc &&
                this.options?.hashCode() == item.options?.hashCode() &&
                this.isTrivia == item.isTrivia &&
                this.streamId == item.streamId &&
                this.countryCode == item.countryCode &&
                this.stampBalance?.hashCode() == item.stampBalance?.hashCode() &&
                this.isOgx == item.isOgx
    }
}

data class LiveShowOption(val actionType: Int, val optionType: Int, var action: String? = null, var actionImg: Any?, val params: String, val triviaCountryCode: String?)

data class LiveShowStatus(val status: Int,
                          val streamId: Int?,
                          val slug: String?,
                          val waitingTime: Int)

data class Balance(val showType: Int, val amount: Double = 0.00, val cashoutUrl: String?, val message: String, val walletGroup: Int)

data class StampBalance(val cashoutUrl: String?, val amount: Double = 0.00)

