package com.domain.interactors.post;

import com.appster.models.PostDataModel;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.PostDataRepository;
import com.domain.interactors.UseCase;

import okhttp3.MultipartBody;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 10/10/2017.
 */

public class CreatePostUseCase extends UseCase<BaseResponse<PostDataModel>, MultipartBody> {

    private PostDataRepository mDataRepository;

    public CreatePostUseCase(Scheduler uiThread, Scheduler executorThread, PostDataRepository repository) {
        super(uiThread, executorThread);
        this.mDataRepository = repository;
    }

    @Override
    public Observable<BaseResponse<PostDataModel>> buildObservable(MultipartBody multipartBody) {
        return mDataRepository.createPost(multipartBody);
    }
}
