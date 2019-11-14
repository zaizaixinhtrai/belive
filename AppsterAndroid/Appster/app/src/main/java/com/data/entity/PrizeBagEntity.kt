package com.data.entity

import com.google.gson.annotations.SerializedName

data class PrizeBagEntity(@field:SerializedName("Id")
                          val id: Int,
                          @field:SerializedName("PrizeItem")
                          val prizeItemEntity: PrizeItemEntity?,
                          @field:SerializedName("Name")
                          val name: String?,
                          @field:SerializedName("Email")
                          val email: String?,
                          @field:SerializedName("RedeemDate")
                          val redeemDate: Int,
                          @field:SerializedName("SentDate")
                          val sentDate: Int,
                          @field:SerializedName("Status")
                          val status: Int,
                          @field:SerializedName("Created")
                          val created: String?)

data class PrizeItemEntity(@field:SerializedName("Id")
                           val id: Int,
                           @field:SerializedName("Name")
                           val name: String?,
                           @field:SerializedName("Title")
                           val title: String?,
                           @field:SerializedName("Image")
                           val image: String?,
                           @field:SerializedName("Type")
                           val type: Int,
                           @field:SerializedName("Limitted")
                           val limited: Boolean,
                           @field:SerializedName("Quantity")
                           val quantity: Int,
                           @field:SerializedName("Amount")
                           val amount: Int,
                           @field:SerializedName("GiftId")
                           val giftId: Int,
                           @field:SerializedName("StoreBrief")
                           val storeBrief: String?,
                           @field:SerializedName("TermConditions")
                           val termConditions: String?,
                           @field:SerializedName("ContactInfo")
                           val contactInfo: String?,
                           @field:SerializedName("InfoUrl")
                           val infoUrl: String?,
                           @field:SerializedName("ExpireDate")
                           val expireDate: String?)