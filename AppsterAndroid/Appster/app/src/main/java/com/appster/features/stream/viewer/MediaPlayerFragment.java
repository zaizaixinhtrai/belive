package com.appster.features.stream.viewer;


import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.adapters.AdapterListWatcher;
import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.adapters.WinnerListAdapter;
import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.recyclerview.LoadMoreRecyclerView;
import com.appster.customview.CircleImageView;
import com.appster.customview.CustomFontButton;
import com.appster.customview.CustomFontEditText;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.ExpensiveGift;
import com.appster.customview.GiftComboGroupView;
import com.appster.customview.GiftRankingGroupView;
import com.appster.customview.GuestSubStreamStatusView;
import com.appster.customview.HostSubStreamStatusView;
import com.appster.customview.IncomingCallView;
import com.appster.customview.ShowCaseViewTutorial;
import com.appster.customview.StickyPadFrameLayout;
import com.appster.customview.SubPlayerLayout;
import com.appster.customview.VideoControllerView;
import com.appster.customview.luckywheel.LuckyItem;
import com.appster.customview.luckywheel.LuckyWheelView;
import com.appster.customview.trivia.GameState;
import com.appster.customview.trivia.TriviaGameState;
import com.appster.customview.trivia.TriviaReviveView;
import com.appster.customview.trivia.TriviaView;
import com.appster.data.AppPreferences;
import com.appster.dialog.DialogReport;
import com.appster.dialog.ExpensiveGiftDialog;
import com.appster.dialog.LiveInviteDialog;
import com.appster.dialog.SharePostDialog;
import com.appster.dialog.TopFanDialog;
import com.appster.domain.RecordedMessagesModel;
import com.appster.features.login.LoginActivity;
import com.appster.features.mvpbase.RecyclerItemCallBack;
import com.appster.features.stream.CameraHintView;
import com.appster.features.stream.State;
import com.appster.features.stream.dialog.LiveShopDialog;
import com.appster.features.stream.dialog.TriviaDialog;
import com.appster.features.stream.dialog.TriviaDialogType;
import com.appster.features.stream.dialog.TriviaHowToPlayDialog;
import com.appster.features.stream.dialog.TriviaRankingDialog;
import com.appster.features.stream.dialog.TriviaReviveUsageDialog;
import com.appster.features.stream.dialog.TriviaWinnerPopupDialog;
import com.appster.features.user_profile.DialogUserProfileFragment;
import com.appster.interfaces.OnBackPressFragment;
import com.appster.interfaces.OnLoadMoreListenerRecyclerView;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.main.MainActivity;
import com.appster.manager.AgoraChatManager;
import com.appster.manager.AppsterChatManger;
import com.appster.manager.ShowErrorManager;
import com.appster.manager.WallFeedManager;
import com.appster.message.ChatItemModelClass;
import com.appster.models.DailyTopFanModel;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.FollowUser;
import com.appster.models.NetworkUploadSpeedModel;
import com.appster.models.NewLikeEventModel;
import com.appster.models.ShareStreamModel;
import com.appster.models.StreamModel;
import com.appster.models.StreamPublisherModel;
import com.appster.models.StreamTitleSticker;
import com.appster.models.TopFanModel;
import com.appster.models.TopFansList;
import com.appster.models.UserModel;
import com.appster.models.ViewVideosEvent;
import com.appster.models.event_bus_models.NewMessageEvent;
import com.appster.models.event_bus_models.UserJoinLeaveEvent;
import com.appster.sendgift.DialogSendGift;
import com.appster.services.InternalMessageReceiver;
import com.appster.tracking.EventTracker;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.GlobalSharedPreferences;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.RecycleItemClickSupport;
import com.appster.utility.RxUtils;
import com.appster.utility.SocialManager;
import com.appster.utility.glide.BlurTransformation;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.BeginStreamRequestModel;
import com.appster.webservice.request_models.CreditsRequestModel;
import com.appster.webservice.request_models.GetTopFanModel;
import com.appster.webservice.request_models.LikeStreamRequestModel;
import com.appster.webservice.request_models.ReportUserRequestModel;
import com.appster.webservice.request_models.SetFollowUserRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.MaintenanceModel;
import com.appster.webservice.response.SubStreamData;
import com.apster.common.Constants;
import com.apster.common.CopyTextUtils;
import com.apster.common.CountryCode;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.DiffCallBaseUtils;
import com.apster.common.DownloadVideos;
import com.apster.common.FileDownloader;
import com.apster.common.Utils;
import com.apster.common.key_broad_detection.KeyboardHeightObserver;
import com.apster.common.key_broad_detection.KeyboardHeightProvider;
import com.apster.common.ksy_utils.KSYConfig;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.data.entity.requests.EarnPointsRequestEntity;
import com.data.exceptions.BeLiveServerException;
import com.data.repository.TriviaDataRepository;
import com.data.repository.datasource.cloud.CloudTriviaDataSource;
import com.domain.interactors.trivia.TriviaAnswerUseCase;
import com.domain.interactors.trivia.TriviaCheckReviveUseCase;
import com.domain.interactors.trivia.TriviaFinishUseCase;
import com.domain.interactors.trivia.TriviaInfoUseCase;
import com.domain.interactors.trivia.TriviaQuestionUseCase;
import com.domain.interactors.trivia.TriviaResultUseCase;
import com.domain.interactors.trivia.TriviaUseReviveUseCase;
import com.domain.interactors.trivia.TriviaWinnerListUseCase;
import com.domain.models.TriviaFinishModel;
import com.domain.models.TriviaInfoModel;
import com.domain.models.TriviaResultModel;
import com.domain.models.WinnerModel;
import com.domain.repository.TriviaRepository;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.player.KSYTextureView;
import com.ksyun.media.streamer.capture.camera.CameraTouchHelper;
import com.ksyun.media.streamer.framework.AVConst;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.ksyun.media.streamer.logstats.StatsLogReport;
import com.pack.utility.StringUtil;
import com.squareup.leakcanary.RefWatcher;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import agora.MediaManager;
import agora.kit.KSYAgoraStreamer;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTouch;
import io.agora.rtc.IRtcEngineEventHandler;
import me.yifeiyuan.library.PeriscopeLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import wowza.gocoder.sdk.app.ui.TimerView;

import static agora.kit.KSYAgoraStreamer.GUEST_SCREEN_GUEST_CAM_HEIGHT;
import static agora.kit.KSYAgoraStreamer.GUEST_SCREEN_GUEST_CAM_LEFT_BEGIN;
import static agora.kit.KSYAgoraStreamer.GUEST_SCREEN_GUEST_CAM_TOP_BEGIN;
import static agora.kit.KSYAgoraStreamer.GUEST_SCREEN_GUEST_CAM_WIDTH;
import static agora.kit.KSYAgoraStreamer.GUEST_SCREEN_HOST_CAM_HEIGHT;
import static agora.kit.KSYAgoraStreamer.GUEST_SCREEN_HOST_CAM_LEFT_BEGIN;
import static agora.kit.KSYAgoraStreamer.GUEST_SCREEN_HOST_CAM_TOP_BEGIN;
import static agora.kit.KSYAgoraStreamer.GUEST_SCREEN_HOST_CAM_WIDTH;
import static agora.kit.KSYAgoraStreamer.HOST_SCREEN_GUEST_CAM_HEIGHT;
import static agora.kit.KSYAgoraStreamer.HOST_SCREEN_GUEST_CAM_LEFT_BEGIN;
import static agora.kit.KSYAgoraStreamer.HOST_SCREEN_GUEST_CAM_TOP_BEGIN;
import static agora.kit.KSYAgoraStreamer.HOST_SCREEN_GUEST_CAM_WIDTH;
import static agora.kit.KSYAgoraStreamer.SCALING_MODE_CENTER_CROP;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.AUDIO_SERVICE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.appster.features.receivers.IncomingBroadcastReceiver.BELIVE_CALL_DETEECTOR;
import static com.appster.features.stream.StreamingActivityGLPlus.RETRY_DELAY_MILLIS;
import static com.appster.manager.AppsterChatManger.ADMIN_USERNAME;
import static com.appster.utility.SocialManager.SHARE_TYPE_STREAM;
import static com.apster.common.Constants.LIVE_STREAM;
import static com.apster.common.Constants.RECORDED_STREAM;
import static com.apster.common.Constants.TARGET_VIEWER;
import static com.apster.common.Constants.TRACKING_STREAM_FORMAT;
import static com.apster.common.Constants.TRIGGER_END_BY_GET_VIEWER_NETWORK_SPEED_FAILED;
import static com.apster.common.Constants.TRIGGER_END_BY_RECORDED_SCRUB_TO_END;
import static com.apster.common.Constants.TRIGGER_END_BY_RELOAD_FAILED;
import static com.apster.common.Utils.getNavBarHeight;
import static com.apster.common.Utils.getNavigationBarSize;
import static com.apster.common.Utils.hasNavBar;
import static com.ksyun.media.streamer.kit.StreamerConstants.KSY_STREAMER_ERROR_PUBLISH_FAILED;
import static com.pack.utility.StringUtil.subStringWithPresetMaxLength;


/**
 * MediaPlayerFragment demonstrates playing an HLS Stream, and fetching
 * stream metadata via the .m3u8 manifest to decorate the display for Live streams.
 */
public class MediaPlayerFragment extends Fragment implements OnBackPressFragment,
        ChatGroupDelegateAdapter.ChatGroupClickListener,
        DialogUserProfileFragment.UserProfileActionListener,
        KeyboardHeightObserver,
        Animator.AnimatorListener, AudioManager.OnAudioFocusChangeListener,
        TriviaView.OnTriviaOptionListener {

    public static final String PLAYER_TAG = "StreamPlayer";
    private static final String TAG = "MediaPlayerFragment";
    public static final String ARG_URL = "media_url";
    public static final String STREAM_SLUG = "slug";
    public static final String USER_URL = "user_url";
    public static final String IS_RECORD = "is_record";
    private static final String TAG_INTERCEPT_EVENT_BEFORE_TUTORIAL = "TAG_INTERCEPT_EVENT_BEFORE_TUTORIAL";
    public static final int NUMBER_FAILS_LOADING_VIDEOS = 3;
    public static final int NUMBER_BUFFER_LOADING_VIDEOS = 20;
    private static final int TWEET_SHARE_REQUEST_CODE = 509;
    private static final int INTERVAL_TIMES_SHOW_GO_SHOP_BUTTON = 3;
    private static final int FACEBOOK_SHARE_REQUEST_CODE = 609;
    static final int BUFFER_DELAYED_TIME = 1;//second;
    final static int PERMISSION_REQUEST_CAMERA_AUDIOREC = 1;
    public static final int MAX_DUPLICATE_MESSAGE_COUNT = 2;
    @Bind(R.id.triviaView)
    TriviaView mTriviaView;
    @Bind(R.id.btn_point)
    ImageButton btnPoint;
    private AppsterWebserviceAPI mService;
    @Bind(R.id.img_pause_gradient)
    ImageView imgPauseGradient;
    @Bind(R.id.tv_pause_message)
    CustomFontTextView tvPauseMessage;
    @Bind(R.id.tvNetworkSlow)
    CustomFontTextView tvNetworkSlow;

    int countPingNetworkByZero = 0;

    //region viewer screen
    @Bind(R.id.fragment_root_view)
    FrameLayout fragmentRootView;

    @Bind(R.id.tvNetworkError)
    TextView tvNetworkError;
    @Bind(R.id.ll_gift_group)
    GiftComboGroupView llGiftGroup;


    @Bind(R.id.commentsList)
    RecyclerView commentsListView;
    @Bind(R.id.periscope)
    PeriscopeLayout periscope;
    @Bind(R.id.vsEndStream)
    ViewStub vsEndStream;
    @Bind(R.id.vsSubScreen)
    ViewStub vsSubScreen;

    @Bind(R.id.vsHostSubScreen)
    ViewStub vsHostSubScreen;
    @Bind(R.id.vsSubPlayer)
    ViewStub vsSubPlayer;
    @Bind(R.id.user_hot_image)
    ImageView userHotImage;
    @Bind(R.id.view_time_line)
    FrameLayout viewTimeLine;

    @Bind(R.id.player_view)
    KSYTextureView mMediaPlayer;


    @Bind(R.id.fl_sticky_title)
    StickyPadFrameLayout mStickyPadFrameLayout;
    @Bind(R.id.btn_live_title)
    ImageButton mBtnLiveTitle;
    @Bind(R.id.btn_live_shop)
    ImageButton mBtnLiveShop;

    @Bind(R.id.progress)
    ProgressBar mProgress;
    @Bind(R.id.vsLuckywheel)
    ViewStub vsLuckywheel;

    @Bind(R.id.vsTriviaWinner)
    ViewStub vsTriviaWinner;
    //endregion


    //region top panel
    @Bind(R.id.ll_time_love_views)
    LinearLayout mLlTimeLoveViewsContainer;
    @Bind(R.id.ciOwnerUserImage)
    CircleImageView ciOwnerUserImage;
    @Bind(R.id.txtTimer)
    TimerView txtTimer;
    @Bind(R.id.tvLiveOwnerUserName)
    CustomFontTextView tvLiveOwnerUserName;
    @Bind(R.id.tvCurrentView)
    CustomFontTextView tvCurrentView;
    @Bind(R.id.tvTotalLiked)
    CustomFontTextView tvTotalLiked;
    @Bind(R.id.txt_stars)
    CustomFontTextView txtStars;
    @Bind(R.id.hlvCustomList)
    RecyclerView hlvCustomList;
    @Bind(R.id.llStreamingToppanel)
    LinearLayout llStreamingToppanel;
    @Bind(R.id.v_live_streamer_bg)
    View mVLiveStreamerBg;
    @Bind(R.id.llLiveVideoOwner)
    LinearLayout llLiveVideoOwner;
    //endregion
    //region bottom panel
    @Bind(R.id.etComment)
    CustomFontEditText etComment;
    @Bind(R.id.ibSendComment)
    ImageButton ibSendComment;
    @Bind(R.id.llActions)
    LinearLayout llActions;
    @Bind(R.id.llBottomContainer)
    LinearLayout llBottomContainer;
    @Bind(R.id.ibGiftStore)
    ImageButton mIbSendGift;
    @Bind(R.id.ibShareStream)
    ImageButton mIbSns;

    @Bind(R.id.iBtnHostFollow)
    ImageButton mIBtnHostFollow;

    @Bind(R.id.subCameraPreview)
    GLSurfaceView mSubCameraView;
    @Bind(R.id.subCameraContainer)
    FrameLayout subCameraContainer;

    @Bind(R.id.playerContainer)
    FrameLayout playerContainer;
    @Bind(R.id.camera_hint)
    CameraHintView mCameraHintView;

    @Bind(R.id.grv_ranking)
    GiftRankingGroupView mGiftRankingGroupView;

    @Bind(R.id.vIncomingCall)
    FrameLayout vIncomingCall;

    @Bind(R.id.triviaReviveView)
    TriviaReviveView triviaReviveView;
    @Bind(R.id.tvReviveCountGuide)
    CustomFontTextView tvReviveCountGuide;
    //endregion

    @Bind(R.id.clTriviaExtraActionsContainer)
    ConstraintLayout clTriviaExtraActionsContainer;
    @Bind(R.id.lo_point)
    ViewGroup loUserPoint;
    @Bind(R.id.tv_user_point)
    TextView tvUserPoint;

    StreamModel currentStreamDetails;
    private AppsterChatManger mChatManager;
    private AgoraChatManager mAgoraChatManager;
    UserModel mAppOwnerProfile;
    private Timer pingTimer;

    long countUserHaveBeenView = 0;
    int countGiftCount = 0;
    int countLikeCount = 0;
    //    int countFollower = 0;
    boolean oldFollowStatus;
    private String mStreamerId;
    long mTotalGoldFans = 0;
    boolean hasShownFollowButtonInChat;
    boolean mHasSentLiveCommerceAnnouncementMessage;
    private AtomicInteger mLiveCommerceAnnouncementCount = new AtomicInteger();


    CallbackManager mFBCallbackManager;
    // M3u8 Media properties inferred from .m3u8

    ArrayList<DisplayableItem> listItemChat;
    ChatGroupDelegateAdapter mChatGroupAdapter;
    LinearLayoutManager commentLayoutManager;
    int recyclerViewCommentState = RecyclerView.SCROLL_STATE_IDLE;
    boolean recyclerViewCommentScrollWaited = false;

    // variable to track event time
    private long mLastClickTime = 0;

    Handler mTickHandler;

    boolean mIsDisconnectedPlayer = false;

    String publisherImage;
    String streamSlug;
    String mMediaUrl;
    String mStreamTitle;
    String mStreamUserId;
    //    boolean mIsRtmpStream = false;
    boolean isRecorded = false;
    boolean mIsPublisherSeller;


    private HashMap<String, Integer> wordCountMap;

    VideoControllerView mediaController;
    private AdapterListWatcher listUserAdapter;

    private boolean isHideAllView = false;


    private int counterHomePress = 0;
    Bitmap avatarBlur;
    boolean isHomePress = false;
    private boolean isStoped = false;
    private boolean mIsEndedStream;
    private boolean mIsTutorialShowing;

    private TopFanDialog topFanDialog;
    private LiveShopDialog mLiveShopDialog;
    ExpensiveGiftDialog mExpensiveGiftDialog;
    boolean isExpensiveGiftDialogShown;
    List<ChatItemModelClass> mGiftQueue;

    LinearLayoutManager layoutManager;
    private ArrayList<RecordedMessagesModel> mRecordedUserMessage;
    private Toast mRecordedLiveToast = null;
    final Subject<Integer, Integer> mVideoTimeChanged = PublishSubject.create();

    PublishSubject<ChatItemModelClass> chatItemModelObservable = PublishSubject.create();
    PublishSubject<UserJoinLeaveEvent> watcherAddEventObservable = PublishSubject.create();
    PublishSubject<ChatItemModelClass> mBotJoinObservable = PublishSubject.create();
    PublishSubject<Object> showHeartObservable = PublishSubject.create();
    CompositeSubscription mCompositeSubscription;
    PublishSubject<Boolean> luckywheelVisibilityListener = PublishSubject.create();
    PublishSubject<Boolean> callSubViewObservable = PublishSubject.create();
    private final PublishSubject<Object> mGetDailyTopFansListObservable = PublishSubject.create();
    private Subscription mVideoTimeChangedSubscription;
    private ArrayList<RecordedMessagesModel> mViewerChatMessages;

    private int mCurrentVideoTime;
    private AtomicBoolean mIsStreamShared = new AtomicBoolean(false);
    AtomicBoolean mIsWaitingReconnect = new AtomicBoolean(false);
    private boolean mIsTapOnShareSNS = false;
    private boolean mIsMuted = false;
    private AtomicBoolean mIsPausing = new AtomicBoolean(false);
//    DialogUserProfileFragment userProfileFragment;

    private ArrayList<String> mNaughtyWords;
    NewLikeEventModel evenlike;
    boolean isClickGoHome = false;
    PublishSubject<Boolean> giftDialogListenObservable = PublishSubject.create();
    LuckyWheelLayout mLuckyWheelLayout;
    AtomicBoolean mHostAlreadyEndedCall = new AtomicBoolean(false);
    /**
     * The keyboard height provider
     */
    private KeyboardHeightProvider keyboardHeightProvider;
    InputMethodManager mInputMethodManager;
    //    final MediaPlayerHandler mMediaPlayerHhandler = new MediaPlayerHandler(this);
    private AtomicBoolean mIsCanChangeNetworkText;
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    protected boolean mIsFollowThisUser = false;
    protected int mViewSessionId;
    protected boolean mIsTrackedLeaveTime = false;
    private AtomicBoolean mIsWaitingTimeoutEnable = new AtomicBoolean(false);
    AtomicBoolean mIsSetupJoinedList = new AtomicBoolean(false);
    AtomicInteger mCurrentFanRanking = new AtomicInteger(-1);
    List<String> mTopFanList = new ArrayList<>();
    SubPlayerLayout mSubPlayerLayout;
    private String mSubStreamUrl = "rtmp://stgwowza.view.belive.sg:1935/Appsters_recording/71d904a1cca94d2e95fbd1821984e434";
    private String mSubSlug = "";
    protected LiveInviteDialog mLiveInviteDialog;
    private AtomicBoolean mIsCalling = new AtomicBoolean(false);
    private AtomicBoolean mHasCalledEndedAPI = new AtomicBoolean(false);
    private boolean mIsTriviaShow = false;
    boolean mIsAbleToSendGift = true;
    private boolean mIsNeedRevive = false;
    private TriviaHowToPlayDialog mTriviaHowToPlayDialog;
    private TriviaReviveUsageDialog mTriviaReviveUsageDialog;
    private TriviaRankingDialog mTriviaRankingDialog;
    private TriviaWinnerPopupDialog mWinnerPopupDialog;
    private boolean mIsEndWinnerList;
    private int mIndexWinnerList;
    private boolean mIsTriviaShowRunning = false;

    private Animation mTriviaInAnim;
    private Animation mTriviaOutAnim;
    private Animation mTriviaResultOutAnim;
    protected boolean mIsNetworkError = false;

    private ArrayList<TriviaDialog> listTriviaDialog;
    private PointInfoDialog pointInfoDialog;

    public static MediaPlayerFragment newInstance(String mediaUrl, String streamSlug, String userImage, boolean isRecorded) {
        MediaPlayerFragment fragment = new MediaPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, mediaUrl);
        args.putString(STREAM_SLUG, streamSlug);
        args.putString(USER_URL, userImage);
        args.putBoolean(IS_RECORD, isRecorded);
        fragment.setArguments(args);
        return fragment;
    }

    public MediaPlayerFragment() {
        // Required empty public constructor
    }


    // Media config

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            Timber.e("VideoPlayer============OnPrepared");
            mVideoWidth = mMediaPlayer.getVideoWidth();
            mVideoHeight = mMediaPlayer.getVideoHeight();

            // Set Video Scaling Mode
            mMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

            //start player
            mMediaPlayer.start();
//            startSubPlayer();
        }
    };


    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {

            Timber.e("mOnVideoSizeChangeListener============mOnVideoSizeChangeListener");
            Timber.e("onVideoSizeChanged %d - %d", width, height);
            Timber.e("onPrepared %d - %d", mVideoWidth, mVideoHeight);
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (width != mVideoWidth || height != mVideoHeight) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();

                    if (mMediaPlayer != null)
                        mMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                }
            }
        }
    };

    public IMediaPlayer.OnInfoListener mOnInfoListener = (iMediaPlayer, i, i1) -> {
        switch (i) {
            case KSYMediaPlayer.MEDIA_INFO_BUFFERING_START:
                Timber.e("======================Buffering Start.");
                break;
            case KSYMediaPlayer.MEDIA_INFO_BUFFERING_END:
                Timber.e("======================Buffering End.");

                break;
            case KSYMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                Timber.e("======================Audio Rendering Start.");
                break;
            case KSYMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                Timber.e("======================Video Rendering Start.");
                mIsReload = false;
                hideProgressAndUserImage();
                checkToShowAppOwnerTopFanJoined();
                unMuteMedia();
                playMedia();
                checkToShowTutorial();
                setupFollowHostSuggestion();
                enableTapOnScreen();
                if (mediaController != null) {
                    mediaController.updatePausePlay();
                }
//                startSubStream();

                break;
            case KSYMediaPlayer.MEDIA_INFO_RELOADED:
                mIsReload = false;
                Timber.e("======================Succeed to mPlayerReload video. hide AFK if any");
                return false;
            default:
                Timber.e("KSYMediaPlayer info %d - %d", i, i1);
                break;

        }
        return false;
    };

    boolean mIsReload = false;
    long currentRecordedPos = 0;
    private IMediaPlayer.OnErrorListener mOnErrorListener = (mp, what, extra) -> {
        Timber.e("OnErrorListener, Error:" + what + ",extra:" + extra);
        if (isRecorded && mMediaPlayer != null) {
            currentRecordedPos = mMediaPlayer.getCurrentPosition();
        }
        switch (what) {
            case KSYMediaPlayer.MEDIA_ERROR_TIMED_OUT:
            case KSYMediaPlayer.MEDIA_ERROR_IO:
            case KSYMediaPlayer.MEDIA_ERROR_INVALID_DATA:
            case KSYMediaPlayer.MEDIA_ERROR_DNS_PARSE_FAILED:
                if (mIsReload) {
                    Timber.e("MEDIA_ERROR_TIMED_OUT" + what + ",extra:" + extra);
                    showNetworkError();
                    handleShowEndLayout(getTrackingLiveReason(TRIGGER_END_BY_RELOAD_FAILED, isRecorded));
                } else {
                    mCompositeSubscription.add(Observable.just(mMediaUrl)
                            .delay(10, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .filter(s -> isFragmentUIActive() && mMediaPlayer != null)
                            .doOnNext(s -> mIsReload = true)
                            .doOnNext(s -> Timber.e("reload %s", s))
                            .subscribe(s -> {
                                try {
                                    mMediaPlayer.reset();
                                    mMediaPlayer.setDataSource(mMediaUrl);
                                    mMediaPlayer.prepareAsync();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }, this::handleRxError));
                }
                break;

            default:
                handleShowEndLayout(getTrackingLiveReason(String.format(Locale.US, "KSY_PLAYER_ERROR %d - %d ", what, extra), isRecorded));
                break;
        }

        return false;
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = mp -> {
        Timber.e("OnCompletionListener, play complete................");
        if (isRecorded) {
            reachedEnd("");
        } else {
            streamPause();
            mIsWaitingReconnect.set(true);
            setupWaitingTimeout();
        }
    };

    private void setupWaitingTimeout() {
        if (mIsWaitingTimeoutEnable.get()) return;
        mIsWaitingTimeoutEnable.set(true);
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (mLiveInviteDialog != null && mLiveInviteDialog.isVisible()) {
                    mLiveInviteDialog.dismissAllowingStateLoss();
                    mLiveInviteDialog = null;
                } else if (mIsCalling.get() && mSubStreamLayout != null) {
                    mSubStreamLayout.notifyCallEndedState();
                }
            });
        }
        mCompositeSubscription.add(Observable.just(true)
                .delay(2, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(s -> isFragmentUIActive() && mIsWaitingReconnect.get())
                .doOnNext(b -> mIsWaitingTimeoutEnable.set(false))
                .subscribe(aBoolean -> {
                    if (BuildConfig.DEBUG && isAdded()) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity().getApplicationContext(), "wait timeout 1 min - show end layout", LENGTH_SHORT).show();
                    }
                    handleShowEndLayout(getTrackingLiveReason("not received resume message after timeout 1 min", isRecorded));
                }));
    }

    /***
     * register network state receiver to listen about network change state
     */
