package com.domain.interactors.post;

import com.appster.models.PostDataModel;
import com.appster.webservice.request_models.EditPostRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.data.repository.PostDataRepository;
import com.domain.interactors.UseCase;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by linh on 10/10/2017.
 */

public class EditPostUseCase extends UseCase<BaseResponse<PostDataModel>, EditPostRequestModel> {

    private PostDataRepository mDataRepository;

    public EditPostUseCase(Scheduler uiThread, Scheduler executorThread, PostDataRepository dataRepository) {
        super(uiThread, executorThread);
        this.mDataRepository = dataRepository;
    }

    @Override
    public Observable<BaseResponse<PostDataModel>> buildObservable(EditPostRequestModel model) {
        return mDataRepository.editPost(model);
    }
}
