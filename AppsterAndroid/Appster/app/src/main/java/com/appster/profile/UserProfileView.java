package com.appster.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.ActivityFollow;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.activity.TopFanActivity;
import com.appster.activity.ViewImageActivity;
import com.appster.adapters.AdapterWallFeed;
import com.appster.customview.CircleImageView;
import com.appster.customview.GenderedCircleImageView;
import com.appster.customview.autolinktextview.AutoLinkMode;
import com.appster.customview.autolinktextview.AutoLinkTextView;
import com.appster.dialog.DialogReport;
import com.appster.features.messages.MessageListActivity;
import com.appster.features.messages.chat.ChatActivity;
import com.appster.giftreceive.GiftStoreActivity;
import com.appster.interfaces.FollowUserListener;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.manager.ShowErrorManager;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.FollowUser;
import com.appster.models.ListenerEventModel;
import com.appster.models.StreamModel;
import com.appster.models.TopFanModel;
import com.appster.models.UserModel;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DialogUtil;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.BlockUserRequestModel;
import com.appster.webservice.request_models.GetTopFanModel;
import com.appster.webservice.request_models.ReportUserRequestModel;
import com.apster.common.Constants;
import com.apster.common.CopyTextUtils;
import com.apster.common.CustomDialogUtils;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.LogUtils;
import com.apster.common.PixelUtil;
import com.apster.common.Utils;
import com.pack.utility.CheckNetwork;
import com.pack.utility.DialogInfoUtility;
import com.pack.utility.StringUtil;

import java.util.List;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.appster.utility.CustomTabUtils.openChromeTab;

/**
 * Created by sonnguyen on 9/25/15.
 */
public class UserProfileView extends LinearLayout implements CustomDialogUtils.ReportAndBlockCallback {
    Activity activity;
    private LayoutInflater inflater;
    TextView textView_follwoers;
    TextView txtFollowerTitle;
    TextView textView_gift_count;
    TextView textView_following;
    TextView txtFollowingTitle;
    Button btn_follow;
    ImageButton btnTabGift;
    ImageButton btnTabList;
    ImageButton btnTabGrid;
    ImageButton btnChat;
    private ImageView imgNotificationAvatar;
    private ImageView imgDimImage;
    private TextView txtNotificationUserName;
    private TextView txtNotificationStreamTitle;

    RelativeLayout rlTopFan;
    CircleImageView imgTopFan1;
    CircleImageView imgTopFan2;
    CircleImageView imgTopFan3;
    ImageView imgTopFanArrow;

    private TextView notificationMessage;
    private AutoLinkTextView txtAbout;
    private TextView txtUserName;
    private TextView txtDisplayName;
    private RelativeLayout rlLiveNotification;


    private UserModel mUserProfileDetails;
    private StreamModel streamDetail;
    private GenderedCircleImageView profile_image;
    boolean isViewMe = false;
    OnUserProfileChangeView onTabChange;
    private DialogInfoUtility utility;

    // variable to track event time
    int currentIndicatorSelected = FragmentMe.TAB_GRID_INDEX;

    AdapterWallFeed.BlockCallback blockCallback;

    FollowUserListener followUserListener;
    boolean hasShowCopyPopup = false;
    private boolean mIsStreaming;

    CompositeSubscription mCompositeSubscription;

//    private PointF pointDown;
//    private PointF pointMoving;

    public UserProfileView(Activity activity, boolean isAppOwner) {
        super(activity);
        this.activity = activity;
        inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.user_profile_header, this, true);
        profile_image = findViewById(R.id._profile_image);
        imgDimImage = (ImageView) findViewById(R.id.img_dim_image);
        textView_follwoers = (TextView) findViewById(R.id.followersCountText);
        txtFollowerTitle = (TextView) findViewById(R.id.txt_follower_title);
        textView_gift_count = (TextView) findViewById(R.id.giftCountText);
        textView_following = (TextView) findViewById(R.id.followingCountText);
        txtFollowingTitle = (TextView) findViewById(R.id.txt_following_title);

