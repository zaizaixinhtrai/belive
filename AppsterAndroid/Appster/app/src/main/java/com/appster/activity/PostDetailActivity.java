package com.appster.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.bundle.BundleMedia;
import com.appster.comments.CommentActivity;
import com.appster.comments.ItemClassComments;
import com.appster.customview.CircleImageView;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.customview.autolinktextview.AutoLinkTextView;
import com.appster.customview.autolinktextview.AutoLinkUtil;
import com.appster.customview.autolinktextview.TouchableSpan;
import com.appster.dialog.DialogReport;
import com.appster.dialog.SharePostDialog;
import com.appster.features.user_liked.StreamLikedUsersActivity;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.layout.SquareImageView;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.FollowUser;
import com.appster.models.PostDetailModel;
import com.appster.models.StreamModel;
import com.appster.models.UserPostModel;
import com.appster.newsfeed.ItemModelClassNewsFeed;
import com.appster.newsfeed.PerformLike;
import com.appster.tracking.EventTracker;
import com.appster.tracking.EventTrackingName;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DialogUtil;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.RxUtils;
import com.appster.utility.SocialManager;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.request_models.BeginStreamRequestModel;
import com.appster.webservice.request_models.BlockUserRequestModel;
import com.appster.webservice.request_models.DeletePostRequestModel;
import com.appster.webservice.request_models.DeleteStreamRequestModel;
import com.appster.webservice.request_models.ReportRequestModel;
import com.appster.webservice.request_models.ReportStreamRequestModel;
import com.appster.webservice.request_models.SinglePostRequestModel;
import com.appster.webservice.request_models.StreamDefaultImageRequest;
import com.appster.webservice.request_models.ViewVideosRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.AnimationHelper;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.apster.common.CopyTextUtils;
import com.apster.common.CustomDialogUtils;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.LogUtils;
import com.apster.common.PixelUtil;
import com.apster.common.UiUtils;
import com.apster.common.Utils;
import com.apster.common.view.ExpandableTextView;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.pack.utility.CheckNetwork;
import com.pack.utility.SetDateTime;
import com.pack.utility.StringUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

import static com.apster.common.FileUtility.MEDIA_TYPE_IMAGE_CROPPED;

/**
 * Created by USER on 10/21/2015.
 */
public class PostDetailActivity extends BaseToolBarActivity implements View.OnClickListener {


    @Bind(R.id.userimage)
    CircleImageView userimage;
    @Bind(R.id.btn_live)
    Button btnLive;
    @Bind(R.id.txt_dislayname)
    TextView txtDislayname;
    @Bind(R.id.address)
    TextView address;
    @Bind(R.id.date_time_text)
    TextView dateTimeText;
    @Bind(R.id.textureVideoView)
    TextureView textureVideoView;
    @Bind(R.id.rl_TextureVideoView)
    FrameLayout rlTextureVideoView;
    @Bind(R.id.mediaImage)
    SquareImageView mediaImage;
    @Bind(R.id.play_video)
    ImageView playVideo;
    @Bind(R.id.progressVideo)
    ProgressBar progressVideo;
    @Bind(R.id.media_image_fl)
    FrameLayout mediaImageFl;
    @Bind(R.id.tv_title)
    AutoLinkTextView txtTitle;
    @Bind(R.id.imv_like)
    ImageView imvLike;
    @Bind(R.id.pblikeProgress)
    ProgressBar pblikeProgress;
    @Bind(R.id.fm_like)
    FrameLayout fmLike;
    @Bind(R.id.vBgLike)
    View vBgLike;
    @Bind(R.id.ivLike)
    ImageView ivLike;
    @Bind(R.id.imgComment)
    ImageView commentIv;
    @Bind(R.id.ln_menu_dialog_button)
    ImageButton linearMenuDailogBtn;
    @Bind(R.id.tv_like_count)
    TextView txtLikeCount;
    @Bind(R.id.commentListLayout)
    LinearLayout commentListLayout;
    @Bind(R.id.view_more_ll)
    TextView viewMoreLl;
    @Bind(R.id.linNewsFeed)
    LinearLayout linNewsFeed;
    @Bind(R.id.swipeRefreshlayout)
    SwipeRefreshLayout swipeRefreshlayout;
    @Bind(R.id.imgShare)
    ImageView shareButton;
    @Bind(R.id.onOffVolume)
    ImageView onOffVolume;

    @Bind(R.id.imgLikeCount)
    ImageView imgLikeCount;

    @Bind(R.id.imgViewCount)
    ImageView imgViewCount;
    @Bind(R.id.tv_view_count)
    TextView tvViewCount;

    @Bind(R.id.tv_content_post)
    AutoLinkTextView tvContentPost;

    private PopupMenu popupMenuOption;

    private String PostId = "";
    private String UserPostId = "";
    PostDetailModel model;

    boolean isEdited = false;

    private int positionOnGrid;

    private int oldFollowType = CommonDefine.USER_PROFILE_IS_FOLLOW;
    Timer timer;
    int commentCount = 0;
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    protected AppsterWebserviceAPI mService;
    boolean isPause = false;
    private KSYMediaPlayer mKsyMediaPlayer;
    private Surface mSurface;
    private StreamModel mStreamModel;
    private String mSlugStream;

//    VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(metaData -> {
//
//    });

    public static Intent createIntent(Context context) {
        return new Intent(context, PostDetailActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mService = AppsterWebServices.get();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            PostId = extras.getString(ConstantBundleKey.BUNDLE_POST_DETAIL_POST_ID);
            UserPostId = extras.getString(ConstantBundleKey.BUNDLE_POST_DETAIL_USER_ID);
            mSlugStream = extras.getString(ConstantBundleKey.BUNDLE_POST_DETAIL_SLUG_STREAM);

            if (!CheckNetwork.isNetworkAvailable(this)) {
                toastTextWhenNoInternetConnection("");
                return;
            }

            if (!StringUtil.isNullOrEmptyString(mSlugStream)) {
                getStreamData(true);
            } else {
                if (!StringUtil.isNullOrEmptyString(PostId)) {
                    getPost(true);
                }
                positionOnGrid = extras.getInt(ConstantBundleKey.BUNDLE_POSITION_ON_GRID, 0);
            }
        }
    }

