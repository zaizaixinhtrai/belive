package com.domain.interactors.setting;

import com.appster.webservice.request_models.SettingFeaturesRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.domain.interactors.UseCase;
import com.domain.repository.SettingRepository;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 07/12/2017.
 */

public class SettingUseCase extends UseCase<BaseResponse<Boolean>, SettingFeaturesRequestModel> {
    private final SettingRepository mSettingRepository;

    public SettingUseCase(Scheduler uiThread, Scheduler executorThread, SettingRepository settingRepository) {
        super(uiThread, executorThread);
        mSettingRepository = settingRepository;
    }

    @Override
    public Observable<BaseResponse<Boolean>> buildObservable(SettingFeaturesRequestModel requestModel) {
        return mSettingRepository.updateSetting(requestModel);
    }
}
