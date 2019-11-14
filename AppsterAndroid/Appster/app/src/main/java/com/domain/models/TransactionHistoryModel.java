package com.domain.models;

import androidx.annotation.IntDef;

import com.appster.core.adapter.DisplayableItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by thanhbc on 10/24/17.
 */

public class TransactionHistoryModel implements DisplayableItem {

    public String transactionTime = "17/10/2017T07:53:57.000Z";
    public int starsDeducted = 14320;
    public double convertedValue = 50;
    public String currency = ":gem:";
    public boolean isTriviaCashOut;

    @TransactionType
    public int type = TransactionType.GEMS;

    @TransactionStatus
    public int status = TransactionStatus.ACCEPTED;


    @IntDef({TransactionType.CASH, TransactionType.GEMS, TransactionType.MASTER_BRAIN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TransactionType {
        int GEMS = 0;
        int CASH = 1;
        int MASTER_BRAIN = -1;
    }

    @IntDef({TransactionStatus.ACCEPTED, TransactionStatus.PENDING, TransactionStatus.REJECTED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TransactionStatus {
        int ACCEPTED = 1;
        int PENDING = 0;
        int REJECTED = -1;
    }
}
