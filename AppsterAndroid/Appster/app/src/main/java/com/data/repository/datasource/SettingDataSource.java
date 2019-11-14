package com.data.repository.datasource;

import com.appster.webservice.request_models.DeactivateAccountRequsetModel;
import com.appster.webservice.request_models.SettingFeaturesRequestModel;
import com.appster.webservice.response.BaseResponse;

import rx.Observable;

/**
 * Created by linh on 07/12/2017.
 */

public interface SettingDataSource {
    Observable<BaseResponse<Boolean>> updateSetting(SettingFeaturesRequestModel request);
    Observable<BaseResponse<Boolean>> deactivateAccount(DeactivateAccountRequsetModel request);
}
