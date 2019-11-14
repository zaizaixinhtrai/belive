package com.appster.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.bundle.BundleMedia;
import com.appster.comments.CommentActivity;
import com.appster.dialog.DialogReport;
import com.appster.dialog.SharePostDialog;
import com.appster.features.login.LoginActivity;
import com.appster.features.user_liked.StreamLikedUsersActivity;
import com.appster.interfaces.FollowUserListener;
import com.appster.interfaces.OnItemClickListenerRecyclerView;
import com.appster.interfaces.OnItemLongClickListenerRecyclerView;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.manager.ShowErrorManager;
import com.appster.manager.VideosManager;
import com.appster.manager.WallFeedManager;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.FollowUser;
import com.appster.models.ListenerEventModel;
import com.appster.models.NewLikeEventModel;
import com.appster.models.ReportEvent;
import com.appster.models.ShareStreamModel;
import com.appster.models.StreamModel;
import com.appster.models.UserModel;
import com.appster.models.UserPostModel;
import com.appster.models.event_bus_models.DeletePost;
import com.appster.models.event_bus_models.DeleteStream;
import com.appster.newsfeed.ItemModelClassNewsFeed;
import com.appster.newsfeed.PerformLike;
import com.appster.tracking.EventTracker;
import com.appster.tracking.EventTrackingName;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DialogUtil;
import com.appster.utility.RxUtils;
import com.appster.utility.SocialManager;
import com.appster.viewholder.WallFeedItemViewHolder;
import com.appster.viewholder.WallFeedStickHeaderViewHolder;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.BlockUserRequestModel;
import com.appster.webservice.request_models.DeletePostRequestModel;
import com.appster.webservice.request_models.DeleteStreamRequestModel;
import com.appster.webservice.request_models.ReportRequestModel;
import com.appster.webservice.request_models.ReportStreamRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.apster.common.CopyTextUtils;
import com.apster.common.CustomDialogUtils;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.LogUtils;
import com.apster.common.UiUtils;
import com.data.entity.requests.EarnPointsRequestEntity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.pack.utility.CheckNetwork;
import com.pack.utility.DialogInfoUtility;
import com.pack.utility.SetDateTime;
import com.pack.utility.StringUtil;
import com.stickyheaders.SectioningAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


/**
 * Created by sonnguyen on 11/15/16.
 */

public class AdapterWallFeed extends SectioningAdapter {
    private static final int USER_ITEM_TYPE_NORMAL = 0;
    private static final int USER_ITEM_TYPE_PROGRESS_INDICATOR = 1;
    private static final int USER_ITEM_TYPE_EXHAUSTED = 2;
    Activity mActivity;

    List<UserPostModel> arrayPost;
    boolean isLoading;
    boolean isExhausted;
    UserModel userProfileDetails;
    UserPostModel currentDataOption;
    private DialogInfoUtility utility;
    //    VideoPlayerManager<MetaData> mVideoPlayerManager;
    VideosManager mVideoPlayerManager;
    FollowUserListener followUserListener;
    BlockCallback blockCallback;

    StreamCoverListener mStreamCoverListener;
    protected CompositeSubscription mCompositeSubscription;


    public interface ClickUserComment {
        void onClickComment(String userID, String displayName);
    }

    public interface BlockCallback {
        void OnBlockSuccessfully();
    }

    public interface StreamCoverListener {
        void onChangeStreamCoverRequest(String slug);
    }

