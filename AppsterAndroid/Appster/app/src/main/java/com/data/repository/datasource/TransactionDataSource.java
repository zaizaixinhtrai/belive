package com.data.repository.datasource;

import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.appster.webservice.response.BaseResponse;
import com.data.entity.LiveShowWalletEntity;
import com.data.entity.TotalCashEntity;
import com.data.entity.TransactionHistoryEntity;

import rx.Observable;

/**
 * Created by thanhbc on 10/25/17.
 */

public interface TransactionDataSource {
    Observable<BaseResponse<BaseDataPagingResponseModel<TransactionHistoryEntity>>> getTransactions(int nextId, int startPage);

    Observable<BaseResponse<Boolean>> checkCashOutAvailable();

    Observable<BaseResponse<TotalCashEntity>> getTotalCashout();

    Observable<BaseResponse<LiveShowWalletEntity>> liveShowWallet(int walletGroup);
}
