package com.appster.features.home

import com.appster.webservice.AppsterWebserviceAPI
import com.apster.common.Constants

/**
 * Created by thanhbc on 6/29/17.
 */

class LatestLivePresenter(service: AppsterWebserviceAPI, authen: String) : HomePresenter(service, authen) {

    override fun getStreamsByTag(type: Int, nextId: Int) {
        checkViewAttached()
        view?.showProgress()
        addSubscription(mService.getLatestLive(mAuthen, nextId, Constants.PAGE_LIMITED)
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
