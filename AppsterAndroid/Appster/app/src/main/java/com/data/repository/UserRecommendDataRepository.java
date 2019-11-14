package com.data.repository;

import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.LatestPostTopResponseModel;
import com.appster.webservice.response.SuggestionResponseModel;
import com.data.repository.datasource.cloud.CloudRecommendDataSource;

import rx.Observable;

/**
 * Created by thanhbc on 6/18/17.
 */

public class UserRecommendDataRepository {

    //    LocalRecommendDataSource mLocalRecommendDataSource;
    private final CloudRecommendDataSource mCloudRecommendDataSource;

//    public UserRecommendDataRepository(LocalRecommendDataSource localRecommendDataSource, CloudRecommendDataSource cloudRecommendDataSource) {
//        mLocalRecommendDataSource = localRecommendDataSource;
//        mCloudRecommendDataSource = cloudRecommendDataSource;
//    }

    public UserRecommendDataRepository(CloudRecommendDataSource cloudRecommendDataSource) {
        mCloudRecommendDataSource = cloudRecommendDataSource;
    }

    public Observable<SuggestionResponseModel> getRecommendUsers() {
        //now only cloud
        return mCloudRecommendDataSource.getRecommendUsers();
    }

    public Observable<BaseResponse<LatestPostTopResponseModel>> getLatestPostTop(int nextId, int statPage) {
        return mCloudRecommendDataSource.getLatestPostTop(nextId, statPage);
    }
}
