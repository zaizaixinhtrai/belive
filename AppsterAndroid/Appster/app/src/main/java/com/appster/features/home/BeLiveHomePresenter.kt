package com.appster.features.home

import android.util.SparseIntArray
import com.appster.core.adapter.DisplayableItem
import com.appster.features.mvpbase.BasePresenter
import com.appster.utility.RxUtils
import com.data.exceptions.BeLiveServerException
import com.domain.interactors.home.CheckLiveShowStatus
import com.domain.interactors.home.GetFriendNumberUseCase
import com.domain.interactors.home.GetLiveShowsUseCase
import com.domain.models.LiveShowFriendNumberModel
import com.domain.models.LiveShowLastModel
import com.domain.models.LiveShowModel
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by thanhbc on 5/18/18.
 */
class BeLiveHomePresenter @Inject constructor(private val getLiveShowsUseCase: GetLiveShowsUseCase,
                                              private val checkLiveShowStatus: CheckLiveShowStatus,
                                              private val getFriendNumberUseCase: GetFriendNumberUseCase,
                                              homeView: BeLiveHomeContract.View) : BasePresenter<BeLiveHomeContract.View>(), BeLiveHomeContract.UserActions {


    init {
        attachView(homeView)
    }

    private var liveShowList: MutableList<DisplayableItem> = mutableListOf()
    private val liveShowStatusMap = SparseIntArray()
    override fun getLiveShows() {
        view?.showProgress()
        addSubscription(getLiveShowsUseCase.execute(null)
                .subscribe({
                    liveShowList = it.toMutableList()
                    view?.displayLiveShows(it)
                    view?.checkVisibleShowStatus()
                    view?.hideProgress()
                    liveShowList.forEach {
                        when (it) {
                            is LiveShowModel -> liveShowStatusMap.put(it.showId, it.showStatus)
                            is LiveShowLastModel -> liveShowStatusMap.put(it.showId, it.showStatus)
                            else -> Unit
                        }
                    }
                }, { error ->
                    view?.apply {
                        if (error is BeLiveServerException) {
                            if (error.code == 1341) loadError(error.message, error.code)
                        } else {
                            handleRetrofitError(error)
                        }
                    }
                })
        )
    }

    override fun checkShowStatus(showId: Int) {
        addSubscription(checkLiveShowStatus.execute(CheckLiveShowStatus.Params.check(showId))
                .subscribe({ showstatus ->
                    if (liveShowStatusMap[showId] != 1 && showstatus.status == 1) {
                        getLiveShows()
                    } else {
                        val showItem = liveShowList.find { (it is LiveShowModel && it.showId == showId) || (it is LiveShowLastModel && it.showId == showId) }
                        showItem?.let {
                            liveShowStatusMap.put(showId, showstatus.status)
                            when (it) {
                                is LiveShowModel -> {
                                    val position = liveShowList.indexOf(showItem)
                                    val updatedItem = (liveShowList[position] as LiveShowModel).copy().apply {
                                        slug = showstatus.slug
                                        waitingTime = showstatus.waitingTime
                                        showStatus = showstatus.status
                                    }
                                    liveShowList.apply { this[position] = updatedItem }
                                }
                                is LiveShowLastModel -> {
                                    val position = liveShowList.indexOf(showItem)
                                    val updatedItem = (liveShowList[position] as LiveShowLastModel).copy().apply {
                                        slug = showstatus.slug
                                        waitingTime = showstatus.waitingTime
                                        showStatus = showstatus.status
                                    }
                                    liveShowList.apply { this[position] = updatedItem }
                                }
                                else -> Unit
                            }
                        }
                        view?.displayLiveShows(liveShowList)
                        when (showstatus.status) {
                            ShowStatus.WAITING, ShowStatus.STARTING, ShowStatus.PLAY, ShowStatus.WATCHING -> view?.checkShowWaitingTime(showId, showstatus.waitingTime, showstatus.status, showstatus.streamId)
                            else -> Unit
                        }
                    }
                }, this::handleRetrofitError))
    }

    override fun getFriendNumber(streamId: Int) {

        Timber.e("streamId %s", streamId)
        getFriendNumberUseCase.execute(GetFriendNumberUseCase.Params.get(streamId))
                .subscribe({
                    view?.apply { displayFriendNumber(it, streamId) }
                }, {
                    Timber.e(it)
                    Timber.e("ERROR %s", streamId)
                })

    }
}
