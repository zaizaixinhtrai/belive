package com.data.repository.datasource.cloud;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.response.BaseResponse;
import com.data.entity.StatusEntity;
import com.data.entity.TriviaFinishEntity;
import com.data.entity.TriviaInfoEntity;
import com.data.entity.TriviaRankingListPagingEntity;
import com.data.entity.TriviaResultEntity;
import com.data.entity.TriviaReviveEntity;
import com.data.entity.TriviaWinnerListPagingEntity;
import com.data.entity.requests.TriviaAnswerRequestEntity;
import com.data.entity.requests.TriviaFinishRequestEntity;
import com.data.entity.requests.TriviaInfoRequestEntity;
import com.data.entity.requests.TriviaRankingRequestModel;
import com.data.entity.requests.TriviaResultRequestEntity;
import com.data.entity.requests.TriviaReviveRequestEntity;
import com.data.entity.requests.TriviaWinnerListRequestEntity;
import com.data.repository.datasource.TriviaDataSource;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

import static com.data.di.ApiServiceModule.APP_AUTHEN;

/**
 * Created by thanhbc on 2/23/18.
 */

public class CloudTriviaDataSource implements TriviaDataSource {
    private final AppsterWebserviceAPI mService;
    private final String mAuthen;

    @Inject
    public CloudTriviaDataSource(AppsterWebserviceAPI service, @Named(APP_AUTHEN) String authen) {
        mService = service;
        mAuthen = authen;
    }

    @Override
    public Observable<BaseResponse<TriviaInfoEntity>> getTriviaInfo(int triviaId) {
        return mService.getTriviaInfoTime(mAuthen, new TriviaInfoRequestEntity(triviaId, System.currentTimeMillis() / 1000L));
    }

    @Override
    public Observable<BaseResponse<StatusEntity>> getTriviaAnswer(int triviaId, int questionId, int optionId) {
        return mService.getTriviaAnswer(mAuthen, new TriviaAnswerRequestEntity(triviaId, questionId, optionId));
    }

    @Override
    public Observable<BaseResponse<TriviaResultEntity>> getTriviaResult(int triviaId, int questionId) {
        return mService.getTriviaResult(mAuthen, new TriviaResultRequestEntity(triviaId, questionId));
    }

    @Override
    public Observable<BaseResponse<TriviaFinishEntity>> getTriviaFinish(int triviaId) {
        return mService.getTriviaFinish(mAuthen, new TriviaFinishRequestEntity(triviaId));
    }

    @Override
    public Observable<BaseResponse<TriviaReviveEntity>> checkTriviaRevise(int triviaId, int questionId) {
        return mService.checkRevive(mAuthen, new TriviaReviveRequestEntity(triviaId, questionId));
    }

    @Override
    public Observable<BaseResponse<StatusEntity>> useRevive(int triviaId, int questionId) {
        return mService.useRevive(mAuthen, new TriviaReviveRequestEntity(triviaId, questionId));
    }

    @Override
    public Observable<BaseResponse<TriviaWinnerListPagingEntity>> getTriviaWinnerList(int nextId, int limit, int triviaId) {
        return mService.getTriviaWinnerList(mAuthen, new TriviaWinnerListRequestEntity(nextId, limit, triviaId));
    }

    @Override
    public Observable<BaseResponse<TriviaInfoEntity>> getTriviaQuestion(int triviaId) {
        return mService.getTriviaQuestion(mAuthen, triviaId);
    }

    @Override
    public Observable<BaseResponse<TriviaInfoEntity>> getTriviaInfoTime(int triviaId) {
        return mService.getTriviaInfoTime(mAuthen, new TriviaInfoRequestEntity(triviaId, System.currentTimeMillis() / 1000L));
    }

    @Override
    public Observable<BaseResponse<TriviaRankingListPagingEntity>> getTriviaRankingList(int triviaRankingType, int nextId, int limit) {
        return mService.getTriviaRankingList(mAuthen, new TriviaRankingRequestModel(triviaRankingType, nextId, limit));
    }
}
