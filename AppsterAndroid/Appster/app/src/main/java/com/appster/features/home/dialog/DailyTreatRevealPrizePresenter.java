package com.appster.features.home.dialog;

import com.appster.features.mvpbase.BasePresenter;
import com.appster.utility.AppsterUtility;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.data.repository.DailyBonusDataRepository;
import com.data.repository.datasource.DailyBonusDataSource;
import com.data.repository.datasource.cloud.CloudDailyBonusDataSource;
import com.domain.interactors.dailybonus.DailyBonusInformationUseCase;
import com.domain.repository.DailyBonusRepository;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


/**
 * Created by linh on 09/11/2017.
 */

public class DailyTreatRevealPrizePresenter extends BasePresenter<DailyTreatRevealPrizeContract.View> implements DailyTreatRevealPrizeContract.UserActions {
    private final DailyBonusInformationUseCase mAppConfigUseCase;

    public DailyTreatRevealPrizePresenter() {
        Scheduler ioThread = Schedulers.io();
        Scheduler uiThread = AndroidSchedulers.mainThread();
        AppsterWebserviceAPI service = AppsterWebServices.get();
        DailyBonusDataSource appConfigDataSource = new CloudDailyBonusDataSource(service,AppsterUtility.getAuth());
        DailyBonusRepository repository = new DailyBonusDataRepository(appConfigDataSource);
        mAppConfigUseCase = new DailyBonusInformationUseCase(uiThread, ioThread, repository);
    }
//
//    @Override
//    public void getAppConfig() {
//        addSubscription(mAppConfigUseCase.execute(null)
//                .subscribe(appConfigModelBaseResponse -> getView().onGetAppConfigSuccessfully(appConfigModelBaseResponse.getData().nextTimeSeconds), Timber::e));
//    }
}
