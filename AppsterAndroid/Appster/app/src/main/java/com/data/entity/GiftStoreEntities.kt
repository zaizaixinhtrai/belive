package com.data.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by thanhbc on 3/27/18.
 */
data class GiftStoreEntity(
        @SerializedName("TotalBean") val totalGem: Int,
        @SerializedName("TotalGold") val totalGold: Int,
        @SerializedName("Gifts") val giftItems: List<GiftStoreItemEntity>)


data class GiftStoreItemEntity(
        @SerializedName("CategoryId") var categoryId: String? = null,
        @SerializedName("GiftId") var giftId: String? = null,
        @SerializedName("GiftImage") var giftImage: String = "",
        @SerializedName("Amount") var amount: Int = 0,
        @SerializedName("CategoryName") var categoryName: String? = null,
        @SerializedName("CostGold") var costGold: Int = 0,
        @SerializedName("GiftName") var giftName: String? = null,
        @SerializedName("CostBean") var costBean: Int = 0,
        @SerializedName("GiftType") var type: Int = 0
)