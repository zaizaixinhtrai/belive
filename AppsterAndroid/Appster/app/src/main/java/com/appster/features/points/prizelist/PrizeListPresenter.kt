package com.appster.features.points.prizelist

import android.util.Log
import com.appster.features.mvpbase.BasePresenter
import com.apster.common.Constants
import com.data.exceptions.BeLiveServerException
import com.domain.interactors.pointsystem.GetPrizeListUseCase
import java.lang.NullPointerException
import javax.inject.Inject

class PrizeListPresenter @Inject constructor(private val mPrizeListUseCase: GetPrizeListUseCase) : BasePresenter<PrizeListContract.View>(), PrizeListContract.UserActions {

    //region -------inheritance methods-------

    //endregion -------inheritance methods-------

    //region -------implement methods-------
    override fun loadPrizeList(boxType: Int, boxId: Int) {
        checkViewAttached()
        view?.showProgress()
        addSubscription(mPrizeListUseCase.execute(GetPrizeListUseCase.Param.createParam(boxType, boxId))
                .subscribe({
                    view?.apply {
                        hideProgress()
                        showPrizeList(it)
                    }
                }, {
                    view?.apply {
                        hideProgress()
                        val errorCode: Int = (it as? BeLiveServerException)?.code
                                ?: Constants.RETROFIT_ERROR
                        loadError(it.message, errorCode)
                    }
                }))
    }
    //endregion -------implement methods-------

    //region -------inner methods-------
    //endregion -------inner methods-------


    //region -------inner class-------
    //endregion -------inner class-------

}
