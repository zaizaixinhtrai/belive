package com.appster.features.points

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.UpdateableItem
import com.google.gson.annotations.SerializedName

/**
 *  Created by DatTN on 10/23/2018
 */
open class MysteryBox(
        val id: Int,
        val title: String,
        val thumbUrl: String,
        val bgColorCode: String?,
        val pointUse: Int,
        var prizes: List<Prize>,
        val coverImage: String?) : DisplayableItem {

    open var viewType: Int = MysteryBoxViewType.VIEW_MYSTERY
        get() = MysteryBoxViewType.VIEW_MYSTERY
}

class Prize(val id: Int,
            val title: String,
            val desc: String,
            val thumbUrl: String,
            val type: Int,
            val limited: Boolean = false,
            val quantity: Int = 0,
            val amount: Int = 0,
            val giftId: Int = 0,
            val storeBrief: String = "",
            val termConditions: String = "",
            val contactInfo: String = "",
            val urlInfo: String = "",
            val expireDate: Int = 0,
            val status: Int = 0) : DisplayableItem
