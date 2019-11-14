package com.domain.repository;

import com.appster.core.adapter.DisplayableItem;
import com.appster.webservice.response.BaseResponse;
import com.domain.models.BasePagingModel;
import com.domain.models.LiveShowWalletModel;
import com.domain.models.TotalCashoutModel;

import rx.Observable;

/**
 * Created by thanhbc on 10/25/17.
 */

public interface TransactionRepository {
    Observable<BasePagingModel<DisplayableItem>> getTransactions(int nextId, int startPage);

    Observable<BaseResponse<Boolean>> checkCashOutAvailable();

    Observable<TotalCashoutModel> getTotalCash();

    Observable<LiveShowWalletModel> liveShowWallet(int walletGroup);
}
