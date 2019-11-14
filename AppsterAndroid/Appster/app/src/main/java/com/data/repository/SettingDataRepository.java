package com.data.repository;

import com.appster.webservice.request_models.DeactivateAccountRequsetModel;
import com.appster.webservice.request_models.SettingFeaturesRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.SettingDataSource;
import com.domain.repository.SettingRepository;

import rx.Observable;

/**
 * Created by linh on 07/12/2017.
 */

public class SettingDataRepository implements SettingRepository {
    private final SettingDataSource mSettingDataSource;

    public SettingDataRepository(SettingDataSource settingDataSource) {
        mSettingDataSource = settingDataSource;
    }

    @Override
    public Observable<BaseResponse<Boolean>> updateSetting(SettingFeaturesRequestModel request) {
        return mSettingDataSource.updateSetting(request);
    }

    @Override
    public Observable<BaseResponse<Boolean>> deactivateAccount(DeactivateAccountRequsetModel request) {
        return mSettingDataSource.deactivateAccount(request);
    }
}
