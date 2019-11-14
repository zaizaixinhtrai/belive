package com.data.repository.datasource.cloud;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.GetRecommendResponse;
import com.appster.webservice.response.LatestPostTopResponseModel;
import com.appster.webservice.response.SuggestionResponseModel;
import com.data.repository.datasource.UserRecommendDataSource;

import rx.Observable;

/**
 * Created by thanhbc on 6/18/17.
 */

public class CloudRecommendDataSource implements UserRecommendDataSource {

    private final AppsterWebserviceAPI mService;
    private final String mAuthen;

    public CloudRecommendDataSource(AppsterWebserviceAPI service, String authen) {
        mService = service;
        mAuthen = authen;
    }

    @Override
    public Observable<SuggestionResponseModel> getRecommendUsers() {
        return mService.getSuggestion(mAuthen);
    }

    @Override
    public Observable<BaseResponse<LatestPostTopResponseModel>> getLatestPostTop(int nextId, int statPage) {
        return mService.getLatestPostTop(mAuthen, nextId, statPage);
    }
}
