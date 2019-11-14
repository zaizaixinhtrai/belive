package com.appster.features.stream;


import android.app.Activity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.R;
import com.appster.customview.trivia.TriviaGameState;
import com.appster.domain.RecordedMessagesModel;
import com.appster.location.GPSTClass;
import com.appster.manager.AgoraChatManager;
import com.appster.manager.AppsterChatManger;
import com.appster.manager.ShowErrorManager;
import com.appster.message.ChatItemModelClass;
import com.appster.models.DailyTopFanModel;
import com.appster.models.NetworkUploadSpeedModel;
import com.appster.models.StreamModel;
import com.appster.models.StreamTitleSticker;
import com.appster.models.TopFanModel;
import com.appster.models.TopFansList;
import com.appster.models.UserModel;
import com.appster.utility.AppsterUtility;
import com.appster.utility.RxUtils;
import com.appster.utility.SocialManager;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.BeginStreamRequestModel;
import com.appster.webservice.request_models.BlockUserRequestModel;
import com.appster.webservice.request_models.BotActionsRequestModel;
import com.appster.webservice.request_models.BotFollowRequestModel;
import com.appster.webservice.request_models.BotSendGiftRequestModel;
import com.appster.webservice.request_models.ChatBotUserRequestModel;
import com.appster.webservice.request_models.CreateStreamRequestModel;
import com.appster.webservice.request_models.GetTopFanModel;
import com.appster.webservice.request_models.LuckyWheelSpinResultRequest;
import com.appster.webservice.request_models.SpinRequest;
import com.appster.webservice.request_models.StreamChatHistoryRequestModel;
import com.appster.webservice.request_models.StreamDefaultImageRequest;
import com.appster.webservice.request_models.SubStreamRequest;
import com.appster.webservice.request_models.UnblockUserRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.ChatBotDataResultModel;
import com.appster.webservice.response.ChatBotUserModel;
import com.appster.webservice.response.EndStreamDataModel;
import com.appster.webservice.response.SendGiftResponseModel;
import com.appster.webservice.response.StatisticStream;
import com.appster.webservice.response.SubStreamData;
import com.appster.webservice.response.VotingLevels;
import com.apster.common.Constants;
import com.apster.common.FileUtility;
import com.apster.common.LogUtils;
import com.data.exceptions.BeLiveServerException;
import com.data.repository.TriviaDataRepository;
import com.data.repository.datasource.cloud.CloudTriviaDataSource;
import com.domain.interactors.trivia.TriviaAnswerUseCase;
import com.domain.interactors.trivia.TriviaFinishUseCase;
import com.domain.interactors.trivia.TriviaInfoHostUseCase;
import com.domain.interactors.trivia.TriviaQuestionUseCase;
import com.domain.interactors.trivia.TriviaResultUseCase;
import com.domain.interactors.trivia.TriviaWinnerListUseCase;
import com.domain.models.TriviaInfoModel;
import com.domain.repository.TriviaRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pack.utility.CheckNetwork;
import com.pack.utility.StringUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import wowza.gocoder.sdk.app.WowzaConstant;

import static com.appster.location.GetAddress.getCountryCode;
import static com.appster.location.GetAddress.getUserCountry;
import static com.appster.utility.AppsterUtility.deletePrefListObjectByKey;
import static com.facebook.FacebookSdk.getApplicationContext;
import static rx.plugins.RxJavaHooks.onError;

/**
 * Created by ThanhBan on 11/22/2016.
 */
