package com.data.entity

import com.data.AESUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by thanhbc on 2/23/18.
 */

class TriviaInfoEntity(
        @SerializedName("TriviaId")
        val triviaId: Int = 0,
        @SerializedName("CurrentDateTime")
        val currentDateTime: Long = 0,
        @SerializedName("StartingDateTime")
        val startingDateTime: Long = 0,
        @SerializedName("ClientCurrnetDateTime")
        val clientCurrentDateTime: Long = 0,
        @SerializedName("NextQuestionDateTime")
        val nextQuestionDateTime: Long = 0,
        @SerializedName("NextQuestionId")
        val nextQuestionId: Int = 0,
        @SerializedName("FinishDateTime")
        val finishDateTime: Long = 0,
        @SerializedName("QuestionWaitingTime")
        val questionWaitingTime: Int = 0,
        @SerializedName("AnswerTime")
        val answerTime: Int = 0,
        @SerializedName("ResultWaitingTime")
        val resultWaitingTime: Int = 0,
        @SerializedName("ResultTime")
        val resultTime: Int = 0,
        @SerializedName("FinishTime")
        val finishTime: Int = 0,
        @SerializedName("CanPlay")
        val canPlay: Boolean = false,
        @SerializedName("EncHash")
        val hash: String?,
        @SerializedName("Message")
        val message: String?,
        @SerializedName("MessageTitle")
        val messageTitle: String?,
        @SerializedName("ReviveTime")
        val reviveWaitingTime: Int = 0,
        @SerializedName("ReviveCount")
        val reviveCount: Int = 0,
        @SerializedName("FinishWaitingTime")
        val finishWaitingTime: Int = 0,
        @SerializedName("IsFinished")
        val isFinished: Boolean = false,
        @SerializedName("ReviveAnim")
        val reviveAnim: Boolean = false,
        @SerializedName("CountryCode")
        val countryCode: String?
) {

    /**
     * TriviaId : 1
     * CurrentDateTime : 1519384662
     * StartingDateTime : 1519384962
     * NextQuestionDateTime : 1519384962
     * NextQuestionId : 1
     * FinishDateTime : 1519385752
     * QuestionWaitingTime : 60
     * AnswerTime : 10
     * ResultWaitingTime : 5
     * ResultTime : 10
     * FinishTime : 60
     * Questions : [{"Title":"test question1","QuestionId":1,"Options":[{"OptionId":1,"Option":"opt1-1"},{"OptionId":2,"Option":"opt1-2"},{"OptionId":3,"Option":"opt1-3"}]},{"Title":"test question2","QuestionId":2,"Options":[{"OptionId":1,"Option":"opt2-1"},{"OptionId":2,"Option":"opt2-2"},{"OptionId":3,"Option":"opt2-3"}]},{"Title":"test question3","QuestionId":3,"Options":[{"OptionId":1,"Option":"opt3-1"},{"OptionId":2,"Option":"opt3-2"},{"OptionId":3,"Option":"opt3-3"}]},{"Title":"test question4","QuestionId":4,"Options":[{"OptionId":1,"Option":"opt4-1"},{"OptionId":2,"Option":"opt4-2"},{"OptionId":3,"Option":"opt4-3"}]},{"Title":"test question5","QuestionId":5,"Options":[{"OptionId":1,"Option":"opt5-1"},{"OptionId":2,"Option":"opt5-2"},{"OptionId":3,"Option":"opt5-3"}]},{"Title":"test question6","QuestionId":6,"Options":[{"OptionId":1,"Option":"opt6-1"},{"OptionId":2,"Option":"opt6-2"},{"OptionId":3,"Option":"opt6-3"}]},{"Title":"test question7","QuestionId":7,"Options":[{"OptionId":1,"Option":"opt7-1"},{"OptionId":2,"Option":"opt7-2"},{"OptionId":3,"Option":"opt7-3"}]},{"Title":"test question8","QuestionId":8,"Options":[{"OptionId":1,"Option":"opt8-1"},{"OptionId":2,"Option":"opt8-2"},{"OptionId":3,"Option":"opt8-3"}]},{"Title":"test question9","QuestionId":9,"Options":[{"OptionId":1,"Option":"opt9-1"},{"OptionId":2,"Option":"opt9-2"},{"OptionId":3,"Option":"opt9-3"}]},{"Title":"test question10","QuestionId":10,"Options":[{"OptionId":1,"Option":"opt10-1"},{"OptionId":2,"Option":"opt10-2"},{"OptionId":3,"Option":"opt10-3"}]}]
     * CanPlay : true
     * Hash : {"Questions":[{"Title":"test question1","QuestionId":1,"Options":[{"OptionId":1,"Option":"opt1-1"},{"OptionId":2,"Option":"opt1-2"},{"OptionId":3,"Option":"opt1-3"}]},{"Title":"test question2","QuestionId":2,"Options":[{"OptionId":1,"Option":"opt2-1"},{"OptionId":2,"Option":"opt2-2"},{"OptionId":3,"Option":"opt2-3"}]},{"Title":"test question3","QuestionId":3,"Options":[{"OptionId":1,"Option":"opt3-1"},{"OptionId":2,"Option":"opt3-2"},{"OptionId":3,"Option":"opt3-3"}]},{"Title":"test question4","QuestionId":4,"Options":[{"OptionId":1,"Option":"opt4-1"},{"OptionId":2,"Option":"opt4-2"},{"OptionId":3,"Option":"opt4-3"}]},{"Title":"test question5","QuestionId":5,"Options":[{"OptionId":1,"Option":"opt5-1"},{"OptionId":2,"Option":"opt5-2"},{"OptionId":3,"Option":"opt5-3"}]},{"Title":"test question6","QuestionId":6,"Options":[{"OptionId":1,"Option":"opt6-1"},{"OptionId":2,"Option":"opt6-2"},{"OptionId":3,"Option":"opt6-3"}]},{"Title":"test question7","QuestionId":7,"Options":[{"OptionId":1,"Option":"opt7-1"},{"OptionId":2,"Option":"opt7-2"},{"OptionId":3,"Option":"opt7-3"}]},{"Title":"test question8","QuestionId":8,"Options":[{"OptionId":1,"Option":"opt8-1"},{"OptionId":2,"Option":"opt8-2"},{"OptionId":3,"Option":"opt8-3"}]},{"Title":"test question9","QuestionId":9,"Options":[{"OptionId":1,"Option":"opt9-1"},{"OptionId":2,"Option":"opt9-2"},{"OptionId":3,"Option":"opt9-3"}]},{"Title":"test question10","QuestionId":10,"Options":[{"OptionId":1,"Option":"opt10-1"},{"OptionId":2,"Option":"opt10-2"},{"OptionId":3,"Option":"opt10-3"}]}]}
     */


    private val questions: List<Questions>? = null


    fun getQuestions(): List<Questions>? {
        return getQuestionFromHash(getDecryptedHash(hash))
    }

    private fun getQuestionFromHash(hash: String?): List<Questions>? {
        return try {
            val gson = Gson()
            val jsonObject = JSONObject(hash)
            val jsonArray = jsonObject.getJSONArray("Questions")
            val listType = object : TypeToken<List<Questions>>() {}.type
            gson.fromJson<List<Questions>>(jsonArray.toString(), listType)

        } catch (e: JSONException) {
            e.printStackTrace()
            ArrayList()
        }

    }

    private fun getDecryptedHash(hash: String?): String? {
        var pass = java.lang.Long.toString(startingDateTime)
        pass += pass
        val sub = pass.substring(0, 16)
        val utils = AESUtils(sub, sub)
        return utils.decrypt(hash!!)
    }


    class Questions(
            /**
             * Title : test question1
             * QuestionId : 1
             * Options : [{"OptionId":1,"Option":"opt1-1"},{"OptionId":2,"Option":"opt1-2"},{"OptionId":3,"Option":"opt1-3"}]
             */
            @SerializedName("Title")
            val title: String?,
            @SerializedName("QuestionId")
            val questionId: Int = 0,
            @SerializedName("Options")
            val options: List<Options>?
    ) {
        class Options(
                /**
                 * OptionId : 1
                 * Option : opt1-1
                 */
                @SerializedName("OptionId")
                val optionId: Int = 0,
                @SerializedName("Option")
                val option: String?
        )
    }
}
