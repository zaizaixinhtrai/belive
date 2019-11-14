package com.data.repository;

import com.appster.core.adapter.DisplayableItem;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.Constants;
import com.data.entity.mapper.TransactionEntityMapper;
import com.data.exceptions.BeLiveServerException;
import com.data.repository.datasource.TransactionDataSource;
import com.domain.models.BasePagingModel;
import com.domain.models.LiveShowWalletModel;
import com.domain.models.TotalCashoutModel;
import com.domain.repository.TransactionRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by thanhbc on 10/25/17.
 */


public class TransactionDataRepository implements TransactionRepository {

    final TransactionDataSource mCloudTransactionDataSource;
    TransactionEntityMapper mTransactionEntityMapper;

    @Inject
    public TransactionDataRepository(@Remote TransactionDataSource cloudTransactionDataSource) {
        mCloudTransactionDataSource = cloudTransactionDataSource;
        mTransactionEntityMapper = new TransactionEntityMapper();
    }

    @Override
    public Observable<BasePagingModel<DisplayableItem>> getTransactions(int nextId, int startPage) {
        return mCloudTransactionDataSource.getTransactions(nextId, startPage)
                .filter(baseDataPagingResponseModelBaseResponse -> baseDataPagingResponseModelBaseResponse != null)
                .flatMap(responseModelBaseResponse -> {
                    if (responseModelBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        return Observable.just(responseModelBaseResponse.getData());
                    } else {
                        return Observable.error(new BeLiveServerException(responseModelBaseResponse.getMessage(), responseModelBaseResponse.getCode()));
                    }
                })
                .map(this.mTransactionEntityMapper::transform);

    }

    @Override
    public Observable<BaseResponse<Boolean>> checkCashOutAvailable() {
        return mCloudTransactionDataSource.checkCashOutAvailable();
    }

    @Override
    public Observable<TotalCashoutModel> getTotalCash() {
        return mCloudTransactionDataSource.getTotalCashout()
                .filter(totalCashEntityBaseResponse -> totalCashEntityBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .flatMap(totalCashEntityBaseResponse -> Observable.just(totalCashEntityBaseResponse.getData()))
                .map(this.mTransactionEntityMapper::transform);
    }

    @Override
    public Observable<LiveShowWalletModel> liveShowWallet(int walletGroup) {
        return mCloudTransactionDataSource.liveShowWallet(walletGroup)
                .filter(liveShowWalletBaseResponse -> liveShowWalletBaseResponse != null)
                .flatMap(liveShowWalletBaseResponse -> {
                    if (liveShowWalletBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        return Observable.just(liveShowWalletBaseResponse);
                    } else {
                        return Observable.error(new BeLiveServerException(liveShowWalletBaseResponse.getMessage(), liveShowWalletBaseResponse.getCode()));
                    }
                })
                .map(this.mTransactionEntityMapper::transform);
    }
}
