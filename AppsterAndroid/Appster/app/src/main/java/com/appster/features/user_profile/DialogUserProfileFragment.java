package com.appster.features.user_profile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.UserProfileActivity;
import com.appster.customview.CustomFontButton;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.autolinktextview.AutoLinkMode;
import com.appster.customview.autolinktextview.AutoLinkTextView;
import com.appster.features.login.LoginActivity;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.manager.AppsterChatManger;
import com.appster.models.FollowUser;
import com.appster.models.StreamModel;
import com.appster.models.UserModel;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DialogUtil;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.RxUtils;
import com.appster.utility.glide.RoundedCornersTransformation;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.BeginStreamRequestModel;
import com.appster.webservice.request_models.UserProfileRequestModel;
import com.apster.common.Constants;
import com.apster.common.CopyTextUtils;
import com.apster.common.Utils;
import com.pack.utility.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.appster.utility.CustomTabUtils.openChromeTab;
import static com.apster.common.Constants.VIDEO_CALL_ENABLE;

public class DialogUserProfileFragment extends DialogFragment {

    public static final String USER_DIALOG_TAG = "UserProfileView";
    private static final String ARG_USER_ID = "ARG_USER_ID";

    @Bind(R.id.ivUserProfileImage)
    ImageView ivUserProfileImage;
    @Bind(R.id.tvBlockOrReport)
    CustomFontTextView tvBlockOrReport;
    @Bind(R.id.tvBan)
    CustomFontTextView tvBan;
    @Bind(R.id.tvHideUnhide)
    CustomFontTextView tvHideUnhide;
    @Bind(R.id.tvMute)
    CustomFontTextView tvMute;
    @Bind(R.id.tvCloseDialog)
    TextView tvCloseDialog;
    @Bind(R.id.tvNumFollower)
    CustomFontTextView tvNumFollower;
    @Bind(R.id.tvNumFollowing)
    CustomFontTextView tvNumFollowing;
    @Bind(R.id.tvStarReceived)
    CustomFontTextView tvStarReceived;
    @Bind(R.id.tvDisplayName)
    CustomFontTextView tvDisplayName;
    @Bind(R.id.tvUserName)
    CustomFontTextView tvUserName;
    @Bind(R.id.tvLocation)
    CustomFontTextView tvLocation;
    @Bind(R.id.tvStatus)
    AutoLinkTextView tvStatus;
    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;
    @Bind(R.id.btnFollow)
    CustomFontButton btnFollow;
    @Bind(R.id.btn_video_call)
    CustomFontButton btnVideoCall;
    @Bind(R.id.llActionContainer)
    LinearLayout llActionContainer;

    @Bind(R.id.llStatsContainer)
    LinearLayout llStatsContainer;
    @Bind(R.id.tvWatcher)
    CustomFontTextView tvWatcher;
    @Bind(R.id.tvStop)
    CustomFontTextView tvStop;

    private long mLastClickTime;
    private static final String IS_VIEWER = "is_viewer";
    private static final String STREAM_SLUG = "stream_slug";
    private static final String IS_TOPFAN = "is_topfan";
    private static final String IS_CALLING = "is_calling";
    private static final String IS_HOST_PROFILE = "is_host_profile";
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    private String mUserName;
    private String mProfileImageUrl;
    private UserModel mUserProfile;
    private int mUserId;
    private boolean mIsViewer;
    private String mSlug;
    private UserProfileActionListener mUserProfileActionListener;
    private boolean isTopfan;
    private boolean canGoProfileScreen = true;
    private boolean isFollowed = false;
    private boolean isMuted = false;
    private boolean isCalling = false;
    private boolean mIsHostProfile = false;

    private boolean mIsHide = false;
    private boolean mIsRecorded = true;
    private UserModel mLoginUser;
    boolean hasShowCopyPopup = false;
    private String mUserIdStreamOwner;

