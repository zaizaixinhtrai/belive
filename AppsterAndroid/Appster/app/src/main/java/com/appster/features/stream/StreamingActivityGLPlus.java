package com.appster.features.stream;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.MotionEventCompat;
import androidx.core.widget.PopupWindowCompat;
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
import com.appster.customview.GiftComboGroupView;
import com.appster.customview.GiftRankingGroupView;
import com.appster.customview.ShowCaseViewTutorial;
import com.appster.customview.StateProgressbar;
import com.appster.customview.StateProgressbar.LevelReachCallback;
import com.appster.customview.StickyPadFrameLayout;
import com.appster.customview.SubPlayerLayout;
import com.appster.customview.category_tag.TagGroup;
import com.appster.customview.luckywheel.LuckyItem;
import com.appster.customview.luckywheel.LuckyWheelView;
import com.appster.customview.trivia.TriviaView;
import com.appster.dialog.DialogReport;
import com.appster.dialog.ExpensiveGiftDialog;
import com.appster.dialog.MoreLiveOptionsPopUp;
import com.appster.dialog.SharePostDialog;
import com.appster.dialog.TopFanDialog;
import com.appster.domain.RecordedMessagesModel;
import com.appster.features.maintenance.MaintenanceActivity;
import com.appster.features.stream.dialog.LiveShopDialog;
import com.appster.features.stream.dialog.TriviaHowToPlayDialog;
import com.appster.features.stream.dialog.TriviaRankingDialog;
import com.appster.features.stream.faceunity.BeLiveImgFaceunityFilter;
import com.appster.features.stream.faceunity.FaceUnityDialogPicker;
import com.appster.features.stream.viewer.PointInfoDialog;
import com.appster.features.user_profile.DialogUserProfileFragment;
import com.appster.interfaces.OnLoadMoreListenerRecyclerView;
import com.appster.manager.AgoraChatManager;
import com.appster.manager.AppsterChatManger;
import com.appster.message.ChatItemModelClass;
import com.appster.models.DailyTopFanModel;
import com.appster.models.FaceUnityStickerModel;
import com.appster.models.StreamModel;
import com.appster.models.StreamTitleSticker;
import com.appster.models.TagListLiveStreamModel;
import com.appster.models.UserModel;
import com.appster.models.event_bus_models.EventBusRefreshFragment;
import com.appster.models.event_bus_models.NewMessageEvent;
import com.appster.models.event_bus_models.UserJoinLeaveEvent;
import com.appster.network_connection.ConnectionClassManager;
import com.appster.network_connection.ConnectionQuality;
import com.appster.network_connection.DeviceBandwidthSampler;
import com.appster.services.InternalMessageReceiver;
import com.appster.tracking.EventTracker;
import com.appster.tracking.EventTrackingName;
import com.appster.utility.AppsterUtility;
import com.appster.utility.Connectivity;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DialogUtil;
import com.appster.utility.GlobalSharedPreferences;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.RecycleItemClickSupport;
import com.appster.utility.RxUtils;
import com.appster.utility.SocialManager;
import com.appster.utility.glide.GlideApp;
import com.appster.utility.glide.RoundedCornersTransformation;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.ReportUserRequestModel;
import com.appster.webservice.response.EndStreamDataModel;
import com.appster.webservice.response.LuckyWheelAwards;
import com.appster.webservice.response.MaintenanceModel;
import com.appster.webservice.response.SubStreamData;
import com.appster.webservice.response.VotingLevels;
import com.apster.common.AlertDialogConnection;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.apster.common.CopyTextUtils;
import com.apster.common.CountryCode;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.DiffCallBaseUtils;
import com.apster.common.DownloadVideos;
import com.apster.common.FileDownloader;
import com.apster.common.LogUtils;
import com.apster.common.Utils;
import com.apster.common.key_broad_detection.KeyboardHeightObserver;
import com.apster.common.key_broad_detection.KeyboardHeightProvider;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.domain.models.TriviaFinishModel;
import com.domain.models.TriviaInfoModel;
import com.domain.models.TriviaResultModel;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ksyun.media.streamer.capture.camera.CameraTouchHelper;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautyProFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgFilterBase;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterMgt;
import com.ksyun.media.streamer.framework.AVConst;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.ksyun.media.streamer.logstats.StatsLogReport;
import com.pack.utility.BitmapUtil;
import com.pack.utility.CheckNetwork;
import com.pack.utility.DialogInfoUtility;
import com.pack.utility.StringUtil;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Completable;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import timber.log.Timber;
import wowza.gocoder.sdk.app.WowzaConstant;
import wowza.gocoder.sdk.app.ui.TimerView;

import static agora.kit.KSYAgoraStreamer.HOST_SCREEN_GUEST_CAM_HEIGHT;
import static agora.kit.KSYAgoraStreamer.HOST_SCREEN_GUEST_CAM_LEFT_BEGIN;
import static agora.kit.KSYAgoraStreamer.HOST_SCREEN_GUEST_CAM_TOP_BEGIN;
import static agora.kit.KSYAgoraStreamer.HOST_SCREEN_GUEST_CAM_WIDTH;
import static agora.kit.KSYAgoraStreamer.RTC_MAIN_SCREEN_CAMERA;
import static agora.kit.KSYAgoraStreamer.SCALING_MODE_CENTER_CROP;
import static com.appster.features.receivers.IncomingBroadcastReceiver.BELIVE_CALL_DETEECTOR;
import static com.appster.features.user_profile.DialogUserProfileFragment.USER_DIALOG_TAG;
import static com.appster.manager.AppsterChatManger.ADMIN_USERNAME;
import static com.appster.network_connection.ConnectionQuality.UNKNOWN;
import static com.appster.utility.SocialManager.SHARE_TYPE_STREAM;
import static com.apster.common.AlertDialogConnection.ALERT_FRAGMENT_TAG;
import static com.apster.common.Constants.LIVE_STREAM;
import static com.apster.common.Constants.TARGET_HOST;
import static com.apster.common.Constants.TRACKING_STREAM_FORMAT;
import static com.apster.common.Constants.TRIGGER_END_BY_ADMIN_BANNED_XMPP;
import static com.apster.common.Constants.TRIGGER_END_BY_NOT_ALLOW_CREATE_SECOND_STREAM;
import static com.apster.common.Constants.TRIGGER_END_BY_PLAYER_RETRY_CONNECT_FAILED;
import static com.apster.common.FileUtility.MEDIA_TYPE_IMAGE_CROPPED;
import static com.apster.common.Utils.getNavigationBarSize;
import static com.ksyun.media.streamer.kit.StreamerConstants.KSY_STREAMER_ERROR_PUBLISH_FAILED;

public class StreamingActivityGLPlus extends BaseActivity implements
        StreamContract.StreamView, ChatGroupDelegateAdapter.ChatGroupClickListener,
        DialogUserProfileFragment.UserProfileActionListener,
        KeyboardHeightObserver, Animator.AnimatorListener,
        SocialManager.SocialSharingListener, LevelReachCallback,
        SubPlayerLayout.OnClickListener, TriviaView.OnTriviaOptionListener {

    final static String TAG = StreamingActivityGLPlus.class.getSimpleName();
    private final static int PERMISSION_REQUEST_CAMERA_AUDIOREC = 1;

    private static final int MODE_BEAUTY = 1;
    private static final int MODE_NONE = 0;
    public static final int MAX_RETRY_IF_PUBLISH_FAILED = 20;
    public static final int RETRY_DELAY_MILLIS = 5000;
    private static final int MORE_OPTION_WINDOW_POPUP_Y_OFSET = Utils.dpToPx(4);

    @Bind(R.id.tvMaintenanceMessage)
    CustomFontTextView tvMaintenanceMessage;
    @Bind(R.id.btn_point)
    ImageButton btnPoint;
    private int mode = MODE_BEAUTY;

    public static final int FACEBOOK = 0;
    public static final int TWITTER = 4;
    @Bind(R.id.txtTimer)
    TimerView mTimerView;

    @Bind(R.id.icon_swith_camera)
    ImageButton iconSwitchCamera;//switch

    @Bind(R.id.camera_preview)
    GLSurfaceView mCameraPreviewView;
    @Bind(R.id.camera_hint)
    CameraHintView mCameraHintView;

    @Bind(R.id.fl_sticky_title)
    StickyPadFrameLayout mStickyPadFrameLayout;
    @Bind(R.id.btn_live_title)
    ImageButton mBtnLiveTitle;
    @Bind(R.id.btn_live_shop)
    ImageButton mBtnLiveShop;

    @Bind(R.id.edt_stream_title)
    EditText mEdtStreamTitle;
    @Bind(R.id.txt_category)
    TextView txtCategory;

    @Bind(R.id.tt_facebook)
    TextView ttFacebook;
    @Bind(R.id.txt_twitter)
    TextView txtTwitter;
    @Bind(R.id.txt_whatsapp)
    TextView txtWhatsapp;
    @Bind(R.id.txt_email)
    TextView txtEmail;
    @Bind(R.id.tt_location)
    TextView txtLocation;

    @Bind(R.id.checkbtn_external_cam)
    AppCompatCheckBox mCheckBoxExternalCam;

    @Bind(R.id.checkbtn_trivia)
    AppCompatCheckBox mTriviaShow;

    @Bind(R.id.ibShareStream)
    ImageButton mIbShareSns;
    @Bind(R.id.img_more_options)
    ImageButton mBtnMoreOptions;
//    @Bind(R.id.btn_face_unity_sticker)
//    ImageButton mBtnStickerOption;

    @Bind(R.id.startStreamBtn)
    Button startStreamBtn;

    @Bind(R.id.ll_share_and_start)
    LinearLayout llShareAndStart;
    @Bind(R.id.ll_title_category)
    LinearLayout llTitleCategory;

    @Bind(R.id.start_live_view)
    FrameLayout startLiveView;
    @Bind(R.id.tvCurrentView)
    TextView tvCurrentView;
    @Bind(R.id.hlvCustomList)
    RecyclerView userWatcherListView;
    @Bind(R.id.commentsList)
    RecyclerView commentsListView;
    @Bind(R.id.ibCloseStream)
    ImageButton cancelLive;
    @Bind(R.id.periscope)
    PeriscopeLayout periscope;
    @Bind(R.id.tvTotalLiked)
    TextView tvTotalLiked;
    @Bind(R.id.llStreamingToppanel)
    RelativeLayout llStreamingToppanel;
    @Bind(R.id.onOffFlash)
    ImageButton onOffFlash;

    @Bind(R.id.stateProgressbar)
    StateProgressbar stateProgressbar;
    @Bind(R.id.progress)
    ProgressBar mProgress;
    @Bind(R.id.fm_blure)
    FrameLayout fmBlure;

    @Bind(R.id.tvUploadCoverPhoto)
    TextView tvUploadCoverPhoto;
    @Bind(R.id.ciOwnerUserImage)
    CircleImageView ciOwnerUserImage;

    @Bind(R.id.txt_stars)
    TextView txtStars;

    @Bind(R.id.txt_stream_upload_speed)
    TextView txtStreamUploadSpeed;

    @Bind(R.id.onOffBeauty)
    ImageButton onOffBeauty;

    @Bind(R.id.ll_gift_group)
    GiftComboGroupView llGiftGroup;


    @Bind(R.id.stream_live_bottom_panel)
    LinearLayout userActionsWholeContainer;

    @Bind(R.id.switcher)
    ViewSwitcher switcher;

    @Bind(R.id.view_stub_preLive)
    ViewStub viewStub;


    @Bind(R.id.etComment)
    CustomFontEditText etComment;

    @Bind(R.id.ibSendComment)
    ImageButton ibSendComment;

    @Bind(R.id.grv_ranking)
    GiftRankingGroupView mGiftRankingGroupView;

    @Bind(R.id.ivStreamCover)
    ImageView mStreamCover;
    @Bind(R.id.llBottomContainer)
    LinearLayout llBottomContainer;

    @Bind(R.id.vsEndStream)
    ViewStub vsEndStream;

    @Bind(R.id.vsTriviaWinner)
    ViewStub vsTriviaWinner;

    @Bind(R.id.vsLuckywheel)
    ViewStub vsLuckywheel;

    LuckyWheelLayout mLuckyWheelLayout;

    @Bind(R.id.llLiveVideoOwner)
    LinearLayout llLiveVideoOwner;
    @Bind(R.id.flLiveContentContainer)
    RelativeLayout flLiveContentContainer;
    @Bind(R.id.tvNetworkSlow)
    CustomFontTextView tvNetworkSlow;

    @Bind(R.id.vsSubPlayer)
    ViewStub vsSubPlayer;

    @Bind(R.id.triviaView)
    TriviaView mTriviaView;
    @Bind(R.id.triviaCountDownView)
    LinearLayout triviaCountDownView;
    @Bind(R.id.tvCountDownStatus)
    CustomFontTextView tvCountDownStatus;
    @Bind(R.id.tvTriviaCountDownText)
    CustomFontTextView tvTriviaCountDownText;

    @Bind(R.id.clTriviaExtraActionsContainer)
    ConstraintLayout clTriviaExtraActionsContainer;
    @Bind(R.id.lo_point)
    ViewGroup loUserPoint;
    @Bind(R.id.tv_user_point)
    TextView tvUserPoint;

    //ksy library
    private KSYAgoraStreamer mStreamer;
    private KSYAgoraStreamer.OnInfoListener mOnInfoListener;
    private KSYAgoraStreamer.OnErrorListener mOnErrorListener;
    private StatsLogReport.OnLogEventListener mOnLogEventListener;
    private CameraTouchHelper.OnTouchListener mOnCameraPreviewTouchListener;
    private Handler mMainHandler;
    private boolean mHWEncoderUnsupported;
    private boolean mSWEncoderUnsupported;
    private boolean mStreaming = false;
    private boolean iFlashOpened = false;
    private AtomicBoolean mIsCallWaitingTimeoutEnable = new AtomicBoolean(false);
    private ArrayList<ChatItemModelClass> mOldCommentList = new ArrayList<>();
    boolean isSavedVideo;
    /**
     * if the network is broken (disconnected)
     * the wowza server will close the stream after that 17 seconds (this value can change on server side) if don't receive any data from client.
     * the streamer will try to reconnect to wowza each 3 seconds
     * thus after 5 times try to reconnect unsuccessfully the stream should be closed otherwise the counter will be reset.
     */
    AtomicInteger mStreamTimeOutCounter;

    private boolean isCommentsLayoutShowing = false;

    private int mShareOption = -1;

    private ArrayList<DisplayableItem> listItemChat;

    ChatGroupDelegateAdapter mChatGroupAdapter;
    LinearLayoutManager commentLayoutManager;
    int recyclerViewCommentState = RecyclerView.SCROLL_STATE_IDLE;
    boolean recyclerViewCommentScrollWaited = false;

    private AdapterListWatcher listUserAdapter;

    private boolean isHideAllView = false;
    private ArrayList<String> mNaughtyWords;
    private int TagId;

    boolean isStreamReady = false;
    private boolean mIsTutorialShowing;

    DialogInfoUtility dialogInfoUtility;
    SharePostDialog sharePostDialog;

    private TopFanDialog topFanDialog;
    private LiveShopDialog mLiveShopDialog;
    ExpensiveGiftDialog mExpensiveGiftDialog;
    List<ChatItemModelClass> mGiftQueue;
    boolean isExpensiveGiftDialogShown;

    LinearLayoutManager layoutManager;


    private ConnectionQuality mConnectionClass = UNKNOWN;
    ArrayList<RecordedMessagesModel> mRecordedMessagesModels = new ArrayList<>();

    private final PublishSubject<UserJoinLeaveEvent> watcherAddEventObservable = PublishSubject.create();
    private final PublishSubject<ChatItemModelClass> chatItemModelObservable = PublishSubject.create();
    private final PublishSubject<Object> showHeartObservable = PublishSubject.create();
    private final PublishSubject<Integer> publishFailedObservable = PublishSubject.create();
    private final PublishSubject<Object> mGetDailyTopFansListObservable = PublishSubject.create();
    private final PublishSubject<Boolean> mTriviaShowListener = PublishSubject.create();
    AlertDialogConnection alertDialogConnection;
    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;

    private AppsterChatManger mChatManager;
    private AgoraChatManager mAgoraChatManager;
    UserModel mCurrentUser;
    @Bind(R.id.streamPreview)
    ImageView streamPreview;
    List<String> mTopFanList = new ArrayList<>();
    final PublishSubject<Boolean> luckywheelVisibilityListener = PublishSubject.create();
    /**
     * The keyboard height provider
     */
    private KeyboardHeightProvider keyboardHeightProvider;
    InputMethodManager mInputMethodManager;


    PublishSubject<Integer> mVotingbarProgressObservable;

    List<TagListLiveStreamModel> tagModelList;

    StreamContract.UserActions mStreamPresenter;

    private EndStreamLayout mEndStreamLayout;
    private PreLiveLayout mPreLiveLayout;
    private Toast mToast;
    String mSlug;
    StreamModel mStreamDetail;
    String mStreamTitle;
    private boolean mIsStreamTitleAvailable;
    private boolean mIsSeller;
    private String mStreamUrlShare;

    private boolean mIsEndStreamByAdmin = false;
    protected Uri mStreamCoverUri;
    private long mLastStreamDuration = 0;
    private boolean mResumePrevisousStream;
    private boolean mStickyPadInitCompleted;
    protected ImgBeautyProFilter mProFilter;
    private boolean mVideoEncoderUnsupported;
    final Subject<Boolean, Boolean> mFaceUnityStateObservable = PublishSubject.create();

    MoreLiveOptionsPopUp mMoreLiveOptionsPopUp;
    FaceUnityDialogPicker mFaceUnityDialogPicker;
    protected FaceUnityStickerModel mStickerModel;
    private boolean mGestureEnabled;
    private boolean mIsExternalCamera = false;
    private boolean mIsTriviaShow = false;

    SubPlayerLayout mSubPlayerLayout;
    private String mSubStreamUrl = "rtmp://stgwowza.view.belive.sg:1935/Appsters_recording/71d904a1cca94d2e95fbd1821984e434";
    private String mConversationWithUser = "";
    boolean mIsCaling = false;
    private String mSubStreamSlug = "";
    private KSYAgoraStreamer.OnRTCInfoListener mOnRTCInfoListener;
    private Subscription mTimeoutSubscription;
    private Subscription mFirstFrameTimeoutSubscription;
    protected DialogbeLiveConfirmation mLiveEndConfirmation;
    AtomicBoolean mGuestAlreadyEndedCall = new AtomicBoolean(false);
    AtomicBoolean mIsResumableStream = new AtomicBoolean(false);
    AtomicBoolean mIsCameraInitDone = new AtomicBoolean(false);
    private AtomicBoolean mIsFirstFrameVideoCallDecoded = new AtomicBoolean(false);
    protected CountDownTimer mTriviaOnOffFaceTimer;
    private CountDownTimer mTriviaGetQuestionCountTime;

    private TriviaHowToPlayDialog mTriviaHowToPlayDialog;
    private TriviaRankingDialog mTriviaRankingDialog;
    private Animation mTriviaInAnim;
    private Animation mTriviaOutAnim;
    private Animation mTriviaResultOutAnim;
    private PointInfoDialog pointInfoDialog;
    private DialogInfoUtility mPermissionGuideDialog;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};
    private boolean mHandledPermissionCheck = false;
    private boolean mFirstCheckPermission = true;
    private final List<String> mRequestedPermissions = new ArrayList<>();

    public static Intent createIntent(Context context) {
        return new Intent(context, StreamingActivityGLPlus.class);
    }

    /***
     * check network connection
     */
    public void checkNetworkConnection() {
        IntentFilter networkStateIntentFilter = new IntentFilter();
        networkStateIntentFilter
                .addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateIntentReceiver, networkStateIntentFilter);
        if (alertDialogConnection != null) {
            alertDialogConnection.dismiss();
        }

    }

    /***
     * register network state receiver to listen about network change state
     */
    BroadcastReceiver networkStateIntentReceiver = null;

    final BroadcastReceiver mPhoneCallStateIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int phoneState = intent.getIntExtra("state", 0);
                if (phoneState == TelephonyManager.CALL_STATE_OFFHOOK && mIsCaling) {
                    //call accepted end video call if  any
                    notifyCallEndedState();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_gpuplus);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ButterKnife.bind(this);
        if (mBeLiveThemeHelper != null && mBeLiveThemeHelper.isTransparentStatusBarRequired()) {
            Window window = getWindow();
            if (window != null) window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        //faceunity
        initKsyStreamerCallback();
        initKsyStreamer();


        EventTracker.trackEvent(EventTrackingName.EVENT_LIVE_STREAM_START);

        streamPreview.setVisibility(View.GONE);
        keyboardHeightProvider = new KeyboardHeightProvider(this);
        mCurrentUser = AppsterApplication.mAppPreferences.getUserModel();
        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        mChatManager = AppsterChatManger.getInstance(this);
        mAgoraChatManager = AgoraChatManager.get();
        mStreamPresenter = new StreamPresenter(this, AppsterWebServices.get(), mChatManager,mAgoraChatManager, mCurrentUser);
        networkStateIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtils.logE(TAG, "CONNECTION CHANGED");
                AlertDialogConnection fragment = (AlertDialogConnection) getSupportFragmentManager()
                        .findFragmentByTag(ALERT_FRAGMENT_TAG);
                if (Connectivity.getConnectedState(context.getApplicationContext()) != Connectivity.NetworkStatus.OFF) {
                    //network is available
                    if (fragment != null) {
                        //check connection bandwidth
                        if (mStreamPresenter != null) mStreamPresenter.networkResume();
                        fragment.dismiss();
                        if (isClickedGoLive && !isStreamReady) {
                            if (progress != null && progress.isShowing()) progress.dismiss();
                            checkNetworkBandwidthAndGoLive();
                        }
                    }
                } else {
                    if (fragment == null) {
                        if (mStreamPresenter != null) mStreamPresenter.networkInterupted();
                        alertDialogConnection = new AlertDialogConnection();
                        alertDialogConnection.setCancelable(false);
                        alertDialogConnection.show(getSupportFragmentManager(),
                                ALERT_FRAGMENT_TAG);
                        alertDialogConnection.setBtnReconnectClick(() -> {
                            AlertDialogConnection fragment1 = (AlertDialogConnection) getSupportFragmentManager()
                                    .findFragmentByTag(ALERT_FRAGMENT_TAG);
                            if (fragment1 != null) {
                                fragment1.dismiss();
                            }
                        });

                    }
                }
            }

        };
        handleView();

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageLoaderUtil.displayUserImage(getApplicationContext(), mCurrentUser.getUserImage(), ciOwnerUserImage);
        loadCoverImageWithLink(mCurrentUser.getUserImage());

        mCompositeSubscription.add(AppsterUtility.clicks(llLiveVideoOwner)
                .subscribe(aVoid -> showUserProfile(mCurrentUser.getUserName(), mCurrentUser.getUserImage(), mSlug)
                        , this::handleRxError));

        mCompositeSubscription.add(AppsterUtility.clicks(btnPoint)
                .subscribe(aVoid -> {
                            if (mStreamDetail != null) {
                                // reuse the formatted point
                                pointInfoDialog = PointInfoDialog.newInstance(tvUserPoint.getText().toString(),
                                        mStreamDetail.getUserPointInfoUrl());
                                pointInfoDialog.show(getSupportFragmentManager(), "PointInfoDialog");
                                EventTracker.trackMBPointsTab(AppsterApplication.mAppPreferences.getUserModel().getUserId());
                            }
                        }
                        , this::handleRxError));

        initObservables();

        mEdtStreamTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mInputMethodManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
                //hide bottom button bar if any
                showFullScreen(getWindow());
            }
            return false;
        });


        dialogInfoUtility = new DialogInfoUtility();
        // make sure to start the keyboard height provider after the onResume
        // of this activity. This is because a popup window must be initialised
        // and attached to the activity root view.
        View view = findViewById(R.id.flStreamContainer);
        view.post(() -> keyboardHeightProvider.start());

        mStreamPresenter.getNaughtyWords();
        enableSocialShare(AppsterApplication.mAppPreferences.getUserLoginType());

        this.mGiftQueue = new LinkedList<>();
        mCompositeSubscription.add(mFaceUnityStateObservable.distinctUntilChanged().subscribe(this::connectWithFaceUnity, Timber::e));
        LocalBroadcastManager.getInstance(this).registerReceiver(mPhoneCallStateIntentReceiver, new IntentFilter(BELIVE_CALL_DETEECTOR));
