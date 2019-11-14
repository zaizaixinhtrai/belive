package com.appster.features.stream.dialog;

import com.appster.features.mvpbase.BasePresenter;
import com.appster.webservice.AppsterWebserviceAPI;
import com.apster.common.Constants;
import com.data.repository.LiveCommerceRepository;
import com.data.repository.datasource.cloud.CloudLiveCommerceDataSource;
import com.domain.interactors.liveCommerce.GetProductsUseCase;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by linh on 20/10/2017.
 */

public class LiveShopDialogPresenter extends BasePresenter<LiveShopDialogContract.View> implements LiveShopDialogContract.UserActions{

    private final AppsterWebserviceAPI mService;
    private final String mAuth;
    private GetProductsUseCase mGetProductsUseCase;

    public LiveShopDialogPresenter(AppsterWebserviceAPI service, String auth) {
        mService = service;
        mAuth = auth;
        final Scheduler uiThread = AndroidSchedulers.mainThread();
        final Scheduler io = Schedulers.io();
        CloudLiveCommerceDataSource liveCommerceDataSource = new CloudLiveCommerceDataSource(mService, mAuth);
        LiveCommerceRepository liveCommerceRepository = new LiveCommerceRepository(liveCommerceDataSource);
        mGetProductsUseCase = new GetProductsUseCase(uiThread, io, liveCommerceRepository);
    }

    @Override
    public void getProductDetail(String sellerId) {
        addSubscription(mGetProductsUseCase.execute(sellerId)
                .subscribe(baseResponse -> {
                    if (baseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        getView().onGetProductDetailSuccessfully(baseResponse.getData());
                    } else {
                        getView().onGetProductDetailFailed(baseResponse.getMessage());
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    getView().onGetProductDetailFailed(throwable.getMessage());
                }));
    }
}
