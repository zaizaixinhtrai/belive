package com.appster.webservice.response

import com.data.entity.StreamsRecentEntity
import com.google.gson.annotations.SerializedName

/**
 * Created by Ngoc on 5/23/2018.
 */
class StreamsRecentResponse{
    @SerializedName("StreamsData")
    val streamsData: BaseDataPagingResponseModel<StreamsRecentEntity>? = null
}