    public static DialogUserProfileFragment newInstance(int userId, boolean isViewer, boolean isTopfan) {
        DialogUserProfileFragment fragment = new DialogUserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_USER_ID, userId);
        bundle.putBoolean(IS_VIEWER, isViewer);
        bundle.putBoolean(IS_TOPFAN, isTopfan);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static DialogUserProfileFragment newInstance(String userName, String userImage, boolean isViewer, boolean isTopfan) {
        DialogUserProfileFragment fragment = new DialogUserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantBundleKey.BUNDLE_USERNAME, userName);
        bundle.putString(ConstantBundleKey.BUNDLE_IMAGE_USER_LINE_TRIM, userImage);
        bundle.putBoolean(IS_VIEWER, isViewer);
        bundle.putBoolean(IS_TOPFAN, isTopfan);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static DialogUserProfileFragment newInstance(String userName, String userImage, boolean isViewer, String slug, boolean isCalling, boolean isHostProfile) {
        DialogUserProfileFragment fragment = new DialogUserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantBundleKey.BUNDLE_USERNAME, userName);
        bundle.putString(ConstantBundleKey.BUNDLE_IMAGE_USER_LINE_TRIM, userImage);
        bundle.putBoolean(IS_VIEWER, isViewer);
        bundle.putString(STREAM_SLUG, slug);
        bundle.putBoolean(IS_CALLING, isCalling);
        bundle.putBoolean(IS_HOST_PROFILE, isHostProfile);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(Utils.dpToPx(280), ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_user_profile, null);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mUserName = bundle.getString(ConstantBundleKey.BUNDLE_USERNAME);
            mUserId = bundle.getInt(ARG_USER_ID);
            mProfileImageUrl = bundle.getString(ConstantBundleKey.BUNDLE_IMAGE_USER_LINE_TRIM);
            mIsViewer = bundle.getBoolean(IS_VIEWER, false);
            mSlug = bundle.getString(STREAM_SLUG, "");
            isTopfan = bundle.getBoolean(IS_TOPFAN, false);
            isCalling = bundle.getBoolean(IS_CALLING, false);
            mIsHostProfile = bundle.getBoolean(IS_HOST_PROFILE, false);
        }
        ButterKnife.bind(this, view);
//        if (AppsterApplication.mAppPreferences.isUserLogin()) {
        if (llStatsContainer != null) llStatsContainer.setVisibility(View.INVISIBLE);
        if (tvLocation != null) tvLocation.setVisibility(View.INVISIBLE);
//        ImageLoaderUtil.displayUserImage(getContext(), mProfileImageUrl, ivUserProfileImage, new RoundedCornersTransformation(Utils.dpToPx(8), 0));

