package com.appster.features.home.dailybonus.treatlist;

import com.appster.core.adapter.DisplayableItem;
import com.appster.features.mvpbase.BasePresenter;
import com.appster.utility.AppsterUtility;
import com.appster.webservice.AppsterWebserviceAPI;
import com.data.repository.DailyBonusDataRepository;
import com.data.repository.datasource.DailyBonusDataSource;
import com.data.repository.datasource.cloud.CloudDailyBonusDataSource;
import com.domain.interactors.dailybonus.DailyBonusTreatListInfoUseCase;
import com.domain.repository.DailyBonusRepository;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by thanhbc on 11/10/17.
 */

public class DailyTreatListPresenter extends BasePresenter<DailyTreatListContract.View> implements DailyTreatListContract.UserActions {

    private List<DisplayableItem> mTreatListItems = new ArrayList<>();
    private final DailyBonusTreatListInfoUseCase mBonusTreatListInforUseCase;

    public DailyTreatListPresenter(AppsterWebserviceAPI service) {
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        DailyBonusDataSource appConfigDataSource = new CloudDailyBonusDataSource(service, AppsterUtility.getAuth());
        DailyBonusRepository repository = new DailyBonusDataRepository(appConfigDataSource);
        mBonusTreatListInforUseCase = new DailyBonusTreatListInfoUseCase(repository, uiThread, ioThread);
    }

    @Override
    public void getThisWeekTreatList() {
        checkViewAttached();
        getView().showProgress();
        addSubscription(mBonusTreatListInforUseCase.execute(null)
                .filter(displayableItems -> getView() != null)
                .doOnNext(displayableItems -> mTreatListItems = displayableItems)
                .subscribe(displayableItems -> getView().onTreatsListReceived(displayableItems)
                        , this::handleRetrofitError));
//        mTreatListItems.add(new TreatListItemModel("This week's ultimate treat"));
//        mTreatListItems.add(new TreatUltimateItem());
//        mTreatListItems.add(new TreatListItemModel("Big treat"));
//        mTreatListItems.add(new TreatBigItem());
//        mTreatListItems.add(new TreatBigItem());
//        mTreatListItems.add(new TreatBigItem());
//        mTreatListItems.add(new TreatBigItem());
//        mTreatListItems.add(new TreatListItemModel("Mini treat"));
//        mTreatListItems.add(new TreatMiniItem());
//        mTreatListItems.add(new TreatMiniItem());
//        mTreatListItems.add(new TreatMiniItem());
//        mTreatListItems.add(new TreatMiniItem());
//        getView().onTreatsListReceived(mTreatListItems);
        getView().hideProgress();
    }
}
