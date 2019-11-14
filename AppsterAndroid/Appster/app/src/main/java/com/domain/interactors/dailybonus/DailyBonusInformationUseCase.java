package com.domain.interactors.dailybonus;

import com.appster.webservice.response.BaseResponse;
import com.domain.interactors.UseCase;
import com.domain.models.NextBonusInformationModel;
import com.domain.repository.DailyBonusRepository;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 09/11/2017.
 */

public class DailyBonusInformationUseCase extends UseCase<BaseResponse<NextBonusInformationModel>, Void> {
    private final DailyBonusRepository mRepository;

    public DailyBonusInformationUseCase(Scheduler uiThread, Scheduler executorThread, DailyBonusRepository repository) {
        super(uiThread, executorThread);
        mRepository = repository;
    }

    @Override
    public Observable<BaseResponse<NextBonusInformationModel>> buildObservable(Void aVoid) {
        return mRepository.getDailyBonusInformation();
    }
}
