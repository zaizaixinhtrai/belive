package com.data.repository.datasource.cloud;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.DeactivateAccountRequsetModel;
import com.appster.webservice.request_models.SettingFeaturesRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.SettingDataSource;

import rx.Observable;

/**
 * Created by linh on 07/12/2017.
 */

public class CloudSettingDataSource implements SettingDataSource {
    private final AppsterWebserviceAPI mService;
    private final String mAuth;

    public CloudSettingDataSource(AppsterWebserviceAPI service, String auth) {
        mService = service;
        mAuth = auth;
    }

    @Override
    public Observable<BaseResponse<Boolean>> updateSetting(SettingFeaturesRequestModel request) {
        return mService.setSettingFeatures(mAuth, request);
    }

    @Override
    public Observable<BaseResponse<Boolean>> deactivateAccount(DeactivateAccountRequsetModel request) {
        return mService.deactivateAccount(mAuth, request);
    }
}
