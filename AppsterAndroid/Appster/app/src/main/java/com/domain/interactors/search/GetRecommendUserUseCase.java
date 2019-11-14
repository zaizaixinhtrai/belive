package com.domain.interactors.search;

import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.GetRecommendResponse;
import com.appster.webservice.response.SuggestionResponseModel;
import com.data.repository.UserRecommendDataRepository;
import com.domain.interactors.UseCase;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by thanhbc on 6/18/17.
 */

public class GetRecommendUserUseCase extends UseCase<SuggestionResponseModel,Void> {
    private final UserRecommendDataRepository mRecommendDataRepository;

    public GetRecommendUserUseCase(UserRecommendDataRepository dataRepository,Scheduler uiThread, Scheduler executorThread) {
        super(uiThread, executorThread);
        this.mRecommendDataRepository=dataRepository;
    }

    @Override
    public Observable<SuggestionResponseModel> buildObservable(Void unused) {
        return this.mRecommendDataRepository.getRecommendUsers();
    }
}









