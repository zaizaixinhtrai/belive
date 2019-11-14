package com.data.repository;

import com.appster.models.AppConfigModel;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.AppConfigsDataSource;
import com.domain.repository.AppConfigsRepository;

import rx.Observable;

/**
 * Created by linh on 21/12/2017.
 */

public class AppConfigsDataRepository implements AppConfigsRepository {
    private final AppConfigsDataSource CloudAppConfigsDataSource;

    public AppConfigsDataRepository(AppConfigsDataSource cloudAppConfigsDataSource) {
        CloudAppConfigsDataSource = cloudAppConfigsDataSource;
    }

    @Override
    public Observable<BaseResponse<AppConfigModel>> getAppConfigs() {
        return CloudAppConfigsDataSource.getAppConfigs();
    }
}
