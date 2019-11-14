package com.domain.repository

import com.appster.webservice.response.BaseResponse
import com.domain.models.EditReferralModel
import rx.Observable

/**
 * Created by Ngoc on 4/11/2018.
 */
interface InviteFriendRepository {
    fun editReferralCode(refId: Int): Observable<EditReferralModel>
}