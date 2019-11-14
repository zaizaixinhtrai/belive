package com.data.repository;

import com.appster.core.adapter.DisplayableItem;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.Constants;
import com.data.entity.mapper.TriviaEntityMapper;
import com.data.exceptions.BeLiveServerException;
import com.data.repository.datasource.TriviaDataSource;
import com.domain.models.BasePagingModel;
import com.domain.models.TriviaFinishModel;
import com.domain.models.TriviaInfoModel;
import com.domain.models.TriviaResultModel;
import com.domain.models.TriviaReviveModel;
import com.domain.repository.TriviaRepository;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by thanhbc on 2/23/18.
 */

public class TriviaDataRepository implements TriviaRepository {
    final TriviaDataSource mTriviaDataSource;
    final TriviaEntityMapper mTriviaEntityMapper;
    final int ATTEMPTS = 3;

    @Inject
    public TriviaDataRepository(@Remote TriviaDataSource cloudTriviaDataSource) {
        mTriviaDataSource = cloudTriviaDataSource;
        mTriviaEntityMapper = new TriviaEntityMapper();
    }

    @Override
    public Observable<TriviaInfoModel> getTriviaViewerInfo(int triviaId) {
        return mTriviaDataSource.getTriviaInfoTime(triviaId)
                .map(BaseResponse::getData)
                .map(mTriviaEntityMapper::transform);

    }

    @Override
    public Observable<TriviaInfoModel> getTriviaHostInfo(int triviaId) {
        return mTriviaDataSource.getTriviaInfo(triviaId)
                .map(BaseResponse::getData)
                .map(mTriviaEntityMapper::transformForHost);
    }

    @Override
    public Observable<Boolean> getTriviaAnswer(int triviaId, int questionId, int optionId) {
        return mTriviaDataSource.getTriviaAnswer(triviaId, questionId, optionId)
                .flatMap(statusEntityBaseResponse -> {
                    switch (statusEntityBaseResponse.getCode()) {
                        case Constants.RESPONSE_FROM_WEB_SERVICE_OK:
                            return Observable.just(statusEntityBaseResponse.getData().status);
                        default:
                            return Observable.error(new BeLiveServerException(statusEntityBaseResponse.getMessage(), statusEntityBaseResponse.getCode()));
                    }
                })
                .retryWhen(errors -> errors.zipWith(Observable.range(1, ATTEMPTS), (n, i) ->
                        i < ATTEMPTS ?
                                Observable.timer((int) Math.pow(3, i), TimeUnit.SECONDS) :
                                Observable.error(n))
                        .flatMap(x -> x));
    }

    @Override
    public Observable<TriviaResultModel> getTriviaResult(int triviaId, int questionId) {
        return mTriviaDataSource.getTriviaResult(triviaId, questionId)
                .map(BaseResponse::getData)
                .map(mTriviaEntityMapper::transform);
    }

    @Override
    public Observable<TriviaFinishModel> getTriviaFinish(int triviaId) {
        return mTriviaDataSource.getTriviaFinish(triviaId)
                .flatMap(statusEntityBaseResponse -> {
                    switch (statusEntityBaseResponse.getCode()) {
                        case Constants.RESPONSE_FROM_WEB_SERVICE_OK:
                            return Observable.just(mTriviaEntityMapper.transform(statusEntityBaseResponse.getData()));
                        default:
                            return Observable.error(new BeLiveServerException(statusEntityBaseResponse.getMessage(), statusEntityBaseResponse.getCode()));
                    }
                })
                .retryWhen(errors -> errors.zipWith(Observable.range(1, ATTEMPTS), (n, i) -> i)
                        .flatMap(retryCount -> Observable.timer((int) Math.pow(3, retryCount), TimeUnit.SECONDS)));
    }

    @Override
    public Observable<TriviaReviveModel> checkTriviaRevise(int triviaId, int questionId) {
        return mTriviaDataSource.checkTriviaRevise(triviaId, questionId)
                .flatMap(triviaReviseEntityBaseResponse -> {
                    switch (triviaReviseEntityBaseResponse.getCode()) {
                        case Constants.RESPONSE_FROM_WEB_SERVICE_OK:
                            return Observable.just(triviaReviseEntityBaseResponse.getData());
                        default:
                            return Observable.error(new BeLiveServerException(triviaReviseEntityBaseResponse.getMessage(), triviaReviseEntityBaseResponse.getCode()));
                    }
                })
                .map(mTriviaEntityMapper::transform)
                .retryWhen(errors -> errors.zipWith(Observable.range(1, ATTEMPTS), (n, i) -> i)
                        .flatMap(retryCount -> Observable.timer((int) Math.pow(3, retryCount), TimeUnit.SECONDS)));
    }

    @Override
    public Observable<Boolean> useRevive(int triviaId, int questionId) {
        return mTriviaDataSource.useRevive(triviaId, questionId)
                .flatMap(triviaReviseEntityBaseResponse -> {
                    switch (triviaReviseEntityBaseResponse.getCode()) {
                        case Constants.RESPONSE_FROM_WEB_SERVICE_OK:
                            return Observable.just(triviaReviseEntityBaseResponse.getData().status);
                        default:
                            return Observable.error(new BeLiveServerException(triviaReviseEntityBaseResponse.getMessage(), triviaReviseEntityBaseResponse.getCode()));
                    }
                })
                .retryWhen(errors -> errors.zipWith(Observable.range(1, ATTEMPTS), (n, i) -> i)
                        .flatMap(retryCount -> Observable.timer((int) Math.pow(3, retryCount), TimeUnit.SECONDS)));
    }

    @Override
    public Observable<BasePagingModel<DisplayableItem>> getTriviaRankingList(int triviaRankingType, int nextId, int limit) {
        return mTriviaDataSource.getTriviaRankingList(triviaRankingType, nextId, limit)
                .filter(baseDataPagingResponseModelBaseResponse -> baseDataPagingResponseModelBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .flatMap(baseDataPagingResponseModelBaseResponse -> Observable.just(baseDataPagingResponseModelBaseResponse.getData()))
                .map(this.mTriviaEntityMapper::transform);
    }

    @Override
    public Observable<BasePagingModel<DisplayableItem>> getTriviaWinnerList(int nextId, int limit, int triviaId) {
        return mTriviaDataSource.getTriviaWinnerList(nextId, limit, triviaId)
                .filter(baseDataPagingResponseModelBaseResponse -> baseDataPagingResponseModelBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .flatMap(baseDataPagingResponseModelBaseResponse -> Observable.just(baseDataPagingResponseModelBaseResponse.getData()))
                .map(mTriviaEntityMapper::transform);
    }

    @Override
    public Observable<TriviaInfoModel> getTriviaQuestion(int triviaId) {
        return mTriviaDataSource.getTriviaQuestion(triviaId)
                .map(BaseResponse::getData)
                .map(mTriviaEntityMapper::transformQuestion);
    }

}
