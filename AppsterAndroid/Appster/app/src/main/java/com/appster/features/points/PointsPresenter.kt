package com.appster.features.points

import com.appster.AppsterApplication
import com.appster.core.adapter.DisplayableItem
import com.appster.features.mvpbase.BasePresenter
import com.apster.common.Constants
import com.data.exceptions.BeLiveServerException
import com.domain.interactors.dailybonus.TreatCollectUseCase
import com.domain.interactors.pointsystem.*

class PointsPresenter(private val mMysteryBoxesUseCase: GetMysteryBoxesUseCase,
                      private val mTreatPickUseCase: TreatCollectUseCase,
                      private val mUserPrizeCountUseCase: GetUserPrizeCountUseCase,
                      private val mDailyBonusCountDownUseCase: GetDailyBonusCountDownUseCase,
                      private val mOpenMysteryBoxUseCase: OpenMysteryBoxUseCase) : BasePresenter<PointsContract.View>(), PointsContract.UserActions {

    //region -------inheritance methods-------

    //endregion -------inheritance methods-------

    //region -------implement methods-------
    override fun loadMysteryBoxes(isShowLoading: Boolean) {
        if (isShowLoading)
            view?.showProgress()
        addSubscription(mMysteryBoxesUseCase.execute(null).subscribe({ boxData ->
            val list = mutableListOf<DisplayableItem>()
            boxData.dailyBonus?.let {
                list.add(it)
                view?.onDailyBonusCountUpdated(it.countDown)
            }
            boxData.mysteryBoxes?.let {
                list.addAll(it)
            }
            view?.apply {
                hideProgress()
                showMysteryBoxes(list)
            }
            boxData.creditsModel?.let {
                AppsterApplication.mAppPreferences.userModel.totalGold = it.total_gold
                AppsterApplication.mAppPreferences.userModel.totalBean = it.total_bean
                AppsterApplication.mAppPreferences.userModel.totalGoldFans = it.totalGoldFans
                AppsterApplication.mAppPreferences.userModel.points = it.totalPoint
            }
            onUserPointLoaded(boxData.userPoint, boxData.pointInfoUrl)
        }, { error ->
            view?.apply {
                hideProgress()
                if (error is BeLiveServerException) {
                    loadError(error.message, error.code)
                } else {
                    handleRetrofitError(error)
                }
            }
        }))
    }

    override fun openDailyBonus() {
        checkViewAttached()
        view?.showProgress()
        addSubscription(mTreatPickUseCase.execute("").subscribe({ result ->
            view?.apply {
                onDailyBonusCollected(result)
                hideProgress()
            }
            // after user success claim the daily bonus. We reset the count down
            loadDailyBonusCountDown()
        }, { error ->
            view?.apply {
                if (error is BeLiveServerException) {
                    if (error.code == 1333) loadError(error.message, error.code)
                } else {
                    handleRetrofitError(error)
                }
                hideProgress()
            }
            // user failed to claim the daily bonus. We still reset the count down
            loadDailyBonusCountDown()
        }))
    }

    override fun loadUserPrizeCount() {
        addSubscription(mUserPrizeCountUseCase.execute(null).subscribe({ count ->
            view?.onUserPrizeUpdated(count)
        }, {
            //            view?.onUserPrizeUpdated(0)
        }))
    }

    override fun loadDailyBonusCountDown() {
        addSubscription(mDailyBonusCountDownUseCase.execute(null).subscribe({ count ->
            view?.onDailyBonusCountUpdated(count)
        }, {
            view?.onDailyBonusCountUpdated(0)
        }))
    }

    override fun openMysteryBox(boxId: Int) {
        checkViewAttached()
        view?.showProgress()
        addSubscription(mOpenMysteryBoxUseCase.execute(boxId).subscribe({ prizeCollectModel ->

            view?.apply {
                hideProgress()
                onMysteryBoxOpened(prizeCollectModel)
            }
            onUserPointLoaded(prizeCollectModel.userPoint, null)
        }, {
            view?.apply {
                hideProgress()
                if (it is BeLiveServerException) {
                    if (it.code == 1406) {
                        view?.showNotEnoughtPointDialog()
                    } else {
                        loadError(it.message, it.code)
                    }
                } else {
                    loadError(it.message, Constants.RETROFIT_ERROR)
                }
            }
        }))
    }
    //endregion -------implement methods-------

    //region -------inner methods-------
    private fun onUserPointLoaded(point: Int, urlInfo: String?) {
        view?.onUserPointUpdated(point, urlInfo)
        AppsterApplication.mAppPreferences.userModel?.totalPoint = point
    }
    //endregion -------inner methods-------


    //region -------inner class-------
    //endregion -------inner class-------
}
