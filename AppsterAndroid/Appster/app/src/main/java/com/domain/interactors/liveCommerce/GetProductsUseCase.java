package com.domain.interactors.liveCommerce;

import com.appster.models.ProductModel;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.LiveCommerceRepository;
import com.domain.interactors.UseCase;

import java.util.List;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 19/10/2017.
 */

public class GetProductsUseCase extends UseCase<BaseResponse<List<ProductModel>>, String> {

    private LiveCommerceRepository mRepository;

    public GetProductsUseCase(Scheduler uiThread, Scheduler executorThread, LiveCommerceRepository repository) {
        super(uiThread, executorThread);
        this.mRepository = repository;
    }

    @Override
    public Observable<BaseResponse<List<ProductModel>>> buildObservable(String s) {
        return mRepository.getProductsDetail(s);
    }
}
