package com.appster.main

import com.appster.AppsterApplication
import com.appster.base.ActivityScope
import com.appster.features.mvpbase.BasePresenter
import com.appster.models.ShareStreamModel
import com.appster.webservice.AppsterWebserviceAPI
import com.domain.interactors.main.CheckHasLiveVideoUseCase
import com.domain.interactors.pointsystem.EarnPointsUseCase
import com.domain.interactors.pointsystem.GetDailyBonusCountDownUseCase
import timber.log.Timber
import javax.inject.Inject
import com.appster.extensions.*

/**
 * Created by Ngoc on 5/17/2018.
 */
@ActivityScope
class MainPresenter @Inject constructor(private val mainView: MainContract.MainView,
                                        val services: AppsterWebserviceAPI,
                                        private val checkHasLiveVideoUseCase: CheckHasLiveVideoUseCase,
                                        private val getDailyBonusCountDownUseCase: GetDailyBonusCountDownUseCase,
                                        private val earnPointsUseCase: EarnPointsUseCase)
    : BasePresenter<MainContract.MainView>(), MainContract.MainActions {


    override fun attachView(view: MainContract.MainView?) {
    }

    override fun detachView() {
//        RxUtils.unsubscribeIfNotNull(compositeSubscription)
    }

    override fun checkHasLiveVideo() {
        addSubscription(checkHasLiveVideoUseCase.execute("")
                .subscribe({ dataResponse ->
                    dataResponse?.let {
                        mainView.onVisibleLiveIcon(dataResponse)
                    }
                })
                { error ->
                    Timber.e(error.message)
                })
    }

    override fun userEarnPoints(shareStreamModel: ShareStreamModel?) {
        shareStreamModel?.slug?.let {
            val actionType = shareStreamModel.actionType.isNullOrEmpty() then "Stream"
                    ?: shareStreamModel.actionType.toString()
            addSubscription(earnPointsUseCase.execute(EarnPointsUseCase.Params.load(actionType, it, shareStreamModel.mode))
                    .subscribe({ response ->
                        val userModel = AppsterApplication.mAppPreferences.userModel
                        userModel.points = response.userPoints
                        AppsterApplication.mAppPreferences.saveUserInforModel(userModel)
                        mainView.onReceivePoints(response.message)
                    }, { error -> Timber.e(error.message) }))
            AppsterApplication.mAppPreferences.saveShareStreamModel(null)
        }
    }

    override fun loadDailyBonusCountDown() {
        addSubscription(getDailyBonusCountDownUseCase.execute(Unit)
                .subscribe({ countDown ->
                    mainView.onVisiblePointsDot(countDown <= 0)
                },
                        { error ->
                            Timber.e(error.message)
                        }))
    }

}