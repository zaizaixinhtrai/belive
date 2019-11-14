package com.data.entity.mapper

import com.appster.BuildConfig
import com.appster.core.adapter.DisplayableItem
import com.apster.common.Constants
import com.data.entity.*
import com.domain.models.*
import com.pack.utility.StringUtil
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by thanhbc on 2/23/18.
 */

class TriviaEntityMapper {
    private var mTopWinnerOrderIndexFlag = 0

    fun transform(entity: TriviaInfoEntity?): TriviaInfoModel? {
        return entity?.let {
            return TriviaInfoModel().apply {
                triviaId = it.triviaId
                answerTime = it.answerTime
                canPlay = it.canPlay
                val startTime = Calendar.getInstance()
                startTime.timeInMillis = it.startingDateTime * 1000L
                val currentServerTime = Calendar.getInstance()
                currentServerTime.timeInMillis = it.currentDateTime * 1000L
                val currentClientTime = System.currentTimeMillis()
                val apiDurationSec = ((currentClientTime / 1000) - it.clientCurrentDateTime).toInt()
//                diffInSec = TimeUnit.MILLISECONDS.toSeconds(currentServerTime.timeInMillis - (it.clientCurrentDateTime * 1000L)).toInt()
                diffInSec -= apiDurationSec / 2

                if (startTime.after(currentServerTime)) {
                    //get diff between start & current time in secs to countdown
                    secsToBegin = TimeUnit.MILLISECONDS.toSeconds(startTime.timeInMillis - currentServerTime.timeInMillis).toInt() + diffInSec
                    secsToGetTriviaQuestionsApi = secsToBegin - 30
                } else {
                    //user already late check is able to play
                    val nextQuestionTime = Calendar.getInstance()
                    nextQuestionTime.timeInMillis = it.nextQuestionDateTime * 1000L
                    if (it.canPlay) {
                        //if able to play -> rejoin
                        isRejoin = true
                    }
                    var secsCalculated = 0L
                    if (!it.isFinished) {
                        secsCalculated = if (it.nextQuestionDateTime == 0L) totalTimeWaitingPerQuestion(it) else nextQuestionTime.timeInMillis - currentServerTime.timeInMillis
                    }
                    secsToBegin = TimeUnit.MILLISECONDS.toSeconds(secsCalculated).toInt() + diffInSec
                    secsToGetTriviaQuestionsApi = 0
                }
                Timber.e("secsToBegin %s", secsToBegin)
                Timber.e("secsToGetTriviaQuestionsApi=%s", secsToGetTriviaQuestionsApi)
//                 hash = it.hash
                finishTime = it.finishTime
                nextQuestionDateTime = it.nextQuestionDateTime
                nextQuestionId = it.nextQuestionId
                resultWaitingTime = it.resultWaitingTime
                resultTime = it.resultTime
                questionWaitingTime = it.questionWaitingTime
                messageTitle = it.messageTitle
                message = it.message
                reviveCount = it.reviveCount
                reviveWaitingTime = it.reviveWaitingTime
                finishWaitingTime = it.finishWaitingTime
                reviveAnim = it.reviveAnim
//                questions = it.getQuestions()?.map(::transform) ?: ArrayList()
                countryCode = it.countryCode
            }
        }
    }

    fun transformQuestion(entity: TriviaInfoEntity?): TriviaInfoModel? {
        return entity?.let {
            return TriviaInfoModel().apply {
                hash = it.hash
                questions = it.getQuestions()?.map(::transform) ?: ArrayList()
            }
        }
    }

    fun transformForHost(entity: TriviaInfoEntity?): TriviaInfoModel? {
        return entity?.let {
            return TriviaInfoModel().apply {
                triviaId = it.triviaId
                answerTime = it.answerTime
                canPlay = it.canPlay
                val startTime = Calendar.getInstance()
                startTime.timeInMillis = it.startingDateTime * 1000L
                val currentServerTime = Calendar.getInstance()
                currentServerTime.timeInMillis = it.currentDateTime * 1000L

                if (startTime.after(currentServerTime)) {
                    //get diff between start & current time in secs to countdown
                    secsToBegin = TimeUnit.MILLISECONDS.toSeconds(startTime.timeInMillis - currentServerTime.timeInMillis).toInt()
                    secsToGetTriviaQuestionsApi = secsToBegin - 30
                } else {
                    //user already late check is able to play
                    val nextQuestionTime = Calendar.getInstance()
                    nextQuestionTime.timeInMillis = it.nextQuestionDateTime * 1000L
                    if (it.canPlay) {
                        //if able to play -> rejoin
                        isRejoin = true
                    }
                    var secsCalculated = 0L
                    if (!it.isFinished) {
                        secsCalculated = if (it.nextQuestionDateTime == 0L) totalTimeWaitingPerQuestion(it) else nextQuestionTime.timeInMillis - currentServerTime.timeInMillis
                    }
                    secsToBegin = TimeUnit.MILLISECONDS.toSeconds(secsCalculated).toInt()
                    secsToGetTriviaQuestionsApi = 0
                }
                Timber.e("secsToBegin %s", secsToBegin)
                //            infoModel.secsToBegin = 60;
//                hash = it.hash
                finishTime = it.finishTime
                nextQuestionDateTime = it.nextQuestionDateTime
                nextQuestionId = it.nextQuestionId
                resultWaitingTime = it.resultWaitingTime
                resultTime = it.resultTime
                questionWaitingTime = it.questionWaitingTime
                messageTitle = it.messageTitle
                message = it.message
                reviveCount = it.reviveCount
                reviveWaitingTime = it.reviveWaitingTime
                finishWaitingTime = it.finishWaitingTime
                reviveAnim = it.reviveAnim
//                questions = it.getQuestions()?.map(::transform) ?: ArrayList()

            }
        }
    }

