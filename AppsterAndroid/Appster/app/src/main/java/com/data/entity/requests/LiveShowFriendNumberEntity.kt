package com.data.entity.requests

import com.google.gson.annotations.SerializedName

data class LiveShowFriendNumberEntity(@field:SerializedName("Message")
                                      val message: String,
                                      @field:SerializedName("WaitingTimeSec")
                                      val waitingTimeSec: Long)
