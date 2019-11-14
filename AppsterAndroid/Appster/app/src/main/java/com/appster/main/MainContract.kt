package com.appster.main

import com.appster.features.mvpbase.BaseContract
import com.appster.models.BanUserMessage
import com.appster.models.ShareStreamModel

/**
 * Created by Ngoc on 5/17/2018.
 */
interface MainContract {
    interface MainView : BaseContract.View {
        fun onVisibleLiveIcon(isVisible: Boolean)
        fun onVisiblePointsDot(isVisible: Boolean)
        fun onReceivePoints(message: String?)
    }

    interface MainActions : BaseContract.Presenter<MainContract.MainView> {
        fun checkHasLiveVideo()
        fun userEarnPoints(shareStreamModel: ShareStreamModel?)
        fun loadDailyBonusCountDown()
    }
}
