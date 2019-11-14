package com.domain.interactors;

import com.appster.models.AppConfigModel;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.AppConfigsDataRepository;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 21/12/2017.
 */

public class AppConfigsUseCase extends UseCase<BaseResponse<AppConfigModel>, Void> {
    private final AppConfigsDataRepository mRepository;

    public AppConfigsUseCase(Scheduler uiThread, Scheduler executorThread, AppConfigsDataRepository repository) {
        super(uiThread, executorThread);
        mRepository = repository;
    }

    @Override
    public Observable<BaseResponse<AppConfigModel>> buildObservable(Void aVoid) {
        return mRepository.getAppConfigs();
    }
}
