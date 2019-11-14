package com.domain.interactors.trivia

import com.data.di.SchedulerModule
import com.domain.interactors.UseCase
import com.domain.models.TriviaInfoModel
import com.domain.repository.TriviaRepository
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

class TriviaQuestionUseCase @Inject constructor(@Named(SchedulerModule.UI) uiThread: Scheduler, @Named(SchedulerModule.IO) executorThread: Scheduler,
                                                private val mRepository: TriviaRepository)
    : UseCase<TriviaInfoModel, TriviaQuestionUseCase.Params>(uiThread, executorThread) {

    override fun buildObservable(params: Params): Observable<TriviaInfoModel> = mRepository.getTriviaQuestion(params.triviaId)

    class Params private constructor(internal val triviaId: Int) {
        companion object {
            @JvmStatic
            fun load(triviaId: Int): Params {
                return Params(triviaId)
            }
        }
    }
}