package com.appster.features.home

import android.app.Activity
import com.appster.location.GPSTClass
import com.appster.webservice.AppsterWebserviceAPI
import com.apster.common.Constants
import com.tbruyelle.rxpermissions.RxPermissions

/**
 * Created by thanhbc on 6/29/17.
 */

class NearByPresenter(service: AppsterWebserviceAPI, authen: String) : HomePresenter(service, authen) {

    private val gpstClass: GPSTClass
    private var mRxPermissions: RxPermissions? = null

    init {
        gpstClass = GPSTClass.getInstance()

    }

    override fun getStreamsByTag(type: Int, nextId: Int) {
        checkViewAttached()
        view?.showProgress()
        mRxPermissions = RxPermissions(view?.viewContext as Activity)
        mRxPermissions?.request(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)?.subscribe {
            val loc = gpstClass.getLocation(view?.viewContext)
            var lat = 0.0
            var lon = 0.0
            // check if GPS enabled
            if (loc != null) {
                lat = loc.latitude
                lon = loc.longitude
            }
            addSubscription(mService.getNearbyLive(mAuthen, lat, lon, nextId, Constants.PAGE_LIMITED)
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
        }?.let { addSubscription(it) }


    }
}
