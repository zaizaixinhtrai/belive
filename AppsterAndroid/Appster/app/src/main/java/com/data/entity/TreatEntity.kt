package com.data.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by thanhbc on 11/13/17.
 */

data class TreatEntity(@field:SerializedName("Claimed")
                       val claimed: Boolean = false,
                       @field:SerializedName("Value")
                       val value: String? = null,
                       @field:SerializedName("Id")
                       val id: Int = 0,
                       @field:SerializedName("Title")
                       val title: String? = null,
                       @field:SerializedName("Name")
                       val description: String? = null,
                       @field:SerializedName("Image")
                       val image: String? = null,
                       @field:SerializedName("Position")
                       val position: Int = 0,
                       @field:SerializedName("TreatColor")
                       val treatColor: Int = 0,
                       @field:SerializedName("TreatRank")
                       val treatRank: Int = 0,
                       @field:SerializedName("Amount")
                       val amount: Int = 0
)
