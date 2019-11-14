package com.appster.features.home

import com.appster.features.mvpbase.BasePresenter
import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.request_models.PopularByTagRequestModel
import com.apster.common.Constants

/**
 * Created by thanhbc on 6/28/17.
 */

open class HomePresenter(protected val mService: AppsterWebserviceAPI, protected val mAuthen: String) : BasePresenter<HomeContract.View>(), HomeContract.UserActions {

    override fun getCategoriesByTag(type: Int) {
        checkViewAttached()
        view?.showProgress()
        addSubscription(mService.getTagListLiveStream(mAuthen)
                .filter { isViewAttached && view?.isRunning ?: false }
                .subscribe({ listBaseResponse ->
                    with(listBaseResponse) {
                        view?.let {
                            it.hideProgress()
                            when (code) {
                                Constants.RESPONSE_FROM_WEB_SERVICE_OK -> it.onCategoriesReceived(data)
                                else -> it.loadError(message, code)
                            }
                        }
                    }

                }, this::handleRetrofitError))
    }

    override fun getEventsByTag(type: Int) {
        checkViewAttached()
        view?.showProgress()
        addSubscription(mService.getCurrentEvent(mAuthen, type)
                .filter { isViewAttached && view?.isRunning ?: false }
                .subscribe({ listBaseResponse ->
                    with(listBaseResponse) {
                        view?.let {
                            it.hideProgress()
                            when (code) {
                                Constants.RESPONSE_FROM_WEB_SERVICE_OK -> it.onEventsReceived(data)
                                else -> it.loadError(message, code)
                            }
                        }
                    }

                }, this::handleRetrofitError)
        )
    }

    override fun getStreamsByTag(type: Int, nextId: Int) {
        checkViewAttached()
        view?.showProgress()
        val request = PopularByTagRequestModel()
        request.tagId = type
        request.nextId = nextId
        addSubscription(mService.getPopularByTag(mAuthen, request)
                .filter { isViewAttached && view?.isRunning ?: false }
                .doOnError { view?.refreshCompleted() }
                .subscribe({ listBaseResponse ->
                    with(listBaseResponse) {
                        view?.let {
                            it.hideProgress()
                            it.refreshCompleted()
                            when (code) {
                                Constants.RESPONSE_FROM_WEB_SERVICE_OK -> it.onStreamsReceived(data.result, data.nextId, data.isEnd)
                                else -> it.loadError(message, code)
                            }
                        }
                    }
                }, this::handleRetrofitError)
        )
    }

}
