package com.data.entity.requests

import com.appster.webservice.request_models.BasePagingRequestModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Ngoc on 5/28/2018.
 */
class SearchUserRequestEntity: BasePagingRequestModel() {
    @SerializedName("DisplayName")
    var displayName: String? = null
}