    private fun totalTimeWaitingPerQuestion(entity: TriviaInfoEntity): Long = TimeUnit.SECONDS.toMillis((entity.answerTime + entity.resultWaitingTime + entity.resultTime + entity.finishWaitingTime).toLong())

    private fun transform(question: TriviaInfoEntity.Questions): TriviaInfoModel.Questions = TriviaInfoModel.Questions(question.title, question.questionId, question.options?.map(::transform)
            ?: ArrayList())

    private fun transform(option: TriviaInfoEntity.Questions.Options): TriviaInfoModel.Questions.Options = TriviaInfoModel.Questions.Options(option.optionId, option.option)

    fun transform(entity: TriviaAnswerEntity): TriviaAnswerModel = TriviaAnswerModel()

    fun transform(entity: TriviaResultEntity?): TriviaResultModel? {
        return entity?.let {
            return TriviaResultModel().apply {
                isCorrectAnswer = it.correct
                participant = it.participants
                previousRevivedCount = it.previousRevivedCount
                message = it.message
                answers = it.options?.map { TriviaResultModel.TriviaAnswers(it.isAnswer, it.count, it.optionId) } ?: ArrayList()
            }
        }
    }

    fun transform(entity: TriviaFinishEntity?): TriviaFinishModel? {
        return entity?.let {
            return TriviaFinishModel().apply {
                message = it.message
                prizePerUserString = it.prizePerUserString
                winnerCount = it.winnerCount
                win = it.win
                if (it.win && it.winnerPopup != null) {
                    winnerPopup = TriviaFinishModel.WinnerPopup()
                    winnerPopup.title = it.winnerPopup.title
                    winnerPopup.message = it.winnerPopup.message
                    winnerPopup.prizeMessage = it.winnerPopup.prizeMessage
                }
            }
        }
    }

    fun transform(entity: TriviaReviveEntity?): TriviaReviveModel? {
        return entity?.let {
            return TriviaReviveModel(it.reviveCount, it.canUseRevive, it.messageTitle, it.message, it.cancelMessageTitle, it.cancelMessage, it.reviveAnim)
        }
    }

    fun transform(triviaPagingEntity: TriviaRankingListPagingEntity?): BasePagingModel<DisplayableItem>? {
        return triviaPagingEntity?.users?.let {
            return BasePagingModel<DisplayableItem>().apply {
                isEnd = it.isEnd
                nextId = it.nextId
                totalRecords = it.totalRecords
                data = it.result.map {
                    mTopWinnerOrderIndexFlag++
                    WinnerModel().apply {
                        var s3ServerLink = Constants.AWS_S3_SERVER_LINK
                        if (StringUtil.isNullOrEmptyString(Constants.AWS_S3_SERVER_LINK)) {
                            s3ServerLink = BuildConfig.AWS_S3_SERVER_LINK + "profile_image_thum"
                        }
                        val imageAvatar = s3ServerLink + "/" + it.userName + ".jpg" + "?=" + triviaPagingEntity.imageCacheParam
                        userId = it.userId
                        displayName = it.displayName
                        prize = it.prize
                        userName = it.userName
                        prizeString = it.prizeString
                        userAvatar = imageAvatar
                        orderIndex = mTopWinnerOrderIndexFlag
                    }
                }
            }
        }
    }

    fun transform(triviaPagingEntity: TriviaWinnerListPagingEntity?): BasePagingModel<DisplayableItem>? {
        return triviaPagingEntity?.users?.let {
            return BasePagingModel<DisplayableItem>().apply {
                isEnd = it.isEnd
                nextId = it.nextId
                data = it.result.map {
                    WinnerModel().apply {
                        var s3ServerLink = Constants.AWS_S3_SERVER_LINK
                        if (StringUtil.isNullOrEmptyString(Constants.AWS_S3_SERVER_LINK)) {
                            s3ServerLink = BuildConfig.AWS_S3_SERVER_LINK + "profile_image_thum"
                        }
                        val imageAvatar = s3ServerLink + "/" + it.userName + ".jpg"
                        userId = it.userId
                        displayName = it.displayName
                        userName = it.userName
                        userAvatar = imageAvatar
                    }
                }
            }
        }
    }
}