//        int error = R.drawable.user_image_default;
//        Picasso picasso = Picasso.with(getActivity().getApplicationContext());
//        if (!StringUtil.isNullOrEmptyString(mProfileImageUrl) && Patterns.WEB_URL.matcher(mProfileImageUrl).matches()) {
//            RequestCreator creator = picasso
//                    .load(mProfileImageUrl)
//                    .config(Bitmap.Config.RGB_565)
//                    .fit()
//                    .centerCrop()
//                    .error(error)
//                    .transformToRequestModel();
//            creator.placeholder(error);
//            creator.into(ivUserProfileImage, null);
//        }
//        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Constants.GUEST_USER_NAME.equals(mUserName)) {
            UserModel guest = new UserModel();
            guest.setUserName(Constants.GUEST_USER_NAME);
            guest.setDisplayName(Constants.GUEST_DISPLAY_NAME);
            guest.setAddress(Constants.GUEST_ADDRESS);
            updateViews(guest);
        } else {
            fetchUserData(mUserName);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogZoomAnimation;
        }

        return dialog;
    }

    protected CompositeSubscription mCompositeSubscription;

    private void fetchUserData(String userName) {

        mLoginUser = AppsterApplication.mAppPreferences.getUserModel();

        UserProfileRequestModel request = new UserProfileRequestModel();
        request.setUserName(userName);
        request.setLimit(1);
        request.setUser_id(mUserId);

        mCompositeSubscription.add(AppsterWebServices.get().getUserProfile("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .filter(userDetailDataResponse -> getDialog() != null && isFragmentUIActive())
                .subscribe(userDetailDataResponse -> {
                    if (userDetailDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        mUserProfile = userDetailDataResponse.getData().getUser();
                        if (isAdded() && mUserProfile != null) {

                            if (mLoginUser != null && mLoginUser.isDevUser() && !TextUtils.isEmpty(mSlug)) {
                                // get stream data for DevUser
                                fetchStreamDetail(mSlug, () -> updateViews(mUserProfile));
                            } else {
                                updateViews(mUserProfile);
                            }

                        }
                    } else {
                        if (tvWatcher != null)
                            tvWatcher.setText(getString(R.string.watch_from_browser_user));
                    }
                }, error -> Timber.e(error.getMessage()))
        );
    }

    private void fetchStreamDetail(String slug, Runnable callback) {
        BeginStreamRequestModel model = new BeginStreamRequestModel(slug);

        mCompositeSubscription.add(AppsterWebServices.get().streamDetail("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), model)
                .filter(userDetailDataResponse -> getDialog() != null && isFragmentUIActive())
                .subscribe(response -> {
                    if (response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        StreamModel data = response.getData();
                        mIsRecorded = data.isIsRecorded();
                        mIsHide = data.getIsHide();
                        mUserIdStreamOwner = data.getUserId();
                        callback.run();
                    }
                }, error -> Timber.e(error.getMessage()))
        );
    }

    private void updateViews(UserModel userProfile) {
        ImageLoaderUtil.displayUserImage(getContext(), userProfile.getUserImage(), ivUserProfileImage, new RoundedCornersTransformation(Utils.dpToPx(8), 0));
        if (llStatsContainer != null) llStatsContainer.setVisibility(View.VISIBLE);
        if (tvLocation != null) tvLocation.setVisibility(View.VISIBLE);
        tvDisplayName.setText(StringUtil.decodeString(userProfile.getDisplayName()));
        String gender = userProfile.getGender();
        int genderResource = Constants.GENDER_MALE.equals(gender) ? R.drawable.ic_gender_male : (Constants.GENDER_FEMALE.equals(gender) ? R.drawable.ic_gender_female : -1);
        if (genderResource != -1) {
            Drawable genderDrawable = ContextCompat.getDrawable(getContext(), genderResource);
            genderDrawable.setBounds(0, 0, Utils.dpToPx(14), Utils.dpToPx(14));
            tvDisplayName.setCompoundDrawablePadding(Utils.dpToPx(5));
            tvDisplayName.setCompoundDrawables(null, null, genderDrawable, null);
        }

        tvUserName.setText(String.format("@%s", userProfile.getUserName()));
        tvNumFollower.setText(String.valueOf(userProfile.getFollowerCount()));
        tvNumFollowing.setText(String.valueOf(userProfile.getFollowingCount()));
        tvStarReceived.setText(Utils.formatThousand(userProfile.getTotalGoldFans()));
        tvLocation.setText(String.valueOf(userProfile.getAddress()));
//        tvStatus.setText(StringUtil.decodeString(userProfile.getAbout()));
        tvStatus.addAutoLinkMode(AutoLinkMode.MODE_URL);
        String about = StringUtil.decodeString(userProfile.getAbout());
        tvStatus.setAutoLinkText(about);
        tvStatus.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (!hasShowCopyPopup) {
                openChromeTab(getActivity(), matchedText);
            }
        });
        tvStatus.setOnLongClickListener(v -> {
            hasShowCopyPopup = true;
            CopyTextUtils.showOptionCopyText(getActivity(), v, about, menu -> {
                hasShowCopyPopup = false;
            });
            return true;
        });

        if (Constants.GUEST_USER_NAME.equals(mUserName)) {
            return;
        }

        tvBan.setVisibility(View.GONE);
        tvHideUnhide.setVisibility(View.GONE);
        tvStop.setVisibility(View.GONE);

        if (mLoginUser != null && mLoginUser.isDevUser()) {
            if (!mLoginUser.getUserId().equals(userProfile.getUserId())) {
                tvBan.setVisibility(View.VISIBLE);
                tvStop.setVisibility(View.VISIBLE);
            }

            if (mIsHostProfile && !mIsRecorded) {
                if (mIsHide) {
                    tvHideUnhide.setText(getActivity().getString(R.string.string_unhide));
                } else {
                    tvHideUnhide.setText(getActivity().getString(R.string.string_hide));
                }

                tvHideUnhide.setVisibility(View.VISIBLE);
            }
        }

        if (!userProfile.getUserId().equals(AppsterApplication.mAppPreferences.getUserModel().getUserId())) {
            if (mIsViewer) {
                tvBlockOrReport.setText(getString(R.string.newsfeed_menu_repost));
            } else {
                tvMute.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(mSlug)) {
                    if (AppsterUtility.loadPrefList(getContext(), Constants.STREAM_MUTE_LIST, userProfile.getUserId()).contains(mSlug)) {
                        tvMute.setText(getString(R.string.string_unmute));
                        isMuted = true;
                    } else {
                        isMuted = false;
                    }

                    if (AppsterUtility.loadPrefList(getContext(), Constants.STREAM_BLOCKED_LIST, userProfile.getUserId()).contains(mSlug)) {
                        tvBlockOrReport.setText(getString(R.string.string_unblock));
                    }
                }

            }
            boolean showVideoCall = isEnableVideoCall(userProfile);

            if (showVideoCall) {
                final int padding = Utils.dpToPx(10);
                llActionContainer.setPadding(padding, 0, padding, padding);
            }
            btnVideoCall.setVisibility(showVideoCall ? View.VISIBLE : View.GONE);
            btnVideoCall.setBackground(ContextCompat.getDrawable(getContext(), isCalling ? R.drawable.video_call_conner_btn_disable : R.drawable.video_call_conner_btn));
            tvBlockOrReport.setVisibility(View.VISIBLE);
            llActionContainer.setTranslationY(Utils.getScreenHeight());
            if (userProfile.getIsFollow() == Constants.IS_FOLLOWING_USER) {
                isFollowed = true;
                btnFollow.setText(getString(R.string.profile_dialog_follow));//
                btnFollow.setBackground(ContextCompat.getDrawable(getContext(), showVideoCall ? R.drawable.conner_btn_d8d8d8 : R.drawable.bottom_conner_btn_d8d8d8));
            } else {
                isFollowed = false;
                btnFollow.setText(getString(R.string.profile_un_follow));//
                btnFollow.setBackground(ContextCompat.getDrawable(getContext(), showVideoCall ? R.drawable.conner_btn_f05c56 : R.drawable.bottom_conner_btn_f05c56));
            }
            if (mUserProfileActionListener != null) {
                mUserProfileActionListener.onChangeFollowStatus(mUserProfile.getUserId(), userProfile.getIsFollow());
            }
            btnFollow.setVisibility(View.VISIBLE);
            slideUpAnimation(llActionContainer);
        }

        if (AppsterApplication.mAppPreferences.isUserLogin() && AppsterApplication.mAppPreferences.getUserModel().getUserName().equals(mUserName)) {
            tvBlockOrReport.setVisibility(View.GONE);
        } else {
            tvBlockOrReport.setVisibility(View.VISIBLE);
        }

        if (isTopfan) {
            tvMute.setVisibility(View.GONE);
            tvBlockOrReport.setVisibility(View.GONE);
        }

    }

    private boolean isEnableVideoCall(UserModel userProfile) {
        return userProfile != null && !mIsViewer && AppsterChatManger.getInstance(getContext()).getArrayCurrentUserInStream().contains(userProfile.getUserName()) && VIDEO_CALL_ENABLE;
    }

