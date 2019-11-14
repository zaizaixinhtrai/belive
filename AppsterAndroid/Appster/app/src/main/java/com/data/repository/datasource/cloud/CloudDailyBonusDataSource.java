package com.data.repository.datasource.cloud;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.response.BaseResponse;
import com.data.di.ApiServiceModule;
import com.data.entity.DailyBonusCheckDaysEntity;
import com.data.entity.DailyTreatListInfoEntity;
import com.data.entity.TreatCollectEntity;
import com.data.entity.TreatEntity;
import com.data.repository.datasource.DailyBonusDataSource;
import com.domain.models.NextBonusInformationModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

/**
 * Created by linh on 09/11/2017.
 */

public class CloudDailyBonusDataSource implements DailyBonusDataSource {
    private final AppsterWebserviceAPI mService;
    private final String mAuthen;

    @Inject
    public CloudDailyBonusDataSource(AppsterWebserviceAPI service, @Named(ApiServiceModule.APP_AUTHEN)String authen) {
        mService = service;
        mAuthen= authen;
    }

    @Override
    public Observable<BaseResponse<NextBonusInformationModel>> getDailyBonusInfo() {
        return mService.getBonusInformation(mAuthen);
    }

    @Override
    public Observable<BaseResponse<List<DailyTreatListInfoEntity>>>getDailyBonusTreatListInfo() {
        return mService.getTreatListInfo(mAuthen);
    }

    @Override
    public Observable<BaseResponse<List<TreatEntity>>> getDailyBonusTreatList() {
        return mService.getTreatList(mAuthen);
    }

    @Override
    public Observable<BaseResponse<TreatCollectEntity>> collect() {
        return mService.collect(mAuthen);
    }

    @Override
    public Observable<BaseResponse<TreatEntity>> getClaimedTreat() {
        return mService.getClaimedTreat(mAuthen);
    }

    @Override
    public Observable<BaseResponse<Boolean>> isDailyBonusDisplayed() {
        return mService.checkDailyBonusDisplayed(mAuthen);
    }

    @Override
    public Observable<BaseResponse<Boolean>> updateDailyBonusDisplayed() {
        return mService.updateDailyBonusDisplayed(mAuthen);
    }

    @Override
    public Observable<BaseResponse<DailyBonusCheckDaysEntity>> checkDays() {
        return mService.checkDays(mAuthen);
    }
}
