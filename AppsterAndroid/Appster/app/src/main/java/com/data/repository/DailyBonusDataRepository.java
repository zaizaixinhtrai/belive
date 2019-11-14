package com.data.repository;

import com.appster.core.adapter.DisplayableItem;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.Constants;
import com.data.entity.mapper.DailyTreatListInfoEntityMapper;
import com.data.entity.mapper.TreatItemEntityMapper;
import com.data.exceptions.BeLiveServerException;
import com.data.repository.datasource.DailyBonusDataSource;
import com.domain.models.DailyBonusCheckDaysModel;
import com.domain.models.NextBonusInformationModel;
import com.domain.models.TreatCollectModel;
import com.domain.models.TreatItemModel;
import com.domain.repository.DailyBonusRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by linh on 09/11/2017.
 */

public class DailyBonusDataRepository implements DailyBonusRepository {
    private final DailyBonusDataSource mDataSource;
    private final DailyTreatListInfoEntityMapper mDailyTreatListInfoEnitityMapper;
    private final TreatItemEntityMapper mTreatItemEntityMapper;

    @Inject
    public DailyBonusDataRepository(DailyBonusDataSource dataSource) {
        mDataSource = dataSource;
        mDailyTreatListInfoEnitityMapper = new DailyTreatListInfoEntityMapper();
        mTreatItemEntityMapper = new TreatItemEntityMapper();

    }

    @Override
    public Observable<BaseResponse<NextBonusInformationModel>> getDailyBonusInformation() {
        return mDataSource.getDailyBonusInfo();
    }

    @Override
    public Observable<List<DisplayableItem>> getDailyBonusTreatListInfo() {
        return mDataSource.getDailyBonusTreatListInfo()
                .filter(listBaseResponse -> listBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .flatMap(listBaseResponse -> Observable.just(listBaseResponse.getData())
                        .map(this.mDailyTreatListInfoEnitityMapper::transform));
    }

    @Override
    public Observable<List<DisplayableItem>> getDailyTreatList() {
        return mDataSource.getDailyBonusTreatList()
                .filter(listBaseResponse -> listBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .flatMap(listBaseResponse -> Observable.just(listBaseResponse.getData())
                        .map(this.mTreatItemEntityMapper::transform));
    }

    @Override
    public Observable<TreatCollectModel> collect() {
        return mDataSource.collect()
                .filter(response -> response != null)
                .flatMap(response -> {
                    if (response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        return Observable.just(response.getData());
                    } else {
                        return Observable.error(new BeLiveServerException(response.getMessage(), response.getCode()));
                    }
                })
                .map(mTreatItemEntityMapper::transform);
    }

    @Override
    public Observable<TreatItemModel> getClaimedTreat() {
        return mDataSource.getClaimedTreat()
                .flatMap(treatEntityBaseResponse -> {
                    switch (treatEntityBaseResponse.getCode()) {
                        case Constants.RESPONSE_FROM_WEB_SERVICE_OK:
                            return Observable.just(treatEntityBaseResponse.getData());
                        default:
                            return Observable.error(new BeLiveServerException(treatEntityBaseResponse.getMessage(), treatEntityBaseResponse.getCode()));
                    }
                })
                .map(this.mTreatItemEntityMapper::transformClaimedTreat);
    }

    @Override
    public Observable<Boolean> checkBonusDisplayed() {
        return mDataSource.isDailyBonusDisplayed()
                .filter(response -> response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && response.getData())
                .map(BaseResponse::getData);
    }

    @Override
    public Observable<Boolean> updateDailyDisplayed() {
        return mDataSource.updateDailyBonusDisplayed()
                .filter(response -> response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .map(BaseResponse::getData);
    }

    @Override
    public Observable<DailyBonusCheckDaysModel> checkDays() {
        return mDataSource.checkDays()
                .filter(response -> response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .flatMap(response -> {
                            if (response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                return Observable.just(response.getData());
                            } else {
                                return Observable.error(new BeLiveServerException(response.getMessage(), response.getCode()));
                            }
                        }
                )
                .map(this.mTreatItemEntityMapper::transform);
    }
}
