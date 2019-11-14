package com.data.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngoc on 6/8/2018.
 */
class DailyBonusCheckDaysEntity(
        @SerializedName("DayType") val dayType: Int,
        @SerializedName("Claimed") val claimed: Boolean)