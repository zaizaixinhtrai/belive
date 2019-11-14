package com.data.entity.requests

import com.google.gson.annotations.SerializedName

data class NotificationListRequestEntity(@field:SerializedName("NotificationStatus")
                                         val notificationStatus: Int?,
                                         @field:SerializedName("NextId")
                                         val nextIndex: Int,
                                         @field:SerializedName("Limit")
                                         val pageLimit:Int)