//    BroadcastReceiver networkStateIntentReceiver = null;

    final BroadcastReceiver mPhoneCallStateIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int phoneState = intent.getIntExtra("state", 0);
                if (phoneState == TelephonyManager.CALL_STATE_OFFHOOK && mIsCalling.get()) {
                    //call accepted end video call if  any
                    Timber.e("mPhoneCallStateIntentReceiver");
                    mSubStreamLayout.notifyCallEndedState();
                }
            }
        }
    };
    private RxPermissions mRxPermissions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mChatManager = AppsterChatManger.getInstance(getActivity());
        mAgoraChatManager = AgoraChatManager.get();
        mFBCallbackManager = CallbackManager.Factory.create();
        mService = AppsterWebServices.get();
        mAppOwnerProfile = AppsterApplication.mAppPreferences.getUserModel();
        mRxPermissions = new RxPermissions(getActivity());
        this.mGiftQueue = new LinkedList<>();
        listTriviaDialog = new ArrayList<>();
        if(mAppOwnerProfile!=null) {
            mAgoraChatManager.login(getString(R.string.agora_app_id), mAppOwnerProfile.getUserName());
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            streamSlug = bundle.getString(STREAM_SLUG);
            publisherImage = bundle.getString(USER_URL);
            isRecorded = bundle.getBoolean(IS_RECORD);
//            mMediaUrl = bundle.getString(ARG_URL);
        }

        if (shouldShowTutorial()) {
            interceptEventBeforeTutorial();
        }
    }


    void handleData() {
        if (!StringUtil.isNullOrEmptyString(mMediaUrl)) {
            preparePlayer();
            if (listItemChat == null) listItemChat = new ArrayList<>();
            mChatManager.reconnectIfNeed();
            startPingingForLiveData();
        }
    }

    private boolean mIsReceivedEndMessage;

    /**
     * called from xmpp
     *
     * @param event new message event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NewMessageEvent event) {
        if (event.getData() != null && !mIsReceivedEndMessage) {
            ChatItemModelClass chatItemModelClass = event.getData();
            if (chatItemModelClass.getType() == null) return;
            Timber.e("** display name %s", chatItemModelClass.getChatDisplayName());
            Timber.e("** message: %s", chatItemModelClass.getMsg());
            Timber.e("** type : %s", chatItemModelClass.getType());
            switch (chatItemModelClass.getType()) {
                case ChatItemModelClass.CHAT_TYPE_END:
                    if (!isRecorded) {
                        handleEndStreamMessage(chatItemModelClass);
                    }
                    break;

                case ChatItemModelClass.CHAT_TYPE_BOT_JOIN_LIST:
                    mBotJoinObservable.onNext(chatItemModelClass);
                    break;

                case ChatItemModelClass.CHAT_TYPE_MUTE:
                    handleMuteMessage(chatItemModelClass);
                    break;

                case ChatItemModelClass.CHAT_TYPE_UNMUTE:
                    handleUnMuteMessage(chatItemModelClass);
                    break;

                case ChatItemModelClass.CHAT_TYPE_BLOCK:
                    handleBlockMessage(chatItemModelClass);
                    break;

                case ChatItemModelClass.CHAT_TYPE_LIKE:
                    handleLikeMessage(chatItemModelClass);
                    break;

                case ChatItemModelClass.CHAT_TYPE_GIFT:
                    updateTopFansList(chatItemModelClass.topFanList);
                    topfanFinalCheck(chatItemModelClass);
                    onGiftReceived(chatItemModelClass);
                    break;

                case ChatItemModelClass.CHAT_TYPE_LIVE_COMMERCE_ANNOUNCEMENT:
                    updateMessageInList(chatItemModelClass);
                    if (canShowGoShopButton()) showGoShopButton();
                    break;

                case ChatItemModelClass.CHAT_TYPE_STREAM_PAUSE:
                    streamPause();
                    break;

                case ChatItemModelClass.CHAT_TYPE_STREAM_RESTART:
                    streamRestart();
                    break;

                case ChatItemModelClass.TYPE_STEAM_TITLE_STICKER:
                    String title = chatItemModelClass.getStreamTitleStickerContent();
                    float x = chatItemModelClass.mStreamTitleStickerX;
                    float y = chatItemModelClass.mStreamTitleStickerY;
                    String color = chatItemModelClass.mStreamTitleColorCode;
                    mStickyPadFrameLayout.onLiveTitleReceived(title, x, y, color);
                    break;

                case ChatItemModelClass.CHAT_TYPE_KICK:
                    if (!chatItemModelClass.isGroup && streamSlug.equals(chatItemModelClass.slug)
                            && AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(chatItemModelClass.getBlockUserId())) {
                        finishIfKicked();
                    }
                    break;

                case ChatItemModelClass.CHAT_TYPE_LUCKY_WHEEL_SHOW:
                    break;

                case ChatItemModelClass.CHAT_TYPE_LUCKY_WHEEL_START:
                    break;

                case ChatItemModelClass.CHAT_TYPE_ADMIN_MESSAGE:
                    break;
                case ChatItemModelClass.CHAT_TYPE_STATISTIC:
//                    if (countUserHaveBeenView < chatItemModelClass.getTotalViewers()) {
                        countUserHaveBeenView = chatItemModelClass.getTotalViewers();
//                    }
                    if(countLikeCount < chatItemModelClass.getTotalLikes()){
                        countLikeCount = chatItemModelClass.getTotalLikes();
                    }
                    updateStreamViewCount();
                    updateTopFansList(chatItemModelClass.topFanList);
                    if (!isRecorded) {
                        if (mSubStreamData == null && chatItemModelClass.subStreamData != null && isAppOwner(chatItemModelClass.subStreamData.userId)) {
                            Timber.e("mSubStreamData == null && chatItemModelClass.subStreamData != null && isAppOwner(chatItemModelClass.subStreamData.userId)");
                            if (isTriggerLiveInviteDismiss(chatItemModelClass.subStreamData.status) && mLiveInviteDialog != null) {
                                Timber.e("isTriggerLiveInviteDismiss(chatItemModelClass.subStreamData.status) && mLiveInviteDialog != null");
                                mLiveInviteDialog.dismissAllowingStateLoss();
                                if (vIncomingCall != null) vIncomingCall.removeAllViews();
                                mLiveInviteDialog = null;
                            } else {
                                if (isTriggerLiveInviteDismiss(chatItemModelClass.subStreamData.status)) {
                                    Timber.e("chatItemModelClass.subStreamData.status == State.DISCONNECTED don't show live invite");
                                    return;
                                }

                                mLiveInviteDialog = LiveInviteDialog.newInstance("Host", currentStreamDetails.getPublisher().getUserImage());
                                mLiveInviteDialog.setLiveInviteListener(new LiveInviteDialog.LiveInviteListener() {
                                    @Override
                                    public void onDecline() {
                                        chatItemModelClass.subStreamData.status = State.REJECT;
                                        sendToGroup(updateAndGetSubStreamStatistic(chatItemModelClass.subStreamData));
                                        removeIncomingView();
                                    }

                                    @Override
                                    public void onAccept() {
                                        if (isPermissionGranted()) {
                                            Timber.e("isPermissionGranted");
                                            mLiveInviteDialog.dismissAllowingStateLoss();
                                            chatItemModelClass.subStreamData.status = State.ACCEPT;
                                            sendToGroup(updateAndGetSubStreamStatistic(chatItemModelClass.subStreamData));
                                            updateSubStreamState(chatItemModelClass.subStreamData);
                                            if (vIncomingCall != null && vIncomingCall.getChildCount() > 0) {
                                                IncomingCallView incomingCallView = (IncomingCallView) vIncomingCall.getChildAt(0);
                                                incomingCallView.updateState(State.ACCEPT);
                                            }
                                        }
                                    }
                                });
                                getChildFragmentManager().beginTransaction().add(mLiveInviteDialog, "LiveInvite").commitAllowingStateLoss();
                                if (vIncomingCall != null && getContext() != null && mAppOwnerProfile != null) {
                                    removeIncomingView();
                                    IncomingCallView incomingCallView = new IncomingCallView(getContext(), vIncomingCall, () -> {
                                        if (mSubStreamLayout != null)
                                            mSubStreamLayout.onCountDownCompleted();
                                    });
                                    incomingCallView.load(mAppOwnerProfile.getUserImage(), mAppOwnerProfile.getDisplayName());
                                }
                                Timber.e("show Live invite popup");
                            }
                        } else {
                            updateSubStreamState(chatItemModelClass.subStreamData);
                        }
                    }

                    break;
                default:
                    if (chatItemModelClass.isGroup) {//CHAT_TYPE_MESSAGE
                        topfanFinalCheck(chatItemModelClass);
                        chatItemModelObservable.onNext(chatItemModelClass);
                    }
            }
        }
    }

    private void removeIncomingView() {
        if (vIncomingCall.getChildCount() > 0) {
            vIncomingCall.removeAllViews();
        }
    }

    boolean isPermissionGranted() {

        final Activity activity = getActivity();
        if (activity == null) return false;
        int cameraPerm = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int audioPerm = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        int readStoragePerm = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (cameraPerm != PackageManager.PERMISSION_GRANTED ||
                audioPerm != PackageManager.PERMISSION_GRANTED ||
                readStoragePerm != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.e(TAG, "No CAMERA or AudioRecord or Storage permission, please check");
//                    showToast("No CAMERA or AudioRecord or Storage permission, please check",
//                            Toast.LENGTH_LONG);
            } else {

                String[] permissions = {Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CAMERA_AUDIOREC);
            }
//                }
        } else {
            return true;
        }
        return false;
    }

    private boolean isTriggerLiveInviteDismiss(@State int status) {
        switch (status) {
            case State.NO_ANSWER:
            case State.DISCONNECTING:
            case State.DISCONNECTED:
                return true;
            default:
                return false;
        }
    }

    void updateSubStreamState(SubStreamData subStreamData) {
        if (subStreamData == null || isRecorded || !mIsFirstFrameRendered) return;
        Timber.e("updateSubStreamState isRecorded %s", isRecorded);
        mSubStreamData = subStreamData;
        mSubSlug = subStreamData.slug;
        if (mSubStreamData.userId == Integer.parseInt(mAppOwnerProfile.getUserId())) {

            boolean isCalling = !(subStreamData.status == State.DISCONNECTING || subStreamData.status == State.DISCONNECTED);
            if (!isCalling) return;
            //sub screen for guest
            if (vsSubScreen.getParent() != null && mSubStreamLayout == null) {
                View view = vsSubScreen.inflate();
                mSubStreamLayout = new GuestSubStreamLayout(view, this);
                updateSubStreamLayoutPosition();
            }

            if (vsHostSubScreen.getParent() != null && mHostSubStreamLayout == null) {
                View view = vsHostSubScreen.inflate();
                mHostSubStreamLayout = new HostSubStreamLayout(view, this);
                updateHostSubStreamLayoutPosition();
            }

            if (subCameraContainer.getVisibility() != View.VISIBLE) {
//                mSubCameraView.onResume();
                subCameraContainer.setVisibility(View.VISIBLE);
            }
            mSubStreamLayout.setGuestAvatar(subStreamData.userImage);
            if (currentStreamDetails != null)
                mHostSubStreamLayout.setGuestAvatar(currentStreamDetails.getPublisher().getUserImage());
            mSubStreamLayout.setGuestDisplayName(subStreamData.displayName);
            mSubStreamLayout.startCameraPreviewWithPermCheck();
            mSubStreamLayout.startStream(streamSlug);
            if (vsSubScreen != null && mSubStreamLayout != null) {
                vsSubScreen.setVisibility(View.VISIBLE);
            }
            if (vsHostSubScreen != null && mHostSubStreamLayout != null) {
                vsHostSubScreen.setVisibility(View.VISIBLE);
            }
            mSubStreamLayout.updateState(subStreamData.status);
            mHostSubStreamLayout.updateState(subStreamData.status);

        } else {
            //sub player for viewers
            if (vsSubPlayer.getParent() != null) {

                View view = vsSubPlayer.inflate();
                Timber.e("vsSubPlayer.getParent() != null - init subview");
                mSubPlayerLayout = new SubPlayerLayout(view);
                mSubPlayerLayout.setClickListener(new SubPlayerLayout.OnClickListener() {
                    @Override
                    public void onEndCallClicked() {
                        // do nothing on viewer side
                    }

//                    @Override
//                    public void onSubScreenDisplayNameClicked(String userName, String profilePic) {
//                        showUserProfileDialog(userName, profilePic);
//                    }

                    @Override
                    public void onCountDownCompleted() {
                        // TODO: 9/25/17 handle after countdown
//                        if (mSubPlayerLayout != null) mSubPlayerLayout.streamStarted();
                    }

                    @Override
                    public void onShowProfileClicked(String userName, String profilePic) {
                        showUserProfileDialog(userName, profilePic);
                    }
                });
                mSubPlayerLayout.setGuestAvatar(subStreamData.receiver.userImage);
                mSubPlayerLayout.setGuestDisplayName(subStreamData.receiver.displayName);
                mSubPlayerLayout.setGuestUserName(subStreamData.receiver.userName);
                mSubPlayerLayout.updateState(State.CONNECTING);
                updateSubPlayerLayoutPosition();
            }


            if (subStreamData.status == State.DISCONNECTING) {

                if (mSubPlayerLayout != null) mSubPlayerLayout.updateState(subStreamData.status);
//            mSubPlayerLayout.stopPlayer();
            } else {
                if (subStreamData.status == State.DISCONNECTED) {
                    updateSubStreamDisconnected();
                } else if (subStreamData.status == State.REJECT || subStreamData.status == State.NO_ANSWER) {
                    mSubPlayerLayout.updateState(subStreamData.status);
                    //change to disconnected after 2s
                    mCompositeSubscription.add(Observable.just(true)
                            .delay(2, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .filter(aBoolean -> isFragmentUIActive())
                            .subscribe(aBoolean -> updateSubStreamDisconnected()));
                } else if (subStreamData.status == State.CONNECTED) {
                    mSubPlayerLayout.streamStarted();
                } else {
                    if (vsSubPlayer.getVisibility() != View.VISIBLE)
                        vsSubPlayer.setVisibility(View.VISIBLE);
                    mSubPlayerLayout.setGuestAvatar(subStreamData.receiver.userImage);
                    mSubPlayerLayout.setGuestDisplayName(subStreamData.receiver.displayName);
                    mSubPlayerLayout.setGuestUserName(subStreamData.receiver.userName);
                    mSubPlayerLayout.updateState(subStreamData.status);
                }
            }
        }
    }

    private void updateHostSubStreamLayoutPosition() {
        if (mHostSubStreamLayout == null) return;
        int screenWidth = Utils.getScreenWidth();
        int screenHeight = Utils.getScreenHeight();
//        if (hasNavBar(getResources())) {
        screenHeight += getNavigationBarSize(getContext()).y;
//        }

        int left = (int) (GUEST_SCREEN_HOST_CAM_LEFT_BEGIN * screenWidth);
        int top = (int) (GUEST_SCREEN_HOST_CAM_TOP_BEGIN * screenHeight);
        View view = mHostSubStreamLayout.getView();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = (int) (GUEST_SCREEN_HOST_CAM_WIDTH * screenWidth);
        layoutParams.height = (int) (GUEST_SCREEN_HOST_CAM_HEIGHT * screenHeight);
        layoutParams.setMargins(left, top, 0, 0);
        view.requestLayout();
    }

    private void updateSubPlayerLayoutPosition() {
        if (mSubPlayerLayout == null) return;
        int screenWidth = Utils.getScreenWidth();
        int screenHeight = Utils.getScreenHeight();
//        if (hasNavBar(getResources())) {
        screenHeight += getNavigationBarSize(getContext()).y;
//        }

//        int left = (int) (HOST_SCREEN_GUEST_CAM_LEFT_BEGIN * screenWidth);
//        int top = (int) (HOST_SCREEN_GUEST_CAM_TOP_BEGIN * screenHeight);
//        View view = mSubPlayerLayout.getView();
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
//        layoutParams.width = (int) (HOST_SCREEN_GUEST_CAM_WIDTH * screenWidth);
//        layoutParams.height = (int) (HOST_SCREEN_GUEST_CAM_HEIGHT * screenHeight);
//        layoutParams.setMargins(left, top, 0, 0);
//        view.requestLayout();
        float[] scaleRatio = getCroppedVideoInPixel(screenHeight, screenWidth);
        int centerScreenX = (int) (screenWidth * 0.5f);
        int centerScreenY = (int) (screenHeight * 0.5f);
        int left = (int) (centerScreenX + (HOST_SCREEN_GUEST_CAM_LEFT_BEGIN - 0.5f) * screenWidth * scaleRatio[0]);
        int top = (int) (centerScreenY + (HOST_SCREEN_GUEST_CAM_TOP_BEGIN - 0.5f) * screenHeight * scaleRatio[1]);
        View view = mSubPlayerLayout.getView();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = (int) ((HOST_SCREEN_GUEST_CAM_WIDTH * screenWidth) * scaleRatio[0]);
        layoutParams.height = (int) ((HOST_SCREEN_GUEST_CAM_HEIGHT * screenHeight) * scaleRatio[1]);
        layoutParams.setMargins(left, top, 0, 0);
        view.requestLayout();
    }

    private void updateSubStreamLayoutPosition() {
        if (mSubStreamLayout == null) return;
        int screenWidth = Utils.getScreenWidth();
        int screenHeight = Utils.getScreenHeight();
//        if (hasNavBar(getResources())) {
        screenHeight += getNavigationBarSize(getContext()).y;
//        }

        int left = (int) (GUEST_SCREEN_GUEST_CAM_LEFT_BEGIN * screenWidth);
        int top = (int) (GUEST_SCREEN_GUEST_CAM_TOP_BEGIN * screenHeight);
        View view = mSubStreamLayout.getView();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = (int) (GUEST_SCREEN_GUEST_CAM_WIDTH * screenWidth);
        layoutParams.height = (int) (GUEST_SCREEN_GUEST_CAM_HEIGHT * screenHeight);
        layoutParams.setMargins(left, top, 0, 0);
        view.requestLayout();

//        int screenWidth = Utils.getScreenWidth();
//        int screenHeight = Utils.getScreenHeight();
////        if (hasNavBar(getResources())) {
//        screenHeight += getNavigationBarSize(getContext()).y;
////        }
//        float[] scaleRatio = getCroppedVideoInPixel(screenHeight,screenWidth);
//        float centerScreenX = screenWidth * 0.5f;
//        float centerScreenY = screenHeight * 0.5f;
//        int left = (int) (centerScreenX + (HOST_SCREEN_GUEST_CAM_LEFT_BEGIN - 0.5f)* screenWidth * scaleRatio[0]);
//        int top = (int) (centerScreenY + (HOST_SCREEN_GUEST_CAM_TOP_BEGIN - 0.5f) * screenHeight * scaleRatio[1]);
//        View view = mSubStreamLayout.getView();
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
//        layoutParams.width = (int) (HOST_SCREEN_GUEST_CAM_WIDTH * screenWidth * scaleRatio[0]);
//        layoutParams.height = (int) (HOST_SCREEN_GUEST_CAM_HEIGHT * screenHeight * scaleRatio[1]);
//        layoutParams.setMargins(left, top, 0, 0);
//        view.requestLayout();
    }

    /**
     * try to get the dimension which cropped off
     * the first element is width
     * the other is height
     *
     * @param screenHeight
     */
    private float[] getCroppedVideoInPixel(int screenHeight, int screenWidth) {
        float[] result = new float[]{1f, 1f};
        if (isStreamVerticallyCropped(screenHeight, screenWidth)) {
            float scaledVideoHeight = ((float) screenWidth * mVideoHeight) / mVideoWidth;
            result[1] = scaledVideoHeight / screenHeight;
        } else {
            float scaledVideoWidth = ((float) screenHeight * mVideoWidth) / mVideoHeight;
            result[0] = scaledVideoWidth / screenWidth;
        }

        return result;
    }

    private boolean isStreamVerticallyCropped(int screenHeight, int screenWidth) {
        float a = (float) mVideoWidth / screenWidth;
        float b = (float) mVideoHeight / screenHeight;
        return a < b;
    }

    private void updateSubStreamDisconnected() {
        if (mSubPlayerLayout != null) mSubPlayerLayout.updateState(State.DISCONNECTED);
        if (vsSubPlayer != null) vsSubPlayer.setVisibility(View.GONE);
        if (mSubStreamData != null) mSubStreamData = null;
    }

    private void topfanFinalCheck(ChatItemModelClass chatItemModelClass) {
        /* support old version - can remove if users upgraded to 1.3.5  */
//        if (chatItemModelClass.rank == -1) {
        chatItemModelClass.rank = mapWithCurrentTopFan(chatItemModelClass.getUserName());
//        }
    }

    private void finishIfKicked() {
        Utils.hideSoftKeyboard(getActivity());
        mIntervalSubject.onNext(-1L);
        setDatainish();
        releaseBotUsers();
        mediaPlayerControl = null;
        getActivity().finish();
    }

    private void handleLikeMessage(ChatItemModelClass chatItemModelClass) {
        if (chatItemModelClass.isLiked() && !isBotListMessage(chatItemModelClass)) {
            /* support old version - can remove if users upgraded to 1.3.5  */
            if (chatItemModelClass.rank == -1) {
                chatItemModelClass.rank = mapWithCurrentTopFan(chatItemModelClass.getUserName());
            }
            chatItemModelObservable.onNext(chatItemModelClass);
            countLikeCount = chatItemModelClass.getTotalLikes();
            updateStreamViewCount();
        }
        showHeartObservable.onNext(null);
    }

    private void handleBlockMessage(ChatItemModelClass chatItemModelClass) {
        if (AppsterApplication.mAppPreferences.getUserId().equals(chatItemModelClass.getBlockUserId())) {
//            AppsterUtility.addPrefListItem(getContext(), Constants.STREAM_BLOCKED_LIST, chatItemModelClass.getBlockUserId(), streamSlug);
            Toast.makeText(getActivity().getApplicationContext(), "You have been blocked for this stream!", Toast.LENGTH_LONG).show();
            closeStream(true);
        }
    }

    private void handleUnMuteMessage(ChatItemModelClass chatItemModelClass) {
        if (AppsterApplication.mAppPreferences.getUserId().equals(chatItemModelClass.getMutedUserId())) {
            mIsMuted = false;
            AppsterUtility.removePrefListItem(getContext(), Constants.STREAM_MUTE_LIST, chatItemModelClass.getMutedUserId(), streamSlug);
        }
    }

    private void handleMuteMessage(ChatItemModelClass chatItemModelClass) {
        String muteUserId = chatItemModelClass.getMutedUserId();
//                Timber.e("muted Id = %s",muteUserId);
        if (AppsterApplication.mAppPreferences.getUserId().equals(muteUserId)) {
            mIsMuted = true;
            AppsterUtility.addPrefListItem(getContext(), Constants.STREAM_MUTE_LIST, muteUserId, streamSlug);
            setupUnMuteTime(streamSlug, currentStreamDetails.getStreamId(), muteUserId);
        }

    }

    private void handleBotListMessage(ChatItemModelClass chatItemModelClass) {
        String userName = chatItemModelClass.getUserName();
        if (!isAppOwner(userName) && !isStreamOwner(userName) && !mChatGroupAdapter.isUserHasJoined(userName)) {
            handleBotJoinEventMessage(chatItemModelClass);
        }

        if (chatItemModelClass.getTotalLikes() != 0) {
            countLikeCount = chatItemModelClass.getTotalLikes();
        }

        if (chatItemModelClass.getTotalViewers() != 0) {
//            if (countUserHaveBeenView < chatItemModelClass.getTotalViewers()) {
                countUserHaveBeenView = chatItemModelClass.getTotalViewers();
//            }
        }
        updateStreamViewCount();
    }

    private void handleEndStreamMessage(ChatItemModelClass chatItemModelClass) {
        if (chatItemModelClass == null) return;
        //only admin can end host stream
//        showEndStreamAndToastMessage(chatItemModelClass);


        mIsReceivedEndMessage = true;
        long duration;
        duration = chatItemModelClass.getDurationTime();
//                ((TextView) getActivity().findViewById(R.id.duration_time)).setText(AppsterUtility.parseStreamingTimeToHHMM(mFollowHostButtonAnimationDuration));
        countLikeCount = chatItemModelClass.getTotalLikes();
        countUserHaveBeenView = chatItemModelClass.getTotalViewers();
        mTotalGoldFans = chatItemModelClass.getTotalReceivedStars();
        showEndStreamLayout("");
        Timber.e("show end stream");
        if (BuildConfig.DEBUG && isAdded()) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.message_received_end_message), LENGTH_SHORT).show();
        }
        if (mEndStreamLayout != null)
            mEndStreamLayout.updateEndStreamData(countUserHaveBeenView, countLikeCount, mTotalGoldFans, duration);
    }

    private void onGiftReceived(ChatItemModelClass chatItemModelClass) {
        long totalStars = 0;
        try {
            totalStars = Long.parseLong(chatItemModelClass.getReceiverStars());
        } catch (Exception e) {
            Timber.e(e);
        }
        if (chatItemModelClass.isExpensiveGift()) {
            chatItemModelObservable.onNext(chatItemModelClass);
            showExpensiveGift(chatItemModelClass);
//            rlExpensiveGift.addGift(chatItemModelClass);

        } else {
            llGiftGroup.addGift(chatItemModelClass);
            llGiftGroup.setGiftComboGroupViewListener(item -> {
                topfanFinalCheck(item);
                chatItemModelObservable.onNext(item);
            });
        }
        updateTotalStars(totalStars);
        onDailyTopFansListReceivedFromXmpp(chatItemModelClass.dailyTopFansList);
    }

    private void onDailyTopFansListReceivedFromXmpp(List<DailyTopFanModel> dailyTopFansList) {
        if (!isRecorded && dailyTopFansList != null) {
            mGiftRankingGroupView.updateRankingList(mIsTriviaShow ? Collections.EMPTY_LIST : dailyTopFansList);

        } else {
            //for supporting versions below v1.4.11 which can send daily-top-fans-list
            //we have to call api to keep the list update to date
//            mGetDailyTopFansListObservable.onNext(null);
        }
    }

    public void getDailyTopFansList() {
        GetTopFanModel getTopFanModel = new GetTopFanModel();
        getTopFanModel.setUserId(currentStreamDetails.getUserId());
        getTopFanModel.setLimit(0);
        mCompositeSubscription.add(mService.getAllTopFanStream(AppsterUtility.getAuth(), getTopFanModel.getMappedRequest())
                .subscribe(topFansListBaseResponse -> {
                    if (topFansListBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                        mGiftRankingGroupView.updateRankingList(mIsTriviaShow ? Collections.EMPTY_LIST : topFansListBaseResponse.getData().dailyTopFans);
                }, Timber::e));
    }

    private void showEndStreamAndToastMessage(ChatItemModelClass chatItemModelClass) {
        if (chatItemModelClass != null && chatItemModelClass.getUserName().equalsIgnoreCase(ADMIN_USERNAME) && !chatItemModelClass.getMsg().isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), chatItemModelClass.getMsg(), Toast.LENGTH_LONG).show();
        }
    }

    private void showExpensiveGift(ChatItemModelClass chatItemModelClass) {
        if (mExpensiveGiftDialog == null) {
            mExpensiveGiftDialog = new ExpensiveGiftDialog();
            mExpensiveGiftDialog.setDialogDismissCallback(() -> mCompositeSubscription.add(Observable.just(null)
                    .delay(100, TimeUnit.MILLISECONDS)
                    .filter(o -> isFragmentUIActive())
                    .subscribe(o -> {
                        if (!mGiftQueue.isEmpty()) {
                            mExpensiveGiftDialog.addGift(mGiftQueue.get(0));
                            mGiftQueue.remove(0);
                            mExpensiveGiftDialog.show(getChildFragmentManager(), ExpensiveGiftDialog.class.getName());
                        } else {
                            isExpensiveGiftDialogShown = false;
                        }
                    }, Timber::e)));
        }
        //if the expensive gift dialog is showing then queue the incoming gift
        if (isExpensiveGiftDialogShown) {
            mGiftQueue.add(chatItemModelClass);
        } else {
            mExpensiveGiftDialog.addGift(chatItemModelClass);
            mExpensiveGiftDialog.show(getChildFragmentManager(), ExpensiveGiftDialog.class.getName());
            isExpensiveGiftDialogShown = true;
        }
    }

    private void setupUnMuteTime(String streamSlug, int streamId, String userId) {
        //set up time reset
        Intent intentAlarm = new Intent(getContext(), InternalMessageReceiver.class);
        intentAlarm.putExtra(InternalMessageReceiver.STREAM_ID_KEY, streamSlug);
        intentAlarm.putExtra(InternalMessageReceiver.STREAM_USER_ID_KEY, userId);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        //1hour
        Long time = new GregorianCalendar().getTimeInMillis() + 60 * 60 * 1000;


//        Long time = new GregorianCalendar().getTimeInMillis() + 30 * 1000;
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(getContext(), streamId, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }

    /**
     * called from xmpp
     *
     * @param event user join group event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UserJoinLeaveEvent event) {

        if (!isFragmentUIActive() && isStreamOwner(event.getUserName()))
            return;

        watcherAddEventObservable.onNext(event);
    }

    private void updateMessageInList(ChatItemModelClass chatItemModelClass) {
        Timber.e("updateMessageInList");
        if (mChatGroupAdapter != null) {
            mChatGroupAdapter.newChatItem(chatItemModelClass);
            scrollCommentListToEnd();
        }
    }

    /**
     * add a list of messages to chat-box-message for recorded stream
     *
     * @param chatItemModelClasses list of messages that buffered
     */
    void updateRecordedBufferedMessageInList(List<ChatItemModelClass> chatItemModelClasses) {
        Timber.e("updateRecordedBufferedMessageInList");
        if (mChatGroupAdapter != null) {
            mChatGroupAdapter.newListChat(chatItemModelClasses);
            scrollCommentListToEnd();
        }
    }

    /**
     * add a list of messages to chat-box-message
     *
     * @param chatItemModelClasses list of messages that buffered
     */
    void updateBufferedMessageInList(List<ChatItemModelClass> chatItemModelClasses) {
        Timber.d("updateBufferedMessageInList");
        if (mChatGroupAdapter != null) {
            mChatGroupAdapter.newListChatNotClear(chatItemModelClasses);
            scrollCommentListToEnd();
        }
    }

    private void transformJointEventToMessage(List<UserJoinLeaveEvent> newWatchers) {
        mCompositeSubscription.add(Observable.from(newWatchers)
                .filter(this::checkIsNewUserJoin)
                .flatMap(flatMapJoinLeaveEvent)
                .doOnNext(chatItemModelClass -> {
                    chatItemModelClass.rank = mapWithCurrentTopFan(chatItemModelClass.getUserName());
                    notifyTopFanJoined(chatItemModelClass);
                })
                .toList()
                .filter(chatItemModelClasses -> !chatItemModelClasses.isEmpty())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateBufferedMessageInList, Timber::e));
    }


    private void updateTopFansList(List<String> topFanList) {
        if (topFanList.isEmpty() || mTopFanList.equals(topFanList)) return;
        mTopFanList.clear();
        mTopFanList.addAll(topFanList);
        mCurrentFanRanking.set(mTopFanList.indexOf(mAppOwnerProfile.getUserName()));
    }

    private void checkToShowAppOwnerTopFanJoined() {
        if (mCurrentFanRanking.get() != -1) {
            ChatItemModelClass chatItem = new ChatItemModelClass();
            chatItem.setProfilePic(mAppOwnerProfile.getUserImage());
            chatItem.setChatDisplayName(mAppOwnerProfile.getDisplayName());
            if (!StringUtil.isNullOrEmptyString(mAppOwnerProfile.getUserName()))
                chatItem.setUserName(mAppOwnerProfile.getUserName());
            chatItem.rank = mCurrentFanRanking.get();
            notifyTopFanJoined(chatItem);
        }
    }

    private void notifyTopFanJoined(ChatItemModelClass joinMessage) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (joinMessage.rank != -1)
                    llGiftGroup.addTopFanJoined(joinMessage);
            });
        }

    }

    private int mapWithCurrentTopFan(String userName) {
        return mTopFanList.indexOf(userName);
    }

    private boolean checkIsNewUserJoin(UserJoinLeaveEvent newWatcher) {
        return !checkIsCurrentUser(newWatcher.getUserName())
                && checkStatusIsJoinAndRequiredFieldsNotEmpty(newWatcher)
                && checkNotIsStreamOwnerAndNotJoined(newWatcher)
                && newWatcher.isJoined();
    }

    private boolean checkNotIsStreamOwnerAndNotJoined(UserJoinLeaveEvent newWatcher) {
        return !isStreamOwner(newWatcher.getUserName()) && !mChatGroupAdapter.isUserHasJoined(newWatcher.getUserName());
    }

    private boolean checkIsCurrentUser(String userName) {
        return mAppOwnerProfile != null && mAppOwnerProfile.getUserName().equals(userName);
    }

    private boolean checkIsCurrentUser(int userId) {
        return mAppOwnerProfile != null && Integer.parseInt(mAppOwnerProfile.getUserId()) == userId;
    }

    private boolean checkStatusIsJoinAndRequiredFieldsNotEmpty(UserJoinLeaveEvent newWatcher) {
        return newWatcher.isJoined() && !TextUtils.isEmpty(newWatcher.getUserName()) && !TextUtils.isEmpty(newWatcher.getDisplayName());
    }

    private Func1<UserJoinLeaveEvent, Observable<ChatItemModelClass>> flatMapJoinLeaveEvent = newWatcher -> {
        ChatItemModelClass chatItem = new ChatItemModelClass();
        chatItem.setUserName(newWatcher.getUserName());
        chatItem.setChatDisplayName(newWatcher.getDisplayName());
        chatItem.setMessageType(ChatItemModelClass.CHAT_TYPE_USER_JOIN_LIST);
        chatItem.setMsg(getString(R.string.message_joined));
        return Observable.just(chatItem);
    };

    boolean isStreamOwner(String userName) {
        return currentStreamDetails != null && currentStreamDetails.getPublisher() != null && currentStreamDetails.getPublisher().getUserName().equals(userName);
    }

    boolean isAppOwner(String userName) {
        return mAppOwnerProfile != null && checkIsCurrentUser(userName);
    }

    boolean isAppOwner(int userId) {
        return mAppOwnerProfile != null && checkIsCurrentUser(userId);
    }


    private void handleBotJoinEventMessage(ChatItemModelClass bot) {
        bot.setMessageType(ChatItemModelClass.CHAT_TYPE_BOT_JOIN_LIST);
        bot.setMsg(getString(R.string.message_joined));
        updateMessageInList(bot);
    }


    void updateStreamViewCount() {
        if (!isFragmentUIActive())
            return;

        tvCurrentView.setText(Utils.formatThousand(countUserHaveBeenView));
        tvTotalLiked.setText(Utils.formatThousand(countLikeCount));
    }

    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }

    private void initListUserWatcher() {
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        hlvCustomList.setLayoutManager(layoutManager);
        listUserAdapter = new AdapterListWatcher(getActivity());
        hlvCustomList.setAdapter(listUserAdapter);
//        hlvCustomList.setHasFixedSize(true);

        RecycleItemClickSupport.addTo(hlvCustomList).setOnItemClickListener((recyclerView, position, v) -> {
            String userName = listUserAdapter.getItemAt(position);
            showUserProfileDialog(userName, "");
        });

    }

    void showUserProfileDialog(String userName, String userImage) {
        if (isFragmentUIActive()) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            DialogUserProfileFragment userProfileFragment = DialogUserProfileFragment.newInstance(
                    userName, userImage, true, streamSlug, false, isStreamOwner(userName));
            userProfileFragment.setUserProfileActionListener(this);
            getChildFragmentManager().beginTransaction().add(userProfileFragment, "UserProfileView").commitAllowingStateLoss();
        }

    }

    private void onShowProfileClicked(String userName) {
        showUserProfileDialog(userName, "");
    }

    private void hideShopDialog() {
        if (mLiveShopDialog != null) {
            mLiveShopDialog.dismissAllowingStateLoss();
        }
    }

    private void hideUserProfileDialog() {
        if (isAdded() && isResumed() && !isDetached()) {
            try {
                final DialogUserProfileFragment profileFragment = (DialogUserProfileFragment) getChildFragmentManager().findFragmentByTag("UserProfileView");
                if (profileFragment != null) {
                    profileFragment.dismiss();
                }

                final DialogUserProfileFragment profileFromTopFanFragment = (DialogUserProfileFragment) getFragmentManager().findFragmentByTag("UserProfileView");
                if (profileFromTopFanFragment != null) {
                    profileFromTopFanFragment.dismiss();
                }

                final DialogReport dialogReport = (DialogReport) getChildFragmentManager().findFragmentByTag("Report");
                if (dialogReport != null) {
                    dialogReport.dismiss();
                }
            } catch (IllegalStateException e) {
                Timber.e(e);
            }
        }
    }

    private void hideShareDialog() {
        if (isAdded() && isResumed() && !isDetached()) {
            try {
                final SharePostDialog sharePostDialog = (SharePostDialog) getFragmentManager().findFragmentByTag("Share");
                if (sharePostDialog != null) {
                    sharePostDialog.dismiss();
                }

            } catch (IllegalStateException e) {
                Timber.e(e);
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
        Timber.e("onResume");


        if (mIsTapOnShareSNS) {
            sendMessageShareStream();
        }

        if (mIsReceivedEndMessage || mEndStreamLayout != null) {
            hideUserProfileDialog();
            hideShareDialog();
        }
        if (isStoped) {
            return;
        }

        if (sendGift != null) {
            sendGift.resume();
        }

        if (counterHomePress == 0) {
            Timber.e("counterHomePress == 0");
            handleData();
            calcKeyboardHeight();
            counterHomePress++;

        } else if (isRecordHasEnded()) {
            Timber.e("record has ended");
            if (mediaController != null && mediaController.isShowing()) {
                mediaController.hide();
            }

            handleShowEndLayout("");
            if (isAdded()) {
                Toast.makeText(getActivity().getApplicationContext(), "RecordHasEnded when resume", LENGTH_SHORT).show();
            }
            isStoped = true;

        } else if (!isDisconnectedPlayer()) {
            Timber.e("resume player");
            // just in case. it will call Play method.
//            mMediaPlayer.start();
            calcKeyboardHeight();
            counterHomePress = 0;

        } else {
            Timber.e("preparePlayer");
//            preparePlayer();

            counterHomePress = 0;
        }

        // reset the count fails to 0
//        countFailLoadVideosPLays = 0;

        if (mMediaPlayer != null) {
            mMediaPlayer.runInForeground();
        }
//        if (mSubPlayerLayout != null) mSubPlayerLayout.onResume();
        if (mSubStreamLayout != null) mSubStreamLayout.onResume();

        mHasCalledEndedAPI.set(false);

    }

    @Override
    public void onPause() {
        super.onPause();
//        mConnectionClassManager.remove(this);
        isHomePress = true;
        if (mSubStreamLayout != null) mSubStreamLayout.onStop();
        Utils.hideSoftKeyboard(getActivity());

        Timber.e("onPause");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        SocialManager.cancelInstance();
//        releasePlayer();
        Timber.e("onDestroy");
        if (avatarBlur != null && !avatarBlur.isRecycled()) {
            avatarBlur.recycle();
            avatarBlur = null;
        }

        mChatManager.clearArrayCurrentUserInStream();
        mChatManager.clearArrayCurrentBotInStream();
        mChatManager.setNumberRecreateGroup(0);
        mIntervalSubject.onNext(-1L);
        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            releaseBotUsers();
            mChatManager.leaveRoom();
            if(mAgoraChatManager!=null) mAgoraChatManager.leaveGroup(streamSlug);
        }
//        if (allBotImages != null) {
//            allBotImages.clear();
//        }

        releasePlayer();
        mAppOwnerProfile = null;
//        mChatManager.leaveRoom();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mPhoneCallStateIntentReceiver, new IntentFilter(BELIVE_CALL_DETEECTOR));

        View rootView = inflater.inflate(R.layout.fragment_media_player, container, false);
        ButterKnife.bind(this, rootView);
//        mMediaPlayer.setDecodeMode(KSYMediaPlayer.KSYDecodeMode.KSY_DECODE_MODE_AUTO);
        showFullscreen();
        disableTapOnScreen();
        mIsCanChangeNetworkText = new AtomicBoolean();
        mIsCanChangeNetworkText.set(true);
        keyboardHeightProvider = new KeyboardHeightProvider(getActivity());
        if (checkCurrentInfoNotNull()) {
            AppsterUtility.removePrefListItem(getContext(), Constants.STREAM_MUTE_LIST, mAppOwnerProfile.getUserId(), streamSlug);
        }
        fragmentRootView.findViewById(R.id.fragment_root_view).post(() -> keyboardHeightProvider.start());
        txtTimer.setVisibility(View.GONE);
        tvLiveOwnerUserName.setVisibility(View.VISIBLE);
        mCompositeSubscription.add(AppsterUtility.clicks(mVLiveStreamerBg)
                .subscribe(aVoid -> {
                            showHostProfile();
                        }
                        , error -> Timber.e(error.getMessage())));

        mCompositeSubscription.add(AppsterUtility.clicks(btnPoint)
                .subscribe(aVoid -> {
                            if (currentStreamDetails != null) {
                                // reuse the formatted point
                                pointInfoDialog = PointInfoDialog.newInstance(tvUserPoint.getText().toString(),
                                        currentStreamDetails.getUserPointInfoUrl());
                                pointInfoDialog.show(getFragmentManager(), "PointInfoDialog");
                                EventTracker.trackMBPointsTab(AppsterApplication.mAppPreferences.getUserModel().getUserId());
                            }
                        }
                        , this::handleRxError));

        mProgress.setVisibility(View.VISIBLE);

        if (!StringUtil.isNullOrEmptyString(publisherImage)) {
            blurImage(publisherImage);
        }

        getNaughtyWords();
        getStreamData("");

//        if (allBotImages == null) {
//            allBotImages = new HashMap<>();
//        }

        initListUserWatcher();
        setupViewerObservables();

        setupLiveTopPanelBg();
        handleMaintenanceMessage(AppsterApplication.mAppPreferences.getMaintenanceModel());
//        showTriviaWinnerLayout(30, "S$ 217.39", "Each winners get:");
        return rootView;
    }

    void showHostProfile() {
        if (currentStreamDetails != null) {
            showUserProfileDialog(currentStreamDetails.getPublisher().getUserName(), publisherImage);
        }
    }

    void showStreamTitle() {
        if (isRecorded) {
            mRecordedUserMessage = new ArrayList<>();
            mVideoTimeChangedSubscription = mVideoTimeChanged.distinctUntilChanged().debounce(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(integer -> mMediaPlayer != null)
                    .doOnNext(integer -> Timber.e("time %d - mFollowHostButtonAnimationDuration %d", integer, mMediaPlayer.getDuration() / 1000))
                    .subscribe(currentTime -> {
                        boolean isScrub = currentTime - mCurrentVideoTime != 1;
                        mCurrentVideoTime = currentTime;
                        addMessageToList(currentTime, isScrub);
                        filterShowStreamTitle(currentTime);
                        if (mCurrentVideoTime > 0 && mCurrentVideoTime == mMediaPlayer.getDuration() / 1000) {
                            reachedEnd(TRIGGER_END_BY_RECORDED_SCRUB_TO_END);
                        }
                    }, Timber::e);

            // hide the chat box in record stream
            etComment.setVisibility(View.INVISIBLE);
        } else {
            if (checkCurrentInfoNotNull()) {
                mCompositeSubscription.add(mService.checkMutedUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), streamSlug, mAppOwnerProfile.getUserId())
                        .filter(booleanBaseResponse -> booleanBaseResponse != null && isFragmentUIActive())
                        .filter(BaseResponse::getData)
                        .subscribe(dataResponse -> mIsMuted = dataResponse.getData(),
                                this::handleRxError));
            }
        }
    }

    private void storeCurrentStreamToPrefs(String userId) {
        if (getContext() != null) {
            AppsterUtility.saveSharedSetting(getContext(), "current_viewing_stream", userId);
        }
    }


    //region stream view listerners
    private void setupViewerObservables() {
        giftDialogVisibilityListener();
//        playerBufferListener();
        watchersJoinLeaveListener();
        botJoinListener();
        chatBoxListener();

        luckyWheelVisibilityListener();
        if (!isRecorded) callSubViewListener();

        Observable<Object> debounceDailyTopFansObservable = mGetDailyTopFansListObservable.debounce(3, TimeUnit.SECONDS);
        mCompositeSubscription.add(debounceDailyTopFansObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> getDailyTopFansList(), this::handleRxError));
    }

    private void callSubViewListener() {
        mCompositeSubscription.add(callSubViewObservable.subscribe(isCalling -> {
            Timber.e("callSubViewListener %s", isCalling);
            mIsCalling.set(isCalling);
            if (isCalling) {
                //stop player and hide
                muteMedia();
                if (mMediaPlayer != null) mMediaPlayer.stop();
                if (subCameraContainer.getVisibility() != View.VISIBLE) {
//                    mSubCameraView.onResume();
                    subCameraContainer.setVisibility(View.VISIBLE);
                }
                if (playerContainer != null) playerContainer.setVisibility(View.GONE);
                if (vsSubScreen.getParent() != null && mSubStreamLayout != null) {
                    vsSubScreen.setVisibility(View.VISIBLE);
                }
                if (vsHostSubScreen.getParent() != null && mHostSubStreamLayout != null) {
                    vsHostSubScreen.setVisibility(View.VISIBLE);
                }
                if (getActivity() != null)
                    getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

                mStickyPadFrameLayout.setVisibility(View.INVISIBLE);
            } else {
//                unMuteMedia();
                if (subCameraContainer.getVisibility() == View.VISIBLE) {
//                    mSubCameraView.onPause();
                    subCameraContainer.setVisibility(View.GONE);
                }
                if (playerContainer != null) playerContainer.setVisibility(View.VISIBLE);
//                unMuteMedia();
                mIsWaitingReconnect.set(true);
                reloadStream();
                if (vsSubScreen.getParent() != null && mSubStreamLayout != null) {
                    vsSubScreen.setVisibility(View.GONE);
                }
                if (vsHostSubScreen.getParent() != null && mHostSubStreamLayout != null) {
                    vsHostSubScreen.setVisibility(View.GONE);
                }
                if (getActivity() != null)
                    getActivity().setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

                mStickyPadFrameLayout.setVisibility(View.VISIBLE);
            }

        }));
    }

    private void luckyWheelVisibilityListener() {
        mCompositeSubscription.add(luckywheelVisibilityListener
                .subscribe(isShow -> {
                    if (isShow) {
                        vsLuckywheel.setVisibility(View.VISIBLE);
                        changePlayerSize(Utils.dpToPx(120), Utils.dpToPx(160));
                    } else {
                        vsLuckywheel.setVisibility(View.GONE);
//                        userHotImage.setVisibility(View.GONE);
                        changePlayerSize(-1, -1);
                    }
                }, error -> Timber.e(error.getMessage())));
    }

    private void chatBoxListener() {
        //
        //buffer messages will be displayed onto chat_list_box
//        Observable<ChatItemModelClass> debouncedChatItemEmitter = chatItemModelObservable.debounce(200, TimeUnit.MILLISECONDS);
        Observable<List<ChatItemModelClass>> deboundChatItemBufferEmitter = chatItemModelObservable.buffer(BUFFER_DELAYED_TIME, TimeUnit.SECONDS);
        mCompositeSubscription.add(deboundChatItemBufferEmitter
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(chatItemModelClasses -> !chatItemModelClasses.isEmpty())
                .subscribe(this::updateBufferedMessageInList, Timber::e));


        //
        //buffer hearts which will be showed
        final int MaxShowedHeartInPeriodTime = 8;
//        Observable<Object> debouncedShowHeartEmitter = showHeartObservable.debounce(200, TimeUnit.MILLISECONDS);
        Observable<List<Object>> deboundShowHeartEmitter = showHeartObservable.buffer(BUFFER_DELAYED_TIME, TimeUnit.SECONDS);
        mCompositeSubscription.add(deboundShowHeartEmitter
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(objects -> Observable.from(objects).take(MaxShowedHeartInPeriodTime))
                .subscribe(object -> showHearts(), this::handleRxError));
    }

    private void watchersJoinLeaveListener() {
//        Observable<UserJoinLeaveEvent> debouncedWatcherAddEmitter = watcherAddEventObservable.debounce(BUFFER_DELAYED_TIME, TimeUnit.SECONDS);
        Observable<List<UserJoinLeaveEvent>> debouncedWatcherAddBufferEmitter = watcherAddEventObservable.publish(joinEvents -> joinEvents.buffer(joinEvents.debounce(BUFFER_DELAYED_TIME, TimeUnit.SECONDS)));
        mCompositeSubscription.add(debouncedWatcherAddBufferEmitter
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(liveStreamWatcherModel -> isFragmentUIActive())
                .subscribe(newWatchers -> {
                    transformJointEventToMessage(newWatchers);
                    if (!mIsTriviaShow)
                        listUserAdapter.updateList(hlvCustomList, mChatManager.getArrayCurrentUserInStream());
                    updateStreamViewCount();
                }, Timber::e));
    }

    private void botJoinListener() {
        mCompositeSubscription.add(mBotJoinObservable.distinct()
                .subscribe(this::handleBotListMessage));
    }

    private boolean isBotListMessage(ChatItemModelClass item) {
        return ChatItemModelClass.INIT_BOT_LIST.equals(item.getUserName());
    }

    private void giftDialogVisibilityListener() {
        mCompositeSubscription.add(giftDialogListenObservable
                .subscribe(isVisible -> {
                    int translation = 0;
                    if (hasNavBar(getResources())) {
                        translation = getNavBarHeight(getResources());
                    }
                    if (isVisible) {
                        translation += Utils.dpToPx(35);
                        llGiftGroup.animate().translationY(-translation).start();
                    } else {
                        llGiftGroup.animate().translationY(-translation).start();
                    }
                }, error -> Timber.e(error.getMessage())));
    }

    //endregion stream view listerners

    void addMessageToList(Integer integer, final boolean isScrub) {
        Timber.d("addMessageToList isScrub %s", String.valueOf(isScrub));
        if (mRecordedUserMessage.isEmpty()) return;
        mCompositeSubscription.add(Observable.concat(Observable.just(mRecordedUserMessage), Observable.just(mViewerChatMessages))
                .filter(recordedUserModels -> recordedUserModels != null)
                .flatMap(Observable::from)
                .filter(recordedMessagesModel -> {
                    if (RecordedMessagesModel.TYPE_STREAM_TITLE_STICKER == recordedMessagesModel.getActionType()) {
                        return false;
                    }
                    if (isScrub) {
                        return recordedMessagesModel.getRecordedTime() <= integer;
                    } else {
                        return recordedMessagesModel.getRecordedTime() == integer;
                    }
                })
                .takeLast(30)
                .toSortedList((recordedMessagesModel, recordedMessagesModel2) -> Integer.compare(recordedMessagesModel.getRecordedTime(), recordedMessagesModel2.getRecordedTime()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recordedUserModels -> {
                    List<ChatItemModelClass> itemModelClassArrayList = new ArrayList<>();
                    for (RecordedMessagesModel model : recordedUserModels) {
                        ChatItemModelClass modelClass = new ChatItemModelClass();
                        modelClass.setChatDisplayName(model.getDisplayName());
                        modelClass.setLiked(model.getActionType() == RecordedMessagesModel.TYPE_LIKE);
                        modelClass.setMsg(model.getMessage());
                        modelClass.setUserName(model.getUserName());
                        modelClass.setProfilePic(model.getProfilePic());
                        modelClass.rank = model.rank;
                        switch (model.getActionType()) {
                            case RecordedMessagesModel.TYPE_GIFT:
                                modelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_GIFT);
                                modelClass.setGiftImage(model.getGiftImage());
                                modelClass.setGiftCombo(model.getGiftComboQuantity());
                                break;
                            case RecordedMessagesModel.TYPE_JOIN:
                                modelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_USER_JOIN_LIST);
                                break;
                            case RecordedMessagesModel.TYPE_LIKE:
                                break;
                            case RecordedMessagesModel.TYPE_MESSAGE:
                                modelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_MESSAGE);
                                break;
                            case RecordedMessagesModel.TYPE_STREAM_TITLE_STICKER:
                                break;
                            case RecordedMessagesModel.TYPE_FOLLOW:
                                modelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_FOLLOW);
                                break;
                            case RecordedMessagesModel.TYPE_SHARE:
                                modelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_SHARE_STREAM);
                                break;
                            case RecordedMessagesModel.TYPE_FOLLOW_HOST_SUGGESTION:
                                modelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_FOLLOW_HOST_SUGGESTION);
                                break;
                            case RecordedMessagesModel.TYPE_LIVE_COMMERCE_ANNOUNCEMENT:
                                modelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_LIVE_COMMERCE_ANNOUNCEMENT);
                                break;
                        }

                        if (!StringUtil.isNullOrEmptyString(model.getProfileColor())) {
                            modelClass.setProfileColor(model.getProfileColor());
                        }
                        if (isScrub) {

                            itemModelClassArrayList.add(modelClass);
                        } else {
//                            if(modelClass.isLiked()) {
//                                sendRandomLikeHeart(1);
//                            }
                            updateMessageInList(modelClass);
                        }
                    }
                    if (isScrub) {
                        updateRecordedBufferedMessageInList(itemModelClassArrayList);
                    }