public class StreamPresenter implements StreamContract.UserActions,
        AppsterChatManger.StreamChatGroupListener {

    static final int BUFFER_DELAYED_TIME_IN_SECOND = 1;
    public static final String CURRENT_STREAM_SLUG = "current_stream_slug";
    public static final String STORE_CHAT_PREFS = "messagesList";
    private StreamContract.StreamView mView;
    private AppsterWebserviceAPI mService;
    private String mAuthen;
    private CompositeSubscription mCompositeSubscription;
    private CompositeSubscription mBotCompositeSubscription;

    private AppsterChatManger mChatManager;
    private String mSlug;
    private int mStreamId;
    private AppsterApplication mAppsterApplication;
    private UserModel mUserData;
    private SparseArray<String> botLikedMap;
    private SparseArray<String> mStreamBlockeddMap;
    private long countUserHaveBeenView = 0;
    private int countLike;
    private long totalGoldFans = 0L;
    private long totalGoldOfStream = 0L;
    private PublishSubject<String> botJoinObservable;
    private PublishSubject<String> botLikeObservable;
    private PublishSubject<ChatBotUserModel> botJoinXmppObservable;
    private boolean isEndStream = false;
    private PublishSubject<Long> mIntervalSubject = PublishSubject.create();
    private ArrayList<String> mLiveStreamWatcherModels;
    private StreamModel mRestartStreamModel;
    private long mLastDuration;
    GPSTClass gpstClass;
    private int mCurrentStreamBitrate;
    private AtomicBoolean mIsPausing = new AtomicBoolean(false);
    private boolean mIsChatGroupCreated;
    List<String> mTopFanList;
    private SubStreamData mSubStreamData;
    private String mSubSlug = "";
    private boolean shouldEndPreviousVideoCall = false;
    private RxPermissions mRxPermissions;
    private boolean isLocationPermisionEnable = false;
    private boolean mIsTriviaShow = false;
    private final TriviaInfoHostUseCase mTriviaInfoHostUseCase;
    private final TriviaResultUseCase mTriviaResultUseCase;
    private final TriviaAnswerUseCase mTriviaAnswerUseCase;
    private final TriviaFinishUseCase mTriviaFinishUseCase;
    private final TriviaWinnerListUseCase mTriviaWinnerListUseCase;
    private final TriviaQuestionUseCase mTriviaQuestionUseCase;
    SparseIntArray mTriviaGameStateMap;
    TriviaInfoModel mCurrentTriviaModel;
    protected TriviaInfoModel.Questions mCurrentTriviaQuestion;
    private AtomicInteger mQuestionIndex = new AtomicInteger(1);
    private AgoraChatManager mAgoraChatManager;
    private int mIndexWinnerList;
    private boolean mIsEndWinnerList;

    public StreamPresenter(StreamContract.StreamView view, AppsterWebserviceAPI service, AppsterChatManger chatManger, AgoraChatManager chatAgoraManager, UserModel streamUserData) {
        attachView(view);
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        mBotCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mBotCompositeSubscription);
        mService = service;
        mChatManager = chatManger;
        mAgoraChatManager = chatAgoraManager;
        mAuthen = AppsterUtility.getAuth();
        TriviaRepository triviaDataSource = new TriviaDataRepository(new CloudTriviaDataSource(service, mAuthen));
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        mAppsterApplication = AppsterApplication.get(view.getViewContext());
        mUserData = streamUserData;
        mAgoraChatManager.login(view.getViewContext().getString(R.string.agora_app_id), mUserData.getUserName());
        botLikedMap = new SparseArray<>();
        mStreamBlockeddMap = new SparseArray<>();
        mRxPermissions = new RxPermissions((Activity) view.getViewContext());
        listenBotJoinObservable();
        listenBotLikeObservable();
        listenBotJoinXmppObservable();
        String previousSlug = AppsterUtility.readSharedSetting(mView.getViewContext(), CURRENT_STREAM_SLUG, "");
        if (!previousSlug.isEmpty()) {
            checkResumeable(previousSlug);
        }
        mTriviaInfoHostUseCase = new TriviaInfoHostUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaResultUseCase = new TriviaResultUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaAnswerUseCase = new TriviaAnswerUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaFinishUseCase = new TriviaFinishUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaWinnerListUseCase = new TriviaWinnerListUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaQuestionUseCase = new TriviaQuestionUseCase(uiThread, ioThread, triviaDataSource);
    }

    private final Func2<BaseResponse<StreamModel>, BaseResponse<TopFansList>, BaseResponse<StreamModel>> mMappingTopFanWithStreamFunc = (stream, topFan) -> {
        if (stream.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) return stream;
        List<String> topFanList = new ArrayList<>();
        List<DailyTopFanModel> dailyTopFansList = new ArrayList<>();
        if (topFan.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
            List<TopFanModel> topFanModels = topFan.getData().topFans.getResult();
            for (int i = 0; i < Math.min(3, topFanModels.size()); i++) {
                topFanList.add(topFanModels.get(i).getUserName());
            }
            dailyTopFansList = topFan.getData().dailyTopFans;
        }
        stream.getData().rankingList = topFanList;
        stream.getData().dailyTopFansList = dailyTopFansList;
        return stream;
    };

    private void checkResumeable(String previousSlug) {
        GetTopFanModel getTopFanModel = new GetTopFanModel();
        getTopFanModel.setUserId(mUserData.getUserId());
        mCompositeSubscription.add(
                Observable.zip(getStreamResumeDetail(previousSlug),
                        mService.getAllTopFanStream(mAuthen, getTopFanModel.getMappedRequest()),
                        mMappingTopFanWithStreamFunc)
                        .subscribe(streamDetail -> {
                            if (streamDetail.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                StreamModel streamModel = streamDetail.getData();
                                mLastDuration = streamModel.getDuration();
                                mSlug = streamModel.getSlug();
                                mSubStreamData = streamModel.subStream;
                                shouldEndPreviousVideoCall = mSubStreamData != null && mSubStreamData.status != 2;
                                if (shouldEndPreviousVideoCall) mSubSlug = mSubStreamData.slug;
                                if (mView != null) {
                                    mView.onPreviousStreamRemain();
                                    mView.updateStars(streamDetail.getData().getPublisher().getTotalGoldFans());
                                    notifyPointsUpdated(streamDetail.getData().getHostPoint());
                                    mView.onLastStreamDuration(mLastDuration);
                                    mView.onOldChatMessages(AppsterUtility.loadPrefListObject(mView.getViewContext(), STORE_CHAT_PREFS, mSlug, RecordedMessagesModel[].class));
                                }

                                storeSlugForReconnect(mSlug);
                                mStreamId = streamModel.getStreamId();
                                mRestartStreamModel = streamModel;
                                mCurrentVotingScores = streamDetail.getData().getPublisher().getVotingScores();

                            }
                        }, Timber::e));
    }

    //region streaming

    @Override
    public void onStreamStatisticReceived(ChatItemModelClass chatItemModelClass) {
        if (chatItemModelClass == null) return;
//        if (countUserHaveBeenView < chatItemModelClass.getTotalViewers()) {
        countUserHaveBeenView = chatItemModelClass.getTotalViewers();
        requestViewCountUpdate();
//        }
    }

    @Override
    public void requestViewCountUpdate() {
        if (mView != null)
            mView.onStreamViewCountChanged(mIsTriviaShow ? mAgoraChatManager.getArrayCurrentUserInStream().size() : mChatManager.getArrayCurrentUserInStream().size(), countUserHaveBeenView, countLike);
    }

    @Override
    public void muteUser(String userId, String displayName) {
        mCompositeSubscription.add(mService.muteUser(mAuthen, mSlug, Integer.valueOf(userId))
                .subscribe(booleanBaseResponse -> Timber.e(String.valueOf(booleanBaseResponse.getData())), Timber::e));
        sendGroupMessage(createMuteMessageModel(mSlug, ChatItemModelClass.CHAT_TYPE_MUTE, userId));
        if (mView != null) mView.onMuteSuccess(displayName, userId, mSlug, mStreamId);
    }


    @Override
    public void unMuteUser(String userId, String displayName) {
        mCompositeSubscription.add(mService.unMuteUser(mAuthen, mSlug, userId)
                .subscribe(booleanBaseResponse -> Timber.e(String.valueOf(booleanBaseResponse.getData())), Timber::e));
        sendGroupMessage(createMuteMessageModel(mSlug, ChatItemModelClass.CHAT_TYPE_UNMUTE, userId));
        if (mView != null) mView.onUnMuteSuccess(displayName, userId, mSlug, mStreamId);
    }

    @Override
    public void blockUser(String userId, String displayName) {
        BlockUserRequestModel request = new BlockUserRequestModel();
        request.setBlockUserId(userId);
        mStreamBlockeddMap.put(Integer.valueOf(userId), displayName);
        //for bot
        if (mStreamBotList != null) {
            mCompositeSubscription.add(Observable.from(mStreamBotList)
                    .takeFirst(chatBotUserModel -> chatBotUserModel.userId == Integer.valueOf(userId))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::removeBotInStream, this::handleError));
        }
        //for real user
        mCompositeSubscription.add(AppsterWebServices.get().blockUser(mAuthen, request)
                .filter(blockOrReportUserDataResponse -> blockOrReportUserDataResponse != null)
                .subscribe(blockOrReportUserDataResponse -> {
                    sendGroupMessage(createBlockMessageModel(mSlug, ChatItemModelClass.CHAT_TYPE_BLOCK, userId));
                    if (mView != null) mView.onBlockSuccess(displayName, userId, mSlug, mStreamId);
                }, this::handleError));
    }

    @Override
    public void unBlockUser(String userId, String displayName) {
        UnblockUserRequestModel request = new UnblockUserRequestModel();
        request.setUnBlockUserId(userId);

        mCompositeSubscription.add(AppsterWebServices.get().unblockUser(mAuthen, request)
                .subscribe(reportUserResponseModel -> {
                    if (mView != null)
                        mView.onUnblockSuccess(displayName, userId, mSlug, mStreamId);
                }, this::handleError));
    }


    private int mCurrentVotingScores;

    @Override
    public void createStream(String streamTitle, int categoryId, boolean isRecorded, int frameRate, boolean isTriviaShow) {
        if (frameRate == 0) return;

        mCurrentStreamBitrate = frameRate;
        CreateStreamRequestModel requestModel = new CreateStreamRequestModel();
        if (!StringUtil.isNullOrEmptyString(streamTitle)) {
            requestModel.setStreamTitle(StringUtil.encodeString(streamTitle));
        }
        requestModel.setTagId(categoryId);
        requestModel.setRecored(isRecorded);
        requestModel.setWowzaApplicationName(WowzaConstant.APPLICATION_NAME_RECORDING);
        requestModel.setWowzaVideoFrameRate(frameRate);
        requestModel.isTrivia = isTriviaShow;
        mIsTriviaShow = isTriviaShow;
        if (mView != null) {
            if (isLocationPermisionEnable && gpstClass.canGetLocation() && mView.isShareLocation()) {
                double lat = gpstClass.getLatitude();
                double lon = gpstClass.getLongitude();
                requestModel.setLatitude(lat);
                requestModel.setLongitude(lon);
                requestModel.setCountryCode(getCountryCode(mView.getViewContext(), lat, lon));
            } else {
                requestModel.setCountryCode(getUserCountry(mView.getViewContext()));
            }
        }

        GetTopFanModel getTopFanModel = new GetTopFanModel();
        getTopFanModel.setUserId(mUserData.getUserId());
        mCompositeSubscription.add(
                Observable.zip(mService.createStream(mAuthen, requestModel),
                        mService.getAllTopFanStream(mAuthen, getTopFanModel.getMappedRequest()),
                        mMappingTopFanWithStreamFunc)
                        .filter(verifyUsernameDataResponse -> verifyUsernameDataResponse != null)
                        .subscribe(createStreamResponseModel -> {
                            if (createStreamResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                if (mView != null) {
                                    mView.updateStars(createStreamResponseModel.getData().getPublisher().getTotalGoldFans());
                                    notifyPointsUpdated(createStreamResponseModel.getData().getHostPoint());
                                    mView.endStreamShareUrl(createStreamResponseModel.getData().getWebStreamUrl());
                                }

                                StreamModel streamModel = createStreamResponseModel.getData();
                                mSlug = streamModel.getSlug();
                                storeSlugForReconnect(mSlug);
                                mStreamId = streamModel.getStreamId();
                                mCurrentVotingScores = createStreamResponseModel.getData().getPublisher().getVotingScores();
                                if (mView != null) {
                                    mView.onStreamCreateSuccess(streamModel, isRecorded);
                                    shareStreamToSocial(streamModel.getWebStreamUrl());
                                }

                            } else if (createStreamResponseModel.getCode() == ShowErrorManager.stream_block) {
                                if (mView != null) mView.onStreamCreateError();
                            } else if (createStreamResponseModel.getCode() == ShowErrorManager.stream_create_second) {
                                if (mView != null) mView.onStreamSecondCreate();
                            } else {
                                if (mView != null)
                                    mView.loadError(createStreamResponseModel.getMessage(), createStreamResponseModel.getCode());
                            }
                        }, this::handleError));
    }

    private void storeSlugForReconnect(String slug) {
        if (mView != null) {
            storeStreamSlugPrefs(slug);
        }
    }

    private void shareStreamToSocial(String linkUrl) {

        if (mView != null) {
            switch (mView.getShareOption()) {
                case StreamingActivityGLPlus.FACEBOOK:
//                    SocialManager.getInstance().shareFBStream(mView.getViewContext(), mView.getViewContext().getString(R.string.sns_facebook_message), linkUrl);
                    break;
                case StreamingActivityGLPlus.TWITTER:
                    SocialManager.getInstance().shareTweet(mView.getViewContext(), mView.getViewContext().getString(R.string.sns_default_message), linkUrl);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void resumePreviousStream() {
        if (mRestartStreamModel != null && mView != null && !mSlug.isEmpty()) {
            mCompositeSubscription.add(mService.resumeStream(mAuthen, mSlug)
                    .subscribe(booleanBaseResponse -> {
                        mIsTriviaShow = mRestartStreamModel.isTrivia;
                        mView.onStreamCreateSuccess(mRestartStreamModel, true);
                        getStreamTitleSticker(mSlug);
                    }, Timber::e));

        }
    }

    private void getStreamTitleSticker(String streamSlug) {
        mCompositeSubscription.add(mService.getStreamTitleSticker("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), streamSlug)
                .subscribe(streamTitleStickerBaseResponse -> {
                    Gson gson = new GsonBuilder().create();
                    StreamTitleSticker streamTitleSticker = gson.fromJson(streamTitleStickerBaseResponse.getData(), StreamTitleSticker.class);
                    if (mView != null) mView.onStreamStickerReceived(streamTitleSticker);
                }, Timber::e));
    }

    @Override
    public void cancelPreviousStream() {
        mRestartStreamModel = null;
        mSubStreamData = null;
        storeStreamSlugPrefs("");
        deletePrefListObjectByKey(mView.getViewContext(), STORE_CHAT_PREFS, mSlug);
        //call api to end previous stream
        mCompositeSubscription.add(mService.endStream(mAuthen, new BeginStreamRequestModel((mSlug)))
                .subscribe(endStreamDataModelBaseResponse -> {
                }, Timber::e));

    }

    boolean isStartStream = false;

    @Override
    public void startStream() {

        if (mRestartStreamModel != null) {
            if (!isStartStream) setUpGroupChatRequirement(mSlug);
            sendNotifyStreamResume();
        } else if (!isStartStream) {
            //start stream only call 1 times
            mCompositeSubscription.add(mService.beginStream(mAuthen, new BeginStreamRequestModel(mSlug))
                    .filter(beginStreamResponseModel -> beginStreamResponseModel != null)
                    .subscribe(beginStreamResponseModel -> setUpGroupChatRequirement(mSlug), this::handleError));
        }
        isStartStream = true;
    }

    private void setUpGroupChatRequirement(String slug) {
        if (mView != null) mView.onStreamBeginSuccess();
        Timber.e("setUpGroupChatRequirement");
        if (mIsTriviaShow) {
            mAgoraChatManager.joinGroup(slug, this, mUserData.getUserName());
        } else {
            mChatManager.setStreamChatGroupListener(this);
            mChatManager.createGroupChat(slug, true, mUserData.getUserName());
        }

        getCurrentEvents();
        setUpChatBotUsers(slug, 0, 0);
        pingStatistic(slug);
        pingNetworkUploadSpeed(slug);
    }

    private void getCurrentEvents() {
        //assumes that event is available
//        mCompositeSubscription.add(mService.getEventSetting(mAuthen)
//                .compose(mAppsterApplication.applySchedulers())
//                .map(BaseResponse::getData)
//                .filter(votingSetting -> votingSetting!=null)
//                .filter(votingSetting -> votingSetting.luckyWheelStatus)
//                .subscribe(votingSetting -> getVotingLevels(),this::handleError));
    }

    private Subscription mStatisticSubscription;

    private void pingStatistic(String slug) {
        mStatisticSubscription = mIntervalSubject.mergeWith(Observable.interval(30, TimeUnit.SECONDS))
                .takeWhile(time -> time != -1)
                .filter(aLong -> !mIsPausing.get())
                .flatMap(intervalTime -> getStatistic(slug))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(statisticStreamBaseResponse -> statisticStreamBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .map(BaseResponse::getData)
                .filter(statisticStream -> statisticStream != null)
                .subscribe(statisticData -> {
                    countUserHaveBeenView = statisticData.getViewCount();
                    countLike = statisticData.getLikeCount();
                    requestViewCountUpdate();
                    sendMessageUpdateViewCount(countUserHaveBeenView, countLike);
                    if (statisticData.getStatus() == Constants.StreamStatus.StreamEnd && mView != null) {
                        mView.showForceStopStream(statisticData.getStatusMessage());
                    }
                }, this::handleBotError, () -> Timber.e("statistic completed"));
    }

    private void sendMessageUpdateViewCount(long countUserHaveBeenView, int countLike) {
        ChatItemModelClass itemModelClass = createMessageModelWithType(ChatItemModelClass.CHAT_TYPE_STATISTIC);
        itemModelClass.setTotalViewers(countUserHaveBeenView);
        itemModelClass.setTotalLikes(countLike);
        itemModelClass.setMsg("");
        sendGroupMessage(itemModelClass);

    }

    private Observable<BaseResponse<StreamModel>> getStreamDetail(String slug) {
        return mService.streamDetail(mAuthen, new BeginStreamRequestModel(slug))
                .filter(streamModelBaseResponse -> streamModelBaseResponse != null);
    }

    private Observable<BaseResponse<StreamModel>> getStreamResumeDetail(String slug) {
        return mService.streamResumeDetail(mAuthen, slug)
                .filter(streamModelBaseResponse -> streamModelBaseResponse != null);
    }

    private Observable<BaseResponse<StatisticStream>> getStatistic(String slug) {
        return mService.statisticStream(mAuthen, new BeginStreamRequestModel(slug))
                .onErrorResumeNext(Observable.empty())
                .filter(statisticStreamResponseModel -> statisticStreamResponseModel != null);
    }


    private void pingNetworkUploadSpeed(String mSlug) {

        mCompositeSubscription.add(mIntervalSubject.mergeWith(Observable.interval(10, TimeUnit.SECONDS))
                .takeWhile(time -> time != -1)
                .flatMap(aLong -> getBytesInRate(mSlug))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(networkUploadSpeedModel -> mView != null)
                .doOnError(error -> {
                    if (error instanceof SocketTimeoutException || error instanceof UnknownHostException) {
                        Timber.e("doOnError %s", error.getMessage());
                        if (mView != null) mView.onShowNotificationNetworkLow();
                    }
                })
                .subscribe(networkUploadSpeedModel -> {

                    double speed = networkUploadSpeedModel.getBytesInRate() * 8 / 1000.0;
                    Timber.e("networkUploadSpeedModel--------------------------->" + speed);
                    if (speed <= mCurrentStreamBitrate - 50) {
                        mView.onShowNotificationNetworkLow();
                    } else {
                        mView.onHideNotificationNetworkLow();
                    }

                }, this::handleBotError, () -> Timber.e("pingNetworkUploadSpeed completed")));
    }

    private Observable<NetworkUploadSpeedModel> getBytesInRate(String mSlug) {
        return mService.getBytesInRate("application/json", BuildConfig.CHECK_NETWORK_UPLOAD_SPEED + mSlug)
                .onErrorResumeNext(Observable.empty())
                .filter(networkUploadSpeedModel -> networkUploadSpeedModel != null);

    }

    private void endStream(String reason) {
        if (mView != null) {
            mView.showEndStreamLayout(reason);
            mView.dismissErrorDialog();
        }
        cancelAllBotThreads();
        mCompositeSubscription.add(mService.endStream(mAuthen, new BeginStreamRequestModel((mSlug)))
                .filter(endStreamDataResponse -> endStreamDataResponse != null)
                .subscribe(endStreamDataResponse -> {
                    if (endStreamDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        EndStreamDataModel data = endStreamDataResponse.getData();
                        Timber.e("send end stream message");
                        countUserHaveBeenView = data.getViewCount();
                        countLike = data.getLikeCount();
                        totalGoldOfStream = data.getTotalGold();
                        Timber.e("duration %d, like %d, view %d, stars %d", data.getDuration(), countLike, countUserHaveBeenView, totalGoldOfStream);
                        sendGroupMessage(createMessageModel(String.valueOf(data.getDuration()), ChatItemModelClass.CHAT_TYPE_END));
                        Timber.e("send end stream message completed");
                        isEndStream = true;
                        if (mChatManager != null) mChatManager.leaveRoom();
                        if (mView != null) {
                            mView.onEndStreamDataReceived(data);
                            storeStreamSlugPrefs("");

                        }

                    } else {
                        if (mView != null)
                            mView.loadError(endStreamDataResponse.getMessage(), endStreamDataResponse.getCode());
                    }
                }, error -> {
                    if (mChatManager != null) mChatManager.leaveRoom();
                }));
    }

    /**
     * store/delete stream slug
     *
     * @param settingValue - slug or empty string
     */
    private void storeStreamSlugPrefs(String settingValue) {
        if (settingValue == null) return;
        //slug in prefs
        AppsterUtility.saveSharedSetting(mView.getViewContext(), CURRENT_STREAM_SLUG, settingValue);
    }

    @Override
    public void endStream(long duration, String reason) {
        mIntervalSubject.onNext(-1L);
        endStream(reason);
        RxUtils.unsubscribeIfNotNull(mStatisticSubscription);

    }

    @Override
    public void getNaughtyWords() {
        mCompositeSubscription.add(AppsterWebServices.get().getNaughtyWords()
                .filter(getNaughtyWordDataResponse -> getNaughtyWordDataResponse.getData() != null)
                .subscribe(getNaughtyWordDataResponse -> mView.onNaughtyWordsReceived(getNaughtyWordDataResponse.getData()), this::handleError));
    }

    //endregion
    //region bot region

    private void listenBotJoinXmppObservable() {
        botJoinXmppObservable = PublishSubject.create();

        Observable<ChatBotUserModel> debouncedJoinXmppEmitter = botJoinXmppObservable.debounce(500, TimeUnit.MILLISECONDS);
        Observable<List<ChatBotUserModel>> deboundJoinXmppEmitter = botJoinXmppObservable.buffer(debouncedJoinXmppEmitter);
        mBotCompositeSubscription.add(deboundJoinXmppEmitter
                .map(chatBotUserModels -> {
                    List<ChatItemModelClass> listChatItemModel = null;
                    for (ChatBotUserModel newBot : chatBotUserModels) {
                        ChatItemModelClass chatItem = new ChatItemModelClass();
                        chatItem.setUserName(newBot.userName);
                        chatItem.setChatDisplayName(newBot.displayName);
                        chatItem.setMessageType(ChatItemModelClass.CHAT_TYPE_BOT_JOIN_LIST);
                        chatItem.setMsg(getApplicationContext().getResources().getString(R.string.message_joined));

                        if (listChatItemModel == null) {
                            listChatItemModel = new ArrayList<>();
                        }
                        listChatItemModel.add(chatItem);

                        if (mIsTriviaShow) {
                            mAgoraChatManager.addBotUserViewer(newBot.userName);
                        } else {
                            mChatManager.addBotUserViewer(newBot.userName);
                        }
                        if (mView != null) {
                            mView.storeBotJoinMessage(chatItem);
                            mView.notifyTopFanJoined(chatItem);
                        }
                        sendBotJoinMessage(newBot);
                    }
                    return listChatItemModel;
                })
                .filter(listChatItemModels -> listChatItemModels != null)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listChatItemModels ->mView.updateBufferedMessageInList(listChatItemModels), this::handleBotError));
    }

    private void listenBotLikeObservable() {
        botLikeObservable = PublishSubject.create();
        //
        //send bot's action like
        Observable<String> debouncedLikeEmitter = botLikeObservable.debounce(BUFFER_DELAYED_TIME_IN_SECOND, TimeUnit.SECONDS);
        Observable<List<String>> debouncedLikeBufferEmitter = botLikeObservable.buffer(debouncedLikeEmitter);
        mBotCompositeSubscription.add(debouncedLikeBufferEmitter
                .map(userIds -> TextUtils.join(",", userIds))
                .filter(strUserIds -> !TextUtils.isEmpty(strUserIds))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userIds -> callBotActionApi(userIds, ChatBotUserModel.TYPE_LIKE),
                        this::handleBotError));
    }

    private void listenBotJoinObservable() {
        botJoinObservable = PublishSubject.create();
        //send bot's action join
        Observable<String> debouncedJoinEmitter = botJoinObservable.debounce(BUFFER_DELAYED_TIME_IN_SECOND, TimeUnit.SECONDS);
        Observable<List<String>> debouncedJoinBufferEmitter = botJoinObservable.buffer(debouncedJoinEmitter);
        mBotCompositeSubscription.add(debouncedJoinBufferEmitter
                .map(userIds -> TextUtils.join(",", userIds))
                .filter(strUserIds -> !TextUtils.isEmpty(strUserIds))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userIds -> callBotActionApi(userIds, 0),
                        this::handleBotError));

    }

    /**
     * @param userIds    list of users who hit same actions in same interval time
     * @param actionType like (1) or join (0) type
     */
    private void callBotActionApi(String userIds, int actionType) {
        if (isEndStream) return;

        if (StringUtil.isNullOrEmptyString(mSlug)) {
            return;
        }

        Timber.d("**botactions** ids: %s", userIds);
        Timber.d("**botactions** length: %s", userIds.split(",").length);
        Timber.d("**botactions** action type: %s", String.valueOf(actionType));

        BotActionsRequestModel botActionsRequestModel = new BotActionsRequestModel(mSlug, userIds, actionType);
        mBotCompositeSubscription.add(AppsterWebServices.get().botActions(mAuthen, botActionsRequestModel)
                .subscribe(statisticStreamResponseModel -> {
                    if (statisticStreamResponseModel != null) {
                        StatisticStream statisticStream = statisticStreamResponseModel.getData();
                        if (statisticStream != null) {
                            countUserHaveBeenView = statisticStream.getViewCount();
                            countLike = statisticStream.getLikeCount();
                            if (mView != null)
                                mView.onVotingScoresReceived(statisticStream.getVotingScores());
                            requestViewCountUpdate();
                        }
                    }
                }, this::handleBotError));

    }


    void sendBotJoinMessage(ChatBotUserModel chatBotUserModel) {
        notifyViewers(chatBotUserModel);
    }

    private void setUpChatBotUsers(String slug, int lastTime, int page) {
        ChatBotUserRequestModel chatBotUserRequestModel = new ChatBotUserRequestModel(slug, lastTime, page);
        mCompositeSubscription.add(mService.getBotUsers(mAuthen, chatBotUserRequestModel)
                .flatMap(chatBotUsersResponse -> {
                    switch (chatBotUsersResponse.getCode()) {
                        case Constants.RESPONSE_FROM_WEB_SERVICE_OK:
                            return Observable.just(chatBotUsersResponse.getData());
                        default:
                            return Observable.error(new BeLiveServerException(chatBotUsersResponse.getMessage(), chatBotUsersResponse.getCode()));
                    }
                })
                .retryWhen(errors -> errors.zipWith(Observable.range(1, 3), (n, i) ->
                        i < 3 ?
                                Observable.timer((int) Math.pow(3, i), TimeUnit.SECONDS) :
                                Observable.error(n))
                        .flatMap(x -> x))
                .filter(chatBotDataResultModel -> chatBotDataResultModel != null)
                .subscribe(chatBotUsers -> {
                    if (!chatBotUsers.botUsers.isEmpty()) {
                        Timber.d("** bots %s", String.valueOf(chatBotUsers.botUsers.size()));
                        setUpTimeDisplayForChatBot(chatBotUsers);
                    }
                }, error -> {
                    if (error instanceof BeLiveServerException) {
                        BeLiveServerException e = (BeLiveServerException) error;
                        if (mView != null)
                            mView.loadError(e.getMessage(), e.code);

                        Timber.e(e.code + " " + e.getMessage());
                    } else {
                        this.handleBotError(error);
                    }
                }));
    }

    int mCurrentPassedTime = 5;
    int mCurrentBotIndex = 0;
    List<ChatBotUserModel> mStreamBotList;

    private void setUpTimeDisplayForChatBot(ChatBotDataResultModel data) {
        mStreamBotList = data.botUsers;
        int botSize = mStreamBotList.size();
        final ChatBotUserModel lastBot = createCompletedBot(mStreamBotList.get(botSize - 1));
        final int repeatTime = data.repeatTime;
        if (mRestartStreamModel != null) {
            //store all previous bots
            mCurrentPassedTime = (int) mLastDuration;
            Timber.e("mRestartStreamModel not null");
            //loop all bots to save
            mCompositeSubscription.add(Observable.from(mStreamBotList)
                    .filter(chatBotUserModel -> chatBotUserModel.atTime <= mCurrentPassedTime)
                    .doOnNext(chatBotUserModel -> {
                        if (chatBotUserModel.getActionType() == ChatBotUserModel.TYPE_LIKE) {
                            botLikedMap.put(chatBotUserModel.userId, chatBotUserModel.userName);
                        }
                        mCurrentBotIndex++;
                        Timber.e(String.valueOf(mCurrentBotIndex));
                    })
                    .filter(chatBotUserModel -> (chatBotUserModel.atTime + chatBotUserModel.leaveTime) <= mCurrentPassedTime)
                    .map(chatBotUserModel -> chatBotUserModel.userName)
                    .doOnNext(userName -> Timber.e(userName))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(chatBotUserModels -> mChatManager != null)
                    .subscribe(botUserName -> {
                        if (mIsTriviaShow) {
                            mAgoraChatManager.addBotUserViewer(botUserName);
                        } else {
                            mChatManager.addBotUserViewer(botUserName);
                        }
                    }, Timber::e));
        }

        mBotCompositeSubscription.add(mIntervalSubject.mergeWith(Observable.interval(repeatTime, repeatTime, TimeUnit.SECONDS))
                .takeWhile(time -> time != -1)
                .map(aLong -> {
                    List<ChatBotUserModel> result = null;
//                    Timber.d("** action index %s", String.valueOf(mCurrentBotIndex));
//                    Timber.d("** all actions %s", String.valueOf(botSize));
                    if (mCurrentBotIndex <= botSize) {
                        for (int i = mCurrentBotIndex; i < botSize; i++) {
                            ChatBotUserModel botUserModel = mStreamBotList.get(i);
                            if (mCurrentPassedTime >= botUserModel.atTime) {
                                if (result == null) {
                                    result = new ArrayList<>();
                                }
                                result.add(botUserModel);
                                Timber.d("** execute bot at time: %d", botUserModel.atTime);
                                Timber.d("** Thread: %d", Thread.currentThread().getId());
                                Timber.d("** mCurrentPassedTime: %d", mCurrentPassedTime);
                                mCurrentBotIndex++;
                            } else {
                                break;
                            }
                        }
                        mCurrentPassedTime += repeatTime;
                    }
                    return result;
                })
                .subscribe(chatBotUserModels -> {
                    if (chatBotUserModels == null && mCurrentBotIndex >= botSize) {
                        //if reach end of list, only send the last bot to viewers
                        notifyViewers(lastBot);
                        Timber.d("** last action **");
                    } else if (chatBotUserModels != null) {
                        for (ChatBotUserModel chatBotUserModel : chatBotUserModels) {
                            executeBotActions(chatBotUserModel);
                        }
                    }
                }, this::handleBotError, () -> Timber.e("interval compledted")));
    }

    private ChatBotUserModel createCompletedBot(ChatBotUserModel chatBotUserModel) {
        ChatBotUserModel botUserModel = new ChatBotUserModel();
        botUserModel.userName = ChatItemModelClass.INIT_BOT_LIST;
        botUserModel.displayName = chatBotUserModel.displayName;
        botUserModel.userImage = chatBotUserModel.userImage;
        botUserModel.userId = chatBotUserModel.userId;
        botUserModel.message = chatBotUserModel.message;
        return botUserModel;
    }

    private void executeBotActions(ChatBotUserModel botUserModel) {
        if (mStreamBlockeddMap.get(botUserModel.userId) == null) {
            if (mIsTriviaShow) {
                if (!mAgoraChatManager.getArrayCurrentBotInStream().contains(botUserModel.userName)) {
                    botJoinObservable.onNext(String.valueOf(botUserModel.userId));
                    botJoinXmppObservable.onNext(botUserModel);
                    if (botUserModel.leaveTime > 0) setUpLeaveTime(botUserModel);
                }
            } else {
                if (!mChatManager.getArrayCurrentBotInStream().contains(botUserModel.userName)) {
                    botJoinObservable.onNext(String.valueOf(botUserModel.userId));
                    botJoinXmppObservable.onNext(botUserModel);
                    if (botUserModel.leaveTime > 0) setUpLeaveTime(botUserModel);
                }
            }

            Timber.d("** number of bots  %s", mIsTriviaShow ? mAgoraChatManager.getArrayCurrentBotInStream() : mChatManager.getArrayCurrentBotInStream().size());
            Timber.d("** delayed time %s", String.valueOf(botUserModel.delayTime));

            switch (botUserModel.getActionType()) {
                case ChatBotUserModel.TYPE_MESSAGE:
                    addChatBotMessage(botUserModel);
                    break;
                case ChatBotUserModel.TYPE_LIKE:
                    addChatBotLike(botUserModel);
                    break;
                case ChatBotUserModel.TYPE_GIFT:
                    sendBotGift(botUserModel);
                    break;
                case ChatBotUserModel.TYPE_FOLLOW:
                    sendBotFollow(botUserModel);
                    break;
                case ChatBotUserModel.TYPE_NONE:
                    break;
                default:
                    break;
            }
        }
    }

    private void sendBotFollow(ChatBotUserModel botUserModel) {
        mCompositeSubscription.add(mService.botFollow(mAuthen, new BotFollowRequestModel(botUserModel.userId))
                .filter(response -> response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && mView != null)
                .subscribe(response -> sendGroupMessage(createBotFollowModel(botUserModel)), this::handleBotError));
    }

    private void addChatBotLike(ChatBotUserModel botUserModel) {
        int delayTimeInMilis = Math.max(botUserModel.delayTime, BUFFER_DELAYED_TIME_IN_SECOND);
        mBotCompositeSubscription.add(Observable.just(botUserModel).delay(delayTimeInMilis, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(botUserModel1 -> {
                    updateStreamViewCount(botUserModel);
                    botLikeObservable.onNext(String.valueOf(botUserModel.userId));
                    notifyViewers(botUserModel1);
                }, this::handleBotError));
        runBotLikeThread(delayTimeInMilis, createBotMessageModel(botUserModel, ChatItemModelClass.CHAT_TYPE_LIKE));
    }

    private void updateStreamViewCount(ChatBotUserModel botUserModel) {
        if (botLikedMap.get(botUserModel.userId, "NOT_FOUND").equalsIgnoreCase("NOT_FOUND")) {
            botLikedMap.put(botUserModel.userId, botUserModel.userName);
            sendLikeMessage(createBotMessageModel(botUserModel, ChatItemModelClass.CHAT_TYPE_LIKE));
        }
    }

    private void sendLikeMessage(ChatItemModelClass message) {
        message.setLiked(true);
        message.setMsg("I sent ");
        sendGroupMessage(message);
    }

    private static final int LIKE_SPEED_IN_MILI = 500;
    private static final int MINIMUM_BOT_LIKE = 10;

    private void runBotLikeThread(int delayTimeInMilis, ChatItemModelClass botLikeModelClass) {
        int numLikes = new Random().nextInt(10) + MINIMUM_BOT_LIKE;
        mBotCompositeSubscription.add(Observable.interval(delayTimeInMilis, LIKE_SPEED_IN_MILI, TimeUnit.MILLISECONDS)
                .take(numLikes)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> sendGroupMessage(botLikeModelClass), this::handleBotError, () -> Timber.e("like thread completed"))
        );
    }

    private void addChatBotMessage(ChatBotUserModel botUserModel) {
        LogUtils.logE("delayed", String.valueOf(botUserModel.delayTime));
        mBotCompositeSubscription.add(Observable.just(botUserModel).delay(Math.max(botUserModel.delayTime, BUFFER_DELAYED_TIME_IN_SECOND), TimeUnit.SECONDS)
                .filter(chatBotUserModel -> mStreamBlockeddMap.get(chatBotUserModel.userId, null) == null)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chatBotUserModel1 -> {
                    if (mStreamBlockeddMap.get(chatBotUserModel1.userId) == null) {
                        sendGroupMessage(createBotMessageModel(chatBotUserModel1, ChatItemModelClass.CHAT_TYPE_MESSAGE));
                    }

                }, this::handleBotError));
    }

    private void setUpLeaveTime(ChatBotUserModel botUserModel) {
        mBotCompositeSubscription.add(Observable.just(botUserModel).delay(botUserModel.leaveTime, TimeUnit.SECONDS)
                .filter(chatBotUserModel -> mStreamBlockeddMap.get(chatBotUserModel.userId, null) == null)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::removeBotInStream,
                        error -> Timber.e("leave bot error %s", error.getMessage()),
                        () -> Timber.e("leave bot completed")));
    }

    private synchronized void removeBotInStream(ChatBotUserModel botUserModel) {
        Timber.e("leave bot %s", botUserModel.userName);
        if (mChatManager == null) return;
        if (mIsTriviaShow) {
            if (mAgoraChatManager.getArrayCurrentBotInStream().contains(botUserModel.userName)) {
                mAgoraChatManager.removeBotUserViewer(botUserModel.userName);
                requestViewCountUpdate();
                notifyViewers(botUserModel);
            }
        } else {
            if (mChatManager.getArrayCurrentBotInStream().contains(botUserModel.userName)) {
                mChatManager.removeBotUserViewer(botUserModel.userName);
                requestViewCountUpdate();
                notifyViewers(botUserModel);
            }
        }

    }

    void notifyViewers(ChatBotUserModel notifyMessage) {
        ChatItemModelClass botListModels = createBotMessageModel(notifyMessage, ChatItemModelClass.CHAT_TYPE_BOT_JOIN_LIST);
        sendGroupMessage(botListModels);
    }


    private void sendBotGift(ChatBotUserModel chatBotUserModel) {
        BotSendGiftRequestModel request = new BotSendGiftRequestModel();
        request.setBot_id(chatBotUserModel.userId);
        request.setGift_id(chatBotUserModel.gift.giftId);
        if (mStreamId > 0) {
            request.setStream_id(mStreamId);
        }
        if (mStreamBlockeddMap.get(chatBotUserModel.userId, null) == null) {
            mBotCompositeSubscription.add(mService.botSendGift(mAuthen, request)
                    .filter(sendGiftDataResponse -> sendGiftDataResponse != null)
                    .subscribe(sendGiftDataResponse -> {
                        if (sendGiftDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            SendGiftResponseModel giftResponseModel = sendGiftDataResponse.getData();
                            if (giftResponseModel == null) return;
                            totalGoldFans = giftResponseModel.getReceiver().getTotalGoldFans();
                            if (mView != null) mView.updateStars(totalGoldFans);
                            sendGroupMessage(createBotGiftModel(chatBotUserModel, String.valueOf(totalGoldFans), giftResponseModel.getVotingScores(), giftResponseModel.topFanList, giftResponseModel.dailyTopFans));
                        } else {
                            if (mView != null)
                                mView.loadError(sendGiftDataResponse.getMessage(), sendGiftDataResponse.getCode());
                        }
                    }, this::handleBotError));
        }
    }


    private ChatItemModelClass createBotFollowModel(ChatBotUserModel chatBotUserModel) {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(chatBotUserModel.displayName);
        itemModelClass.setUserName(chatBotUserModel.userName);
        itemModelClass.setUserIdSend(String.valueOf(chatBotUserModel.userId));
        itemModelClass.setProfilePic(chatBotUserModel.userImage);
        itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_FOLLOW);
        String messageGift = String.format(mView.getViewContext().getString(R.string.message_followed), mUserData.getDisplayName());
        itemModelClass.setTotalLikes(countLike);
        itemModelClass.setTotalViewers(countUserHaveBeenView);
        itemModelClass.setMsg(messageGift);
        if (mView != null) itemModelClass.rank = mView.getRankByUserName(chatBotUserModel.userName);
        return itemModelClass;
    }


    private ChatItemModelClass createBotGiftModel(ChatBotUserModel chatBotUserModel, String receiverTotalGold, int votingScores, List<String> topFanList, List<DailyTopFanModel> dailyTopFans) {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(chatBotUserModel.displayName);
        itemModelClass.setUserName(chatBotUserModel.userName);
        itemModelClass.setUserIdSend(String.valueOf(chatBotUserModel.userId));
        itemModelClass.setProfilePic(chatBotUserModel.userImage);
        itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_GIFT);
        itemModelClass.setGiftImage(chatBotUserModel.gift.image);
        itemModelClass.setGiftId(String.valueOf(chatBotUserModel.gift.giftId));
        itemModelClass.setGiftName(chatBotUserModel.gift.giftName);
        itemModelClass.setReceiverStars(String.valueOf(receiverTotalGold));
        itemModelClass.setVotingScores(votingScores);
        itemModelClass.topFanList = topFanList;
        itemModelClass.rank = topFanList.indexOf(chatBotUserModel.userName);
        itemModelClass.dailyTopFansList = dailyTopFans;
        String messageGift = String.format(mView.getViewContext().getString(R.string.just_send_gift), chatBotUserModel.gift.giftName);
        itemModelClass.setMsg(messageGift);
        return itemModelClass;
    }

    private ChatItemModelClass createBotMessageModel(ChatBotUserModel botUser, @ChatItemModelClass.ChatStringType String type) {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(botUser.displayName);
        itemModelClass.setUserName(botUser.userName);
        itemModelClass.setUserIdSend(String.valueOf(botUser.userId));
        itemModelClass.setProfilePic(botUser.userImage);
        itemModelClass.setMessageType(type);
        itemModelClass.setMsg(botUser.message);
        itemModelClass.setChatBotUserModels(mIsTriviaShow ? mAgoraChatManager.getArrayCurrentBotInStream() : mChatManager.getArrayCurrentBotInStream());
        itemModelClass.setTotalLikes(countLike);
        itemModelClass.setTotalViewers(countUserHaveBeenView);
        if (mView != null) itemModelClass.rank = mView.getRankByUserName(botUser.userName);
        return itemModelClass;
    }


    //endregion


    //region get categories tag
    @Override
    public void getTagCategories() {
        mView.showProgress();
        mCompositeSubscription.add(mService.getTagListLiveStream(mAuthen)
                .filter(tagDataResponse -> tagDataResponse != null)
                .doOnNext(tagDataResponse -> {
                    if (tagDataResponse.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK && mView != null) {
                        mView.loadError(tagDataResponse.getMessage(), tagDataResponse.getCode());
                    }
                })
                .map(BaseResponse::getData)
                .filter(tagListLiveStreamModels -> mView != null)
                .subscribe(tagListLiveStreamModels -> mView.onTagCategoriesReceived(tagListLiveStreamModels),
                        this::handleError,
                        () -> mView.hideProgress())
        );
    }

    //endregion


    //region user action
    private ChatItemModelClass createMessageModel(String content, @ChatItemModelClass.ChatStringType String type) {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(mUserData.getDisplayName());
        itemModelClass.setUserName(mUserData.getUserName());
        itemModelClass.setUserIdSend(mUserData.getUserId());
        itemModelClass.setProfilePic(mUserData.getUserImage());
        itemModelClass.setMessageType(type);
        itemModelClass.setMsg(content);
        itemModelClass.setTotalLikes(countLike);
        itemModelClass.setTotalViewers(countUserHaveBeenView);
        itemModelClass.setTotalReceivedStars(totalGoldFans);
        if (type.contentEquals(ChatItemModelClass.CHAT_TYPE_END)) {
            itemModelClass.setDurationTime(Long.parseLong(content));
            itemModelClass.setTotalReceivedStars(totalGoldOfStream);
        }

        return itemModelClass;
    }

    private ChatItemModelClass createMuteMessageModel(String content, @ChatItemModelClass.ChatStringType String type, String muteUserId) {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(mUserData.getDisplayName());
        itemModelClass.setUserName(mUserData.getUserName());
        itemModelClass.setUserIdSend(mUserData.getUserId());
        itemModelClass.setProfilePic(mUserData.getUserImage());
        itemModelClass.setMessageType(type);
        itemModelClass.setMsg(content);
        itemModelClass.setTotalLikes(countLike);
        itemModelClass.setMutedUserId(muteUserId);
        itemModelClass.setTotalViewers(countUserHaveBeenView);
        itemModelClass.setTotalReceivedStars(totalGoldFans);
        if (type.contentEquals(ChatItemModelClass.CHAT_TYPE_END)) {
            itemModelClass.setDurationTime(Long.parseLong(content));
        }

        return itemModelClass;
    }

    private ChatItemModelClass createBlockMessageModel(String content, @ChatItemModelClass.ChatStringType String type, String blockUserId) {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(mUserData.getDisplayName());
        itemModelClass.setUserName(mUserData.getUserName());
        itemModelClass.setUserIdSend(mUserData.getUserId());
        itemModelClass.setProfilePic(mUserData.getUserImage());
        itemModelClass.setMessageType(type);
        itemModelClass.setMsg(content);
        itemModelClass.setBlockUserId(blockUserId);
        itemModelClass.setTotalLikes(countLike);
        itemModelClass.setTotalViewers(countUserHaveBeenView);
        itemModelClass.setTotalReceivedStars(totalGoldFans);
        if (type.contentEquals(ChatItemModelClass.CHAT_TYPE_END)) {
            itemModelClass.setDurationTime(Long.parseLong(content));
        }

        return itemModelClass;
    }


    private ChatItemModelClass createMessageModelWithType(String type) {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(mUserData.getDisplayName());
        itemModelClass.setUserName(mUserData.getUserName());
        itemModelClass.setUserIdSend(mUserData.getUserId());
        itemModelClass.setProfilePic(mUserData.getUserImage());
        itemModelClass.setMessageType(type);
        return itemModelClass;
    }
    //end region


    //region upload chat message

    private Subscription saveStreamSubscription;

    @Override
    public void saveStreamChatMessage(List<RecordedMessagesModel> recordedMessages) {
        saveStreamSubscription = Observable.just(recordedMessages)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(this::writeMessageToFile, error -> Timber.e(error.getMessage()));
    }

    private void writeMessageToFile(List<RecordedMessagesModel> recordedMessages) {
        try {
            FileUtility.writeFile(getApplicationContext(), mSlug, new Gson().toJson(recordedMessages), true);
            uploadToServer(mSlug);
        } catch (IOException e) {
            onError(e);
        }
    }

    private void uploadToServer(String slug) {
        mCompositeSubscription.add(mService.saveStreamChatHistory(mAuthen, new StreamChatHistoryRequestModel(slug, mAppsterApplication.getFileStreamPath(slug)).build())
                .subscribe(streamChatHistoryDataResponse -> {
                    Timber.e("chatFileUpload Sucessfully!!!!!!!!!!!!!!!!!");
                    RxUtils.unsubscribeIfNotNull(saveStreamSubscription);
                    mAppsterApplication.deleteFile(slug);
                }, error -> Timber.e(error.getMessage())));
    }

    //endregion
    private void handleError(Throwable error) {
        if (mView != null) {
            error.printStackTrace();
            Timber.e(error.getMessage());
            mView.hideProgress();
            mView.loadError(error.getMessage(), Constants.RETROFIT_ERROR);
        }
    }

    private void handleBotError(Throwable error) {
        error.printStackTrace();
        if (error.getMessage() != null) Timber.e(error.getMessage());
    }

    /**
     * Only host use this method
     *
     * @param message - message to send
     */
    @Override
    public void sendMessage(String message) {
        ChatItemModelClass messageItem = createMessageModel(message, ChatItemModelClass.CHAT_TYPE_MESSAGE);
        if (mUserData != null) {
            if (!StringUtil.isNullOrEmptyString(mUserData.getColor())) {
                messageItem.setProfileColor(mUserData.getColor());
            }
            sendMessage(messageItem);
            if (mView != null) mView.onMessageSuccess(messageItem);
        }
    }

    @Override
    public void sendMessage(ChatItemModelClass messageItem) {
        sendGroupMessage(messageItem);
    }

    private void sendGroupMessage(ChatItemModelClass chatItemModelClass) {
        try {
            if (!isEndStream && mChatManager != null) {
                if (mIsTriviaShow) {
                    if (mAgoraChatManager != null)
                        mAgoraChatManager.sendGroupMessage(mSlug, chatItemModelClass);
                } else {
                    mChatManager.sendGroupMessage(chatItemModelClass);
                }
            }
        } catch (Exception e) {
            Timber.e((e.getMessage()));
        }
    }

    @Override
    public void attachView(StreamContract.StreamView view) {
        mView = view;
    }


    @Override
    public void onBackPressed() {
        cancelAllBotThreads();
        if (mChatManager != null) mChatManager.clearArrayCurrentBotInStream();
        if (mAgoraChatManager != null) mAgoraChatManager.clearArrayCurrentBotInStream();
    }

    private void cancelAllBotThreads() {
        Timber.d("cancel bot beeep!!!!!!!!!!!!!!");
        RxUtils.unsubscribeIfNotNull(mBotCompositeSubscription);
    }

    @Override
    public void networkInterupted() {
        mIsPausing.set(true);
    }

    @Override
    public void networkResume() {
        mIsPausing.set(false);
    }

    @Override
    public void getFaceUnityStickerList() {
        mCompositeSubscription.add(AppsterWebServices.get().getFaceUnityStickerList(AppsterUtility.getAuth(), Constants.ANDROID_DEVICE_TYPE)
                .subscribe(baseResponse -> {
                    if (baseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        mView.onGetFaceUnityStickerSuccessfully(baseResponse.getData());
                    } else {
                        mView.onGetFaceUnityStickerFailed(baseResponse.getMessage());
                    }
                }, e -> {
                    Timber.e(e);
                    mView.hideProgress();
                }, () -> mView.hideProgress()));
    }

    @Override
    public void detachView() {
        if (!isEndStream) {
            Timber.e("endstream is false but app close -> send stream afk message");
            //store all chat messages in prefs
            mIntervalSubject.onNext(-1L);
            if (CheckNetwork.isNetworkAvailable(mView.getViewContext()) && !StringUtil.isNullOrEmptyString(mChatManager.getGroupName())) {
                sendNotifyStreamPause();
            }
        }
        mChatManager.setStreamChatGroupListener(null);
        mChatManager.clearArrayCurrentUserInStream();
        mChatManager.clearArrayCurrentBotInStream();
        mChatManager.setNumberRecreateGroup(0);
        mChatManager.leaveRoom();
        if (mAgoraChatManager != null) {
            mAgoraChatManager.clearArrayCurrentUserInStream();
            mAgoraChatManager.clearArrayCurrentBotInStream();
            mAgoraChatManager.leaveGroup(mSlug);
        }
        mView = null;
        mChatManager = null;
        mUserData = null;
        // clear hashmap
        if (botLikedMap != null) {
            botLikedMap.clear();
            botLikedMap = null;
        }
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        RxUtils.unsubscribeIfNotNull(mBotCompositeSubscription);
        RxUtils.unsubscribeIfNotNull(mStatisticSubscription);
    }

    private void storeChatMessagesInPrefs(ArrayList<RecordedMessagesModel> recordedMessages) {
        AppsterUtility.storePrefListObject(mView.getViewContext(), STORE_CHAT_PREFS, mSlug, recordedMessages);
    }

    @Override
    public void uploadFirstStreamImage(String slug, File bitmap) {
        StreamDefaultImageRequest request = new StreamDefaultImageRequest(slug, bitmap);
        mCompositeSubscription.add(mService.saveFirstStreamImage(mAuthen, request.build())
                .subscribe(streamPostImageResponse -> Timber.v("uploadFirstStreamImage!!!!!!!!!!!!!!!!! " + streamPostImageResponse.getData()), error -> Timber.e(error.getMessage())));


    }

    @Override
    public void updateTotalStars(long totalStars) {
        this.totalGoldFans = totalStars;
        if (mView != null) mView.updateStars(totalGoldFans);

    }

    private void notifyPointsUpdated(int points) {
        if (mView != null) mView.updatePoints(points);
    }

    @Override
    public void saveStream(String slug) {
        mView.showProgress();
        mCompositeSubscription.add(mService.saveStream(mAuthen, slug)
                .subscribe(baseResponse -> {
                    if (baseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        mView.onSaveStreamSuccess();
                    } else {
                        if (baseResponse.getCode() == 603) {
                            if (mView != null)
                                mView.loadError(baseResponse.getMessage(), baseResponse.getCode());
                        } else {
                            mView.onSaveStreamFailed(baseResponse.getMessage());
                        }
                        Timber.d(baseResponse.getMessage());
                    }
                    mView.hideProgress();
                }, this::handleError));
    }

    @Override
    public void onLuckyWheelStarted(int luckyResult) {
        sendGroupMessage(luckyWheelResultMessage(luckyResult));
    }

    @Override
    public void onLuckyWheelShowed() {
        mLiveStreamWatcherModels.addAll(mChatManager.getArrayCurrentUserInStream());
        sendGroupMessage(luckyWheelShowMessage());
    }


    @Override
    public void getVoteAwards(int votingLevel) {
        mCompositeSubscription.add(mService.getVoteAwards(mAuthen, votingLevel)
                .subscribe(voteAwardsResponse -> {
                    if (voteAwardsResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        mView.onVoteAwardReceived(voteAwardsResponse.getData());
                    } else {
                        mView.loadError(voteAwardsResponse.getMessage(), voteAwardsResponse.getCode());
                        Timber.d(voteAwardsResponse.getMessage());
                    }
                }, this::handleError));
    }

    private int currentSpinId = -1;

    @Override
    public void getLuckyWheelResult(int votingLevel) {
        SpinRequest spinRequest = new SpinRequest(votingLevel, mSlug);
        mCompositeSubscription.add(mService.getLuckyWheelResult(mAuthen, spinRequest)
                .subscribe(luckyWheelSpinResponseModelBaseResponse -> {
                    if (luckyWheelSpinResponseModelBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        currentSpinId = luckyWheelSpinResponseModelBaseResponse.getData().spinId;
                        mView.onLuckyWheelResultReceived(luckyWheelSpinResponseModelBaseResponse.getData().awardId);
                    } else {
                        mView.loadError(luckyWheelSpinResponseModelBaseResponse.getMessage(), luckyWheelSpinResponseModelBaseResponse.getCode());
                        Timber.d(luckyWheelSpinResponseModelBaseResponse.getMessage());
                    }
                }, this::handleError));
    }

    @Override
    public void sendLuckyReceivedUser() {
        if (mChatManager != null && !mChatManager.getArrayCurrentUserInStream().isEmpty()) {
            if (currentSpinId == -1) return;

            mLiveStreamWatcherModels.retainAll(mChatManager.getArrayCurrentUserInStream());
            mCompositeSubscription.add(Observable.from(mLiveStreamWatcherModels)
                    .toList()
                    .flatMap(strings -> mService.luckyWheelResult(mAuthen, currentSpinId, new LuckyWheelSpinResultRequest(strings)))
                    .subscribe(baseResponseObservable -> {
                        if (baseResponseObservable.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            currentSpinId = -1;
                        } else {
                            mView.loadError(baseResponseObservable.getMessage(), baseResponseObservable.getCode());
                            Timber.d(baseResponseObservable.getMessage());
                        }
                    }, this::handleError));
        }
    }

    private void getVotingLevels() {
        mLiveStreamWatcherModels = new ArrayList<>();
        mCompositeSubscription.add(mService.getVotingLevels(mAuthen)
                .subscribe(votingResponse -> {
                    if (votingResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        int levelUnlockedIndex = 0;
                        for (VotingLevels level : votingResponse.getData()) {
                            if (mCurrentVotingScores > level.toCredit) {
                                levelUnlockedIndex = level.orderIndex;
                            }
                        }
                        mView.onVoteLevelsReceived(votingResponse.getData(), levelUnlockedIndex, mCurrentVotingScores);
                    } else {
                        mView.loadError(votingResponse.getMessage(), votingResponse.getCode());
                        Timber.d(votingResponse.getMessage());
                    }
                }, this::handleError));
    }


    private ChatItemModelClass luckyWheelShowMessage() {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(mUserData.getDisplayName());
        itemModelClass.setUserName(mUserData.getUserName());
        itemModelClass.setUserIdSend(mUserData.getUserId());
        itemModelClass.setProfilePic(mUserData.getUserImage());
        itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_LUCKY_WHEEL_SHOW);
        itemModelClass.setTotalLikes(countLike);
        itemModelClass.setTotalViewers(countUserHaveBeenView);
        itemModelClass.setTotalReceivedStars(totalGoldFans);
        return itemModelClass;

    }

    private ChatItemModelClass luckyWheelResultMessage(int luckyResult) {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(mUserData.getDisplayName());
        itemModelClass.setUserName(mUserData.getUserName());
        itemModelClass.setUserIdSend(mUserData.getUserId());
        itemModelClass.setProfilePic(mUserData.getUserImage());
        itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_LUCKY_WHEEL_START);
        itemModelClass.setTotalLikes(countLike);
        itemModelClass.setTotalViewers(countUserHaveBeenView);
        itemModelClass.setTotalReceivedStars(totalGoldFans);
        itemModelClass.setLuckyResult(luckyResult);
        return itemModelClass;
    }

    @Override
    public void sendNotifyStreamPause() {
        if (mView != null) storeChatMessagesInPrefs(mView.getRecordedMessages());
        informAFKToServer(mSlug);
        sendGroupMessage(createMessageModelWithType(ChatItemModelClass.CHAT_TYPE_STREAM_PAUSE));
    }

    private void informAFKToServer(String slug) {
        mCompositeSubscription.add(mService.streamAFK(mAuthen, slug)
                .subscribe(booleanBaseResponse -> {
                    mIsPausing.set(true);
                    Timber.e(String.valueOf(booleanBaseResponse.getData()));
                }, Timber::e));
    }

    @Override
    public void sendNotifyStreamResume() {
        informResumeAFKToServer(mSlug);
        if (mIsChatGroupCreated) {
            sendGroupMessage(createMessageModelWithType(ChatItemModelClass.CHAT_TYPE_STREAM_RESTART));
            if (mRestartStreamModel != null && shouldEndPreviousVideoCall) {
                Timber.e("mRestartStreamModel!=null && shouldEndPreviousVideoCall");
                notifySubStreamEnded();
            }
        }
    }

    private void informResumeAFKToServer(String slug) {
        mCompositeSubscription.add(mService.resumeFromAFK(mAuthen, slug)
                .subscribe(booleanBaseResponse -> {
                    mIsPausing.set(false);
                    Timber.e(String.valueOf(booleanBaseResponse.getData()));
                }, Timber::e));
    }

    @Override
    public void streamRemovedByAdmin(ChatItemModelClass adminMessage) {
        mIntervalSubject.onNext(-1L);
        cancelAllBotThreads();
        EndStreamDataModel data = new EndStreamDataModel();
        data.setDuration(adminMessage.getDurationTime());
        data.setTotalGold(adminMessage.getTotalReceivedStars());
        data.setLikeCount(adminMessage.getTotalLikes());
        data.setViewCount(adminMessage.getTotalViewers());
        countUserHaveBeenView = data.getViewCount();
        countLike = data.getLikeCount();
        totalGoldOfStream = data.getTotalGold();
        Timber.e("duration %d, like %d, view %d, stars %d", data.getDuration(), countLike, countUserHaveBeenView, totalGoldOfStream);
        isEndStream = true;
        if (mChatManager != null) mChatManager.leaveRoom();
//        if (mView != null) {
//            mView.onEndStreamDataReceived(data);
//        }
        if (mView != null) {
//            mView.showEndStreamLayout(TRIGGER_END_BY_ADMIN_BANNED_XMPP);
            mView.dismissErrorDialog();
            if (!adminMessage.getMsg().isEmpty()) {
                mView.onStreamRemoved(adminMessage.getMsg(), data);
            }
        }
        //stream not able to resume if stopped by admin
        storeStreamSlugPrefs("");
        RxUtils.unsubscribeIfNotNull(mStatisticSubscription);

    }

    @Override
    public void storeStreamTitlePosition(String title, float percentX, float percentY, String color) {
        StreamTitleSticker streamTitle = new StreamTitleSticker();
        streamTitle.mStreamTitleStickerX = percentX;
        streamTitle.mStreamTitleStickerY = percentY;
        streamTitle.mStreamTitleStickerContent = title;
        streamTitle.mStreamTitleColorCode = color;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(streamTitle);
        mCompositeSubscription.add(mService.storeStreamTitleSticker(mAuthen, mSlug, json)
                .subscribe(baseResponse -> {
                    if (baseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        Timber.d("save stream title into server successfully");
                    }
                }, Timber::e));
    }

    @Override
    public void sendStreamTitlePositionXmpp(String title, float percentX, float percentY, String color) {
        ChatItemModelClass chatItem = new ChatItemModelClass();
        chatItem.setMessageType(ChatItemModelClass.TYPE_STEAM_TITLE_STICKER);
        chatItem.setStreamTitleStickerX(percentX);
        chatItem.setStreamTitleStickerY(percentY);
        chatItem.setStreamTitleStickerContent(title);
        chatItem.setUserName(mUserData.getUserName());
        chatItem.mStreamTitleColorCode = color;
        sendMessage(chatItem);
    }

    @Override
    public void getAppConfig() {
        mCompositeSubscription.add(AppsterWebServices.get().getAppConfigs(AppsterUtility.getAuth())
                .filter(appConfigModelBaseResponse -> appConfigModelBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .doOnNext(config -> Constants.VIDEO_CALL_ENABLE = config.getData().enableVideoCall)
                .map(appConfigModelBaseResponse -> appConfigModelBaseResponse.getData().enableStreamTitle)
                .filter(isEnable -> isEnable)
                .subscribe(isEnable -> mView.onStreamTitleEnabled(), Timber::e));
    }

    @Override
    public void notifySubStreamRejected() {
        informSubStreamStatusToServer(-1);
        mCompositeSubscription.add(Observable.just(true)
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(aBoolean -> mView != null)
                .subscribe(aBoolean -> mView.onSubStreamDisconnected()));
    }

    @Override
    public void notifySubStreamNoAnswer() {
        informSubStreamStatusToServer(2);
        sendGroupMessage(updateAndGetSubStreamStatistic(State.NO_ANSWER));
        mCompositeSubscription.add(Observable.just(true)
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(aBoolean -> mView != null)
                .subscribe(aBoolean -> mView.onSubStreamDisconnected()));
    }


    @Override
    public void notifyCallConnected() {
        sendGroupMessage(updateAndGetSubStreamStatistic(State.CONNECTED));
    }

    @Override
    public void notifyCallReConnected() {
        sendGroupMessage(updateAndGetSubStreamStatistic(State.RECONNECTED));
    }

    @Override
    public void getDailyTopFansList() {
        GetTopFanModel getTopFanModel = new GetTopFanModel();
        getTopFanModel.setUserId(mUserData.getUserId());
        getTopFanModel.setLimit(0);
        mCompositeSubscription.add(mService.getAllTopFanStream(mAuthen, getTopFanModel.getMappedRequest())
                .subscribe(topFansListBaseResponse -> {
                    if (topFansListBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                        mView.onGetDailyTopFansListSuccessfully(topFansListBaseResponse.getData().dailyTopFans);
                }, Timber::e));
    }

    @Override
    public void triviaGameStarted() {
        if (mTriviaGameStateMap == null) return;
        setupGameState(TriviaGameState.GAME_START);
    }

    private void setupGameState(int gameState) {
        mCompositeSubscription.add(Observable.just(gameState).delay(mTriviaGameStateMap.get(gameState, 0), TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(integer -> mView != null)
                .subscribe(this::handleGameState, Timber::e));
    }

    private void handleGameState(@TriviaGameState int state) {
        if (mView == null || mCurrentTriviaModel == null) return;
        switch (state) {
            case TriviaGameState.GAME_END:
                mView.dismissWinnerList();
                break;
            case TriviaGameState.GAME_FINISH:
                //no more question
                mCompositeSubscription.add(mTriviaFinishUseCase.execute(TriviaFinishUseCase.Params.finish(mCurrentTriviaModel.triviaId))
                        .subscribe(triviaFinishModel -> {
                            mView.displayTriviaWinnerList(triviaFinishModel);
                            mView.countDownToOffScreen(mView.getViewContext().getString(R.string.count_down_winners_list_ended_title), mTriviaGameStateMap.get(TriviaGameState.GAME_END));
                            setupGameState(TriviaGameState.GAME_END);
                        }, this::handleError));
                break;
            case TriviaGameState.GAME_START:
            case TriviaGameState.QUESTION_WAITING_TIMESUP:
                //display question and waiting for answer
                mCurrentTriviaQuestion = mCurrentTriviaModel.getNextQuestion();
                if (mCurrentTriviaQuestion != null) {
                    int countDownTime = mTriviaGameStateMap.get(TriviaGameState.QUESTION_ANSWER_TIMESUP) - 1;
                    mView.displayTriviaQuestion(mCurrentTriviaQuestion, countDownTime);
                    mView.countDownToFaceTime("FaceTime in", countDownTime);
                }
                //setup next state
                setupGameState(mCurrentTriviaQuestion != null ? TriviaGameState.QUESTION_ANSWER_TIMESUP : TriviaGameState.GAME_FINISH);

                break;
            case TriviaGameState.QUESTION_ANSWER_TIMESUP:
                //waiting result
                mView.dismissTriviaQuestion();
                //setup next state
                mView.countDownToOffScreen(String.format(Locale.US, "Q%d Answer in", mQuestionIndex.get()), mTriviaGameStateMap.get(TriviaGameState.RESULT_WAITING_TIMESUP));
                setupGameState(TriviaGameState.RESULT_WAITING_TIMESUP);
                break;
            case TriviaGameState.RESULT_WAITING_TIMESUP:
                //get result
                mCompositeSubscription.add(mTriviaResultUseCase.execute(TriviaResultUseCase.Params.result(mCurrentTriviaModel.triviaId, mCurrentTriviaQuestion.questionId))
                        .subscribe(triviaResultModel -> mView.displayTriviaResult(triviaResultModel, String.format(Locale.US, "Question %d", mQuestionIndex.get())), this::handleError));
                //start next state immediately, don't need to wait result response
                mView.countDownToFaceTime("FaceTime in", mTriviaGameStateMap.get(TriviaGameState.RESULT_TIMESUP));
                //setup next state
                setupGameState(TriviaGameState.RESULT_TIMESUP);
                break;
            case TriviaGameState.RESULT_TIMESUP:
                //next question
                mView.dismissTriviaResult();
                //setup next state
                boolean isOutOfQuestionIndex = mQuestionIndex.incrementAndGet() > mCurrentTriviaModel.questions.size();
                int nextState = isOutOfQuestionIndex ? TriviaGameState.FINISH_WAITING_TIME : TriviaGameState.QUESTION_WAITING_TIMESUP;
                mView.countDownToOffScreen(isOutOfQuestionIndex ? "Winners in" : String.format(Locale.US, "Question %d in", mQuestionIndex.get()),
                        mTriviaGameStateMap.get(nextState));
                setupGameState(nextState);
                break;

            case TriviaGameState.FINISH_WAITING_TIME:
                setupGameState(TriviaGameState.GAME_FINISH);
                break;
        }
    }

    @Override
    public void triviaAnswer(int optionId) {
        mCompositeSubscription.add(mTriviaAnswerUseCase.execute(TriviaAnswerUseCase.Params.answer(mCurrentTriviaModel.triviaId, mCurrentTriviaQuestion.questionId, optionId))
                .subscribe(triviaAnswerModel -> {
                }, this::handleError));
    }

    @Override
    public void getTriviaWinnerList() {
        if (mIsEndWinnerList) return;
        mCompositeSubscription.add(mTriviaWinnerListUseCase.execute(TriviaWinnerListUseCase.Params.byType(mIndexWinnerList, Constants.PAGE_LIMITED, mCurrentTriviaModel.triviaId))
                .subscribe(basePagingModel -> {
                    mIsEndWinnerList = basePagingModel.isEnd;
                    mIndexWinnerList = basePagingModel.nextId;
                    mView.showTriviaWinnerListData(basePagingModel.data);
                }, error -> {
                    handleError(error);
                    mView.getTriviaWinnerListError();
                }));
    }

    @Override
    public void getTriviaQuestionInfo(int triviaInfo) {
        mCompositeSubscription.add(mTriviaQuestionUseCase.execute(TriviaQuestionUseCase.Params.load(triviaInfo))
                .subscribe(triviaInfoModel -> mCurrentTriviaModel.questions = triviaInfoModel.questions,
                        this::handleError));
    }

    @Override
    public void getTriviaInfo(int triviaInfo) {
        mTriviaGameStateMap = new SparseIntArray();
        mCompositeSubscription.add(mTriviaInfoHostUseCase.execute(TriviaInfoHostUseCase.Params.load(triviaInfo))
                .filter(triviaInfoModel -> mView != null)
                .subscribe(triviaInfoModel -> {
                    mCurrentTriviaModel = triviaInfoModel;
                    mTriviaGameStateMap.put(TriviaGameState.QUESTION_ANSWER_TIMESUP, triviaInfoModel.answerTime + 1);
                    mTriviaGameStateMap.put(TriviaGameState.RESULT_WAITING_TIMESUP, triviaInfoModel.resultWaitingTime);
                    mTriviaGameStateMap.put(TriviaGameState.RESULT_TIMESUP, triviaInfoModel.resultTime);
                    mTriviaGameStateMap.put(TriviaGameState.QUESTION_WAITING_TIMESUP, triviaInfoModel.questionWaitingTime);
                    mTriviaGameStateMap.put(TriviaGameState.GAME_END, triviaInfoModel.finishTime);
                    mTriviaGameStateMap.put(TriviaGameState.FINISH_WAITING_TIME, triviaInfoModel.finishWaitingTime);
                    mView.onTriviaInfoReceived(triviaInfoModel);
                }, this::handleError));
    }

    @Override
    public void notifySubStreamEnded() {
        if (shouldEndPreviousVideoCall) shouldEndPreviousVideoCall = false;

        informSubStreamStatusToServer(2);
        sendGroupMessage(updateAndGetSubStreamStatistic(State.DISCONNECTING));
        mCompositeSubscription.add(Observable.just(true)
                .delay(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(aBoolean -> mView != null)
                .subscribe(aBoolean -> {
                    Timber.e("notifySubStreamEnded onSubStreamDisconnected after 5s");
                    sendGroupMessage(updateAndGetSubStreamStatistic(State.DISCONNECTED));
                    mView.onSubStreamDisconnected();
                }));
    }

    @Override
    public void startRTC() {
        if (mView != null && mSubStreamData != null) mView.onSubStreamAvailable(mSubStreamData);
    }

    private void informSubStreamStatusToServer(int status) {
        if (mSubSlug.isEmpty()) return;
        mCompositeSubscription.add(mService.updateSubStreamStatus(AppsterUtility.getAuth(), mSlug, mSubSlug, status)
                .subscribe(subStreamDataBaseResponse -> Timber.e(String.valueOf(subStreamDataBaseResponse.getData()))
                        , Timber::e));
    }

    @NonNull
    private ChatItemModelClass updateAndGetSubStreamStatistic(@State int state) {
        if (mSubStreamData != null) mSubStreamData.status = state;
        return updateAndGetSubStreamStatistic(mSubStreamData);
    }

    @NonNull
    private ChatItemModelClass updateAndGetSubStreamStatistic(SubStreamData subStreamData) {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(mUserData.getDisplayName());
        itemModelClass.setUserName(mUserData.getUserName());
        itemModelClass.setUserIdSend(mUserData.getUserId());
        itemModelClass.setProfilePic(mUserData.getUserImage());
        itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_STATISTIC);
        itemModelClass.subStreamData = subStreamData;
        itemModelClass.setTotalLikes(countLike);
        itemModelClass.setTotalViewers(countUserHaveBeenView);
        return itemModelClass;
    }

    @Override
    public void callRequest(String userId, String userName) {
        //check that user still in stream
        if (mChatManager.getArrayCurrentUserInStream().contains(userName)) {
            mCompositeSubscription.add(mService.createSubStream(mAuthen, mSlug, new SubStreamRequest(Integer.parseInt(userId)))
                    .filter(callDataResponseModel -> callDataResponseModel != null && mView != null)
                    .subscribe(callDataResponseModel -> {
                        if (callDataResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            this.mSubStreamData = callDataResponseModel.getData();
                            mSubSlug = mSubStreamData.slug;
                            mSubStreamData.status = State.CONNECTING;
                            mView.onSubStreamCreated(mSubStreamData);
                            sendCallRequest(mSubStreamData);
                        } else {
                            if (mView != null)
                                mView.loadError(callDataResponseModel.getMessage(), callDataResponseModel.getCode());
                        }
                    }, Timber::e));
        } else if (mView != null) {
            mView.onCallRequestFailedSinceUserHasLeftStream(userName);
        }
    }

    private void sendCallRequest(SubStreamData data) {
        ChatItemModelClass callRequestMessage = createMessageModel("call request", ChatItemModelClass.CHAT_TYPE_STATISTIC);
        callRequestMessage.subStreamData = data;
        Timber.e(data.toString());
        sendMessage(callRequestMessage);
    }

    @Override
    public void onChatGroupJoinedSuccesfully() {
        Timber.e("onChatGroupJoinedSuccesfully");
        mIsChatGroupCreated = true;
        sendNotifyStreamResume();
    }

    @Override
    public void onChatGroupJoinError(String errorMessage) {
        // do nothing
    }

    @Override
    public void onChatGroupWatchersListReceived(List<String> watchers) {
        // do nothing
    }

    @Override
    public void onLocationPermissionChanged(boolean granted) {
        isLocationPermisionEnable = granted;
        if (granted) {
            gpstClass = GPSTClass.getInstance();
            gpstClass.getLocation(mView.getViewContext());
            // check if GPS enabled
            if (gpstClass.canGetLocation()) {
                mView.onLocationDetected();
            }
        }
    }
}
