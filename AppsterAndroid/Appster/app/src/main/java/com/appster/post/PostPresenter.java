package com.appster.post;
import com.appster.R;
import com.appster.features.mvpbase.BasePresenter;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.EditPostRequestModel;
import com.apster.common.Constants;
import com.data.repository.PostDataRepository;
import com.data.repository.datasource.cloud.CloudPostDataSource;
import com.domain.interactors.post.CreatePostUseCase;
import com.domain.interactors.post.EditPostUseCase;

import okhttp3.MultipartBody;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by linh on 10/10/2017.
 */

public class PostPresenter extends BasePresenter<PostContract.View> implements PostContract.PostActions {
    protected final AppsterWebserviceAPI mService;
    protected final String mAuthen;
    private CreatePostUseCase mCreatePostUseCase;
    private EditPostUseCase mEditPostUseCase;

    public PostPresenter(AppsterWebserviceAPI service, String authen) {
        mService = service;
        mAuthen = authen;
        final Scheduler uiThread = AndroidSchedulers.mainThread();
        final Scheduler io = Schedulers.io();
        PostDataRepository postDataRepository = new PostDataRepository(new CloudPostDataSource(mService, authen));
        mCreatePostUseCase = new CreatePostUseCase(uiThread, io, postDataRepository);
        mEditPostUseCase = new EditPostUseCase(uiThread, io, postDataRepository);
    }

    @Override
    public void post(MultipartBody multipartBody) {
        getView().showProgress();
        addSubscription(mCreatePostUseCase.execute(multipartBody)
                .subscribe(postCreatePostResponseModel -> {
                    getView().hideProgress();
                    if (postCreatePostResponseModel == null) return;

                    if (postCreatePostResponseModel.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        getView().loadError(postCreatePostResponseModel.getMessage(), postCreatePostResponseModel.getCode());
                        return;
                    }

                    getView().onPostSuccessfully(postCreatePostResponseModel.getData());
                }, error -> {
                    getView().hideProgress();
                    getView().loadError(error.getMessage(), Constants.RETROFIT_ERROR);
                }));

    }

    @Override
    public void editPost(EditPostRequestModel model) {
        getView().showProgress(getView().getViewContext().getString(R.string.post_updating));
        addSubscription(mEditPostUseCase.execute(model)
                .subscribe(editPostModel -> {
                    getView().hideProgress();
                    if (editPostModel == null) return;
                    getView().onEditPostSuccessfully(editPostModel);
                }, error -> {
                    getView().hideProgress();
                    getView().loadError(error.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }
}