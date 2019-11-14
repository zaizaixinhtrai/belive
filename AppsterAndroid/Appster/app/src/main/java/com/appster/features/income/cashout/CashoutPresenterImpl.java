package com.appster.features.income.cashout;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.domain.CashItemModel;
import com.appster.pocket.CreditsModel;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.CreditsRequestModel;
import com.appster.webservice.request_models.WithdrawnRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.Constants;
import com.data.repository.TransactionDataRepository;
import com.data.repository.datasource.cloud.CloudTransactionDataSource;
import com.domain.interactors.transaction_history.CheckCashoutTransactionUseCase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.appster.features.income.cashout.CashoutActivity.NOT_ENOUGH_STARS;

/**
 * Created by thanhbc on 2/13/17.
 */

class CashoutPresenterImpl implements CashoutContract.UserAction {

    private AppsterWebserviceAPI mService;
    private CashoutContract.CashoutView mView;
    private String mAuthen;
    private CompositeSubscription mCompositeSubscription;
    private long mCurrentStars;
    private CashItemModel mCurrentCashItemModel;
    CheckCashoutTransactionUseCase mCashoutTransactionUseCase;

    CashoutPresenterImpl(CashoutContract.CashoutView cashoutView, AppsterWebserviceAPI service) {
        this.mService = service;
        mAuthen = "Bearer " + AppsterApplication.mAppPreferences.getUserToken();
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        mCashoutTransactionUseCase = new CheckCashoutTransactionUseCase(new TransactionDataRepository(new CloudTransactionDataSource(mService, mAuthen)), AndroidSchedulers.mainThread(), Schedulers.io());
        attachView(cashoutView);
    }

    @Override
    public void checkCashoutAvailable() {
        mCompositeSubscription.add(mCashoutTransactionUseCase.execute(null)
                .filter(booleanBaseResponse -> mView != null)
                .subscribe(booleanBaseResponse -> mView.onCashoutAvailableResult(booleanBaseResponse.getData()), Timber::e));
    }

    @Override
    public void getPaymentRates() {
        mCompositeSubscription.add(getCredits()
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(creditsModel -> {
                    mCurrentStars = creditsModel.getTotal_gold();
                    AppsterApplication.mAppPreferences.getUserModel().setTotalGold(mCurrentStars);
                    AppsterApplication.mAppPreferences.getUserModel().setTotalBean(creditsModel.getTotal_bean());
                    mView.displayUserCredit(String.valueOf(mCurrentStars));
                    checkCashoutAvailable();
                    return paymentRates();
                }).subscribe(cashItemModels -> {
                    mView.displayPaymentRates(cashItemModels);
                }, error -> {
                    mView.loadError(error.getMessage(), Constants.RETROFIT_ERROR);
                    Timber.e(error.getMessage());
                }));
    }

    private Observable<ArrayList<CashItemModel>> paymentRates() {
        return mService.getPaymentRates(mAuthen)
                .map(BaseResponse::getData);
    }

    private Observable<CreditsModel> getCredits() {
        return mService.getUserCredits(mAuthen, new CreditsRequestModel())
                .flatMap(creditsDataResponse -> Observable.just(creditsDataResponse.getData()));
    }

    @Override
    public void getAccountLists() {
//        mView.showProgress();
        mCompositeSubscription.add(mService.getAccountList(mAuthen)
                .flatMap(withdrawnAccountResponse -> {
//                            mView.hideProgress();
                    return Observable.from(withdrawnAccountResponse.getData());
                })
                .filter(accountItemModel -> accountItemModel.currentAccount)
                .subscribe(accountItemModel -> {
//                            mView.hideProgress();
                            mView.autoFillPaymentAccount(accountItemModel);
                        }, error -> {
//                            mView.hideProgress();
                            mView.loadError(error.getMessage(), Constants.RETROFIT_ERROR);
                            Timber.e(error.getMessage());
                        }
                ));
    }

    @Override
    public void withdrawn(String email, String firstName, String lastName, String mobile) {
        mView.showProgress();
        WithdrawnRequestModel requestModel = new WithdrawnRequestModel();
        requestModel.setEmail(email);
        requestModel.setFirstName(firstName);
        requestModel.setLastName(lastName);
        requestModel.setMobile(mobile);
        requestModel.setPaymentExchangeRateId(mCurrentCashItemModel.paymentExchangeRateId);
        mCompositeSubscription.add(mService.withdrawn(mAuthen, requestModel)
                .subscribe(accountItemModel -> {
                            if (accountItemModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                mView.onWithdrawnSuccessfully(String.format("%s %s", mCurrentCashItemModel.currentcy, mCurrentCashItemModel.money));
                            } else {
                                mView.loadError(accountItemModel.getMessage(), accountItemModel.getCode());
                            }
                            mView.hideProgress();
                        }
                        , error -> {
                            mView.hideProgress();
                            mView.loadError(error.getMessage(), Constants.RETROFIT_ERROR);
                            Timber.e(error.getMessage());
                        }
                ));
    }

    @Override
    public void cashout(CashItemModel cashItemModel) {
        mCurrentCashItemModel = cashItemModel;
        if (isEnoughStars(cashItemModel.gold)) {
            mView.cashoutAvailable(String.format(Locale.US,"%,d",cashItemModel.gold), String.format(Locale.US,"%s %,d", cashItemModel.currentcy, cashItemModel.money),cashItemModel.paymentUrl);
        } else {
            mView.loadError(mView.getViewContext().getString(R.string.not_enought_stars), NOT_ENOUGH_STARS);
        }
    }

    private boolean isEnoughStars(int gold) {
        return mCurrentStars >= gold;
    }

    @Override
    public void attachView(CashoutContract.CashoutView view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }
}