//        mTriviaView.setRole(Role.HOST);
        mTriviaView.setTriviaOptionListener(this);

//        showTriviaWinnerLayout(30, "S$ 217.39", "Each winners get:");
        mRequestedPermissions.addAll(Arrays.asList(REQUIRED_PERMISSIONS));
        mRequestedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        mRequestedPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void initObservables() {
        initChatGroupObservables();
        initLuckyWheelObservable();
        Observable<Integer> debouncePublishFailedObservable = publishFailedObservable.debounce(1, TimeUnit.SECONDS);
        mCompositeSubscription.add(debouncePublishFailedObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> onReconnectWowzaServer(), this::handleRxError));

        Observable<Object> debounceDailyTopFansObservable = mGetDailyTopFansListObservable.debounce(3, TimeUnit.SECONDS);
        mCompositeSubscription.add(debounceDailyTopFansObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> mStreamPresenter.getDailyTopFansList(), this::handleRxError));
        mCompositeSubscription.add(mTriviaShowListener.subscribe(this::handleTriviaControls));
    }

    private void handleTriviaControls(boolean isTrivia) {
        //disable all bottom action
        if (etComment != null) etComment.setEnabled(!isTrivia);
        if (mBtnMoreOptions != null) mBtnMoreOptions.setEnabled(!isTrivia);
        if (mIbShareSns != null) mIbShareSns.setEnabled(!isTrivia);
//        if (mBtnStickerOption != null) mBtnStickerOption.setEnabled(!isTrivia);
        if (mBtnLiveTitle != null) mBtnLiveTitle.setEnabled(!isTrivia);
        if (txtStars != null) txtStars.setEnabled(!isTrivia);
    }

    private void enableSocialShare(int loginType) {
        switch (loginType) {
            case FACEBOOK:
                ttFacebook.performClick();
                txtTwitter.setVisibility(View.GONE);
                break;
            case TWITTER:
                txtTwitter.performClick();
                ttFacebook.setVisibility(View.GONE);
                break;
            default:
                txtTwitter.setVisibility(View.GONE);
                ttFacebook.setVisibility(View.GONE);
        }
    }

    private void initLuckyWheelObservable() {
        mCompositeSubscription.add(luckywheelVisibilityListener
                .distinctUntilChanged()
                .subscribe(isShow -> {
//                    if (isShow) {
//                        vsLuckywheel.setVisibility(View.VISIBLE);
//                        mStreamPresenter.onLuckyWheelShowed();
////                            float showLeft = 0.3f, showTop = 0.35f;
////                            float showRight = 0.4f + showLeft, showBottom = 0.3f + showTop;
//                        if (streamPreview.getVisibility() != View.VISIBLE) {
//                            mCameraView.startThunbnailCliping(Utils.dpToPx(120), Utils.dpToPx(160), null, new CameraRecordGLSurfaceView.TakeThunbnailCallback() {
//
//                                boolean isUsing = false;
//
//                                @Override
//                                public boolean isUsingBitmap() {
//                                    return isUsing;
//                                }
//
//                                @Override
//                                public void takeThunbnailOK(Bitmap bmp) {
//                                    isUsing = true;
//                                    streamPreview.setImageBitmap(bmp);
//                                    streamPreview.setVisibility(View.VISIBLE);
//                                    isUsing = false;
//                                }
//                            });
//                        }
//                    } else {
//                        vsLuckywheel.setVisibility(View.GONE);
//                        mCameraView.stopThunbnailCliping();
//                        streamPreview.setVisibility(View.GONE);
//                        if(mLuckyWheelLayout!=null) mLuckyWheelLayout.clearData();
//                    }
                }, this::handleRxError));
    }

    private void initChatGroupObservables() {
        //buffer watchers whose avatar will show into recycler view
        Observable<UserJoinLeaveEvent> debouncedWatcherAddEmitter = watcherAddEventObservable.debounce(500, TimeUnit.MILLISECONDS);
        Observable<List<UserJoinLeaveEvent>> debouncedWatcherAddBufferEmitter = watcherAddEventObservable.buffer(debouncedWatcherAddEmitter);
        mCompositeSubscription.add(debouncedWatcherAddBufferEmitter.observeOn(AndroidSchedulers.mainThread())
                .subscribe(newWatchers -> {
                    LogUtils.logD(TAG, "** joined watchers debounced " + newWatchers.size());
                    transformJointEventToMessage(newWatchers);
                    updateWatcherList();
                }, error -> LogUtils.logE(TAG, error.getMessage())));

        //
        // buffer messages which will be showed onto the chat box
//        Observable<ChatItemModelClass> debouncedChatItemEmitter = chatItemModelObservable.debounce(200, TimeUnit.MILLISECONDS);
        Observable<List<ChatItemModelClass>> deboundChatItemBufferEmitter = chatItemModelObservable.buffer(1, TimeUnit.SECONDS);
        mCompositeSubscription.add(deboundChatItemBufferEmitter.filter(chatItemModelClasses -> !chatItemModelClasses.isEmpty())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(chatItemModelClasses -> !isFinishing() && !isDestroyed())
                .subscribe(this::updateBufferedMessageInList, this::handleRxError));

        //
        //buffer hearts which will be showed
        final int MAX_HEART_IN_PERIOD_TIME = 8;
//        Observable<Object> debouncedShowHeartEmitter = showHeartObservable.debounce(200, TimeUnit.MILLISECONDS);
        Observable<List<Object>> deboundShowHeartEmitter = showHeartObservable.buffer(1, TimeUnit.SECONDS);
        mCompositeSubscription.add(deboundShowHeartEmitter.observeOn(AndroidSchedulers.mainThread())
                .flatMap(objects -> Observable.from(objects).take(MAX_HEART_IN_PERIOD_TIME))
                .subscribe(object -> showHearts(), this::handleRxError));
    }

    private void enableGesture(boolean shouldEnable) {
        if (shouldEnable) mImgFaceunityFilter.setGestureType(0);
        mGestureEnabled = true;
    }

    private void showFullScreen(Window window) {
        if (window != null && window.getDecorView() != null)
            window.getDecorView().setSystemUiVisibility(getFullScreenVisibilityFlags());
    }

    public void showFullScreen(View view) {
        if (view != null) view.setSystemUiVisibility(getFullScreenVisibilityFlags());

    }

    @Override
    public void onStreamBeginSuccess() {
        //mTriviaInAnim only init when startTrivia start so I use it as a flag to ensure trivia call once
        if (mIsTriviaShow && mTriviaInAnim == null) {
            startTrivia(mStreamDetail.triviaId);
        }
    }

    //#region ksy library ==========================================================================
    private void initKsyStreamerCallback() {
        mMainHandler = new Handler();
        mOnInfoListener = (what, msg1, msg2) -> {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE:
                    Timber.d("KSY_STREAMER_CAMERA_INIT_DONE");
                    setCameraAntiBanding50Hz();
                    updateFaceunitParams();
                    mIsCameraInitDone.set(true);
                    if (mIsResumableStream.get()) showResumeConfirmDialog();
                    break;
                case StreamerConstants.KSY_STREAMER_OPEN_STREAM_SUCCESS:
                    Timber.d("KSY_STREAMER_OPEN_STREAM_SUCCESS");
                    if (progress != null) progress.dismiss();
                    isStreamReady = true;
                    mVideoEncoderUnsupported = false;
                    mStreamTimeOutCounter.set(0);
                    updateTimer();
                    visibleViewWhenStartLive();
                    hideProcessView();
                    // Keep the screen on while we are broadcasting
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    mStreamPresenter.startStream();

                    uploadCoverImage();
                    mIbShareSns.setVisibility(View.VISIBLE);
                    mStreamPresenter.sendNotifyStreamResume();
                    checkToShowTutorial();
                    break;

                case StreamerConstants.KSY_STREAMER_OPEN_FILE_SUCCESS:
                    Timber.d("KSY_STREAMER_OPEN_FILE_SUCCESS");
                    break;
                case StreamerConstants.KSY_STREAMER_FRAME_SEND_SLOW:
                    Timber.d("KSY_STREAMER_FRAME_SEND_SLOW %d ms", msg1);
                    showToast(getString(R.string.message_network_not_good), Toast.LENGTH_SHORT);
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
                    publishFailedObservable.onNext(KSY_STREAMER_ERROR_PUBLISH_FAILED);
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
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_EVICTED:
                    if (mStreamer != null) mStreamer.stopCameraPreview();
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                    if (mStreamer != null) mStreamer.stopCameraPreview();
//                    mMainHandler.postDelayed(this::startCameraPreviewWithPermCheck, 5000);
                    mMainHandler.postDelayed(this::requestPermissions, 5000);
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

        mOnCameraPreviewTouchListener = new CameraTouchHelper.OnTouchListener() {
            private static final int MAX_CLICK_DURATION = 200;
            private long startClickTime;
            boolean mHasActionPointerDown;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = MotionEventCompat.getActionMasked(motionEvent);
                if (MotionEvent.ACTION_DOWN == action) {
                    mHasActionPointerDown = false;
                    startClickTime = System.currentTimeMillis();
                    Timber.d("mOnCameraPreviewTouchListener ACTION_DOWN");
                } else if (MotionEvent.ACTION_POINTER_DOWN == action) {
                    mHasActionPointerDown = true;
                    Timber.d("mOnCameraPreviewTouchListener ACTION_POINTER_DOWN");
                } else if (MotionEvent.ACTION_UP == action && !mHasActionPointerDown) {
                    long clickDuration = System.currentTimeMillis() - startClickTime;
                    if (clickDuration < MAX_CLICK_DURATION) {
                        onScreenClicked();
                    }
                    Timber.d("mOnCameraPreviewTouchListener MotionEvent.ACTION_UP == action && !mHasActionPointerDown %s", clickDuration);
                }
                return false;
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
                                if (mGuestAlreadyEndedCall.get()) {
                                    Timber.e("joined channel success but guest already ended");
                                    notifyCallEndedState();
//                                    showToast("Guest ended before start call",Toast.LENGTH_LONG);
                                }
                            });
                        } else {
                            Timber.e("joined channel failed end call");
                            mMainHandler.post(() -> notifyCallEndedState());
                        }
                        break;
                    }

                    case MediaManager.MediaUiHandler.FIRST_FRAME_DECODED: {
                        //收到辅播数据后，设置辅播画面为大窗口
                        Timber.e("onFirstRemoteVideoDecoded " + Arrays.toString(data));
                        mIsFirstFrameVideoCallDecoded.set(true);
                        mMainHandler.post(() -> {
                            if (mSubPlayerLayout != null && mSubPlayerLayout.isTapScreenEnable()) {
                                Timber.e("Tap screen already enable but still received first frame decode mean -> reconnect");
                                if (mStreamPresenter != null) {
                                    mSubPlayerLayout.updateState(State.RECONNECTED);
                                    mStreamPresenter.notifyCallReConnected();
                                }
                            } else {
                                if (mStreamPresenter != null)
                                    mStreamPresenter.notifyCallConnected();
                                if (mSubPlayerLayout != null) mSubPlayerLayout.streamStarted();
                            }
                        });

                        break;
                    }

                    case MediaManager.MediaUiHandler.LEAVE_CHANNEL: {
                        // temporarily only one remote stream supported, so reset uid here
                        Timber.e("MediaManager.MediaUiHandler.LEAVE_CHANNEL");
                        mMainHandler.post(() -> {
                            if (BuildConfig.DEBUG) {
                                showToast("leave channel success", Toast.LENGTH_LONG);
                            }

                            resetStreamStateAfterCallingForHuaWeiDevices();
                        });
                        mIsFirstFrameVideoCallDecoded.set(false);
                        break;
                    }

                    case MediaManager.MediaUiHandler.USER_OFFLINE: {
                        //辅播断开后，设置主播画面为大窗口
                        Timber.e("MediaManager.MediaUiHandler.USER_OFFLINE");
                        mMainHandler.post(() -> notifyCallEndedState());
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
        }

        ;
    }

    private void initKsyStreamer() {
        mStreamer = new KSYAgoraStreamer(this);
        mStreamTimeOutCounter = new AtomicInteger();

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
        mStreamer.setPreviewFps(30);
        mStreamer.setTargetFps(30);
        mStreamer.setVideoKBitrate(1000, 1000, 400);//init, max, min --- init should be 3/4 of max, min should be 1/4 of max.
//        mStreamer.setVideoKBitrate(400)
        int videoResolution = StreamerConstants.VIDEO_RESOLUTION_480P;
        mStreamer.setPreviewResolution(videoResolution);
        mStreamer.setTargetResolution(videoResolution);
        mStreamer.setVideoCodecId(AVConst.CODEC_ID_AVC);//h264
        mStreamer.setFrontCameraMirror(false);

        mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_HARDWARE);

        mStreamer.setDisplayPreview(mCameraPreviewView);

        mStreamer.setEnableAutoRestart(true, RETRY_DELAY_MILLIS);

        mStreamer.setOnInfoListener(mOnInfoListener);
        mStreamer.setOnErrorListener(mOnErrorListener);
        mStreamer.setOnLogEventListener(mOnLogEventListener);
        mStreamer.setOnRTCInfoListener(mOnRTCInfoListener);
        mStreamer.getImgTexFilterMgt().setOnErrorListener((filter, errno) -> {
            Toast.makeText(StreamingActivityGLPlus.this, "your device doesn't support this filter", Toast.LENGTH_SHORT).show();
            mStreamer.getImgTexFilterMgt().setFilter(mStreamer.getGLRender(),
                    ImgTexFilterMgt.KSY_FILTER_BEAUTY_DISABLE);
        });

        turnOnBeautyFilter();

        // add RGBA buffer filter to ImgTexFilterMgt, this would cause performance drop,
        // only valid after Android 4.4
        //RGBABufDemoFilter demoFilter = new RGBABufDemoFilter(mStreamer.getGLRender());
        //mStreamer.getImgTexFilterMgt().setExtraFilter(demoFilter);

        // touch focus and zoom support
        CameraTouchHelper cameraTouchHelper = new CameraTouchHelper();
        cameraTouchHelper.setEnableTouchFocus(false);
        cameraTouchHelper.setCameraCapture(mStreamer.getCameraCapture());
        cameraTouchHelper.addTouchListener(mOnCameraPreviewTouchListener);
//        mCameraPreviewView.setOnTouchListener(cameraTouchHelper)
        mCameraHintView.setOnTouchListener(cameraTouchHelper);
        // set CameraHintView to show focus rect and zoom ratio
        cameraTouchHelper.setCameraHintView(mCameraHintView);
        //for sub screen
        cameraTouchHelper.addTouchListener(mSubScreenTouchListener);
    }

    void onReconnectWowzaServer() {
        int tryTime = mStreamTimeOutCounter.incrementAndGet();
        Timber.d("KSY_STREAMER reconnect %d", tryTime);
        if (tryTime > MAX_RETRY_IF_PUBLISH_FAILED) {
            stopStream(TRIGGER_END_BY_PLAYER_RETRY_CONNECT_FAILED);
            showEndStreamLayout(TRIGGER_END_BY_PLAYER_RETRY_CONNECT_FAILED);
        }
    }

    private void turnOnBeautyFilter() {


        mProFilter = new ImgBeautyProFilter(mStreamer.getGLRender(), getApplicationContext());
        float grindingRatio = 0.5f;
        float whiteningRatio = 0.25f;
        float rosyRatio = 0.45f;

        if (mProFilter.isGrindRatioSupported()) {
            mProFilter.setGrindRatio(grindingRatio);
        }
        if (mProFilter.isWhitenRatioSupported()) {
            mProFilter.setWhitenRatio(whiteningRatio);
        }
        if (mProFilter.isRuddyRatioSupported()) {
            mProFilter.setRuddyRatio(rosyRatio);
        }
        if (mStreamer != null) {
            mStreamer.setEnableImgBufBeauty(true);
            mStreamer.getImgTexFilterMgt().setFilter(mProFilter);
        }
    }

    // Example to handle camera related operation
    private void setCameraAntiBanding50Hz() {
        Camera.Parameters parameters = mStreamer.getCameraCapture().getCameraParameters();
        if (parameters != null) {
            parameters.setAntibanding(Camera.Parameters.ANTIBANDING_50HZ);
            mStreamer.getCameraCapture().setCameraParameters(parameters);
        }
    }

    private void startCameraPreviewWithPermCheck() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        int cameraPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audioPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (cameraPerm != PackageManager.PERMISSION_GRANTED ||
                audioPerm != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Toast.makeText(this, "No CAMERA or AudioRecord or Storage permission, please check",
                        Toast.LENGTH_LONG).show();
            } else {
                String[] permissions = {Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
//                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CAMERA_AUDIOREC);
            }
        } else {
            if (mStreamer != null) {
                mStreamer.startCameraPreview();
//                if (!mGestureEnabled) {
//                    mFaceUnityStateObservable.onNext(true);
//                    enableGesture(false);
//                }
            }
        }
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

    //start streaming