    public AdapterWallFeed(List<UserPostModel> arrayPost, Activity mContext, VideosManager mVideoPlayerManager) {
        this.arrayPost = arrayPost;
        this.mActivity = mContext;
        this.mVideoPlayerManager = mVideoPlayerManager;
        this.userProfileDetails = null;
        this.followUserListener = null;
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    public AdapterWallFeed(List<UserPostModel> arrayPost, Activity mContext, VideosManager mVideoPlayerManager, UserModel userProfileDetails, FollowUserListener followUserListener) {
        this.arrayPost = arrayPost;
        this.mActivity = mContext;
        this.mVideoPlayerManager = mVideoPlayerManager;
        this.userProfileDetails = userProfileDetails;
        this.followUserListener = followUserListener;
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    public void setUserProfileDetails(UserModel userProfileDetails) {
        this.userProfileDetails = userProfileDetails;
        notifyAllSectionsDataSetChanged();
    }

    public void setBlockCallback(BlockCallback blockCallback) {
        this.blockCallback = blockCallback;
    }

    public void setStreamCoverListener(StreamCoverListener streamCoverListener) {
        mStreamCoverListener = streamCoverListener;
    }


    @Override
    public int getNumberOfSections() {
        return arrayPost.size();
    }

    // Hard code 1 since 1 header with 1 item
    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return 1;
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {

        // loading and exhausted indicators are the ONLY item in the LAST section
        if (sectionIndex == arrayPost.size() - 1) {
            if (isLoading) {
                return USER_ITEM_TYPE_PROGRESS_INDICATOR;
            } else if (isExhausted) {
                return USER_ITEM_TYPE_EXHAUSTED;
            }
        }
        return USER_ITEM_TYPE_NORMAL;
    }

    @Override
    public void notifySectionInserted(int sectionIndex) {
        super.notifySectionInserted(sectionIndex);
    }

    public void addAllSection(ArrayList<UserPostModel> arrayPosts) {
        if (!isLoading && !isExhausted) {
            arrayPost.addAll(arrayPosts);
            notifySectionInserted(arrayPost.size() - 1);
        }
    }

    // All section must have header
    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (itemType) {
            case USER_ITEM_TYPE_NORMAL:
                return new WallFeedItemViewHolder(inflater.inflate(R.layout.view_item_wall_post, parent, false), mActivity, mVideoPlayerManager);

            case USER_ITEM_TYPE_PROGRESS_INDICATOR:
                //return new LoadingIndicatorItemViewHolder(inflater.inflate(R.layout.list_item_load_progress, parent, false));

            case USER_ITEM_TYPE_EXHAUSTED:
                //return new ExhaustedItemViewHolder(inflater.inflate(R.layout.list_item_load_exhausted, parent, false));
        }

        throw new IllegalArgumentException("Unrecognized itemType: " + itemType);
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemUserType) {
        if (viewHolder instanceof WallFeedItemViewHolder) {
            if (!arrayPost.isEmpty() && sectionIndex < arrayPost.size())
                ((WallFeedItemViewHolder) viewHolder).onBindData(mActivity, arrayPost.get(sectionIndex), handleClickOnItem, handleLongClickItem, sectionIndex, onClickComment, userProfileDetails);
        }
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerUserType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.view_stick_header_wall_post, parent, false);
        return new WallFeedStickHeaderViewHolder(v);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int sectionIndex, int headerUserType) {
        if (arrayPost != null && !arrayPost.isEmpty() && sectionIndex < arrayPost.size()) {
            bindHeaderView(arrayPost.get(sectionIndex), ((WallFeedStickHeaderViewHolder) (viewHolder)));
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    private void bindHeaderView(UserPostModel userPostModel, WallFeedStickHeaderViewHolder holder) {
        String userAvatar = "", address, displayName = "", timeCreate = "", userID = "";
        if (userProfileDetails != null) {
            userAvatar = userProfileDetails.getUserImage();
            userID = userProfileDetails.getUserId();
            displayName = userProfileDetails.getNameShowInClient();
            if (userPostModel.isStreamItem()) {
                address = userPostModel.getStream().getAddress();
                if (userPostModel.getStream().isStreamBeingLive()) {
                    String partTimeForFeedItem = SetDateTime.partTimeForFeedItem(userPostModel.getStream().getCreated(), mActivity);
                    if (Constants.VIETNAMESE_LANGUAGE_PHONE.equals(UiUtils.getLocalization()) && mActivity.getString(R.string.time_just_now).equals(partTimeForFeedItem)) {
                        timeCreate = mActivity.getString(R.string.viet_namese_just_started);
                    } else {
                        timeCreate = String.format(mActivity.getString(R.string.started_xx_ago), partTimeForFeedItem);
                    }
                } else {
                    timeCreate = SetDateTime.partTimeForFeedItem(userPostModel.getStream().getCreated(), mActivity);
                }
            } else {
                address = userPostModel.getPost().getAddress();
                timeCreate = SetDateTime.partTimeForFeedItem(userPostModel.getPost().getCreated(), mActivity);
            }

        } else if (userPostModel.isStreamItem()) {
            StreamModel streamModel = userPostModel.getStream();
            address = streamModel.getAddress();
            if (streamModel.getPublisher() != null) {
                userAvatar = streamModel.getPublisher().getUserImage();
                userID = streamModel.getPublisher().getUserId();
                displayName = streamModel.getPublisher().getNameShowInClient();
                if (streamModel.isStreamBeingLive()) {
                    String partTimeForFeedItem = SetDateTime.partTimeForFeedItem(userPostModel.getStream().getCreated(), mActivity);
                    if (Constants.VIETNAMESE_LANGUAGE_PHONE.equals(UiUtils.getLocalization()) && mActivity.getString(R.string.time_just_now).equals(partTimeForFeedItem)) {
                        timeCreate = mActivity.getString(R.string.viet_namese_just_started);
                    } else {
                        timeCreate = String.format(mActivity.getString(R.string.started_xx_ago), partTimeForFeedItem);
                    }
                } else {
                    timeCreate = SetDateTime.partTimeForFeedItem(streamModel.getCreated(), mActivity);
                }
            }
        } else {
            ItemModelClassNewsFeed feedModel = userPostModel.getPost();
            userAvatar = feedModel.getUserImage();
            displayName = feedModel.getNameShowInClient();
            address = feedModel.getAddress();
            timeCreate = SetDateTime.partTimeForFeedItem(feedModel.getCreated(), mActivity);
            userID = feedModel.getUserId();
        }
        final String userIDClick = userID;
        final String DisplayNameClick = displayName;
        View.OnClickListener clickHeader = v -> clickGoingProfile(userIDClick, DisplayNameClick);
        holder.imgUserImange.setOnClickListener(clickHeader);
        holder.tvDisplayName.setOnClickListener(clickHeader);

        holder.onBindData(mActivity, userAvatar, displayName, address, timeCreate);
    }


    private void clickGoingProfile(String userID, String DisplayName) {
        if (AppsterApplication.mAppPreferences.isUserLogin()) {

            if (userProfileDetails == null) {
                if (!userID.equals(AppsterApplication.mAppPreferences.getUserModel().getUserId())) {
                    ((BaseToolBarActivity) mActivity).startActivityProfile(userID, DisplayName);
                }
            } else {
                if (!userID.equals(userProfileDetails.getUserId())) {
                    ((BaseToolBarActivity) mActivity).startActivityProfile(userID, DisplayName);
                }
            }

        } else {
            if (userProfileDetails == null) {
                ((BaseToolBarActivity) mActivity).startActivityProfile(userID, DisplayName);
            } else {
                if (!userID.equals(userProfileDetails.getUserId())) {
                    ((BaseToolBarActivity) mActivity).startActivityProfile(userID, DisplayName);
                }
            }
        }
    }

    private ClickUserComment onClickComment = this::clickGoingProfile;

    private OnItemLongClickListenerRecyclerView handleLongClickItem = (OnItemLongClickListenerRecyclerView<UserPostModel>) (v, data, viewHolder, position) -> {

        switch (v.getId()) {
            case R.id.tv_title:
            case R.id.tv_content_post:

                if (data != null) {
                    if (data.isStreamItem()) {
                        CopyTextUtils.showOptionCopyText(mActivity, v, StringUtil.decodeString(data.getStream().getTitle()), null);
                    } else {
                        CopyTextUtils.showOptionCopyText(mActivity, v, StringUtil.decodeString(data.getPost().getTitle()), null);
                    }
                }

                break;
        }
    };


    private OnItemClickListenerRecyclerView handleClickOnItem = new OnItemClickListenerRecyclerView<UserPostModel>() {
        @Override
        public void OnclickItem(View v, UserPostModel data, RecyclerView.ViewHolder mHolder, int position) {
            StreamModel stream = data.getStream();
            ItemModelClassNewsFeed post = data.getPost();
            switch (v.getId()) {
                case R.id.fm_like:
                    if (!AppsterApplication.mAppPreferences.isUserLogin()) {
                        startActivityLogin();
                        return;
                    }
                    if (!data.isStreamItem()) {
                        int typeLike = CommonDefine.NEWS_FEED_LIKE;
                        if (post.getIsLike() == CommonDefine.NEWS_FEED_LIKE) {
                            typeLike = CommonDefine.NEWS_FEED_UNLIKE;
                        }
                        clickLike(typeLike, post, (WallFeedItemViewHolder) mHolder, position);
                    }
                    break;
                case R.id.imgComment:
                    if (!AppsterApplication.mAppPreferences.isUserLogin()) {
                        startActivityLogin();
                        return;
                    }
                    if (data.isStreamItem()) {
                        StreamModel streamModel = data.getStream();
                        viewComment(streamModel.getStreamId(), streamModel.getSlug(), position, Constants.COMMENT_TYPE_STREAM, streamModel.getUserId());
                    } else {
                        try {
                            viewComment(Integer.parseInt(post.getPostId()), position, Constants.COMMENT_TYPE_POST, post.getUserId());
                        } catch (NumberFormatException e) {
                            Timber.e(e);
                        }
                    }
                    break;
                case R.id.imgShare:
                    showChooseShareType(data.isStreamItem() ? getItemModelStreamFeed(stream) : post, data.isStreamItem() && !data.getStream().isIsRecorded());
                    break;
                case R.id.ln_menu_dialog_button:
                    currentDataOption = data;
                    if (currentDataOption.isStreamItem()) {
                        Timber.e("menu getReported stream %d", currentDataOption.getStream().getIsReport());
                    } else {
                        Timber.e("menu getReported post %d", currentDataOption.getPost().getIsReport());
                    }
                    showOptionMenu(v, data, position);
                    break;

                case R.id.mediaImage:
                    handelClickGoingStream(stream);
                    break;

                case R.id.tv_like_count:
                    if (!data.isStreamItem() && data.getPost() != null) {
                        openLikedUserScreen(data.getPost().getPostId(), false, "");
                    } else if (data.isStreamItem() && data.getStream() != null) {
                        openLikedUserScreen("0", true, data.getStream().getSlug());
                    }
                    break;
            }
        }

        private void handelClickGoingStream(StreamModel streamModel) {
            if (streamModel == null || streamModel.getPublisher() == null) {
                return;
            }
            if (streamModel.isIsRecorded() && (streamModel.getStatus() == Constants.StreamStatus.StreamEnd)) {
                ((BaseToolBarActivity) mActivity).openViewLiveStream(streamModel.getStreamRecording().getDownloadUrl(), streamModel.getSlug(), streamModel.getPublisher().getUserImage(), true);
            } else {
                ((BaseToolBarActivity) mActivity).openViewLiveStream(streamModel.getStreamUrl(), streamModel.getSlug(), streamModel.getPublisher().getUserImage(), false);
            }
        }
    };

    void viewComment(int postID, String slug, int position, int commentType, String userId) {
        if (!CheckNetwork.isNetworkAvailable(mActivity)) {
            return;
        }
        Intent intent = CommentActivity.createIntent(mActivity, postID, slug, position, commentType, userId);
        mActivity.startActivityForResult(intent, Constants.COMMENT_REQUEST);
    }

    void viewComment(int postID, int position, int commentType, String userId) {
        viewComment(postID, "", position, commentType, userId);
    }


    void clickLike(int typeLike, final ItemModelClassNewsFeed itemFeed, WallFeedItemViewHolder mHolder, int position) {

        if (!CheckNetwork.isNetworkAvailable(mActivity)) {
            return;
        }

        String userId;
        if (userProfileDetails != null) {
            userId = userProfileDetails.getUserId();
        } else {
            userId = itemFeed.getNfs_userid();
        }
        mHolder.pblikeProgress.setVisibility(View.VISIBLE);
        mHolder.linkeLayout.setClickable(false);
        mHolder.mediaImage.setClickable(false);
        PerformLike like = new PerformLike(mActivity, itemFeed.getPostId(),
                userId, typeLike);
        like.setmFinishLike(new PerformLike.FinishLike() {
            @Override
            public void errorLikeClickListener(int positionListview) {
                mHolder.pblikeProgress.setVisibility(View.GONE);
                mHolder.linkeLayout.setClickable(true);
                mHolder.mediaImage.setClickable(true);
            }

            @Override
            public void successLikeListener(int positionListview, int typeLike) {
                mHolder.pblikeProgress.setVisibility(View.GONE);
                mHolder.linkeLayout.setClickable(true);
                mHolder.mediaImage.setClickable(true);
                NewLikeEventModel likeEventModel = new NewLikeEventModel();
                likeEventModel.setIsLike(typeLike);
                likeEventModel.setStream(false);
                likeEventModel.setPostId(arrayPost.get(position).getPost().getPostId());
                int likeCount = itemFeed.getLikeCount();
                if (typeLike == Constants.NEWS_FEED_LIKE) {
                    if (!arrayPost.get(position).isStreamItem()) {
                        itemFeed.setLikeCount(likeCount + 1);
                        itemFeed.setIsLike(Constants.NEWS_FEED_LIKE);
                        arrayPost.get(position).getPost().setLikeCount(likeCount + 1);
                        arrayPost.get(position).getPost().setIsLike(Constants.NEWS_FEED_LIKE);
                    }
                    mHolder.imvLike.setImageResource(R.drawable.ic_heart_like_25dp_selected);
                    mHolder.animateLikeAction();
                    mHolder.handleLikeCountView(likeCount + 1);
                    likeEventModel.setLikeCount(likeCount + 1);

                } else if (typeLike == Constants.NEWS_FEED_UNLIKE) {
                    if (!arrayPost.get(position).isStreamItem()) {
                        itemFeed.setLikeCount(likeCount - 1);
                        itemFeed.setIsLike(Constants.NEWS_FEED_UNLIKE);
                        arrayPost.get(position).getPost().setLikeCount(likeCount - 1);
                        arrayPost.get(position).getPost().setIsLike(Constants.NEWS_FEED_UNLIKE);
                    }
                    mHolder.imvLike.setImageResource(R.drawable.ic_heart_like_25dp_default);
                    mHolder.handleLikeCountView(likeCount - 1);
                    likeEventModel.setLikeCount(likeCount - 1);
                }
                ListenerEventModel listenerEventModel = new ListenerEventModel();
                listenerEventModel.setTypeEvent(ListenerEventModel.TypeEvent.NEW_LIKE);
                listenerEventModel.setNewLikeEventModel(likeEventModel);
                ((BaseActivity) mActivity).eventChange(listenerEventModel);
                WallFeedManager.getInstance().updateLike(likeEventModel);

            }
        });

        like.likePost();
    }

    private void startActivityLogin() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(mActivity, R.anim.slide_in_up, R.anim.keep_view_animation);
        Intent intent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN, options.toBundle());

    }