        btn_follow = (Button) findViewById(R.id.btn_follow);
        btnTabGift = (ImageButton) findViewById(R.id.btnTabGift);
        btnTabGrid = (ImageButton) findViewById(R.id.btnTabGrid);
        btnTabList = (ImageButton) findViewById(R.id.btnTabList);
        btnChat = (ImageButton) findViewById(R.id.btnChat);

        rlTopFan = (RelativeLayout) findViewById(R.id.rl_top_fans);
        imgTopFan1 = (CircleImageView) findViewById(R.id.img_top_fan1);
        imgTopFan2 = (CircleImageView) findViewById(R.id.img_top_fan2);
        imgTopFan3 = (CircleImageView) findViewById(R.id.img_top_fan3);
        imgTopFanArrow = (ImageView) findViewById(R.id.img_top_fan_arrow);

        notificationMessage = (TextView) findViewById(R.id.notificationMessage);
        txtAbout = (AutoLinkTextView) findViewById(R.id.txt_about);
        txtUserName = (TextView) findViewById(R.id.txt_username);
        txtDisplayName = (TextView) findViewById(R.id.txt_display_name);
        txtAbout.addAutoLinkMode(AutoLinkMode.MODE_URL);
//        userWatcherListView = (RecyclerView) findViewById(R.id.hlvCustomList);

        // util TopPan
//        initListUserWatcher();
        utility = new DialogInfoUtility();

