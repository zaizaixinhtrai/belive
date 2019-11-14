package com.data.repository.datasource.cloud;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.BasePagingRequestModel;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.Constants;
import com.data.entity.LiveShowWalletEntity;
import com.data.entity.TotalCashEntity;
import com.data.entity.TransactionHistoryEntity;
import com.data.repository.datasource.TransactionDataSource;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

import static com.data.di.ApiServiceModule.APP_AUTHEN;

/**
 * Created by thanhbc on 10/25/17.
 */

public class CloudTransactionDataSource implements TransactionDataSource {
    private final AppsterWebserviceAPI mService;
    private final String mAuthen;

    @Inject
    public CloudTransactionDataSource(AppsterWebserviceAPI service, @Named(APP_AUTHEN) String authen) {
        mService = service;
        mAuthen = authen;
    }

    @Override
    public Observable<BaseResponse<BaseDataPagingResponseModel<TransactionHistoryEntity>>> getTransactions(int nextId, int startPage) {
        return mService.getTransactionHistory(mAuthen,new BasePagingRequestModel(nextId, Constants.PAGE_LIMITED));
    }

    @Override
    public Observable<BaseResponse<Boolean>> checkCashOutAvailable() {
        return mService.checkCashout(mAuthen);
    }

    @Override
    public Observable<BaseResponse<TotalCashEntity>> getTotalCashout() {
        return mService.getTotalCashout(mAuthen);
    }

    @Override
    public Observable<BaseResponse<LiveShowWalletEntity>> liveShowWallet(int walletGroup) {
        return mService.liveShowWallet(mAuthen, walletGroup);
    }
}
