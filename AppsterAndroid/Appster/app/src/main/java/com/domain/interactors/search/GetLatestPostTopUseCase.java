package com.domain.interactors.search;

import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.LatestPostTopResponseModel;
import com.data.repository.UserRecommendDataRepository;
import com.domain.interactors.UseCase;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by Ngoc on 8/25/2017.
 */

public class GetLatestPostTopUseCase extends UseCase<BaseResponse<LatestPostTopResponseModel>, GetLatestPostTopUseCase.Params> {
    private final UserRecommendDataRepository mRecommendDataRepository;

    public GetLatestPostTopUseCase(UserRecommendDataRepository dataRepository, Scheduler uiThread, Scheduler executorThread) {
        super(uiThread, executorThread);
        this.mRecommendDataRepository = dataRepository;
    }

    @Override
    public Observable<BaseResponse<LatestPostTopResponseModel>> buildObservable(Params params) {
        if (params == null) {
            throw new NullPointerException("This use case require params to execute");
        }
        return mRecommendDataRepository.getLatestPostTop(params.nextId, params.startPage);
    }

    public static final class Params {
        final int startPage;
        final int nextId;

        private Params(int startPage, int nextId) {
            this.startPage = startPage;
            this.nextId = nextId;
        }

        public static Params loadPage(int startPage, int nextId) {
            return new Params(startPage, nextId);
        }
    }
}
