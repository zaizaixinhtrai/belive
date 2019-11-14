package com.data.repository.datasource;

import com.appster.webservice.response.BaseResponse;
import com.data.entity.StatusEntity;
import com.data.entity.TriviaFinishEntity;
import com.data.entity.TriviaInfoEntity;
import com.data.entity.TriviaRankingListPagingEntity;
import com.data.entity.TriviaResultEntity;
import com.data.entity.TriviaReviveEntity;
import com.data.entity.TriviaWinnerListPagingEntity;

import rx.Observable;

/**
 * Created by thanhbc on 2/23/18.
 */

public interface TriviaDataSource {
    Observable<BaseResponse<TriviaInfoEntity>> getTriviaInfo(int triviaId);

    Observable<BaseResponse<StatusEntity>> getTriviaAnswer(int triviaId, int questionId, int optionId);

    Observable<BaseResponse<TriviaResultEntity>> getTriviaResult(int triviaId, int questionId);

    Observable<BaseResponse<TriviaFinishEntity>> getTriviaFinish(int triviaId);

    Observable<BaseResponse<TriviaRankingListPagingEntity>> getTriviaRankingList(int triviaRankingType, int nextId, int limit);

    Observable<BaseResponse<TriviaReviveEntity>> checkTriviaRevise(int triviaId, int questionId);

    Observable<BaseResponse<StatusEntity>> useRevive(int triviaId, int questionId);

    Observable<BaseResponse<TriviaWinnerListPagingEntity>> getTriviaWinnerList(int nextId, int limit, int triviaId);

    Observable<BaseResponse<TriviaInfoEntity>> getTriviaQuestion(int triviaId);

    Observable<BaseResponse<TriviaInfoEntity>> getTriviaInfoTime(int triviaId);
}