//    private void startStream() {
//        mStreamer.startStream();
//        mShootingText.setText(STOP_STRING);
//        mShootingText.postInvalidate();
//        mStreaming = true;
//    }

    private void uploadCoverImage() {
        if (mStreamCoverUri != null) {
            mStreamPresenter.uploadFirstStreamImage(mSlug, new File(mStreamCoverUri.getPath()));
            mStreamCoverUri = null;
        }
//        else{
//            mStreamer.requestScreenShot(bitmap -> mStreamPresenter.uploadFirstStreamImage(mSlug, AppsterUtility.persistImage(StreamingActivityGLPlus.this, bitmap, "default")));
//        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void stopRecord() {
        mStreamer.stopRecord();
    }

    void handleRxError(Throwable e) {
        Timber.d(e.getMessage());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA_AUDIOREC:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mStreamer != null) mStreamer.startCameraPreview();
                } else {
                    Log.e(TAG, "No CAMERA or AudioRecord permission");
                    Toast.makeText(this, "No CAMERA or AudioRecord permission",
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @OnFocusChange(R.id.etComment)
    public void onFocus(boolean hasFocus) {
        Timber.d("comment focus -> %s", hasFocus);
        if (hasFocus) {
            onEdtCommentFocusGained();
        } else {
            onEdtCommentFocusLost();
            mInputMethodManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
            if (getWindow() != null) showFullScreen(getWindow());
        }
    }

    private void onEdtCommentFocusGained() {
        mBtnMoreOptions.setVisibility(View.GONE);
        mIbShareSns.setVisibility(View.GONE);
        iconSwitchCamera.setVisibility(View.GONE);
//        mBtnStickerOption.setVisibility(View.GONE);
        ibSendComment.setVisibility(View.VISIBLE);
    }

    private void onEdtCommentFocusLost() {
        mBtnMoreOptions.setVisibility(View.VISIBLE);
        mIbShareSns.setVisibility(View.VISIBLE);
        iconSwitchCamera.setVisibility(View.VISIBLE);
//        mBtnStickerOption.setVisibility(View.VISIBLE);
        ibSendComment.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        Timber.e("onDestroy");
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPhoneCallStateIntentReceiver);
        releaseKSYResources();
        if (mToast != null) {
            mToast.cancel();
        }
        mStreamPresenter.detachView();
        if (mTriviaOnOffFaceTimer != null) mTriviaOnOffFaceTimer.cancel();
        if (mTriviaGetQuestionCountTime != null) mTriviaGetQuestionCountTime.cancel();
        mConnectionClassManager.reset();
        EventBus.getDefault().unregister(this);
        keyboardHeightProvider.setKeyboardHeightObserver(null);
        keyboardHeightProvider.close();
        FileDownloader.getInstance().clearAllDownloadThread();

        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        ButterKnife.unbind(this);

//        if (mSubPlayerLayout != null) mSubPlayerLayout.onDestroy();
        mCurrentUser = null;


    }

    void releaseKSYResources() {
        //ksy library
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
        if (mStreamer != null) mStreamer.onPause();
        if (!mIsEndStream) {
            if (mStreamer != null) mStreamer.stopCameraPreview();
        }

        onRTCCallHandle("");
        if (mStreamer != null) {
            if (mImgFaceunityFilter != null)
                mStreamer.getCameraCapture().mImgBufSrcPin.disconnect(mImgFaceunityFilter.getBufSinkPin(), false);
            mImgFaceunityFilter = null;
            mProFilter = null;
            mStreamer.setOnRTCInfoListener(null);
            mStreamer.getImgTexFilterMgt().setOnErrorListener(null);
            mStreamer.setOnLogEventListener(null);
            mStreamer.setOnErrorListener(null);
            mStreamer.setOnInfoListener(null);
            mStreamer.release();
            mStreamer = null;
        }
    }


//    @Override
//    public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
//        mConnectionClass = bandwidthState;
//        LogUtils.logE(TAG, "ConnectionQuality " + bandwidthState.toString());
//        if (bandwidthState == ConnectionQuality.POOR) {
//            Toast.makeText(this, "low net work average upload now < 400Kpbs", Toast.LENGTH_LONG).show();
//        } else if (bandwidthState == ConnectionQuality.MODERATE) {
//            Toast.makeText(this, "net work now average upload ~ 400Kpbs", Toast.LENGTH_LONG).show();
//        } else if (bandwidthState == ConnectionQuality.GOOD) {
//            Toast.makeText(this, "net work now average upload ~ 800Kpbs", Toast.LENGTH_LONG).show();
//        } else if (bandwidthState == ConnectionQuality.EXCELLENT) {
//            Toast.makeText(this, "net work now average upload ~ 2000Kpbs", Toast.LENGTH_LONG).show();
//        }
//    }

    private void handleView() {
        mStreamPresenter.getTagCategories();
        init();
        hideAllView();
    }


    @Override
    public void onBackPressed() {
        if (mIsTutorialShowing) return;
        if (etComment.isFocused()) etComment.clearFocus();
        if (isCommentsLayoutShowing) {
            showUserActions();

        } else if (isStreamReady) {
            showStreamEndDialog();
        }
    }

    private void showUserActions() {
        userActionsWholeContainer.setVisibility(View.VISIBLE);
        isCommentsLayoutShowing = false;
        //hide bottom button bar if any
        if (getWindow() != null) showFullScreen(getWindow());
    }

    // UI updates must run on MainThread
    private boolean mIsEndStream;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NewMessageEvent event) {
        if (event.getData() != null && !mIsEndStream) {
            ChatItemModelClass chatItemModelClass = event.getData();
            switch (chatItemModelClass.getType()) {
                case ChatItemModelClass.CHAT_TYPE_LIKE:
                    if (chatItemModelClass.isLiked()) {
                        Timber.e("chatItemModelClass.isLiked() %s", chatItemModelClass.getUserName());
                        if (mVotingbarProgressObservable != null)
                            mVotingbarProgressObservable.onNext(chatItemModelClass.getVotingScores());
                        topfanFinalCheck(chatItemModelClass);

                        storeChatMessages(chatItemModelClass, RecordedMessagesModel.TYPE_LIKE);
                        chatItemModelObservable.onNext(chatItemModelClass);
                    }
                    showHeartObservable.onNext(null);
                    break;

                case ChatItemModelClass.CHAT_TYPE_GIFT:
                    updateTopFansList(chatItemModelClass.topFanList);
                    /* support old version - can remove if users upgraded to 1.3.5  */
                    topfanFinalCheck(chatItemModelClass);
                    onGiftReceived(chatItemModelClass);
                    break;

                case ChatItemModelClass.CHAT_TYPE_LIVE_COMMERCE_ANNOUNCEMENT:
                    chatItemModelObservable.onNext(chatItemModelClass);
                    storeChatMessages(chatItemModelClass, RecordedMessagesModel.TYPE_LIVE_COMMERCE_ANNOUNCEMENT);
                    break;

                case ChatItemModelClass.CHAT_TYPE_BOT_JOIN_LIST:
                    updateWatcherList();
                    break;

                case ChatItemModelClass.CHAT_TYPE_END:
                    //only admin can end host stream
                    showEndStreamAndToastMessage(chatItemModelClass);
                    break;

                case ChatItemModelClass.CHAT_TYPE_FOLLOW:
                    topfanFinalCheck(chatItemModelClass);
                    showFollowMessage(chatItemModelClass);
                    storeChatMessages(chatItemModelClass, RecordedMessagesModel.TYPE_FOLLOW);
                    break;

                case ChatItemModelClass.CHAT_TYPE_SHARE_STREAM:
                    topfanFinalCheck(chatItemModelClass);
                    showShareStreamMessage(chatItemModelClass);
                    storeChatMessages(chatItemModelClass, RecordedMessagesModel.TYPE_SHARE);
                    break;
                case ChatItemModelClass.CHAT_TYPE_STATISTIC:
                    if (mStreamPresenter != null)
                        mStreamPresenter.onStreamStatisticReceived(chatItemModelClass);
                    updateTopFansList(chatItemModelClass.topFanList);
                    updateSubStreamState(chatItemModelClass.subStreamData);
                    break;
                default:
                    if (chatItemModelClass.isGroup) {//CHAT_TYPE_MESSAGE
                        topfanFinalCheck(chatItemModelClass);
                        storeChatMessages(chatItemModelClass, RecordedMessagesModel.TYPE_MESSAGE);
                        chatItemModelObservable.onNext(chatItemModelClass);
                    }
            }
        }
    }

    private void updateSubStreamState(SubStreamData subStreamData) {
        if (subStreamData == null) return;
        if (vsSubPlayer.getParent() != null && mSubPlayerLayout == null) {
            View view = vsSubPlayer.inflate();
            mSubPlayerLayout = new SubPlayerLayout(view);
            updateSubPlayerLayoutPosition();
            mSubPlayerLayout.setRole(Role.HOST);
            mSubPlayerLayout.setClickListener(this);
            mSubPlayerLayout.setGuestAvatar(subStreamData.userImage);
            mSubPlayerLayout.setGuestDisplayName(subStreamData.displayName);
            mSubPlayerLayout.updateState(State.CONNECTING);
        }

        removeCallWaitingTimeout();
        if (subStreamData.status == State.DISCONNECTING) {
            mGuestAlreadyEndedCall.set(true);
            if (mSubPlayerLayout != null) mSubPlayerLayout.updateState(subStreamData.status);
            if (mLiveEndConfirmation != null) mLiveEndConfirmation.dismiss();
//            mSubPlayerLayout.stopPlayer();
        } else {
            if (subStreamData.status == State.DISCONNECTED) {
                updateSubStreamDisconnected();
            } else if (subStreamData.status == State.REJECT) {
                //guest rejected leave rtc
                showToast(getString(R.string.vid_call_declined), Toast.LENGTH_SHORT);
                onRTCCallHandle("");
                mGuestAlreadyEndedCall.set(true);
                mSubPlayerLayout.updateState(subStreamData.status);
                if (mStreamPresenter != null) {
                    mStreamPresenter.notifySubStreamRejected();
                }
            } else {
                if (vsSubPlayer.getVisibility() != View.VISIBLE)
                    vsSubPlayer.setVisibility(View.VISIBLE);
                mSubPlayerLayout.setGuestAvatar(subStreamData.receiver.userImage);
                mSubPlayerLayout.setGuestDisplayName(subStreamData.receiver.displayName);
                mSubPlayerLayout.setGuestUserName(subStreamData.receiver.userName);
//            mSubPlayerLayout.playSubStream(subStreamData.streamUrl);
                mSubPlayerLayout.updateState(subStreamData.status);
            }
        }
    }

    private void removeCallWaitingTimeout() {
        RxUtils.unsubscribeIfNotNull(mTimeoutSubscription);
        mIsCallWaitingTimeoutEnable.set(false);
    }

    private void topfanFinalCheck(ChatItemModelClass chatItemModelClass) {
        /* support old version - can remove if users upgraded to 1.3.5  */
//        if (chatItemModelClass.rank == -1) {
        chatItemModelClass.rank = mapWithCurrentTopFan(chatItemModelClass.getUserName());
//        }
    }

    private void updateTopFansList(List<String> topFanList) {
        if (topFanList.isEmpty() || mTopFanList.equals(topFanList)) return;
        mTopFanList.clear();
        mTopFanList.addAll(topFanList);
    }

    private void showShareStreamMessage(ChatItemModelClass chatItemModelClass) {
        chatItemModelObservable.onNext(chatItemModelClass);
    }

    private void showFollowMessage(ChatItemModelClass chatItemModelClass) {
        chatItemModelObservable.onNext(chatItemModelClass);
    }

    private void showEndStreamAndToastMessage(ChatItemModelClass chatItemModelClass) {
        if (chatItemModelClass != null && chatItemModelClass.getUserName().equalsIgnoreCase(ADMIN_USERNAME)) {
            mIsEndStreamByAdmin = true;
            mStreamPresenter.streamRemovedByAdmin(chatItemModelClass);
        }
    }

    // UI updates must run on MainThread
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UserJoinLeaveEvent event) {
        if (mIsEndStream) {
            return;
        }
        if (mCurrentUser != null && mCurrentUser.getUserName().equalsIgnoreCase(event.getUserName()))
            return;

        Timber.e(event.toString());
        watcherAddEventObservable.onNext(event);
        mStreamPresenter.requestViewCountUpdate();
    }

    private void onGiftReceived(ChatItemModelClass chatItemModelClass) {
        long totalStars = 0;
        try {
            if (mVotingbarProgressObservable != null)
                mVotingbarProgressObservable.onNext(chatItemModelClass.getVotingScores());
            totalStars = Long.parseLong(chatItemModelClass.getReceiverStars());
        } catch (Exception e) {
            Timber.d(e);
        }

//        mStreamPresenter.updateTotalStars(totalStars);
        updateStars(totalStars);

        onDailyTopFansListReceivedFromXmpp(chatItemModelClass.dailyTopFansList);

        if (chatItemModelClass.isGroup) {
            if (chatItemModelClass.isExpensiveGift()) {
                chatItemModelObservable.onNext(chatItemModelClass);
                showExpensiveGift(chatItemModelClass);
                storeChatMessages(chatItemModelClass, RecordedMessagesModel.TYPE_GIFT);
            } else {
                llGiftGroup.addGift(chatItemModelClass);
                llGiftGroup.setGiftComboGroupViewListener(item -> {
                    topfanFinalCheck(item);
                    chatItemModelObservable.onNext(item);
                    storeChatMessages(item, RecordedMessagesModel.TYPE_GIFT);
                });
            }
        }
    }

    private void onDailyTopFansListReceivedFromXmpp(List<DailyTopFanModel> dailyTopFansList) {
        if (dailyTopFansList != null) {
            mGiftRankingGroupView.updateRankingList(mIsTriviaShow ? Collections.EMPTY_LIST : dailyTopFansList);

        } else {
            //for supporting versions below v1.4.11 which can send daily-top-fans-list
            //we have to call api to keep the list update to date
//            mGetDailyTopFansListObservable.onNext(null);
        }
    }

    private void showExpensiveGift(ChatItemModelClass chatItemModelClass) {
        if (mExpensiveGiftDialog == null) {
            mExpensiveGiftDialog = new ExpensiveGiftDialog();
            mExpensiveGiftDialog.setDialogDismissCallback(() -> mCompositeSubscription.add(Observable.just(null)
                    .delay(100, TimeUnit.MILLISECONDS)
                    .subscribe(o -> {
                        if (!mGiftQueue.isEmpty()) {
                            mExpensiveGiftDialog.addGift(mGiftQueue.get(0));
                            mGiftQueue.remove(0);
                            mExpensiveGiftDialog.show(getSupportFragmentManager(), ExpensiveGiftDialog.class.getName());
                        } else {
                            isExpensiveGiftDialogShown = false;
                        }
                    })));
        }
        //if the expensive gift dialog is showing then queue the incoming gift

        if (isExpensiveGiftDialogShown) {
            Timber.d("-- expensive is showing");
            mGiftQueue.add(chatItemModelClass);
        } else {
            mExpensiveGiftDialog.addGift(chatItemModelClass);
            mExpensiveGiftDialog.show(getSupportFragmentManager(), ExpensiveGiftDialog.class.getName());
            isExpensiveGiftDialogShown = true;
        }
    }

    private void showHearts() {
        if (periscope != null) periscope.addHeart();
    }


    private void storeStreamTitle(String title, float percentX, float percentY, String color) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        StreamTitleSticker streamTitle = new StreamTitleSticker();
        streamTitle.mStreamTitleStickerX = percentX;
        streamTitle.mStreamTitleStickerY = percentY;
        streamTitle.mStreamTitleStickerContent = title;
        streamTitle.mStreamTitleColorCode = color;
        RecordedMessagesModel recordedMessagesModel = new RecordedMessagesModel();
        recordedMessagesModel.setStreamTitleSticker(streamTitle);
        recordedMessagesModel.setRecordedTime((int) (getStreamDuration()));

        if (mRecordedMessagesModels == null) {
            mRecordedMessagesModels = new ArrayList<>();
        }
        mRecordedMessagesModels.add(recordedMessagesModel);
        Timber.d("stream title stored %s", title);
    }

    //region recorded chat message
    void storeChatMessages(ChatItemModelClass chatItemModelClass, @RecordedMessagesModel.ActionType int actionType) {
        if (!isFinishing() && !isDestroyed()) {
            mRecordedMessagesModels.add(createSavedMessageItem(chatItemModelClass, actionType));
        }
    }

    private RecordedMessagesModel createSavedMessageItem(ChatItemModelClass chatItemModelClass, @RecordedMessagesModel.ActionType int actionType) {

        RecordedMessagesModel recordedMessagesModel = new RecordedMessagesModel();
        recordedMessagesModel.setActionType(actionType);
        recordedMessagesModel.setMessage(chatItemModelClass.getMsg());
        recordedMessagesModel.setRecordedTime((int) (getStreamDuration()));
        recordedMessagesModel.setDisplayName(chatItemModelClass.getChatDisplayName());
        recordedMessagesModel.setUserName(chatItemModelClass.getUserName());
        recordedMessagesModel.setProfilePic(chatItemModelClass.getProfilePic());
        recordedMessagesModel.rank = chatItemModelClass.rank;
        if (actionType == RecordedMessagesModel.TYPE_GIFT /*Gift*/) {
            recordedMessagesModel.setGiftId(chatItemModelClass.getGiftId());
            recordedMessagesModel.setGiftImage(chatItemModelClass.getGiftImage());
            recordedMessagesModel.setGiftComboQuantity(chatItemModelClass.getGiftCombo());
            recordedMessagesModel.setGiftName(chatItemModelClass.getGiftName());
        }
        if (!StringUtil.isNullOrEmptyString(chatItemModelClass.getProfileColor())) {
            recordedMessagesModel.setProfileColor(chatItemModelClass.getProfileColor());
        }
        return recordedMessagesModel;
    }

    //endregion


    private void updateWatcherList() {
        if (mIsTriviaShow) return;
        Timber.d("** updateWatcherList");
        List<String> currentUserInStream = mIsTriviaShow? mAgoraChatManager.getArrayCurrentUserInStream() :mChatManager.getArrayCurrentUserInStream();
        if (currentUserInStream == null) {
            return;
        }
        Timber.d("** joined watchers %s", currentUserInStream.size());
        listUserAdapter.updateList(userWatcherListView, currentUserInStream);
    }

    private void transformJointEventToMessage(List<UserJoinLeaveEvent> newWatchers) {
        mCompositeSubscription.add(Observable.from(newWatchers)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .filter(newWatcher -> !TextUtils.isEmpty(newWatcher.getUserName()) && !mChatGroupAdapter.isUserHasJoined(newWatcher.getUserName()) && newWatcher.isJoined())
                .flatMap(new Func1<UserJoinLeaveEvent, Observable<ChatItemModelClass>>() {
                    @Override
                    public Observable<ChatItemModelClass> call(UserJoinLeaveEvent userJoinLeaveEvent) {
                        ChatItemModelClass chatItem = new ChatItemModelClass();
                        chatItem.setUserName(userJoinLeaveEvent.getUserName());
                        chatItem.setChatDisplayName(userJoinLeaveEvent.getDisplayName());
                        chatItem.setMessageType(ChatItemModelClass.CHAT_TYPE_USER_JOIN_LIST);
                        chatItem.setMsg(getString(R.string.message_joined));
                        return Observable.just(chatItem);
                    }
                })
                .doOnNext(chatItemModelClass -> {
                    chatItemModelClass.rank = mapWithCurrentTopFan(chatItemModelClass.getUserName());
                    storeChatMessages(chatItemModelClass, RecordedMessagesModel.TYPE_JOIN);
                    notifyTopFanJoined(chatItemModelClass);
                })
                .toList()
                .filter(chatItemModelClasses -> !chatItemModelClasses.isEmpty())
                .subscribe(this::updateBufferedMessageInList, throwable -> LogUtils.logE(TAG, throwable.getMessage())));
    }

    private int mapWithCurrentTopFan(String userName) {
        return mTopFanList.indexOf(userName);
    }

    private void updateMessageInList(ChatItemModelClass chatItemModelClass) {
        mChatGroupAdapter.newChatItem(chatItemModelClass);
        scrollCommentListToEnd();
    }

    /**
     * add a list of messages to chat-box-message
     *
     * @param chatItemModelClasses list of messages that buffered
     */
    @Override
    public void updateBufferedMessageInList(List<ChatItemModelClass> chatItemModelClasses) {
//        Timber.e("updateBufferedMessageInList thread %d", (Thread.currentThread().getId()));
        mChatGroupAdapter.newListChatNotClear(chatItemModelClasses);
        scrollCommentListToEnd();
    }


    private void init() {
        initListChat(mCurrentUser.getDisplayName());
        initStartViews();
        bindEvents();
    }

    private void bindEvents() {
        bindStartLiveStream();
        updateStars(mCurrentUser.getTotalGoldFans());

    }


    private void initListUserWatcher() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        userWatcherListView.setLayoutManager(layoutManager);
        listUserAdapter = new AdapterListWatcher(StreamingActivityGLPlus.this);
        userWatcherListView.setAdapter(listUserAdapter);
//        userWatcherListView.setHasFixedSize(true);
        //prevent blinking when watcher list updated
//        RecyclerView.ItemAnimator animator = userWatcherListView.getItemAnimator();
//        if (animator instanceof SimpleItemAnimator) {
//            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
//            Timber.d("setSupportsChangeAnimations false");
//        }
        RecycleItemClickSupport.addTo(userWatcherListView).setOnItemClickListener((recyclerView, position, v) -> {
            String userName = listUserAdapter.getItemAt(position);
            showUserProfile(userName, "", mSlug);
//            DialogUserProfileFragment userProfileFragment = DialogUserProfileFragment.getInstance(watcher.getWatcher_userName(), watcher.getWatcher_image(),false);
//            userProfileFragment.show(getSupportFragmentManager(), USER_DIALOG_TAG);
        });

    }

    void gotoProfileScreen(int userId, String displayName) {
        //not allow host to go to guest profile
//            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left);
//            startActivityForResult(UserProfileActivity.newIntent(this, String.valueOf(userId), displayName),
//                    Constants.REQUEST_CODE_VIEW_USER_PROFILE, options.toBundle());
    }

    private void showUserProfile(String userName, String userImage, String slug) {
        if (mIsTriviaShow) return;
        boolean isHostProfile = userName.equals(mCurrentUser.getUserName());

        DialogUserProfileFragment userProfileFragment = DialogUserProfileFragment.newInstance(userName, userImage, false, slug, !mConversationWithUser.isEmpty(), isHostProfile);
        userProfileFragment.setUserProfileActionListener(this);
        getSupportFragmentManager().beginTransaction().add(userProfileFragment, USER_DIALOG_TAG).commitAllowingStateLoss();

//        userProfileFragment.show(getSupportFragmentManager(), USER_DIALOG_TAG);
    }

    private void hideUserProfileDialog() {
        try {
            final DialogUserProfileFragment profileFragment = (DialogUserProfileFragment) getSupportFragmentManager().findFragmentByTag(USER_DIALOG_TAG);
            if (profileFragment != null) {
                profileFragment.dismiss();
            }
        } catch (IllegalStateException e) {
            Timber.e(e);
        }
    }

    private void initStartViews() {
        ttFacebook.setTextColor(ContextCompat.getColor(this, R.color.white));
        txtTwitter.setTextColor(ContextCompat.getColor(this, R.color.white));
//        txtInstagram.setTextColor(ContextCompat.getColor(this, R.color.white));
        txtWhatsapp.setTextColor(ContextCompat.getColor(this, R.color.white));
        txtEmail.setTextColor(ContextCompat.getColor(this, R.color.white));

//        recordStreamCb.setBackgroundResource(R.drawable.savevideo_off);

        UserModel loginUser = AppsterApplication.mAppPreferences.getUserModel();
        if (loginUser.isDevUser()) {
            mCheckBoxExternalCam.setVisibility(View.VISIBLE);
            mTriviaShow.setVisibility(View.VISIBLE);
        }

    }

    //region on screen button click
    public void onScreenClicked() {
        Timber.e("onScreenClicked");
        mInputMethodManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
        if (etComment.isFocused()) {
            etComment.clearFocus();
        } else {
            showUserActions();
            handleHideShowView();
        }

    }

    @OnClick(R.id.ivStreamCover)
    public void onCoverSelected() {
        if (Utils.hasDoNotAskAgainPermissions(this, false, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // This permission is denied by user. Show dialog to guide user enable it
            showPermissionGuideDialog(getString(R.string.permission_storage), 1, false);
            return;
        }
        mCompositeSubscription.add(mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        takePictureFromGallery();
                    }
                    handlePermissionResult(Utils.fillPermissionResult(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            false, false));
                    mHandledPermissionCheck = true;
                }));
    }


//    @OnClick(R.id.periscope)
//    public void onPeriscopeClicked() {
//        if(mCameraView!=null) mCameraView.performClick();
//    }

    @OnClick(R.id.txt_stars)
    public void showTopFans() {
        topFanDialog = TopFanDialog.newInstance(StreamingActivityGLPlus.this, mCurrentUser, true, false);
        topFanDialog.setUserProfileActionListener(this);
        topFanDialog.show();
    }

    @OnClick(R.id.ibShareStream)
    void onIbShareStreamClick() {
        showChooseShareType();
    }

    @OnClick(R.id.tt_facebook)
    public void shareFabceBook() {
        ttFacebook.setSelected(!ttFacebook.isSelected());
        if (ttFacebook.isSelected()) {
            ttFacebook.setAlpha(1f);
            mShareOption = FACEBOOK;
        } else {
            ttFacebook.setAlpha(0.5f);
            mShareOption = -1;
        }
//        SocialManager.getInstance().shareURLToFacebook(this, getUserWebProfile());
//        // Track event
//        EventTracker.trackShareFacebook();
    }

    @OnClick(R.id.txt_email)
    public void shareEmail() {
        txtEmail.setSelected(!txtEmail.isSelected());
        if (txtEmail.isSelected()) {
            txtEmail.setAlpha(1f);
        } else {
            txtEmail.setAlpha(0.5f);
        }
//        SocialManager.getInstance().shareURLToEmail(this, getUserWebProfile());
//        //
//        // Track event
//        EventTracker.trackShareEmail();
    }

    @OnClick(R.id.txt_whatsapp)
    public void shareWhatapps() {
        txtWhatsapp.setSelected(!txtWhatsapp.isSelected());
        if (txtWhatsapp.isSelected()) {
            txtWhatsapp.setAlpha(1f);
        } else {
            txtWhatsapp.setAlpha(0.5f);
        }
//        SocialManager.getInstance().shareVideoToWhatsapp(this, getUserWebProfile());
//        // Track event
//        EventTracker.trackShareWhatsApp();
    }

    @OnClick(R.id.txt_twitter)
    public void shareTwitter() {
        txtTwitter.setSelected(!txtTwitter.isSelected());
        if (txtTwitter.isSelected()) {
            txtTwitter.setAlpha(1f);
            mShareOption = TWITTER;
        } else {
            txtTwitter.setAlpha(0.5f);
            mShareOption = -1;
        }
//        SocialManager.getInstance().ShareFeedQuotesToTwitter(this, "", getUserWebProfile());
//        // Track event
//        EventTracker.trackShareTwitter();
    }

    @OnClick(R.id.tt_location)
    public void shareLocation() {
        // turn location off does not need to checking the permission
        if (txtLocation.isSelected()) {
            txtLocation.setSelected(false);
            txtLocation.setAlpha(0.5f);
            return;
        }
        if (Utils.hasDoNotAskAgainPermissions(this, false, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // This permission is denied by user. Show dialog to guide user enable it
            showPermissionGuideDialog(getString(R.string.permission_location), 1, false);
            return;
        }
        mCompositeSubscription.add(mRxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    txtLocation.setSelected(granted);
                    if (granted) {
                        txtLocation.setAlpha(1f);
                    } else {
                        txtLocation.setAlpha(0.5f);
                    }
                    handlePermissionResult(Utils.fillPermissionResult(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            false, false));
                    mHandledPermissionCheck = true;
                }));