    void showChooseShareType(ItemModelClassNewsFeed modelShare, boolean isStreaming) {

        final SharePostDialog sharePostDialog = SharePostDialog.newInstance();
        if (isStreaming) {
            sharePostDialog.setHideShareInstagramView();
        }

//        if (!data.isStreamItem() && (data.getPost() != null && data.getPost().getMediaType() == CommonDefine.TYPE_QUOTES)) {
//            sharePostDialog.setHideCopyLinkView(true);
//        } else {
//            sharePostDialog.setHideCopyLinkView(false);
//        }

        sharePostDialog.setChooseShareListenner(new SharePostDialog.ChooseShareListenner() {
            @Override
            public void chooseShareFacebook() {

                SocialManager.getInstance().shareURLToFacebook(mActivity, modelShare.getWebPostUrl(),
                        Constants.FACEBOOK_SHARE_REQUEST_CODE,
                        new FacebookCallback<Sharer.Result>() {
                            @Override
                            public void onSuccess(Sharer.Result result) {
                                Timber.e("onSuccess");
                                userEarnPoints(Constants.FACEBOOK_SHARE_TYPE, modelShare.slug);
                                SocialManager.getInstance().cancelStartingTask();
                            }

                            @Override
                            public void onCancel() {
                                Timber.e("onCancel");
                                SocialManager.getInstance().cancelStartingTask();
                            }

                            @Override
                            public void onError(FacebookException error) {
                                Timber.e("onError");
                                SocialManager.getInstance().cancelStartingTask();
                            }
                        });
                // tracking
                EventTracker.trackSharePost(EventTrackingName.FACEBOOK);
                if (isStreaming)
                    AppsterApplication.mAppPreferences.saveShareStreamModel(new ShareStreamModel("Stream", modelShare.slug, Constants.FACEBOOK_SHARE_TYPE));
            }

            @Override
            public void chooseShareInstagram() {
                SocialManager.getInstance().shareFeedToInstagram(mActivity, modelShare);
                // tracking
                EventTracker.trackSharePost(EventTrackingName.INSTAGRAM);
            }

            @Override
            public void chooseShareTwtter() {

                SocialManager.getInstance().shareFeedToTwitter(mActivity, modelShare, isStreaming);
                //
                // tracking
                EventTracker.trackSharePost(EventTrackingName.TWITTER);
                if (isStreaming)
                    AppsterApplication.mAppPreferences.saveShareStreamModel(
                            new ShareStreamModel("Stream", modelShare.slug, Constants.TWITTWER_SHARE_TYPE));
            }

            @Override
            public void chooseShareEmail() {
                SocialManager.getInstance().shareFeedToShareAction(true, mActivity, modelShare, isStreaming);
                // tracking
                EventTracker.trackSharePost(EventTrackingName.EMAIL);
                if (isStreaming)
                    AppsterApplication.mAppPreferences.saveShareStreamModel(new ShareStreamModel(
                            "Stream", modelShare.slug, Constants.EMAIL_SHARE_TYPE));
            }

            @Override
            public void chooseShareWhatApp() {
                SocialManager.getInstance().shareFeedToWhatsapp(mActivity, modelShare, isStreaming);
                // tracking
                EventTracker.trackSharePost(EventTrackingName.WHATSAPP);
                if (isStreaming)
                    AppsterApplication.mAppPreferences.saveShareStreamModel(new ShareStreamModel(
                            "Stream", modelShare.slug, Constants.WHATSAPP_SHARE_TYPE));
            }

            @Override
            public void copyLink() {
                String webURL = modelShare.getWebPostUrl();
                CopyTextUtils.CopyClipboard(mActivity.getApplicationContext(), webURL, mActivity.getString(R.string.share_link_copied));
                if (isStreaming)
                    userEarnPoints(Constants.COPY_LINK_TYPE, modelShare.slug);
            }

            @Override
            public void chooseShareOthers() {
                SocialManager.getInstance().shareFeedToShareAction(false, mActivity, modelShare, isStreaming);
                if (isStreaming)
                    AppsterApplication.mAppPreferences.saveShareStreamModel(new ShareStreamModel(
                            "Stream", modelShare.slug, Constants.OTHER_SHARE_TYPE));
            }
        });

        sharePostDialog.show(((BaseActivity) mActivity).getSupportFragmentManager(), "Share");
    }