        if (isAppOwner) {
            btnTabGift.setVisibility(GONE);
        } else {
            btnTabGift.setVisibility(VISIBLE);
        }

//        pointDown = new PointF();
//        pointMoving = new PointF();
//        this.setOnTouchListener(this);
    }

    public UserProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UserProfileView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    public void setFollowUserListener(FollowUserListener followUserListener) {
        this.followUserListener = followUserListener;
    }

    public void setBlockCallback(AdapterWallFeed.BlockCallback blockCallback) {
        this.blockCallback = blockCallback;
    }

    @Override
    public void onReportClick() {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ((BaseToolBarActivity) activity).goingLoginScreen();
            return;
        }

        DialogReport dialogReport = DialogReport.newInstance();
        dialogReport.setChooseReportListenner(this::onReportUser);
        dialogReport.show(((BaseActivity) activity).getSupportFragmentManager(), "Report");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Timber.d("dispatchTouchEvent " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Timber.d("onInterceptTouchEvent "+ ev.getAction());
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            pointDown.x = ev.getX();
//            pointDown.y = ev.getY();
//        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            pointMoving.x = ev.getX();
//            pointMoving.y = ev.getY();
//            float deltaX = pointDown.x - pointMoving.x;
//            float deltaY = pointDown.y - pointMoving.y;
//            int direction = TouchEventUtil.determineDirection(deltaX, deltaY);
//            if (TouchEventUtil.calculateMovementDistance(pointDown, pointMoving) > 0
//                    && (((direction & TouchEventUtil.MOVE_DOWN) == TouchEventUtil.MOVE_DOWN)
//                    || ((direction & TouchEventUtil.MOVE_LEFT) == TouchEventUtil.MOVE_LEFT)
//                    || ((direction & TouchEventUtil.MOVE_RIGHT) == TouchEventUtil.MOVE_RIGHT))){
//                Timber.d("direction "+ String.valueOf(direction));
//                getParent().requestDisallowInterceptTouchEvent(true);
//                return true;
//            }
//        }
//        return false;
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        Timber.d("onTouchEvent "+ ev.getAction());
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            pointDown.x = ev.getX();
//            pointDown.y = ev.getY();
//        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            pointMoving.x = ev.getX();
//            pointMoving.y = ev.getY();
//            float deltaX = pointDown.x - pointMoving.x;
//            float deltaY = pointDown.y - pointMoving.y;
//            int direction = TouchEventUtil.determineDirection(deltaX, deltaY);
//            if (TouchEventUtil.calculateMovementDistance(pointDown, pointMoving) > 0
//                    && (((direction & TouchEventUtil.MOVE_DOWN) == TouchEventUtil.MOVE_DOWN)
//                        || ((direction & TouchEventUtil.MOVE_LEFT) == TouchEventUtil.MOVE_LEFT)
//                        || ((direction & TouchEventUtil.MOVE_RIGHT) == TouchEventUtil.MOVE_RIGHT))){
//                Timber.d("direction "+ String.valueOf(direction));
//                getParent().requestDisallowInterceptTouchEvent(true);
//            }
//        }
//        return true;
//    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        return false;
//    }

    @Override
    public void onBlockClick() {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ((BaseToolBarActivity) activity).goingLoginScreen();
            return;
        }
        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title(activity.getString(R.string.block_this_user))
                .message(activity.getString(R.string.block_confirmation_content))
                .confirmText(activity.getString(R.string.string_block))
                .onConfirmClicked(this::onBlockUser)
                .build().show(activity);

    }

    @Override
    public void onBanClick() {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ((BaseToolBarActivity) activity).goingLoginScreen();
            return;
        }

        DialogUtil.showConfirmDialog(activity,
                activity.getString(R.string.ban_user_title),
                activity.getString(R.string.ban_user_message),
                activity.getString(R.string.string_ban),
                () -> {
                    // ban
                    mCompositeSubscription.add(AppsterWebServices.get().banUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), mUserProfileDetails.getUserId())
                            .subscribe(response -> {
                                if (response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                    Toast.makeText(activity, R.string.ban_user_successed, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, R.string.ban_user_failed, Toast.LENGTH_SHORT).show();
                                }
                            }, error -> {
                                ((BaseActivity) activity).handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                            }));
                }
        );
    }

    public void setRedNotification(RelativeLayout layout) {
        rlLiveNotification = layout;
        imgNotificationAvatar = (ImageView) layout.findViewById(R.id.img_notification_avatar);
        txtNotificationUserName = (TextView) layout.findViewById(R.id.txt_notification_user);
        txtNotificationStreamTitle = (TextView) layout.findViewById(R.id.txt_notification_stream_title);
    }

    public void setOnTabChange(OnUserProfileChangeView onTabChange) {
        this.onTabChange = onTabChange;
    }

    public void setStreamDetail(StreamModel streamDetail) {
        this.streamDetail = streamDetail;
    }

    public void setUserProfileDetails(final UserModel userProfileDetails, OnClickListener mBackClick, String currentUserID) {
        this.mUserProfileDetails = userProfileDetails;
        if (mUserProfileDetails.getUserId().equals(currentUserID)) {
            isViewMe = true;
        }

        if (!StringUtil.isNullOrEmptyString(mUserProfileDetails.getUserImage())) {
            ImageLoaderUtil.displayUserImage(activity, mUserProfileDetails.getUserImage(), profile_image);
            ImageLoaderUtil.displayUserImage(activity, mUserProfileDetails.getUserImage(), imgDimImage);
        } else {
            profile_image.setImageResource(R.drawable.user_image_default);
            imgDimImage.setImageResource(R.drawable.img_noavatar_nearby);
        }
        profile_image.setGender(userProfileDetails.getGender());

        String username = "@" + mUserProfileDetails.getUserName();
        txtUserName.setText(username);
        txtDisplayName.setText(mUserProfileDetails.getDisplayName());

        String about = StringUtil.decodeString(mUserProfileDetails.getAbout());
        txtAbout.setAutoLinkText(about);
        txtAbout.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (!hasShowCopyPopup) {
                Timber.e("%s - %s", autoLinkMode, matchedText);
                openChromeTab(activity, matchedText);
            }
        });