//        SocialManager.getInstance().ShareFeedQuotesToTwitter(this, "", getUserWebProfile());
//        // Track event
//        EventTracker.trackShareTwitter();
    }


    @OnTouch(R.id.commentsList)
    public boolean onTouchListChat() {
//        if (isCommentsLayoutShowing && (listItemChat == null || listItemChat.isEmpty())) {
//            mInputMethodManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
//            showUserActions();
//        }
        if (etComment.isFocused()) {
            etComment.clearFocus();
            showUserActions();
        }
        return false;
    }

    public void turnOnOffFlash() {
        if (iFlashOpened) {
            mStreamer.toggleTorch(false);
            iFlashOpened = false;
        } else if (mStreamer.isTorchSupported()) {
            mStreamer.toggleTorch(true);
            iFlashOpened = true;
        }
        onOffFlash.setImageResource(iFlashOpened ? R.drawable.icon_flash_on : R.drawable.icon_flash_off);
        if (mMoreLiveOptionsPopUp != null) {
            mMoreLiveOptionsPopUp.setFlashOpened(iFlashOpened);
        }
    }

//    @OnClick(R.id.txt_record)
//    public void handleRecordButton() {
//        isSavedVideo = !isSavedVideo;
//
//        if (isSavedVideo) {
//            txtSavedVideo.setCustomDrawableStart(R.drawable.icon_recorded_on);
//            txtSavedVideo.setText(R.string.live_trim_record);
//
//        } else {
//            txtSavedVideo.setCustomDrawableStart(R.drawable.icon_recorded_off);
//            txtSavedVideo.setText(R.string.live_trim_not_recorded);
//        }
//    }

    @OnClick(R.id.icon_swith_camera)
    public void switchCamera() {
        if (mIsTriviaShow || mTriviaShow.isChecked()) return;
        if (mStreamer.isFrontCamera()) {
            onOffFlash.setAlpha(1f);
        } else {
            mStreamer.toggleTorch(false);
            iFlashOpened = false;
            onOffFlash.setImageDrawable(ContextCompat.getDrawable(StreamingActivityGLPlus.this, R.drawable.icon_flash_off));
            onOffFlash.setAlpha(0.5f);
        }
        mCameraHintView.hideAll();
        mStreamer.switchCamera();
        boolean isFrontCamera = !mStreamer.isFrontCamera();
        if (isFrontCamera) {
            if (mImgFaceunityFilter != null) mImgFaceunityFilter.setMirror(true);
            mStreamer.setRemoteMirror(true);
        } else {
            if (mImgFaceunityFilter != null) mImgFaceunityFilter.setMirror(false);
            mStreamer.setRemoteMirror(false);
        }
//        mCameraView.switchCamera();
//        onOffFlash.setVisibility(!mCameraView.isCameraBackForward() ? View.GONE : View.VISIBLE);
//        if (iFlashOpened) {
//            mCameraView.setFlashLightMode(Camera.Parameters.FLASH_MODE_OFF);
//        }
//        setCameraFocusMode();
    }

    @OnClick(R.id.ibCloseStream)
    public void cancelLive() {
        if (mIsTutorialShowing) return;
        if (isStreamReady) {
            showStreamEndDialog();
        } else {
            finish();
        }
    }


    @OnEditorAction(R.id.etComment)
    public boolean editorListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            sendChatMessage();
        }
        return false;
    }

    @OnClick(R.id.ibSendComment)
    public void sendChatMessage() {
        Timber.d("sendChatMessage");
//        mInputMethodManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
        showUserActions();
        String comment = removeNaughtyWords(etComment.getText().toString());
        if (comment.trim().equalsIgnoreCase("") || comment.equals("")) return;
        etComment.setText(null);
//        etComment.clearFocus();

        mStreamPresenter.sendMessage(comment);
    }

    @OnClick(R.id.img_more_options)
    public void onMoreOptionClicked() {
        if (mMoreLiveOptionsPopUp == null) {
            mMoreLiveOptionsPopUp = MoreLiveOptionsPopUp.newInstance(this);
            mMoreLiveOptionsPopUp.setListener(new MoreLiveOptionsPopUp.MoreLiveOptionsPopUpListener() {
                @Override
                public void onFlashOptionSelected() {
                    turnOnOffFlash();
                }

                @Override
                public void onBeautyOptionSelected() {
                    beautyMode();
                }
            });
            mMoreLiveOptionsPopUp.setOnDismissListener(() -> {
                if (mBtnMoreOptions != null)
                    mBtnMoreOptions.setImageResource(R.drawable.ic_live_host_option_up);
            });
        }
        mMoreLiveOptionsPopUp.setFlashOpened(iFlashOpened);
        mMoreLiveOptionsPopUp.setBeautyOpened(mode == MODE_BEAUTY);
        mMoreLiveOptionsPopUp.setShouldEnableFlashButton(!mStreamer.isFrontCamera());
        showHideLiveOptionsPopup();
    }

    @OnClick(R.id.onOffBeauty)
    public void onOffBeauty() {
        beautyMode();
    }

    private void showHideLiveOptionsPopup() {
        if (mMoreLiveOptionsPopUp == null) return;
        if (mMoreLiveOptionsPopUp.isShowing()) {
            mMoreLiveOptionsPopUp.dismiss();
        } else {
            mMoreLiveOptionsPopUp.setFocusable(false);
            mMoreLiveOptionsPopUp.update();
            PopupWindowCompat.showAsDropDown(mMoreLiveOptionsPopUp, mBtnMoreOptions, 0, MORE_OPTION_WINDOW_POPUP_Y_OFSET, Gravity.TOP | Gravity.START);

            mBtnMoreOptions.setImageResource(R.drawable.ic_live_host_option_down);
            showFullScreen(mMoreLiveOptionsPopUp.getContentView());
            mMoreLiveOptionsPopUp.setFocusable(true);
            mMoreLiveOptionsPopUp.update();

        }
    }

