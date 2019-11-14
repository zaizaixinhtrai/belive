package com.data.repository.datasource;

import com.appster.models.AppConfigModel;
import com.appster.webservice.response.BaseResponse;

import rx.Observable;

/**
 * Created by linh on 21/12/2017.
 */

public interface AppConfigsDataSource {
    Observable<BaseResponse<AppConfigModel>> getAppConfigs();
}
