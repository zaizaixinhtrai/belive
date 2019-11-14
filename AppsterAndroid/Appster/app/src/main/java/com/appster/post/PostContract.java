package com.appster.post;

import com.appster.features.mvpbase.BaseContract;
import com.appster.models.PostDataModel;
import com.appster.webservice.request_models.EditPostRequestModel;
import com.appster.webservice.response.BaseResponse;

import okhttp3.MultipartBody;

/**
 * Created by linh on 10/10/2017.
 */

public class PostContract {
    interface View extends BaseContract.View {
        void onPostSuccessfully(PostDataModel data);
        void onPostFailed();
        void onEditPostSuccessfully(BaseResponse<PostDataModel> editPostModel);
        void onEditPostFailed();
        void showProgress(String message);
    }

    interface PostActions extends BaseContract.Presenter<View> {
        void post(MultipartBody multipartBody);
        void editPost(EditPostRequestModel model);
    }
}
