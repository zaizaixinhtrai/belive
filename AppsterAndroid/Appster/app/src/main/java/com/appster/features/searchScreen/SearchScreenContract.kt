package com.appster.features.searchScreen

import com.appster.core.adapter.DisplayableItem
import com.appster.features.mvpbase.BaseContract

/**
 * Created by ThanhBan on 9/14/2016.
 */
interface SearchScreenContract {

    interface SearchView : BaseContract.View {

        fun displayStreamsRecent(streamsRecent: List<DisplayableItem>, isEndedList: Boolean)
        fun scrollTopForForceRefresh()
    }

    interface UserActions : BaseContract.Presenter<SearchView> {

        fun getStreamsRecent(isShowDialog: Boolean, isRefresh: Boolean)
    }
}
