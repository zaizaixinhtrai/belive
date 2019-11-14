package com.data.repository.datasource.cloud;

import com.appster.models.ProductModel;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.LiveCommerceDataSource;

import java.util.List;

import rx.Observable;

/**
 * Created by linh on 19/10/2017.
 */

public class CloudLiveCommerceDataSource implements LiveCommerceDataSource {
    private final AppsterWebserviceAPI mService;
    private final String mAuth;

    public CloudLiveCommerceDataSource(AppsterWebserviceAPI service, String auth) {
        mService = service;
        mAuth = auth;
    }

    @Override
    public Observable<BaseResponse<List<ProductModel>>> getProductsDetail(String sellerId) {
        return mService.getProductsDetail(mAuth, sellerId);
    }
}
