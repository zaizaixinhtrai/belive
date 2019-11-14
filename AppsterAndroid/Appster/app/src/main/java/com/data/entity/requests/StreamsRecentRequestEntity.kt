package com.data.entity.requests

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngoc on 5/23/2018.
 */
class StreamsRecentRequestEntity(@SerializedName("NextId") var nextId: Int = 0, @SerializedName("Limit") var limit: Int = 0)