    ItemModelClassNewsFeed getItemModelStreamFeed(StreamModel streamModel) {
        ItemModelClassNewsFeed itemModelClassNewsFeed = new ItemModelClassNewsFeed();
        itemModelClassNewsFeed.setMediaType(0);
        itemModelClassNewsFeed.setTitle(streamModel.getTitle());
        itemModelClassNewsFeed.setWebPostUrl(streamModel.getWebStreamUrl());
        itemModelClassNewsFeed.setUserName(streamModel.getPublisher().getUserName());
        itemModelClassNewsFeed.slug = streamModel.getSlug();
        return itemModelClassNewsFeed;
    }

    private PopupMenu popupMenuOption;

    void showOptionMenu(View view, UserPostModel optionPost, int position) {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ((BaseToolBarActivity) (mActivity)).goingLoginScreen();
            return;
        }
//        popupMenuOption = new PopupMenu(mActivity, view);

        String usetId = "";
        int isFollow = 0;
        int isReport = 0;
        if (userProfileDetails != null) {
            usetId = userProfileDetails.getUserId();
            isFollow = userProfileDetails.getIsFollow();
            isReport = getReportStatus(optionPost);
        } else if (optionPost.isStreamItem()) {
            if (optionPost.getStream() != null && optionPost.getStream().getPublisher() != null) {
                usetId = optionPost.getStream().getPublisher().getUserId();
                isFollow = optionPost.getStream().getPublisher().getIsFollow();
                isReport = optionPost.getStream().getIsReport();
            }
        } else {
            if (optionPost.getPost() != null) {
                usetId = optionPost.getPost().getUserId();
                isFollow = optionPost.getPost().getIsFollow();
                isReport = optionPost.getPost().getIsReport();
            }
        }

