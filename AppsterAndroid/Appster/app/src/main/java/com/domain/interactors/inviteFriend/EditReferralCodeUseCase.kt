package com.domain.interactors.inviteFriend

import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.models.EditReferralModel
import com.domain.repository.InviteFriendRepository

import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Ngoc on 4/11/2018.
 */


class EditReferralCodeUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler, private val mDataRepository: InviteFriendRepository)
    : UseCase<EditReferralModel, EditReferralCodeUseCase.Params>(uiThread, executorThread) {
    override fun buildObservable(params: Params): Observable<EditReferralModel> {
        return mDataRepository.editReferralCode(params.referralCode)
    }

    class Params(var referralCode: Int = 0) {

        companion object {
            @JvmStatic
            fun byId(referralCode: Int): Params {
                return Params(referralCode)
            }
        }
    }

}
