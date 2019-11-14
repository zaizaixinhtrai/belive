package com.appster.features.income.cashout;

import com.appster.domain.CashItemModel;
import com.appster.domain.WithdrawnAccountItemModel;
import com.appster.features.mvpbase.BaseContract;

import java.util.ArrayList;

/**
 * Created by thanhbc on 2/13/17.
 */

interface CashoutContract extends BaseContract {
    interface CashoutView extends BaseContract.View{
        void displayPaymentRates(ArrayList<CashItemModel> cashItemModels);
        void autoFillPaymentAccount(WithdrawnAccountItemModel accountItemModel);
        void cashoutAvailable(String amountStars, String amountMoney, String paymentUrl);
        void onWithdrawnSuccessfully(String amountMoney);
        void displayUserCredit(String currentCredit);
        void onCashoutAvailableResult(boolean isAbleToCashOut);
    }

    interface UserAction extends BaseContract.Presenter<CashoutView>{
        void checkCashoutAvailable();
        void getPaymentRates();
        void getAccountLists();
        void withdrawn(String email, String firstName, String lastName, String mobile);
        void cashout(CashItemModel cashItemModel);
    }
}
