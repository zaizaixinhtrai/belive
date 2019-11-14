package com.data.repository.datasource.cloud;

import com.appster.models.AppConfigModel;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.AppConfigsDataSource;

import rx.Observable;

/**
 * Created by linh on 21/12/2017.
 */

public class CloudAppConfigsDataSource implements AppConfigsDataSource {
    private final AppsterWebserviceAPI mService;
    private final String mAuthen;
    public CloudAppConfigsDataSource(AppsterWebserviceAPI service,String authen) {
        mService = service;
        mAuthen= authen;
    }

    @Override
    public Observable<BaseResponse<AppConfigModel>> getAppConfigs() {
        return mService.getAppConfigs(mAuthen);
    }
}
