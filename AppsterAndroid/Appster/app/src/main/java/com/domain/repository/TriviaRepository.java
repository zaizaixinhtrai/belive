package com.domain.repository;

import com.appster.core.adapter.DisplayableItem;
import com.appster.webservice.response.BaseResponse;
import com.domain.models.BasePagingModel;
import com.domain.models.TriviaFinishModel;
import com.domain.models.TriviaInfoModel;
import com.domain.models.TriviaResultModel;
import com.domain.models.TriviaReviveModel;

import rx.Observable;

/**
 * Created by thanhbc on 2/23/18.
 */

public interface TriviaRepository {

    Observable<TriviaInfoModel> getTriviaViewerInfo(int triviaId);

    Observable<TriviaInfoModel> getTriviaHostInfo(int triviaId);

    Observable<Boolean> getTriviaAnswer(int triviaId, int questionId, int optionId);

    Observable<TriviaResultModel> getTriviaResult(int triviaId, int questionId);

    Observable<TriviaFinishModel> getTriviaFinish(int triviaId);

    Observable<TriviaReviveModel> checkTriviaRevise(int triviaId,int questionId);

    Observable<Boolean> useRevive(int triviaId, int questionId);

    Observable<BasePagingModel<DisplayableItem>> getTriviaRankingList(int triviaRankingType, int nextId, int limit);

    Observable<BasePagingModel<DisplayableItem>> getTriviaWinnerList(int nextId, int limit, int triviaId);

    Observable<TriviaInfoModel> getTriviaQuestion(int triviaId);

}
