package com.data.repository.datasource;

import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.LatestPostTopResponseModel;
import com.appster.webservice.response.SuggestionResponseModel;

import rx.Observable;

/**
 * Created by thanhbc on 6/18/17.
 */

public interface UserRecommendDataSource {

    Observable<SuggestionResponseModel> getRecommendUsers();

    Observable<BaseResponse<LatestPostTopResponseModel>> getLatestPostTop(int nextId, int statPage);
}
