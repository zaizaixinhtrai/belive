package com.data.repository.datasource.local;

import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.GetRecommendResponse;
import com.appster.webservice.response.LatestPostTopResponseModel;
import com.appster.webservice.response.SuggestionResponseModel;
import com.data.repository.datasource.UserRecommendDataSource;

import rx.Observable;

/**
 * Created by thanhbc on 6/18/17.
 */

public class LocalRecommendDataSource implements UserRecommendDataSource {
    @Override
    public Observable<SuggestionResponseModel> getRecommendUsers() {
        return null;
    }

    @Override
    public Observable<BaseResponse<LatestPostTopResponseModel>> getLatestPostTop(int nextId, int statPage) {
        return null;
    }
}
