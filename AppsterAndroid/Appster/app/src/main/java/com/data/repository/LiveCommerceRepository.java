package com.data.repository;

import com.appster.models.ProductModel;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.cloud.CloudLiveCommerceDataSource;

import java.util.List;

import rx.Observable;

/**
 * Created by linh on 19/10/2017.
 */

public class LiveCommerceRepository {
    private final CloudLiveCommerceDataSource mCloudDataSource;

    public LiveCommerceRepository(CloudLiveCommerceDataSource cloudDataSource) {
        mCloudDataSource = cloudDataSource;
    }

    public Observable<BaseResponse<List<ProductModel>>> getProductsDetail(String sellerId) {
        return mCloudDataSource.getProductsDetail(sellerId);
    }
}
