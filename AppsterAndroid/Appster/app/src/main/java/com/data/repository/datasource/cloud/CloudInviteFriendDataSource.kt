package com.data.repository.datasource.cloud

import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.response.BaseResponse
import com.data.di.ApiServiceModule
import com.data.entity.EditReferralCodeEntity
import com.data.entity.requests.EditReferralCodeRequestEntity
import com.data.repository.datasource.InviteFriendDataSource

import rx.Observable
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Ngoc on 4/11/2018.
 */

class CloudInviteFriendDataSource @Inject constructor(private val mService: AppsterWebserviceAPI, @Named(ApiServiceModule.APP_AUTHEN) private val mAuthen: String) : InviteFriendDataSource {

    override fun editReferralCode(refId: Int): Observable<BaseResponse<EditReferralCodeEntity>> {
        return mService.editRefCode(mAuthen, EditReferralCodeRequestEntity(refId))
    }
}