        if (isMatchedUserId(usetId)) {
            CustomDialogUtils.showOwnerFeedOptionPopup(mActivity, new OnDialogMenuItemClickListener(position), getDeleteMessage(optionPost.isStreamItem()), getEditOrChangeCoverMessage(optionPost.isStreamItem()));
        } else {
            CustomDialogUtils.showFeedOptionPopup(mActivity, new OnDialogMenuItemClickListener(position), getFollowMessage(isFollow), getReportMessage(isReport), mActivity.getString(R.string.block_user));
        }
    }

    private String getEditOrChangeCoverMessage(boolean streamItem) {
        return streamItem ? mActivity.getString(R.string.prelive_change_cover_image) : mActivity.getString(R.string.newsfeed_menu_edit_post);
    }

    boolean isMatchedUserId(String usetId) {
        return AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(usetId);
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

    private class OnDialogMenuItemClickListener implements CustomDialogUtils.FeedOptionCallback {

        int position;

        OnDialogMenuItemClickListener(int postion) {
            this.position = postion;

        }


        @Override
        public void onOptionClicked(int optionPos) {
            String displayName = "";
            String userId;
            int isFollow;
            if (userProfileDetails != null) {
                userId = userProfileDetails.getUserId();
                isFollow = userProfileDetails.getIsFollow();
            } else if (currentDataOption.isStreamItem()) {
                displayName = currentDataOption.getStream().getPublisher().getDisplayName();
                userId = currentDataOption.getStream().getPublisher().getUserId();
                isFollow = currentDataOption.getStream().getPublisher().getIsFollow();
            } else {
                displayName = currentDataOption.getPost().getDisplayName();
                userId = currentDataOption.getPost().getUserId();
                isFollow = currentDataOption.getPost().getIsFollow();
            }
            if (isMatchedUserId(userId)) {
                switch (optionPos) {
                    case 0: /*Delete*/
                        confirmDeleteItem(currentDataOption.isStreamItem(), position);
                        break;
                    case 1:/*Edit/ChangeCover*/
                        if (!currentDataOption.isStreamItem()) {
                            BundleMedia bundleMedia = new BundleMedia();
                            bundleMedia.setIsPost(false);
                            bundleMedia.setItemModelClassNewsFeed(currentDataOption.getPost());
                            bundleMedia.setKey(ConstantBundleKey.BUNDLE_MEDIA_KEY);
                            bundleMedia.setType(currentDataOption.getPost().getMediaType());
                            bundleMedia.setUriPath(currentDataOption.getPost().getMediaImage());
                            bundleMedia.setPostId(currentDataOption.getPost().getPostId());
                            bundleMedia.setDiscription(currentDataOption.getPost().getTitle());
                            bundleMedia.setPosition(position);
                            bundleMedia.startActivityForResult(mActivity, "ActivityPostMedia", Constants.REQUEST_EDIT_POST);
                        } else {
                            // TODO: 5/15/17 implement change cover function
                            if (mStreamCoverListener != null)
                                mStreamCoverListener.onChangeStreamCoverRequest(currentDataOption.getStream().getSlug());

                        }
                        break;
                }
            } else {
                switch (optionPos) {
                    case 0:/*Follow/Unfollow*/
                        handleFollowUnFollow(isFollow, userId, displayName);
                        break;
                    case 1:/*Report/Unreport*/
                        handleReportClick(position);
                        break;

                    case 2:/*Block*/
                        final String userIdBlock = userId;
                        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
                        builder.title(mActivity.getString(R.string.block_this_user))
                                .message(mActivity.getString(R.string.block_confirmation_content))
                                .confirmText(mActivity.getString(R.string.string_block))
                                .onConfirmClicked(() -> onBlockUser(userIdBlock))
                                .build().show(mActivity);
                        break;
                }
            }
        }
    }

    private class OnMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;

        OnMenuItemClickListener(int postion) {
            this.position = postion;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            String displayName = "";
            String userId;
            int isFollow;
            if (userProfileDetails != null) {
                userId = userProfileDetails.getUserId();
                isFollow = userProfileDetails.getIsFollow();
            } else if (currentDataOption.isStreamItem()) {
                displayName = currentDataOption.getStream().getPublisher().getDisplayName();
                userId = currentDataOption.getStream().getPublisher().getUserId();
                isFollow = currentDataOption.getStream().getPublisher().getIsFollow();
            } else {
                displayName = currentDataOption.getPost().getDisplayName();
                userId = currentDataOption.getPost().getUserId();
                isFollow = currentDataOption.getPost().getIsFollow();
            }

            if (isMatchedUserId(userId)) {
                switch (item.getOrder()) {
                    case 0: /*Delete*/
                        confirmDeleteItem(currentDataOption.isStreamItem(), position);
                        return true;
                    case 1:/*Edit/ChangeCover*/
                        if (!currentDataOption.isStreamItem()) {
                            BundleMedia bundleMedia = new BundleMedia();
                            bundleMedia.setIsPost(false);
                            bundleMedia.setItemModelClassNewsFeed(currentDataOption.getPost());
                            bundleMedia.setKey(ConstantBundleKey.BUNDLE_MEDIA_KEY);
                            bundleMedia.setType(currentDataOption.getPost().getMediaType());
                            bundleMedia.setUriPath(currentDataOption.getPost().getMediaImage());
                            bundleMedia.setPostId(currentDataOption.getPost().getPostId());
                            bundleMedia.setDiscription(currentDataOption.getPost().getTitle());
                            bundleMedia.setPosition(position);
                            bundleMedia.startActivityForResult(mActivity, "ActivityPostMedia", Constants.REQUEST_EDIT_POST);
                        } else {
                            // TODO: 5/15/17 implement change cover function
                            if (mStreamCoverListener != null)
                                mStreamCoverListener.onChangeStreamCoverRequest(currentDataOption.getStream().getSlug());

                        }
                        return true;
                }
            } else {
                switch (item.getOrder()) {
                    case 0:/*Follow/Unfollow*/
                        handleFollowUnFollow(isFollow, userId, displayName);
                        return true;
                    case 1:/*Report/Unreport*/
                        handleReportClick(position);
                        return true;

                    case 2:/*Block*/
                        final String userIdBlock = userId;
                        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
                        builder.title(mActivity.getString(R.string.block_this_user))
                                .message(mActivity.getString(R.string.block_confirmation_content))
                                .confirmText(mActivity.getString(R.string.string_block))
                                .onConfirmClicked(() -> onBlockUser(userIdBlock))
                                .build().show(mActivity);
                        return true;
                }
            }
            return false;
        }
    }

    void confirmDeleteItem(final Boolean isStream, int position) {
        String confirmString;
        if (isStream) {
            confirmString = mActivity.getString(R.string.do_you_want_to_delete_this_stream);
        } else {
            confirmString = mActivity.getString(R.string.do_you_want_to_delete_this_post);
        }

        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title(mActivity.getString(R.string.app_name))
                .message(confirmString)
                .confirmText(mActivity.getString(R.string.btn_text_ok))
                .singleAction(false)
                .onConfirmClicked(() -> {
                    String itemID = "";
                    if (isStream) {
                        itemID = currentDataOption.getStream().getSlug();
                    } else {
                        itemID = currentDataOption.getPost().getPostId();
                    }
                    deleteItem(isStream, position, itemID);
                })
                .build().show(mActivity);
    }

    private void deleteItem(final Boolean isStream, final int position, final String itemID) {

        if (!CheckNetwork.isNetworkAvailable(mActivity)) {
            utility.showMessage(
                    mActivity.getString(R.string.app_name),
                    mActivity.getString(R.string.no_internet_connection),
                    mActivity);
            return;
        }
        DialogManager.getInstance().showDialog(mActivity, mActivity.getResources().getString(R.string.connecting_msg));
        if (isStream) {
            DeleteStreamRequestModel deleteStreamRequestModel = new DeleteStreamRequestModel(itemID);
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
                            if (deleteStreamDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && deleteStreamDataResponse.getData()) {
                                arrayPost.remove(position);
                                EventBus.getDefault().post(new DeleteStream(itemID));
                                notifyAllSectionsDataSetChanged();
                            } else {
                                ((BaseActivity) mActivity).handleError(deleteStreamDataResponse.getMessage(),
                                        deleteStreamDataResponse.getCode());
                            }
                        }
                    }));
        } else {
            DeletePostRequestModel request = new DeletePostRequestModel();
            request.setPostId(itemID);
            mCompositeSubscription.add(AppsterWebServices.get().deletePost(AppsterApplication.mAppPreferences.getUserTokenRequest(), request)
                    .subscribe(new Subscriber<BaseResponse<Boolean>>() {
                        @Override
                        public void onCompleted() {
                            DialogManager.getInstance().dismisDialog();
                        }

                        @Override
                        public void onError(Throwable error) {
                            DialogManager.getInstance().dismisDialog();
                            ((BaseActivity) mActivity).handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                        }

                        @Override
                        public void onNext(BaseResponse<Boolean> deletePostDataResponse) {
                            if (deletePostDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                                arrayPost.remove(position);
                                EventBus.getDefault().post(new DeletePost(itemID));
                                notifyAllSectionsDataSetChanged();
                            } else {
                                ((BaseActivity) mActivity).handleError(deletePostDataResponse.getMessage(), deletePostDataResponse.getCode());
                            }
                        }
                    }));
        }

    }