//                    return itemModelClassArrayList;
////                    if(chatItemList.isEmpty()) updateRecordedBufferedMessageInList(new ArrayList<>());
//                    if (chatItemList.size() == 1 && !isScrub) {
//                        updateMessageInList(chatItemList.get(0));
//                    } else if (chatItemList.size() > 1 && !isScrub){
//                        updateBufferedMessageInList(chatItemList);
//                    }else{
//                        updateRecordedBufferedMessageInList(chatItemList);
//                    }
                }, this::handleRxError));
    }

    private void filterShowStreamTitle(Integer integer) {
        mCompositeSubscription.add(Observable.from(mRecordedUserMessage)
                .filter(recordedMessagesModel ->
                        RecordedMessagesModel.TYPE_STREAM_TITLE_STICKER == recordedMessagesModel.getActionType() && recordedMessagesModel.getRecordedTime() <= integer)
                .defaultIfEmpty(null)
                .last()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recordedMessagesModel -> {
                    if (recordedMessagesModel != null) {
                        mStickyPadFrameLayout.onLiveTitleReceived(recordedMessagesModel.streamTitleSticker);
                        mStickyPadFrameLayout.setVisibility(View.VISIBLE);
                        Timber.d("stream title shown at %d", recordedMessagesModel.getRecordedTime());
                    } else {
                        if (currentStreamDetails != null)
                            mStickyPadFrameLayout.setVisibility(View.INVISIBLE);
                    }
                }, this::handleRxError));
    }


    public void stopCurrentPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    private void showHearts() {
        if (periscope != null) periscope.addHeart();
    }

    void handleRxError(Throwable e) {
        if (e.getMessage() != null) {
            Timber.e(e);
        }
    }

    private void bindEvents() {
        bindEventTimeLineVideo();

    }

    private void bindEventTimeLineVideo() {
        if (!isRecorded) {
            return;
        }

        fragmentRootView.setOnKeyListener((v, keyCode, event) -> !(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE
                || keyCode == KeyEvent.KEYCODE_MENU) && mediaController.dispatchKeyEvent(event));

        viewTimeLine.setVisibility(View.VISIBLE);
        mediaController = new VideoControllerView(getActivity().getApplicationContext());
        mediaController.setAnchorView(viewTimeLine);

    }


    // Implement the MediaController.MediaPlayerControl interface
    VideoControllerView.MediaPlayerControl mediaPlayerControl = new VideoControllerView.MediaPlayerControl() {

        private int duration;

        @Override
        public void start() {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
                duration = (int) mMediaPlayer.getDuration();
            }
        }

        @Override
        public void pause() {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
            }
        }

        @Override
        public int getDuration() {
            if (mMediaPlayer != null) {
                return (int) mMediaPlayer.getDuration();
            }
            return 0;
        }

        @Override
        public int getCurrentPosition() {
            if (mMediaPlayer != null) {
                return (int) mMediaPlayer.getCurrentPosition();
            }
            return 0;
        }

        @Override
        public void seekTo(int pos) {
            if (mMediaPlayer != null) {
                Timber.e("seek= %d, mFollowHostButtonAnimationDuration=%d", pos, mMediaPlayer.getDuration());
                if (pos == mMediaPlayer.getDuration()) {
                    reachedEnd(TRIGGER_END_BY_RECORDED_SCRUB_TO_END);
                } else {
                    mMediaPlayer.seekTo((long) pos, true);
                }

//                if (pos == duration) {
//                    reachedEnd(TRIGGER_END_BY_RECORDED_SCRUB_TO_END);
//                } else {
//                    mMediaPlayer.seekTo((long) pos, true);
//                }
            }
        }

        @Override
        public boolean isPlaying() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                return true;
            }

            return false;
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return true;
        }

        @Override
        public boolean canSeekForward() {
            return true;
        }

        @Override
        public boolean isFullScreen() {
            return false;
        }

        @Override
        public void toggleFullScreen() {

        }
    };

    //region on screen button click

//    @OnClick({R.id.player_surface_frame, R.id.periscope, R.id.commentsList})
//    public void onPlayerViewClicked(View v) {
////        Utils.hideSoftKeyboard(getActivity());
//        if (!isRecorded) {
//            if (v.getId() != R.id.commentsList) handleShowHideView();
//        } else {
//
//            if (mediaController != null) {
//                if (mediaController.isShowing()) {
//                    mediaController.hide();
//                } else {
//                    mediaController.show(5000);
//                }
//            }
//        }
//    }

//    @OnClick(R.id.periscope)
//    public void onPerisscopeClicked() {
//        if (!isRecorded) {
//            handleShowHideView();
//        }else{
//            mMediaPlayer.performClick();
//        }
//    }

