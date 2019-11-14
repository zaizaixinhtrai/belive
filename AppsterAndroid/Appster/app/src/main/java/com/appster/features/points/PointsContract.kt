package com.appster.features.points

import com.appster.core.adapter.DisplayableItem
import com.appster.features.mvpbase.BaseContract
import com.domain.models.PrizeCollectModel
import com.domain.models.TreatCollectModel

interface PointsContract {

    interface View : BaseContract.View {
        fun showMysteryBoxes(mysteryBoxes: List<DisplayableItem>)

        fun onDailyBonusCountUpdated(countDown: Int)
        fun onUserPrizeUpdated(numOfPrize: Int)
        fun onUserPointUpdated(numOfPoint: Int, infoUrl: String?)
        fun onDailyBonusCollected(model: TreatCollectModel)
        fun onMysteryBoxOpened(model: PrizeCollectModel)
        fun showNotEnoughtPointDialog()
    }

    interface UserActions : BaseContract.Presenter<PointsContract.View> {
        fun loadMysteryBoxes(isShowLoading: Boolean)
        fun loadUserPrizeCount()
        fun openDailyBonus()
        fun loadDailyBonusCountDown()
        fun openMysteryBox(boxId: Int)
    }
}
