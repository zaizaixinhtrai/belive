package com.data.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngoc on 6/14/2018.
 */
class TreatCollectEntity(
        @SerializedName("Id") val id: Int,
        @SerializedName("Title") val title: String,
        @SerializedName("Description") val description: String,
        @SerializedName("Image") val image: String,
        @SerializedName("Amount") val amount: Int,
        @SerializedName("TreatRank") val treatRank: Int
)