//    @OnClick(R.id.subCameraPreview)
//    public void subStreamClicked(){
//        if(mSubStreamLayout!=null) mSubStreamLayout.onSubCameraClicked();
//    }

    public void closeStreamAndNotRefreshHome() {
        if (mIsTutorialShowing && mEndStreamLayout == null) return;
        if (getActivity() != null) Utils.hideSoftKeyboard(getActivity());
        mIntervalSubject.onNext(-1L);
        setDatainish();
        releaseBotUsers();
        mediaPlayerControl = null;
        if (getActivity() != null) getActivity().finish();
    }

    @OnTouch({R.id.commentsList})
    public boolean onPeriscopeTouched() {
        if (fragmentRootView != null) {
            hideKeyBoardIfAny();
            if (etComment != null && etComment.isFocused()) {
                etComment.clearFocus();
                Utils.hideSoftKeyboard(getActivity());
            }
        }
        return false;
    }

    private void hideKeyBoardIfAny() {
        mInputMethodManager.hideSoftInputFromWindow(fragmentRootView.getWindowToken(), 0);
    }


    @OnClick(R.id.ibCloseStream)
    public void closeStream() {
        if (mIsTriviaShowRunning && mEndStreamLayout == null) {
            //there is no end stream screen
            if (getContext() != null) {
                new TriviaDialog(getContext())
                        .setTitleText(isVNTrivia() ? getContext().getString(R.string.leave_the_game_text_vi) : getContext().getString(R.string.leave_the_game_text))
                        .setContentText(isVNTrivia() ? getString(R.string.trivia_game_leave_confirm_text_vi) : getString(R.string.trivia_game_leave_confirm_text))
                        .setCancelText(isVNTrivia() ? getString(R.string.leave_text_vi) : getString(R.string.leave_text))
                        .setConfirmText(isVNTrivia() ? getString(R.string.stay_text_vi) : getString(R.string.stay_text))
                        .showCancelButton(true)
                        .setCancelClickListener(alertDialog -> {
                            closeStream(false);
                            alertDialog.dismissWithAnimation();
                        }).show();
            }
        } else {
            closeStream(false);
        }
    }

    private void closeStream(boolean forcedClose) {
        if (mIsTutorialShowing && !forcedClose) return;
        if (getActivity() != null) Utils.hideSoftKeyboard(getActivity());
        if (forcedClose && mSubStreamLayout != null) {
            mSubStreamLayout.stopVideoCall();
        } else if (mSubStreamLayout != null && !mSubStreamLayout.isAbleToLeaveStream()) {
            return;
        }
        mIntervalSubject.onNext(-1L);
        if (etComment != null && etComment.isFocused()) etComment.clearFocus();
        showFullscreen();
        setDatainish();
        releaseBotUsers();
        mediaPlayerControl = null;
        if (!isRecorded) {//make sure this is live stream
            Intent resultIntent = new Intent();
            resultIntent.putExtra(ConstantBundleKey.BUNDLE_VIEWER_CLOSE_STREAM, true);
            if (getActivity() != null) getActivity().setResult(RESULT_OK, resultIntent);
        }
        if (getActivity() != null) getActivity().finish();
    }

    @OnClick({R.id.fragment_root_view, R.id.periscope, R.id.commentsList})
    public void onScreenClicked(View v) {
        if (etComment.isFocused()) {
            etComment.clearFocus();
            Utils.hideSoftKeyboard(getActivity());
        } else {
            Utils.hideSoftKeyboard(getActivity());
            if (!isRecorded) {
                if (v != null && v.getId() != R.id.commentsList) handleShowHideView();
            } else {

                if (mediaController != null) {
                    if (mediaController.isShowing()) {
                        mediaController.hide();
                    } else {
                        mediaController.show(5000);
                    }
                }
            }
        }
    }


    @OnClick(R.id.txt_stars)
    public void showTopFans() {
        if (checkPublisherNotNull()) {
            UserModel userModel = new UserModel();
            userModel.setUserId(currentStreamDetails.getPublisher().getUserId());
            topFanDialog = TopFanDialog.newInstance(getContext(), userModel, true, true);
            topFanDialog.setUserProfileActionListener(this);
            topFanDialog.setOnDismissListener(() -> {
                if (getActivity() != null && getActivity().getWindow() != null) {
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(getFullScreenVisibilityFlags());
                }
            });
            topFanDialog.show();
        }
    }

    @OnClick(R.id.iBtnHostFollow)
    public void onHostFollowButton() {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            startActivityLogin();
        } else {
            SetFollowUserRequestModel request = new SetFollowUserRequestModel();
            request.setFollow_user_id(mStreamerId);
            mCompositeSubscription.add(AppsterWebServices.get().setFollowUser(AppsterUtility.getAuth(), request)
                    .compose(AppsterApplication.get(getContext()).applySchedulers())
                    .subscribe(setFollowUserResponseModel -> {
                        if (setFollowUserResponseModel != null && setFollowUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            updateFollowHostButton(true);
                            mChatGroupAdapter.removeFollowHostSuggestionItem();
                            if (isRecorded) removeHostFollowSuggestionOnRecord();
                            AppsterApplication.mAppPreferences.getUserModel().setFollowingCount(setFollowUserResponseModel.getData().getFollowingCount());
                            if (currentStreamDetails != null && currentStreamDetails.getPublisher() != null) {
                                currentStreamDetails.getPublisher().setIsFollow(Constants.IS_FOLLOWING_USER);
                            }
                        }

//                        else {
//                            Toast.makeText(getContext(), getString(R.string.some_error_happen_please_try), Toast.LENGTH_SHORT).show();
//                        }
                    }, error -> {
                        Toast.makeText(getContext(), getString(R.string.some_error_happen_please_try), LENGTH_SHORT).show();
                        Timber.e(error);
                    }));
        }
    }

    @OnClick(R.id.ibShareStream)
    void onIbShareStreamClick() {
        showChooseShareType();
    }


    @OnClick(R.id.ibGiftStore)
    void onIbGiftStoreClick() {
        if (mIsTriviaShow && !mIsAbleToSendGift) return;
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            startActivityLogin();
        } else {
            if (getActivity().isFinishing() || currentStreamDetails == null || currentStreamDetails.getPublisher() == null || currentStreamDetails.getPublisher().getUserName() == null) {
                return;
            }
            if (!checkCurrentInfoNotNull()) return;

            Timber.e("onIbGiftStoreClick");
            giftDialogListenObservable.onNext(true);

            if (!currentStreamDetails.getPublisher().getUserId().equals(mAppOwnerProfile.getUserId())) {
                sendGift = new DialogSendGift(getActivity(),
                        currentStreamDetails.getPublisher().getUserId(), currentStreamDetails.getStreamId());
                sendGift.setOnDismissListener(() -> {
                    if (getActivity() != null && getActivity().getWindow() != null)
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(getFullScreenVisibilityFlags());
                    giftDialogListenObservable.onNext(false);
                    llGiftGroup.animate().translationY(0).start();
                });
                sendGift.setCompleteSendGift((ItemSend, senderTotalBean, senderTotalGold, receiverTotalBean, receiverTotalGoldFans, votingScores, rankingList, dailyTopFans) -> {
                    if (isFragmentUIActive()) {
                        updateTopFansList(rankingList);
                        if (mGiftRankingGroupView != null && !isRecorded)
                            mGiftRankingGroupView.updateRankingList(mIsTriviaShow ? Collections.EMPTY_LIST : dailyTopFans);
                        ChatItemModelClass itemModelClass = new ChatItemModelClass();
                        itemModelClass.setChatDisplayName(mAppOwnerProfile.getDisplayName());
                        itemModelClass.setUserName(mAppOwnerProfile.getUserName());
                        itemModelClass.setUserIdSend(mAppOwnerProfile.getUserId());
                        itemModelClass.setProfilePic(mAppOwnerProfile.getUserImage());
                        itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_GIFT);
                        itemModelClass.setGiftImage(ItemSend.getGiftImage());
                        itemModelClass.setGiftId(ItemSend.getGiftId());
                        itemModelClass.setGiftName(ItemSend.getGiftName());
                        itemModelClass.setReceiverStars(String.valueOf(receiverTotalGoldFans));
                        itemModelClass.setVotingScores(votingScores);
                        itemModelClass.topFanList = rankingList;
                        itemModelClass.rank = rankingList.indexOf(mAppOwnerProfile.getUserName());
                        itemModelClass.dailyTopFansList = dailyTopFans;
                        String messageGift = String.format(getString(R.string.just_send_gift), ItemSend.getGiftName());
                        boolean isExpensive = ExpensiveGift.checkExpensiveGiftByMessage(itemModelClass.getGiftId());
                        itemModelClass.setMsg(messageGift);
                        itemModelClass.giftColor = ItemSend.getGiftColor();
//                        itemModelClass.setIsExpensive(isExpensive);

                        if (isExpensive) {
                            if (sendGift != null) sendGift.dimissDialog();
//                            rlExpensiveGift.addGift(itemModelClass);
                            showExpensiveGift(itemModelClass);
                            updateMessageInList(itemModelClass);
                        } else {
                            llGiftGroup.addGift(itemModelClass);
                            llGiftGroup.setGiftComboGroupViewListener(item -> {
                                topfanFinalCheck(item);
                                Timber.e(item.toString());
                                updateMessageInList(item);
                            });
                        }
                        countGiftCount = countGiftCount + 1;
                        updateStreamViewCount();
                        sendToGroup(itemModelClass);

                        updateTotalStars(receiverTotalGoldFans);

                        Timber.e("rankingList=" + rankingList);
                        Timber.e("itemModelClass.rank=" + itemModelClass.rank);
                    }
                });
                sendGift.show();
            } else {
                sendGift = new DialogSendGift(getActivity(), mAppOwnerProfile.getUserId(), true);
                sendGift.setOnDismissListener(() -> {
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(getFullScreenVisibilityFlags());
                    giftDialogListenObservable.onNext(false);
                    llGiftGroup.animate().translationY(0).start();
                });
                sendGift.show(true);
            }
        }
    }


    @OnEditorAction(R.id.etComment)
    public boolean completeMessage(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            ibSendComment.performClick();
        }

        return false;
    }

    @OnClick(R.id.ibSendHeart)
    void onIbSendHeartClick() {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            startActivityLogin();
        } else {
            periscope.addHeart();
            if (mIsTriviaShow && currentStreamDetails.isLike()) return;
            if (!checkCurrentInfoNotNull()) return;
            final ChatItemModelClass itemModelClass = new ChatItemModelClass();
            itemModelClass.setChatDisplayName(mAppOwnerProfile.getDisplayName());
            itemModelClass.setUserName(mAppOwnerProfile.getUserName());
            itemModelClass.setUserIdSend(mAppOwnerProfile.getUserId());
            itemModelClass.setProfilePic(mAppOwnerProfile.getUserImage());
            itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_LIKE);

            String messageGift = getString(R.string.like_this);
            itemModelClass.setMsg(messageGift);

            sendToGroup(itemModelClass);
            if (currentStreamDetails != null && !currentStreamDetails.isLike()) {
                mCompositeSubscription.add(mService.likeStream(AppsterApplication.mAppPreferences.getUserTokenRequest(), new LikeStreamRequestModel(currentStreamDetails.getSlug(), true))
                        .subscribe(likePostDataResponse -> {
                            if (!isFragmentUIActive()) return;
                            if (likePostDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                countLikeCount = likePostDataResponse.getData().getLikeCount();
                                currentStreamDetails.setLike(1);//set that stream have been liked
                                itemModelClass.setTotalLikes(countLikeCount);
                                sendLikeMessage(itemModelClass);
                                updateStreamViewCount();
                                createLikeEvent(countLikeCount);
                            }
                        }, error -> Timber.e(error.getMessage())));
            }
        }
    }


    @OnFocusChange(R.id.etComment)
    public void onFocus(boolean hasFocus) {

        Timber.e("comment focus %s", hasFocus);
        if (hasFocus) {
            llActions.setVisibility(View.GONE);
            ibSendComment.setVisibility(View.VISIBLE);
            llStreamingToppanel.setVisibility(View.INVISIBLE);
        } else {
            if (fragmentRootView != null) {
                llStreamingToppanel.setVisibility(View.VISIBLE);
                llActions.setVisibility(View.VISIBLE);
                ibSendComment.setVisibility(View.GONE);
                hideKeyBoardIfAny();
                showFullscreen();
            }
        }
    }


    @OnClick(R.id.ibSendComment)
    public void sendChatMessage() {

        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            startActivityLogin();
        } else {
            if (mIsMuted) {
                Toast.makeText(getActivity().getApplicationContext(), "You have been muted for this stream!", LENGTH_SHORT).show();
                return;
            }
            if (wordCountMap == null) wordCountMap = new HashMap<>();
            String message = etComment.getText().toString();
            String messageCompare = message.toLowerCase().trim();
            Integer count = wordCountMap.get(messageCompare);
            if (count == null) {
                wordCountMap.clear();
                wordCountMap.put(messageCompare, 1);
            } else {
                if (count == MAX_DUPLICATE_MESSAGE_COUNT) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity().getApplicationContext(), "Spam detected", LENGTH_SHORT).show();
                        etComment.setText(null);
                    }
                    return;
                } else {
                    wordCountMap.put(messageCompare, count + 1);
                }
            }

            String comment = removeNaughtyWords(message).replaceAll("\\*", "");
            if (comment.equals("") || comment.trim().equalsIgnoreCase("")) return;
            etComment.setText(null);


            if (!checkCurrentInfoNotNull()) return;

            ChatItemModelClass itemModelClass = new ChatItemModelClass();
            itemModelClass.rank = mCurrentFanRanking.get();
            itemModelClass.setChatDisplayName(mAppOwnerProfile.getDisplayName());
            itemModelClass.setUserName(mAppOwnerProfile.getUserName());
            itemModelClass.setUserIdSend(mAppOwnerProfile.getUserId());
            itemModelClass.setProfilePic(mAppOwnerProfile.getUserImage());
            itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_MESSAGE);
            itemModelClass.setMsg(comment);
            if (!StringUtil.isNullOrEmptyString(AppsterApplication.mAppPreferences.getUserModel().getColor())) {
                itemModelClass.setProfileColor(AppsterApplication.mAppPreferences.getUserModel().getColor());
            }
            if (isRecorded) {
                tempStoreViewerChatMessage(itemModelClass);
            }
            updateMessageInList(itemModelClass);

            sendToGroup(itemModelClass);
        }
    }

    void sendToGroup(ChatItemModelClass itemModelClass) {
        try {
            itemModelClass.rank = mCurrentFanRanking.get();
            if(mIsTriviaShow){
                if(mAgoraChatManager!=null) mAgoraChatManager.sendGroupMessage(streamSlug,itemModelClass);
            }else {
                if (mChatManager != null) mChatManager.sendGroupMessage(itemModelClass);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    //endregion


    //region user dialog actions
    @Override
    public void onReportUserClick(String userId) {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            startActivityLogin();
            return;
        }

        DialogReport dialogReport = DialogReport.newInstance();
        dialogReport.setChooseReportListenner(reason -> onReportUser(reason, userId));
        dialogReport.show(getChildFragmentManager(), "Report");
    }

    @Override
    public void onBlockUserClick(String userId, String displayName) {
        //do nothing on viewer side
    }

    @Override
    public void onMuteUserClick(String userId, String displayName) {
        //do nothing on viewer side
    }

    @Override
    public void onUnMuteUserClick(String userId, String displayName) {
        //do nothing on viewer side
    }


    @Override
    public void onFollowCountChanged(int count) {
        //do nothing on viewer side
    }

    @Override
    public void onDimissed() {
        showFullscreen();
    }

    @Override
    public void onChangeFollowStatus(String userId, int status) {
        if (isMatchedPublisherId(userId) && checkCurrentInfoNotNull()) {
            updateFollowHostButton(status == Constants.IS_FOLLOWING_USER);
            if (currentStreamDetails != null && currentStreamDetails.getPublisher() != null) {
                currentStreamDetails.getPublisher().setIsFollow(status);
            }
        }
    }

    @Override
    public void onVideoCallClicked(String userId, String userName) {
        // do nothing on viewers side
    }

    private boolean checkCurrentInfoNotNull() {
        return mAppOwnerProfile != null;
    }

    private boolean isMatchedPublisherId(String userId) {
        return checkPublisherNotNull() && !TextUtils.isEmpty(userId) && currentStreamDetails.getPublisher().getUserId().equalsIgnoreCase(userId);
    }

    private boolean checkPublisherNotNull() {
        return currentStreamDetails != null && currentStreamDetails.getPublisher() != null;
    }

    private void sendFollowMessage(String displayName) {

        if (mIsFollowThisUser) return;

        mIsFollowThisUser = true;
        final ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(mAppOwnerProfile.getDisplayName());
        itemModelClass.setUserName(mAppOwnerProfile.getUserName());
        itemModelClass.setUserIdSend(mAppOwnerProfile.getUserId());
        itemModelClass.setProfilePic(mAppOwnerProfile.getUserImage());
        itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_FOLLOW);
        itemModelClass.setMsg(String.format(getString(R.string.message_followed), displayName));
        itemModelClass.rank = mCurrentFanRanking.get();
        try {
            updateMessageInList(itemModelClass);
            sendToGroup(itemModelClass);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void onReportUser(String reason, String userId) {
        ReportUserRequestModel request = new ReportUserRequestModel();
        request.setReportedUserId(userId);
        request.setReason(reason);

        mCompositeSubscription.add(mService.reportUser(AppsterUtility.getAuth(), request)
                .subscribe(blockOrReportUserDataResponse -> Toast.makeText(getActivity().getApplicationContext(), getString(R.string.report_toast_message), LENGTH_SHORT).show()
                        , error -> ((BaseActivity) getActivity()).handleError(error.getMessage(), Constants.RETROFIT_ERROR)));
    }
    //endregion

    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            if (fragmentRootView != null) {
                fragmentRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = fragmentRootView.getRootView().getHeight();
                int keyboardHeight = screenHeight - (r.bottom);

                // IF height diff is more then 150, consider keyboard as visible.
                if (keyboardHeight > 150) {
                    AppsterApplication.mAppPreferences.setIntPreferenceData(Constants.KEYBOARD_HEIGHT, keyboardHeight);
                    fragmentRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        }
    };

    private void calcKeyboardHeight() {
        fragmentRootView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    private void handleShowHideView() {
//        if (isCommentsLayoutShowing) {
        Utils.hideSoftKeyboard(getActivity());
//            isCommentsLayoutShowing = false;
//            return;
//        }
        if (!isHideAllView) {
            if (shouldShowTutorial()) return;
            hideAllView();
            isHideAllView = true;
        } else {
            showAllView();
            isHideAllView = false;
        }
    }


    private boolean isRecordHasEnded() {

        if (mMediaPlayer == null) {
            return false;
        }

        float duration = mMediaPlayer.getDuration();
        if (duration == 0) {
            return false;
        }

        Timber.e("isRecordHasEnded - StreamPosition=%d, StreamDuration=%d", mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());

        float remain = duration - mMediaPlayer.getCurrentPosition();
        return remain <= 100;
    }

    private boolean isDisconnectedPlayer() {
        return mMediaPlayer == null;
    }


    private void hideAllView() {
        llStreamingToppanel.setVisibility(View.INVISIBLE);
        llBottomContainer.setVisibility(View.GONE);
        mStickyPadFrameLayout.setVisibility(View.INVISIBLE);
        mBtnLiveShop.setVisibility(View.INVISIBLE);
        loUserPoint.setVisibility(View.INVISIBLE);
        periscope.setVisibility(View.GONE);
        if (mIsTriviaShow) clTriviaExtraActionsContainer.setVisibility(View.GONE);
    }

    public void showAllView() {
        llStreamingToppanel.setVisibility(View.VISIBLE);
        llBottomContainer.setVisibility(View.VISIBLE);
        if (!mIsCalling.get()) mStickyPadFrameLayout.setVisibility(View.VISIBLE);
        if (mIsPublisherSeller) mBtnLiveShop.setVisibility(View.VISIBLE);
        loUserPoint.setVisibility(View.VISIBLE);
        periscope.setVisibility(View.VISIBLE);
        if (mIsTriviaShow) clTriviaExtraActionsContainer.setVisibility(View.VISIBLE);
    }

    DialogSendGift sendGift;


    void createLikeEvent(int countLikeCount) {
        evenlike = new NewLikeEventModel();
        evenlike.setStream(true);
        evenlike.setSlug(streamSlug);
        evenlike.setLikeCount(countLikeCount);
    }

    void sendLikeMessage(ChatItemModelClass message) {
        try {
            message.rank = mCurrentFanRanking.get();
            message.setLiked(true);
            message.setMsg("I sent ");
            if(mIsTriviaShow){
                if(mAgoraChatManager!=null) mAgoraChatManager.sendGroupMessage(streamSlug,message);
            }else {
                if (mChatManager != null) mChatManager.sendGroupMessage(message);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void tempStoreViewerChatMessage(ChatItemModelClass itemModelClass) {
        tempStoreViewerChatMessage(itemModelClass, 0);
    }

    private void tempStoreViewerChatMessage(ChatItemModelClass itemModelClass, int actionType) {
        if (mViewerChatMessages == null) {
            mViewerChatMessages = new ArrayList<>();
        }

        mViewerChatMessages.add(createSavedMessageItem(itemModelClass, actionType));
    }


    private RecordedMessagesModel createSavedMessageItem(ChatItemModelClass chatItemModelClass, int actionType) {

        RecordedMessagesModel recordedMessagesModel = new RecordedMessagesModel();
        recordedMessagesModel.setActionType(actionType);
        recordedMessagesModel.setMessage(chatItemModelClass.getMsg());

        recordedMessagesModel.setRecordedTime(mCurrentVideoTime);
        recordedMessagesModel.setDisplayName(chatItemModelClass.getChatDisplayName());
        recordedMessagesModel.setUserName(chatItemModelClass.getUserName());
        if (!StringUtil.isNullOrEmptyString(chatItemModelClass.getProfileColor())) {
            recordedMessagesModel.setProfileColor(chatItemModelClass.getProfileColor());
        }
        return recordedMessagesModel;
    }


    @Override
    public void onDestroyView() {
        Timber.e("onDestroyView");
        if (!mHasCalledEndedAPI.get() && !mIsTrackedLeaveTime && mViewSessionId != 0 && !TextUtils.isEmpty(streamSlug)) {
            mCompositeSubscription.add(mService.leaveStream(AppsterUtility.getAuth(), streamSlug, mViewSessionId)
                    .subscribe(booleanBaseResponse -> {
                    }, Timber::e));
        }
        releaseTriviaPlayer();
        releasePlayer();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
        keyboardHeightProvider.close();
        mStreamChatGroupListener = null;
        if (mTriviaOnOffFaceTimer != null) mTriviaOnOffFaceTimer.cancel();
        if (mTriviaGetQuestionCountTime != null) mTriviaGetQuestionCountTime.cancel();
        RxUtils.unsubscribeIfNotNull(mVideoTimeChangedSubscription);
        RxUtils.unsubscribeIfNotNull(readRecoredMessageSubscription);
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        FileDownloader.getInstance().clearAllDownloadThread();
        fragmentRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
//        if (mSubPlayerLayout != null) mSubPlayerLayout.onDestroy();
        if (mSubStreamLayout != null) mSubStreamLayout.onDestroy();
        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mPhoneCallStateIntentReceiver);
        }
        if (getActivity() != null) {
            AudioManager am = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
            if (am != null) {
                am.abandonAudioFocus(this);
            }
        }
        super.onDestroyView();

        ButterKnife.unbind(this);
        RefWatcher refWatcher = AppsterApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);

    }

    private void startPingingForLiveData() {
        if (currentStreamDetails == null) return;

        if (isRecorded) {
            if (pingTimer != null) {
                pingTimer.cancel();
                pingTimer = null;
            }
            pingTimer = new Timer();
            pingTimer.scheduleAtFixedRate(new StatisticTasks(this), 0, 60000);
        }
    }

    public void pingStreamStatistic() {
        try {

            mCompositeSubscription.add(mService.statisticStream(AppsterApplication.mAppPreferences.getUserTokenRequest(),
                    new BeginStreamRequestModel(streamSlug))
                    .subscribe(statisticStreamResponseModel -> {

                        if (!isFragmentUIActive()) return;
                        switch (statisticStreamResponseModel.getCode()) {
                            case Constants.RESPONSE_FROM_WEB_SERVICE_OK:
                                countGiftCount = statisticStreamResponseModel.getData().getGiftCount();
                                countUserHaveBeenView = statisticStreamResponseModel.getData().getViewCount();
                                countLikeCount = statisticStreamResponseModel.getData().getLikeCount();
//                            countFollower = statisticStreamResponseModel.getData().getFollowerCount();
                                updateStreamViewCount();
                                break;
                            case Constants.RESPONSE_FROM_WEB_SERVICE_404:
                                Timber.e("RESPONSE_FROM_WEB_SERVICE_404");
                                releasePlayer();
                                showStreamHasBeenDeletedDialog(getString(R.string.app_name), getString(R.string.recorded_stream_has_been_deleted));
                                break;
                            case Constants.RESPONSE_FROM_WEB_SERVICE_1404:
                                releasePlayer();
                                showStreamHasBeenDeletedDialog(getString(R.string.mb_not_begun_yet), statisticStreamResponseModel.getMessage());
                                break;
                            case ShowErrorManager.account_deactivated_or_suspended:
                                Timber.e("Account deactive");
                                releasePlayer();
                                if (getContext() != null) {
                                    new DialogbeLiveConfirmation.Builder()
                                            .title(getContext().getString(R.string.app_name))
                                            .singleAction(true)
                                            .message(statisticStreamResponseModel.getMessage())
                                            .onConfirmClicked(() -> AppsterApplication.logout(getContext()))
                                            .build().show(getContext());
                                }
                                break;
                            default:
                                break;
                        }
                    }, Timber::e));
        } catch (Exception e) {
            Log.d("Error in ping", e.getMessage());
        }
    }

    private void pingIntervalManiplulateNetworkText() {
        mCompositeSubscription.add(mIntervalSubject.mergeWith(Observable.interval(5, TimeUnit.SECONDS))
                .takeWhile(time -> time != -1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(checkTime -> mIsCanChangeNetworkText.set(true)
                        , this::handleRxError, () -> Timber.e("pingIntervalManilpulateNetworkText completed")));
    }

    private void pingNetworkDownloadSpeed() {
        previousDataSize = mMediaPlayer.getDownloadDataSize();
        mCompositeSubscription.add(mIntervalSubject.mergeWith(Observable.interval(5, TimeUnit.SECONDS))
                .takeWhile(time -> time != -1)
                .filter(aLong -> !mIsPausing.get())
                .flatMap(aLong -> Observable.fromCallable(this::getBitRate))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(speed -> Timber.e("pingNetworkDownloadSpeed -> %d", speed))
                .subscribe(this::handleViewerNetworkSpeedMessage
                        , this::handleRxError, () -> Timber.e("pingNetworkDownloadSpeed completed")));

    }

    private void handleViewerNetworkSpeedMessage(long speed) {
        handleNetworkSpeedMessage(speed, false);
    }

    private void handleHotNetworkSpeedMessage(int speed) {
        handleNetworkSpeedMessage(speed, true);
    }

    private void handleNetworkSpeedMessage(long speed, boolean isHost) {

        if (mIsCanChangeNetworkText.get()) {
            if (speed <= mStreamBitrate - 50) {
                onShowNotificationNetworkLow(isHost);
            } else {
                onHideNotificationNetworkLow();
            }
        }
    }


    long previousDataSize;

    private long getBitRate() {

        if (mMediaPlayer != null) {
            long currentDataSize = mMediaPlayer.getDownloadDataSize();
            long bitrate = ((currentDataSize - previousDataSize) * 8) / 5;
            previousDataSize = currentDataSize;

            Timber.e("realBitrate+++++++++++++++++++++++++ %s", bitrate);
            return bitrate;
        }

        return 0;

    }


    private PublishSubject<Long> mIntervalSubject = PublishSubject.create();
    AtomicInteger mNetworkTimeoutCount = new AtomicInteger();

    private void pingNetworkUploadSpeed(String mSlug) {

        mCompositeSubscription.add(mIntervalSubject.mergeWith(Observable.interval(10, TimeUnit.SECONDS))
                .takeWhile(time -> time != -1)
                .filter(aLong -> !mIsPausing.get())
                .flatMap(aLong -> getBytesInRate(mSlug))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(networkUploadSpeedModel -> (int) (networkUploadSpeedModel.getBytesInRate() * 8 / 1000))
                .doOnNext(speed -> {
                    Timber.e("pingNetworkUploadSpeed -> %d", speed);
                    if (speed <= 0) {
                        countPingNetworkByZero++;
                    } else {
                        mNetworkTimeoutCount.set(0);
                        countPingNetworkByZero = 0;
                    }
                    if (countPingNetworkByZero >= 2) {
                        showEndStreamNetworkError();
                    }
                })
                .doOnError(error -> {
                    if (error instanceof SocketTimeoutException || error instanceof UnknownHostException) {
                        Timber.e("doOnError %s", error.getMessage());
                        mNetworkTimeoutCount.getAndIncrement();
                        Timber.e("mNetworkTimeoutCount %d", mNetworkTimeoutCount.get());
                        if (mNetworkTimeoutCount.get() == 2) {
                            showEndStreamNetworkError();
                        }
                        if (mIsCanChangeNetworkText.get()) {
                            onShowNotificationNetworkLow(true);
                        }
                    }
                })
                .retryWhen(errors -> errors.zipWith(Observable.range(1, 2), (n, i) -> i)
                        .flatMap(retryCount -> Observable.timer((int) Math.pow(5, retryCount), TimeUnit.SECONDS)))
                .subscribe(this::handleHotNetworkSpeedMessage
                        , this::handleRxError, () -> Timber.e("pingNetworkUploadSpeed completed")));
    }

    private Observable<NetworkUploadSpeedModel> getBytesInRate(String mSlug) {
        return mService.getBytesInRate("application/json", BuildConfig.CHECK_NETWORK_UPLOAD_SPEED + mSlug)
                .filter(networkUploadSpeedModel -> networkUploadSpeedModel != null);

    }

    public void onShowNotificationNetworkLow(boolean isHost) {
        if (tvNetworkSlow == null) return;
        if (tvNetworkSlow.getVisibility() == View.GONE) {
//            if (isHost) {
//                tvNetworkSlow.setText(getString(R.string.hot_network_is_low));
//            } else {
            tvNetworkSlow.setText(getString(R.string.viewer_network_is_low));
//            }

            mIsCanChangeNetworkText.set(false);
            tvNetworkSlow.setVisibility(View.VISIBLE);
            Animation slideLeft = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                    R.anim.noti_network_slide_in_left);
            tvNetworkSlow.startAnimation(slideLeft);

        }
    }

    public void onHideNotificationNetworkLow() {
        if (!isFragmentUIActive()) return;
        if (tvNetworkSlow.getVisibility() == View.VISIBLE) {
            tvNetworkSlow.setVisibility(View.GONE);
        }
    }


    private void getStreamTitleSticker(String streamSlug) {
        mCompositeSubscription.add(mService.getStreamTitleSticker(AppsterUtility.getAuth(), streamSlug)
                .subscribe(streamTitleStickerBaseResponse -> {
                    Gson gson = new GsonBuilder().create();
                    StreamTitleSticker streamTitleSticker = gson.fromJson(streamTitleStickerBaseResponse.getData(), StreamTitleSticker.class);
                    mStickyPadFrameLayout.onLiveTitleReceived(streamTitleSticker);
                }, Timber::e));
    }

    private void setupLiveCommerce(StreamModel streamModel) {
        mIsPublisherSeller = streamModel.getPublisher().isSeller();
        if (mIsPublisherSeller) {
            mBtnLiveShop.setVisibility(View.VISIBLE);
            mBtnLiveShop.setOnClickListener(v -> {
                AppsterUtility.temporaryLockView(v);
                openLiveCommerceShop(streamModel);
            });
        }
    }

    private void openLiveCommerceShop(StreamModel streamModel) {
        StreamPublisherModel publisher = streamModel.getPublisher();
        String sellerId = publisher.getUserId();
        String sellerName = publisher.getDisplayName();
        String liveCommerceAnnouncementMessage = streamModel.liveCommerceAnnouncementMessage;
        if (mLiveShopDialog == null) {
            String auth = "Bearer " + AppsterApplication.mAppPreferences.getUserToken();
            mLiveShopDialog = LiveShopDialog.newInstance(auth, sellerId, sellerName, false, streamModel.liveShopOrderButtonNowLabel);
            mLiveShopDialog.setLiveShopCallback(() -> sendLiveCommerceAnnouncementMessage(liveCommerceAnnouncementMessage));
        }
        mLiveShopDialog.show(getChildFragmentManager(), LiveShopDialog.class.getName());
    }

    private void sendLiveCommerceAnnouncementMessage(String liveCommerceAnnouncementMessage) {
        if (isRecorded || mHasSentLiveCommerceAnnouncementMessage) return;
        ChatItemModelClass item = new ChatItemModelClass();
        item.setUserName(mAppOwnerProfile.getUserName());
        item.setMessageType(ChatItemModelClass.CHAT_TYPE_LIVE_COMMERCE_ANNOUNCEMENT);
        item.setMsg(liveCommerceAnnouncementMessage);
        updateMessageInList(item);
        sendToGroup(item);
        mHasSentLiveCommerceAnnouncementMessage = true;
        if (canShowGoShopButton()) showGoShopButton();
    }

    private boolean canShowGoShopButton() {
        int count = mLiveCommerceAnnouncementCount.incrementAndGet();
        return count > 0 && count % INTERVAL_TIMES_SHOW_GO_SHOP_BUTTON == 0;
    }

    private void showGoShopButton() {
        ChatItemModelClass item = new ChatItemModelClass();
        item.setMessageType(ChatItemModelClass.CHAT_TYPE_LIVE_COMMERCE_SUGGESTION);
        updateMessageInList(item);
    }

    private void setupAudioListener() {
        if (getActivity() == null) {
            return;
        }
        Timber.d("AUDIO FOCUS set up");
        AudioManager am = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
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


    private void getStreamData(String pass) {
        setUpWaitTimeout();
        BeginStreamRequestModel streamRequest = new BeginStreamRequestModel(streamSlug);
        if (!pass.isEmpty()) {
            streamRequest.setPrivatePassword(pass);
        }
        mCompositeSubscription.add(mService.streamDetail(AppsterUtility.getAuth(), streamRequest)
                .doOnNext(streamDetailResponseModel -> {
                    if (!isFragmentUIActive()) return;
                    switch (streamDetailResponseModel.getCode()) {
                        case Constants.RESPONSE_FROM_WEB_SERVICE_404:
                            Timber.e("RESPONSE_FROM_WEB_SERVICE_404");
                            releasePlayer();
                            showStreamHasBeenDeletedDialog(getString(R.string.app_name), getString(R.string.recorded_stream_has_been_deleted));
                            break;
                        case Constants.RESPONSE_FROM_WEB_SERVICE_1404:
                            releasePlayer();
                            showStreamHasBeenDeletedDialog(getString(R.string.mb_not_begun_yet), streamDetailResponseModel.getMessage());
                            break;
                        case ShowErrorManager.account_deactivated_or_suspended:
                            Timber.e("Account deactive");
                            releasePlayer();
                            if (getContext() != null) {
                                new DialogbeLiveConfirmation.Builder()
                                        .title(getContext().getString(R.string.app_name))
                                        .singleAction(true)
                                        .message(streamDetailResponseModel.getMessage())
                                        .onConfirmClicked(() -> AppsterApplication.logout(getContext()))
                                        .build().show(getContext());
                            }
                            break;
                        case ShowErrorManager.userHaveBeenKickedOutStream:
                            Timber.e("User Have Been Kicked Out Stream");
                            releasePlayer();
                            new DialogbeLiveConfirmation.Builder()
                                    .title(getContext().getString(R.string.app_name))
                                    .singleAction(true)
                                    .message(getString(R.string.you_have_been_kicked_out_of_the_stream))
                                    .onConfirmClicked(() -> getActivity().finish())
                                    .build().show(getContext());
                            break;
                        case ShowErrorManager.userHaveBeenBlockedStream:
                            Timber.e("User Have Been Blocked Stream");
                            Toast.makeText(getActivity().getApplicationContext(), streamDetailResponseModel.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            releasePlayer();
                            if (getActivity() != null) getActivity().finish();
                            break;
                        case ShowErrorManager.stream_pass_word_required:
                            Timber.e("Stream Require Password");
                            mIsWaitingForPassword = true;
                            new DialogbeLiveConfirmation.Builder()
                                    .title(getContext().getString(R.string.enter_password))
                                    .setPasswordBox(true)
                                    .confirmText(getContext().getString(R.string.verify))
                                    .onEditTextValue(value -> getStreamData(value))
                                    .onCancelClicked(() -> {
                                        releasePlayer();
                                        if (getActivity() != null) getActivity().finish();
                                    })
                                    .build().show(getContext());
                            break;
                        case ShowErrorManager.stream_pass_incorrect:
                            new DialogbeLiveConfirmation.Builder()
                                    .title(getContext().getString(R.string.app_name))
                                    .singleAction(true)
                                    .message(streamDetailResponseModel.getMessage())
                                    .onConfirmClicked(() -> getActivity().finish())
                                    .build().show(getContext());
                            break;
                        default:
                            break;
                    }
                })
                .filter(streamModelBaseResponse -> streamModelBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK
                        && isFragmentUIActive())
                .flatMap(streamModelBaseResponse -> {
                    final StreamModel streamData = streamModelBaseResponse.getData();
                    GetTopFanModel getTopFanModel = new GetTopFanModel();
                    getTopFanModel.setUserId(streamData.getUserId());
                    return Observable.zip(Observable.just(streamModelBaseResponse),
                            mService.getAllTopFanStream(AppsterUtility.getAuth(), getTopFanModel.getMappedRequest()),
                            mMappingTopFanWithStreamFunc);
                })
                .subscribe(streamDetailResponseModel -> {
                    if (streamDetailResponseModel == null) return;
                    final StreamModel streamModel = streamDetailResponseModel.getData();
                    mIsWaitingForPassword = false;
                    if (streamModel != null) {
                        currentStreamDetails = streamModel;
                        if (mAppOwnerProfile != null && !mAppOwnerProfile.isDevUser()) {
                            EventTracker.trackEnterStream(isRecorded, currentStreamDetails.getStreamId());
                        }
                        if (streamModel.getStatus() == 1) {
                            mMediaUrl = streamModel.getStreamUrl();
                            isRecorded = false;
                        } else if (streamModel.getStatus() == 2) {
                            if (streamModel.isIsRecorded() && streamModel.getStreamRecording() != null) {
                                mMediaUrl = streamModel.getStreamRecording().PlayUrl;
                                isRecorded = true;
                            } else {
                                mStreamUserId = streamModel.getUserId();
                                if (streamModel.getPublisher() != null)
                                    publisherImage = streamModel.getPublisher().getUserImage();
                                handleShowEndLayout("");
                                return;
                            }
                        }
                        handleData();
                        mStreamTitle = streamModel.getTitle();
                        mViewSessionId = streamModel.getViewSessionId();
                        mIsTriviaShow = mIsTriviaShowRunning = streamModel.isTrivia;
                        setDataToViewStartRecording(streamModel);

                        if (isRecorded) {
                            if (!TextUtils.isEmpty(streamModel.getHistoryChat())) {
                                getJSONFromUrl(streamModel.getHistoryChat());
                            }

                        } else {
                            getStreamTitleSticker(streamSlug);
                        }
//                                mChatManager.runFakeTimerMessage();
//                            getAllBotImage(streamSlug);
                        setupLiveCommerce(streamModel);
                        setupAudioListener();
                        bindEvents();
                        showStreamTitle();
                        mGiftRankingGroupView.updateRankingList(mIsTriviaShow ? Collections.EMPTY_LIST : streamModel.dailyTopFansList);
                        mGiftRankingGroupView.setListener(this::onShowProfileClicked);
                    }

                }, error -> {
                    Timber.e(error);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity().getApplicationContext(), "Cannot Start The Stream", Toast.LENGTH_LONG).show();
                    }
                }));
    }

    private void setUpWaitTimeout() {
        Timber.e("setUpWaitTimeout");
        mCompositeSubscription.add(Observable.just(true)
                .delay(15, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(aBoolean -> isFragmentUIActive() && !mIsFirstFrameRendered && mEndStreamLayout == null && !mIsWaitingForPassword)
                .subscribe(aBoolean -> showNetWorkUnstablePopup(), error -> Timber.e(error.getMessage())));
    }

    private void showNetWorkUnstablePopup() {
        if (isFragmentUIActive()) {
            Timber.e("showNetWorkUnstablePopup");
            new DialogbeLiveConfirmation.Builder()
                    .title(getString(R.string.app_name))
                    .message(getString(R.string.stream_network_unstable))
                    .confirmText(getString(R.string.btn_text_close_stream))
                    .cancelText(getString(R.string.btn_text_wait))
                    .onConfirmClicked(this::closeStream)
                    .onCancelClicked(this::setUpWaitTimeout).build().show(getActivity());
        }
    }

    void checkLocalFile(String fileName) {
        File dir = new File(Constants.FILE_CACHE_FOLDER);

        if (!dir.exists()) {
            boolean isDirectoryCreated = dir.mkdirs();
            if (!isDirectoryCreated) {
                Timber.e("Cannot create directory");
            }
        }
        File localFile = new File(Constants.FILE_CACHE_FOLDER, fileName);
        if (localFile.exists()) {
            readRecordedMessageFromFile("/" + fileName);
        }
    }

    void downloadRecordedFile(final String historyChat) {

        FileDownloader.getInstance().isFileAlreadyDownloaded(historyChat, new DownloadVideos.IFileAlreadyDownloadedListener() {
            @Override
            public void needToDownload(boolean isNeedToDownload, String fileName) {
                if (isNeedToDownload) {
                    FileDownloader.getInstance().downloadFile(historyChat, new DownloadVideos.IDownloadListener() {
                        @Override
                        public void successful(String filePath) {
                            readRecordedMessageFromFile(filePath);
                        }

                        @Override
                        public void fail() {

                        }
                    });
                } else {
                    //read from local file
                    readRecordedMessageFromFile(fileName);
                }
            }
        });

    }


    Subscription readRecoredMessageSubscription;

    void readRecordedMessageFromFile(String filePath) {
        mRecordedUserMessage.clear();

        readRecoredMessageSubscription = Observable.just(filePath)
                .observeOn(Schedulers.newThread())
                .map(this::getJSONFromUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> RxUtils.unsubscribeIfNotNull(readRecoredMessageSubscription), Timber::e);

    }

    public String getJSONFromUrl(String url) {
        if (StringUtil.isNullOrEmptyString(url)) return "";
        if (!URLUtil.isValidUrl(url)) return "";
        final StringBuilder buffer = new StringBuilder();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Timber.e(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) return;
                BufferedReader reader = null;

                try {
                    InputStream ins = response.body().byteStream();
                    reader = new BufferedReader(new InputStreamReader(ins));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            Timber.e(e);
                        }
                    }
                }

                if (!buffer.toString().isEmpty()) {
                    Gson gson = new Gson();
                    mRecordedUserMessage.addAll(Arrays.asList(gson.fromJson(buffer.toString(), RecordedMessagesModel[].class)));
                }
            }

        });
        return "";
    }


    void showStreamHasBeenDeletedDialog(String title, String messageContent) {
        if (pingTimer != null) {
            pingTimer.cancel();
            pingTimer = null;
        }
        if (!mIsFirstFrameRendered) {
            mIsFirstFrameRendered = true;
        }
        new DialogbeLiveConfirmation.Builder()
                .title(title)
                .singleAction(true)
                .message(messageContent)
                .onConfirmClicked(() -> {
                    if (getActivity() != null) {
                        Utils.hideSoftKeyboard(getActivity());
                        getActivity().setResult(RESULT_OK);
                        getActivity().finish();
                    }
                })
                .build().show(getContext());

    }

    private void initListChat(String currentStreamOwnerName) {
        listItemChat = new ArrayList<>();
        if (!isRecorded) {
            ChatItemModelClass modelClass = new ChatItemModelClass();
            modelClass.setChatDisplayName("warning_message");
            modelClass.setLiked(false);
            listItemChat.add(0, modelClass);

            // welcome message
            ChatItemModelClass welcomeMessage = new ChatItemModelClass();
            welcomeMessage.setChatDisplayName("welcome_message");
            welcomeMessage.setMsg(StringUtil.decodeString(currentStreamDetails.getPublisher().getDisplayName()) + ": " +
                    StringUtil.decodeString(currentStreamDetails.getTitle()) + ".");
            welcomeMessage.setLiked(false);
            listItemChat.add(1, welcomeMessage);
        }
        mChatGroupAdapter = new ChatGroupDelegateAdapter(listItemChat, this, currentStreamOwnerName);
        commentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        commentsListView.setLayoutManager(commentLayoutManager);
        commentsListView.setAdapter(mChatGroupAdapter);
        commentsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerViewCommentScrollWaited) {
                    recyclerViewCommentScrollWaited = false;
                    scrollCommentListToEnd();
                }
                recyclerViewCommentState = newState;
            }
        });
        commentsListView.setHasFixedSize(true);
        if (mChatGroupAdapter.getItemCount() > 0) {
            scrollCommentListToEnd();
        }
    }

    void scrollCommentListToEnd() {
        if (recyclerViewCommentState == RecyclerView.SCROLL_STATE_IDLE && isFragmentUIActive() && commentsListView.getVisibility() == View.VISIBLE) {
            recyclerViewCommentScrollWaited = false;
            commentsListView.post(() -> {
                if (commentLayoutManager != null && mChatGroupAdapter.getItemCount() > 0) {
                    commentLayoutManager.scrollToPositionWithOffset(mChatGroupAdapter.getItemCount() - 1, 0);
                }
            });
        } else {
            recyclerViewCommentScrollWaited = true;
        }
    }

    private int mStreamBitrate;

    void setDataToViewStartRecording(StreamModel data) {

        if (!isFragmentUIActive())
            return;

        if (isRecorded) {
            toastRecoredLiveText();
        }

        // First hide it if not have any data
//        userDetailsLayout.setVisibility(View.VISIBLE);
        if (mRecordedUserMessage == null) mRecordedUserMessage = new ArrayList<>();
        currentStreamDetails = data;
        StreamPublisherModel publisher = currentStreamDetails.getPublisher();
        if (checkPublisherNotNull()) {
            storeCurrentStreamToPrefs(publisher.getUserId());
        }
        mStreamBitrate = currentStreamDetails.getWowzaVideoFrameRate();
        wordCountMap = new HashMap<>();
        Timber.e("stream bitrate %d", mStreamBitrate);

        if (StringUtil.isNullOrEmptyString(publisherImage)) {
            blurImage(publisher.getUserImage());
        }

        publisherImage = publisher.getUserImage();
        String publisherName = publisher.getDisplayName();
        if (!publisher.getDisplayName().isEmpty()) {
            publisherName = subStringWithPresetMaxLength(publisherName);
        }

        if (mIsTriviaShow) initTriviaUseCases();
        if (mIsTriviaShow && data.getStatus() == 1) {
            if (mTriviaView != null) mTriviaView.setTriviaOptionListener(this);
            getTriviaInfo(data.triviaId);
            mTriviaInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.trivia_zoom_in);
            mTriviaOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.trivia_zoom_out);
            mTriviaResultOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.trivia_zoom_out);
            mTriviaOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mTriviaView != null) mTriviaView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mTriviaResultOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mTriviaView != null) {
                        mTriviaView.setVisibility(View.GONE);
                        mTriviaView.resetData();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        //get current rank
        updateTopFansList(data.rankingList);

        tvLiveOwnerUserName.setText(publisherName);

        countUserHaveBeenView = currentStreamDetails.getViewCount();
        ViewVideosEvent event = new ViewVideosEvent();
        event.setSlug(streamSlug);
        event.setStream(true);
        event.setViewCount(countUserHaveBeenView);
        WallFeedManager.getInstance().viewVideosCount(event);

//        countFollower = publisher.getFollowerCount();
        countLikeCount = currentStreamDetails.getLikeCount();
        countGiftCount = currentStreamDetails.getGiftCount();
        mStreamerId = publisher.getUserId();
        mTotalGoldFans = publisher.getTotalGoldFans();

        updateFollowHostButton(checkIsCurrentUser(publisher.getUserName()) || publisher.isFollow());
        updateTotalStars(mTotalGoldFans);
        updateTotalPoint(currentStreamDetails.getViewerPoint());
        updateStreamViewCount();
        initListChat(publisher.getDisplayName());
        if (!isRecorded)
            toastMessagePoints(currentStreamDetails.statusMessage);

        startPingingForLiveData();

        if (!isRecorded) {
            pingIntervalManiplulateNetworkText();
            pingNetworkDownloadSpeed();
            pingNetworkUploadSpeed(streamSlug);
            if (currentStreamDetails.subStream != null && currentStreamDetails.subStream.userId != Integer.parseInt(mAppOwnerProfile.getUserId())) {
                //don't update substream if user id of substream is equal current user id
                mSubStreamData = currentStreamDetails.subStream;
            }

        }

//        updateSubStreamState(mSubStreamData);
        releaseBotUsers();

        if(mIsTriviaShow){
            /** for trivia we using agora chat**/
//            mAgoraChatManager.leaveGroup(streamSlug);
            if(mAgoraChatManager!=null) mAgoraChatManager.joinGroup(streamSlug,mStreamChatGroupListener,publisher.getUserName());
        }else {
            mChatManager.clearArrayCurrentUserInStream();
            mChatManager.leaveRoom();
            if (AppsterApplication.mAppPreferences.isUserLogin()) {
                mChatManager.setStreamChatGroupListener(mStreamChatGroupListener);
                mChatManager.createGroupChat(streamSlug, false, publisher.getUserName());
            } else {
                mChatManager.createGroupChatAsGuest(streamSlug, false, publisher.getUserName());
            }
        }
        if (data.AfkStatus == 1 && !isRecorded) {
            streamPause();
        }
        ImageLoaderUtil.displayUserImage(getActivity(), publisherImage, ciOwnerUserImage);

    }

    private void initTriviaUseCases() {
        String authen = AppsterUtility.getAuth();
        TriviaRepository triviaDataSource = new TriviaDataRepository(new CloudTriviaDataSource(mService, authen));
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        mTriviaInfoUseCase = new TriviaInfoUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaResultUseCase = new TriviaResultUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaAnswerUseCase = new TriviaAnswerUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaFinishUseCase = new TriviaFinishUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaCheckReviseUseCase = new TriviaCheckReviveUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaUseReviveUseCase = new TriviaUseReviveUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaWinnerListUseCase = new TriviaWinnerListUseCase(uiThread, ioThread, triviaDataSource);
        mTriviaQuestionUseCase = new TriviaQuestionUseCase(uiThread, ioThread, triviaDataSource);
    }

    private AppsterChatManger.StreamChatGroupListener mStreamChatGroupListener = new AppsterChatManger.StreamChatGroupListener() {
        @Override
        public void onChatGroupJoinedSuccesfully() {
            notifyCurrentViewCount();
        }

        @Override
        public void onChatGroupJoinError(String errorMessage) {
            if(getActivity()!=null) {
                getActivity().runOnUiThread(() -> {
                    if (!isRecorded) {
                        final String errorMsg = getString(R.string.viewer_can_not_join_group);
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        public void onChatGroupWatchersListReceived(List<String> watchers) {
            if(getActivity()!=null) {
                getActivity().runOnUiThread(() -> {
                    if (!mIsSetupJoinedList.get() && mChatGroupAdapter != null) {
                        mIsSetupJoinedList.set(true);
                        mChatGroupAdapter.putJoinedUserList(watchers);
                    }
                });
            }

        }
    };

    void notifyCurrentViewCount() {
        if (isFragmentUIActive()) {
            if (mAppOwnerProfile == null) return;
            ChatItemModelClass itemModelClass = new ChatItemModelClass();
            itemModelClass.setChatDisplayName(mAppOwnerProfile.getDisplayName());
            itemModelClass.setUserName(mAppOwnerProfile.getUserName());
            itemModelClass.setUserIdSend(mAppOwnerProfile.getUserId());
            itemModelClass.setProfilePic(mAppOwnerProfile.getUserImage());
            itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_STATISTIC);
            itemModelClass.topFanList = mTopFanList;
            itemModelClass.setMsg("");
            itemModelClass.setTotalLikes(countLikeCount);
            itemModelClass.setTotalViewers(countUserHaveBeenView);
            try {
                if(mIsTriviaShow){
                 if(mAgoraChatManager!=null) mAgoraChatManager.sendGroupMessage(streamSlug,itemModelClass);
                }else {
                    if (mChatManager != null) mChatManager.sendGroupMessage(itemModelClass);
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void toastRecoredLiveText() {
        TextView textView = new TextView(getActivity());
        textView.setBackgroundResource(R.drawable.boder_text_background);
        textView.setText(getString(R.string.streaming_recorded_live_toast));
        textView.setTextColor(Color.WHITE);

        mRecordedLiveToast = new Toast(getActivity().getApplicationContext());
        mRecordedLiveToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        mRecordedLiveToast.setDuration(Toast.LENGTH_LONG);
        mRecordedLiveToast.setView(textView);
        mRecordedLiveToast.show();
    }

    private void updateTotalStars(long totalGold) {
//        if (totalGold <= Constants.LIMIT_TOTAL_STARS_IN_VIEW) {
        txtStars.setText(String.valueOf(totalGold));
//        } else {
//            txtStars.setText(Utils.formatThousand(totalGold));
//        }
    }

    private void updateTotalPoint(int totalPoint) {
        tvUserPoint.setText(Utils.formatThousand(totalPoint));
        UserModel userModel = AppsterApplication.mAppPreferences.getUserModel();
        userModel.setPoints(totalPoint);
        AppsterApplication.mAppPreferences.saveUserInforModel(userModel);
    }

    private void setupLiveTopPanelBg() {
        mVLiveStreamerBg.setPivotX(0f);
        final ViewTreeObserver obs = mVLiveStreamerBg.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mVLiveStreamerBg.getViewTreeObserver().removeOnPreDrawListener(this);
                onPreDrawLiveTopPanelBg();
                return true;
            }
        });
        ((ViewGroup) tvCurrentView.getParent()).addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            Timber.d("onLayoutChange tvCurrentView parent");
            updateLiveTopPanelBg();
        });
    }

    int currentTimeLoveViewsContainerRight;

    void onPreDrawLiveTopPanelBg() {
        if (currentTimeLoveViewsContainerRight == mLlTimeLoveViewsContainer.getRight()) {
            return;
        }
        currentTimeLoveViewsContainerRight = mLlTimeLoveViewsContainer.getRight();

        int liveStreamBgWidth = currentTimeLoveViewsContainerRight
                + (int) getResources().getDimension(R.dimen.live_love_views_container_margin_end)
                + (int) getResources().getDimension(R.dimen.live_top_panel_bg_margin);
        Timber.d("onPreDrawLiveTopPanelBg mLlTimeLoveViewsContainer.getRight() %d", currentTimeLoveViewsContainerRight);
        Timber.d("onPreDrawLiveTopPanelBg mIBtnHostFollow.getX() %f", mIBtnHostFollow.getX());
        Timber.d("onPreDrawLiveTopPanelBgmIBtnHostFollow.getRight() %d", mIBtnHostFollow.getRight());

        ViewGroup.LayoutParams layoutParams = mVLiveStreamerBg.getLayoutParams();
        layoutParams.width = liveStreamBgWidth;
        mVLiveStreamerBg.requestLayout();
        Timber.d("onPreDrawLiveTopPanelBg set layout params %d", liveStreamBgWidth);
    }

    private void updateLiveTopPanelBg() {
        currentTimeLoveViewsContainerRight = mLlTimeLoveViewsContainer.getRight();

        int startWidth = mVLiveStreamerBg.getRight();
        int endWidth = currentTimeLoveViewsContainerRight
                + (int) getResources().getDimension(R.dimen.live_love_views_container_margin_end)
                + (int) getResources().getDimension(R.dimen.live_top_panel_bg_margin);

        if (mIBtnHostFollow.getVisibility() == View.VISIBLE) {
            endWidth += getResources().getDimension(R.dimen.live_host_follow_button_size);
        }
        animationLiveTopPanelBg(startWidth, endWidth);
        Timber.d("updateLiveTopPanelBg mLlTimeLoveViewsContainer.getRight() %d", currentTimeLoveViewsContainerRight);
        Timber.d("updateLiveTopPanelBg reset layout params %d - %d", startWidth, endWidth);
    }

    private void animationLiveTopPanelBg(int start, int end) {
        ValueAnimator anim = ValueAnimator.ofInt(start, end);
        anim.addUpdateListener(valueAnimator -> {
            if (mVLiveStreamerBg == null) return;
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = mVLiveStreamerBg.getLayoutParams();
            layoutParams.width = val;
            mVLiveStreamerBg.requestLayout();
        });
        anim.setDuration(mFollowHostButtonAnimationDuration);
        anim.start();
    }

    final static int mFollowHostButtonAnimationDuration = 350;

    private void updateFollowHostButton(boolean isFollowingHost) {
        int endWidth =
                +mLlTimeLoveViewsContainer.getRight()
                        + (int) getResources().getDimension(R.dimen.live_love_views_container_margin_end)
                        + (int) getResources().getDimension(R.dimen.live_top_panel_bg_margin);
        Timber.d("updateFollowHostButton mLlTimeLoveViewsContainer.getRight() %d", mLlTimeLoveViewsContainer.getRight());

        if (isFollowingHost) {
            if (mIBtnHostFollow.getVisibility() == View.GONE) {
                return;
            }
            mIBtnHostFollow.setVisibility(View.GONE);
            int startWidth = mVLiveStreamerBg.getRight();
            Timber.d("onAnimationUpdate hide %d to %d", startWidth, endWidth);
            animationLiveTopPanelBg(startWidth, endWidth);
            if (!isRecorded) {//push a message into chat box
                sendFollowMessage(currentStreamDetails.getPublisher().getDisplayName());
            }
        } else {
            if (mIBtnHostFollow.getVisibility() == View.VISIBLE) {
                return;
            }
            mIBtnHostFollow.setVisibility(View.VISIBLE);
            int startWidth = mVLiveStreamerBg.getRight();
            endWidth += getResources().getDimension(R.dimen.live_host_follow_button_size); //width of host-follow button
            animationLiveTopPanelBg(startWidth, endWidth);
        }

        Timber.d("is following %s", oldFollowStatus);
    }

    private void blurImage(String userImage) {

        int heightScreen = Resources.getSystem().getDisplayMetrics().heightPixels;
        int widthScreen = Resources.getSystem().getDisplayMetrics().widthPixels;
        if (StringUtil.isNullOrEmptyString(userImage)) {
            userImage = "https://stagingapi.appsters.net/api/";
        }
        ImageLoaderUtil.displayUserImage(getContext(), userImage, userHotImage, widthScreen, heightScreen, new BlurTransformation(getContext().getApplicationContext()));
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.e("onStop()");
        if (pingTimer != null) {
            pingTimer.cancel();
        }
        if (mRecordedLiveToast != null) {
            mRecordedLiveToast.cancel();
        }

        if (mTickHandler != null) {
            mTickHandler.removeCallbacks(null);
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.runInBackground(true);
        }

        if (!isRecorded && !mIsTrackedLeaveTime && mViewSessionId != 0 && !TextUtils.isEmpty(streamSlug)) {
            mCompositeSubscription.add(mService.leaveStream(AppsterUtility.getAuth(), streamSlug, mViewSessionId)
                    .subscribe(booleanBaseResponse -> {
                    }, Timber::e));
            mHasCalledEndedAPI.set(true);
        }
    }

    public void startActivityLogin() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(), R.anim.slide_in_up, R.anim.keep_view_animation);
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN, options.toBundle());

    }

    @Override
    public void onBackPress() {
        closeStream();

    }

    private void leaveStream() {
        if (etComment.isFocused()) etComment.clearFocus();
        showFullscreen();
        mediaPlayerControl = null;
        setDatainish();
        if (getActivity() != null) getActivity().finish();
    }

    private int getFullScreenVisibilityFlags() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }

    void setDatainish() {

        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            releaseBotUsers();
            mChatManager.leaveRoom();
            mAgoraChatManager.leaveGroup();
        }

        if (AppsterApplication.mAppPreferences.isUserLogin() && checkStreamDetailNotnull()
                && ((oldFollowStatus != isFollowed())) || evenlike != null) {

            FollowStatusChangedEvent followStatusChangedEvent = new FollowStatusChangedEvent();
            int followType = currentStreamDetails.getPublisher().isFollow() ? Constants.IS_FOLLOWING_USER : Constants.UN_FOLLOW_USER;
            followStatusChangedEvent.setFollowType(followType);
            followStatusChangedEvent.setStream(true);
            followStatusChangedEvent.setUserId(mStreamerId);
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra(ConstantBundleKey.BUNDLE_DATA_FOLLOW_USER_FROM_PROFILE_ACTIVITY, followStatusChangedEvent);
                intent.putExtra(ConstantBundleKey.BUNDLE_DATA_LIST_LIKE_FROM_PROFILE_ACTIVITY, evenlike);
                if (getActivity() != null) getActivity().setResult(RESULT_OK, intent);
            }
        } else if (isClickGoHome) {
            Timber.e("clear all tasks");
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(ConstantBundleKey.BUNDLE_GO_HOME, true);
                if (getActivity() != null) getActivity().setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        }
    }

    boolean checkStreamDetailNotnull() {
        return currentStreamDetails != null && (currentStreamDetails.getPublisher() != null);
    }

    void preparePlayer() {
        //String mMediaUrl = mMediaUrl.replace(":1935", "");
//        if (mMediaUrl.startsWith("http")) {
//            mMediaUrl = mMediaUrl.replace("http://static-dev-clients.belive.sg/livestreamrecord/", "rtmp://stgwowza.view.belive.sg:1935/vod/");
//            mMediaUrl = mMediaUrl.replace("http://static-clients.belive.sg/livestreamrecord/", "rtmp://wowza.view.belive.sg:1935/vod/");
//        }
        Timber.e("stream url %s", mMediaUrl);
        // tick timer
        setTickTimer();

        KSYConfig.initConfig(mMediaPlayer);
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
        mMediaPlayer.setOnInfoListener(mOnInfoListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

        // for china
        if (AppPreferences.getInstance(getActivity()).getUserCountryCode().equals(CountryCode.CHINA)) {
            mMediaPlayer.setBufferTimeMax(5.0f);
            mMediaPlayer.setTimeout(10, 30);
        }

        //---------

//        mMediaPlayer.setDecodeMode(KSYMediaPlayer.KSYDecodeMode.KSY_DECODE_MODE_AUTO);
        if (getActivity() != null) getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(mMediaUrl);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        mMediaPlayer.runInForeground();

    }

    private void setTickTimer() {

        if (mTickHandler != null) {
            mTickHandler.removeCallbacksAndMessages(null);
        }

        if (isRecorded) {
            // record video

            final int interval = 500;

            // make handler on UI Thread
            mTickHandler = new Handler();
            mTickHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Activity activity = getActivity();
                    if (activity == null || activity.isFinishing()) return;
                    if (mMediaPlayer == null) return;
                    if (mIsDisconnectedPlayer) return;

                    long position = mMediaPlayer.getCurrentPosition();
                    position = Math.min(position, mMediaPlayer.getDuration());

                    mVideoTimeChanged.onNext((int) position / 1000);

                    mTickHandler.postDelayed(this, interval);
                }
            }, interval);

        }
    }

    void releasePlayer() {
        Timber.e("mMediaPlayer releasePlayer!");
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            Timber.e("mMediaPlayer destroyed!");
        }
        if (mediaController != null) {
            mediaController.release();
        }
    }

    /**
     * called when
     * - host blocked this viewer
     * - host ended stream
     * - video reached the end
     */
    void handleShowEndLayout(String reason) {
        Timber.e("handleShowEndLayout reason %s", reason);
        if (isFragmentUIActive()) {
            hideProgressAndUserImage();
            if (mediaController != null && mediaController.isShowing()) {
                mediaController.hide();
            }

            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
            }

            showEndStreamLayout(reason);

            callStaticEndStream();
        }
    }

    String getTrackingLiveReason(String trigger, boolean isRecorded) {
        final String reason = String.format(TRACKING_STREAM_FORMAT, isRecorded ? RECORDED_STREAM : LIVE_STREAM, TARGET_VIEWER, streamSlug, mAppOwnerProfile != null ? mAppOwnerProfile.getUserName() : "null", trigger);
        Timber.e("getTrackingLiveReason %s", reason);
        return reason;
    }

    EndStreamLayout mEndStreamLayout;
    GuestSubStreamLayout mSubStreamLayout;
    HostSubStreamLayout mHostSubStreamLayout;

    private void showEndStreamLayout(String reason) {
        mIsEndedStream = true;
        releaseTriviaPlayer();
        hideTriviaDialog();
        hideShopDialog();
        hideUserProfileDialog();
        hideShareDialog();
        hideAllPopup();
        removeTutorialIfNeed();
        if (mViewSessionId != 0 && !TextUtils.isEmpty(streamSlug)) {
            mIsTrackedLeaveTime = true;
            mCompositeSubscription.add(mService.leaveStream(AppsterUtility.getAuth(), streamSlug, mViewSessionId)
                    .subscribe(booleanBaseResponse -> Timber.e(String.valueOf(booleanBaseResponse.getData())), Timber::e));
        }
        luckywheelVisibilityListener.onNext(false);
        Utils.hideSoftKeyboard(getActivity());
        inflateEndStreamLayout(reason);
    }

    private void hideTriviaDialog() {
        if (mTriviaHowToPlayDialog != null) {
            mTriviaHowToPlayDialog.dismissAllowingStateLoss();
        }
        if (mTriviaRankingDialog != null) {
            mTriviaRankingDialog.dismissAllowingStateLoss();
        }

        if (mTriviaReviveUsageDialog != null) {
            mTriviaReviveUsageDialog.dismissAllowingStateLoss();
        }

        if (mWinnerPopupDialog != null) {
            mWinnerPopupDialog.dismissAllowingStateLoss();
        }

        if (pointInfoDialog != null) pointInfoDialog.dismissAllowingStateLoss();
    }

    public void inflateEndStreamLayout(String reason) {
        if (vsEndStream.getParent() != null) {
            View view = vsEndStream.inflate();
            mIntervalSubject.onNext(-1L);
            mEndStreamLayout = new EndStreamLayout(view, this, reason);
        }
    }

    public void hideAllPopup() {

        if (mExpensiveGiftDialog != null && mExpensiveGiftDialog.isAdded()) {
            mGiftQueue.clear();
            mExpensiveGiftDialog.dismiss();
        }

        if (topFanDialog != null) {
            topFanDialog.dismiss();
        }

        if (sendGift != null) {
            sendGift.dimissDialog();
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

        if (mTriviaHowToPlayDialog != null) mTriviaHowToPlayDialog.dismissAllowingStateLoss();

        if (mTriviaReviveUsageDialog != null) mTriviaReviveUsageDialog.dismissAllowingStateLoss();

        if (!listTriviaDialog.isEmpty()) {
            for (int i = 0; i < listTriviaDialog.size(); i++) {
                TriviaDialog triviaDialog = listTriviaDialog.get(i);
                if (triviaDialog != null && triviaDialog.isShowing()) triviaDialog.dismiss();
            }
        }

        luckywheelVisibilityListener.onNext(false);
        Utils.hideSoftKeyboard(getActivity());

    }

    private void callStaticEndStream() {
        // Call statistic Stream
        mCompositeSubscription.add(mService.statisticStream(AppsterApplication.mAppPreferences.getUserTokenRequest(), new BeginStreamRequestModel(streamSlug))
                .subscribe(statisticStreamResponseModel -> {
                    if (!isFragmentUIActive()) return;
                    if (statisticStreamResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        if (!isFragmentUIActive()) {
                            return;
                        }
                        mEndStreamLayout.updateEndStreamData(statisticStreamResponseModel.getData().getViewCount(), statisticStreamResponseModel.getData().getLikeCount(), statisticStreamResponseModel.getData().getTotalGold(), statisticStreamResponseModel.getData().getDuration());
                    }
                }, error -> {
                    if (error instanceof UnknownHostException) {
                        Timber.e("cannot connect to server!!!!!!");
                    } else {
                        Timber.e(error.getMessage());
                    }
                }));
    }

    private void releaseBotUsers() {
        mChatManager.clearArrayCurrentBotInStream();
        if(mAgoraChatManager!=null) mAgoraChatManager.clearArrayCurrentBotInStream();
    }


    public void onClickViewEndStream(View view) {
        // this handle when click on back ground end stream
    }

    public void onClickViewDoNoThing(View view) {
        // this handle when click on back ground end stream
    }


    LuckyWheelLayout openLuckyWheel() {
        if (mLuckyWheelLayout == null) {
            View view = vsLuckywheel.inflate();
            mLuckyWheelLayout = new LuckyWheelLayout(view);
        } else {
            luckywheelVisibilityListener.onNext(true);
        }
        return mLuckyWheelLayout;
    }

    private void changePlayerSize(int width, int height) {
//        LinearLayout.LayoutParams frameParrent = (LinearLayout.LayoutParams) mMediaPlayer.getLayoutParams();
//        frameParrent.width = width;
//        frameParrent.height = height;
//        if (width != -1 && height != -1) {
//            frameParrent.topMargin = llStreamingToppanel.getBottom();
//        } else {
//            frameParrent.topMargin = 0;
//        }
//        mMediaPlayer.setLayoutParams(frameParrent);
    }

    private void showChooseShareType() {

        final SharePostDialog sharePostDialog = SharePostDialog.newInstance(true);
        sharePostDialog.setHideShareInstagramView();
        sharePostDialog.setHideCopyLinkView(false);
        sharePostDialog.setDialogDismisListener(this::showFullscreen);
        sharePostDialog.setChooseShareListenner(new SharePostDialog.ChooseShareListenner() {
            @Override
            public void chooseShareFacebook() {
                if (getActivity() != null && currentStreamDetails != null && !StringUtil.isNullOrEmptyString(currentStreamDetails.getWebStreamUrl())) {
                    SocialManager.getInstance().shareURLToFacebook(getActivity(), currentStreamDetails.getWebStreamUrl(),
                            FACEBOOK_SHARE_REQUEST_CODE, mFBCallbackManager, new FacebookCallback<Sharer.Result>() {
                                @Override
                                public void onSuccess(Sharer.Result result) {
                                    Timber.e("onSuccess");
                                    sendMessageShareStream();
                                    if (!isRecorded)
                                        userEarnPoints(new ShareStreamModel("Stream", streamSlug, Constants.FACEBOOK_SHARE_TYPE));
                                }

                                @Override
                                public void onCancel() {
                                    Timber.e("onCancel");
                                }

                                @Override
                                public void onError(FacebookException error) {
                                    Timber.e("onError");
                                }
                            });
                }
            }

            @Override
            public void chooseShareInstagram() {
//                Toast.makeText(getActivity(), getString(R.string.share_instagram_not_share_text), Toast.LENGTH_LONG).show();
//                SocialManager.getInstance().shareFeedToInstagram(getActivity(), arrayListNewFeeds.get(position));

            }

            @Override
            public void chooseShareTwtter() {
                if (getActivity() != null && currentStreamDetails != null && !StringUtil.isNullOrEmptyString(currentStreamDetails.getWebStreamUrl())) {
                    Intent intent = new TweetComposer.Builder(getActivity())
                            .text(getShareContent(getActivity(), currentStreamDetails.getPublisher().getUserName(),
                                    currentStreamDetails.titlePlainText) + "\n" + currentStreamDetails.getWebStreamUrl())
                            .createIntent();
                    startActivityForResult(intent, TWEET_SHARE_REQUEST_CODE);
                    if (!isRecorded)
                        AppsterApplication.mAppPreferences.saveShareStreamModel(new ShareStreamModel(
                                "Stream", streamSlug, Constants.TWITTWER_SHARE_TYPE));
                }
            }

            @Override
            public void chooseShareEmail() {
                if (currentStreamDetails != null && !StringUtil.isNullOrEmptyString(currentStreamDetails.getWebStreamUrl())) {
                    mIsTapOnShareSNS = true;
                    SocialManager.getInstance().shareURLToEmail(getActivity(), currentStreamDetails.getWebStreamUrl(),
                            currentStreamDetails.getPublisher().getUserName(), currentStreamDetails.titlePlainText, SHARE_TYPE_STREAM, false);
                    if (!isRecorded)
                        AppsterApplication.mAppPreferences.saveShareStreamModel(new ShareStreamModel(
                                "Stream", streamSlug, Constants.EMAIL_SHARE_TYPE));
                }
            }

            @Override
            public void chooseShareWhatApp() {
                if (currentStreamDetails != null && !StringUtil.isNullOrEmptyString(currentStreamDetails.getWebStreamUrl())) {
                    mIsTapOnShareSNS = true;
                    SocialManager.getInstance().shareVideoToWhatsapp(getActivity(), currentStreamDetails.getWebStreamUrl(),
                            currentStreamDetails.getPublisher().getUserName(), currentStreamDetails.titlePlainText, SHARE_TYPE_STREAM, false);
                    if (!isRecorded)
                        AppsterApplication.mAppPreferences.saveShareStreamModel(new ShareStreamModel(
                                "Stream", streamSlug, Constants.WHATSAPP_SHARE_TYPE));
                }
            }

            @Override
            public void copyLink() {
                if (currentStreamDetails != null)
                    CopyTextUtils.CopyClipboard(getContext().getApplicationContext(), currentStreamDetails.getWebStreamUrl(), getString(R.string.share_link_copied));
                if (!isRecorded)
                    userEarnPoints(new ShareStreamModel("Stream", streamSlug, Constants.COPY_LINK_TYPE));
            }

            @Override
            public void chooseShareOthers() {
                if (currentStreamDetails != null && !StringUtil.isNullOrEmptyString(currentStreamDetails.getWebStreamUrl())) {
                    mIsTapOnShareSNS = true;
                    SocialManager.getInstance().shareURLToOthers(getActivity(), currentStreamDetails.getWebStreamUrl(),
                            currentStreamDetails.getPublisher().getUserName(), currentStreamDetails.titlePlainText, SHARE_TYPE_STREAM, false);
                    if (!isRecorded)
                        AppsterApplication.mAppPreferences.saveShareStreamModel(new ShareStreamModel(
                                "Stream", streamSlug, Constants.OTHER_SHARE_TYPE));
                }
            }
        });


        sharePostDialog.show(getActivity().getSupportFragmentManager(), "Share");

    }

    private void userEarnPoints(ShareStreamModel shareStreamModel) {
        if (shareStreamModel == null) return;
        EarnPointsRequestEntity request = new EarnPointsRequestEntity(shareStreamModel.getActionType(), shareStreamModel.getSlug(), shareStreamModel.getMode());
        mCompositeSubscription.add(mService.earnPoints(AppsterUtility.getAuth(), request)
                .filter(subStreamDataBaseResponse -> isFragmentUIActive())
                .filter(subStreamDataBaseResponse -> subStreamDataBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK)
                .subscribe(response -> {

                    if (response.getData() != null) {
                        updateTotalPoint(response.getData().getUserPoints());
                        toastMessagePoints(response.getData().getMessage());
                    }

                }, Timber::e));


        AppsterApplication.mAppPreferences.saveShareStreamModel(null);
    }

    String getShareContent(Context context, String userName, String title) {
        String validatedUserName = TextUtils.isEmpty(userName) ? "" : userName.trim();
        String validatedTitle = TextUtils.isEmpty(title) ? "" : title.trim();
        String content;
        content = String.format(context.getString(R.string.header_title_share_stream_viewer), validatedTitle, validatedUserName).trim();
        return content;
    }

    void sendMessageShareStream() {
        if (isRecorded || mIsStreamShared.get()) return;

        if (checkCurrentInfoNotNull()) {
            mIsStreamShared.set(true);
            final ChatItemModelClass itemModelClass = new ChatItemModelClass();
            itemModelClass.setChatDisplayName(mAppOwnerProfile.getDisplayName());
            itemModelClass.setUserName(mAppOwnerProfile.getUserName());
            itemModelClass.setUserIdSend(mAppOwnerProfile.getUserId());
            itemModelClass.setProfilePic(mAppOwnerProfile.getUserImage());
            itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_SHARE_STREAM);
            itemModelClass.setMsg(getString(R.string.shared_live_stream));
            itemModelClass.rank = mCurrentFanRanking.get();
            try {
                updateMessageInList(itemModelClass);
                sendToGroup(itemModelClass);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFBCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TWEET_SHARE_REQUEST_CODE:
                    sendMessageShareStream();
                    userEarnPoints(AppsterApplication.mAppPreferences.getShareStreamModel());
                    break;

                default:
                    break;
            }
        }

        if (requestCode == Constants.REQUEST_CODE_SHARE_FEED) {
            userEarnPoints(AppsterApplication.mAppPreferences.getShareStreamModel());
        }
        Timber.e("Request code %d - resultCode %s", requestCode, resultCode);
    }

    private void getNaughtyWords() {
        mCompositeSubscription.add(mService.getNaughtyWords()
                .filter(getNaughtyWordDataResponse -> getNaughtyWordDataResponse.getData() != null)
                .subscribe(getNaughtyWordDataResponse -> mNaughtyWords = getNaughtyWordDataResponse.getData(), this::handleRxError));
    }

    private String removeNaughtyWords(String originalString) {
        if (!TextUtils.isEmpty(originalString)) {
            if (mNaughtyWords != null && !mNaughtyWords.isEmpty()) {
                for (String swearWord : mNaughtyWords) {
                    Pattern pat = Pattern.compile(swearWord, Pattern.CASE_INSENSITIVE);
                    Matcher mat = pat.matcher(originalString);
                    originalString = mat.replaceAll("");
                }
            }
        }
        return originalString.trim();
    }

    @Override
    public void onDisplayNameClicked(ChatItemModelClass item) {
        showUserProfileDialog(item.getUserName(), item.getProfilePic());
    }

    @Override
    public void onMessageClicked(ChatItemModelClass item) {
        onScreenClicked(null);
    }

    @Override
    public void onFollowHostSuggestionItemClicked(ChatItemModelClass item) {
        onHostFollowButton();
    }

    @Override
    public void onLiveCommerceSuggestionItemClicked() {
        openLiveCommerceShop(currentStreamDetails);
    }

    //region keyboard
    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";
        Timber.i("onKeyboardHeightChanged in pixels: %d - %s", height, or);
        if (height == 0) {
//            if (etComment != null) etComment.clearFocus();
            showAllView();
            showFullscreen();
        }


        llBottomContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        if (height > 150) {
            if (getContext() != null) height += getNavigationBarSize(getContext()).y;
//            if (hasNavBar(getResources())) {
//                height += getNavBarHeight(getResources());
//            }
            llBottomContainer.animate().setDuration(100).translationY(-height).setListener(this).start();
            if (mIsTriviaShow)
                mTriviaView.animate().setDuration(100).translationY(-mTriviaView.getMoveUpHeight()).start();
            mBtnLiveShop.setVisibility(View.INVISIBLE);

        } else {
            llBottomContainer.animate().setDuration(100).translationY(0).setListener(this).start();
            if (mIsTriviaShow) mTriviaView.animate().setDuration(100).translationY(0).start();
            if (mIsPublisherSeller) mBtnLiveShop.setVisibility(View.VISIBLE);
            if (etComment != null)
                if (etComment != null && etComment.isFocused()) etComment.clearFocus();
        }
    }

    private void showFullscreen() {
        if (getActivity() != null && getActivity().getWindow() != null && getActivity().getWindow().getDecorView() != null) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(getFullScreenVisibilityFlags());
        }
    }

    @Override
    public void onAnimationStart(Animator animator) {
        //not use
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if (llBottomContainer != null) llBottomContainer.setLayerType(View.LAYER_TYPE_NONE, null);
    }

    @Override
    public void onAnimationCancel(Animator animator) {
        //not use

    }

    @Override
    public void onAnimationRepeat(Animator animator) {
        //not use

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Timber.d("AUDIO FOCUS GAIN");
                // resume playback
                mMediaPlayer.setVolume(1f, 1f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                Timber.d("AUDIO FOCUS LOSS");
                mMediaPlayer.setVolume(0f, 0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Timber.d("AUDIO FOCUS LOSS TRANSIENT");
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                mMediaPlayer.setVolume(0f, 0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Timber.d("AUDIO FOCUS LOSS TRANSIENT CAN DUCK");

                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                mMediaPlayer.setVolume(0.2f, 0.2f);
                break;
            default:
                break;
        }
    }


    //endregion

    private boolean mIsFirstFrameRendered = false;
    private boolean mIsWaitingForPassword = false;

    //region inner classes

    void enableTapOnScreen() {
        if (!mIsFirstFrameRendered) {
            mIsFirstFrameRendered = true;
            checkSubStreamStatus();
        }

        if (llStreamingToppanel == null || llBottomContainer == null || periscope == null) {
            return;
        }
        llStreamingToppanel.setEnabled(true);
        llBottomContainer.setEnabled(true);
        periscope.setEnabled(true);
    }

    private void checkSubStreamStatus() {
        if (mSubStreamData != null) {
            mCompositeSubscription.add(mService.getSubStreamDetail(AppsterUtility.getAuth(), streamSlug, mSubStreamData.slug)
                    .filter(subStreamDataBaseResponse -> isFragmentUIActive())
                    .subscribe(subStreamDataBaseResponse -> {
                        if (subStreamDataBaseResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            if (subStreamDataBaseResponse.getData() != null && subStreamDataBaseResponse.getData().receiver != null) {
                                mSubStreamData = subStreamDataBaseResponse.getData();
                            }
                            if (mSubStreamData != null && mSubStreamData.status != 2) {
                                mSubStreamData.status = mSubStreamData.afkStatus == 1 ? State.AWAY : (mSubStreamData.status == 0 ? State.CONNECTING : State.CONNECTED);
                                Timber.e("mSubStreamData.status %d", mSubStreamData.status);
                                updateSubStreamState(mSubStreamData);
                            }
                        }
                    }, Timber::e));
        }
    }

    void disableTapOnScreen() {
        llStreamingToppanel.setEnabled(false);
        llBottomContainer.setEnabled(false);
        periscope.setEnabled(false);
    }

    //region media player loop actions

    void showNetworkError() {
        if (isAdded() && tvNetworkError.getVisibility() != View.VISIBLE) {
            tvNetworkError.setVisibility(View.VISIBLE);
//            getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show());
        }
    }

    void closePlayer() {
        mIsDisconnectedPlayer = true;
        if (!isStoped) {
            handleShowEndLayout("");
            isStoped = true;
            if (BuildConfig.DEBUG && isAdded()) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(), "message close player from KSY", LENGTH_SHORT).show());
            }
        }
    }

    void showEndStreamNetworkError() {
        showNetworkError();
        reachedEnd(TRIGGER_END_BY_GET_VIEWER_NETWORK_SPEED_FAILED);
//        countFailLoadVideosPLays = 0;
    }

    void reachedEnd(String reason) {

        if (!isHomePress) {
            handleShowEndLayout(getTrackingLiveReason(reason, isRecorded));
            isStoped = true;
        } else {
            handleShowEndLayout(getTrackingLiveReason(reason, isRecorded));
        }
        if (BuildConfig.DEBUG && isAdded()) {
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(), "message reachedEnd from KSY", LENGTH_SHORT).show());
        }
    }

    void stopMedia() {
        if (!isFragmentUIActive()) {
            isStoped = true;
            return;
        }
//                    userHotImage.setVisibility(View.GONE);
        Timber.e(String.valueOf(isHomePress));
        if (!isHomePress) {
            if (mediaController != null && mediaController.isShowing()) {
                mediaController.hide();
            }
            handleShowEndLayout("");
            isStoped = true;
        }
    }

    void unMuteMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setPlayerMute(0);
        }
    }

    void muteMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setPlayerMute(1);
        }
    }

    void pauseMedia() {
        hideProgressAndUserImage();
    }


    void playMedia() {
        mIsDisconnectedPlayer = false;
        isHomePress = false;
//        if (mMediaPlayer != null) mMediaPlayer.UpdateView();
        if (isRecorded && mediaController != null) {
            mediaController.setMediaPlayer(mediaPlayerControl);
            mediaController.setEnabled(true);
            if (currentRecordedPos != 0) mMediaPlayer.seekTo(currentRecordedPos);
        }
    }

    private void streamPause() {
        if (mIsTriviaShow)
            return;
        showAFK();
        if (!mIsCalling.get()) muteMedia();
    }

    private void showAFK() {
        mIsPausing.set(true);
        if (imgPauseGradient != null) imgPauseGradient.setVisibility(View.VISIBLE);
        if (tvPauseMessage != null) tvPauseMessage.setVisibility(View.VISIBLE);
    }

    private void streamRestart() {
        hideAFK();
        if (!mIsCalling.get()) unMuteMedia();
        reloadStream();
    }

    private void hideAFK() {
        mIsPausing.set(false);
        if (imgPauseGradient != null) imgPauseGradient.setVisibility(View.GONE);
        if (tvPauseMessage != null) tvPauseMessage.setVisibility(View.GONE);
    }

    private void reloadStream() {
        if (mIsWaitingReconnect.get()) {
            try {
                if (mIsCalling.get() && mSubStreamLayout != null)
                    mSubStreamLayout.notifyCallEndedState();
                showProgressAndUserImage();
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mMediaUrl);
                mMediaPlayer.prepareAsync();
                mIsWaitingReconnect.set(false);
                mIsWaitingTimeoutEnable.set(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //endregion
    void hideProgressAndUserImage() {
        if (mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
        if (userHotImage != null) {
            userHotImage.setVisibility(View.GONE);
        }
    }

    void showProgressAndUserImage() {
        if (mProgress != null) {
            mProgress.setVisibility(View.VISIBLE);
        }
        if (userHotImage != null) {
            userHotImage.setVisibility(View.VISIBLE);
        }
    }

    //endregion

    void navigateToProfileScreen() {
        mediaPlayerControl = null;
        setDatainish();
        if (currentStreamDetails != null) {//started from recorded stream
            Intent intent = new Intent();
            intent.putExtra(Constants.USER_PROFILE_ID, currentStreamDetails.getPublisher().getUserId());
            intent.putExtra(Constants.USER_PROFILE_DISPLAYNAME, currentStreamDetails.getPublisher().getDisplayName());
            getActivity().setResult(RESULT_OK, intent);
        } else {
            if (!StringUtil.isNullOrEmptyString(mStreamUserId)) {
                Intent intent = new Intent();
                intent.putExtra(Constants.USER_PROFILE_ID, mStreamUserId);
                if (AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(mStreamUserId)) {
                    intent.putExtra(ConstantBundleKey.BUNDLE_GO_PROFILE, true);
                }
                getActivity().setResult(RESULT_OK, intent);
            }
        }
        getActivity().finish();
    }


    void navigateToHomeScreen() {
        if (getActivity() == null) return;
        Utils.hideSoftKeyboard(getActivity());
//        EventBus.getDefault().post(new EventBusRefreshFragment());
        isClickGoHome = true;
        setDatainish();

        getActivity().finish();
    }

    boolean checkNotSameUser() {
        return (mAppOwnerProfile != null) && currentStreamDetails.getPublisher().getUserId().equalsIgnoreCase(mAppOwnerProfile.getUserId());
    }

    boolean isFollowed() {
        return currentStreamDetails != null && currentStreamDetails.getPublisher() != null && currentStreamDetails.getPublisher().isFollow();
    }

    void executeFollowUser(String userID) {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            startActivityLogin();
            return;
        }
        if (TextUtils.isEmpty(userID)) return;

        DialogManager.getInstance().showDialog(getActivity(), getResources().getString(R.string.connecting_msg));
        FollowUser followUser = new FollowUser(getActivity(), userID, true);
        followUser.execute();
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                DialogManager.getInstance().dismisDialog();

                if (AppsterApplication.mAppPreferences.isUserLogin() && currentStreamDetails != null && currentStreamDetails.getPublisher() != null) {
                    currentStreamDetails.getPublisher().setIsFollow(Constants.IS_FOLLOWING_USER);
                    if (mEndStreamLayout != null) mEndStreamLayout.changeToFollowing(getContext());
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                DialogManager.getInstance().dismisDialog();
                ((BaseActivity) getActivity()).handleError(message, errorCode);
            }
        });
    }

    void executeFollowUser() {
        if (currentStreamDetails == null || currentStreamDetails.getPublisher() == null) return;
        executeFollowUser(currentStreamDetails.getPublisher().getUserId());
    }

    private void removeHostFollowSuggestionOnRecord() {
        if (mViewerChatMessages == null) return;
        for (RecordedMessagesModel item : mViewerChatMessages) {
            int type = item.getActionType();
            if (RecordedMessagesModel.TYPE_FOLLOW_HOST_SUGGESTION == type) {
                mViewerChatMessages.remove(item);
            }
        }
    }

    private void setupFollowHostSuggestion() {
        if (currentStreamDetails == null || currentStreamDetails.getPublisher() == null
                || currentStreamDetails.getPublisher().isFollow() || hasShownFollowButtonInChat) {
            return;
        }
        mCompositeSubscription.add(Observable.just(currentStreamDetails.getPublisher()).delay(3, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(streamPublisherModel -> !streamPublisherModel.isFollow() && !hasShownFollowButtonInChat)
                .subscribe(streamPublisherModel -> addFollowHostSuggestionItemToChatBox(), Timber::e));

    }

    private void addFollowHostSuggestionItemToChatBox() {
        if (currentStreamDetails == null || currentStreamDetails.getPublisher() == null
                || hasShownFollowButtonInChat) {
            return;
        }
        hasShownFollowButtonInChat = true;
        StreamPublisherModel publisher = currentStreamDetails.getPublisher();
        ChatItemModelClass chatItem = new ChatItemModelClass();
        chatItem.setMessageType(ChatItemModelClass.CHAT_TYPE_FOLLOW_HOST_SUGGESTION);
        chatItem.setProfilePic(publisher.getUserImage());
        chatItem.setChatDisplayName(publisher.getDisplayName());
        chatItem.setUserName(publisher.getUserName());
        chatItem.setUserIdSend(publisher.getUserId());
        if (isRecorded) {
            tempStoreViewerChatMessage(chatItem, RecordedMessagesModel.TYPE_FOLLOW_HOST_SUGGESTION);
        }
        updateMessageInList(chatItem);
    }

    private boolean shouldShowTutorial() {
        return !isRecorded && !GlobalSharedPreferences.isTutorialViewerStreamShown(getContext());
    }

    private void checkToShowTutorial() {
        if (shouldShowTutorial()) {
            showChatBarTutorial();
        }
    }

    private void interceptEventBeforeTutorial() {
        View view = new View(getContext());
        ViewGroup.LayoutParams lps = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lps);
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setTag(TAG_INTERCEPT_EVENT_BEFORE_TUTORIAL);
        view.setOnClickListener(view1 -> {
        });
        ViewGroup parent = getActivity().findViewById(android.R.id.content);
        parent.addView(view);
    }

    private void showChatBarTutorial() {
        if (mIsEndedStream) return;
        mIsTutorialShowing = true;
        new ShowCaseViewTutorial.Builder(getContext())
                .setAnchorView(etComment)
                .setBubbleMarginTopBottom(Utils.dpToPx(12))
                .setBubbleMessage(getString(R.string.tutorial_chat_viewer))
                .setOnShowCaseViewDismiss(() -> mCompositeSubscription.add(Completable.complete()
                        .delay(Constants.DELAYED_TIME_SHOWN_NEXT_TUTORIAL, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::showSnsTutorial, Timber::e))).build().show(getActivity());
    }

    private void showSnsTutorial() {
        if (mIsEndedStream) return;
        new ShowCaseViewTutorial.Builder(getContext())
                .setAnchorView(mIbSns)
                .setBubbleMarginTopBottom(Utils.dpToPx(12))
                .setBubbleMessage(getString(R.string.tutorial_sns_viewer))
                .setOnShowCaseViewDismiss(() -> mCompositeSubscription.add(Completable.complete()
                        .delay(Constants.DELAYED_TIME_SHOWN_NEXT_TUTORIAL, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::showGiftTutorial, Timber::e))).build().show(getActivity());
    }

    void showGiftTutorial() {
        if (mIsEndedStream) return;
        new ShowCaseViewTutorial.Builder(getContext())
                .setAnchorView(mIbSendGift)
                .setBubbleMarginTopBottom(Utils.dpToPx(12))
                .setBubbleMessage(getString(R.string.tutorial_send_gift_viewer))
                .setOnShowCaseViewDismiss(this::onFinishTutorial).build().show(getActivity());
    }

    private void removeTutorialIfNeed() {
        if (getActivity() != null) {
            mIsTutorialShowing = false;
            ShowCaseViewTutorial.removeItself(getActivity());
            ViewGroup parent = getActivity().findViewById(android.R.id.content);
            View v = parent.findViewWithTag(TAG_INTERCEPT_EVENT_BEFORE_TUTORIAL);
            if (v != null)
                parent.removeView(v);
        }
    }

    private void onFinishTutorial() {
        mIsTutorialShowing = false;
        GlobalSharedPreferences.setTutorialViewerStreamShown(getContext());
        ViewGroup parent = getActivity().findViewById(android.R.id.content);
        View v = parent.findViewWithTag(TAG_INTERCEPT_EVENT_BEFORE_TUTORIAL);
        parent.removeView(v);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA_AUDIOREC:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if( mSubStreamLayout==null && mLiveInviteDialog!=null && mLiveInviteDialog.isVisible()) {
////                        mLiveInviteDialog.onPermissionGranted();
//                    }else {
//                        if (mSubStreamLayout != null) mSubStreamLayout.startCameraPreview();
//                    }
                } else {
                    if (mLiveInviteDialog != null && mLiveInviteDialog.isVisible()) {
                        mLiveInviteDialog.onPermissionDenied();
                    }
                    Log.e(TAG, "No CAMERA or AudioRecord permission");
                    Toast.makeText(getContext(), "No CAMERA or AudioRecord permission",
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private static final class StatisticTasks extends TimerTask {
        private final WeakReference<MediaPlayerFragment> mFragment;

        StatisticTasks(MediaPlayerFragment fragment) {
            this.mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void run() {
            final MediaPlayerFragment parent = mFragment.get();
            if (parent != null) {
                parent.pingStreamStatistic();
            }
        }
    }

    void stopVideoCallIfAny() {
        if (mSubStreamLayout != null) mSubStreamLayout.releaseKSYResources();
    }

    //region endstream
    static class EndStreamLayout {

        @Bind(R.id.ivStreamOwnerAlpha)
        ImageView ivStreamOwnerAlpha;
        @Bind(R.id.duration_time)
        CustomFontTextView durationTime;
        @Bind(R.id.userHotsImage)
        CircleImageView userImage;
        @Bind(R.id.score_on_stream_end)
        CustomFontTextView scoreOnStreamEnd;
        @Bind(R.id.like_received_count)
        CustomFontTextView likeReceivedCount;
        @Bind(R.id.gift_received_count)
        CustomFontTextView giftReceivedCount;
        @Bind(R.id.go_back_stream_end)
        CustomFontButton goBackStreamEnd;
        @Bind(R.id.go_profile)
        CustomFontButton btnProfile;

        @Bind(R.id.btnFollow)
        CustomFontButton btnFollow;
        private final MediaPlayerFragment mParent;

        EndStreamLayout(View view, MediaPlayerFragment parent, String reason) {
            ButterKnife.bind(this, view);

            this.mParent = new WeakReference<>(parent).get();
            Timber.e("end stream layout showed - %s", reason);
            if (!reason.isEmpty())
                Answers.getInstance().logCustom(new CustomEvent("End Stream Screen")
                        .putCustomAttribute("endstreamscreen", truncateMessage(reason)));
            if (mParent != null) {
                String streamImageUrl = mParent.publisherImage;
                ImageLoaderUtil.displayUserImage(view.getContext().getApplicationContext(),
                        streamImageUrl,
                        userImage);
                ImageLoaderUtil.displayUserImage(view.getContext().getApplicationContext(),
                        streamImageUrl, ivStreamOwnerAlpha);

                if (mParent.checkStreamDetailNotnull()) {

                    if (mParent.checkNotSameUser()) {
                        btnFollow.setVisibility(View.GONE);
                    } else {
                        if (mParent.isFollowed() ||
                                (mParent.checkNotSameUser())) {
                            btnFollow.setBackground(ContextCompat.getDrawable(mParent.getContext(), R.drawable.ended_stream_following));
                            btnFollow.setText(mParent.getContext().getString(R.string.profile_dialog_follow));
                            btnFollow.setTextColor(Color.parseColor("#FF5167"));
                        } else {
                            btnFollow.setBackground(ContextCompat.getDrawable(mParent.getContext(), R.drawable.ended_stream_hot_delete_button));
                            btnFollow.setText(mParent.getContext().getString(R.string.ended_stream_follow));
                            btnFollow.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                }
                mParent.stopVideoCallIfAny();
            }
        }

        private String truncateMessage(String reason) {
            return reason.length() < 100 ? reason : reason.substring(0, 100);
        }

        @OnClick(R.id.btnFollow)
        void onFollowClicked() {
            if (!mParent.isFollowed()) {
                mParent.executeFollowUser();
            }
        }


        @OnClick(R.id.go_back_stream_end)
        void onHomeButtonClicked() {
            AppsterApplication.mAppPreferences.setIsNotNeedRefreshHome(true);
            mParent.closeStreamAndNotRefreshHome();
        }

        @OnClick(R.id.go_profile)
        void onProfileButtonClicked() {
            mParent.navigateToProfileScreen();
        }

        void updateEndStreamData(long viewsCount, int likesCount, long totalGold, long duration) {

            durationTime.setText(AppsterUtility.parseStreamingTimeToHHMM(duration));

            scoreOnStreamEnd.setText(Utils.formatThousand(viewsCount));
            likeReceivedCount.setText(Utils.formatThousand(likesCount));
            giftReceivedCount.setText(Utils.formatThousand(totalGold));
            if (duration != 0L) mParent.tvNetworkError.setVisibility(View.GONE);
        }

        void changeToFollowing(Context context) {
            if (btnFollow != null) {
                btnFollow.setBackground(ContextCompat.getDrawable(mParent.getContext(), R.drawable.ended_stream_following));
                btnFollow.setText(mParent.getContext().getString(R.string.profile_dialog_follow));
                btnFollow.setTextColor(Color.parseColor("#FF5167"));
            }
        }
    }


    //endregion

    //region luckywheel
    class LuckyWheelLayout {
        @Bind(R.id.luckyWheel)
        LuckyWheelView luckyWheel;
        @Bind(R.id.playLucky)
        ImageButton playLucky;

        List<LuckyItem> data = new ArrayList<>();
        String host = "Host,1000".toUpperCase();
        String viewer = "Viewer,1000".toUpperCase();
        String url = "https://vi.gravatar.com/userimage/30130623/8d47462c1b150c3ad6dc6f471b59161d.jpg?size=128";

        LuckyWheelLayout(View view) {

            ButterKnife.bind(this, view);
            //viewer dont have spin button
            playLucky.setVisibility(View.GONE);
            LuckyItem luckyItem1 = new LuckyItem();
            luckyItem1.text = host;
            luckyItem1.iconUrl = url;
            luckyItem1.color = Color.parseColor("#FEFFFE");
            data.add(luckyItem1);

            LuckyItem luckyItem2 = new LuckyItem();
            luckyItem2.text = viewer;
            luckyItem2.iconUrl = url;
            luckyItem2.color = Color.parseColor("#F4F5F4");
            data.add(luckyItem2);

            LuckyItem luckyItem3 = new LuckyItem();
            luckyItem3.text = host;
            luckyItem3.iconUrl = url;
            luckyItem3.color = Color.parseColor("#FEFFFE");
            data.add(luckyItem3);

            //////////////////
            LuckyItem luckyItem4 = new LuckyItem();
            luckyItem4.text = viewer;
            luckyItem4.iconUrl = url;
            luckyItem4.color = Color.parseColor("#F4F5F4");
            data.add(luckyItem4);

            LuckyItem luckyItem5 = new LuckyItem();
            luckyItem5.text = host;
            luckyItem5.iconUrl = url;
            luckyItem5.color = Color.parseColor("#FEFFFE");
            data.add(luckyItem5);

            LuckyItem luckyItem6 = new LuckyItem();
            luckyItem6.text = viewer;
            luckyItem6.iconUrl = url;
            luckyItem6.color = Color.parseColor("#F4F5F4");
            data.add(luckyItem6);
            //////////////////

//            //////////////////////
//            LuckyItem luckyItem7 = new LuckyItem();
//            luckyItem7.text = "700";
//            luckyItem7.iconUrl = R.drawable.test7;
//            luckyItem7.color = 0xffFFF3E0;
//            data.add(luckyItem7);
//
//            LuckyItem luckyItem8 = new LuckyItem();
//            luckyItem8.text = "800";
//            luckyItem8.iconUrl = R.drawable.test8;
//            luckyItem8.color = 0xffFFE0B2;
//            data.add(luckyItem8);
//
//
//            LuckyItem luckyItem9 = new LuckyItem();
//            luckyItem9.text = "900";
//            luckyItem9.iconUrl = R.drawable.test9;
//            luckyItem9.color = 0xffFFCC80;
//            data.add(luckyItem9);
//            ////////////////////////
//
//            LuckyItem luckyItem10 = new LuckyItem();
//            luckyItem10.text = "1000";
//            luckyItem10.iconUrl = R.drawable.test10;
//            luckyItem10.color = 0xffFFE0B2;
//            data.add(luckyItem10);
//
//            LuckyItem luckyItem11 = new LuckyItem();
//            luckyItem11.text = "2000";
//            luckyItem11.iconUrl = R.drawable.test10;
//            luckyItem11.color = 0xffFFE0B2;
//            data.add(luckyItem11);
//
//            LuckyItem luckyItem12 = new LuckyItem();
//            luckyItem12.text = "3000";
//            luckyItem12.iconUrl = R.drawable.test10;
//            luckyItem12.color = 0xffFFE0B2;
//            data.add(luckyItem12);
//
//            /////////////////////

            luckyWheel.setData(data);
            luckyWheel.setRound(getRandomRound());
            luckywheelVisibilityListener.onNext(true);
//            streamPreview.setVisibility(View.VISIBLE);
            luckyWheel.setLuckyRoundItemSelectedListener(index -> {
                Toast.makeText(getActivity().getApplicationContext(), String.valueOf(data.get(index).text), LENGTH_SHORT).show();
                luckywheelVisibilityListener.onNext(false);
//                Observable.just(2).delay(5,TimeUnit.SECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(integer -> {
//                            luckywheelVisibilityListener.onNext(true);
//                            vsLuckywheel.setVisibility(View.VISIBLE);
////                            streamPreview.setVisibility(View.VISIBLE);
//                        },error -> Timber.e(error.getMessage()));
            });
        }

        void startWheel(int resultIndex) {
            luckyWheel.startLuckyWheelWithTargetIndex(resultIndex);
        }


//        @OnClick(R.id.playLucky)
//        public void startWheel(){
//            int index = getRandomIndex();
//
//        }

        private int getRandomIndex() {
            Random rand = new Random();
            return rand.nextInt(data.size() - 1);
        }

        private int getRandomRound() {
            return 5;
        }

    }

    private SubStreamData mSubStreamData;

    public void notifySubStreamJoinChannelSuccess() {
        if (isFragmentUIActive()) {
            informSubStreamStatusToServer(1);
            updateAndGetSubStreamStatistic(State.CONNECTED);
//            sendToGroup(updateAndGetSubStreamStatistic(State.CONNECTED));
        }
    }

    private void informSubStreamStatusToServer(int status) {
        if (mSubSlug.isEmpty()) return;
        mCompositeSubscription.add(mService.updateSubStreamStatus(AppsterUtility.getAuth(), streamSlug, mSubSlug, status)
                .subscribe(subStreamDataBaseResponse -> Timber.e(String.valueOf(subStreamDataBaseResponse.getData()))
                        , Timber::e));
    }

    @NonNull
    private ChatItemModelClass updateAndGetSubStreamStatistic(@State int state) {
        if (mSubStreamData != null) mSubStreamData.status = state;
        return updateAndGetSubStreamStatistic(mSubStreamData);
    }

    @NonNull
    ChatItemModelClass updateAndGetSubStreamStatistic(SubStreamData subStreamData) {
        ChatItemModelClass itemModelClass = new ChatItemModelClass();
        itemModelClass.setChatDisplayName(mAppOwnerProfile.getDisplayName());
        itemModelClass.setUserName(mAppOwnerProfile.getUserName());
        itemModelClass.setUserIdSend(mAppOwnerProfile.getUserId());
        itemModelClass.setProfilePic(mAppOwnerProfile.getUserImage());
        itemModelClass.setMessageType(ChatItemModelClass.CHAT_TYPE_STATISTIC);
        itemModelClass.subStreamData = subStreamData;
        itemModelClass.setTotalLikes(countLikeCount);
        itemModelClass.setTotalViewers(countUserHaveBeenView);
        return itemModelClass;
    }

    void notifySubStreamStopped() {
        if (isFragmentUIActive()) {
//            if (userHotImage != null) {
//                userHotImage.setVisibility(View.VISIBLE);
//            }
            if (mHostSubStreamLayout != null) mHostSubStreamLayout.updateState(State.DISCONNECTING);
            informSubStreamStatusToServer(2);
            sendToGroup(updateAndGetSubStreamStatistic(State.DISCONNECTING));
            mCompositeSubscription.add(Observable.just(true).delay(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(aBoolean -> isFragmentUIActive())
                    .subscribe(aBoolean -> {
                        sendToGroup(updateAndGetSubStreamStatistic(State.DISCONNECTED));
                        updateUIStoppedSubStream();
                    }));
        }
    }

    void updateUIStoppedSubStream() {
        callSubViewObservable.onNext(false);
        mHostAlreadyEndedCall.set(false);
        mSubStreamData = null;
        mSubSlug = "";
//        mLiveInviteDialog=null;
//        if (userHotImage != null) {
//            userHotImage.setVisibility(View.GONE);
//        }
        if (mSubStreamLayout != null) mSubStreamLayout.updateState(State.DISCONNECTED);
        if (mHostSubStreamLayout != null) mHostSubStreamLayout.updateState(State.DISCONNECTED);

    }

    void notifyAudioOnly(boolean audioOnly) {
        if (isFragmentUIActive()) {
            sendToGroup(updateAndGetSubStreamStatistic(audioOnly ? State.AUDIO_ONLY : State.VIDEO_AND_AUDIO));
        }
    }

    public void sendNotifySubStreamPause() {
        if (mSubStreamData == null) return;
        informSubStreamAFKToServer(streamSlug, mSubStreamData.slug);
        sendToGroup(updateAndGetSubStreamStatistic(State.AWAY));
    }

    private void informSubStreamAFKToServer(String slug, String subStreamSlug) {
        mCompositeSubscription.add(mService.subStreamAFK(AppsterUtility.getAuth(), slug, subStreamSlug)
                .subscribe(booleanBaseResponse -> {
                    Timber.e(String.valueOf(booleanBaseResponse.getData()));
                }, Timber::e));
    }

    public void sendNotifySubStreamResume() {
        if (mSubStreamData == null) return;
        informResumeAFKToServer(streamSlug, mSubStreamData.slug);
        sendToGroup(updateAndGetSubStreamStatistic(State.CONNECTED));
    }

    private void informResumeAFKToServer(String slug, String subStreamSlug) {
        mCompositeSubscription.add(mService.subStreamResumeFromAFK(AppsterUtility.getAuth(), slug, subStreamSlug)
                .subscribe(booleanBaseResponse -> {
                    Timber.e(String.valueOf(booleanBaseResponse.getData()));
                }, Timber::e));
    }

    //region trivia winner screen
    TriviaWinnerLayout mTriviaWinnerLayout;

    private void showTriviaWinnerLayout(int winnerCount, String prizePerUserString, String message) {
        if (vsTriviaWinner.getParent() != null && mTriviaWinnerLayout == null) {
            View view = vsTriviaWinner.inflate();
            mTriviaWinnerLayout = new TriviaWinnerLayout(view, this);
            mTriviaWinnerLayout.updateView(winnerCount, prizePerUserString, message);
        }
    }

    static class TriviaWinnerLayout implements OnLoadMoreListenerRecyclerView,
            RecyclerItemCallBack<WinnerModel> {
        private final MediaPlayerFragment mParent;
        @Bind(R.id.tvNumWinner)
        CustomFontTextView tvNumWinner;
        @Bind(R.id.tvCongratsText)
        CustomFontTextView tvCongratsText;
        @Bind(R.id.tvPrizeAmount)
        CustomFontTextView tvPrizeAmount;
        @Bind(R.id.rcvWinnerList)
        LoadMoreRecyclerView rcvWinnerList;

        private WinnerListAdapter winnerListAdapter;
        List<DisplayableItem> mWinnerItems;

        TriviaWinnerLayout(View view, MediaPlayerFragment parent) {
            Timber.e("GuestSubStreamLayout init");
            ButterKnife.bind(this, view);
            this.mParent = new WeakReference<>(parent).get();
            mParent.getTriviaWinnerList();
            setAdapter();
        }

        private void setAdapter() {
            DialogManager.getInstance().dismisDialog();
            mWinnerItems = new ArrayList<>();
            winnerListAdapter = new WinnerListAdapter(new DiffCallBaseUtils(), new ArrayList<>(), this);
            rcvWinnerList.setOnLoadMoreListener(this);
            rcvWinnerList.setAdapter(winnerListAdapter);
        }

        private void checkEmptyList() {
            if (mWinnerItems != null && mWinnerItems.isEmpty()) {
                rcvWinnerList.setVisibility(View.GONE);
            }
        }

        void showTriviaWinnerList(List<DisplayableItem> winnerList) {
            if (winnerList != null && !winnerList.isEmpty()) {
                mWinnerItems.addAll(winnerList);
                winnerListAdapter.updateItems(mWinnerItems);
            }
            rcvWinnerList.setLoading(false);
            checkEmptyList();
        }

        void updateView(int winnerCount, String prizePerUserString, String message) {
            if (tvNumWinner != null) {
                tvNumWinner.setText(String.format(mParent.isVNTrivia() ? mParent.getString(R.string.trivia_winners_number_vi) : mParent.getString(R.string.trivia_winners_number), winnerCount));
            }

            if (tvCongratsText != null) {
                tvCongratsText.setText(message);
            }

            if (tvPrizeAmount != null) {
                tvPrizeAmount.setText(prizePerUserString);
            }
        }

        void getTriviaWinnerListError() {
            rcvWinnerList.setLoading(false);
        }

        @Override
        public void onLoadMore() {
            if (mParent != null) {
                mParent.getTriviaWinnerList();
            }
        }

        @Override
        public void onItemClicked(WinnerModel item, int position) {
            if (mParent != null) {
                mParent.showUserProfileDialog(item.getUserName(), item.getUserAvatar());
            }
        }
    }

    void getTriviaWinnerList() {
        if (mIsEndWinnerList) return;
        mCompositeSubscription.add(mTriviaWinnerListUseCase.execute(TriviaWinnerListUseCase.Params.byType(mIndexWinnerList, Constants.PAGE_LIMITED, mCurrentTriviaModel.triviaId))
                .subscribe(basePagingModel -> {
                    if (mTriviaWinnerLayout != null) {
                        mIsEndWinnerList = basePagingModel.isEnd;
                        mIndexWinnerList = basePagingModel.nextId;
                        mTriviaWinnerLayout.showTriviaWinnerList(basePagingModel.data);
                    }
                }, error -> {
                    if (getActivity() != null) {
                        ((BaseActivity) getActivity()).handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                    }
                    if (mTriviaWinnerLayout != null) {
                        mTriviaWinnerLayout.getTriviaWinnerListError();
                    }
                }));
    }

    //endregion
    //region sub streaming screen
    static class GuestSubStreamLayout implements GuestSubStreamStatusView.OnClickListener {
        //        @Bind(R.id.subCameraPreview)
//        GLSurfaceView mSubCameraView;
        @Bind(R.id.subStreamStatus)
        GuestSubStreamStatusView subStreamStatus;
        final MediaPlayerFragment mParent;
        private KSYAgoraStreamer mStreamer;
        private boolean mVideoEncoderUnsupported;
        private KSYAgoraStreamer.OnInfoListener mOnInfoListener;
        private KSYAgoraStreamer.OnErrorListener mOnErrorListener;
        private StatsLogReport.OnLogEventListener mOnLogEventListener;
        private KSYAgoraStreamer.OnRTCInfoListener mOnRTCInfoListener;

        private Handler mMainHandler;
        boolean isStreamReady = false;
        AtomicInteger mStreamTimeOutCounter = new AtomicInteger();
        private Toast mToast;
        private boolean mStreaming;
        private String mSlug = "";
        private boolean mIsEndStream;
        private boolean mHWEncoderUnsupported;
        private boolean mSWEncoderUnsupported;
        private boolean mIsCaling = false;
        private DialogbeLiveConfirmation mLiveEndConfirmation;
        private DialogbeLiveConfirmation mLeaveStreamDuringCallConfirmation;

        View root;

        public GuestSubStreamLayout(View view, MediaPlayerFragment parent) {
            Timber.e("GuestSubStreamLayout init");
            ButterKnife.bind(this, view);
            root = view;
            this.mParent = new WeakReference<>(parent).get();
//            mSubCameraView.setEGLConfigChooser(8,8,8,8,16,0);
//            mSubCameraView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            subStreamStatus.setListener(this);
            initKsyStreamerCallback();
            initKsyStreamer();
        }

        boolean mEnableStatusView = true;


        public void onSubCameraClicked() {
            if (subStreamStatus != null) subStreamStatus.showScreenOptions();
        }

        public void setGuestAvatar(String avatarUrl) {
            if (subStreamStatus != null) subStreamStatus.setGuestAvatar(avatarUrl);
        }

        public void setGuestDisplayName(String displayName) {
            if (subStreamStatus != null) subStreamStatus.setGuestName(displayName);
        }

        public View getView() {
            return root;
        }

        private void initKsyStreamer() {
            mStreamer = new KSYAgoraStreamer(mParent.getContext());
            mStreamer.initAgoraRTC();
            mStreamer.setHost(false);
            //audio
            mStreamer.setAudioSampleRate(48000);
            mStreamer.setAudioKBitrate(64);
            mStreamer.setAudioChannels(2);//1: mono, 2: stereo
            mStreamer.setVoiceVolume(1f);
            mStreamer.setEnableAudioMix(false);
            mStreamer.setEnableAudioPreview(false);

            mStreamer.setMuteAudio(false);
            mStreamer.setEnableAudioPreview(false);

            //video
            mStreamer.setPreviewFps(15);
            mStreamer.setTargetFps(15);
            mStreamer.setVideoKBitrate(600, 600, 400);//init, max, min --- init should be 3/4 of max, min should be 1/4 of max.
//        mStreamer.setVideoKBitrate(400)
            int videoResolution = StreamerConstants.VIDEO_RESOLUTION_360P;
            mStreamer.setPreviewResolution(videoResolution);
            mStreamer.setTargetResolution(videoResolution);
            mStreamer.setVideoCodecId(AVConst.CODEC_ID_AVC);//h264
            mStreamer.setFrontCameraMirror(false);

            mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_HARDWARE);

            mStreamer.setDisplayPreview(mParent.mSubCameraView);

            mStreamer.setEnableAutoRestart(true, RETRY_DELAY_MILLIS);

            mStreamer.setOnInfoListener(mOnInfoListener);
            mStreamer.setOnErrorListener(mOnErrorListener);
            mStreamer.setOnLogEventListener(mOnLogEventListener);
            mStreamer.setOnRTCInfoListener(mOnRTCInfoListener);


            CameraTouchHelper cameraTouchHelper = new CameraTouchHelper();
            cameraTouchHelper.setEnableTouchFocus(false);
            cameraTouchHelper.setEnableZoom(false);
            cameraTouchHelper.setCameraCapture(mStreamer.getCameraCapture());
//            cameraTouchHelper.addTouchListener(mOnCameraPreviewTouchListener);
            cameraTouchHelper.setCameraHintView(mParent.mCameraHintView);
            mParent.mCameraHintView.setOnTouchListener(cameraTouchHelper);
            mParent.periscope.setClickable(false);
            //for sub screen
            cameraTouchHelper.addTouchListener(mSubScreenTouchListener);
        }

        void setDisplayCamera(GLSurfaceView displayCamera) {
            mStreamer.setDisplayPreview(displayCamera);

        }

        //#region ksy library ==========================================================================
        private void initKsyStreamerCallback() {
            mMainHandler = new Handler();
            mOnInfoListener = (what, msg1, msg2) -> {
                switch (what) {
                    case StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE:
                        Timber.d("KSY_STREAMER_CAMERA_INIT_DONE");
                        setCameraAntiBanding50Hz();

                        break;
                    case StreamerConstants.KSY_STREAMER_OPEN_STREAM_SUCCESS:
                        Timber.d("KSY_STREAMER_OPEN_STREAM_SUCCESS");
                        isStreamReady = true;
                        mVideoEncoderUnsupported = false;
                        mStreamTimeOutCounter.set(0);
                        mParent.notifySubStreamJoinChannelSuccess();
//                        mStreamPresenter.startStream();
//                        mStreamPresenter.sendNotifyStreamResume();
                        break;

                    case StreamerConstants.KSY_STREAMER_OPEN_FILE_SUCCESS:
                        Timber.d("KSY_STREAMER_OPEN_FILE_SUCCESS");
                        break;
                    case StreamerConstants.KSY_STREAMER_FRAME_SEND_SLOW:
                        Timber.d("KSY_STREAMER_FRAME_SEND_SLOW %d ms", msg1);
//                        showToast(getString(R.string.message_network_not_good), Toast.LENGTH_SHORT);
                        break;
                    case StreamerConstants.KSY_STREAMER_EST_BW_RAISE:
                        Timber.d("BW raise to " + msg1 / 1000 + "kbps");
                        break;
                    case StreamerConstants.KSY_STREAMER_EST_BW_DROP:
                        Timber.d("BW drop to " + msg1 / 1000 + "kpbs");
                        break;
                    default:
                        Timber.d("OnInfo: " + what + " msg1: " + msg1 + " msg2: " + msg2);
                        break;
                }
            };

            mOnErrorListener = (what, msg1, msg2) -> {
                switch (what) {
                    case StreamerConstants.KSY_STREAMER_ERROR_DNS_PARSE_FAILED:
                        Timber.d("KSY_STREAMER_ERROR_DNS_PARSE_FAILED");
                        break;
                    case StreamerConstants.KSY_STREAMER_ERROR_CONNECT_FAILED:
                        Timber.d("KSY_STREAMER_ERROR_CONNECT_FAILED");
                        break;
                    case KSY_STREAMER_ERROR_PUBLISH_FAILED:
                        Timber.d("KSY_STREAMER_ERROR_PUBLISH_FAILED");
//                        publishFailedObservable.onNext(KSY_STREAMER_ERROR_PUBLISH_FAILED);
                        break;
                    case StreamerConstants.KSY_STREAMER_ERROR_CONNECT_BREAKED:
                        Timber.d("KSY_STREAMER_ERROR_CONNECT_BREAKED");
                        break;
                    case StreamerConstants.KSY_STREAMER_ERROR_AV_ASYNC:
                        Timber.d("KSY_STREAMER_ERROR_AV_ASYNC " + msg1 + "ms");
                        break;
                    case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:
                        Timber.d("KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED");
                        mVideoEncoderUnsupported = true;
                        break;
                    case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN:
                        Timber.d("KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN");
                        break;
                    case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNSUPPORTED:
                        Timber.d("KSY_STREAMER_AUDIO_ENCODER_ERROR_UNSUPPORTED");
                        break;
                    case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNKNOWN:
                        Timber.d("KSY_STREAMER_AUDIO_ENCODER_ERROR_UNKNOWN");
                        break;
                    case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                        Timber.d("KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED");
                        showToast("Audio recorder start failed, please ensure permission is granted", Toast.LENGTH_LONG);
                        break;
                    case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                        Timber.d("KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN");
                        break;
                    case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                        Timber.d("KSY_STREAMER_CAMERA_ERROR_UNKNOWN");
                        break;
                    case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                        Timber.d("KSY_STREAMER_CAMERA_ERROR_START_FAILED");
                        break;
                    case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                        Timber.d("KSY_STREAMER_CAMERA_ERROR_SERVER_DIED");
                        break;
                    //Camera was disconnected due to use by higher priority user.
                    case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_EVICTED:
                        Timber.d("KSY_STREAMER_CAMERA_ERROR_EVICTED");
                        break;
                    default:
                        Timber.d("what=" + what + " msg1=" + msg1 + " msg2=" + msg2);
                        break;
                }
                switch (what) {
                    case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                    case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                    case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                    case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                        break;
                    case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                        if (mStreamer != null) mStreamer.stopCameraPreview();
                        mMainHandler.postDelayed(this::startCameraPreviewWithPermCheck, 5000);
                        break;
                    case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_CLOSE_FAILED:
                    case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_ERROR_UNKNOWN:
                    case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_OPEN_FAILED:
                    case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_FORMAT_NOT_SUPPORTED:
                    case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_WRITE_FAILED:
                        stopRecord();
                        break;
                    case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:
                    case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN:
                        handleEncodeError();
                        stopStream("KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN - switch encoder");
                        mMainHandler.postDelayed(() -> startStream(mSlug), RETRY_DELAY_MILLIS);
                        break;
                    default:
                        if (mStreamer != null && !mStreamer.getEnableAutoRestart()) {
                            stopStream(String.format(Locale.US, "KSY_ERROR %d - %d - %d", what, msg1, msg2));
                            mMainHandler.postDelayed(() -> startStream(mSlug), RETRY_DELAY_MILLIS);
                        }
                        break;
                }
            };


            mOnLogEventListener = singleLogContent -> Timber.i("***onLogEvent : %s", singleLogContent.toString());

            mOnRTCInfoListener = new KSYAgoraStreamer.OnRTCInfoListener() {
                @Override
                public void onRTCMediaEvent(int event, Object... data) {

                    switch (event) {
                        case MediaManager.MediaUiHandler.USER_JOINED: {
                            Timber.e("MediaManager.MediaUiHandler.USER_JOINED");
                            break;
                        }

                        case MediaManager.MediaUiHandler.JOIN_CHANNEL_RESULT: {

                            boolean success = (Boolean) data[0];
                            Timber.e("MediaManager.MediaUiHandler.JOIN_CHANNEL_RESULT %s", success);
                            if (success) {
                                mMainHandler.post(() -> {
                                    if (BuildConfig.DEBUG)
                                        showToast("join channel success", Toast.LENGTH_LONG);
                                    if (mParent != null)
                                        mParent.notifySubStreamJoinChannelSuccess();
                                });
                            } else {

                            }

                            break;
                        }

                        case MediaManager.MediaUiHandler.FIRST_FRAME_DECODED: {
                            //收到辅播数据后，设置辅播画面为大窗口
                            Timber.e("onFirstRemoteVideoDecoded " + Arrays.toString(data));
                            mMainHandler.post(() -> {
                                if (mParent != null) mParent.callSubViewObservable.onNext(true);
                                if (subStreamStatus != null) subStreamStatus.streamStarted();
                            });
                            break;
                        }

                        case MediaManager.MediaUiHandler.LEAVE_CHANNEL: {
                            // temporarily only one remote stream supported, so reset uid here
                            Timber.e("MediaManager.MediaUiHandler.LEAVE_CHANNEL");
                            mMainHandler.post(() -> {
                                if (BuildConfig.DEBUG)
                                    showToast("leave channel success", Toast.LENGTH_LONG);
//                                if(mParent!=null) mParent.callSubViewObservable.onNext(false);
                            });
                            break;
                        }

                        case MediaManager.MediaUiHandler.USER_OFFLINE: {
                            //辅播断开后，设置主播画面为大窗口
                            Timber.e("MediaManager.MediaUiHandler.USER_OFFLINE");
                            mMainHandler.post(() -> {
                                Timber.e("Host disconnected thread id - %d", Thread.currentThread().getId());
                                notifyCallEndedState();
                            });
                            break;
                        }

                        case MediaManager.MediaUiHandler.ERROR: {
                            int errorCode = (Integer) data[0];
                            Timber.e("MediaManager.MediaUiHandler.ERROR %d", errorCode);
                            mMainHandler.post(() -> {
                                if (BuildConfig.DEBUG)
                                    showToast(String.format("Agora error  %d", errorCode), Toast.LENGTH_LONG);
                            });
                            if (errorCode == IRtcEngineEventHandler.ErrorCode.ERR_INVALID_APP_ID) {

                            } else {

                            }
                        }
                    }
                }
            };
        }

        void notifyCallEndedState() {
            if (mParent != null && mParent.isFragmentUIActive() && !mParent.mHostAlreadyEndedCall.get()) {
                stopStream("");
                mParent.notifySubStreamStopped();
                if (subStreamStatus != null)
                    subStreamStatus.updateState(State.DISCONNECTING);
            }
        }

        public void onStart() {

        }

        public void onResume() {
            if (mStreamer != null && mParent != null) {
                mStreamer.setDisplayPreview(mParent.mSubCameraView);
                mStreamer.onResume();
                mStreamer.setUseDummyAudioCapture(false);
                startCameraPreviewWithPermCheck();
            }
            if (mIsCaling && mParent != null)
                mParent.sendNotifySubStreamResume();
        }

        public void onStop() {
            if (mStreamer != null) {
                mStreamer.onPause();
                mStreamer.setUseDummyAudioCapture(true);
                mStreamer.stopCameraPreview();
            }

            if (mIsCaling && mParent != null)
                mParent.sendNotifySubStreamPause();
        }

        public void onDestroy() {
            releaseKSYResources();
        }

        public boolean isAbleToLeaveStream() {
            if (mIsCaling) {
                mLeaveStreamDuringCallConfirmation = new DialogbeLiveConfirmation.Builder()
                        .title("End Call")
                        .message("Are you sure you want to disconnect from this call?")
                        .onConfirmClicked(() -> {
                            stopVideoCall();
                        })
                        .build();
                mLeaveStreamDuringCallConfirmation.show(mParent.getContext());
                return false;
            }
            return true;
        }

        public void stopVideoCall() {
            stopStream("");
            if (mParent != null) {
                mParent.notifySubStreamStopped();
                mParent.onBackPress();
            }
        }

        void releaseKSYResources() {
            //ksy library
            if (mMainHandler != null) {
                mMainHandler.removeCallbacksAndMessages(null);
                mMainHandler = null;
            }
            if (mStreamer != null) {
                mStreamer.onPause();
                mStreamer.stopCameraPreview();
            }

            if (mIsCaling) stopRTC();
            if (mStreamer != null) {
                mStreamer.setOnRTCInfoListener(null);
                mStreamer.release();
                mStreamer = null;
            }
        }

        private void stopRecord() {
            mStreamer.stopRecord();
        }

        private void handleEncodeError() {
            int encodeMethod = mStreamer.getVideoEncodeMethod();
            if (encodeMethod == StreamerConstants.ENCODE_METHOD_HARDWARE) {
                mHWEncoderUnsupported = true;
                if (mSWEncoderUnsupported) {
                    mStreamer.setEncodeMethod(
                            StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT);
                    Log.e(TAG, "Got HW encoder error, switch to SOFTWARE_COMPAT mode");
                } else {
                    mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE);
                    Log.e(TAG, "Got HW encoder error, switch to SOFTWARE mode");
                }
            } else if (encodeMethod == StreamerConstants.ENCODE_METHOD_SOFTWARE) {
                mSWEncoderUnsupported = true;
                if (mHWEncoderUnsupported) {
                    mStreamer.setEncodeMethod(
                            StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT);
                    Log.e(TAG, "Got SW encoder error, switch to SOFTWARE_COMPAT mode");
                } else {
                    mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_HARDWARE);
                    Log.e(TAG, "Got SW encoder error, switch to HARDWARE mode");
                }
            }
        }

        void startStream(String slug) {
            if (mStreamer == null || slug == null || slug.isEmpty() || mIsCaling) return;
            this.mSlug = slug;
            try {
//                String url = "rtmp://" +
//                        BuildConfig.WOWZA_HOST_IP +
//                        ":1935/" +
//                        WowzaConstant.APPLICATION_NAME_RECORDING +
//                        "/" +
//                        slug;
//                mStreamer.setUrl(url);        //rtsp://stgwowza.view.belive.sg:1935/Appsters_recording/7065ca0ff7e444db8ac8859e4335a66f
//                boolean streamIsPublish = mStreamer.startStream();
                mStreaming = true;
                mIsCaling = true;

//                Timber.e("streamIsPublish %s", streamIsPublish);
//                displayStreamUploadInfo();
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        //region agora rtc
        private void onRTCCallHandle(String channel) {
            if (mIsCaling) {
                stopRTC();
            } else {
                startRTC(channel);
            }
        }

        private void startRTC(String channel) {
            if (mStreamer != null) {
//                mStreamer.showRemoteView(false);
                if (mStreamer.getCameraFacing() != 1) mStreamer.switchCamera();
                mStreamer.setRemoteMirror(true);
                mStreamer.setRTCSubScreenRect(GUEST_SCREEN_GUEST_CAM_LEFT_BEGIN, GUEST_SCREEN_GUEST_CAM_TOP_BEGIN, GUEST_SCREEN_GUEST_CAM_WIDTH, GUEST_SCREEN_GUEST_CAM_HEIGHT, SCALING_MODE_CENTER_CROP);
                mStreamer.setRTCMainScreen(KSYAgoraStreamer.RTC_MAIN_SCREEN_REMOTE);

                mStreamer.startRTC(channel);
                mIsCaling = true;
            }
        }

        private void stopRTC() {
            if (mStreamer != null && mIsCaling) {
                mStreamer.stopRTC();
                mIsCaling = false;
            }
        }

        //endregion

        private void stopStream(String reason) {
            stopRTC();
            if (!mIsEndStream && !mVideoEncoderUnsupported) {
                mStreaming = false;
                mIsEndStream = true;
                isStreamReady = false;
                mSlug = "";
                if (mStreamer != null) {
                    mStreamer.stopCameraPreview();
                }
            }

//            mStreamer.stopStream();

        }

        private void setCameraAntiBanding50Hz() {
            Camera.Parameters parameters = mStreamer.getCameraCapture().getCameraParameters();
            if (parameters != null) {
                parameters.setAntibanding(Camera.Parameters.ANTIBANDING_50HZ);
                mStreamer.getCameraCapture().setCameraParameters(parameters);
            }
        }

        void showToast(String msg, int duration) {
            if (mToast != null) {
                mToast.cancel();
            }
            final Context context = mParent.getContext();
            if (context != null) {
                mToast = Toast.makeText(context, msg, duration);
                mToast.show();
            }

        }

        void startCameraPreviewWithPermCheck() {
//            if (isFinishing() || isDestroyed()) {
//                return;
//            }
            final Context context = mParent.getContext();
            if (context == null) return;
            int cameraPerm = PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA);
            int audioPerm = PermissionChecker.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
            int readStoragePerm = PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (cameraPerm != PackageManager.PERMISSION_GRANTED ||
                    audioPerm != PackageManager.PERMISSION_GRANTED ||
                    readStoragePerm != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Log.e(TAG, "No CAMERA or AudioRecord or Storage permission, please check");
                    showToast("No CAMERA or AudioRecord or Storage permission, please check",
                            Toast.LENGTH_LONG);
                } else {
                    String[] permissions = {Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE};
                    mParent.requestPermissions(permissions, PERMISSION_REQUEST_CAMERA_AUDIOREC);
                }
            } else {
                if (mStreamer != null) {
                    mStreamer.startCameraPreview();

                }
            }
        }

        public void startCameraPreview() {
            if (mStreamer != null) mStreamer.startCameraPreview();
            if (mStreaming && !mSlug.isEmpty()) startStream(mSlug);
        }

        public void stopCameraPreview() {
            if (mStreamer != null) mStreamer.stopCameraPreview();
        }

        @Override
        public void onSwitchCamClicked() {
            if (mStreamer != null) {
                mStreamer.switchCamera();
                updateFrontMirror();
            }
        }

        private void updateFrontMirror() {
            boolean isFrontCamera = !mStreamer.isFrontCamera();
            mStreamer.setRemoteMirror(isFrontCamera);
        }

        @Override
        public void onAudioOnlyChecked(boolean audioOnly) {
            if (mStreamer != null) mStreamer.setAudioOnly(audioOnly);
            if (mParent != null) mParent.notifyAudioOnly(audioOnly);
        }

        @Override
        public void onCountDownCompleted() {
            if (!mSlug.isEmpty()) startRTC(mSlug);
        }

        @Override
        public void onEndCallClicked() {
            mLiveEndConfirmation = new DialogbeLiveConfirmation.Builder()
                    .title(getView().getContext().getString(R.string.calling_disconnect_title))
                    .message("Are you sure you want to disconnect with the host?")
                    .onConfirmClicked(this::notifyCallEndedState)
                    .build();
            mLiveEndConfirmation.show(mParent.getContext());

        }

        @Override
        public void onGuestDisplayNameClicked() {
            if (mParent != null) mParent.navigateToGuestProfile();
        }

        @Override
        public void onShowGuestProfile() {
            if (mParent != null) mParent.navigateToGuestProfile();
        }

        void updateState(@State int status) {
            if (status == State.DISCONNECTING) {
                stopStream("host end substream or guest diconnected");
                if (mLiveEndConfirmation != null) mLiveEndConfirmation.dismiss();
//                if (mParent != null) mParent.updateUIStoppedSubStream();
            }
//            else {
            if (subStreamStatus != null) subStreamStatus.updateState(status);
//            }
        }


//        private CameraTouchHelper.OnTouchListener mOnCameraPreviewTouchListener = new CameraTouchHelper.OnTouchListener() {
//
//
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                int action = MotionEventCompat.getActionMasked(motionEvent);
//                if (MotionEvent.ACTION_DOWN == action) {
//                    mHasActionPointerDown = false;
//                    startClickTime = System.currentTimeMillis();
//                    Timber.d("mOnCameraPreviewTouchListener ACTION_DOWN");
//                } else if (MotionEvent.ACTION_POINTER_DOWN == action) {
//                    mHasActionPointerDown = true;
//                    Timber.d("mOnCameraPreviewTouchListener ACTION_POINTER_DOWN");
//                } else if (MotionEvent.ACTION_UP == action && !mHasActionPointerDown) {
//                    long clickDuration = System.currentTimeMillis() - startClickTime;
//                    if (clickDuration < MAX_CLICK_DURATION && !isSubScreenArea(motionEvent.getX(), motionEvent.getY(), left, right,
//                            top, bottom)) {
//                        if (mParent != null) mParent.onScreenClicked(view);
//                    }
//                    Timber.d("mOnCameraPreviewTouchListener MotionEvent.ACTION_UP == action && !mHasActionPointerDown %s", clickDuration);
//                }
//                return false;
//            }
//        };

        /***********************************
         * for sub move&switch
         ********************************/
        private float mSubTouchStartX;
        private float mSubTouchStartY;
        private float mLastRawX;
        private float mLastRawY;
        private float mLastX;
        private float mLastY;
        private float mSubMaxX = 0;   //小窗可移动的最大X轴距离
        private float mSubMaxY = 0;  //小窗可以移动的最大Y轴距离
        private boolean mIsSubMoved = false;  //小窗是否移动过了，如果移动过了，ACTION_UP时不触发大小窗内容切换
        private int SUB_TOUCH_MOVE_MARGIN = 30;  //触发移动的最小距离

        private CameraTouchHelper.OnTouchListener mSubScreenTouchListener = new CameraTouchHelper.OnTouchListener() {
            private static final int MAX_CLICK_DURATION = 200;
            private long startClickTime;
            boolean mHasActionPointerDown;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //获取相对屏幕的坐标，即以屏幕左上角为原点
                mLastRawX = event.getRawX();
                mLastRawY = event.getRawY();
                // 预览区域的大小
                int width = view.getWidth();
                int height = view.getHeight();
                //小窗的位置信息
                RectF subRect = mStreamer != null ? mStreamer.getRTCSubScreenRect() : null;
                if (subRect == null) return true;
                int left = (int) (subRect.left * width);
                int right = (int) (subRect.right * width);
                int top = (int) (subRect.top * height);
                int bottom = (int) (subRect.bottom * height);
                int subWidth = right - left;
                int subHeight = bottom - top;


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHasActionPointerDown = false;
                        startClickTime = System.currentTimeMillis();
                        Timber.d("mOnCameraPreviewTouchListener ACTION_DOWN");
                        //只有在小屏区域才触发位置改变
                        if (isSubScreenArea(event.getX(), event.getY(), left, right, top, bottom)) {
                            //获取相对sub区域的坐标，即以sub左上角为原点
                            mSubTouchStartX = event.getX() - left;
                            mSubTouchStartY = event.getY() - top;
                            mLastX = event.getX();
                            mLastY = event.getY();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mHasActionPointerDown = true;
                        Timber.d("mOnCameraPreviewTouchListener ACTION_POINTER_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) Math.abs(event.getX() - mLastX);
                        int moveY = (int) Math.abs(event.getY() - mLastY);
                        if (mSubTouchStartX > 0f && mSubTouchStartY > 0f && (
                                (moveX > SUB_TOUCH_MOVE_MARGIN) ||
                                        (moveY > SUB_TOUCH_MOVE_MARGIN))) {
                            //触发移动
                            mIsSubMoved = true;
//                        updateSubPosition(width, height, subWidth, subHeight);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        boolean isSubScreenArea = isSubScreenArea(event.getX(), event.getY(), left, right,
                                top, bottom);
                        if (!mHasActionPointerDown && !isSubScreenArea) {
                            long clickDuration = System.currentTimeMillis() - startClickTime;
                            if (clickDuration < MAX_CLICK_DURATION) {
                                if (mParent != null) mParent.onScreenClicked(view);
                            }
                        }
                        //未移动并且在小窗区域，则触发大小窗切换
                        if (!mIsSubMoved && isSubScreenArea) {
                            if (mParent != null) mParent.onScreenClicked(view);
//                            if (subStreamStatus != null) subStreamStatus.showScreenOptions();
                        }

                        mIsSubMoved = false;
                        mSubTouchStartX = 0f;
                        mSubTouchStartY = 0f;
                        mLastX = 0f;
                        mLastY = 0f;
                        break;
                }

                return true;
            }
        };


        /**
         * check is sub screen moving
         *
         * @param x      sub screen top left x position
         * @param y      sub screen top left y position
         * @param left   sub screen left moved base on x
         * @param right  sub screen right moved base on x
         * @param top    sub screen left moved base on y
         * @param bottom sub screen right moved base on x
         * @return
         */
        boolean isSubScreenArea(float x, float y, int left, int right, int top, int bottom) {
            if (!mStreamer.isRemoteConnected()) {
                return false;
            }

            if (x > left && x < right &&
                    y > top && y < bottom) {
                return true;
            }

            return false;
        }

        /**
         * sub screen moving
         *
         * @param screenWidth parent width
         * @param sceenHeight parent height
         * @param subWidth    sub width
         * @param subHeight   sub height
         */
        private void updateSubPosition(int screenWidth, int sceenHeight, int subWidth, int subHeight) {
            mSubMaxX = screenWidth - subWidth;
            mSubMaxY = sceenHeight - subHeight;

            //update floating params
            float newX = (mLastRawX - mSubTouchStartX);
            float newY = (mLastRawY - mSubTouchStartY);

            //cannot move out of screen
            if (newX < 0) {
                newX = 0;
            }

            if (newY < 0) {
                newY = 0;
            }

            //cannot move out of screen -> bottom
            if (newX > mSubMaxX) {
                newX = mSubMaxX;
            }

            if (newY > mSubMaxY) {
                newY = mSubMaxY;
            }
            //sub screen with & height not updating
            RectF subRect = mStreamer.getRTCSubScreenRect();
            float width = subRect.width();
            float height = subRect.height();

            float left = newX / screenWidth;
            float top = newY / sceenHeight;

            mStreamer.setRTCSubScreenRect(left, top, width, height,
                    SCALING_MODE_CENTER_CROP);
        }
    }

    static class HostSubStreamLayout implements HostSubStreamStatusView.OnClickListener {
        final MediaPlayerFragment mParent;
        @Bind(R.id.hostSubStreamStatus)
        HostSubStreamStatusView hostSubStreamStatus;
        View root;
        private String mGuestAvatar;

        public HostSubStreamLayout(View view, MediaPlayerFragment parent) {
            Timber.e("GuestSubStreamLayout init");
            ButterKnife.bind(this, view);
            root = view;
            this.mParent = new WeakReference<>(parent).get();
            hostSubStreamStatus.setListener(this);
        }

        public View getView() {
            return root;
        }

        @Override
        public void onHostGradientViewClicked() {
            if (mParent != null) mParent.showHostProfile();
        }

        public void updateState(@State int status) {
            if (hostSubStreamStatus != null) hostSubStreamStatus.updateState(status);
        }

        void setGuestAvatar(String avatarUrl) {
            if (hostSubStreamStatus != null) hostSubStreamStatus.setHostAvatarUrl(avatarUrl);
        }
    }

    void navigateToGuestProfile() {
        if (mSubStreamData != null && mSubStreamData.receiver != null)
            showUserProfileDialog(mSubStreamData.receiver.userName, mSubStreamData.receiver.userImage);
    }


    //endregion
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MaintenanceModel model) {
        handleMaintenanceMessage(model);
    }

    void handleMaintenanceMessage(MaintenanceModel model) {
        if (model != null && !isRecorded && isFragmentUIActive()) {
            TextView tvMaintenanceMessage = (TextView) fragmentRootView.findViewById(R.id.tvMaintenanceMessage);
            switch (model.maintenanceMode) {

                case Constants.MAINTENANCE_MODE_STOP:
                    tvMaintenanceMessage.setVisibility(View.GONE);
                    break;
                case Constants.MAINTENANCE_MODE_STANDBY:
                    tvMaintenanceMessage.setText(model.message);
                    tvMaintenanceMessage.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    public void closeStreamWhenNewStreamNotification() {
        Utils.hideSoftKeyboard(getActivity());
        mIntervalSubject.onNext(-1L);
        setDatainish();
        releaseBotUsers();
        mediaPlayerControl = null;
    }


    //region trivia

    TriviaInfoUseCase mTriviaInfoUseCase;
    TriviaResultUseCase mTriviaResultUseCase;
    TriviaAnswerUseCase mTriviaAnswerUseCase;
    TriviaFinishUseCase mTriviaFinishUseCase;
    TriviaCheckReviveUseCase mTriviaCheckReviseUseCase;
    TriviaUseReviveUseCase mTriviaUseReviveUseCase;
    TriviaWinnerListUseCase mTriviaWinnerListUseCase;
    TriviaQuestionUseCase mTriviaQuestionUseCase;
    SparseIntArray mTriviaGameStateMap;
    TriviaInfoModel mCurrentTriviaModel;
    protected TriviaInfoModel.Questions mCurrentTriviaQuestion;
    protected CountDownTimer mTriviaOnOffFaceTimer;
    protected CountDownTimer mTriviaGetQuestionCountTime;
    private int mWaitingSecForRevive = 3; //default is 3

    public void onTriviaInfoReceived(TriviaInfoModel triviaInfoModel) {
        startOnOffFaceTimerCountDown(triviaInfoModel.secsToBegin);
        startGetTriviaQuestionApiTimerCountDown(triviaInfoModel.secsToGetTriviaQuestionsApi, triviaInfoModel.triviaId);
        displayTriviaExtraActions();
        mWaitingSecForRevive = triviaInfoModel.reviveWaitingTime;
        if (isVNTrivia()) tvReviveCountGuide.setText(getString(R.string.trivia_get_revives_vi));
    }

    private void displayTriviaExtraActions() {
        clTriviaExtraActionsContainer.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btnTriviaTopWinner)
    public void onTopWinnerClicked() {
        if (mTriviaRankingDialog == null) {
            mTriviaRankingDialog = TriviaRankingDialog.newInstance(mCurrentTriviaModel != null ? mCurrentTriviaModel.countryCode : "");
            mTriviaRankingDialog.setRecyclerItemCallBack((item, position) -> showUserProfileDialog(item.getUserName(), item.getUserAvatar()));
        }
        mTriviaRankingDialog.show(getChildFragmentManager(), TriviaRankingDialog.class.getSimpleName());
    }

    @OnClick(R.id.btnTriviaHowToPlay)
    public void onHowToPlayClicked(View view) {
        AppsterUtility.temporaryLockView(view);
        if (mTriviaHowToPlayDialog == null) {
            Timber.e("triviaCountryCode =%s", mCurrentTriviaModel.countryCode);
            mTriviaHowToPlayDialog = TriviaHowToPlayDialog.newInstance(mCurrentTriviaModel != null ? mCurrentTriviaModel.countryCode : "");
        }
        mTriviaHowToPlayDialog.show(getChildFragmentManager(), TriviaHowToPlayDialog.class.getSimpleName());
    }

    @OnClick(R.id.triviaReviveView)
    public void onReviveCountClicked(View view) {
//        triviaReviveView.increaseReviveWithAnimation();
        AppsterUtility.temporaryLockView(view);
        if (tvReviveCountGuide.getVisibility() != View.GONE)
            tvReviveCountGuide.setVisibility(View.GONE);
        showTriviaUsageDialog();
    }

    private void showTriviaUsageDialog() {
        if (mTriviaReviveUsageDialog == null) {
            mTriviaReviveUsageDialog = TriviaReviveUsageDialog.newInstance(mCurrentTriviaModel != null ? mCurrentTriviaModel.countryCode : "");
        }

        mTriviaReviveUsageDialog.show(getChildFragmentManager(), TriviaReviveUsageDialog.class.getSimpleName());
    }

    @OnClick(R.id.tvReviveCountGuide)
    public void onReviveGuideClicked(View view) {
        view.setVisibility(View.GONE);
        showTriviaUsageDialog();
    }


    private void startOnOffFaceTimerCountDown(int countDownSecs) {
        if (mTriviaOnOffFaceTimer != null) mTriviaOnOffFaceTimer.cancel();
        mTriviaOnOffFaceTimer = new CountDownTimer(countDownSecs * 1000, 500) {

            @Override
            public void onTick(long l) {
                int minutes = (int) TimeUnit.SECONDS.toMinutes((int) (l / 1000));
                Timber.e("onTick %d", minutes);
                //the last 60s is not able to send gift
                if (minutes == 0 && mIsAbleToSendGift) {
                    mIsAbleToSendGift = false;
                    if (sendGift != null) {
                        sendGift.dimissDialog();
                    }
                }
            }

            @Override
            public void onFinish() {
                triviaGameStarted();
            }
        }.start();
    }

    private void startGetTriviaQuestionApiTimerCountDown(int countDownSecs, int triviaId) {
        if (countDownSecs <= 0) {
            getTriviaQuestion(triviaId);
            return;
        }
        if (mTriviaGetQuestionCountTime != null) mTriviaGetQuestionCountTime.cancel();
        mTriviaGetQuestionCountTime = new CountDownTimer(countDownSecs * 1000, 500) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                getTriviaQuestion(triviaId);
            }
        }.start();
    }

    private void releaseTriviaPlayer() {
        if (mTriviaView != null) {
            mTriviaView.releaseAllPlayer();
        }
    }

    public void displayTriviaQuestion(TriviaInfoModel.Questions question, int countDownTime) {
        Timber.e("displayTriviaQuestion");
        if (mTriviaView != null && mTriviaInAnim != null) {
            hideTriviaDialog();
            hideKeyBoardIfAny();
            mTriviaView.startQuestion(question, countDownTime);
            mTriviaView.startAnimation(mTriviaInAnim);
        }

    }

    @Override
    public void onTriviaOptionSelected(TriviaInfoModel.Questions.Options selectedOption) {
        triviaAnswer(selectedOption.optionId);
    }

    @Override
    public void onUserGameStateUpdated(int newState) {
        if (newState == GameState.ELIMINATED) {
            mIsNeedRevive = true;
        }
    }

    public void displayWinnerPopup(TriviaFinishModel triviaFinishModel) {
        if (triviaFinishModel.winnerPopup == null) return;
        if (mWinnerPopupDialog == null) {
            mWinnerPopupDialog = TriviaWinnerPopupDialog.newInstance(triviaFinishModel.winnerPopup.title,
                    triviaFinishModel.winnerPopup.prizeMessage,
                    triviaFinishModel.winnerPopup.message,
                    triviaFinishModel.prizePerUserString,
                    mCurrentTriviaModel != null ? mCurrentTriviaModel.countryCode : "");
        }
        mWinnerPopupDialog.show(getChildFragmentManager(), TriviaWinnerPopupDialog.class.getSimpleName());
    }

    public void displayTriviaWinnerList(TriviaFinishModel triviaFinishModel) {
        if (triviaFinishModel == null) return;
        Timber.e("displayTriviaWinnerList");
        showTriviaWinnerLayout(triviaFinishModel.winnerCount, triviaFinishModel.prizePerUserString, triviaFinishModel.message);
    }

    public void dismissTriviaQuestion() {
        if (mTriviaView != null && mTriviaOutAnim != null) {
            mTriviaView.startAnimation(mTriviaOutAnim);
        }
    }

    public void displayTriviaResult(TriviaResultModel triviaResultModel) {
        Timber.e("displayTriviaResult");
        if (mTriviaView != null && mTriviaInAnim != null) {
            if (triviaResultModel.isCorrectAnswer) {
                updatePoints();
                toastMessagePoints(triviaResultModel.message);
            }
            hideKeyBoardIfAny();
            hideTriviaDialog();
            mTriviaView.startAnimation(mTriviaInAnim);
            mTriviaView.setNetworkError(mIsNetworkError);
            mTriviaView.showAnswer(triviaResultModel, mCurrentTriviaModel.countryCode);
        }
    }

    private void toastMessagePoints(String message) {
        if (!StringUtil.isNullOrEmptyString(message))
            Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void dismissTriviaResult() {
        if (mTriviaView != null && mTriviaResultOutAnim != null) {
            mTriviaView.startAnimation(mTriviaResultOutAnim);
            mIsNetworkError = false;
            mTriviaView.setNetworkError(mIsNetworkError);
            int numLikes = new Random().nextInt(30) + 50; //min 50 hearts
            mCompositeSubscription.add(Observable.interval(200, TimeUnit.MILLISECONDS)
                    .take(numLikes)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(aLong -> isFragmentUIActive() && mEndStreamLayout == null)
                    .subscribe(aLong -> showHearts(), Timber::e));
        }

        if (mIsNeedRevive && mCurrentTriviaModel != null && mCurrentTriviaQuestion != null) {
            mIsNeedRevive = false;
            //check user is able to revise or not
            mCompositeSubscription.add(mTriviaCheckReviseUseCase.execute(TriviaCheckReviveUseCase.Params.checkTrivia(mCurrentTriviaModel.triviaId, mCurrentTriviaQuestion.questionId))
                    .filter(triviaReviseModel -> isFragmentUIActive())
                    .subscribe(triviaReviseModel -> {

                        if (triviaReviseModel.getUseRevise() && triviaReviseModel.isReviveAnim()) {
                            if (triviaReviseModel.getReviseCount() != 0) {
                                showReviveDialogWithWelcomeDialog(triviaReviseModel.getTitle(), triviaReviseModel.getMessage(), triviaReviseModel.getCancelMessageTitle(), triviaReviseModel.getCancelMessage(), triviaReviseModel.getReviseCount());
                            } else {
                                showMasterBrainWelcomeDialog(triviaReviseModel.getTitle(), triviaReviseModel.getMessage(), triviaReviseModel.getReviseCount());
                            }
                            return;
                        }

                        if (triviaReviseModel.isReviveAnim()) {
                            showMasterBrainWelcomeDialog(triviaReviseModel.getTitle(), triviaReviseModel.getMessage(), triviaReviseModel.getReviseCount());
                            return;
                        }
                        if (triviaReviseModel.getUseRevise()) {
                            if (triviaReviseModel.getReviseCount() != 0) {
                                showReviveDialog(triviaReviseModel.getTitle(), triviaReviseModel.getMessage(), triviaReviseModel.getCancelMessageTitle(), triviaReviseModel.getCancelMessage());
                            } else {
                                showReviseNotAvailableGameDialog(triviaReviseModel.getTitle(), triviaReviseModel.getMessage());
                            }
                        } else {
                            showTriviaDialogNonAction(triviaReviseModel.getTitle(), triviaReviseModel.getMessage());
                        }
                    }, this::handleRxError));
        }
    }

    private void updatePoints() {
        mCompositeSubscription.add(AppsterWebServices.get().getUserCredits("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), new CreditsRequestModel())
                .subscribe(creditsResponseModel -> {
                    if (creditsResponseModel == null) return;
                    if (creditsResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        updateTotalPoint(creditsResponseModel.getData().totalPoint);
                        AppsterApplication.mAppPreferences.getUserModel().setPoints(creditsResponseModel.getData().totalPoint);
                    }
                }, Timber::e));
    }

    private void showReviveDialogWithWelcomeDialog(String title, String message, String cancelMessageTitle, String cancelMessage, int reviveCount) {

        if (getContext() == null) return;
        TriviaDialog triviaErrorDialog = new TriviaDialog(getContext(), TriviaDialogType.PROGRESS);
        triviaErrorDialog.setProgressDuration(mWaitingSecForRevive)
                .setContentText(message)
                .setConfirmText(isVNTrivia() ? getString(R.string.trivia_use_revise_vi) : getString(R.string.trivia_use_revise))
                .setCancelText(isVNTrivia() ? getString(R.string.trivia_cancel_use_revise_vi) : getString(R.string.trivia_cancel_use_revise))
                .setConfirmClickListener(alertDialog -> {
                    useTriviaGameRevive();
                    alertDialog.dismissWithAnimation();
                })
                .setCancelClickListener(alertDialog -> {
                    alertDialog.dismissWithAnimation();
                    showMasterBrainWelcomeDialog(cancelMessageTitle, cancelMessage, reviveCount);
                }).show();
        listTriviaDialog.add(triviaErrorDialog);
    }

    private void showTriviaDialogNonAction(String title, String message) {
        Timber.e("message = %s", message);
        if (getContext() == null) return;
        TriviaDialog triviaErrorDialog = new TriviaDialog(getContext());
        triviaErrorDialog.setTitleText(title)
                .setContentText(message)
                .showCancelButton(false)
                .show();
        listTriviaDialog.add(triviaErrorDialog);
    }

    private void showReviseNotAvailableGameDialog(String title, String message) {
        if (getContext() == null) return;
        TriviaDialog triviaDialog = new TriviaDialog(getContext());
        triviaDialog.setTitleText(title)
                .setContentText(message)
                .setCancelText(isVNTrivia() ? getString(R.string.trivia_aw_okay_vi) : getString(R.string.trivia_aw_okay))
                .setConfirmText(isVNTrivia() ? getString(R.string.trivia_get_revives_vi) : getString(R.string.trivia_get_revives))
                .setCancelDrawable(R.drawable.selector_pink_trivia_button)
                .setConfirmDrawable(R.drawable.selector_green_trivia_button)
                .setConfirmClickListener(alertDialog -> {
                    alertDialog.dismissWithAnimation();
                    showTriviaUsageDialog();
                })
                .setCancelTextColor(Color.WHITE)
                .show();
        listTriviaDialog.add(triviaDialog);
    }

    private void showMasterBrainWelcomeDialog(String title, String message, int reviveCount) {
        if (getContext() == null) return;
        TriviaDialog masterBrainWelcomeDialog = new TriviaDialog(getContext());
        masterBrainWelcomeDialog.setTitleText(title)
                .setContentText(message)
                .showCancelButton(false)
                .setConfirmClickListener(alertDialog -> {
                    alertDialog.dismissWithAnimation();
                    if (triviaReviveView != null)
                        triviaReviveView.increaseReviveWithAnimation(reviveCount);
                })
                .show();
        listTriviaDialog.add(masterBrainWelcomeDialog);
    }

    private void showReviveDialog(String title, String message, String cancelTitle, String cancelMessage) {
        if (getContext() == null) return;
        TriviaDialog triviaDialog = new TriviaDialog(getContext(), TriviaDialogType.PROGRESS);
        triviaDialog.setProgressDuration(mWaitingSecForRevive)
                .setContentText(message)
                .setConfirmText(isVNTrivia() ? getString(R.string.trivia_use_revise_vi) : getString(R.string.trivia_use_revise))
                .setCancelText(isVNTrivia() ? getString(R.string.trivia_cancel_use_revise_if_fail_vi) : getString(R.string.trivia_cancel_use_revise))
                .setConfirmClickListener(alertDialog -> {
                    useTriviaGameRevive();
                    alertDialog.dismissWithAnimation();
                })
                .setCancelClickListener(alertDialog -> {
                    alertDialog.dismissWithAnimation();
                    showTriviaDialogNonAction(cancelTitle, cancelMessage);
                }).show();
        listTriviaDialog.add(triviaDialog);
    }

    private void useTriviaGameRevive() {
        if (mCurrentTriviaModel != null && mCurrentTriviaQuestion != null) {
            mCompositeSubscription.add(mTriviaUseReviveUseCase.execute(TriviaUseReviveUseCase.Params.useWith(mCurrentTriviaModel.triviaId, mCurrentTriviaQuestion.questionId))
                    .filter(aBoolean -> isFragmentUIActive())
                    .subscribe(success -> {
                        if (success && mTriviaView != null) {
                            mTriviaView.setUserGameState(GameState.ALIVE);
                            mTriviaView.setUserPreviousGameState(GameState.ALIVE);
                            if (mCurrentTriviaModel != null) {
                                mCurrentTriviaModel.reviveCount = mCurrentTriviaModel.reviveCount - 1;
                                updateReviveCount(mCurrentTriviaModel.reviveCount);
                            }
                        }
                    }, this::handleRxError));
        }
    }

    public void dismissWinnerList() {
        if (vsTriviaWinner != null) {
            vsTriviaWinner.setVisibility(View.GONE);
        }
    }

    public void triviaGameStarted() {
        if (mTriviaGameStateMap == null) return;
        setupGameState(TriviaGameState.GAME_START);
    }

    private void setupGameState(int gameState) {
        if (mEndStreamLayout != null)
            return; //don't run anytrivia game state if end layout is showed
        mCompositeSubscription.add(Observable.just(gameState).delay(mTriviaGameStateMap.get(gameState, 0), TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(integer -> isFragmentUIActive())
                .subscribe(this::handleGameState, Timber::e));
    }

    private void handleGameState(@TriviaGameState int state) {
        Timber.e("state= %s", state);
        if (mCurrentTriviaModel == null) return;
        switch (state) {
            case TriviaGameState.GAME_END:
                dismissWinnerList();
                break;
            case TriviaGameState.GAME_FINISH:
                //no more question
                mIsAbleToSendGift = true;
                mIsTriviaShowRunning = false;
                mCompositeSubscription.add(mTriviaFinishUseCase.execute(TriviaFinishUseCase.Params.finish(mCurrentTriviaModel.triviaId))
                        .subscribe(triviaFinishModel -> {
                            displayTriviaWinnerList(triviaFinishModel);
                            if (triviaFinishModel.win && mCurrentTriviaModel.canPlay)
                                displayWinnerPopup(triviaFinishModel);
                            setupGameState(TriviaGameState.GAME_END);
                        }, this::handleRxError));
                break;
            case TriviaGameState.GAME_START:
            case TriviaGameState.QUESTION_WAITING_TIMESUP:
                //display question and waiting for answer
                mCurrentTriviaQuestion = mCurrentTriviaModel.getNextQuestion();
                Timber.e("mCurrentTriviaQuestion= %s", mCurrentTriviaQuestion);
                if (mCurrentTriviaQuestion != null) {
                    int countDownTime = mTriviaGameStateMap.get(TriviaGameState.QUESTION_ANSWER_TIMESUP) - 1;
                    Timber.e("countDownTime= %s", countDownTime);
                    displayTriviaQuestion(mCurrentTriviaQuestion, countDownTime);
                }
                //setup next state
                setupGameState(mCurrentTriviaQuestion != null ? TriviaGameState.QUESTION_ANSWER_TIMESUP : TriviaGameState.GAME_FINISH);

                break;
            case TriviaGameState.QUESTION_ANSWER_TIMESUP:
                //waiting result
                dismissTriviaQuestion();
                //setup next state
                setupGameState(TriviaGameState.RESULT_WAITING_TIMESUP);
                break;
            case TriviaGameState.RESULT_WAITING_TIMESUP:
                //get result
                mCompositeSubscription.add(mTriviaResultUseCase.execute(TriviaResultUseCase.Params.result(mCurrentTriviaModel.triviaId, mCurrentTriviaQuestion.questionId))
                        .subscribe(this::displayTriviaResult, this::handleRxError));
                //start next state immediately, don't need to wait result response
                //setup next state
                setupGameState(TriviaGameState.RESULT_TIMESUP);
                break;
            case TriviaGameState.RESULT_TIMESUP:
                //next question
                dismissTriviaResult();
                //setup next state
                if (mCurrentTriviaModel.hasEndedQuestions()) {
                    // show winners list
                    setupGameState(TriviaGameState.FINISH_WAITING_TIME);
                } else {
                    // next question
                    setupGameState(TriviaGameState.QUESTION_WAITING_TIMESUP);
                }
                break;

            case TriviaGameState.FINISH_WAITING_TIME:
                setupGameState(TriviaGameState.GAME_FINISH);
                break;
        }
    }


    public void triviaAnswer(int optionId) {
        mCompositeSubscription.add(mTriviaAnswerUseCase.execute(TriviaAnswerUseCase.Params.answer(mCurrentTriviaModel.triviaId, mCurrentTriviaQuestion.questionId, optionId))
                .subscribe(success -> {
                    if (!success && mTriviaView != null) {
                        mTriviaView.setViewToEliminated();
                    }
                }, this::handleTriviaError));
    }

    private void handleTriviaError(Throwable throwable) {
        if (throwable instanceof BeLiveServerException) {
            BeLiveServerException exception = (BeLiveServerException) throwable;
            showTriviaDialogNonAction(getString(R.string.error_title), exception.getMessage());
            mIsNetworkError = true;
        }
    }


    public void getTriviaInfo(int triviaInfo) {
        mTriviaGameStateMap = new SparseIntArray();
        mCompositeSubscription.add(mTriviaInfoUseCase.execute(TriviaInfoUseCase.Params.load(triviaInfo))
                .subscribe(triviaInfoModel -> {
                    mCurrentTriviaModel = triviaInfoModel;
                    Timber.e("answerTime = %s", triviaInfoModel.answerTime);
                    mTriviaGameStateMap.put(TriviaGameState.QUESTION_ANSWER_TIMESUP, triviaInfoModel.answerTime + 1);
                    mTriviaGameStateMap.put(TriviaGameState.RESULT_WAITING_TIMESUP, triviaInfoModel.resultWaitingTime);
                    mTriviaGameStateMap.put(TriviaGameState.RESULT_TIMESUP, triviaInfoModel.resultTime);
                    mTriviaGameStateMap.put(TriviaGameState.QUESTION_WAITING_TIMESUP, triviaInfoModel.questionWaitingTime);
                    mTriviaGameStateMap.put(TriviaGameState.GAME_END, triviaInfoModel.finishTime);
                    mTriviaGameStateMap.put(TriviaGameState.FINISH_WAITING_TIME, triviaInfoModel.finishWaitingTime);
                    onTriviaInfoReceived(triviaInfoModel);
                    if (triviaInfoModel.reviveAnim) {
                        showMasterBrainWelcomeDialog(triviaInfoModel.messageTitle, triviaInfoModel.message, triviaInfoModel.reviveCount);
                        if (!triviaInfoModel.canPlay) {
                            if (mTriviaView != null) {
                                mTriviaView.setUserGameState(GameState.ELIMINATED);
                                mTriviaView.setUserPreviousGameState(GameState.ELIMINATED);
                            }
                        }
                    } else {
                        updateReviveCount(triviaInfoModel.reviveCount);
                        if (triviaInfoModel.canPlay) {
                            if (triviaInfoModel.isRejoin) {
                                showTriviaDialogNonAction(triviaInfoModel.messageTitle, triviaInfoModel.message);
                            }
                        } else {
                            if (mTriviaView != null) {
                                mTriviaView.setUserGameState(GameState.ELIMINATED);
                                mTriviaView.setUserPreviousGameState(GameState.ELIMINATED);
                            }
                            showTriviaDialogNonAction(triviaInfoModel.messageTitle, triviaInfoModel.message);
                        }
                    }
                }, this::handleRxError));
    }

    public void getTriviaQuestion(int triviaInfo) {
        mCompositeSubscription.add(mTriviaQuestionUseCase.execute(TriviaQuestionUseCase.Params.load(triviaInfo))
                .subscribe(triviaInfoModel -> mCurrentTriviaModel.questions = triviaInfoModel.questions,
                        this::handleRxError));
    }

    private void updateReviveCount(int reviveCount) {
        triviaReviveView.setCurrentRevive(reviveCount);
    }

    private void showDialogWithSingleAction(String title, String message) {
        if (getContext() != null) {
            new TriviaDialog(getContext())
                    .showCancelButton(false)
                    .setConfirmText(getString(R.string.btn_text_ok))
                    .setContentText(message)
                    .setTitleText(title)
                    .show();
        }
    }

    private boolean isVNTrivia() {
        if (mCurrentTriviaModel == null) return false;
        if (StringUtil.isNullOrEmptyString(mCurrentTriviaModel.countryCode)) return false;
        return Constants.COUNTRY_CODE_VN_FROM_SERVER_RETURN.equals(mCurrentTriviaModel.countryCode);
    }

    //endregion
}


