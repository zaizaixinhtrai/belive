package com.data.entity

import com.google.gson.annotations.SerializedName

data class EarnPointsEntity(@field:SerializedName("UserPoint")
                            val userPoints: Int,
                            @field:SerializedName("PointInfoURL")
                            val PointInfoURL: String?,
                            @field:SerializedName("Message")
                            val message:String?)