//    @OnClick(R.id.btn_face_unity_sticker)
//    public void onFaceUnityButtonClicked() {
//        showProgress();
//        mStreamPresenter.getFaceUnityStickerList();
//    }

    @Override
    public void onGetFaceUnityStickerSuccessfully(List<FaceUnityStickerModel> data) {
        if (mFaceUnityDialogPicker == null) {
            mFaceUnityDialogPicker = FaceUnityDialogPicker.newInstance();
            mFaceUnityDialogPicker.setShouldShowStatusBar(true);
            mFaceUnityDialogPicker.setListener(item -> {
                if (mStickerModel != null && mStickerModel.getId() == item.getId()) return;
                mStickerModel = item;
                if (item.isAvailable()) {
                    boolean shouldEnableFaceunity = !item.getFile().isEmpty() || item.getId() != 0;
                    mFaceUnityStateObservable.onNext(shouldEnableFaceunity);
                    openFaceUnityWithLink(item.getId(), item.getFile());
                }
                Timber.d("on sticker item clicked - %s", item.getFile());
            });
        }
        if (!mFaceUnityDialogPicker.isAdded() || !mFaceUnityDialogPicker.isVisible()) {
            mFaceUnityDialogPicker.setFaceUnityStickerList(data);
            mFaceUnityDialogPicker.setSelectedStickerModel(mStickerModel);
            mFaceUnityDialogPicker.show(getSupportFragmentManager(), FaceUnityDialogPicker.class.getName());
        }
        Timber.d("onGetFaceUnityStickerSuccessfully %d", data.size());
    }

    @Override
    public void onGetFaceUnityStickerFailed(String message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    void hideTagCateGoryLayout() {
        if (tagModelList != null && !tagModelList.isEmpty()) {
            int selectedTagPosition = mPreLiveLayout.tagGroup.getSlectedTagViewPosition();
            if (selectedTagPosition != TagGroup.NO_TAG_SELECTED) {
                TagListLiveStreamModel selectedTag = tagModelList.get(selectedTagPosition);
                txtCategory.setText(selectedTag.getTagName());
                TagId = selectedTag.getTagId();
            } else {
                TagId = tagModelList.get(tagModelList.size() - 1).getTagId();
                txtCategory.setText(getString(R.string.live_stream_category));
            }
        }
        switcher.setInAnimation(this, R.anim.slide_in_left);
        switcher.setOutAnimation(this, R.anim.slide_out_right);
        switcher.showPrevious();
        cancelLive.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.txt_category, R.id.img_category_arrow})
    void showTagCategoryLayout() {
        Utils.hideSoftKeyboard(this);
        cancelLive.setVisibility(View.GONE);
        if (viewStub.getParent() != null) {
            View preLiveView = viewStub.inflate();
            mPreLiveLayout = new PreLiveLayout(preLiveView, tagModelList, new PreLiveLayout.TagScreenListener() {
                @Override
                public void onTagClick() {
                    hideTagCateGoryLayout();
                }

                @Override
                public void onBackClick() {
                    hideTagCateGoryLayout();
                }
            });
        }
        switcher.setInAnimation(this, R.anim.slide_in_right);
        switcher.setOutAnimation(this, R.anim.slide_out_left);

        switcher.showNext();

    }

    //endregion

    private void showChooseShareType() {
        if (sharePostDialog == null) {
            sharePostDialog = SharePostDialog.newInstance(true);
            sharePostDialog.setHideShareInstagramView();
            sharePostDialog.setHideCopyLinkView(false);
            sharePostDialog.setDialogDismisListener(null);
            sharePostDialog.setChooseShareListenner(new SharePostDialog.ChooseShareListenner() {
                @Override
                public void chooseShareFacebook() {
                    SocialManager.getInstance().shareURLToFacebook(StreamingActivityGLPlus.this, mStreamDetail.getWebStreamUrl());
                }

                @Override
                public void chooseShareInstagram() {
                    //can share url to instagram.
                    //so this feature is disabled.
                }

                @Override
                public void chooseShareTwtter() {
                    SocialManager.getInstance().ShareFeedQuotesToTwitter(StreamingActivityGLPlus.this, mStreamDetail.getWebStreamUrl(),
                            mStreamDetail.getPublisher().getUserName(), mStreamDetail.titlePlainText, SocialManager.SHARE_TYPE_STREAM, true);
                }

                @Override
                public void chooseShareEmail() {
                    SocialManager.getInstance().shareURLToEmail(StreamingActivityGLPlus.this, mStreamDetail.getWebStreamUrl(),
                            mStreamDetail.getPublisher().getUserName(), mStreamDetail.titlePlainText, SocialManager.SHARE_TYPE_STREAM, true);
                }

                @Override
                public void chooseShareWhatApp() {
                    SocialManager.getInstance().shareVideoToWhatsapp(StreamingActivityGLPlus.this,
                            mStreamDetail.getWebStreamUrl(), mStreamDetail.getPublisher().getUserName(), mStreamDetail.titlePlainText, SocialManager.SHARE_TYPE_STREAM, true);
                }

                @Override
                public void copyLink() {
                    CopyTextUtils.CopyClipboard(getApplicationContext(), mStreamDetail.getWebStreamUrl(), getString(R.string.share_link_copied));
                }

                @Override
                public void chooseShareOthers() {
                    SocialManager.getInstance().shareURLToOthers(StreamingActivityGLPlus.this, mStreamDetail.getWebStreamUrl(),
                            mStreamDetail.getPublisher().getUserName(), mStreamDetail.titlePlainText, SHARE_TYPE_STREAM, true);
                }
            });
        }
        sharePostDialog.show(getSupportFragmentManager(), "Share");

    }

    private void showProcessView() {
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideProcessView() {
        mProgress.setVisibility(View.GONE);
    }

    boolean isClickedGoLive;
    boolean isCheckingNetworkBandwidth;
    ProgressDialog progress;

    private void bindStartLiveStream() {
        mCompositeSubscription.add(AppsterUtility.clicks(startStreamBtn).subscribe(aVoid -> {
            if (!Utils.hasAllPermissionsGranted(this, REQUIRED_PERMISSIONS)) {
                // this should not happen since we already check this condition at the onResume method.
                // If this happens before the onResume method, it must be an error and safe to swallow.
                // Simply, just return. The permission checking is happening very soon
                return;
            }
            if (checkTitleIsAvailable()) {
                isClickedGoLive = true;
                mInputMethodManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
                if (Connectivity.getConnectedState(this) != Connectivity.NetworkStatus.WIFI && CheckNetwork.isNetworkAvailable(this)) {
                    utility.showMessage("Tips!", getString(R.string.start_stream_poor_connection), StreamingActivityGLPlus.this, v -> checkNetworkBandwidthAndGoLive());
                } else {
                    checkNetworkBandwidthAndGoLive();
                }
            } else {
                showToast(getString(R.string.stream_title_not_available), Toast.LENGTH_SHORT);
            }
        }, error -> LogUtils.logE(TAG, error.getMessage())));

    }

    private boolean checkTitleIsAvailable() {
        return mEdtStreamTitle != null && !String.valueOf(mEdtStreamTitle.getText()).trim().isEmpty();
    }

    void checkNetworkBandwidthAndGoLive() {
        if (mConnectionClass == UNKNOWN || mConnectionClass == ConnectionQuality.POOR) {
            if (!isCheckingNetworkBandwidth) {
                progress = ProgressDialog.show(this, "", getString(R.string.message_check_network), true);
                startCheckNetworkUploadSpeed();
            }
        } else {
            goLive();
        }
    }


    private void goingLiveStream() {
        if (progress != null) {
            progress.setMessage(getString(R.string.message_prepare_stream));
        } else {
            progress = ProgressDialog.show(this, "", getString(R.string.message_prepare_stream), true);
        }
        mStreamTitle = mEdtStreamTitle.getText().toString();
        mStreamPresenter.createStream(mStreamTitle, TagId, isSavedVideo, 450, mTriviaShow.isChecked());
    }


    private void visibleViewWhenStartLive() {

//        cancelLive.setVisibility(View.VISIBLE);
//        actionButtonsControll.setVisibility(View.VISIBLE);
        userActionsWholeContainer.setVisibility(View.VISIBLE);

//        cancelLive.setVisibility(View.GONE);
        startStreamBtn.setVisibility(View.GONE);
        mEdtStreamTitle.setVisibility(View.GONE);

        showAllView();
        etComment.clearFocus();
//        initListUserWatcher();
    }


    private void initListChat(String displayName) {
        listItemChat = new ArrayList<>();
        ChatItemModelClass modelClass = new ChatItemModelClass();
        modelClass.setChatDisplayName("warning_message");
        modelClass.setLiked(false);
        listItemChat.add(modelClass);

        mChatGroupAdapter = new ChatGroupDelegateAdapter(listItemChat, this, displayName);
//        mChatGroupAdapter.setChatGroupClickListener(this);
        commentLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        commentsListView.setLayoutManager(commentLayoutManager);
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
        commentsListView.setAdapter(mChatGroupAdapter);
        commentsListView.setVisibility(View.VISIBLE);
        showFullScreen(getWindow());
    }

    void scrollCommentListToEnd() {
        if (recyclerViewCommentState == RecyclerView.SCROLL_STATE_IDLE && commentsListView.getVisibility() == View.VISIBLE) {
            recyclerViewCommentScrollWaited = false;
            commentsListView.post(() -> commentLayoutManager.scrollToPositionWithOffset(mChatGroupAdapter.getItemCount() - 1, 0));
        } else {
            recyclerViewCommentScrollWaited = true;
        }
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


    private void showStreamEndDialog() {
        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title(getString(R.string.app_name))
                .message(getString(R.string.are_you_sure_you_want_to_end_this_stream))
                .confirmText(getString(R.string.end_stream))
                .singleAction(false)
                .onConfirmClicked(() -> {
                    stopStream("");
                    if (BuildConfig.DEBUG) {
                        showToast("host end stream manual", Toast.LENGTH_LONG);
                    }
                    showEndStreamLayout("");
                })
                .onCancelClicked(this::showUserActions)
                .build().show(this);
    }

    private void stopStream(String reason) {
        if (!mIsEndStream && !mVideoEncoderUnsupported) {
            mStreaming = false;
            mIsEndStream = true;
            isStreamReady = false;
//            mStreamer.stopCameraPreview();

            updateTimer();
            if (!mIsEndStreamByAdmin) {
                final long durationStream = mTimerView.getTimerStreaming();
                mStreamPresenter.endStream(durationStream, reason);
            }
            if (mStreamer != null) mStreamer.stopStream();
        }

    }


    private void handleHideShowView() {
        if (isStreamReady) {
            if (!isHideAllView) {
                if (GlobalSharedPreferences.isTutorialHostStreamShown(this)) {
                    hideAllView();
                }
                userActionsWholeContainer.setVisibility(View.VISIBLE);
                isHideAllView = true;

            } else {
                showAllView();
                isHideAllView = false;
            }
        }
    }

    private void hideAllView() {
        commentsListView.setVisibility(View.INVISIBLE);
        mStickyPadFrameLayout.setVisibility(View.INVISIBLE);
        mBtnLiveShop.setVisibility(View.INVISIBLE);
        loUserPoint.setVisibility(View.INVISIBLE);
        mBtnLiveTitle.setVisibility(View.INVISIBLE);
        llStreamingToppanel.setVisibility(View.INVISIBLE);
        if (mStreaming) {
            llBottomContainer.setVisibility(View.INVISIBLE);
            if (mIsTriviaShow) clTriviaExtraActionsContainer.setVisibility(View.GONE);
        } else {
            etComment.setVisibility(View.INVISIBLE);
        }
    }

    private void showAllView() {
        commentsListView.setVisibility(View.VISIBLE);
        scrollCommentListToEnd();
        llStreamingToppanel.setVisibility(View.VISIBLE);
        userActionsWholeContainer.setVisibility(View.VISIBLE);
        mStickyPadFrameLayout.setVisibility(View.VISIBLE);
        if (mIsSeller) mBtnLiveShop.setVisibility(View.VISIBLE);
        if (mIsStreamTitleAvailable) mBtnLiveTitle.setVisibility(View.VISIBLE);
        loUserPoint.setVisibility(View.VISIBLE);
        etComment.setVisibility(View.VISIBLE);
        llBottomContainer.setVisibility(View.VISIBLE);
        if (mIsTriviaShow) clTriviaExtraActionsContainer.setVisibility(View.VISIBLE);
//        mTimerView.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        checkNetworkConnection();

        if (!mCameraHintView.isZooming()) mCameraHintView.hideAll();

        if (mStreamer != null) {
            mStreamer.setDisplayPreview(mCameraPreviewView);
            mStreamer.onResume();
            mStreamer.setUseDummyAudioCapture(false);

        }
        if (!mIsEndStream && isStreamReady) mStreamPresenter.sendNotifyStreamResume();
        requestPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.e("onStart");
//        if (mEndStreamLayout == null) {


    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.e("onStop");

//        if (mEndStreamLayout == null) {

//        }
//        if (mSubPlayerLayout != null) mSubPlayerLayout.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.e("onPause");
//        mConnectionClassManager.remove(this);
        if (networkStateIntentReceiver != null) {
            unregisterReceiver(networkStateIntentReceiver);
            networkStateIntentReceiver = null;
        }
//        if (mCameraView != null) {
//            mCameraView.stopPreview();
//        }
//        EventBus.getDefault().unregister(this);
        // Send message that stream is pause
        if (!mIsEndStream && isStreamReady) {
            // don't pause with External Camera
            if (!mIsExternalCamera) {
                mStreamPresenter.sendNotifyStreamPause();
            }
        }
        if (mStreamer != null) {
            mStreamer.onPause();
            mStreamer.setUseDummyAudioCapture(true);
            mStreamer.stopCameraPreview();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mHandledPermissionCheck = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.e("onActivityResult");
        if (resultCode == RESULT_OK) {
            Uri imageCroppedURI;
            try {
                imageCroppedURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE_CROPPED);
            } catch (NullPointerException e) {
                Timber.d(e);
                return;
            }
            switch (requestCode) {
                case Constants.REQUEST_PIC_FROM_LIBRARY:
                    fileUri = data.getData();
                    if (fileUri == null) {
                        return;
                    }
                    performCrop(fileUri, imageCroppedURI);
                    break;

                case Constants.REQUEST_PIC_FROM_CROP:
                    final Uri resultUri = UCrop.getOutput(data);
                    Timber.e("resultUri=%s", resultUri);
                    if (resultUri != null) {
                        tvUploadCoverPhoto.setVisibility(View.GONE);
                        loadCoverImageWithUri(resultUri);
                    } else {
                        Toast.makeText(this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void loadCoverImageWithUri(Uri resultUri) {
        if (mStreamCover == null) return;
        mStreamCoverUri = resultUri;
        int error = R.drawable.user_image_default;
        GlideApp.with(getApplicationContext())
                .load(resultUri)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new RoundedCornersTransformation(Utils.dpToPx(5), 0))
                .placeholder(error)
                .error(error)
                .into(mStreamCover);
    }

    private void loadCoverImageWithLink(String imageUrl) {
        if (mStreamCover == null) return;
        ImageLoaderUtil.displayUserImage(getApplicationContext(), imageUrl, mStreamCover, new RoundedCornersTransformation(Utils.dpToPx(5), 0));
    }

    public void beautyMode() {
        if (mode == MODE_NONE) {
            mode = MODE_BEAUTY;
            turnOnBeautyFilter();
            showToast(getString(R.string.message_beauty_on), Toast.LENGTH_SHORT);
        } else {
            mode = MODE_NONE;
            turnOffBeautyFilter();
            showToast(getString(R.string.message_beauty_off), Toast.LENGTH_SHORT);
        }
        if (mMoreLiveOptionsPopUp != null) {
            mMoreLiveOptionsPopUp.setBeautyOpened(mode == MODE_BEAUTY);
        }

        if (onOffBeauty != null && onOffBeauty.getVisibility() == View.VISIBLE) {
            onOffBeauty.setImageResource(mode == MODE_BEAUTY ? R.drawable.icon_beauty_on : R.drawable.icon_beauty_off);
        }

        Timber.d(String.valueOf(mode));
    }

    private void turnOffBeautyFilter() {
        mStreamer.setEnableImgBufBeauty(false);
        mStreamer.getImgTexFilterMgt().setFilter((ImgFilterBase) null);
    }

    void showToast(String msg, int duration) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, msg, duration);
        mToast.show();

    }


    public void goLive() {
        if (!CheckNetwork.isNetworkAvailable(StreamingActivityGLPlus.this)) {
            hideProcessView();
            if (progress != null) progress.dismiss();

            utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection), StreamingActivityGLPlus.this);
            return;
        }

        startLiveView.setVisibility(View.GONE);
        showProcessView();
//                if(!isCanCreateStream){
//                    handleDialogCannotCreateStream();
//                    return;
//                }
        goingLiveStream();

//        etComment.setVisibility(View.VISIBLE);
//        userActionsWholeContainer.setWeightSum(3);
    }


    private LuckyWheelLayout openLuckyWheel(int levelId) {
        if (mLuckyWheelLayout == null) {
            View view = vsLuckywheel.inflate();
            mLuckyWheelLayout = new LuckyWheelLayout(view);
        }

        mStreamPresenter.getVoteAwards(levelId);
        mStreamPresenter.getLuckyWheelResult(levelId);
        luckywheelVisibilityListener.onNext(true);
        return mLuckyWheelLayout;
    }

    /**
     * Update the state of the UI controls
     */
    protected void updateTimer() {
        if (mStreaming) {
            mTimerView.startTimer();
        } else {
            mTimerView.stopTimer();
        }
    }

    //    /**
//     * Enable Android's sticky immersive full-screen mode
//     * See http://developer.android.com/training/system-ui/immersive.html#sticky
//     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Timber.e("onWindowFocusChanged ->" + hasFocus);
        if (hasFocus) showFullScreen(getWindow());
    }


    private int getFullScreenVisibilityFlags() {
        Timber.e("getFullScreenVisibilityFlags");
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                ;
    }

//    public int immersiveMode() {
//        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE;
//    }

    private void handleDialogCannotCreateStream() {
        DialogInfoUtility dialogInfoUtility = new DialogInfoUtility();
        dialogInfoUtility.showMessageWithClickButton(getString(R.string.app_name), getString(R.string.message_do_not_have_permission_create_stream),
                this, v -> finish());

    }


    private void handleError(String message) {
        if (isFinishing()) {
            return;
        }
        if (dialogInfoUtility == null) {
            dialogInfoUtility = new DialogInfoUtility();
        }

        if (topFanDialog != null) {
            topFanDialog.dismiss();
        }

        AlertDialogConnection fragment = (AlertDialogConnection) getSupportFragmentManager()
                .findFragmentByTag(ALERT_FRAGMENT_TAG);
        if (fragment == null)
            dialogInfoUtility.showMessage(getString(R.string.app_name), message, this);
        if (BuildConfig.DEBUG) {
            showToast("got error " + message, Toast.LENGTH_LONG);
        }
        stopStream(message);
    }

//region faceunity


    private BeLiveImgFaceunityFilter mImgFaceunityFilter;

    /**
     * Turn on/off faceunity function
     *
     * @param isConnect - true (on) false (off)
     */
    void connectWithFaceUnity(boolean isConnect) {
        Timber.e("connectWithFaceUnity %s", isConnect);
        if (isConnect) {
            initFaceunity();
            if (mStreamer != null && !mGestureEnabled)
                mStreamer.getCameraCapture().mImgBufSrcPin.connect(mImgFaceunityFilter.getBufSinkPin());
        } else {
//            if (mStreamer != null)
//                mStreamer.getCameraCapture().mImgBufSrcPin.disconnect(mImgFaceunityFilter.getBufSinkPin(), false);
            if (mImgFaceunityFilter != null) mImgFaceunityFilter.setFilePath(null);
        }
    }

    private void setupViewActionsForLive() {
        mBtnMoreOptions.setVisibility(View.VISIBLE);
//        mBtnStickerOption.setVisibility(View.VISIBLE);
        onOffBeauty.setVisibility(View.GONE);
        onOffFlash.setVisibility(View.GONE);
    }

    void initFaceunity() {
        if (mImgFaceunityFilter == null) {
            //add faceunity filter
            mImgFaceunityFilter = new BeLiveImgFaceunityFilter(this, mStreamer.getGLRender());
            mStreamer.getImgTexFilterMgt().setExtraFilter(mImgFaceunityFilter);
        }
        updateFaceunitParams();
    }

    private void updateFaceunitParams() {
        if (mImgFaceunityFilter != null) {
            mImgFaceunityFilter.setTargetSize(mStreamer.getTargetWidth(),
                    mStreamer.getTargetHeight());

            if (mStreamer.isFrontCamera()) {
                mImgFaceunityFilter.setMirror(true);
            } else {
                mImgFaceunityFilter.setMirror(false);
            }
        }
    }

    //endregion

    //region stream view


    @Override
    public int getRankByUserName(String userName) {
        return mapWithCurrentTopFan(userName);
    }

    @Override
    public void onGetDailyTopFansListSuccessfully(List<DailyTopFanModel> dailyTopFansList) {
        mGiftRankingGroupView.updateRankingList(mIsTriviaShow ? Collections.EMPTY_LIST : dailyTopFansList);
    }

    @Override
    public void notifyTopFanJoined(ChatItemModelClass joinMessage) {
        runOnUiThread(() -> {
            if (joinMessage.rank != -1)
                llGiftGroup.addTopFanJoined(joinMessage);
        });

    }

    @Override
    public void onOldChatMessages(List<RecordedMessagesModel> messages) {
        this.mRecordedMessagesModels = new ArrayList<>(messages);
        showOldMessages(mRecordedMessagesModels);
    }

    @Override
    public void onStreamStickerReceived(StreamTitleSticker sticker) {
        if (mStickyPadInitCompleted && sticker != null) {
            mStickyPadFrameLayout.onLiveTitleReceived(sticker);
        }
    }

    private void showOldMessages(ArrayList<RecordedMessagesModel> recordedMessagesModels) {
        if (!recordedMessagesModels.isEmpty()) {
            mCompositeSubscription.add(Observable.from(recordedMessagesModels)
                    .subscribeOn(Schedulers.computation())
                    .filter(recordedMessagesModel -> recordedMessagesModel.getActionType() != RecordedMessagesModel.TYPE_STREAM_TITLE_STICKER)
                    .map(this::createChatItemWithType)
                    .toList()
                    .filter(chatItemModelClasses -> !chatItemModelClasses.isEmpty())
                    .doOnNext(chatItemModelClasses -> Timber.e("old messages prepare completed"))
                    .filter(chatItemModelClasses -> !chatItemModelClasses.isEmpty())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(chatItemModelClasses -> {
                        if (mResumePrevisousStream) {
                            updateBufferedMessageInList(chatItemModelClasses);
                        } else {
                            mOldCommentList = new ArrayList<>(chatItemModelClasses);
                        }
                    }, Timber::e));

        }
    }

    private ChatItemModelClass createChatItemWithType(RecordedMessagesModel model) {
        ChatItemModelClass chatItem = new ChatItemModelClass();
        chatItem.setChatDisplayName(model.getDisplayName());
        chatItem.setLiked(model.getActionType() == RecordedMessagesModel.TYPE_LIKE);
        chatItem.setMsg(model.getMessage());
        chatItem.setUserName(model.getUserName());
        chatItem.setProfilePic(model.getProfilePic());
        chatItem.rank = model.rank;
        switch (model.getActionType()) {
            case RecordedMessagesModel.TYPE_GIFT:
                chatItem.setMessageType(ChatItemModelClass.CHAT_TYPE_GIFT);
                chatItem.setGiftImage(model.getGiftImage());
                chatItem.setGiftCombo(model.getGiftComboQuantity());
                break;
            case RecordedMessagesModel.TYPE_JOIN:
                chatItem.setMessageType(ChatItemModelClass.CHAT_TYPE_USER_JOIN_LIST);
                break;
            case RecordedMessagesModel.TYPE_LIKE:
                break;
            case RecordedMessagesModel.TYPE_MESSAGE:
                chatItem.setMessageType(ChatItemModelClass.CHAT_TYPE_MESSAGE);
                break;
            case RecordedMessagesModel.TYPE_FOLLOW:
                chatItem.setMessageType(ChatItemModelClass.CHAT_TYPE_FOLLOW);
                break;
            case RecordedMessagesModel.TYPE_SHARE:
                chatItem.setMessageType(ChatItemModelClass.CHAT_TYPE_SHARE_STREAM);
                break;
            case RecordedMessagesModel.TYPE_LIVE_COMMERCE_ANNOUNCEMENT:
                chatItem.setMessageType(ChatItemModelClass.CHAT_TYPE_LIVE_COMMERCE_ANNOUNCEMENT);
                break;
        }

        if (!StringUtil.isNullOrEmptyString(model.getProfileColor())) {
            chatItem.setProfileColor(model.getProfileColor());
        }
        return chatItem;
    }


    @Override
    public ArrayList<RecordedMessagesModel> getRecordedMessages() {
        return new ArrayList<>(mRecordedMessagesModels);
    }

    @Override
    public void onPreviousStreamRemain() {
        if (mIsCameraInitDone.get()) {
            showResumeConfirmDialog();
        } else {
            mIsResumableStream.set(true);
        }
    }

    private void showResumeConfirmDialog() {
        mIsResumableStream.set(false);
        new DialogbeLiveConfirmation.Builder()
                .onConfirmClicked(() -> {
                    if (mTimerView != null) mTimerView.plus(mLastStreamDuration);
                    mStreamPresenter.resumePreviousStream();
                    //show old messages
                    mResumePrevisousStream = true;
                    if (!mOldCommentList.isEmpty()) updateBufferedMessageInList(mOldCommentList);
                })
                .onCancelClicked(() -> {
                    mStreamPresenter.cancelPreviousStream();
                    if (mRecordedMessagesModels != null) mRecordedMessagesModels.clear();
                    if (mOldCommentList != null) mOldCommentList.clear();
                })
                .title(getString(R.string.app_name))
                .message(getString(R.string.do_you_want_resume_previous_stream))
                .confirmText(getString(R.string.resume))
                .singleAction(false)
                .build()
                .show(this);
    }

    @Override
    public void storeBotJoinMessage(ChatItemModelClass messageItem) {
        storeChatMessages(messageItem, RecordedMessagesModel.TYPE_JOIN);
    }

    @Override
    public void endStreamShareUrl(String StreamResultUrl) {
        if (mEndStreamLayout != null) {
            mEndStreamLayout.setShareUrl(StreamResultUrl);
        }
        mStreamUrlShare = StreamResultUrl;
        Timber.e("endStreamShareUrl=" + StreamResultUrl);
    }

    @Override
    public int getShareOption() {
        return mShareOption;
    }

    @Override
    public long getStreamDuration() {
        return mTimerView != null ?
                mLastStreamDuration != 0 ? mTimerView.getTimerStreaming() + mLastStreamDuration : mTimerView.getTimerStreaming() :
                0;
    }

    @Override
    public void onLastStreamDuration(long duration) {
        mLastStreamDuration = duration;
    }

    @Override
    public boolean isShareLocation() {
        return txtLocation.isSelected();
    }

    @Override
    public void onLocationDetected() {
        Timber.e("enable location");
        txtLocation.setSelected(true);
        txtLocation.setAlpha(1f);
    }

    @Override
    public void onBlockSuccess(String blockedName, String userId, String slug, int streamId) {
        showToast(String.format("%s has been kicked out of stream", blockedName), Toast.LENGTH_SHORT);
        AppsterUtility.addPrefListItem(this, Constants.STREAM_BLOCKED_LIST, userId, slug);
        dismissProfileDialog();
    }

    @Override
    public void onUnblockSuccess(String blockedName, String userId, String slug, int streamId) {
        showToast(String.format("Unblocked %s", blockedName), Toast.LENGTH_SHORT);
        AppsterUtility.removePrefListItem(this, Constants.STREAM_BLOCKED_LIST, userId, slug);
        dismissProfileDialog();
    }

    @Override
    public void onMuteSuccess(String mutedName, String userId, String slug, int streamId) {
        AppsterUtility.addPrefListItem(this, Constants.STREAM_MUTE_LIST, userId, slug);
        showToast(String.format("%s has been muted ", mutedName), Toast.LENGTH_SHORT);
        setupUnMuteTime(slug, streamId, userId);
        dismissProfileDialog();
    }

    @Override
    public void onUnMuteSuccess(String unMutedName, String userId, String slug, int streamId) {
        showToast(String.format("%s has been unmuted ", unMutedName), Toast.LENGTH_SHORT);
        AppsterUtility.removePrefListItem(this, Constants.STREAM_MUTE_LIST, userId, slug);
        dismissProfileDialog();
    }

    @Override
    public void onSaveStreamSuccess() {
        Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();
        // Send message update in wall feed and profile
        EventBus.getDefault().post(new EventBusRefreshFragment());
        mEndStreamLayout.goToProfileScreen();
    }

    @Override
    public void onSaveStreamFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //region luckywheel actions
    AtomicReference<Integer> mStarsReceivedDuringAnimation;
    int mCurrentVotingBarLevel = -1;
    private SparseArray<VotingLevels> mVotingBarLevel;

    @Override
    public void onVoteLevelsReceived(ArrayList<VotingLevels> votingLevels, int levelUnlockedIndex, int currentScore) {
        if (mVotingbarProgressObservable == null) {
            mVotingBarLevel = new SparseArray<>();
            mStarsReceivedDuringAnimation = new AtomicReference<>();
            mVotingbarProgressObservable = PublishSubject.create();
            mCompositeSubscription.add(mVotingbarProgressObservable
                    .subscribe(stars -> {
                        if (stateProgressbar.isAnimating()) {
                            mStarsReceivedDuringAnimation.set(stars);
                        } else {
                            try {
                                stateProgressbar.setProgress(stars);
                                mStarsReceivedDuringAnimation.set(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, this::handleRxError));
            adjustStateProgressbarPosition();
        }
        Observable<VotingLevels> levelsObservable = Observable.from(votingLevels);
        mCompositeSubscription.add(levelsObservable.last().subscribe(lastLevel -> stateProgressbar.setMaxProgress(lastLevel.toCredit), this::handleRxError));
        mCompositeSubscription.add(levelsObservable.subscribe(level -> {
            mVotingBarLevel.put(level.orderIndex, level);
            stateProgressbar.addLevel(level.toCredit);
        }, this::handleRxError, () -> {
            try {
                if (levelUnlockedIndex > 0) stateProgressbar.setLevelUnlocked(levelUnlockedIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));


        stateProgressbar.setReachLevelCallback(this);
        stateProgressbar.setVisibility(View.VISIBLE);
        mVotingbarProgressObservable.onNext(currentScore);

    }

    private void adjustStateProgressbarPosition() {
        FrameLayout.LayoutParams frameParrent = (FrameLayout.LayoutParams) stateProgressbar.getLayoutParams();
        frameParrent.topMargin = llStreamingToppanel.getBottom() - Utils.dpToPx(10);
        stateProgressbar.setLayoutParams(frameParrent);
    }

    @Override
    public void onVoteAwardReceived(ArrayList<LuckyWheelAwards> luckyWheelAwards) {

//        if (mLuckyWheelLayout == null) {
//            openLuckyWheel();
//        }

        mLuckyWheelLayout.setAwardData(luckyWheelAwards);
    }

    @Override
    public void onLuckyWheelResultReceived(int awardId) {
        if (mLuckyWheelLayout != null) mLuckyWheelLayout.setLuckyWheelResultIndex(awardId);
    }

    @Override
    public void onVotingScoresReceived(int votingScores) {
        if (mVotingbarProgressObservable != null) mVotingbarProgressObservable.onNext(votingScores);
    }

    @Override
    public void onShowNotificationNetworkLow() {
        Timber.e("--------------------onShowNotificationNetworkLow");
        if (tvNetworkSlow.getVisibility() == View.GONE) {
            tvNetworkSlow.setVisibility(View.VISIBLE);
            Animation slideLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.noti_network_slide_in_left);
            tvNetworkSlow.startAnimation(slideLeft);
        }
    }

    @Override
    public void onHideNotificationNetworkLow() {
        if (tvNetworkSlow.getVisibility() == View.VISIBLE) {
            tvNetworkSlow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStreamRemoved(String message, EndStreamDataModel data) {
        if (mStreamer != null) {
            mStreamer.onPause();
        }
        if (mTimerView != null) {
            mTimerView.stopTimer();
            mTimerView.setText(AppsterUtility.parseStreamingTimeToHHMM(data.getDuration()));
            mTimerView.setVisibility(View.VISIBLE);
        }
        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title(getString(R.string.app_name))
                .message(message)
                .singleAction(true)
                .onConfirmClicked(() -> {
                    showEndStreamLayout(TRIGGER_END_BY_ADMIN_BANNED_XMPP);
                    onEndStreamDataReceived(data);
                })
                .build().show(this);
    }

    @Override
    public void onStreamTitleEnabled() {
        setupStickyPadLayout();
    }

    @Override
    public void reachLevel(int levelIndex, int remain) {
//        mStarsReceivedDuringAnimation.set(mStarsReceivedDuringAnimation.get() + remain);
        mCurrentVotingBarLevel = levelIndex;
//        openLuckyWheel(mVotingBarLevel.get(levelIndex).id);
    }

    //endregion luckywheel actions

    @Override
    public void onStreamSecondCreate() {
        if (progress != null) progress.dismiss();
        llBottomContainer.setVisibility(View.GONE);
        if (mEndStreamLayout == null) {
            View view = vsEndStream.inflate();
            mEndStreamLayout = new EndStreamLayout(view, this, TRIGGER_END_BY_NOT_ALLOW_CREATE_SECOND_STREAM);
            mEndStreamLayout.setTitle(getString(R.string.error_start_stream));
        }
    }

    String getTrackingLiveReason(String trigger) {
        return String.format(TRACKING_STREAM_FORMAT, LIVE_STREAM, TARGET_HOST, mSlug, mCurrentUser != null ? mCurrentUser.getUserName() : "null", trigger);
    }

    private void setupUnMuteTime(String streamSlug, int streamId, String userId) {
        //set up time reset
        Intent intentAlarm = new Intent(this, InternalMessageReceiver.class);
        intentAlarm.putExtra(InternalMessageReceiver.STREAM_ID_KEY, streamSlug);
        intentAlarm.putExtra(InternalMessageReceiver.STREAM_USER_ID_KEY, userId);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        //1hour
        Long time = new GregorianCalendar().getTimeInMillis() + 60 * 60 * 1000;


//        Long time = new GregorianCalendar().getTimeInMillis() + 30 * 1000;
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, streamId, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private void setupLiveCommerce() {
        mIsSeller = mCurrentUser.isSeller();
        String sellerId = mCurrentUser.getUserId();
        String sellerName = mCurrentUser.getDisplayName();
        if (mIsSeller) {
            mBtnLiveShop.setVisibility(View.VISIBLE);
            mBtnLiveShop.setOnClickListener(v -> {
                AppsterUtility.temporaryLockView(v);
                if (mLiveShopDialog == null) {
                    String auth = "Bearer " + AppsterApplication.mAppPreferences.getUserToken();
                    mLiveShopDialog = LiveShopDialog.newInstance(auth, sellerId, sellerName, true, mStreamDetail.liveShopOrderButtonNowLabel);
                    mLiveShopDialog.setShouldShowStatusBar(true);
                }
                mLiveShopDialog.show(getSupportFragmentManager(), LiveShopDialog.class.getName());
            });
        }
    }

    private void setupStickyPadLayout() {
        Timber.d("setupStickyLayout");
        mBtnLiveTitle.setVisibility(View.VISIBLE);
        mStickyPadFrameLayout.setVisibility(View.VISIBLE);
        mStickyPadFrameLayout.setFragmentManager(getSupportFragmentManager());
        mStickyPadFrameLayout.setOnStickyPositionChangedListener((text, percentX, percentY, color) -> {
            mBtnLiveTitle.setVisibility(View.VISIBLE);
            String hex = String.format("#%06X", (0xFFFFFF & color));
            mStreamPresenter.storeStreamTitlePosition(text, percentX, percentY, hex);
            mStreamPresenter.sendStreamTitlePositionXmpp(text, percentX, percentY, hex);
            storeStreamTitle(text, percentX, percentY, hex);
            Timber.d("percent x %f", percentX);
            Timber.d("percent Y %f", percentY);
        });
        mBtnLiveTitle.setOnClickListener(v -> mStickyPadFrameLayout.showLiveTitleEditorDialog());
        mStickyPadInitCompleted = true;
        mIsStreamTitleAvailable = true;
    }

    @Override
    public void onMessageSuccess(ChatItemModelClass messageItem) {
        storeChatMessages(messageItem, RecordedMessagesModel.TYPE_MESSAGE);
        mChatGroupAdapter.newChatItem(messageItem);
        LinearLayoutManager commentsListViewLayoutManager = (LinearLayoutManager) commentsListView.getLayoutManager();
        commentsListView.post(() -> commentsListViewLayoutManager.scrollToPositionWithOffset(mChatGroupAdapter.getItemCount() - 1, 0));

    }

    @Override
    public void onNaughtyWordsReceived(ArrayList<String> naughtyWords) {
        mNaughtyWords = naughtyWords;
    }

    @Override
    public void dismissErrorDialog() {
        if (dialogInfoUtility != null) {
            dialogInfoUtility.dismissDialog();
        }
    }

    @Override
    public void showEndStreamLayout(String reason) {
        Timber.e("EndStreamLayout!!!");
        llBottomContainer.setVisibility(View.GONE);
        stateProgressbar.setVisibility(View.GONE);
        removeTutorialIfNeed();
//        luckywheelVisibilityListener.onNext(false);
        if (mEndStreamLayout == null) {
            if (vsEndStream.getParent() != null) {
                vsEndStream.setOnInflateListener((stub, inflated) -> {
                    Timber.e("EndStreamLayout inflated!!!");
                    mEndStreamLayout = new EndStreamLayout(inflated, this, reason);
                    mEndStreamLayout.setShareUrl(mStreamUrlShare);
                });
                vsEndStream.inflate();
            }
        }
        if (mExpensiveGiftDialog != null && mExpensiveGiftDialog.isAdded()) {
            mGiftQueue.clear();
            mExpensiveGiftDialog.dismiss();
        }

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
            showToast("Stream prepare timeout!", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onEndStreamDataReceived(EndStreamDataModel streamDataModel) {

        if (mEndStreamLayout != null) {
            mEndStreamLayout.updateEndStreamData(streamDataModel);
        }
        stopStream("");

    }

    @Override
    public void updateStars(long totalGold) {
        txtStars.setText(String.valueOf(totalGold));
        AppsterApplication.mAppPreferences.getUserModel().setTotalGoldFans(totalGold);
    }

    @Override
    public void updatePoints(int totalPoint) {
        tvUserPoint.setText(Utils.formatThousand(totalPoint));
        AppsterApplication.mAppPreferences.getUserModel().setPoints(totalPoint);
    }

    @Override
    public void onStreamCreateSuccess(StreamModel streamModel, boolean isRecorded) {
        startLiveView.setVisibility(View.GONE);
        this.mStreamDetail = streamModel;
        this.mSlug = streamModel.getSlug();
        mCurrentUser.setSeller(streamModel.getPublisher().isSeller());
        mTopFanList.addAll(streamModel.rankingList);
        mIsTriviaShow = streamModel.isTrivia;
        mGiftRankingGroupView.updateRankingList(mIsTriviaShow ? Collections.EMPTY_LIST : streamModel.dailyTopFansList);
        mGiftRankingGroupView.setListener(this::onShowProfileClicked);
        initListUserWatcher();
        setupViewActionsForLive();
        setupLiveCommerce();

        //check config from server to show stream title
        mStreamPresenter.getAppConfig();

        // check external camera option with Admin
        UserModel loginUser = AppsterApplication.mAppPreferences.getUserModel();
        if (loginUser.isDevUser()) {
            if (mCheckBoxExternalCam.isChecked()) {
                mIsExternalCamera = true;
            }
        }

        if (mIsExternalCamera) {
            DialogInfoUtility dialogInfo = new DialogInfoUtility();
            dialogInfo.showMessageWithSelectable(getString(R.string.stream_id), mSlug, this, onClick -> {
                startStream(mSlug);
            });
        } else {
            startStream(mSlug);
        }

//        mStreamPresenter.getTriviaInfo(10);

    }

    private void startTrivia(int triviaId) {
        mTriviaShowListener.onNext(true);
        mStreamPresenter.getTriviaInfo(triviaId);
        mTriviaInAnim = AnimationUtils.loadAnimation(this, R.anim.trivia_zoom_in);
        mTriviaOutAnim = AnimationUtils.loadAnimation(this, R.anim.trivia_zoom_out);
        mTriviaResultOutAnim = AnimationUtils.loadAnimation(this, R.anim.trivia_zoom_out);
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

    @Override
    public void onTagCategoriesReceived(List<TagListLiveStreamModel> tagCategories) {
        tagModelList = tagCategories;
        TagId = tagModelList.get(tagModelList.size() - 1).getTagId();
    }

    @Override
    public void onStreamViewCountChanged(int currentViewer, long viewedUserCount, int likeCount) {
        Timber.e("** have been viewed %s", String.valueOf(viewedUserCount));
        Timber.e("** viewing %s", String.valueOf(currentViewer));
        tvCurrentView.setText(Utils.formatThousand(viewedUserCount));
        tvTotalLiked.setText(Utils.formatThousand(likeCount));
    }

    @Override
    public void showForceStopStream(String message) {
        handleError(message);
    }

    @Override
    public void onStreamCreateError() {
        if (progress != null) progress.dismiss();
        handleDialogCannotCreateStream();
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void loadError(String errorMessage, int code) {
        if (progress != null) progress.dismiss();
        handleError(errorMessage, code);
    }

    @Override
    public void showProgress() {
        DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg), true);
    }

    @Override
    public void hideProgress() {
        if (DialogManager.isShowing()) {
            DialogManager.getInstance().dismisDialog();
        }
    }

    //endregion

    //region agora rtc
    private void onRTCCallHandle(String channel) {
        if (mIsCaling) {
            stopRTC();
        } else {
            if (!channel.isEmpty()) {
                mStreamer.initAgoraRTC();
                startRTC(channel);
            }
        }
    }

    private void startRTC(String channel) {
        if (mIsCaling) return;
        if (mStreamer != null) {
            mStreamer.setRTCSubScreenRect(HOST_SCREEN_GUEST_CAM_LEFT_BEGIN, HOST_SCREEN_GUEST_CAM_TOP_BEGIN, HOST_SCREEN_GUEST_CAM_WIDTH, HOST_SCREEN_GUEST_CAM_HEIGHT, SCALING_MODE_CENTER_CROP);
//        mStreamer.getMediaManager().audioOnly(true);
            mStreamer.setRTCMainScreen(RTC_MAIN_SCREEN_CAMERA);
            mStreamer.startRTC(channel);
            mIsCaling = true;
            Timber.e("startRTC");
        }
    }

    private void stopRTC() {
        if (mStreamer != null) mStreamer.stopRTC();
        mIsCaling = false;
        Timber.e("stopRTC");
    }

    private void updateSubPlayerLayoutPosition() {
        if (mSubPlayerLayout == null) return;
        int screenWidth = Utils.getScreenWidth();
        int screenHeight = Utils.getScreenHeight();
//        if (hasNavBar(getResources())) {

        screenHeight += getNavigationBarSize(this).y;
//        }

        int left = (int) (HOST_SCREEN_GUEST_CAM_LEFT_BEGIN * screenWidth);
        int top = (int) (HOST_SCREEN_GUEST_CAM_TOP_BEGIN * screenHeight);
        View view = mSubPlayerLayout.getView();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = (int) (HOST_SCREEN_GUEST_CAM_WIDTH * screenWidth);
        layoutParams.height = (int) (HOST_SCREEN_GUEST_CAM_HEIGHT * screenHeight);
        layoutParams.setMargins(left, top, 0, 0);
        view.requestLayout();
    }

    //endregion
    //region network check
    void startCheckNetworkUploadSpeed() {
        isCheckingNetworkBandwidth = true;
        mDeviceBandwidthSampler.startSampling();
        mCompositeSubscription.add(Observable.fromCallable(this::getUploadSpeed)
                .timeout(30, TimeUnit.SECONDS, Observable.create(Observer::onCompleted))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(aDouble -> !isFinishing() && !isDestroyed())
                .doOnNext(connectionQuality -> mDeviceBandwidthSampler.stopSampling())
                .subscribe(connectionQuality -> {
                            mConnectionClass = connectionQuality;
                            Timber.e("on next check network %s", mConnectionClass.toString());
                        },

                        e -> {
                            Timber.e("ERRRR------------- %s", e.getMessage());
                            if (progress != null) progress.dismiss();
                            showLowNetWorkPopup();
                            mConnectionClassManager.reset();
                            isCheckingNetworkBandwidth = false;
                        }, () -> {
                            Timber.e("onCompleted check network");
                            if (mConnectionClass == ConnectionQuality.POOR || mConnectionClass == UNKNOWN)
                                mConnectionClass = stopCheckAndGetNetworkQuality();
                            if (isClickedGoLive) {
                                if (mConnectionClass == ConnectionQuality.POOR || mConnectionClass == UNKNOWN) {
                                    showLowNetWorkPopup();
                                    mConnectionClassManager.reset();
                                } else {
                                    Timber.e("ConnectionQuality %s", mConnectionClass.toString());
                                    goLive();
                                }
                            }
                        })
        );
    }


    ConnectionQuality stopCheckAndGetNetworkQuality() {
        mDeviceBandwidthSampler.stopSampling();
        isCheckingNetworkBandwidth = false;
        return mConnectionClassManager.getCurrentBandwidthQuality();
    }

    void showLowNetWorkPopup() {
        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title(getString(R.string.app_name))
                .message(getString(R.string.low_network_message))
                .confirmText(getString(R.string.btn_text_ok))
                .onConfirmClicked(() -> {
                    if (progress != null) progress.dismiss();
                })
                .singleAction(true)
                .build().show(this);
    }

    /**
     * Upload a random string and measure transfer speed
     */
    public ConnectionQuality getUploadSpeed()
            throws IOException {
        int MB = 1;

        /** generate random string */
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < MB * 1024 * 1024; i++) {
            sb.append(
                    chars[random.nextInt(chars.length)]);
        }

        long totalTxBytes = TrafficStats.getTotalTxBytes();
        double startTime = System.currentTimeMillis();

        String checkNetworkUrl = "http://" + BuildConfig.WOWZA_WEB_HOST_IP + "/check";
        String UL_Packet = sb.toString();
        RequestBody formBody = new FormBody.Builder()
                .add("content", UL_Packet)
                .add("guestbookName", "default")
                .build();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(checkNetworkUrl)
                .addHeader("Connection", "close")
                .post(formBody)
                .build();
        final Response resp = client.newCall(request).execute();

        double endTime = System.currentTimeMillis();
        double tranferTime = (endTime - startTime) / 1000;
        long byteDiff = TrafficStats.getTotalTxBytes() - totalTxBytes;
        double bandwidth = (((byteDiff) * 1.0) / (endTime - startTime)) * 8;
        /** Log */
        Timber.e("Upload Start: %f", startTime);
        Timber.e("Upload End:  %f ", endTime);
        Timber.e("Tranfer Time: %f secs", tranferTime);
        Timber.e("Data size:    %d MB", MB);
        Timber.e("Upload speed: %f MB/s", bandwidth);
        ResponseBody body = resp.body();
        if (resp.isSuccessful()) {
            body.string(); // Closes automatically.
        } else {
            body.close();
        }
        if (bandwidth < 0) {
            return ConnectionQuality.UNKNOWN;
        }
        if (bandwidth < 400) {
            return ConnectionQuality.POOR;
        }
        if (bandwidth < 1200) {
            return ConnectionQuality.MODERATE;
        }
        if (bandwidth < 2000) {
            return ConnectionQuality.GOOD;
        }

        return ConnectionQuality.EXCELLENT;
    }
    //endregion


    private void startStream(String slug) {
        if (mStreamer == null) return;

        String wowzaAppName = WowzaConstant.APPLICATION_NAME_RECORDING;

        if (mIsExternalCamera) {
            wowzaAppName = WowzaConstant.APPLICATION_NAME_EXTERNAL;
            showToast(getString(R.string.start_external_cam_live), Toast.LENGTH_SHORT);
        }

        try {
            String url = "rtmp://" +
                    BuildConfig.WOWZA_HOST_IP +
                    ":1935/" +
                    wowzaAppName +
                    "/" +
                    slug;
            mStreamer.setUrl(url);        //rtsp://stgwowza.view.belive.sg:1935/Appsters_recording/7065ca0ff7e444db8ac8859e4335a66f
            boolean streamIsPublish = mStreamer.startStream();
            mStreaming = true;
            Timber.e("streamIsPublish %s", streamIsPublish);
            displayStreamUploadInfo();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Download faceunity file if needed or open local file
     *
     * @param id  - stickerId
     * @param url - url to download
     */
    private void openFaceUnityWithLink(int id, @NonNull String url) {
        if (url.isEmpty()) return;
        FileDownloader.getInstance().isFileAlreadyDownloaded(url, (isNeedToDownload, localVideoPath) -> {
            if (isNeedToDownload) {
                FileDownloader.getInstance().downloadFile(url, new DownloadVideos.IDownloadListener() {
                    @Override
                    public void successful(String filePath) {
                        setFaceUnityPath(filePath);
                        notifyStickerDownloaded(id);
                    }

                    @Override
                    public void fail() {
                        //do nothing
                    }
                });
            } else {
                //read from local file
                setFaceUnityPath(localVideoPath);
            }
        });
    }

    void notifyStickerDownloaded(int stickerId) {
        if (!isFinishing() && !isDestroyed()) {
            if (mFaceUnityDialogPicker != null && (mFaceUnityDialogPicker.isAdded() && mFaceUnityDialogPicker.isVisible())) {
                mFaceUnityDialogPicker.notifyDataChanged(stickerId);
            }
        }
    }

    /**
     * Apply faceunity sticker with file name
     *
     * @param localPath - actually is filename include "/xxx.mp3'
     */
    void setFaceUnityPath(@NonNull String localPath) {
        if (mStickerModel != null && !mStickerModel.getFile().isEmpty() && !localPath.isEmpty() && getFileName(mStickerModel.getFile()).equalsIgnoreCase(getFileName(localPath))) {
            initFaceunity();
            mImgFaceunityFilter.setFilePath(new File(Constants.FILE_CACHE_FOLDER, getFileName(localPath)).getAbsolutePath());
        }
    }

    private String getFileName(String file) {
        return file.substring(file.lastIndexOf('/'));
    }

    private Timer mTimer;

    private void displayStreamUploadInfo() {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (mTimer == null) {
            mTimer = new Timer();
        }
        txtStreamUploadSpeed.setVisibility(View.VISIBLE);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> txtStreamUploadSpeed.setText(getUploadStreamInfo()));
            }
        }, 100, 2000);
    }

    //update debug info
    private String getUploadStreamInfo() {
        if (mStreamer == null) {
            return "";
        }

//        String.format(Locale.getDefault(),
//                "RtmpHostIP()=%s DroppedFrameCount()=%d \n " +
//                        "ConnectTime()=%d DnsParseTime()=%d \n " +
//                        "UploadedKB()=%d EncodedFrames()=%d \n" +
//                        "CurrentKBitrate=%d Version()=%s",
//                mStreamer.getRtmpHostIP(), mStreamer.getDroppedFrameCount(),
//                mStreamer.getConnectTime(), mStreamer.getDnsParseTime(),
//                mStreamer.getUploadedKBytes(), mStreamer.getEncodedFrames(),
//                mStreamer.getCurrentUploadKBitrate(), KSYStreamer.getVersion());

        return String.format(Locale.getDefault(),
                "Current bitrate=%dKb\n" +
                        "Uploaded=%dKb\n" +
                        "Encoded Frames=%d\n" +
                        "Dropped Frames=%d",
                mStreamer.getCurrentUploadKBitrate(),
                mStreamer.getUploadedKBytes(),
                mStreamer.getEncodedFrames(),
                mStreamer.getDroppedFrameCount()
        );

//        return String.format(Locale.getDefault(), "CurrentKBitrate=%dkb", mStreamer.getCurrentUploadKBitrate());
    }


    @Override
    public void onDisplayNameClicked(ChatItemModelClass item) {
        showUserProfile(item.getUserName(), item.getProfilePic(), mSlug);
    }

    @Override
    public void onMessageClicked(ChatItemModelClass item) {
//        onScreenClicked();
    }

    @Override
    public void onFollowHostSuggestionItemClicked(ChatItemModelClass item) {

    }

    @Override
    public void onLiveCommerceSuggestionItemClicked() {

    }

    //region user dialog actions
    @Override
    public void onReportUserClick(String userId) {

        DialogReport dialogReport = DialogReport.newInstance();
        dialogReport.setChooseReportListenner(reason -> onReportUser(reason, userId));
        dialogReport.show(getSupportFragmentManager(), "Report");

    }


    @Override
    public void onBlockUserClick(String userId, String displayName) {
        if (AppsterUtility.loadPrefList(this, Constants.STREAM_BLOCKED_LIST, userId).contains(mSlug)) {
//            mStreamPresenter.unBlockUser(userId, displayName);
//            dismissProfileDialog();
            DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
            builder.title(getString(R.string.unblock_this_user))
                    .message(getString(R.string.unblock_confirmation_content))
                    .confirmText(getString(R.string.string_unblock))
                    .onConfirmClicked(() -> {
                        mStreamPresenter.unBlockUser(userId, displayName);
                        dismissProfileDialog();
                    })
                    .build().show(this);
        } else {
            DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
            builder.title(getString(R.string.block_this_user))
                    .message(getString(R.string.block_confirmation_content))
                    .confirmText(getString(R.string.string_block))
                    .onConfirmClicked(() -> {
                        mStreamPresenter.blockUser(userId, displayName);
                        dismissProfileDialog();
                    })
                    .build().show(this);
        }

    }

    private void dismissProfileDialog() {
        getSupportFragmentManager().findFragmentByTag(USER_DIALOG_TAG).onDestroyView();
    }


    @Override
    public void onMuteUserClick(String userId, String displayName) {
        mStreamPresenter.muteUser(userId, displayName);
    }

    @Override
    public void onUnMuteUserClick(String userId, String displayName) {
        if (mStreamPresenter != null) mStreamPresenter.unMuteUser(userId, displayName);

    }

    @Override
    public void onSubStreamCreated(SubStreamData subStreamData) {
        if (vsSubPlayer.getParent() != null) {
            View view = vsSubPlayer.inflate();
            mSubPlayerLayout = new SubPlayerLayout(view);
            updateSubPlayerLayoutPosition();
            mSubPlayerLayout.setRole(Role.HOST);
            mSubPlayerLayout.setClickListener(this);
        }
        startTimeoutWaitingTimer();
        mSubPlayerLayout.setGuestAvatar(subStreamData.userImage);
        mSubPlayerLayout.setGuestDisplayName(subStreamData.displayName);
        mSubPlayerLayout.updateState(State.CONNECTING);
        vsSubPlayer.setVisibility(View.VISIBLE);
        hideUserProfileDialog();
        if (mStreamPresenter != null) mStreamPresenter.startRTC();
    }

    private void startTimeoutWaitingTimer() {
        if (mIsCallWaitingTimeoutEnable.get()) return;
        mIsCallWaitingTimeoutEnable.set(true);
        mTimeoutSubscription = Observable.just(true)
                .delay(30, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(s -> (!isFinishing() && !isDestroyed()) && mIsCallWaitingTimeoutEnable.get())
                .doOnNext(b -> mIsCallWaitingTimeoutEnable.set(false))
                .subscribe(aBoolean -> {
                    if (BuildConfig.DEBUG) {
                        showToast("call request time out 30s", Toast.LENGTH_LONG);
                    }
                    if (mSubPlayerLayout != null) mSubPlayerLayout.updateState(State.NO_ANSWER);
                    if (mStreamPresenter != null) mStreamPresenter.notifySubStreamNoAnswer();
                    onRTCCallHandle("");
                });

    }

    @Override
    public void onCallRequestFailedSinceUserHasLeftStream(String userName) {
        showToast(getString(R.string.vid_call_user_not_available), Toast.LENGTH_SHORT);
        mConversationWithUser = "";
    }

    @Override
    public void onVideoCallClicked(String userId, String userName) {
        if (isAbleMakeVideoCall()) {
            mConversationWithUser = userName;
            if (mStreamPresenter != null) mStreamPresenter.callRequest(userId, userName);
        } else {
            showToast(getString(R.string.vid_in_a_call), Toast.LENGTH_SHORT);
        }

    }

    private boolean isAbleMakeVideoCall() {
        return mConversationWithUser.isEmpty();
    }

    @Override
    public void onFollowCountChanged(int count) {
        //current do nothing
    }

    @Override
    public void onDimissed() {
        showFullScreen(getWindow());
    }

    @Override
    public void onChangeFollowStatus(String userId, int status) {
        //current do nothing
    }

    private void onReportUser(String reason, String userId) {
        ReportUserRequestModel request = new ReportUserRequestModel();
        request.setReportedUserId(userId);
        request.setReason(reason);

        mCompositeSubscription.add(AppsterWebServices.get().reportUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(blockOrReportUserDataResponse -> getSupportFragmentManager().findFragmentByTag(USER_DIALOG_TAG).onDestroyView()
                        , error -> handleError(error.getMessage(), Constants.RETROFIT_ERROR)));

    }


    //endregion
    //region keyboard
//    public int getStatusBarHeight() {
//        int result = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }

    private boolean mIsChangeCategoryPosition;

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        Timber.d("onKeyboardHeightChanged %d", height);
        if (height == 0) {
//            if (etComment != null) etComment.clearFocus();
            if (isStreamReady) showAllView();
            showFullScreen(getWindow());
            scrollCommentListToEnd();
        }


//        showUserActions();

        if (!isStreamReady) {
            llShareAndStart.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            llBottomContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        if (height > 150) {
            if (isStreamReady) {
                height += getNavigationBarSize(this).y;

                llGiftGroup.animate().setDuration(100).translationY(-height).setListener(this).start();
                llBottomContainer.animate().setDuration(100).translationY(-height).setListener(this).start();
                if (llStreamingToppanel != null) llStreamingToppanel.setVisibility(View.INVISIBLE);
                mBtnLiveShop.setVisibility(View.INVISIBLE);
                mBtnLiveTitle.setVisibility(View.INVISIBLE);
            }
        } else {
            if (!isStreamReady) {
                if (mIsChangeCategoryPosition) {
                    llTitleCategory.animate().setDuration(100).translationY(0).start();
                    mIsChangeCategoryPosition = false;
                }
                llShareAndStart.animate().setDuration(100).translationY(0).setListener(this).start();
            } else {
                llBottomContainer.animate().setDuration(100).translationY(0).setListener(this).start();
                llGiftGroup.animate().setDuration(100).translationY(0).setListener(this).start();
                if (llStreamingToppanel != null) llStreamingToppanel.setVisibility(View.VISIBLE);
                if (mIsSeller) mBtnLiveShop.setVisibility(View.VISIBLE);
                if (mIsStreamTitleAvailable) mBtnLiveTitle.setVisibility(View.VISIBLE);
                if (etComment != null) etComment.clearFocus();
            }
        }

    }


    @Override
    public void onAnimationStart(Animator animator) {
        // current do nothing
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if (!isStreamReady) {
            if (llShareAndStart != null) llShareAndStart.setLayerType(View.LAYER_TYPE_NONE, null);
        } else {
            if (llShareAndStart != null && llGiftGroup != null) {
                llBottomContainer.setLayerType(View.LAYER_TYPE_NONE, null);
                llGiftGroup.setLayerType(View.LAYER_TYPE_NONE, null);
                llBottomContainer.requestLayout();
                llGiftGroup.requestLayout();
            }


        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {
// current do nothing
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
// current do nothing
    }

    //endregion
    //region sharing
    @Override
    public void onNotLoginForSharing() {
        // current do nothing
    }

    @Override
    public void onStartSharing(SocialManager.TypeShare typeShare, Context context) {
        SocialManager.getInstance().isComepleteSharing = false;
    }

    @Override
    public void onErrorSharing(SocialManager.TypeShare typeShare, Context context, String message) {
        // current do nothing
    }

    @Override
    public void onCompleteSharing(SocialManager.TypeShare typeShare, Context context, String message) {
        SocialManager.getInstance().isComepleteSharing = true;
        SocialManager.getInstance().setBitmapSend(null);
        SocialManager.getInstance().socialSharingListener = null;
        switch (typeShare) {
            case SHARE_FACEBOOK:
                showToast(getString(R.string.stream_share_facebook_success), Toast.LENGTH_SHORT);
                break;
            case SHARE_TWITTER:
                showToast(getString(R.string.stream_share_twitter_success), Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }
//        showToast(getString(R.string.stream_share_facebook_success),Toast.LENGTH_SHORT);
    }


//endregion

    //region share endstream
    void shareEndStreamViaWhatapps(Uri shareUri, String urlShare) {
//        if (shareUri == null) {
//            return;
//        }
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.setType("image/*");
//        sendIntent.setPackage("com.whatsapp");
//        sendIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_end_result_message));
//
//        try {
//            startActivityForResult(sendIntent, Constants.REQUEST_CODE_SHARE_FEED);
//        } catch (ActivityNotFoundException ex) {
//            Toast.makeText(getApplicationContext(), getString(R.string.whatsapp_have_not_been_installed), Toast.LENGTH_SHORT).show();
//        }

        if (shareUri == null) return;
        if (StringUtil.isNullOrEmptyString(urlShare)) {
            return;
        }
        SocialManager.getInstance().shareStreamURLToWhatsapp(this, shareUri, urlShare, AppsterApplication.mAppPreferences.getUserModel().getUserName());
        // Track event
        EventTracker.trackShareWhatsApp();
    }

    void shareEndStreamViaEmail(Uri shareUri, String urlShare) {
        if (shareUri == null) {
            return;
        }
        if (StringUtil.isNullOrEmptyString(urlShare)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
//        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_end_result_message));
        intent.putExtra(Intent.EXTRA_SUBJECT, getTitleShareEndStream(urlShare));
        intent.putExtra(Intent.EXTRA_STREAM, shareUri);

        try {
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, Constants.REQUEST_CODE_SHARE_FEED);
            }
        } catch (ActivityNotFoundException ex) {
            Timber.e(ex.getMessage());
            Toast.makeText(getApplicationContext(), getString(R.string.email_have_not_been_installed), Toast.LENGTH_SHORT).show();
        }
        // Track event
        EventTracker.trackShareEmail();
    }

    void shareEndStreamOthers(Uri shareUri, String urlShare) {

        if (shareUri == null) return;
        if (StringUtil.isNullOrEmptyString(urlShare)) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, getTitleShareEndStream(urlShare));
        intent.putExtra(Intent.EXTRA_STREAM, replaceUriNotAllowed(shareUri));
        startActivity(Intent.createChooser(intent, "Share Via"));

    }

    String getTitleShareEndStream(String urlShare) {
        return "@" + AppsterApplication.mAppPreferences.getUserModel().getUserName() + System.getProperty("line.separator") +
                getString(R.string.share_end_result_message) +
                System.getProperty("line.separator") + urlShare;
    }

    void shareEndStreamViaTwitter(Uri shareUri, String urlShare) {
        if (shareUri == null) {
            return;
        }
        if (StringUtil.isNullOrEmptyString(urlShare)) {
            return;
        }
        //            if (!StringUtil.isNullOrEmptyString(mShareResultUrl)) {
        TweetComposer.Builder builder = new TweetComposer.Builder(getViewContext())
//                .text(getString(R.string.share_end_result_message))
                .text(getTitleShareEndStream(urlShare))
                .image(replaceUriNotAllowed(shareUri));
        builder.show();
//                SocialManager.getInstance().ShareFeedQuotesToTwitter(getApplicationContext(), "", mShareResultUrl);
        // Track event
        EventTracker.trackShareTwitter();
    }

    void shareEndStreamViaInstagram(Uri shareUri) {
        if (shareUri == null) {
            return;
        }
        SocialManager.getInstance().shareFeedToInstagram(getViewContext(), CommonDefine.TYPE_IMAGE, shareUri);
        // Track event
        EventTracker.trackShareInstagram();
    }

    void shareEndStreamViaFacebook(String urlShare) {
        if (StringUtil.isNullOrEmptyString(urlShare)) {
            return;
        }
        ShareDialog shareDialog = new ShareDialog(this);
//        SharePhoto photo = new SharePhoto.Builder().setBitmap(shareBitmap).setCaption(getString(R.string.share_end_result_message)).build();
//        SharePhotoContent linkContent = new SharePhotoContent.Builder()
//                .addPhoto(photo)
//                .build();
//        shareDialog.show(linkContent);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(urlShare))
                .setQuote(getTitleShareEndStream(urlShare))
                .build();
        shareDialog.show(linkContent);

        // Track event
        EventTracker.trackShareFacebook();
    }

    private Uri replaceUriNotAllowed(Uri uri) {
        if (uri == null)
            return uri;

        String uriString = uri.toString();
        if (StringUtil.isNullOrEmptyString(uriString)) return uri;
        if (uriString.startsWith("file:///")) {
            uriString = uriString.replace("file:///", "");
        }

        return FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(uriString));

    }
    //endregion

    void resetStreamStateAfterCallingForHuaWeiDevices() {
        String deviceMan = Build.MANUFACTURER;
        Timber.e("device manufacturer -> %s", deviceMan);
        if (deviceMan.toLowerCase().trim().equalsIgnoreCase("huawei")) {
            mCompositeSubscription.add(Observable.just(true)
                    .delay(10, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(aBoolean -> !isFinishing() && !isDestroyed() && !mIsEndStream)
                    .subscribe(reset -> {
                        if (mStreamer != null) {
                            mStreamer.onPause();
                            mStreamer.setUseDummyAudioCapture(true);
                            mStreamer.stopCameraPreview();
                            mStreamer.setDisplayPreview(mCameraPreviewView);
                            mStreamer.onResume();
                            mStreamer.setUseDummyAudioCapture(false);
                        }
//                        startCameraPreviewWithPermCheck();
                        requestPermissions();
                    }));
        }
    }

    void saveCurrentStream() {
        EventTracker.trackEvent(EventTrackingName.EVENT_LIVE_STREAM_SAVE);
        uploadChatMessage();
        mStreamPresenter.saveStream(mSlug);
    }

    private void uploadChatMessage() {
        if (mRecordedMessagesModels != null) {
            mStreamPresenter.saveStreamChatMessage(mRecordedMessagesModels);
        }
    }

    boolean isStreamOver1Min() {
        return getStreamDuration() > 60 && !mIsEndStreamByAdmin;
    }

    void goBackToHomeScreen() {
        setResult(RESULT_OK);
        finish();
    }

    void goToUserProfileScreen() {
        Intent intent = new Intent();
        intent.putExtra(ConstantBundleKey.BUNDLE_GO_PROFILE, true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private boolean shouldShowTutorial() {
        return !GlobalSharedPreferences.isTutorialHostStreamShown(this);
    }

    private void checkToShowTutorial() {
        if (shouldShowTutorial()) {
            mCompositeSubscription.add(Completable.complete()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showStreamTitleTutorial, Timber::e));
        }
    }

    private void showStreamTitleTutorial() {
        mIsTutorialShowing = true;
        new ShowCaseViewTutorial.Builder(this)
                .setAnchorView(mBtnLiveTitle)
                .setBubbleMarginTopBottom(Utils.dpToPx(82))
                .setBubbleMessage(getString(R.string.tutorial_stream_title))
                .setHorizontalArrow()
                .setOnShowCaseViewDismiss(() -> mCompositeSubscription.add(Completable.timer(Constants.DELAYED_TIME_SHOWN_NEXT_TUTORIAL, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::showSnsTutorial, Timber::e))).build().show(this);
    }

//    private void showFaceStickerTutorial() {
//        new ShowCaseViewTutorial.Builder(this)
//                .setAnchorView(mBtnStickerOption)
//                .setBubbleMessage(getString(R.string.tutorial_face_sticker))
//                .setOnShowCaseViewDismiss(() -> mCompositeSubscription.add(Completable.timer(Constants.DELAYED_TIME_SHOWN_NEXT_TUTORIAL, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(this::showSnsTutorial, Timber::e))).build().show(this);
//    }

    private void showSnsTutorial() {
        new ShowCaseViewTutorial.Builder(this)
                .setAnchorView(mIbShareSns)
                .setBubbleMessage(getString(R.string.tutorial_sns_host))
                .setOnShowCaseViewDismiss(this::onFinishTutorial).build().show(this);
    }

    private void onFinishTutorial() {
        mIsTutorialShowing = false;
        GlobalSharedPreferences.setTutorialHostStreamShown(this);
    }

    private void removeTutorialIfNeed() {
        ShowCaseViewTutorial.removeItself(this);
    }

    @Override
    public void onSubStreamAvailable(SubStreamData subStreamData) {
        onRTCCallHandle(mSlug);
    }

    @Override
    public void onSubStreamDisconnected() {
        updateSubStreamDisconnected();
    }


    @Override
    public void onTriviaInfoReceived(TriviaInfoModel triviaInfoModel) {
        triviaCountDownView.setVisibility(View.VISIBLE);
        tvCountDownStatus.setText("Question 1 in");
        if (mTriviaView != null) mTriviaView.setRole(Role.HOST);
        startOnOffFaceTimerCountDown(triviaInfoModel.secsToBegin);
        displayTriviaExtraActions();
        startTriviaQuestionApiTimerCountDown(triviaInfoModel.secsToGetTriviaQuestionsApi, triviaInfoModel.triviaId);

    }

    private void displayTriviaExtraActions() {
        clTriviaExtraActionsContainer.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btnTriviaTopWinner)
    public void onTopWinnerClicked() {
        if (mTriviaRankingDialog == null) {
            mTriviaRankingDialog = TriviaRankingDialog.newInstance("");
            mTriviaRankingDialog.setRecyclerItemCallBack((item, position) -> showUserProfile(item.getUserName(), item.getUserAvatar(), mSlug));
        }
        mTriviaRankingDialog.show(getSupportFragmentManager(), TriviaRankingDialog.class.getSimpleName());
    }

    @OnClick(R.id.btnTriviaHowToPlay)
    public void onHowToPlayClicked(View view) {
        AppsterUtility.temporaryLockView(view);
        if (mTriviaHowToPlayDialog == null) {
            mTriviaHowToPlayDialog = TriviaHowToPlayDialog.newInstance("");
        }
        mTriviaHowToPlayDialog.show(getSupportFragmentManager(), TriviaHowToPlayDialog.class.getSimpleName());
    }

    @Override
    public void displayTriviaQuestion(TriviaInfoModel.Questions question, int countDownTime) {
        Timber.e("displayTriviaQuestion");
        // Dismiss How To Play Dialog
        if (mTriviaHowToPlayDialog != null) {
            mTriviaHowToPlayDialog.dismissAllowingStateLoss();
        }

        if (mTriviaRankingDialog != null) {
            mTriviaRankingDialog.dismissAllowingStateLoss();
        }

        if (pointInfoDialog != null) pointInfoDialog.dismissAllowingStateLoss();

        mTriviaView.startQuestion(question, countDownTime);
        mTriviaView.startAnimation(mTriviaInAnim);
        mTriviaView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onTriviaOptionSelected(TriviaInfoModel.Questions.Options selectedOption) {
        if (mStreamPresenter != null) mStreamPresenter.triviaAnswer(selectedOption.optionId);
    }

    @Override
    public void onUserGameStateUpdated(int newState) {
        //not use for host
    }

    @Override
    public void displayTriviaWinnerList(TriviaFinishModel triviaFinishModel) {
        Timber.e("displayTriviaWinnerList");
        showTriviaWinnerLayout(triviaFinishModel.winnerCount, triviaFinishModel.prizePerUserString, triviaFinishModel.message);
    }

    @Override
    public void dismissTriviaQuestion() {
        mTriviaView.startAnimation(mTriviaOutAnim);
    }

    @Override
    public void displayTriviaResult(TriviaResultModel triviaResultModel, String questionText) {
        mTriviaView.startAnimation(mTriviaInAnim);
        mTriviaView.showAnswer(triviaResultModel, "");
        mTriviaView.setAnswerStatusText(questionText);
    }

    @Override
    public void dismissTriviaResult() {
        mTriviaView.startAnimation(mTriviaResultOutAnim);
    }

    @Override
    public void dismissWinnerList() {
        if (triviaCountDownView != null) {
            triviaCountDownView.setVisibility(View.GONE);
        }
        if (vsTriviaWinner != null) {
            vsTriviaWinner.setVisibility(View.GONE);
        }
        mTriviaShowListener.onNext(true);
    }

    @Override
    public void showTriviaWinnerListData(List<DisplayableItem> triviaWinnerListPagingEntity) {
        if (mTriviaWinnerLayout != null) {
            mTriviaWinnerLayout.showTriviaWinnerList(triviaWinnerListPagingEntity);
        }
    }

    @Override
    public void getTriviaWinnerListError() {
        if (mTriviaWinnerLayout != null) {
            mTriviaWinnerLayout.getTriviaWinnerListError();
        }
    }

    @Override
    public void countDownToFaceTime(String content, int countDownSecs) {
        if (mTriviaOnOffFaceTimer != null) mTriviaOnOffFaceTimer.cancel();
        triviaCountDownView.setVisibility(View.VISIBLE);
        tvCountDownStatus.setText(content);
        final int countDownTextSizeIfHasMinute = 30;
        final int countDownTextSize = 65;
        mTriviaOnOffFaceTimer = new CountDownTimer(countDownSecs * 1000L, 500) {

            @Override
            public void onTick(long l) {
                int seconds = (int) (l / 1000) % 60;
                int minutes = (int) TimeUnit.SECONDS.toMinutes((int) (l / 1000));
                if (tvTriviaCountDownText != null) {
                    if (minutes > 0 && tvTriviaCountDownText.getTextSize() != countDownTextSizeIfHasMinute) {
                        tvTriviaCountDownText.setTextSize(countDownTextSizeIfHasMinute);
                    } else if (tvTriviaCountDownText.getTextSize() != countDownTextSize) {
                        tvTriviaCountDownText.setTextSize(countDownTextSize);
                    }

                    tvTriviaCountDownText.setText(minutes > 0 ? String.format(Locale.US, "%02d:%02d", minutes, seconds) : String.valueOf(seconds));
                }
            }

            @Override
            public void onFinish() {
                if (tvTriviaCountDownText != null) tvTriviaCountDownText.setText(String.valueOf(0));
            }
        }.start();
    }

    @Override
    public void countDownToOffScreen(String content, int countDownSecs) {
        if (mTriviaOnOffFaceTimer != null) mTriviaOnOffFaceTimer.cancel();
        triviaCountDownView.setVisibility(View.VISIBLE);
        tvCountDownStatus.setText(content);
        final int countDownTextSizeIfHasMinute = 30;
        final int countDownTextSize = 65;
        mTriviaOnOffFaceTimer = new CountDownTimer(countDownSecs * 1000L, 500) {

            @Override
            public void onTick(long l) {
                int seconds = (int) (l / 1000) % 60;
                int minutes = (int) TimeUnit.SECONDS.toMinutes((int) (l / 1000));
                if (tvTriviaCountDownText != null) {
                    if (minutes > 0 && tvTriviaCountDownText.getTextSize() != countDownTextSizeIfHasMinute) {
                        tvTriviaCountDownText.setTextSize(countDownTextSizeIfHasMinute);
                    } else if (tvTriviaCountDownText.getTextSize() != countDownTextSize) {
                        tvTriviaCountDownText.setTextSize(countDownTextSize);
                    }

                    tvTriviaCountDownText.setText(minutes > 0 ? String.format(Locale.US, "%02d:%02d", minutes, seconds) : String.valueOf(seconds));
                }
            }

            @Override
            public void onFinish() {
                if (tvTriviaCountDownText != null) tvTriviaCountDownText.setText(String.valueOf(0));
            }
        }.start();
    }

    private void startTriviaQuestionApiTimerCountDown(int countDownSecs, int triviaId) {

        if (mTriviaGetQuestionCountTime != null) mTriviaGetQuestionCountTime.cancel();
        mTriviaGetQuestionCountTime = new CountDownTimer(countDownSecs * 1000, 500) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                mStreamPresenter.getTriviaQuestionInfo(triviaId);
            }
        }.start();
    }

    private void startOnOffFaceTimerCountDown(int countDownSecs) {
        if (mTriviaOnOffFaceTimer != null) mTriviaOnOffFaceTimer.cancel();
        final int countDownTextSizeIfHasMinute = 30;
        final int countDownTextSize = 65;
        Timber.e("startOnOffFaceTimerCountDown %s", countDownSecs);
        mTriviaOnOffFaceTimer = new CountDownTimer(countDownSecs * 1000L, 500) {

            @Override
            public void onTick(long l) {
                int seconds = (int) (l / 1000) % 60;
                int minutes = (int) TimeUnit.SECONDS.toMinutes((int) (l / 1000));
                if (tvTriviaCountDownText != null) {
                    if (minutes > 0 && tvTriviaCountDownText.getTextSize() != countDownTextSizeIfHasMinute) {
                        tvTriviaCountDownText.setTextSize(countDownTextSizeIfHasMinute);
                    } else if (tvTriviaCountDownText.getTextSize() != countDownTextSize) {
                        tvTriviaCountDownText.setTextSize(countDownTextSize);
                    }

                    tvTriviaCountDownText.setText(minutes > 0 ? String.format(Locale.US, "%02d:%02d", minutes, seconds) : String.valueOf(seconds));
                }
            }

            @Override
            public void onFinish() {
                if (tvTriviaCountDownText != null) tvTriviaCountDownText.setText(String.valueOf(0));
//                if(triviaCountDownView!=null) triviaCountDownView.setVisibility(View.GONE);
                if (mStreamPresenter != null) mStreamPresenter.triviaGameStarted();
            }
        }.start();
    }

    private void updateSubStreamDisconnected() {
        if (mSubPlayerLayout != null) mSubPlayerLayout.updateState(State.DISCONNECTED);
        if (vsSubPlayer != null) vsSubPlayer.setVisibility(View.GONE);
        mConversationWithUser = "";
        mGuestAlreadyEndedCall.set(false);
    }

    @Override
    public void onEndCallClicked() {
        //                    if (mSubPlayerLayout != null) mSubPlayerLayout.stopPlayer();
        //                    if (mSubPlayerLayout != null) mSubPlayerLayout.stopPlayer();
        mLiveEndConfirmation = new DialogbeLiveConfirmation.Builder()
                .title(getString(R.string.calling_disconnect_title))
                .message("Are you sure you want to disconnect with this guest?")
                .onConfirmClicked(this::notifyCallEndedState).build();
        mLiveEndConfirmation.show(this);
    }

    void notifyCallEndedState() {
        if (!isFinishing() && !isDestroyed()) {
            removeCallWaitingTimeout();
            RxUtils.unsubscribeIfNotNull(mFirstFrameTimeoutSubscription);
            onRTCCallHandle("");
            if (mStreamPresenter != null) mStreamPresenter.notifySubStreamEnded();
            if (mSubPlayerLayout != null) mSubPlayerLayout.updateState(State.DISCONNECTING);

        }
    }

//    @Override
//    public void onSubScreenDisplayNameClicked(String userName, String profilePic) {
////        showUserProfile(userName, profilePic, mSlug);
//    }

    @Override
    public void onCountDownCompleted() {
        Timber.e("onCountDownCompleted");
        if (mGuestAlreadyEndedCall.get()) {
            Timber.e("onCountDownCompleted but guest already ended call");
            //user has left before join rtc channel
            notifyCallEndedState();
        } else {
            setupFirstFrameTimeout();
        }
//        if (mStreamPresenter != null) {
//            mStreamPresenter.startRTC();
//        }
    }

    @Override
    public void onShowProfileClicked(String userName, String profilePic) {
        showUserProfile(userName, profilePic, mSlug);
    }

    private void onShowProfileClicked(String userName) {
        if (mIsTriviaShow) return;
        onShowProfileClicked(userName, "");
    }

    private void setupFirstFrameTimeout() {
        mFirstFrameTimeoutSubscription = Observable.just(true)
                .delay(10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(s -> (!isFinishing() && !isDestroyed()) && !mIsFirstFrameVideoCallDecoded.get() && mIsCaling)
                .subscribe(aBoolean -> notifyCallEndedState());
    }


    //region endstream
    static class EndStreamLayout {
        @Bind(R.id.video_view_stream_ended_container)
        FrameLayout videoViewStreamEndedContainer;
        @Bind(R.id.end_user_image)
        ImageView endUserImage;
        @Bind(R.id.duration_time)
        CustomFontTextView durationTime;

        @Bind(R.id.score_on_stream_end)
        CustomFontTextView scoreOnStreamEnd;

        @Bind(R.id.like_received_count)
        CustomFontTextView likeReceivedCount;

        @Bind(R.id.gift_received_count)
        CustomFontTextView giftReceivedCount;

        @Bind(R.id.end_im_facebook)
        ImageView endTtFacebook;
        @Bind(R.id.end_im_instagram)
        ImageView endTxtInstagram;
        @Bind(R.id.end_im_whatsapp)
        ImageView endTxtWhatsapp;
        @Bind(R.id.end_im_twitter)
        ImageView endTxtTwitter;
        @Bind(R.id.end_im_wechat)
        ImageView endTxtWeChat;
        @Bind(R.id.end_im_weibo)
        ImageView endTxtWeibo;
        @Bind(R.id.end_im_email)
        ImageView endTxtEmail;
        @Bind(R.id.end_im_others)
        ImageView endTxtOthers;
        @Bind(R.id.txt_post_description)
        TextView txtDescription;
        @Bind(R.id.go_back_stream_end)
        CustomFontButton goBackStreamEnd;
        @Bind(R.id.go_profile)
        CustomFontButton goProfile;
        @Bind(R.id.title_ended_stream)
        CustomFontTextView titleEndedStream;

        Uri mShareUri;
        private File mShareFile;
        private Bitmap mShareBitmap;
        boolean isEndImageLoadCompleted;
        boolean isEndStreamDataLoaded;
        String urlShare;

        private final StreamingActivityGLPlus mParentActivity;

        EndStreamLayout(View view, StreamingActivityGLPlus parentActivity, String reason) {
            mParentActivity = new WeakReference<>(parentActivity).get();
            ButterKnife.bind(this, view);
            if (!reason.isEmpty())
                Answers.getInstance().logCustom(new CustomEvent("End Stream Screen")
                        .putCustomAttribute("endstreamscreen", mParentActivity.getTrackingLiveReason(reason)));
            ImageLoaderUtil.displayUserImage(view.getContext().getApplicationContext(),
                    mParentActivity.mCurrentUser.getUserImage(),
                    endUserImage, true, new ImageLoaderUtil.ImageLoaderCallback() {
                        @Override
                        public void onFailed(Exception e) {
                            isEndImageLoadCompleted = true;
                            if (isEndStreamDataLoaded) {
                                endableSNS();
                            }
                        }

                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            isEndImageLoadCompleted = true;
                            if (isEndStreamDataLoaded) {
                                endableSNS();
                            }
                        }
                    });
            disableSNS();
            setupButtons();

        }

        void takeScreenShot() {
            mShareBitmap = BitmapUtil.screenShot(videoViewStreamEndedContainer);
            mShareUri = BitmapUtil.storeAndGetUri(mShareBitmap, "result_image.png");
            if (mShareUri != null) {
                mShareFile = new File(mShareUri.getPath());
            }

            Timber.e("Take screen shot completed!");
        }

        void disableSNS() {
            endTtFacebook.setEnabled(false);
            endTxtInstagram.setEnabled(false);
            endTxtWhatsapp.setEnabled(false);
            endTxtTwitter.setEnabled(false);
            endTxtEmail.setEnabled(false);
            endTxtWeChat.setEnabled(false);
            endTxtWeibo.setEnabled(false);
            endTxtOthers.setEnabled(false);

            if (AppsterApplication.mAppPreferences.getUserCountryCode().equals(CountryCode.CHINA)) {
                endTtFacebook.setVisibility(View.GONE);
                endTxtInstagram.setVisibility(View.GONE);
                endTxtWhatsapp.setVisibility(View.GONE);
                endTxtTwitter.setVisibility(View.GONE);
                endTxtEmail.setVisibility(View.GONE);
                endTxtWeChat.setVisibility(View.VISIBLE);
                endTxtWeibo.setVisibility(View.VISIBLE);
            } else {
                endTtFacebook.setVisibility(View.VISIBLE);
                endTxtInstagram.setVisibility(View.VISIBLE);
                endTxtWhatsapp.setVisibility(View.VISIBLE);
                endTxtTwitter.setVisibility(View.VISIBLE);
                endTxtEmail.setVisibility(View.VISIBLE);
                endTxtWeChat.setVisibility(View.GONE);
                endTxtWeibo.setVisibility(View.GONE);
            }
        }

        void endableSNS() {
            endTtFacebook.setEnabled(true);
            endTxtInstagram.setEnabled(true);
            endTxtWhatsapp.setEnabled(true);
            endTxtTwitter.setEnabled(true);
            endTxtEmail.setEnabled(true);
            endTxtWeChat.setEnabled(true);
            endTxtWeibo.setEnabled(true);
            endTxtOthers.setEnabled(true);

            if (AppsterApplication.mAppPreferences.getUserCountryCode().equals(CountryCode.CHINA)) {
                endTtFacebook.setVisibility(View.GONE);
                endTxtInstagram.setVisibility(View.GONE);
                endTxtWhatsapp.setVisibility(View.GONE);
                endTxtTwitter.setVisibility(View.GONE);
                endTxtEmail.setVisibility(View.GONE);
                endTxtWeChat.setVisibility(View.VISIBLE);
                endTxtWeibo.setVisibility(View.VISIBLE);
            } else {
                endTtFacebook.setVisibility(View.VISIBLE);
                endTxtInstagram.setVisibility(View.VISIBLE);
                endTxtWhatsapp.setVisibility(View.VISIBLE);
                endTxtTwitter.setVisibility(View.VISIBLE);
                endTxtEmail.setVisibility(View.VISIBLE);
                endTxtWeChat.setVisibility(View.GONE);
                endTxtWeibo.setVisibility(View.GONE);
            }
            mParentActivity.releaseKSYResources();
        }

        @OnClick(R.id.end_im_facebook)
        void shareFaceBookEnded() {
            Timber.e("shareFaceBookEnded");
            captureEndResult();
            mParentActivity.shareEndStreamViaFacebook(urlShare);
        }

        private void captureEndResult() {
            releaseResultImage();
            takeScreenShot();
        }

        @OnClick(R.id.end_im_instagram)
        void shareInstagramEnded() {
            captureEndResult();
            mParentActivity.shareEndStreamViaInstagram(mShareUri);

        }

        @OnClick(R.id.end_im_email)
        void shareEmailEnded() {
            captureEndResult();
            mParentActivity.shareEndStreamViaEmail(mShareUri, urlShare);

        }

        @OnClick(R.id.end_im_whatsapp)
        public void shareWhatappsEnded() {
            captureEndResult();
            mParentActivity.shareEndStreamViaWhatapps(mShareUri, urlShare);
        }


        @OnClick(R.id.end_im_twitter)
        public void shareTwitterEnded() {
            captureEndResult();
            mParentActivity.shareEndStreamViaTwitter(mShareUri, urlShare);
        }

        @OnClick(R.id.end_im_wechat)
        public void shareWeChatEnded() {
            //captureEndResult();
            //mParentActivity.shareEndStreamViaWeChat(mShareUri);
        }

        @OnClick(R.id.end_im_weibo)
        public void shareWeiboEnded() {
            //captureEndResult();
            //mParentActivity.shareEndStreamViaWeibo(mShareUri);
        }

        @OnClick(R.id.end_im_others)
        public void shareOthersEnded() {
            captureEndResult();
            mParentActivity.shareEndStreamOthers(mShareUri, urlShare);
        }

        void setShareUrl(String shareUrl) {
            //current is not using this
            this.urlShare = shareUrl;
        }

        @OnClick(R.id.go_back_stream_end)
        void onHomeButtonClicked() {

            if (mParentActivity.isStreamOver1Min()) {
                DialogUtil.showConfirmIgnoreLiveStream(mParentActivity, this::goHome, null);
            } else {
//                goHome();
                AppsterApplication.mAppPreferences.setIsNotNeedRefreshHome(true);
                releaseResultImage();
                mParentActivity.finish();
            }
        }

        @OnClick(R.id.go_profile)
        void onProfileButtonClicked() {
            if (mParentActivity.isStreamOver1Min()) {
                mParentActivity.saveCurrentStream();
            } else {
                goToProfileScreen();
            }
        }

        void goToProfileScreen() {
            releaseResultImage();
            mParentActivity.goToUserProfileScreen();
        }


        void goHome() {
            releaseResultImage();
            mParentActivity.goBackToHomeScreen();
        }


        void releaseResultImage() {
            if (mShareFile != null && mShareFile.exists()) {
                mShareFile.delete();
            }
            if (mShareBitmap != null && !mShareBitmap.isRecycled()) {
                mShareBitmap.recycle();
                mShareBitmap = null;
            }
        }


        void setTitle(String title) {
            if (!StringUtil.isNullOrEmptyString(title)) {
                titleEndedStream.setAllCaps(false);
                titleEndedStream.setText(title);
            }
        }

        void updateEndStreamData(EndStreamDataModel data) {
            videoViewStreamEndedContainer.setVisibility(View.VISIBLE);
            durationTime.setText(AppsterUtility.parseStreamingTimeToHHMM(data.getDuration()));
            scoreOnStreamEnd.setText(Utils.formatThousand(data.getViewCount()));
            likeReceivedCount.setText(Utils.formatThousand(data.getLikeCount()));
            giftReceivedCount.setText(Utils.formatThousand(data.getTotalGold()));
            mParentActivity.flLiveContentContainer.setVisibility(View.INVISIBLE);

            isEndStreamDataLoaded = true;
            if (StringUtil.isNullOrEmptyString(mParentActivity.mCurrentUser.getUserImage())) {
                isEndImageLoadCompleted = true;
            }
            if (isEndImageLoadCompleted) {
                endableSNS();
            }
        }

        void setupButtons() {
            if (mParentActivity.isStreamOver1Min()) {
                goProfile.setText(R.string.ended_stream_post);
                goBackStreamEnd.setBackgroundResource(R.drawable.btn_end_live_stream_delete);
                goBackStreamEnd.setTextColor(ContextCompat.getColor(mParentActivity, R.color.color_f05c56));
                goBackStreamEnd.setText(R.string.delete);
                txtDescription.setText(R.string.record_instruction);
            } else {
                goBackStreamEnd.setBackgroundResource(R.drawable.btn_end_live_stream_delete);
                goBackStreamEnd.setTextColor(Color.parseColor("#FF5167"));
                goBackStreamEnd.setText(R.string.ended_stream_back);
                txtDescription.setText(R.string.record_instruction_below_one_minute);
            }
        }
    }

//endregion

    //region trivia winner screen
    TriviaWinnerLayout mTriviaWinnerLayout;

    private void showTriviaWinnerLayout(int winnerCount, String prizePerUserString, String message) {
        if (vsTriviaWinner.getParent() != null && mTriviaWinnerLayout == null) {
            View view = vsTriviaWinner.inflate();
            mTriviaWinnerLayout = new TriviaWinnerLayout(view, this);
            mTriviaWinnerLayout.updateView(winnerCount, prizePerUserString, message);
        }
    }

    static class TriviaWinnerLayout implements OnLoadMoreListenerRecyclerView {
        private final StreamingActivityGLPlus mParent;
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

        TriviaWinnerLayout(View view, StreamingActivityGLPlus parent) {
            Timber.e("GuestSubStreamLayout init");
            ButterKnife.bind(this, view);
            this.mParent = new WeakReference<>(parent).get();
            setAdapter();
            parent.getTriviaWinnerList();
        }

        private void setAdapter() {
            DialogManager.getInstance().dismisDialog();
            mWinnerItems = new ArrayList<>();
            winnerListAdapter = new WinnerListAdapter(new DiffCallBaseUtils(), new ArrayList<>(), null);
            rcvWinnerList.setOnLoadMoreListener(this);
            rcvWinnerList.setAdapter(winnerListAdapter);
        }

        void showTriviaWinnerList(List<DisplayableItem> winnerList) {
            if (winnerList != null && !winnerList.isEmpty()) {
                mWinnerItems.addAll(winnerList);
                winnerListAdapter.updateItems(mWinnerItems);
            }
            rcvWinnerList.setLoading(false);
            checkEmptyList();
        }

        void getTriviaWinnerListError() {
            rcvWinnerList.setLoading(false);
        }

        private void checkEmptyList() {
            if (mWinnerItems != null && mWinnerItems.isEmpty()) {
                rcvWinnerList.setVisibility(View.GONE);
            }
        }

        void updateView(int winnerCount, String prizePerUserString, String message) {
            if (tvNumWinner != null) {
                tvNumWinner.setText(String.format("%s winners!", winnerCount));
            }

            if (tvCongratsText != null) {
                tvCongratsText.setText(message);
            }

            if (tvPrizeAmount != null) {
                tvPrizeAmount.setText(prizePerUserString);
            }
        }

        @Override
        public void onLoadMore() {
            if (mParent != null) {
                mParent.getTriviaWinnerList();
            }
        }
    }

    void getTriviaWinnerList() {
        mStreamPresenter.getTriviaWinnerList();
    }

    //endregion

    //region preLive
    static class PreLiveLayout implements TagGroup.OnTagClickListener {

        @Bind(R.id.tag_group)
        TagGroup tagGroup;

        @Bind(R.id.ll_category_back)
        LinearLayout llBack;

        @Bind(R.id.img_category_back)
        ImageButton btnBack;

        ArrayList<String> tagList;
        TagScreenListener callback;

        public PreLiveLayout(View view, List<TagListLiveStreamModel> streamTagList, TagScreenListener callback) {
            ButterKnife.bind(this, view);
            this.callback = callback;
            tagGroup.setOnTagClickListener(this);
            if (tagList == null && streamTagList != null) {
                tagList = new ArrayList<>();
                for (TagListLiveStreamModel tagModel : streamTagList) {
                    tagList.add(tagModel.getTagName());
                }
                tagGroup.setTags(tagList);
                tagGroup.setGravity(TagGroup.TagGravity.MIDDLE);
            }
        }


        @OnClick({R.id.ll_category_back, R.id.img_category_back})
        public void onCategorySelected() {
            if (callback != null) {
                callback.onBackClick();
            }
        }


        @Override
        public void onTagClick(String tag) {
            if (callback != null) {
                callback.onTagClick();
            }
        }

        public interface TagScreenListener {
            void onTagClick();

            void onBackClick();

        }
    }
//endregion

    //region luckywheel
    class LuckyWheelLayout {
        @Bind(R.id.luckyWheel)
        LuckyWheelView luckyWheel;
        @Bind(R.id.playLucky)
        ImageButton playLucky;

        private final int UNKNOW_RESULT = -1;
        List<LuckyItem> data = new ArrayList<>();
        String host = "Host,1000".toUpperCase();
        String viewer = "Viewer,1000".toUpperCase();
        String url = "https://vi.gravatar.com/userimage/30130623/8d47462c1b150c3ad6dc6f471b59161d.jpg?size=128";
        private int mLuckyWheelResultIndex;
        private boolean mAwardReady = false;

        LuckyWheelLayout(View view) {
            ButterKnife.bind(this, view);
            playLucky.setEnabled(false);
            luckyWheel.setVisibility(View.INVISIBLE);
        }


        @OnClick(R.id.playLucky)
        void startWheel() {
            playLucky.setEnabled(false);
            mStreamPresenter.onLuckyWheelStarted(mLuckyWheelResultIndex);
            luckyWheel.startLuckyWheelWithTargetIndex(mLuckyWheelResultIndex);
        }

        void setLuckyWheelResultIndex(int index) {
            mLuckyWheelResultIndex = index;
            if (mAwardReady) playLucky.setEnabled(true);
        }

        private int getRandomRound() {
            return 5;
        }


        private LuckyItem transferResponseToLuckyItem(LuckyWheelAwards awards) {
            LuckyItem luckyItem = new LuckyItem();

            Timber.e("awards.colorCode -> %s", awards.colorCode);
            luckyItem.text = String.format("%s,%s - %s", awards.awardTypeName.toUpperCase(), awards.creditAmount, awards.getCreditType().toUpperCase());
            luckyItem.iconUrl = awards.image;
            luckyItem.color = Color.parseColor(awards.colorCode);
            luckyItem.order = awards.orderIndex;
            return luckyItem;
        }

        private void enableLuckyWheel(List<LuckyItem> luckyItems) {
            luckyWheel.setData(luckyItems);
//            luckyWheel.setDummyData(luckyItems);
            luckyWheel.setRound(getRandomRound());
            luckyWheel.setLuckyRoundItemSelectedListener(index -> {
                Toast.makeText(getApplicationContext(), String.valueOf(luckyItems.get(index).text), Toast.LENGTH_SHORT).show();
                mCompositeSubscription.add(Observable.just(2).delay(2, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            mStreamPresenter.sendLuckyReceivedUser();
                            stateProgressbar.animateLevelUnlocked(mCurrentVotingBarLevel, () -> {
                                if (mVotingbarProgressObservable != null)
                                    mVotingbarProgressObservable.onNext(mStarsReceivedDuringAnimation.get());
                            });
                            luckywheelVisibilityListener.onNext(false);
                        }, StreamingActivityGLPlus.this::handleRxError));
            });
            luckyWheel.setVisibility(View.VISIBLE);
            mAwardReady = true;
            if (mLuckyWheelResultIndex != UNKNOW_RESULT) playLucky.setEnabled(true);
        }

        void setAwardData(ArrayList<LuckyWheelAwards> luckyWheelAwards) {
            mCompositeSubscription.add(Observable.from(luckyWheelAwards)
                    .subscribeOn(Schedulers.newThread())
                    .toSortedList((luckyWheelAwards1, luckyWheelAwards2) -> Integer.valueOf(luckyWheelAwards1.orderIndex).compareTo(luckyWheelAwards2.orderIndex))
                    .flatMap(Observable::from)
                    .flatMap(sortedLuckyAwardItem -> Observable.fromCallable(() -> transferResponseToLuckyItem(sortedLuckyAwardItem)))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(luckyItem -> data.add(luckyItem)
                            , StreamingActivityGLPlus.this::handleRxError,
                            () -> enableLuckyWheel(data)));

        }

        void clearData() {
            luckyWheel.clearData();
            data.clear();

        }

    }
    //endregion


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MaintenanceModel model) {
        if (model != null) {
            switch (model.maintenanceMode) {

                case Constants.MAINTENANCE_MODE_STOP:
                    tvMaintenanceMessage.setVisibility(View.GONE);
                    break;
                case Constants.MAINTENANCE_MODE_STANDBY:
                    tvMaintenanceMessage.setText(model.message);
                    tvMaintenanceMessage.setVisibility(View.VISIBLE);


                    break;

                case Constants.MAINTENANCE_MODE_START:

                    if (mStreamer != null) {
                        stopStreamByMaintenance();
                    }
                    if (mMainHandler != null) {
                        mMainHandler.removeCallbacksAndMessages(null);
                        mMainHandler = null;
                    }
                    MaintenanceActivity.startMaintenanceActivity(this, model);
                    break;
                default:
                    break;
            }
        }
    }

    void stopStreamByMaintenance() {
        if (!mIsEndStream) {
            mStreaming = false;
            mIsEndStream = true;
            isStreamReady = false;
            if (mStreamPresenter != null) mStreamPresenter.cancelPreviousStream();
            mStreamer.stopCameraPreview();
            mStreamer.stopStream();
            mStreamer.release();
            mTimerView.stopTimer();
        }
    }

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
                    //只有在小屏区域才触发位置改变
                    if (isSubScreenArea(event.getX(), event.getY(), left, right, top, bottom)) {
                        //获取相对sub区域的坐标，即以sub左上角为原点
                        mSubTouchStartX = event.getX() - left;
                        mSubTouchStartY = event.getY() - top;
                        mLastX = event.getX();
                        mLastY = event.getY();
                    }
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
                    //未移动并且在小窗区域，则触发大小窗切换
                    if (!mIsSubMoved && isSubScreenArea(event.getX(), event.getY(), left, right,
                            top, bottom)) {
                        if (mSubPlayerLayout != null) mSubPlayerLayout.onPlayerClicked();
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
    private boolean isSubScreenArea(float x, float y, int left, int right, int top, int bottom) {
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

    /**
     * Handle request permissions.
     * There are some cases we need to handle.<br>
     * 1. All permission are granted -P <br>
     * 2. All permission are denied -P<br>
     * 3. All permission are denied & never ask again -P<br>
     * 3. Only required permissions are granted<br>
     * 4. Required permissions are denied<br>
     * 5. Required permissions are denied and never ask again<br>
     */
    private void requestPermissions() {
        if (mHandledPermissionCheck) {
            return;
        }
        // We have 2 required permissions (camera & audio)
        // If we can not request these permissions (user selected never ask again)
        // We show a dialog and guide user to manually grant these permissions
        if (checkRequiredPermissions()) {
            // At this time, there will be a dialog that guides user to enable permission.
            // Consider this as the permission is handled
            mHandledPermissionCheck = true;
            return;
        }

        String[] allPermissions = mRequestedPermissions.toArray(new String[0]);
        // filter out all of the granted or not requestable
        List<String> permissions = Utils.keepNotRequestedPermissions(this, mFirstCheckPermission, allPermissions);
        if (permissions.isEmpty()) {
            handlePermissionResult(Utils.fillPermissionResult(this, allPermissions, false, false));
            return;
        }

        // request all of the permission
        executeRequestPermission(permissions.toArray(new String[0]));
    }

    private void executeRequestPermission(String[] permissions) {
        mCompositeSubscription.add(mRxPermissions.request(permissions)
                .map(granted -> Utils.fillPermissionResult(this, permissions, false, false))
                .subscribe(result -> {
                    // We do not want to request these permissions again
                    mRequestedPermissions.remove(Manifest.permission.ACCESS_FINE_LOCATION);
                    mRequestedPermissions.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    mFirstCheckPermission = false;
                    handlePermissionResult(result);
                }));
    }

    private boolean checkRequiredPermissions() {
        if (Utils.hasDoNotAskAgainPermissions(this, mFirstCheckPermission, REQUIRED_PERMISSIONS)) {
            String perms = getString(R.string.permission_camera) + " & " + getString(R.string.permission_record_audio);
            // Finish this activity
            showPermissionGuideDialog(perms, 2, true);
            return true;
        }
        return false;
    }

    /**
     * @param result the permission result. This might be null if the activity needs to perform permission checking on resume
     */
    private void handlePermissionResult(@NonNull Map<String, Boolean> result) {
        if (checkRequiredPermissions()) {
            mHandledPermissionCheck = true;
            return;
        }
        if (mPermissionGuideDialog != null) {
            mPermissionGuideDialog.dismissDialog();
        }
        // Handle location permission
        Boolean locPermission = result.get(android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (locPermission != null) {
            mStreamPresenter.onLocationPermissionChanged(locPermission);
        }
        // Handle storage permission.
        // We do not handle this permission at this time. We just pre-request for later use

        // Handle camera & audio permission
        if (Utils.hasAllPermissionsGranted(this, REQUIRED_PERMISSIONS)) {
            if (mStreamer != null) {
                mStreamer.startCameraPreview();
            }
        } else {
            retryRequestPermissions();
        }
        mHandledPermissionCheck = true;
    }

    private void retryRequestPermissions() {
        requestPermissions();
    }

    /**
     * Actually, we can not open the manage app permission page.
     * The best way we could do is to open the app default setting page.
     * The android.intent.action.MANAGE_APP_PERMISSIONS is existed but it does not public to 3rd apps.
     */
    private void openPermissionSetting() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 0);
        }
    }

    /**
     * Show a dialog that guide user to open the permission page.
     *
     * @param displayPerms the display permissions which user needs to enable
     * @param quantity     the number of permissions, used to format text
     */
    private void showPermissionGuideDialog(String displayPerms, int quantity, boolean finishOnCancel) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (mPermissionGuideDialog != null && mPermissionGuideDialog.isShowing()) {
            mPermissionGuideDialog.dismissDialog();
        }
        mPermissionGuideDialog = new DialogInfoUtility();
        mPermissionGuideDialog.showConfirmMessage(this, "",
                getResources().getQuantityString(R.plurals.open_permission_setting_explanation, quantity, displayPerms),
                getString(R.string.btn_text_ok),
                getString(R.string.btn_text_cancel), this::openPermissionSetting, () -> {
                    if (finishOnCancel) {
                        finish();
                    }
                });

    }

}