    @Override
    public void onDestroy() {
        stopVideo();
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        if (isEdited) {

            intent.putExtra(ConstantBundleKey.BUNDLE_EDIT_ABLE_POST, true);
            setResult(RESULT_OK, intent);

        } else if (model != null && oldFollowType != model.getIs_follow()) {
            FollowStatusChangedEvent followStatusChangedEvent = new FollowStatusChangedEvent();
            followStatusChangedEvent.setFollowType(model.getIs_follow());
            followStatusChangedEvent.setUserId(model.getUser_id());
            intent.putExtra(ConstantBundleKey.BUNDLE_PROFILE_CHANGE_FOLLOW_USER, followStatusChangedEvent);

            setResult(RESULT_OK, intent);

        } else {
            setResult(RESULT_CANCELED);
        }

        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBarTile(getString(R.string.post_detail_title));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());

        goneNotify(true);

        if (isPause && model != null && model.getMedia_type() == Constants.VIDEOS_FEED) {
            initVideoView(model.getMedia_video());
        }

        handleTurnoffMenuSliding();
        isPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mKsyMediaPlayer != null) {
            stopVideo();
        }
        isPause = true;
    }


    @Override
    public int getLayoutContentId() {
        return R.layout.activity_post_detail;
    }

    @Override
    public void init() {

        ButterKnife.bind(this);
        setupAutoLink();
        swipeRefreshlayout.setOnRefreshListener(() -> {
            if (isShowStreamRecord()) {
                getStreamData(false);
            } else {
                getPost(false);
            }
        });


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, PixelUtil.dpToPx(this, getResources().getInteger(R.integer.margin_top_newfeed_item_top)), 0, 0);
        linNewsFeed.setLayoutParams(params);

        UiUtils.setColorSwipeRefreshLayout(swipeRefreshlayout);

    }

    boolean isRecord() {
        return !StringUtil.isNullOrEmptyString(mSlugStream);
    }

    private void getStreamData(boolean isShowDialog) {
        if (isShowDialog) {
            showDialog(this, getString(R.string.connecting_msg));
        }
        mCompositeSubscription.add(mService.streamDetail("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), new BeginStreamRequestModel(mSlugStream))
                .subscribe(streamDetailResponseModel -> {
                    dismisDialog();
                    swipeRefreshlayout.setRefreshing(false);
                    if (streamDetailResponseModel == null) return;

                    if (streamDetailResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        if (streamDetailResponseModel.getData() != null) {
                            mStreamModel = streamDetailResponseModel.getData();
                            model = PostDetailModel.convertPostDetailModelToStreamModel(mStreamModel);
                            setDataPost();
                        }
                    }
                }, error -> {
                    dismisDialog();
                    swipeRefreshlayout.setRefreshing(false);
                    if (isInFront) {
                        Toast.makeText(getApplicationContext(), "Cannot Start The Stream", Toast.LENGTH_LONG).show();
                    }
                }));
    }

    private void setupAutoLink() {
        AutoLinkUtil.addAutoLinkMode(txtTitle, tvContentPost);
        txtTitle.setAutoLinkOnClickListener(AutoLinkUtil.newListener(this));
        tvContentPost.setAutoLinkOnClickListener(AutoLinkUtil.newListener(this));
    }


    private void setDataPost() {

        if (model == null) {
            return;
        }

        // Set height for rl_TextureVideoView
        setHeightForTextureVideoView();

        txtDislayname.setText(StringUtil.decodeString(model.getDisplay_name()));

        dateTimeText.setText(SetDateTime.partTimeForFeedItem(model.getCreated(), PostDetailActivity.this));

        if (model.getLike_count() == 0) {
            txtLikeCount.setVisibility(View.GONE);
            imgLikeCount.setVisibility(View.GONE);
        } else {
            txtLikeCount.setVisibility(View.VISIBLE);
            imgLikeCount.setVisibility(View.VISIBLE);
            if (model.getLike_count() > 1) {
                txtLikeCount.setText(String.valueOf(model.getLike_count() + " " + getString(R.string.newsfeed_like_count_text)));
            } else {
                txtLikeCount.setText(String.valueOf(model.getLike_count() + " " + getString(R.string.newsfeed_like_count_is_one)));
            }
        }

        txtLikeCount.setOnClickListener(v -> openLikedUserScreen(model.getId()));
        long countView = model.getViewCount();
        if (countView <= 0) {
            tvViewCount.setVisibility(View.GONE);
            imgViewCount.setVisibility(View.GONE);
        } else if (model.getMedia_type() == Constants.VIDEOS_FEED) {
            tvViewCount.setVisibility(View.VISIBLE);
            imgViewCount.setVisibility(View.VISIBLE);
            String likeCountString;
            if (countView > 1) {
                likeCountString = String.format(getString(R.string.views_count), Utils.shortenNumber(countView));
            } else {
                likeCountString = String.format(getString(R.string.view_count), String.valueOf(countView));
            }
            tvViewCount.setText(likeCountString);
        }


        ImageLoaderUtil.displayUserImage(this, model.getUser_image(),
                userimage);

        if (model.getIs_like() == CommonDefine.NEWS_FEED_LIKE) {
            imvLike.setImageResource(R.drawable.ic_heart_like_25dp_selected);

        } else {
            imvLike.setImageResource(R.drawable.ic_heart_like_25dp_default);
        }

        if (!StringUtil.isNullOrEmptyString(model.getAddress())) {
            address.setVisibility(View.VISIBLE);
            address.setText(model.getAddress());
        } else {
            address.setVisibility(View.GONE);
        }

        // Set description

        if (model.getMedia_type() == CommonDefine.TYPE_QUOTES) {
            tvContentPost.setVisibility(View.VISIBLE);
            tvContentPost.setAutoLinkText(StringUtil.decodeString(model.getTitle()));
            txtTitle.setVisibility(View.GONE);
        } else {
            if (!StringUtil.isNullOrEmptyString(model.getTitle()) && model.getMedia_type() != CommonDefine.TYPE_QUOTES) {
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setAutoLinkText(StringUtil.decodeString(model
                        .getTitle()));
            } else {
                txtTitle.setVisibility(View.GONE);
            }
        }


        if (StringUtil.isNullOrEmptyString(model.getMedia_image())) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(PixelUtil.dpToPx(this, 20), PixelUtil.dpToPx(this, 0), PixelUtil.dpToPx(this, 20), 0);
            txtTitle.setLayoutParams(params);

        } else {
            ImageLoaderUtil.displayUserImage(this, model.getMedia_image(), mediaImage);

            if (!StringUtil.isNullOrEmptyString(model.getTitle())) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(PixelUtil.dpToPx(this, 20), PixelUtil.dpToPx(this, 10), PixelUtil.dpToPx(this, 20), 0);
                txtTitle.setLayoutParams(params);
            }

        }
        commentCount = model.getComment_count();
        handleComment(model.getComments());

        shareButton.setOnClickListener(v -> showChooseShareType());


        if (model.getMedia_type() == Constants.QUOTES_FEED) {
            onOffVolume.setVisibility(View.GONE);
            btnLive.setVisibility(View.GONE);
        } else if (model.getMedia_type() == Constants.VIDEOS_FEED) {
            btnLive.setVisibility(View.VISIBLE);
            btnLive.setText(mActivity.getString(R.string.newsfeed_btn_video));
            onOffVolume.setVisibility(View.VISIBLE);
        } else if (model.getMedia_type() == Constants.IMAGE_FEED) {
            mediaImage.setVisibility(View.VISIBLE);
            onOffVolume.setVisibility(View.GONE);
            btnLive.setVisibility(View.GONE);
        }

        onOffVolume.setOnClickListener(v -> muteUnMuteVideo());

        GestureDetector textureVideoGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (model.getMedia_type() == CommonDefine.TYPE_VIDEO) {
                    if (isShowStreamRecord()) {
                        openViewLiveStream(model.getMedia_video(), mSlugStream, true);
                    } else {
                        onOffVolume.performClick();
                    }
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                fmLike.performClick();
                return true;
            }
        });
        GestureDetector mediaImageGestureGDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            // event when double tap occurs
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                LogUtils.logV("NCS", "onDoubleTapEvent");
                fmLike.performClick();
                return true;
            }
        });

        rlTextureVideoView.setOnTouchListener((v, event) -> textureVideoGestureDetector.onTouchEvent(event));
        mediaImage.setOnTouchListener((v, event) -> mediaImageGestureGDetector.onTouchEvent(event));

        linearMenuDailogBtn.setOnClickListener(this);
        viewMoreLl.setOnClickListener(this);
        fmLike.setOnClickListener(this);
        userimage.setOnClickListener(this);
