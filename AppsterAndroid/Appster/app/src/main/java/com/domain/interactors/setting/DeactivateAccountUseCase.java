package com.domain.interactors.setting;

import com.appster.webservice.request_models.DeactivateAccountRequsetModel;
import com.appster.webservice.response.BaseResponse;
import com.domain.interactors.UseCase;
import com.domain.repository.SettingRepository;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 07/12/2017.
 */

public class DeactivateAccountUseCase extends UseCase<BaseResponse<Boolean>, DeactivateAccountRequsetModel> {
    private final SettingRepository mSettingRepository;

    public DeactivateAccountUseCase(Scheduler uiThread, Scheduler executorThread, SettingRepository settingRepository) {
        super(uiThread, executorThread);
        mSettingRepository = settingRepository;
    }

    @Override
    public Observable<BaseResponse<Boolean>> buildObservable(DeactivateAccountRequsetModel deactivateAccountRequsetModel) {
        return mSettingRepository.deactivateAccount(deactivateAccountRequsetModel);
    }
}
