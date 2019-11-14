package com.data.entity.mapper

import com.data.entity.EditReferralCodeEntity
import com.domain.models.EditReferralModel

/**
 * Created by Ngoc on 4/11/2018.
 */

class InviteFriendEntityMapper {
    fun transform(entityBaseResponse: EditReferralCodeEntity?): EditReferralModel? {
        return entityBaseResponse?.let {
            return EditReferralModel(it.receiverUserId,it.receiverUserId,it.isTriviaRequester,it.isTriviaReceiver)
        }
    }
}
