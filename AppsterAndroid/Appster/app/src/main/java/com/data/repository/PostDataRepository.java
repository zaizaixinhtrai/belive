package com.data.repository;

import com.appster.models.PostDataModel;
import com.appster.webservice.request_models.EditPostRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.cloud.CloudPostDataSource;

import okhttp3.MultipartBody;
import rx.Observable;

/**
 * Created by linh on 11/10/2017.
 */

public class PostDataRepository {
    CloudPostDataSource mCloudDataSource;

    public PostDataRepository(CloudPostDataSource cloudDataSource) {
        mCloudDataSource = cloudDataSource;
    }

    public Observable<BaseResponse<PostDataModel>> createPost(MultipartBody multipartBody) {
        return mCloudDataSource.createPost(multipartBody);
    }

    public Observable<BaseResponse<PostDataModel>> editPost(EditPostRequestModel model) {
        return mCloudDataSource.editPost(model);
    }
}
