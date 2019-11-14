package com.data.entity.requests

import com.google.gson.annotations.SerializedName

data class EarnPointsRequestEntity(@field:SerializedName("ActionType")
                                   val actionType: String?,
                                   @field:SerializedName("Slug")
                                   val slug: String?,
                                   @field:SerializedName("Mode")
                                   val mode: Int?)