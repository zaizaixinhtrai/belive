package com.data.repository.datasource;

import com.appster.webservice.response.BaseResponse;
import com.data.entity.DailyBonusCheckDaysEntity;
import com.data.entity.DailyTreatListInfoEntity;
import com.data.entity.TreatCollectEntity;
import com.data.entity.TreatEntity;
import com.domain.models.NextBonusInformationModel;

import java.util.List;

import rx.Observable;

/**
 * Created by linh on 09/11/2017.
 */

public interface DailyBonusDataSource {
    Observable<BaseResponse<NextBonusInformationModel>> getDailyBonusInfo();

    Observable<BaseResponse<List<DailyTreatListInfoEntity>>> getDailyBonusTreatListInfo();

    Observable<BaseResponse<List<TreatEntity>>> getDailyBonusTreatList();

    Observable<BaseResponse<TreatCollectEntity>> collect();

    Observable<BaseResponse<TreatEntity>> getClaimedTreat();

    Observable<BaseResponse<Boolean>> isDailyBonusDisplayed();

    Observable<BaseResponse<Boolean>> updateDailyBonusDisplayed();

    Observable<BaseResponse<DailyBonusCheckDaysEntity>> checkDays();
}
