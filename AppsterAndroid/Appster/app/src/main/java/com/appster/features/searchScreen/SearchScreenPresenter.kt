package com.appster.features.searchScreen


import com.appster.features.mvpbase.BasePresenter
import com.apster.common.Constants
import com.domain.interactors.explore.GetExploreStreamUseCase

import javax.inject.Inject

/**
 * Created by ThanhBan on 9/16/2016.
 */


class SearchScreenPresenter @Inject
constructor(view: SearchScreenContract.SearchView, private val mGetExploreStreamUseCase: GetExploreStreamUseCase) : BasePresenter<SearchScreenContract.SearchView>(), SearchScreenContract.UserActions {

    private var nextId: Int = 0

    init {
        attachView(view)
    }

    override fun getStreamsRecent(isShowDialog: Boolean, isRefresh: Boolean) {
        if (isRefresh) {
            nextId = 0
        }
        addSubscription(mGetExploreStreamUseCase.execute(GetExploreStreamUseCase.Params.loadPage(nextId))
                .filter { _ -> view != null }
                .subscribe({ streamsRecentBaseResponse ->
                    if (isShowDialog) {
                        view?.hideProgress()
                    }

                    view?.displayStreamsRecent(streamsRecentBaseResponse.data, streamsRecentBaseResponse.isEnd)
                    nextId = streamsRecentBaseResponse.nextId
                    if(isRefresh) view?.scrollTopForForceRefresh()
                }, { error ->
                    view?.loadError(error.message, Constants.RETROFIT_ERROR)
                }))
    }


}
