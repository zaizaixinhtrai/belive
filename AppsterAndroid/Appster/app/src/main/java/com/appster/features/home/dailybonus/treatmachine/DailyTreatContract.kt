package com.appster.features.home.dailybonus.treatmachine

import com.appster.features.mvpbase.BaseContract
import com.domain.models.DailyBonusCheckDaysModel
import com.domain.models.TreatCollectModel

/**
 * Created by thanhbc on 11/9/17.
 */

interface DailyTreatContract {
    interface View : BaseContract.View {
        fun onTreatCollectResult(collectModel: TreatCollectModel?)
        fun onCheckDayOrderResult(dailyBonusCheckDaysModel: DailyBonusCheckDaysModel?)
    }

    interface UserActions : BaseContract.Presenter<DailyTreatContract.View> {
        fun checkDays()
        fun collect()
    }
}
