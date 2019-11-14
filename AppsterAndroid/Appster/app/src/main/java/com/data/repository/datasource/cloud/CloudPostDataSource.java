package com.data.repository.datasource.cloud;

import com.appster.models.PostDataModel;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.EditPostRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.datasource.PostDataSource;

import okhttp3.MultipartBody;
import rx.Observable;

/**
 * Created by linh on 11/10/2017.
 */

public class CloudPostDataSource implements PostDataSource {
    private final AppsterWebserviceAPI mService;
    private final String mAuthen;

    public CloudPostDataSource(AppsterWebserviceAPI service, String authen) {
        mService = service;
        mAuthen = authen;
    }

    @Override
    public Observable<BaseResponse<PostDataModel>> createPost(MultipartBody multipartBody) {
        return mService.postCreatePost(mAuthen, multipartBody);
    }

    @Override
    public Observable<BaseResponse<PostDataModel>> editPost(EditPostRequestModel model) {
        return mService.editPost(mAuthen, model);
    }
}