//    void broadCastDeleteItem(String postID, boolean isStream) {
//        ListenerEventModel listenerEventModel = new ListenerEventModel();
//        listenerEventModel.setTypeEvent(ListenerEventModel.TypeEvent.DELETE_POST);
//        DeletePostEventModel eventModel = new DeletePostEventModel();
//        eventModel.setStream(isStream);
//        eventModel.setPostId(postID);
//        eventModel.setSlug(postID);
//        listenerEventModel.setDeletePostEventModel(eventModel);
//        ((BaseActivity) mActivity).eventChange(listenerEventModel);
//    }


    void handleFollowUnFollow(int isFollow, String userID, String displayName) {
        if (isFollow == Constants.IS_FOLLOWING_USER) {
            DialogUtil.showConfirmUnFollowUser(mActivity, displayName, () -> unFollowUser(userID));
        } else {
            followUser(userID);
        }
    }

    private void unFollowUser(String userID) {
        FollowUser followUser = new FollowUser(mActivity, userID, false);

        followUser.execute();

        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                changeFollowUser(Constants.UN_FOLLOW_USER);
            }

            @Override
            public void onError(int errorCode, String message) {

            }
        });
    }

    private void followUser(String userID) {
        FollowUser followUser = new FollowUser(mActivity, userID, true);

        followUser.execute();
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                changeFollowUser(Constants.IS_FOLLOWING_USER);
            }

            @Override
            public void onError(int errorCode, String message) {
                if (errorCode == ShowErrorManager.pass_word_required) {
                    handleFollowWithPassword(userID);
                }
            }
        });
    }

    private void handleFollowWithPassword(String userID) {
        new DialogbeLiveConfirmation.Builder()
                .title(mActivity.getString(R.string.enter_password))
                .setPasswordBox(true)
                .confirmText(mActivity.getString(R.string.verify))
                .onEditTextValue(value -> followUserWithPassword(userID, value))
                .build().show(mActivity);
    }

    private void followUserWithPassword(String userID, String password) {
        FollowUser followUser = new FollowUser(mActivity, userID, true);

        followUser.executeFollowWithPass(password);
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                changeFollowUser(Constants.IS_FOLLOWING_USER);
            }

            @Override
            public void onError(int errorCode, String message) {
                if (errorCode == ShowErrorManager.pass_word_required) {
                    handleFollowWithPassword(userID);
                }
            }

        });
    }

    void onBlockUser(String userId) {
        BlockUserRequestModel request = new BlockUserRequestModel();
        request.setBlockUserId(userId);

        mCompositeSubscription.add(AppsterWebServices.get().blockUser("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(reportUserResponseModel -> {
                    if (reportUserResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        if (blockCallback != null) {
                            blockCallback.OnBlockSuccessfully();
                        }
                        Toast.makeText(mActivity, mActivity.getString(R.string.blocked), Toast.LENGTH_SHORT).show();
                    } else {
                        ((BaseActivity) mActivity).handleError(reportUserResponseModel.getMessage(), Constants.RETROFIT_ERROR);
                        LogUtils.logE("onblockuser", reportUserResponseModel.getMessage());
                    }
                }, error -> ((BaseActivity) mActivity).handleError(error.getMessage(), Constants.RETROFIT_ERROR)));
    }

    private void changeFollowUser(int isFollow) {
        if (followUserListener != null) {
            followUserListener.onFollowUser(isFollow);
        }
        if (userProfileDetails != null) {
            userProfileDetails.setIsFollow(isFollow);
            FollowStatusChangedEvent followStatusChangedEvent = new FollowStatusChangedEvent();
            followStatusChangedEvent.setStream(false);
            followStatusChangedEvent.setUserId(userProfileDetails.getUserId());
            followStatusChangedEvent.setFollowType(isFollow);
            WallFeedManager.getInstance().updateFollowStatus(followStatusChangedEvent);
//            notifyDataSetChanged();

        } else if (currentDataOption != null) {
            String userID;
            if (currentDataOption.isStreamItem()) {
                userID = currentDataOption.getStream().getPublisher().getUserId();
                currentDataOption.getStream().getPublisher().setIsFollow(isFollow);
            } else {
                userID = currentDataOption.getPost().getUserId();
                currentDataOption.getPost().setIsFollow(isFollow);
            }
            for (int i = 0; i < arrayPost.size(); i++) {
                UserPostModel postModel = arrayPost.get(i);
                if (postModel.isStreamItem()) {
                    if (postModel.getStream().getPublisher().getUserId().equalsIgnoreCase(userID)) {
                        arrayPost.get(i).getStream().getPublisher().setIsFollow(isFollow);
                    }
                } else {
                    if (postModel.getPost().getUserId().equalsIgnoreCase(userID)) {
                        arrayPost.get(i).getPost().setIsFollow(isFollow);
                    }
                }
            }
            FollowStatusChangedEvent followStatusChangedEvent = new FollowStatusChangedEvent();
            followStatusChangedEvent.setStream(false);
            followStatusChangedEvent.setUserId(userID);
            followStatusChangedEvent.setFollowType(isFollow);
            WallFeedManager.getInstance().updateFollowStatus(followStatusChangedEvent);
        }

    }

    void handleReportClick(int position) {
        Boolean isStream = currentDataOption.isStreamItem();
        Boolean isReported;
        String itemID;
        if (isStream) {
            isReported = currentDataOption.getStream().getIsReport() == Constants.HAS_BEEN_REPORT_POST;
            itemID = currentDataOption.getStream().getSlug();
        } else {
            isReported = currentDataOption.getPost().getIsReport() == Constants.HAS_BEEN_REPORT_POST;
            itemID = currentDataOption.getPost().getPostId();
        }
        if (isReported) {
            if (isStream) {
                unReportStream(currentDataOption.getStream().getSlug(), position);
            } else {
                unReportPost(currentDataOption.getPost().getPostId(), position);
            }
        } else {
            showDialogReportItem(isStream, itemID, position);
        }
    }

    private void showDialogReportItem(boolean isStream, String itemID, int position) {
        DialogReport dialogReport = DialogReport.newInstance();
        dialogReport.setChooseReportListenner(reason -> {
            Timber.e("report reason %s", reason);
            if (isStream) {
                reportStream(itemID, reason, position);
            } else {
                Timber.e("report post %s", reason);
                reportPost(itemID, reason, position);
            }

        });
        dialogReport.show(((BaseActivity) mActivity).getSupportFragmentManager(), "Report");
    }

    private void reportStream(String slug, String reason, int position) {
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
                            handleReportItem(true, position, Constants.HAS_BEEN_REPORT_POST);
                        } else {
                            ((BaseActivity) mActivity).handleError(reportDataResponse.getMessage(),
                                    reportDataResponse.getCode());
                        }
                    }
                }));

    }

    private void reportPost(String itemID, String reason, int position) {
        ReportRequestModel requestModel = new ReportRequestModel();
        requestModel.setMessage(reason);
        requestModel.setReport_for(itemID);
        mCompositeSubscription.add(AppsterWebServices.get().report(AppsterApplication.mAppPreferences.getUserTokenRequest(), requestModel)
                .subscribe(new Subscriber<BaseResponse<Boolean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onNext(BaseResponse<Boolean> reportDataResponse) {
                        if (reportDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && reportDataResponse.getData()) {
                            handleReportItem(false, position, Constants.HAS_BEEN_REPORT_POST);
                        } else {
                            ((BaseActivity) mActivity).handleError(reportDataResponse.getMessage(),
                                    reportDataResponse.getCode());
                        }
                    }
                }));

    }

    private void unReportStream(String Slug, int position) {
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
                        if (reportDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && reportDataResponse.getData()) {
                            handleReportItem(true, position, Constants.NOT_REPORT_POST);
                        } else {
                            ((BaseActivity) mActivity).handleError(reportDataResponse.getMessage(),
                                    reportDataResponse.getCode());
                        }
                    }
                }));

    }

    private void unReportPost(String itemID, int position) {
        ReportRequestModel requestModel = new ReportRequestModel();
        requestModel.setReport_for(itemID);
        mCompositeSubscription.add(AppsterWebServices.get().unReport(AppsterApplication.mAppPreferences.getUserTokenRequest(), requestModel)
                .subscribe(new Subscriber<BaseResponse<Boolean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onNext(BaseResponse<Boolean> reportDataResponse) {
                        if (reportDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK && reportDataResponse.getData()) {
                            handleReportItem(false, position, Constants.NOT_REPORT_POST);
                        } else {
                            ((BaseActivity) mActivity).handleError(reportDataResponse.getMessage(),
                                    reportDataResponse.getCode());
                        }
                    }
                }));
    }


    void handleReportItem(Boolean isStream, int position, int reportType) {
        if (arrayPost.get(position) != null) {
            ReportEvent reportEvent = new ReportEvent();
            reportEvent.setIsReport(reportType);
            if (isStream) {
                arrayPost.get(position).getStream().setIsReport(reportType);
                reportEvent.setStream(true);
                reportEvent.setSlug(arrayPost.get(position).getStream().getSlug());
            } else {
                arrayPost.get(position).getPost().setIsReport(reportType);
                reportEvent.setStream(false);
                reportEvent.setPostId(arrayPost.get(position).getPost().getPostId());
            }
            WallFeedManager.getInstance().reportEvent(reportEvent);
        }

    }

    void openLikedUserScreen(String postId, Boolean isStream, String slug) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(mActivity, R.anim.push_in_to_right, R.anim.push_in_to_left);
        ActivityCompat.startActivityForResult(mActivity, StreamLikedUsersActivity.createIntent(mActivity, Integer.parseInt(postId), isStream, slug),
                Constants.REQUEST_LIKED_USERS_LIST_ACTIVITY, options.toBundle());
    }

    private void userEarnPoints(@Constants.EARN_POINTS_SHARE_TYPE int mode, String streamSlug) {
        EarnPointsRequestEntity request = new EarnPointsRequestEntity("Stream", streamSlug, mode);
        mCompositeSubscription.add(AppsterWebServices.get().earnPoints(AppsterUtility.getAuth(), request)
                .filter(subStreamDataBaseResponse -> mActivity != null)
                .subscribe(response -> {
                    if (response != null) {
                        AppsterApplication.mAppPreferences.getUserModel().setPoints(response.getData().getUserPoints());
                        if (!StringUtil.isNullOrEmptyString(response.getData().getMessage()))
                            Toast.makeText(mActivity.getApplicationContext(), response.getData().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, Timber::e));

    }
}
