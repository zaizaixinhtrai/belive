package com.data.entity.requests

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngoc on 4/11/2018.
 */

class EditReferralCodeRequestEntity(
        @SerializedName("RefId") var refId: Int =0
)