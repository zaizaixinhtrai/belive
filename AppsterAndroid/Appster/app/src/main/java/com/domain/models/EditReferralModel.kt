package com.domain.models

/**
 * Created by Ngoc on 4/11/2018.
 */
class EditReferralModel(
        val requesterUserId: Int = 0,
        val receiverUserId: Int = 0,
        val isTriviaRequester: Boolean = false,
        val isTriviaReceiver: Boolean = false
)