package com.data.entity.requests

import com.google.gson.annotations.SerializedName

/**
 *  Created by DatTN on 11/1/2018
 */
class PickPrizeItemRequestEntity(
        @SerializedName("BoxId")
        val boxId: Int)