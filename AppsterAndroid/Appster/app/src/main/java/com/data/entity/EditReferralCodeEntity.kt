package com.data.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngoc on 4/11/2018.
 */

data class EditReferralCodeEntity(

        @SerializedName("RequesterUserId") val requesterUserId: Int ,
        @SerializedName("ReceiverUserId") val receiverUserId: Int ,
        @SerializedName("IsTriviaRequester") val isTriviaRequester: Boolean ,
        @SerializedName("IsTriviaReceiver") val isTriviaReceiver: Boolean

)