//        mediaImage.setOnClickListener(this);
        txtDislayname.setOnClickListener(this);
        commentIv.setOnClickListener(this);
        txtLikeCount.setOnClickListener(this);

        if (model.getMedia_type() == Constants.QUOTES_FEED) {
            mediaImageFl.setVisibility(View.GONE);
        } else {
            linNewsFeed.setVisibility(View.VISIBLE);
            mediaImageFl.setVisibility(View.VISIBLE);
        }

        if (isShowStreamRecord()) {
            btnLive.setText(getString(R.string.streaming_recorded));
            fmLike.setVisibility(View.GONE);
        }

        if (model.getMedia_type() == Constants.VIDEOS_FEED) {
//            handleVideoLoading(model.getMedia_video());

            initVideoView(model.getMedia_video());
        }
    }

    private void showChooseShareType() {

        final SharePostDialog sharePostDialog = SharePostDialog.newInstance();
        sharePostDialog.setChooseShareListenner(new SharePostDialog.ChooseShareListenner() {
            @Override
            public void chooseShareFacebook() {

                ItemModelClassNewsFeed item = new ItemModelClassNewsFeed(model);
                SocialManager.getInstance().shareFeedToFacebook(mActivity, item);
                //
                //tracking
                EventTracker.trackViewerShareStream(EventTrackingName.FACEBOOK);
            }

            @Override
            public void chooseShareInstagram() {

                ItemModelClassNewsFeed item = new ItemModelClassNewsFeed(model);
                SocialManager.getInstance().shareFeedToInstagram(mActivity, item);
                //tracking
                EventTracker.trackViewerShareStream(EventTrackingName.INSTAGRAM);

            }

            @Override
            public void chooseShareTwtter() {

                ItemModelClassNewsFeed item = new ItemModelClassNewsFeed(model);
                SocialManager.getInstance().shareFeedToTwitter(mActivity, item, false);
                //tracking
                EventTracker.trackViewerShareStream(EventTrackingName.TWITTER);
            }

            @Override
            public void chooseShareEmail() {
                ItemModelClassNewsFeed item = new ItemModelClassNewsFeed(model);
                SocialManager.getInstance().shareFeedToShareAction(true, mActivity, item, false);
                //tracking
                EventTracker.trackViewerShareStream(EventTrackingName.EMAIL);

            }

            @Override
            public void chooseShareWhatApp() {
                ItemModelClassNewsFeed item = new ItemModelClassNewsFeed(model);
                SocialManager.getInstance().shareFeedToWhatsapp(mActivity, item, false);
                //tracking
                EventTracker.trackViewerShareStream(EventTrackingName.WHATSAPP);
            }

            @Override
            public void copyLink() {
                CopyTextUtils.CopyClipboard(mActivity.getApplicationContext(), model.getWebPostUrl(), getString(R.string.share_link_copied));
            }

            @Override
            public void chooseShareOthers() {
                ItemModelClassNewsFeed item = new ItemModelClassNewsFeed(model);
                SocialManager.getInstance().shareFeedToShareAction(false, mActivity, item, false);
            }
        });

        sharePostDialog.show(getSupportFragmentManager(), "Share");
    }

    private void setHeightForTextureVideoView() {

//        if (mediaImageFl.getWidth() > 0)
//            mediaImageFl.getLayoutParams().height = mediaImageFl.getWidth();
    }

    private void getPost(boolean isShowDialog) {
        if (isShowDialog) {
            showDialog(this, getString(R.string.connecting_msg));
        }
        SinglePostRequestModel request = new SinglePostRequestModel();
        request.setPost_id(PostId);

        mCompositeSubscription.add(mService.getSinglePost("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .filter(postDetailModelBaseResponse -> !isFinishing() || !isDestroyed())
                .subscribe(singlePostResponseModel -> {

                    dismisDialog();
                    if (singlePostResponseModel == null) return;
                    if (singlePostResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        findViewById(R.id.no_data).setVisibility(View.GONE);
                        findViewById(R.id.linNewsFeed).setVisibility(View.VISIBLE);

                        model = singlePostResponseModel.getData();
                        commentCount = model.getComment_count();
                        oldFollowType = model.getIs_follow();

                        setDataPost();

                    } else if (singlePostResponseModel.getCode() == 404) {
                        findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        findViewById(R.id.linNewsFeed).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.linNewsFeed).setVisibility(View.GONE);
                        findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        handleError(singlePostResponseModel.getMessage(), singlePostResponseModel.getCode());
                    }

                    swipeRefreshlayout.setRefreshing(false);
                }, error -> {
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                    dismisDialog();
                    swipeRefreshlayout.setRefreshing(false);
                }));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fm_like:
                if (!AppsterApplication.mAppPreferences.isUserLogin()) {
                    goingLoginScreen();
                } else {
                    pblikeProgress.setVisibility(View.VISIBLE);

                    if (model.getIs_like() == CommonDefine.NEWS_FEED_LIKE) {

                        clickLike(model, CommonDefine.NEWS_FEED_UNLIKE);
                    } else {
                        clickLike(model, CommonDefine.NEWS_FEED_LIKE);

                    }
                }
                break;
            case R.id.imgComment:
                viewComment();
                break;
            case R.id.ln_menu_dialog_button:
                if (!AppsterApplication.mAppPreferences.isUserLogin()) {
                    goingLoginScreen();
                } else {
                    showOptionMenu(view);
                }
                break;
            case R.id.view_more_ll:
                viewComment();
                break;
            case R.id.txt_dislayname:
                clickGoingProfile();
                break;

            case R.id.userimage:

                clickGoingProfile();
                break;
            case R.id.tv_like_count:
                openLikedUserScreen(model.getId());
                break;

        }
    }

    private void openLikedUserScreen(String postId) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(mActivity, R.anim.push_in_to_right, R.anim.push_in_to_left);
        ActivityCompat.startActivityForResult(mActivity, StreamLikedUsersActivity.createIntent(mActivity, Integer.parseInt(postId)),
                Constants.REQUEST_LIKED_USERS_LIST_ACTIVITY, options.toBundle());
    }

    void toEditPost() {

        ItemModelClassNewsFeed item = new ItemModelClassNewsFeed(model);

        BundleMedia bundleMedia = new BundleMedia();
        bundleMedia.setIsPost(false);
        bundleMedia.setItemModelClassNewsFeed(item);
        bundleMedia.setKey(ConstantBundleKey.BUNDLE_MEDIA_KEY);
        bundleMedia.setType(model.getMedia_type());
        bundleMedia.setUriPath(model.getMedia_image());
        bundleMedia.setPostId(PostId);
        bundleMedia.setDiscription(model.getTitle());
        bundleMedia.startActivityForResult(mActivity, "ActivityPostMedia", Constants.REQUEST_EDIT_POST);
    }


    public void deletePost() {
        if (!model.getUser_id().equals(AppsterApplication.mAppPreferences.getUserModel().getUserId())) {

            utility.showMessage(
                    getString(R.string.app_name),
                    getResources()
                            .getString(
                                    R.string.profile_post_not_of_me),
                    this);

            return;
        }

        if (!CheckNetwork.isNetworkAvailable(this)) {
            utility.showMessage(
                    getString(R.string.app_name),
                    getResources()
                            .getString(
                                    R.string.no_internet_connection),
                    this);

            return;
        }

        DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg));
        if (isShowStreamRecord()) {
            DeleteStreamRequestModel deleteStreamRequestModel = new DeleteStreamRequestModel(model.getId());
            mCompositeSubscription.add(AppsterWebServices.get().deleteStream(AppsterApplication.mAppPreferences.getUserTokenRequest(), deleteStreamRequestModel)
                    .subscribe(new Subscriber<BaseResponse<Boolean>>() {
                        @Override
                        public void onCompleted() {
                            DialogManager.getInstance().dismisDialog();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(BaseResponse<Boolean> deleteStreamDataResponse) {
                            if (deleteStreamDataResponse == null) return;
                            if (deleteStreamDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                onBackPressed();
                            } else {
                                ((BaseActivity) mActivity).handleError(deleteStreamDataResponse.getMessage(),
                                        deleteStreamDataResponse.getCode());
                            }
                        }
                    }));
        } else {
            DeletePostRequestModel request = new DeletePostRequestModel();
            request.setPostId(PostId);
            mCompositeSubscription.add(mService.deletePost("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                    .filter(booleanBaseResponse -> !isFinishing() || !isDestroyed())
                    .subscribe(deletePostResponseModel -> {
                        DialogManager.getInstance().dismisDialog();
                        if (deletePostResponseModel == null) return;
                        if (deletePostResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
//                            utility.showMessage(getString(R.string.app_name), getString(R.string.your_post_has_delete),
//                                    PostDetailActivity.this, view -> {
//                                        Intent intent1 = new Intent();
//                                        intent1.putExtra(ConstantBundleKey.BUNDLE_DELETE_POST_ABLE, true);
//                                        intent1.putExtra(ConstantBundleKey.BUNDLE_POSITION_ON_GRID, positionOnGrid);
//
//                                        setResult(RESULT_OK, intent1);
//                                        finish();
//                                    });
                            Intent intent1 = new Intent();
                            intent1.putExtra(ConstantBundleKey.BUNDLE_DELETE_POST_ABLE, true);
                            intent1.putExtra(ConstantBundleKey.BUNDLE_POSITION_ON_GRID, positionOnGrid);
                            setResult(RESULT_OK, intent1);
                            finish();
                        } else {
                            handleError(deletePostResponseModel.getMessage(), deletePostResponseModel.getCode());
                        }
                    }, error -> {
                        DialogManager.getInstance().dismisDialog();
                        handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                    }));
        }
    }


    public void viewComment() {
        int intPostId;
        try {
            intPostId = isShowStreamRecord() ? Integer.parseInt(model.getId()) : Integer.parseInt(PostId);
        } catch (NumberFormatException e) {
            intPostId = 0;
            Timber.e(e);
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = CommentActivity.createIntent(this, intPostId, isShowStreamRecord() ? mSlugStream : "", 0, isShowStreamRecord() ? Constants.COMMENT_TYPE_STREAM : Constants.COMMENT_TYPE_POST,
                model.getUser_id());
        startActivityForResult(intent, Constants.COMMENT_REQUEST, options.toBundle());

    }

    public void callFollow() {

        FollowUser followUser;

        if (model.getIs_follow() == 1) {
            followUser = new FollowUser(this, model.getUser_id(), false);
        } else {
            followUser = new FollowUser(this, model.getUser_id(), true);
        }

        if (model.getIs_follow() == Constants.UN_FOLLOW_USER) {
            followUser.execute();
        } else {
            DialogUtil.showConfirmUnFollowUser(this, model.getDisplay_name(), () -> followUser.execute());
        }

        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                if (isFollow) {
                    model.setIs_follow(Constants.IS_FOLLOWING_USER);
                } else {
                    model.setIs_follow(Constants.UN_FOLLOW_USER);
                }
            }

            @Override
            public void onError(int errorCode, String message) {
            }
        });
    }

    public void confirmDeletePost(boolean isStreamRecord) {

        String title = isStreamRecord ? getString(R.string.do_you_want_to_delete_this_stream) : getString(R.string.do_you_want_to_delete_this_post);

        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        DialogbeLiveConfirmation confirmation = new DialogbeLiveConfirmation(builder);
        builder.title(getString(R.string.app_name))
                .message(title)
                .confirmText(getString(R.string.btn_text_ok))
                .singleAction(false)
                .onConfirmClicked(this::deletePost)
                .build().show(this);

    }

    public void callRepost() {
        if (!CheckNetwork.isNetworkAvailable(this)) {
            utility.showMessage(
                    getString(R.string.app_name),
                    getResources()
                            .getString(
                                    R.string.no_internet_connection),
                    this);

            return;
        }

        if (model.getIs_report() == Constants.NOT_REPORT_POST) {

            DialogReport dialogReport = DialogReport.newInstance();
            dialogReport.setChooseReportListenner(reason -> {

                ReportRequestModel request = new ReportRequestModel();
                request.setReport_for(model.getId());
                request.setMessage(reason);
                mCompositeSubscription.add(mService.report("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                        .filter(booleanBaseResponse -> !isFinishing() || !isDestroyed())
                        .subscribe(reportResponseModel -> {
                            if (reportResponseModel == null) return;
                            if (reportResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && reportResponseModel.getData()) {
                                model.setIs_report(Constants.HAS_BEEN_REPORT_POST);
                            } else {
                                handleError(reportResponseModel.getMessage(), reportResponseModel.getCode());
                            }
                        }, error -> {
                            handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                        }));

            });

            dialogReport.show(((BaseActivity) mActivity).getSupportFragmentManager(), "Report");

        } else {

            ReportRequestModel request = new ReportRequestModel();
            request.setReport_for(PostId);
            mCompositeSubscription.add(mService.unReport("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                    .filter(booleanBaseResponse -> !isFinishing() || !isDestroyed())
                    .subscribe(reportResponseModel -> {
                        if (reportResponseModel == null) return;
                        if (reportResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && reportResponseModel.getData()) {
                            model.setIs_report(Constants.NOT_REPORT_POST);
                        } else {
                            handleError(reportResponseModel.getMessage(), reportResponseModel.getCode());
                        }
                    }, error -> {
                        handleError(error.getMessage(), Constants.RETROFIT_ERROR);

                    }));
        }
    }

    public void clickLike(final PostDetailModel model, int typeLike) {
        if (!CheckNetwork.isNetworkAvailable(this)) {
            return;
        }

        fmLike.setClickable(false);
        mediaImage.setClickable(false);
        PerformLike like = new PerformLike(this, PostId,
                AppsterApplication.mAppPreferences.getUserModel().getUserId(), typeLike);
        like.setmFinishLike(new PerformLike.FinishLike() {
            @Override
            public void errorLikeClickListener(int positionListview) {
                if (pblikeProgress == null) return;
                pblikeProgress.setVisibility(View.GONE);
                fmLike.setClickable(true);
                mediaImage.setClickable(true);
            }

            @Override
            public void successLikeListener(int positionListview, int typeLike) {
                if (pblikeProgress == null) return;
                int likeCount = model
                        .getLike_count();
                if (typeLike == CommonDefine.NEWS_FEED_LIKE) {
                    model.setLike_count(likeCount + 1);
                    model.setIs_like(CommonDefine.NEWS_FEED_LIKE);

                    AnimationHelper.animateLikeAction(vBgLike, ivLike);
                    imvLike.setImageResource(R.drawable.ic_heart_like_25dp_selected);
                }

                if (typeLike == CommonDefine.NEWS_FEED_UNLIKE) {
                    model.setLike_count(likeCount - 1);
                    model.setIs_like(CommonDefine.NEWS_FEED_UNLIKE);

                    imvLike.setImageResource(R.drawable.ic_heart_like_25dp_default);
                }

                pblikeProgress.setVisibility(View.GONE);
                fmLike.setClickable(true);
                mediaImage.setClickable(true);

                if (model.getLike_count() <= 0) {
                    txtLikeCount.setVisibility(View.GONE);
                    imgLikeCount.setVisibility(View.GONE);
                } else {
                    txtLikeCount.setVisibility(View.VISIBLE);
                    imgLikeCount.setVisibility(View.VISIBLE);
                    if (model.getLike_count() > 1) {
                        txtLikeCount.setText(String.valueOf(model.getLike_count() + " " + getString(R.string.newsfeed_like_count_text)));
                    } else {
                        txtLikeCount.setText(String.valueOf(model.getLike_count() + " " + getString(R.string.newsfeed_like_count_is_one)));
                    }
                }
                isEdited = true;
            }
        });

        like.likePost();

    }

    public void showOptionMenu(View view) {

//        popupMenuOption = new PopupMenu(this, view);
        if (!isShowStreamRecord()) {
            if (isOwnerStream()) {
                CustomDialogUtils.showOwnerFeedOptionPopup(mActivity, StringUtil.isNullOrEmptyString(mSlugStream) ? clickMenuWithPost : clickMenuWithStreamRecord, getString(R.string.newsfeed_menu_del_post), getString(R.string.newsfeed_menu_edit_post));
//                popupMenuOption.getMenu().add(0, 0, 0, getString(R.string.newsfeed_menu_del_post));
//                popupMenuOption.getMenu().add(0, 0, 1, getString(R.string.newsfeed_menu_edit_post));
            } else {
                CustomDialogUtils.showFeedOptionPopup(mActivity, StringUtil.isNullOrEmptyString(mSlugStream) ? clickMenuWithPost : clickMenuWithStreamRecord, getFollowMessage(model.getIs_follow()), getReportMessage(model.getIs_report()), mActivity.getString(R.string.block_user));
//                    popupMenuOption.getMenu().add(0, 0, 0, getFollowMessage(model.getIsFollow()));
//                    popupMenuOption.getMenu().add(0, 0, 1, getReportMessage(model.getIs_report()));
            }
        } else {
            if (isOwnerStream()) {
                CustomDialogUtils.showOwnerFeedOptionPopup(mActivity, StringUtil.isNullOrEmptyString(mSlugStream) ? clickMenuWithPost : clickMenuWithStreamRecord, getString(R.string.newsfeed_menu_del_stream), getString(R.string.prelive_change_cover_image));
//                popupMenuOption.getMenu().add(0, 0, 0, getString(R.string.newsfeed_menu_del_stream));
//                popupMenuOption.getMenu().add(0, 0, 1, getString(R.string.prelive_change_cover_image));
            } else {
                CustomDialogUtils.showFeedOptionPopup(mActivity, StringUtil.isNullOrEmptyString(mSlugStream) ? clickMenuWithPost : clickMenuWithStreamRecord, getFollowMessage(model.getIs_follow()), getReportMessage(model.getIs_report()), mActivity.getString(R.string.block_user));
//                popupMenuOption.getMenu().add(0, 0, 0, getFollowMessage(model.getIsFollow()));
//                popupMenuOption.getMenu().add(0, 0, 1, getReportMessage(model.getIs_report()));
//                popupMenuOption.getMenu().add(0, 0, 2, mActivity.getString(R.string.block_user));
            }
        }


//            popupMenuOption.setOnMenuItemClickListener(StringUtil.isNullOrEmptyString(mSlugStream)?clickMenuWithPost:clickMenuWithStreamRecord);

//        popupMenuOption.show();
    }

    private boolean isShowStreamRecord() {
        return !StringUtil.isNullOrEmptyString(mSlugStream);
    }

    private int getReportStatus(UserPostModel postModel) {
        return postModel.isStreamItem() ? postModel.getStream().getIsReport() : postModel.getPost().getIsReport();
    }

    private String getDeleteMessage(boolean streamItem) {
        return streamItem ? mActivity.getString(R.string.newsfeed_menu_del_stream) : mActivity.getString(R.string.newsfeed_menu_del_post);
    }

    private String getReportMessage(int isReport) {
        return isReport == Constants.HAS_BEEN_REPORT_POST ? mActivity.getString(R.string.newsfeed_menu_unrepost) : mActivity.getString(R.string.newsfeed_menu_repost);
    }

    private String getFollowMessage(int isFollow) {
        return isFollow == Constants.IS_FOLLOWING_USER ? mActivity.getString(R.string.newsfeed_menu_unfolow) : mActivity.getString(R.string.newsfeed_menu_folow);
    }

    boolean isOwnerStream() {
        return AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(model.getUser_id());
    }

    private final CustomDialogUtils.FeedOptionCallback clickMenuWithStreamRecord = position -> {
        switch (position) {
            case 0:
                if (isOwnerStream()) {
                    confirmDeletePost(true);
                } else {
                    callFollow();
                }
                break;
            case 1:
                if (isOwnerStream()) {
                    CustomDialogUtils.openRecordVideoDialog(this, getString(R.string.select_a_stream_cover_from), v -> takePictureFromCamera(), v -> takePictureFromGallery());
                } else {
                    handleReportClick();
                }
                break;
            case 2:
                final String userIdBlock = model.getUser_id();
                DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
                builder.title(mActivity.getString(R.string.block_this_user))
                        .message(mActivity.getString(R.string.block_confirmation_content))
                        .confirmText(mActivity.getString(R.string.string_block))
                        .onConfirmClicked(() -> onBlockUser(userIdBlock))
                        .build().show(mActivity);

                break;
        }
    };
//    PopupMenu.OnMenuItemClickListener clickMenuWithStreamRecord = menuItem -> {
//
//        switch (menuItem.getOrder()) {
//            case 0:
//                if (isOwnerStream()) {
//                    confirmDeletePost(true);
//                } else {
//                    callFollow();
//                }
//                return true;
//            case 1:
//                if (isOwnerStream()) {
//                    CustomDialogUtils.openRecordVideoDialog(this, getString(R.string.select_a_stream_cover_from), v -> takePictureFromCamera(), v -> takePictureFromGallery());
//                } else {
//                    handleReportClick();
//                }
//                return true;
//            case 2:
//                final String userIdBlock = model.getUser_id();
//                DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
//                builder.title(mActivity.getString(R.string.block_this_user))
//                        .message(mActivity.getString(R.string.block_confirmation_content))
//                        .confirmText(mActivity.getString(R.string.string_block))
//                        .onConfirmClicked(() -> onBlockUser(userIdBlock))
//                        .build().show(mActivity);
//
//                return true;
//        }
//        return false;
//    };


    private final CustomDialogUtils.FeedOptionCallback clickMenuWithPost = position -> {
        switch (position) {
            case 0:
                if (AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(model.getUser_id())) {
                    confirmDeletePost(false);
                } else {
                    callFollow();
                }
                break;
            case 1:
                if (!AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(model.getUser_id())) {
                    callRepost();
                } else {
                    toEditPost();
                }
                break;
            case 2:/*Block*/
                final String userIdBlock = model.getUser_id();
                DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
                builder.title(mActivity.getString(R.string.block_this_user))
                        .message(mActivity.getString(R.string.block_confirmation_content))
                        .confirmText(mActivity.getString(R.string.string_block))
                        .onConfirmClicked(() -> onBlockUser(userIdBlock))
                        .build().show(mActivity);
                break;
        }
    };
//    PopupMenu.OnMenuItemClickListener clickMenuWithPost = menuItem -> {
//        switch (menuItem.getOrder()) {
//            case 0:
//                if (AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(model.getUser_id())) {
//                    confirmDeletePost(false);
//                } else {
//                    callFollow();
//                }
//                return true;
//            case 1:
//                if (!AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(model.getUser_id())) {
//                    callRepost();
//                } else {
//                    toEditPost();
//                }
//                return true;
//        }
//        return false;
//    };

    void handleReportClick() {
        boolean isReported = model.getIs_report() == Constants.HAS_BEEN_REPORT_POST;
        if (isReported) {
            unReportStream(mSlugStream);
        } else {
            showDialogReportItem(mSlugStream);
        }
    }

    private void showDialogReportItem(String itemID) {
        DialogReport dialogReport = DialogReport.newInstance();
        dialogReport.setChooseReportListenner(reason -> {
            reportStream(itemID, reason);
        });
        dialogReport.show(((BaseActivity) mActivity).getSupportFragmentManager(), "Report");
    }

    private void reportStream(String slug, String reason) {
        ReportStreamRequestModel reques = new ReportStreamRequestModel();
        reques.setMessage(reason);
        reques.setSlug(slug);
        mCompositeSubscription.add(AppsterWebServices.get().reportStream(AppsterApplication.mAppPreferences.getUserTokenRequest(), reques)
                .subscribe(new Subscriber<BaseResponse<Boolean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResponse<Boolean> reportDataResponse) {
                        if (reportDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && reportDataResponse.getData()) {
                            model.setIs_report(Constants.HAS_BEEN_REPORT_POST);
                        } else {
                            ((BaseActivity) mActivity).handleError(reportDataResponse.getMessage(),
                                    reportDataResponse.getCode());
                        }
                    }
                }));

    }

    private void unReportStream(String Slug) {
        ReportStreamRequestModel request = new ReportStreamRequestModel();
        request.setSlug(Slug);
        mCompositeSubscription.add(AppsterWebServices.get().unReportStream(AppsterApplication.mAppPreferences.getUserTokenRequest(), request)
                .subscribe(new Subscriber<BaseResponse<Boolean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResponse<Boolean> reportDataResponse) {
                        if (reportDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            model.setIs_report(Constants.NOT_REPORT_POST);
                        } else {
                            ((BaseActivity) mActivity).handleError(reportDataResponse.getMessage(),
                                    reportDataResponse.getCode());
                        }
                    }
                }));

    }

    void onBlockUser(String userId) {
        BlockUserRequestModel request = new BlockUserRequestModel();
        request.setBlockUserId(userId);

        mCompositeSubscription.add(AppsterWebServices.get().blockUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(reportUserResponseModel -> {
                    if (reportUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        Toast.makeText(getApplicationContext(), getString(R.string.blocked), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        ((BaseActivity) mActivity).handleError(reportUserResponseModel.getMessage(), Constants.RETROFIT_ERROR);
                        Timber.e(reportUserResponseModel.getMessage());
                    }
                }, error -> handleError(error.getMessage(), Constants.RETROFIT_ERROR)));
    }

    private void clickGoingProfile() {

        if (AppsterApplication.mAppPreferences.isUserLogin()) {

            if (model.getUser_id().equals(AppsterApplication.mAppPreferences.getUserModel().getUserId())) {
                return;
            }

        }

        startActivityProfile(model.getUser_id(), model.getDisplay_name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }
        Uri imageCroppedURI;
        try {
            imageCroppedURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE_CROPPED);
        } catch (NullPointerException e) {
            Timber.d(e);
            return;
        }
        switch (requestCode) {
            case Constants.COMMENT_REQUEST:
                getComment(data);
                isEdited = true;
                break;

            case Constants.REQUEST_EDIT_POST:
                if (!CheckNetwork.isNetworkAvailable(this)) {
                    toastTextWhenNoInternetConnection("");
                    return;
                } else {
                    if (!StringUtil.isNullOrEmptyString(PostId)) {
                        getPost(true);
                        isEdited = true;
                    }
                }

                break;

            case Constants.REQUEST_PIC_FROM_CAMERA:
                fileUri = data.getData();
                if (fileUri == null) {
                    return;
                }
                uploadNewCoverImage(fileUri, mSlugStream);
                break;
            case Constants.REQUEST_COVER_FROM_LIBRARY:
                fileUri = data.getData();
                if (fileUri == null) {
                    return;
                }

                performCrop(fileUri, imageCroppedURI);
                break;
            case Constants.REQUEST_PIC_FROM_CROP:
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    uploadNewCoverImage(resultUri, mSlugStream);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void uploadNewCoverImage(Uri resultUri, String streamSlug) {
        // TODO: 5/15/17 implement upload cover image
        StreamDefaultImageRequest request = new StreamDefaultImageRequest(streamSlug, new File(resultUri.getPath()));
        mCompositeSubscription.add(AppsterWebServices.get().saveFirstStreamImage("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request.build())
                .filter(booleanBaseResponse -> isInFront)
                .subscribe(streamPostImageResponse -> {
                    if (streamPostImageResponse.getData()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.cover_image_changed), Toast.LENGTH_SHORT).show();
                    }
                }, error -> Timber.e(error.getMessage())));
    }

    private void getComment(Intent data) {

        Bundle extras = data.getExtras();

        if (extras == null) {
            return;
        }

        ArrayList<ItemClassComments> arrCommentReturn = extras.getParcelableArrayList(ConstantBundleKey.BUNDLE_LIST_COMMENT);
        if (arrCommentReturn == null) {
            arrCommentReturn = new ArrayList<>();
        }
        model.getComments().clear();
        model.getComments().addAll(arrCommentReturn);
        commentCount = extras.getInt(ConstantBundleKey.BUNDLE_COMMENT_COUNT);
        handleComment(model.getComments());
    }

    private void handleComment(ArrayList<ItemClassComments> arrComment) {

        int commentListSize = arrComment.size();
        Timber.e("commentListSize1 = " + commentListSize);
        if (commentListSize > 0) {
            commentListLayout.setVisibility(View.VISIBLE);
            commentListLayout.removeAllViews();
            if (commentListSize > Constants.NUMBER_COMMENT_SHOW) {
                commentListSize = Constants.NUMBER_COMMENT_SHOW;
            }
            for (int i = arrComment.size() - commentListSize;
                 i < arrComment.size() && i >= 0; i++) {
                final ItemClassComments comment = arrComment.get(i);
                final ExpandableTextView tvCommentUserName = (ExpandableTextView) mActivity.getLayoutInflater().inflate(R.layout.comment_newfeed_row, null);
                AutoLinkUtil.addAutoLinkMode(tvCommentUserName);
                tvCommentUserName.setAutoLinkOnClickListener(AutoLinkUtil.newListener(this));
                String contentShow = StringUtil.decodeString(comment.getNameShowUI()) + " " + StringUtil.decodeString(comment.getMessage());
                int start = contentShow.indexOf(comment.getNameShowUI());
                int end = contentShow.indexOf(comment.getNameShowUI()) + comment.getNameShowUI().length();
                Typeface opensansbold = Typeface.createFromAsset(mActivity.getAssets(),
                        "fonts/opensansbold.ttf");
                TouchableSpan clickUSerName = new TouchableSpan() {
                    @Override
                    public void onClick(View widget) {
                        startActivityProfile(comment.getUser_id(), comment.getDisplay_name());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#58585B"));
                        ds.setUnderlineText(false);
                    }
                };

                SpannableString commentsContentSpan = new SpannableString(contentShow);

                commentsContentSpan.setSpan(new RelativeSizeSpan(1.0f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentsContentSpan.setSpan(new CustomTypefaceSpan("sans-serif", Typeface.create(opensansbold, Typeface.NORMAL)),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentsContentSpan.setSpan(clickUSerName, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                commentsContentSpan.setSpan(clickComment, end, contentShow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvCommentUserName.setAutoLinkText(commentsContentSpan);
                tvCommentUserName.setOnClickListener(v -> commentIv.performClick());
                commentListLayout.addView(tvCommentUserName);
            }

        } else {
            commentListLayout.setVisibility(View.GONE);
        }

        Timber.e("commentListSize = " + commentListSize);
        if (commentCount > Constants.NUMBER_COMMENT_SHOW) {
            viewMoreLl.setVisibility(View.VISIBLE);
            viewMoreLl.setText(String.format(mActivity.getString(R.string.view_all_for), String.valueOf(commentCount)));
            viewMoreLl.setOnClickListener(v -> commentIv.performClick());
        } else {
            viewMoreLl.setVisibility(View.GONE);
        }

    }

    public void muteUnMuteVideo() {
        if (mKsyMediaPlayer == null) return;
        if (AppsterApplication.mAppPreferences.getIsMuteVideos()) {
            mKsyMediaPlayer.setPlayerMute(1);
            onOffVolume.setBackgroundResource(R.drawable.volume_off);
            AppsterApplication.mAppPreferences.setIsMuteVideos(false);
        } else {
            mKsyMediaPlayer.setPlayerMute(0);
            onOffVolume.setBackgroundResource(R.drawable.volume_on);
            AppsterApplication.mAppPreferences.setIsMuteVideos(true);
        }
    }

    void initVideoView(String urlVideo) {
        if (isFinishing() || isDestroyed()) return;
        rlTextureVideoView.setVisibility(View.VISIBLE);
        mediaImage.setVisibility(View.GONE);
        progressVideo.setVisibility(View.GONE);
        textureVideoView.setVisibility(View.VISIBLE);

        stopVideo();
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }

        mKsyMediaPlayer = new KSYMediaPlayer.Builder(getApplicationContext()).build();
        textureVideoView.setSurfaceTextureListener(mSurfaceTextureListener);
        powerOnSurfaceTextureAvailable();

        mKsyMediaPlayer.setTimeout(10, 30);
        mKsyMediaPlayer.setBufferTimeMax(2);
        mKsyMediaPlayer.setBufferSize(15);

        mKsyMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mKsyMediaPlayer.setOnInfoListener(mOnInfoListener);
        mKsyMediaPlayer.setOnErrorListener(mOnErrorListener);
        mKsyMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        try {
            mKsyMediaPlayer.setDataSource(urlVideo);
        } catch (IOException e) {
            Timber.e(e.getMessage());
        }

        mKsyMediaPlayer.prepareAsync();
//        mKsyMediaPlayer.runInForeground();
//        mKsyMediaPlayer.start();

    }

    private void powerOnSurfaceTextureAvailable() {
        if (textureVideoView.isAvailable()) {
            mSurfaceTextureListener.onSurfaceTextureAvailable(textureVideoView.getSurfaceTexture(), textureVideoView.getWidth(), textureVideoView.getHeight());
        }
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

            if (mSurface == null) {
                mSurface = new Surface(surfaceTexture);

                if (mKsyMediaPlayer != null) {
                    Timber.e("setSurface");
                    mKsyMediaPlayer.setSurface(mSurface);
                }

            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (mSurface != null) {
                mSurface.release();
                mSurface = null;
            }

            Timber.e("onSurfaceTextureDestroyed = ");

//            if (mKsyMediaPlayer != null)
//                mKsyMediaPlayer.setSurface(null);
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {

            Timber.e("VideoPlayer============OnPrepared");

            if (isRecord()) {
                mVideoWidth = mKsyMediaPlayer.getVideoWidth();
                mVideoHeight = mKsyMediaPlayer.getVideoHeight();
                Timber.e("Video w - %d, h - %d", mVideoWidth, mVideoHeight);
                FrameLayout.LayoutParams frameParrent = (FrameLayout.LayoutParams) textureVideoView.getLayoutParams();
                int videoViewSize = Math.max(rlTextureVideoView.getWidth(), rlTextureVideoView.getHeight());

                if (mVideoWidth > mVideoHeight) {
                    frameParrent.width = videoViewSize;
                    frameParrent.height = (int) (mVideoHeight * (videoViewSize / (float) mVideoWidth));
                    Timber.e("mVideoWidth");
                } else {
                    frameParrent.height = videoViewSize;
                    frameParrent.width = (int) (mVideoWidth * (videoViewSize / (float) mVideoHeight));
                    Timber.e("mVideoHeight");
                }
                textureVideoView.setLayoutParams(frameParrent);
            } else {
                FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                frameParams.gravity = Gravity.CENTER;
                textureVideoView.setLayoutParams(frameParams);
            }

//             Set Video Scaling Mode
            mKsyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);

            progressVideo.setVisibility(View.GONE);
            AppsterApplication.mAppPreferences.setIsMuteVideos(true);
            muteUnMuteVideo();
            handleInCreateViewCount();

            //start player
            mKsyMediaPlayer.start();
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
                mediaImage.setVisibility(View.GONE);
                if (mSurface != null) mKsyMediaPlayer.setSurface(mSurface);
                break;
            case KSYMediaPlayer.MEDIA_INFO_RELOADED:
                Timber.e("======================Succeed to mPlayerReload video.");
                return false;
            default:
                Timber.e("KSYMediaPlayer info %d - %d", i, i1);
                break;

        }
        return false;
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = (mp, what, extra) -> {
        Timber.e("OnErrorListener, Error:" + what + ",extra:" + extra);

        switch (what) {
            case KSYMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                break;

            default:
                break;
        }

        return false;
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = mp -> {
        Timber.e("OnCompletionListener, play complete................");
        if (mKsyMediaPlayer != null) {
            mKsyMediaPlayer.start();
            handleInCreateViewCount();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void stopVideo() {
        if (mKsyMediaPlayer != null) {
            mKsyMediaPlayer.stop();
            mKsyMediaPlayer.release();
            mKsyMediaPlayer = null;
        }
    }

    private Subscription mSubscription;

    void handleInCreateViewCount() {
//        if (isPause) return;
        RxUtils.unsubscribeIfNotNull(mSubscription);
        mSubscription = mService.viewVideos(AppsterApplication.mAppPreferences.getUserTokenRequest(), new ViewVideosRequestModel(model.getId()))
                .filter(videosCountModelBaseResponse -> !isFinishing() || !isDestroyed())
                .subscribe(viewVideosCountResponse -> {
                    if (viewVideosCountResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        long countView = viewVideosCountResponse.getData().mViewCount;
                        if (tvViewCount != null && imgViewCount != null) {
                            if (countView <= 0) {
                                tvViewCount.setVisibility(View.GONE);
                                imgViewCount.setVisibility(View.GONE);
                            } else {
                                tvViewCount.setVisibility(View.VISIBLE);
                                imgViewCount.setVisibility(View.VISIBLE);
                                String likeCountString;
                                if (countView > 1) {
                                    likeCountString = String.format(getString(R.string.views_count), StringUtil.handleThousandNumber(countView));
                                } else {
                                    likeCountString = String.format(getString(R.string.view_count), String.valueOf(countView));
                                }
                                tvViewCount.setText(likeCountString);
                            }
//
                        }
                    }
                }, Timber::e);
    }

}


