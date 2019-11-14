package com.data.repository.datasource;

import com.appster.models.PostDataModel;
import com.appster.webservice.request_models.EditPostRequestModel;
import com.appster.webservice.response.BaseResponse;

import okhttp3.MultipartBody;
import rx.Observable;

/**
 * Created by linh on 11/10/2017.
 */

public interface PostDataSource {
    Observable<BaseResponse<PostDataModel>> createPost(MultipartBody multipartBody);
    Observable<BaseResponse<PostDataModel>> editPost(EditPostRequestModel model);
}
