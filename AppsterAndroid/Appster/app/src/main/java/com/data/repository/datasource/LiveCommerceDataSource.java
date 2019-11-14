package com.data.repository.datasource;

import com.appster.models.ProductModel;
import com.appster.webservice.response.BaseResponse;

import java.util.List;

import rx.Observable;

/**
 * Created by linh on 19/10/2017.
 */

public interface LiveCommerceDataSource {
    Observable<BaseResponse<List<ProductModel>>> getProductsDetail(String mSellerId);
}
