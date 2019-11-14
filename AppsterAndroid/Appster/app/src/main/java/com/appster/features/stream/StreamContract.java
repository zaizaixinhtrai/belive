package com.appster.features.stream;

import com.appster.core.adapter.DisplayableItem;
import com.appster.domain.RecordedMessagesModel;
import com.appster.features.mvpbase.BaseContract;
import com.appster.message.ChatItemModelClass;
import com.appster.models.DailyTopFanModel;
import com.appster.models.FaceUnityStickerModel;
import com.appster.models.StreamModel;
import com.appster.models.StreamTitleSticker;
import com.appster.models.TagListLiveStreamModel;
import com.appster.webservice.response.EndStreamDataModel;
import com.appster.webservice.response.LuckyWheelAwards;
import com.appster.webservice.response.SubStreamData;
import com.appster.webservice.response.VotingLevels;
import com.domain.models.TriviaFinishModel;
import com.domain.models.TriviaInfoModel;
import com.domain.models.TriviaResultModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThanhBan on 11/22/2016.
 */

public interface StreamContract {
    interface StreamView extends BaseContract.View {
        void onStreamCreateSuccess(StreamModel streamModel, boolean isRecorded);

        void onStreamBeginSuccess();

        void onTagCategoriesReceived(List<TagListLiveStreamModel> tagCategories);

        void onStreamViewCountChanged(int currentViewer, long viewedUserCount, int likeCount);

        void showForceStopStream(String message);

        void onStreamCreateError();

        void updateStars(long totalGold);
        void updatePoints(int totalPoint);

        void onEndStreamDataReceived(EndStreamDataModel streamDataModel);

        void showEndStreamLayout(String reason);

        void onMessageSuccess(ChatItemModelClass messageItem);

        void dismissErrorDialog();

        void onNaughtyWordsReceived(ArrayList<String> naughtyWords);

        void updateBufferedMessageInList(List<ChatItemModelClass> listChatItemModel);

        void storeBotJoinMessage(ChatItemModelClass messageItem);

        void endStreamShareUrl(String StreamResultUrl);

        int getShareOption();

        long getStreamDuration();

        boolean isShareLocation();

        void onLocationDetected();

        void onBlockSuccess(String blockedName, String userId, String slug, int streamId);

        void onUnblockSuccess(String blockedName, String userId, String slug, int streamId);

        void onMuteSuccess(String mutedName, String userId, String slug, int streamId);

        void onUnMuteSuccess(String mutedName, String userId, String slug, int streamId);

        void onSaveStreamSuccess();

        void onSaveStreamFailed(String message);

        void onStreamSecondCreate();

        void onVoteLevelsReceived(ArrayList<VotingLevels> votingLevels, int levelUnlockedIndex, int currentScore);

        void onVoteAwardReceived(ArrayList<LuckyWheelAwards> luckyWheelAwards);

        void onLuckyWheelResultReceived(int awardId);

        void onVotingScoresReceived(int votingScores);

        void onShowNotificationNetworkLow();

        void onHideNotificationNetworkLow();

        void onStreamRemoved(String message, EndStreamDataModel data);

        void onStreamTitleEnabled();

        void onPreviousStreamRemain();

        void onLastStreamDuration(long duration);

        ArrayList<RecordedMessagesModel> getRecordedMessages();

        void onOldChatMessages(List<RecordedMessagesModel> messages);

        void onStreamStickerReceived(StreamTitleSticker sticker);

        void onGetFaceUnityStickerSuccessfully(List<FaceUnityStickerModel> data);

        void onGetFaceUnityStickerFailed(String message);

        void notifyTopFanJoined(ChatItemModelClass joinMessage);

        int getRankByUserName(String userName);

        void onGetDailyTopFansListSuccessfully(List<DailyTopFanModel> dailyTopFansList);

        void onCallRequestFailedSinceUserHasLeftStream(String userName);

        void onSubStreamCreated(SubStreamData subStreamData);

        void onSubStreamAvailable(SubStreamData subStreamData);

        void onSubStreamDisconnected();

        void onTriviaInfoReceived(TriviaInfoModel triviaInfoModel);

        void displayTriviaQuestion(TriviaInfoModel.Questions question, int countDownTime);

        void displayTriviaWinnerList(TriviaFinishModel triviaFinishModel);

        void dismissTriviaQuestion();

        void displayTriviaResult(TriviaResultModel triviaResultModel, String questionText);

        void countDownToFaceTime(String content, int countDownSecs);

        void countDownToOffScreen(String content, int countDownSecs);

        void dismissTriviaResult();

        void dismissWinnerList();

        void showTriviaWinnerListData(List<DisplayableItem> triviaWinnerListPagingEntity);

        void getTriviaWinnerListError();

    }

    interface UserActions extends BaseContract.Presenter<StreamView> {
        void createStream(String streamTitle, int categoryId, boolean isRecorded, int frameRate, boolean isTriviaShow);

        void startStream();

        void endStream(long duration, String reason);

        //        void endStream();
        void getTagCategories();

        void sendMessage(String message);

        void sendMessage(ChatItemModelClass messageItem);

        void saveStreamChatMessage(List<RecordedMessagesModel> recordedMessages);

        void onBackPressed();

        void getNaughtyWords();

        void requestViewCountUpdate();

        void blockUser(String userId, String displayName);

        void muteUser(String userId, String displayName);

        void unMuteUser(String userId, String displayName);

        void unBlockUser(String userId, String displayName);

        void uploadFirstStreamImage(String slug, File bitmap);

        void updateTotalStars(long totalStars);

        void saveStream(String slug);

        void onLuckyWheelStarted(int luckyResult);

        void onLuckyWheelShowed();

        void getVoteAwards(int votingLevel);

        void getLuckyWheelResult(int votingLevel);

        void sendLuckyReceivedUser();

        void sendNotifyStreamPause();

        void sendNotifyStreamResume();

        void streamRemovedByAdmin(ChatItemModelClass adminMessage);

        void storeStreamTitlePosition(String title, float percentX, float percentY, String color);

        void sendStreamTitlePositionXmpp(String title, float percentX, float percentY, String color);

        void getAppConfig();

        void resumePreviousStream();

        void cancelPreviousStream();

        void networkInterupted();

        void networkResume();

        void getFaceUnityStickerList();

        void onStreamStatisticReceived(ChatItemModelClass chatItemModelClass);

        void callRequest(String userId, String userName);

        void notifySubStreamEnded();

        void startRTC();

        void notifySubStreamRejected();

        void notifySubStreamNoAnswer();

        void notifyCallConnected();

        void notifyCallReConnected();

        void getDailyTopFansList();

        void getTriviaInfo(int triviaInfo);

        void triviaGameStarted();

        void triviaAnswer(int optionId);

        void getTriviaWinnerList();

        void getTriviaQuestionInfo(int triviaInfo);

        /**
         * Call this method to notify the presenter handle location permission changed
         *
         * @param granted true if the permission is granted, false otherwise.
         */
        void onLocationPermissionChanged(boolean granted);

//        /**
//         * @param userIds    list of users who hit same actions in same interval time
//         * @param actionType like (1) or join (0) type
//         */
//        void callBotActionApi(List<String> userIds,int actionType);

    }
}
