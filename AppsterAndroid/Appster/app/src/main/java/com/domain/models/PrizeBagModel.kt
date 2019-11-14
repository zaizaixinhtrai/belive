package com.domain.models

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.UpdateableItem

data class PrizeBagModel(val id: Int,
                         val prizeItem: PrizeItem?,
                         val name: String?,
                         val email: String?,
                         val redeemDate: Int,
                         val sentDate: Int,
                         val status: Int,
                         val created: String?) : UpdateableItem {

    override fun isSameItem(item: DisplayableItem?): Boolean {
        return item is PrizeBagModel && this.id == item.id
    }

    override fun isSameContent(item: DisplayableItem?): Boolean {
        return item is PrizeBagModel &&
                this.id == item.id &&
                this.prizeItem?.hashCode() == item.prizeItem?.hashCode() &&
                this.name == item.name &&
                this.email == item.email &&
                this.redeemDate == item.redeemDate &&
                this.sentDate == item.sentDate &&
                this.status == item.status &&
                this.created == item.created
    }
}

data class PrizeItem(
        val id: Int,
        val name: String?,
        val title: String?,
        val image: String?,
        val type: Int,
        val limited: Boolean,
        val quantity: Int,
        val amount: Int,
        val giftId: Int,
        val storeBrief: String?,
        val termConditions: String?,
        val contactInfo: String?,
        val infoUrl: String?,
        val expireDate: String?)