package com.appster.features.mvpbase

import android.content.Context
import com.appster.utility.RxUtils
import com.apster.common.Constants

import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

/**
 * Created by thanhbc on 4/26/17.
 *
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mView that
 * can be accessed from the children classes by calling getView().
 */
open class BasePresenter<T : BaseContract.View> : BaseContract.Presenter<T> {

    var view: T? = null
        private set

    private val mCompositeSubscription = CompositeSubscription()
    private var subscribeScheduler: Scheduler? = null

    val isViewAttached: Boolean
        get() = view != null

    private val schedulersTransformer: Observable.Transformer<T, T> = object : Observable.Transformer<T, T> {
        override fun call(t: Observable<T>?): Observable<T> {
            return (t as Observable<T>).subscribeOn(defaultSubscribeScheduler())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(defaultSubscribeScheduler())
        }

    }

    override fun attachView(view: T) {
        this.view = view
    }

    override fun detachView() {
        view = null
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription)
    }

    fun checkViewAttached() {
        if (!isViewAttached) {
            throw MvpViewNotAttachedException()
        }
    }

    fun addSubscription(subscription: Subscription) {
        this.mCompositeSubscription.add(subscription)
    }

    class MvpViewNotAttachedException : RuntimeException("Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter")

    //Reusing Transformers - Singleton
    fun <T> applySchedulers(): Observable.Transformer<T, T> {
        return schedulersTransformer as Observable.Transformer<T, T>
    }

    fun defaultSubscribeScheduler(): Scheduler {
        if (subscribeScheduler == null) {
            subscribeScheduler = Schedulers.io()
        }
        return subscribeScheduler ?: Schedulers.io()
//        return subscribeScheduler
    }

    fun handleRetrofitError(error: Throwable) {
        Timber.e(error)
        view?.let {
            it.hideProgress()
            it.loadError(error.message, Constants.RETROFIT_ERROR)
        }
    }

    fun checkNotNull(`object`: Any?): Boolean {
        return `object` != null
    }

    fun getContext(): Context? {
        return view?.viewContext
    }
}