//    public void setVisibleReport() {
//        if (isViewReport) {
//            tvBlockOrReport.setVisibility(View.GONE);
//        } else {
//            tvBlockOrReport.setVisibility(View.VISIBLE);
//        }
//    }

    private void slideUpAnimation(View v) {

        v.animate()
                .setStartDelay(300)
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    private void executeFollowUser() {
        FollowUser followUser = new FollowUser(getActivity().getApplicationContext(), mUserProfile.getUserId(), true);
        followUser.execute();
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                if (getDialog() != null) {
                    mUserProfile.setIsFollow(Constants.IS_FOLLOWING_USER);


                    int followerCountOfUser = mUserProfile.getFollowerCount();
                    followerCountOfUser = followerCountOfUser + 1;
                    tvNumFollower.setText(String.valueOf(followerCountOfUser));
                    mUserProfile.setFollowerCount(followerCountOfUser);
                    if (mUserProfileActionListener != null) {
                        mUserProfileActionListener.onFollowCountChanged(followerCountOfUser);
                        mUserProfileActionListener.onChangeFollowStatus(mUserProfile.getUserId(),
                                isFollow ? Constants.IS_FOLLOWING_USER : Constants.UN_FOLLOW_USER);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                if (getDialog() != null) {
                    ((BaseActivity) getActivity()).handleError(message, errorCode);
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.e("Dialog destroy");
        ButterKnife.unbind(this);
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    @OnClick(R.id.ivUserProfileImage)
    public void onUserProfileClicked(View v) {
        if (!mIsViewer || isOwnerProfile(mUserProfile) || !canGoProfileScreen) return;
        if (PreventMultiClicks()) {
            return;
        }
        if (isFragmentUIActive() && getContext() != null && mUserProfile != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getContext(), R.anim.push_in_to_right, R.anim.push_in_to_left);
            startActivityForResult(UserProfileActivity.newIntent(getContext(), mUserProfile.getUserId(), mUserProfile.getDisplayName()),
                    Constants.REQUEST_CODE_VIEW_USER_PROFILE, options.toBundle());
        }
    }

    public void setCanGoProfileScreen(boolean canGoProfileScreen) {
        this.canGoProfileScreen = canGoProfileScreen;
    }

    private boolean isOwnerProfile(UserModel userProfile) {
        return userProfile != null &&
                AppsterApplication.mAppPreferences.getUserModel() != null &&
                AppsterApplication.mAppPreferences.getUserModel().getUserId().equalsIgnoreCase(userProfile.getUserId());
    }

    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }

    public boolean PreventMultiClicks() {

        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return true;
        }

        mLastClickTime = SystemClock.elapsedRealtime();

        return false;
    }

    @OnClick(R.id.btnFollow)
    public void onButtonFollowClicked(View v) {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(), R.anim.slide_in_up, R.anim.keep_view_animation);
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN, options.toBundle());
        } else {
            if (isFollowed)
                return;
            isFollowed = true;
            btnFollow.setBackground(ContextCompat.getDrawable(getContext(), isEnableVideoCall(mUserProfile) ? R.drawable.conner_btn_d8d8d8 : R.drawable.bottom_conner_btn_d8d8d8));
            btnFollow.setText(getActivity().getString(R.string.profile_dialog_follow));
            executeFollowUser();
        }

    }


    @OnClick(R.id.tvMute)
    public void onMuteClicked() {
        if (mUserProfileActionListener != null) {
            if (isMuted) {
                mUserProfileActionListener.onUnMuteUserClick(mUserProfile.getUserId(), mUserProfile.getDisplayName());
            } else {
                mUserProfileActionListener.onMuteUserClick(mUserProfile.getUserId(), mUserProfile.getDisplayName());
            }
        }

    }

    @OnClick(R.id.tvBlockOrReport)
    public void onBlockOrReportClicked() {
        if (mUserProfileActionListener != null) {
            if (tvBlockOrReport.getText().equals(getString(R.string.newsfeed_menu_repost))) {
                //report
                mUserProfileActionListener.onReportUserClick(mUserProfile.getUserId());
            } else {
                //block
                mUserProfileActionListener.onBlockUserClick(mUserProfile.getUserId(), mUserProfile.getDisplayName());
            }
        }
    }

    @OnClick(R.id.tvBan)
    public void onBanClicked() {
        if (mUserProfile == null) return;
        final String targetUserId = mUserProfile.getUserId();

        DialogUtil.showConfirmDialog(getActivity(),
                getActivity().getString(R.string.ban_user_title),
                getActivity().getString(R.string.ban_user_message),
                getActivity().getString(R.string.string_ban),
                () -> {
                    // ban
                    mCompositeSubscription.add(AppsterWebServices.get().banUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), targetUserId)
                            .subscribe(response -> {

                                if (response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                    if (getActivity() != null)
                                        Toast.makeText(getActivity(), R.string.ban_user_successed, Toast.LENGTH_SHORT).show();
                                } else {
                                    if (getActivity() != null)
                                        Toast.makeText(getActivity(), R.string.ban_user_failed, Toast.LENGTH_SHORT).show();
                                }
                            }, error -> {
                                if (getActivity() != null)
                                    ((BaseActivity) getActivity()).handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                            })
                    );
                }
        );
    }

    @OnClick(R.id.tvStop)
    public void onStopClicked() {
        if (mUserProfile == null) return;
        if (StringUtil.isNullOrEmptyString(mUserIdStreamOwner)) return;

        DialogUtil.showConfirmDialog(getActivity(),
                getString(R.string.stop_stream_title),
                getString(R.string.stop_stream_message),
                getActivity().getString(R.string.string_stop),
                () -> {
                    mCompositeSubscription.add(AppsterWebServices.get().stopStream("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), mUserIdStreamOwner)
                            .subscribe(response -> {
                                if (response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                    onCloseClicked();
                                }
                            }, error -> {
                                if (getActivity() != null)
                                    ((BaseActivity) getActivity()).handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                            })
                    );
                }
        );
    }

    @OnClick(R.id.tvHideUnhide)
    public void onHideUnhideClicked() {
        if (mUserProfile == null) return;
        final String targetUserId = mUserProfile.getUserId();

        String hideUnhide = (mIsHide) ?
                getActivity().getString(R.string.string_unhide) :
                getActivity().getString(R.string.string_hide);

        final String hideUnhideStr = hideUnhide;

        DialogUtil.showConfirmDialog(getActivity(),
                getActivity().getString(R.string.hide_unhide_stream_title, hideUnhideStr),
                getActivity().getString(R.string.hide_unhide_stream_message, hideUnhideStr),
                hideUnhideStr,
                () -> {
                    // hide Unhide
                    mCompositeSubscription.add(AppsterWebServices.get().hideStream(
                            "Bearer " + AppsterApplication.mAppPreferences.getUserToken(), mSlug, (mIsHide) ? 0 : 1)
                            .subscribe(response -> {
                                if (response.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                    if (mIsHide) {
                                        if (getActivity() != null)
                                            Toast.makeText(getActivity(), R.string.unhide_stream_succeeded, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (getActivity() != null)
                                            Toast.makeText(getActivity(), R.string.hide_stream_succeeded, Toast.LENGTH_SHORT).show();
                                    }

                                    // update text
                                    tvHideUnhide.setText((mIsHide) ?
                                            getActivity().getString(R.string.string_hide) :
                                            getActivity().getString(R.string.string_unhide));
                                    mIsHide = !mIsHide;

                                } else {

                                    if (getActivity() != null) Toast.makeText(getActivity(),
                                            getActivity().getString(R.string.hide_unhide_stream_failed, hideUnhideStr),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }, error -> {
                                ((BaseActivity) getActivity()).handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                            })
                    );
                }
        );
    }

    @OnClick(R.id.tvCloseDialog)
    public void onCloseClicked() {
        dismiss();
    }

    @OnClick(R.id.btn_video_call)
    public void callVideo() {
        if (mUserProfileActionListener != null)
            mUserProfileActionListener.onVideoCallClicked(mUserProfile.getUserId(), mUserProfile.getUserName());
    }

    public void setUserProfileActionListener(UserProfileActionListener userProfileActionListener) {
        mUserProfileActionListener = userProfileActionListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mUserProfileActionListener != null) {
            mUserProfileActionListener.onDimissed();
        }
    }

    public interface UserProfileActionListener {
        void onReportUserClick(String userId);

        void onBlockUserClick(String userId, String displayName);

        void onMuteUserClick(String userId, String displayName);

        void onUnMuteUserClick(String userId, String displayName);

        void onFollowCountChanged(int count);

        void onDimissed();

        void onChangeFollowStatus(String userId, int status);

        void onVideoCallClicked(String userId, String userName);
    }
}
