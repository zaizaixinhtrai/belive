package com.appster.features.home.dailybonus.treatmachine

import com.appster.features.mvpbase.BasePresenter
import com.appster.utility.AppsterUtility
import com.appster.webservice.AppsterWebserviceAPI
import com.apster.common.Constants
import com.data.exceptions.BeLiveServerException
import com.data.repository.DailyBonusDataRepository
import com.data.repository.datasource.cloud.CloudDailyBonusDataSource
import com.domain.interactors.dailybonus.DailyBonusCheckDaysUseCase
import com.domain.interactors.dailybonus.TreatCollectUseCase
import com.domain.interactors.dailybonus.UpdateDailyBonusShowedUseCase
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by thanhbc on 11/9/17.
 */

class DailyTreatPresenter(service: AppsterWebserviceAPI) : BasePresenter<DailyTreatContract.View>(), DailyTreatContract.UserActions {
    private val mTreatPickUseCase: TreatCollectUseCase
    private val mDailyBonusShowedUseCase: UpdateDailyBonusShowedUseCase
    private val dailyBonusCheckDaysUseCase: DailyBonusCheckDaysUseCase

    init {
        val uiThread = AndroidSchedulers.mainThread()
        val ioThread = Schedulers.io()
        val appConfigDataSource = CloudDailyBonusDataSource(service, AppsterUtility.getAuth())
        val repository = DailyBonusDataRepository(appConfigDataSource)
        mTreatPickUseCase = TreatCollectUseCase(repository, uiThread, ioThread)
        mDailyBonusShowedUseCase = UpdateDailyBonusShowedUseCase(repository, uiThread, ioThread)
        dailyBonusCheckDaysUseCase = DailyBonusCheckDaysUseCase(uiThread, ioThread, repository)
    }

    override fun checkDays() {
        checkViewAttached()
        updateDailyBonusShowed()
        addSubscription(dailyBonusCheckDaysUseCase.execute("")
                .subscribe({ response ->
                    view?.apply {
                        onCheckDayOrderResult(response)
                    }
                }, { error -> view?.apply { handleRetrofitError(error) } }))
    }

    override fun collect() {
        checkViewAttached()
        view?.showProgress()
        addSubscription(mTreatPickUseCase.execute("")
                .subscribe({ result ->
                    view?.apply {
                        onTreatCollectResult(result)
                        hideProgress()
                    }
                }, { error ->
                    view?.apply {
                        if (error is BeLiveServerException) {
                            if (error.code == 1333) loadError(error.message, error.code)
                        } else {
                            handleRetrofitError(error)
                        }
                        hideProgress()
                    }
                }))
    }

    private fun updateDailyBonusShowed() {
        addSubscription(mDailyBonusShowedUseCase.execute(null).subscribe({ }, { Timber.e(it) }))
    }
}
