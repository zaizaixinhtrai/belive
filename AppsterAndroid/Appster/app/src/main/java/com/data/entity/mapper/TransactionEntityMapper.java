package com.data.entity.mapper;

import com.appster.core.adapter.DisplayableItem;
import com.appster.webservice.response.BaseDataPagingResponseModel;
import com.appster.webservice.response.BaseResponse;
import com.data.entity.LiveShowWalletEntity;
import com.data.entity.TotalCashEntity;
import com.data.entity.TransactionHistoryEntity;
import com.domain.models.BasePagingModel;
import com.domain.models.LiveShowWalletModel;
import com.domain.models.TotalCashoutModel;
import com.domain.models.TransactionHistoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanhbc on 10/26/17.
 */

public class TransactionEntityMapper {
    public TotalCashoutModel transform(TotalCashEntity totalCashEntity) {
        TotalCashoutModel totalCashoutModel = null;
        if (totalCashEntity != null) {
            totalCashoutModel = new TotalCashoutModel(totalCashEntity.totalCash, totalCashEntity.currency);
        }
        return totalCashoutModel;
    }

    public BasePagingModel<DisplayableItem> transform(BaseDataPagingResponseModel<TransactionHistoryEntity> transactionHistory) {
        BasePagingModel<DisplayableItem> transactions = null;
        if (transactionHistory != null) {
            transactions = new BasePagingModel<>();
            transactions.isEnd = transactionHistory.isEnd();
            transactions.nextId = transactionHistory.getNextId();
            transactions.totalRecords = transactionHistory.getTotalRecords();
            List<TransactionHistoryEntity> tempList = transactionHistory.getResult();
            final List<DisplayableItem> transactionList = new ArrayList<>();
            for (TransactionHistoryEntity transactionHistoryEntity : tempList) {
                transactionList.add(transform(transactionHistoryEntity));
            }
            transactions.data = transactionList;
        }
        return transactions;
    }

    private DisplayableItem transform(TransactionHistoryEntity entity) {
        TransactionHistoryModel historyModel = null;
        if (entity != null) {
            historyModel = new TransactionHistoryModel();
            historyModel.status = entity.status;
            historyModel.convertedValue = entity.convertedValue;
            historyModel.currency = entity.currency;
            historyModel.starsDeducted = entity.starsDeducted;
            historyModel.transactionTime = entity.transactionTime;
            historyModel.isTriviaCashOut = entity.isTriviaCashOut;
            historyModel.type = entity.isTriviaCashOut ? TransactionHistoryModel.TransactionType.MASTER_BRAIN :
                    returnTypeByCurrency(entity.currency);
        }
        return historyModel;
    }

    private int returnTypeByCurrency(String currency) {
        return ":gem:".equals(currency) ? TransactionHistoryModel.TransactionType.GEMS : TransactionHistoryModel.TransactionType.CASH;
    }

    public LiveShowWalletModel transform(BaseResponse<LiveShowWalletEntity> entity) {

        LiveShowWalletModel liveShowWalletModel = null;

        if (entity != null && entity.getData() != null)
            liveShowWalletModel = new LiveShowWalletModel(entity.getData().getCashoutUrl(),
                    entity.getData().getAmount(),
                    entity.getData().getMessage(),
                    entity.getData().getWithDrawable());

        return liveShowWalletModel;
    }
}
