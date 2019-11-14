package com.appster.features.home

import com.appster.core.adapter.DisplayableItem
import com.appster.features.mvpbase.BaseContract
import com.domain.models.LiveShowFriendNumberModel

/**
 * Created by thanhbc on 5/18/18.
 */

interface BeLiveHomeContract {
    interface View : BaseContract.View {
        fun displayLiveShows(shows: List<DisplayableItem>)
        fun checkShowWaitingTime(showId: Int, waitingTime: Int, showStatus: Int, streamId: Int?)
        fun checkVisibleShowStatus()
        fun displayFriendNumber(model: LiveShowFriendNumberModel, streamId: Int)
    }

    interface UserActions : BaseContract.Presenter<BeLiveHomeContract.View> {
        fun getLiveShows()
        fun checkShowStatus(showId: Int)
        fun getFriendNumber(streamId: Int)
    }
}