//        txtAbout.setText(HyperLinkUtils.autoHyperLink(about));
//        txtAbout.setMovementMethod(LinkMovementMethod.getInstance());
//        HyperLinkUtils.stripUnderlinesOfURL(activity, txtAbout, url -> {
//            if (!hasShowCopyPopup) {
//                HyperLinkUtils.openBrowser(activity, url);
//            }
//        });
        txtAbout.setOnLongClickListener(v -> {
            hasShowCopyPopup = true;
            CopyTextUtils.showOptionCopyText(activity, v, about, menu -> {
                hasShowCopyPopup = false;
            });
            return true;
        });

        setCount(mUserProfileDetails);
        btnTabGift.setOnClickListener(v -> {
            if (FragmentMe.TAB_GIFT_INDEX != currentIndicatorSelected) {
                changeTabIcon(2);
                onTabChange.onChangeToGift();
            }
        });
        // View Gift
        btnTabList.setOnClickListener(v -> {
            if (FragmentMe.TAB_LIST_INDEX != currentIndicatorSelected) {
                changeTabIcon(1);
                onTabChange.onChangeToListView();
            }
        });
        btnTabGrid.setOnClickListener(v -> {
            if (FragmentMe.TAB_GRID_INDEX != currentIndicatorSelected) {
                changeTabIcon(0);
                onTabChange.onChangeToGridView();
            }
        });

        if (isViewMe) {
            btn_follow.setVisibility(View.GONE);
        } else {
            ((BaseToolBarActivity) activity).goneNotify(false);
            ((BaseToolBarActivity) activity).setImageEditProfile(v -> CustomDialogUtils.showUserOptionPopUp(activity, UserProfileView.this));

            btn_follow.setVisibility(View.VISIBLE);
            boolean isFollow = mUserProfileDetails.getIsFollow() == Constants.IS_FOLLOWING_USER;
            setFollowUser(isFollow);
        }
        // View Chat Screen
        btnChat.setOnClickListener(v -> {

            if (!AppsterApplication.mAppPreferences.isUserLogin()) {
                ((BaseToolBarActivity) activity).goingLoginScreen();

                return;
            }

            if (isViewMe) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.push_in_to_right, R.anim.push_in_to_left);
                Intent intent = new Intent(getContext(), MessageListActivity.class);
                activity.startActivityForResult(intent, Constants.REQUEST_MESSAGE_LIST_ACTIVITY, options.toBundle());

                return;
            }

            if (CheckNetwork.isNetworkAvailable(getContext())) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.push_in_to_right, R.anim.push_in_to_left);

                Intent intent = ChatActivity.Companion.createIntent(getContext(), mUserProfileDetails.getUserId(), mUserProfileDetails.getUserName(), mUserProfileDetails.getDisplayName(), mUserProfileDetails.getUserImage(), mUserProfileDetails.getMessaging());
                activity.startActivityForResult(intent, Constants.CONVERSATION_REQUEST, options.toBundle());
            } else {
                utility.showMessage(getContext().getString(R.string.app_name), getContext().getResources().getString(
                        R.string.no_internet_connection), getContext());
            }
        });

        // View Follower
        textView_follwoers.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.push_in_to_right, R.anim.push_in_to_left);
            Intent intent = new Intent(activity, ActivityFollow.class);
            intent.putExtra(ConstantBundleKey.BUNDLE_TYPE_KEY, ActivityFollow.TypeList.FOLLOWER.ordinal());
            intent.putExtra(ConstantBundleKey.BUNDLE_PROFILE_ID_KEY, mUserProfileDetails.getUserId());
            activity.startActivityForResult(intent, Constants.REQUEST_FOLLOW, options.toBundle());
        });
        txtFollowerTitle.setOnClickListener(v -> textView_follwoers.performClick());

        textView_following.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.push_in_to_right, R.anim.push_in_to_left);
            Intent intent = new Intent(activity, ActivityFollow.class);
            intent.putExtra(ConstantBundleKey.BUNDLE_TYPE_KEY, ActivityFollow.TypeList.FOLLOWING.ordinal());
            intent.putExtra(ConstantBundleKey.BUNDLE_PROFILE_ID_KEY, mUserProfileDetails.getUserId());
            activity.startActivityForResult(intent, Constants.REQUEST_FOLLOW, options.toBundle());
        });
        txtFollowingTitle.setOnClickListener(v -> textView_following.performClick());

        rlTopFan.setOnClickListener(v -> viewTopFan());


        btn_follow.setOnClickListener(v -> {
            btn_follow.setClickable(false);
            // Set follow
            if (!AppsterApplication.mAppPreferences.isUserLogin()) {
                ((BaseToolBarActivity) activity).goingLoginScreen();
                btn_follow.setClickable(true);
                return;
            }

            if (!CheckNetwork.isNetworkAvailable(activity)) {
                ((BaseActivity) activity).utility.showMessage("", activity.getString(R.string.no_internet_connection), activity);
                btn_follow.setClickable(true);
                return;
            }

            onFollowButtonClicked();
            btn_follow.setClickable(true);
        });

        profile_image.setOnClickListener(v -> showFullImage(mUserProfileDetails.getUserImage()));

        // get topPan
        getTopFan();
    }

    public void setCount(UserModel userProfileDetails) {
        textView_gift_count.setText(Utils.formatThousand(userProfileDetails.getTotalGoldFans()));
        textView_follwoers.setText(Utils.formatThousand(userProfileDetails.getFollowerCount()));
//        if (userProfileDetails.getFollowerCount() < 1000) {
//            textView_follwoers.setText(userProfileDetails.getFollowerCount() + "");
//        } else {
//            textView_follwoers.setText(Utils.roundOnStreamRecord(userProfileDetails.getFollowerCount()) + activity.getString(R.string.stream_end_stream_thousand));
//        }

        textView_following.setText(Utils.formatThousand(userProfileDetails.getFollowingCount()));
//        if (userProfileDetails.getFollowingCount() < 1000) {
//            textView_following.setText(userProfileDetails.getFollowingCount() + "");
//        } else {
//            textView_following.setText(Utils.roundOnStreamRecord(userProfileDetails.getFollowingCount()) + activity.getString(R.string.stream_end_stream_thousand));
//        }
    }

    private void getTopFan() {

        GetTopFanModel request = new GetTopFanModel();
        request.setUserId(mUserProfileDetails.getUserId());
        request.setLimit(3);

        mCompositeSubscription.add(AppsterWebServices.get().getTopFan("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(getTopFanDataResponse -> {
                    if (getTopFanDataResponse == null) return;

                    if (getTopFanDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        onTopFanDataResponse(getTopFanDataResponse.getData().getResult());
                    }

                }, Timber::e));
    }

    void onTopFanDataResponse(List<TopFanModel> topFans) {
        if (topFans != null) {
            if (topFans.size() == 1) {
                String url1 = topFans.get(0).getUserImage();
                ImageLoaderUtil.displayUserImage(activity, url1, imgTopFan1);
                imgTopFanArrow.setVisibility(VISIBLE);
                imgTopFan1.setVisibility(VISIBLE);
                imgTopFan2.setVisibility(GONE);
                imgTopFan3.setVisibility(GONE);
            } else if (topFans.size() == 2) {
                String url1 = topFans.get(0).getUserImage();
                String url2 = topFans.get(1).getUserImage();
                ImageLoaderUtil.displayUserImage(activity, url1, imgTopFan1);
                ImageLoaderUtil.displayUserImage(activity, url2, imgTopFan2);
                imgTopFanArrow.setVisibility(VISIBLE);
                imgTopFan1.setVisibility(VISIBLE);
                imgTopFan2.setVisibility(VISIBLE);
                imgTopFan3.setVisibility(GONE);
            } else if (topFans.size() >= 3) {
                String url1 = topFans.get(0).getUserImage();
                String url2 = topFans.get(1).getUserImage();
                String url3 = topFans.get(2).getUserImage();
                ImageLoaderUtil.displayUserImage(activity, url1, imgTopFan1);
                ImageLoaderUtil.displayUserImage(activity, url2, imgTopFan2);
                ImageLoaderUtil.displayUserImage(activity, url3, imgTopFan3);
                imgTopFanArrow.setVisibility(VISIBLE);
                imgTopFan1.setVisibility(VISIBLE);
                imgTopFan2.setVisibility(VISIBLE);
                imgTopFan3.setVisibility(VISIBLE);
            }
        }

    }

    public boolean isStreaming() {
        return mIsStreaming;
    }

    public void setIsStreaming(boolean isStreaming, final String currentStream) {
        mIsStreaming = isStreaming;
        if (AppsterApplication.mAppPreferences.isUserLogin() && AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(mUserProfileDetails.getUserId())) {
            rlLiveNotification.setVisibility(GONE);
            this.setPadding(0, 0, 0, 0);
        } else if (isStreaming) {
            rlLiveNotification.setVisibility(VISIBLE);
            int marginTop = PixelUtil.dpToPx(getContext(), 54);
            this.setPadding(0, marginTop, 0, 0);
            ImageLoaderUtil.displayUserImage(activity, mUserProfileDetails.getUserImage(), imgNotificationAvatar);

            String userIsLive = mUserProfileDetails.getDisplayName() + " " + getResources().getString(R.string.message_is_lived_now);
            String streamTitle = "";
            if (streamDetail != null) {
                streamTitle = StringUtil.decodeString(streamDetail.getTitle());
            }
            txtNotificationUserName.setText(userIsLive);
            txtNotificationStreamTitle.setText(streamTitle);
            if (TextUtils.isEmpty(streamTitle)) {
                txtNotificationStreamTitle.setVisibility(GONE);
            } else {
                txtNotificationStreamTitle.setVisibility(VISIBLE);
            }
        } else {
            rlLiveNotification.setVisibility(GONE);
            this.setPadding(0, 0, 0, 0);
        }

        rlLiveNotification.setOnClickListener(v -> ((BaseToolBarActivity) activity).openViewLiveStream("", currentStream, mUserProfileDetails.getUserImage(), false));
    }

    public void showFullImage(String url) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = new Intent(activity, ViewImageActivity.class);
        intent.putExtra(ViewImageActivity.key_image_link, url);
        activity.startActivity(intent, options.toBundle());
    }

    public void handleNotificationMessage(boolean isShowNotifi, int numberUnread) {
        if (isShowNotifi) {
            notificationMessage.setVisibility(View.VISIBLE);
        } else {
            notificationMessage.setVisibility(View.GONE);
        }
    }

    void setFollowUser(boolean isFollow) {
        if (isFollow) {
            btn_follow.setBackgroundResource(R.drawable.ic_profile_following);
        } else {
            btn_follow.setBackgroundResource(R.drawable.ic_profile_follow);
        }
    }

    private void onFollowButtonClicked() {
        if (mUserProfileDetails.getIsFollow() == Constants.UN_FOLLOW_USER) {
            followUser();

        } else {
            DialogUtil.showConfirmUnFollowUser(activity, mUserProfileDetails.getDisplayName(), this::unFollowUser);
        }
    }

    private void followUser() {
        FollowUser followUser = new FollowUser(getContext(), mUserProfileDetails.getUserId(), true);

        followUser.execute();

        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {

                btn_follow.setClickable(true);
                updateFollowButton(Constants.IS_FOLLOWING_USER);

                if (followUserListener != null) {
                    followUserListener.onFollowUser(Constants.IS_FOLLOWING_USER);

                }

                setFollowUser(isFollow);
                updateFollowerCount(Constants.IS_FOLLOWING_USER);
                callEventChange(isFollow);
            }

            @Override
            public void onError(int errorCode, String message) {
                handleErrorCode(errorCode, message);
                btn_follow.setClickable(true);
            }
        });
    }
    void followUserWithPassword(String pass){
        FollowUser followUser = new FollowUser(getContext(), mUserProfileDetails.getUserId(), true);

        followUser.executeFollowWithPass(pass);

        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {

                btn_follow.setClickable(true);
                updateFollowButton(Constants.IS_FOLLOWING_USER);

                if (followUserListener != null) {
                    followUserListener.onFollowUser(Constants.IS_FOLLOWING_USER);

                }

                setFollowUser(isFollow);
                updateFollowerCount(Constants.IS_FOLLOWING_USER);
                callEventChange(isFollow);
            }

            @Override
            public void onError(int errorCode, String message) {
                handleErrorCode(errorCode, message);
                btn_follow.setClickable(true);
            }
        });
    }

    public void handleErrorCode(int code, String message) {
        if (activity != null) {
            switch (code) {
                case 603:
                    new DialogbeLiveConfirmation.Builder()
                            .title(activity.getString(R.string.app_name))
                            .singleAction(true)
                            .message(message)
                            .onConfirmClicked(() -> AppsterApplication.logout(activity))
                            .build().show(activity);
                    break;
                case ShowErrorManager.pass_word_required:
                    new DialogbeLiveConfirmation.Builder()
                            .title(activity.getString(R.string.enter_password))
                            .setPasswordBox(true)
                            .confirmText(activity.getString(R.string.verify))
                            .onEditTextValue(this::followUserWithPassword)
                            .build().show(activity);
                    break;
                default:
                    ((BaseToolBarActivity) activity).handleError(message,code);
                    break;
            }
        }
    }

    void unFollowUser() {
        FollowUser followUser = new FollowUser(getContext(), mUserProfileDetails.getUserId(), false);

        followUser.execute();

        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {

                btn_follow.setClickable(true);
                updateFollowButton(Constants.UN_FOLLOW_USER);

                if (followUserListener != null) {
                    followUserListener.onFollowUser(Constants.UN_FOLLOW_USER);
                }
                setFollowUser(isFollow);
                updateFollowerCount(Constants.UN_FOLLOW_USER);
                callEventChange(isFollow);
            }

            @Override
            public void onError(int errorCode, String message) {
                handleErrorCode(errorCode, message);
                btn_follow.setClickable(true);
            }

        });
    }

    private void onReportUser(String reason) {
        ReportUserRequestModel request = new ReportUserRequestModel();
        request.setReportedUserId(mUserProfileDetails.getUserId());
        request.setReason(reason);

        mCompositeSubscription.add(AppsterWebServices.get().reportUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(blockOrReportUserDataResponse -> {

                }, error -> {
                    ((BaseActivity) activity).handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    private void onBlockUser() {
        BlockUserRequestModel request = new BlockUserRequestModel();
        request.setBlockUserId(mUserProfileDetails.getUserId());

        mCompositeSubscription.add(AppsterWebServices.get().blockUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(reportUserResponseModel -> {
                    if (reportUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        if (blockCallback != null) {
                            blockCallback.OnBlockSuccessfully();
                        }
                    } else {
                        ((BaseActivity) activity).handleError(reportUserResponseModel.getMessage(), Constants.RETROFIT_ERROR);
                        LogUtils.logE("onblockuser", reportUserResponseModel.getMessage());
                    }
                }, error -> ((BaseActivity) activity).handleError(error.getMessage(), Constants.RETROFIT_ERROR)));
    }

    private void viewGiftStore() {

        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            if (mUserProfileDetails.getUserId().equals(AppsterApplication.mAppPreferences.getUserModel().getUserId())) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.push_in_to_right, R.anim.push_in_to_left);
                Intent intent = new Intent(activity, GiftStoreActivity.class);
                activity.startActivity(intent, options.toBundle());

            }
        }
    }

    private void viewTopFan() {

        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = new Intent(activity, TopFanActivity.class);
        intent.putExtra(ConstantBundleKey.BUNDLE_PROFILE, mUserProfileDetails);
        activity.startActivityForResult(intent, Constants.REQUEST_TOPFAN_ACTIVITY, options.toBundle());
    }

    public void updateFollowCount() {

        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            if (mUserProfileDetails != null) {
                if (mUserProfileDetails.getUserId().equals(AppsterApplication.mAppPreferences.getUserModel().getUserId())) {
                    textView_following.setText(Utils.formatThousand(AppsterApplication.mAppPreferences.getUserModel().getFollowingCount()));
                }
            }
        }

    }

    public void updateFollowerCount(int type) {

        int followerCount = mUserProfileDetails.getFollowerCount();

        if (type == Constants.IS_FOLLOWING_USER) {

            followerCount = followerCount + 1;

        } else if (type == Constants.UN_FOLLOW_USER) {

            followerCount = followerCount - 1;

        }

        mUserProfileDetails.setFollowerCount(followerCount);
        textView_follwoers.setText(Utils.formatThousand(followerCount));

    }

    public void updateFollowButton(int type) {

        setFollowUser(type == Constants.IS_FOLLOWING_USER);

    }

    public void updateUserProfileDetails(UserModel userModel) {
        this.mUserProfileDetails = userModel;
    }

    public void changeTabIcon(int newPosition) {
        if (newPosition == currentIndicatorSelected) {
            return;
        }

        switch (currentIndicatorSelected) {
            case FragmentMe.TAB_LIST_INDEX:
                btnTabList.setImageResource(R.drawable.ic_profile_bar_list);
                break;

            case FragmentMe.TAB_GRID_INDEX:
                btnTabGrid.setImageResource(R.drawable.ic_profile_bar_grid);
                break;

            case FragmentMe.TAB_GIFT_INDEX:
                btnTabGift.setImageResource(R.drawable.ic_profile_bar_gift);
                break;

        }

        switch (newPosition) {
            case FragmentMe.TAB_LIST_INDEX:
                btnTabList.setImageResource(R.drawable.ic_profile_bar_list_selected);
                break;

            case FragmentMe.TAB_GRID_INDEX:
                btnTabGrid.setImageResource(R.drawable.ic_profile_bar_grid_selected);
                break;

            case FragmentMe.TAB_GIFT_INDEX:
                btnTabGift.setImageResource(R.drawable.ic_profile_bar_gift_selected);
                break;
        }

//        if (translate == null) {//== setup object animator for tab indicator
//            translate = ObjectAnimator.ofFloat(tabListIndicator, "translationX", 0f, 0f);
//            translate.setDuration(200);
//            translate.setInterpolator(new AccelerateDecelerateInterpolator());
//        }
//        int translateFrom = currentIndicatorSelected * tabListIndicator.getWidth();
//        int translateTo = newPosition * tabListIndicator.getWidth();
//        translate.setFloatValues(translateFrom, translateTo);
//        translate.start();
        currentIndicatorSelected = newPosition;
    }

    void callEventChange(boolean isFollow) {

        ListenerEventModel listenerEventModel = new ListenerEventModel();
        listenerEventModel.setTypeEvent(ListenerEventModel.TypeEvent.FOLLOW_USER);

        FollowStatusChangedEvent followStatusChangedEvent = new FollowStatusChangedEvent();
        int followType = isFollow == true ? Constants.IS_FOLLOWING_USER : Constants.UN_FOLLOW_USER;
        followStatusChangedEvent.setFollowType(followType);
        followStatusChangedEvent.setUserId(mUserProfileDetails.getUserId());

        listenerEventModel.setFollowStatusChangedEvent(followStatusChangedEvent);

        ((BaseActivity) activity).eventChange(listenerEventModel);
    }
}
