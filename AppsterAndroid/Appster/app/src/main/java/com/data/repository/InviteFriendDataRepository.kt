package com.data.repository

import com.apster.common.Constants
import com.data.entity.mapper.InviteFriendEntityMapper
import com.data.exceptions.BeLiveServerException
import com.data.repository.datasource.InviteFriendDataSource
import com.domain.models.EditReferralModel
import com.domain.repository.InviteFriendRepository
import rx.Observable
import javax.inject.Inject

/**
 * Created by Ngoc on 4/11/2018.
 */

class InviteFriendDataRepository @Inject constructor(@Remote internal val mInviteFriendDataSource: InviteFriendDataSource) : InviteFriendRepository {
    internal val mInviteFriendEntityMapper: InviteFriendEntityMapper = InviteFriendEntityMapper()

    override fun editReferralCode(refId: Int): Observable<EditReferralModel> {
        return mInviteFriendDataSource.editReferralCode(refId)
                .flatMap { t ->
                    when (t.getCode()) {
                        Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(mInviteFriendEntityMapper.transform(t.data))
                        else -> Observable.error(BeLiveServerException(t.message, t.code))
                    }
                }
    }
}

