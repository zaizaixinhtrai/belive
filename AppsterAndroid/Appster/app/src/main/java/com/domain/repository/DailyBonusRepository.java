package com.domain.repository;

import com.appster.core.adapter.DisplayableItem;
import com.appster.webservice.response.BaseResponse;
import com.domain.models.DailyBonusCheckDaysModel;
import com.domain.models.NextBonusInformationModel;
import com.domain.models.TreatCollectModel;
import com.domain.models.TreatItemModel;

import java.util.List;

import rx.Observable;

/**
 * Created by linh on 09/11/2017.
 */

public interface DailyBonusRepository {
    Observable<BaseResponse<NextBonusInformationModel>> getDailyBonusInformation();

    Observable<List<DisplayableItem>> getDailyBonusTreatListInfo();

    Observable<List<DisplayableItem>> getDailyTreatList();

    Observable<TreatCollectModel> collect();

    Observable<TreatItemModel> getClaimedTreat();

    Observable<Boolean> checkBonusDisplayed();

    Observable<Boolean> updateDailyDisplayed();

    Observable<DailyBonusCheckDaysModel> checkDays();
}
