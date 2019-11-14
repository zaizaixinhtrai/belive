package com.data.entity

import com.google.gson.annotations.SerializedName

/**
 *  Created by DatTN on 10/23/2018
 */
class MysteryBoxEntity(
        @SerializedName("Id")
        val id: Int,
        @SerializedName("Name")
        val title: String,
        @SerializedName("Image")
        val thumbUrl: String,
        val bgColorCode: String?,
        @SerializedName("Points")
        val pointUse: Int,
        @SerializedName("Cover")
        val coverImage: String?,
        @SerializedName("PrizeItems")
        val prizeEntities: List<PrizeEntity>) {


    companion object {
        const val TYPE_DAILY_BONUS = 0
        const val TYPE_MYSTERY_BOX = 5
    }
}

class PrizeEntity(
        @SerializedName("Id")
        val id: Int,
        @SerializedName("Title")
        val title: String,
        @SerializedName("Name")
        val desc: String,
        @SerializedName("Image")
        val thumbUrl: String,
        @SerializedName("Type")
        val type: Int,
        @SerializedName("Limitted")
        val limited: Boolean = false,
        @SerializedName("Quantity")
        val quantity: Int = 0,
        @SerializedName("Amount")
        val amount: Int = 0,
        @SerializedName("GiftId")
        val giftId: Int = 0,
        @SerializedName("StoreBrief")
        val storeBrief: String = "",
        @SerializedName("TermConditions")
        val termConditions: String = "",
        @SerializedName("ContactInfo")
        val contactInfo: String = "",
        @SerializedName("InfoUrl")
        val urlInfo: String = "",
        @SerializedName("ExpireDate")
        val expireDate: Int = 0,
        @SerializedName("Status")
        val status: Int = 0)

class DailyPrizeEntity(
        @SerializedName("Id")
        val id: Int,
        @SerializedName("Name")
        val title: String,
        @SerializedName("Title")
        val desc: String,
        @SerializedName("Image")
        val thumbUrl: String,
        @SerializedName("Type")
        val type: Int,
        @SerializedName("Limitted")
        val limited: Boolean = false,
        @SerializedName("Quantity")
        val quantity: Int = 0,
        @SerializedName("Amount")
        val amount: Int = 0,
        @SerializedName("GiftId")
        val giftId: Int = 0,
        @SerializedName("StoreBrief")
        val storeBrief: String = "",
        @SerializedName("TermConditions")
        val termConditions: String = "",
        @SerializedName("ContactInfo")
        val contactInfo: String = "",
        @SerializedName("InfoUrl")
        val urlInfo: String = "",
        @SerializedName("ExpireDate")
        val expireDate: Int = 0,
        @SerializedName("Status")
        val status: Int = 0)

class UserPrizeBagInfoEntity(
        @SerializedName("ItemCount")
        val prizeCount: Int)