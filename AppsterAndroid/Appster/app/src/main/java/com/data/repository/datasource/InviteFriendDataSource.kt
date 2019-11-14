package com.data.repository.datasource

import com.appster.webservice.response.BaseResponse
import com.data.entity.EditReferralCodeEntity
import rx.Observable

/**
 * Created by Ngoc on 4/11/2018.
 */

interface InviteFriendDataSource{
    fun editReferralCode(refId: Int): Observable<BaseResponse<EditReferralCodeEntity>>
}
