package com.data.entity.requests

import com.google.gson.annotations.SerializedName


data class SubmitRedemptionEntity(
        @field:SerializedName("BagItemId")
        val bagItemId: Int,
        @field:SerializedName("Name")
        val name: String?,
        @field:SerializedName("Email")
        val email: String?
)