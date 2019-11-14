package com.appster.viewholder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.adapters.AdapterWallFeed;
import com.appster.comments.ItemClassComments;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.customview.autolinktextview.AutoLinkTextView;
import com.appster.customview.autolinktextview.AutoLinkUtil;
import com.appster.customview.autolinktextview.TouchableSpan;
import com.appster.interfaces.OnItemClickListenerRecyclerView;
import com.appster.interfaces.OnItemLongClickListenerRecyclerView;
import com.appster.layout.SquareImageView;
import com.appster.manager.VideosManager;
import com.appster.models.ListenerEventModel;
import com.appster.models.StreamModel;
import com.appster.models.StreamPublisherModel;
import com.appster.models.UserModel;
import com.appster.models.UserPostModel;
import com.appster.models.ViewVideosEvent;
import com.appster.newsfeed.ItemModelClassNewsFeed;
import com.appster.utility.AppsterUtility;
import com.appster.utility.ImageLoaderUtil;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.ViewVideosRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.VideosCountModel;
import com.apster.common.AnimationHelper;
import com.apster.common.CommonDefine;
import com.apster.common.Constants;
import com.apster.common.DownloadVideos;
import com.apster.common.FileUtility;
import com.apster.common.LogUtils;
import com.apster.common.Utils;
import com.apster.common.view.ExpandableTextView;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.pack.utility.StringUtil;
import com.stickyheaders.SectioningAdapter;

import java.util.List;
import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import timber.log.Timber;

import static com.appster.customview.autolinktextview.AutoLinkUtil.addAutoLinkMode;

/**
 * Created by sonnguyen on 11/16/16.
 */

public class WallFeedItemViewHolder extends SectioningAdapter.ItemViewHolder {

    public static final String TAG = WallFeedItemViewHolder.class.getSimpleName();

    @Bind(R.id.textureVideoView)
    TextureView textureVideoView;
    @Bind(R.id.rl_TextureVideoView)
    FrameLayout rlTextureVideoView;
    @Bind(R.id.mediaImage)
    public SquareImageView mediaImage;
    @Bind(R.id.play_video)
    public ImageView playVideo;
    //    @Bind(R.id.progressVideo)
//    ProgressBar progressVideo;
    @Bind(R.id.media_image_fl)
    FrameLayout mediaImageFl;
    @Bind(R.id.tv_title)
    AutoLinkTextView tvTitle;
    @Bind(R.id.tv_content_post)
    AutoLinkTextView tvContentPost;
    @Bind(R.id.imv_like)
    public ImageView imvLike;
    @Bind(R.id.vBgLike)
    View vBgLike;
    @Bind(R.id.ivLike)
    ImageView ivLike;
    @Bind(R.id.pblikeProgress)
    public ProgressBar pblikeProgress;
    @Bind(R.id.onOffVolume)
    ImageView onOffVolume;
    @Bind(R.id.fm_like)
    public FrameLayout linkeLayout;
    @Bind(R.id.ln_menu_dialog_button)
    ImageButton lnMenuDailog;
    @Bind(R.id.imgComment)
    ImageView imgComment;

    @Bind(R.id.imgShare)
    ImageView imgShare;


    @Bind(R.id.btn_live)
    Button btnLive;
    @Bind(R.id.tv_like_count)
    TextView tvLikeCount;
    @Bind(R.id.tv_view_count)
    TextView tv_ViewCount;
    @Bind(R.id.commentListLayout)
    LinearLayout commentListLayout;
    @Bind(R.id.tv_more_ll)
    TextView tv_ViewMoreLl;

    @Bind(R.id.imgLikeCount)
    ImageView imgLiKeCount;
    @Bind(R.id.imgViewCount)
    ImageView imgViewCount;

    @Bind(R.id.space_header)
    View space_header;


    UserPostModel model;
    Context mContext;
    //    VideoPlayerManager<MetaData> mVideoPlayerManager;
    VideosManager mVideoPlayerManager;
    AdapterWallFeed.ClickUserComment clickUserComment;
    OnItemClickListenerRecyclerView<UserPostModel> onItemClick;
    OnItemLongClickListenerRecyclerView<UserPostModel> onItemLongClick;
    UserModel userProfileDetails;
    int position;

    Timer timer;
    Subscription mSubscription;
    boolean isFirstPlay = true;

    long viewCount = 0;
    int commentCount = 0;
    int likeCount = 0;
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    private Surface mSurface;
    private KSYMediaPlayer mKsyMediaPlayer;

    Typeface mTypeFaceOpensansBold;
    private final Subject<Boolean, Boolean> mContentClickSubject;

    public WallFeedItemViewHolder(View view, Context context, VideosManager mVideoPlayerManager) {
        super(view);
        this.mContext = context;
        mContentClickSubject = PublishSubject.create();
        this.mVideoPlayerManager = mVideoPlayerManager;
        mTypeFaceOpensansBold = Typeface.create(Typeface.createFromAsset(mContext.getAssets(), "fonts/opensansbold.ttf"), Typeface.NORMAL);
        ButterKnife.bind(this, itemView);
        initPlayer();
    }


    private void initPlayer() {
        if (mKsyMediaPlayer != null) {
            mKsyMediaPlayer.stop();
            mKsyMediaPlayer.release();
            mKsyMediaPlayer = null;
//            mVideoPlayerManager.resetMediaPlayer();
        }
        mVideoPlayerManager.resetMediaPlayer();
        if (textureVideoView != null) {
            releaseSurface();
            mKsyMediaPlayer = new KSYMediaPlayer.Builder(mContext.getApplicationContext()).build();
            textureVideoView.setSurfaceTextureListener(mSurfaceTextureListener);
            powerOnSurfaceTextureAvailable();

            mKsyMediaPlayer.setTimeout(10, 30);
            mKsyMediaPlayer.setBufferTimeMax(2);
            mKsyMediaPlayer.setBufferSize(15);

            mKsyMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mKsyMediaPlayer.setOnInfoListener(mOnInfoListener);
            mKsyMediaPlayer.setOnErrorListener(mOnErrorListener);
            mKsyMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            muteUnMuteVideo(true);
        }
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

//            if (mKsyMediaPlayer != null)
//                mKsyMediaPlayer.setSurface(null);
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };

    private void releaseSurface() {
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
    }


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
                autoAnimationPlayVideo();
                if (mSurface != null) mKsyMediaPlayer.setSurface(mSurface);
//                mKsyMediaPlayer.seekTo(0);
//                mKsyMediaPlayer.start();
                break;
            case KSYMediaPlayer.MEDIA_INFO_RELOADED:
                Timber.e("======================Succeed to mPlayerReload video.");
                return false;

        }
        return false;
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = (mp, what, extra) -> {
        Timber.e("OnErrorListener, Error:" + what + ",extra:" + extra);
        switch (what) {
            case KSYMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Timber.e("MEDIA_ERROR_TIMED_OUT" + what + ",extra:" + extra);
                break;

//            case KSYMediaPlayer.MEDIA_ERROR_UNKNOWN:
//                handleShowEndLayout();
//
//                break;

            default:
        }

        return false;
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = mp -> {
        Timber.e("OnCompletionListener, play complete................");
        if (!model.isStreamItem() || !model.getStream().isStreamBeingLive()) {
            mKsyMediaPlayer.seekTo(0);
            mKsyMediaPlayer.start();
            handleInCreateViewCount();
        }
    };

    private void autoAnimationPlayVideo() {
        Animation fadeOut = AnimationUtils.loadAnimation(mContext.getApplicationContext(), R.anim.video_auto_fade_out);
        mediaImage.startAnimation(fadeOut);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mediaImage.setVisibility(View.GONE);
                //progressVideo.setVisibility(View.GONE);
                if (isFirstPlay) {
                    //onOffVolume.setVisibility(View.VISIBLE);
                    //muteUnMuteVideo();
//                    increateTimertaks();
                    handleInCreateViewCount();
                }
                isFirstPlay = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void onBindData(Context mContext, UserPostModel model, OnItemClickListenerRecyclerView<UserPostModel> onItemClick,
                           OnItemLongClickListenerRecyclerView<UserPostModel> onItemLongClick,
                           int position, AdapterWallFeed.ClickUserComment clickUserComment,
                           UserModel userProfileDetails) {
        // handle Item type Stream item
        this.mContext = mContext;
        this.model = model;
        this.clickUserComment = clickUserComment;
        this.onItemClick = onItemClick;
        this.position = position;
        String imagePostURL = "";
        this.userProfileDetails = userProfileDetails;
        this.onItemLongClick = onItemLongClick;

        if (model.isStreamItem()) {
            if (userProfileDetails != null) {
                model.getStream().setPublisher(new StreamPublisherModel(userProfileDetails));
            }
            if (model.getStream().isIsRecorded()) {
                handleRecordedStream(model.getStream(), onItemClick, position);
            } else {
                handleDataStream(model.getStream(), onItemClick, position);
            }
            if (!StringUtil.isNullOrEmptyString(model.getStream().getCoverImage())) {
                imagePostURL = model.getStream().getCoverImage();
            } else if (model.getStream().getPublisher() != null && model.getStream().getPublisher().getUserImage() != null && !model.getStream().getPublisher().getUserImage().isEmpty()) {
                imagePostURL = model.getStream().getPublisher().getUserImage();
            }
            viewCount = model.getStream().getViewCount();
            likeCount = model.getStream().getLikeCount();

        } else {
            addAutoLinkMode(tvTitle, tvContentPost);
            if (userProfileDetails != null) {
                model.getPost().setDisplayName(userProfileDetails.getDisplayName());
                model.getPost().setUserImage(userProfileDetails.getUserName());
                model.getPost().setUserImage(userProfileDetails.getUserImage());
                model.getPost().setIsFollow(userProfileDetails.getIsFollow());
                model.getPost().setUserId(userProfileDetails.getUserId());
            }
            handleDataPost(model.getPost(), onItemClick, position);
            viewCount = model.getPost().getViewCount();
            likeCount = model.getPost().getLikeCount();
            commentCount = model.getPost().getCommentCount();
        }
        this.model = model;
        // hanlde Comment and like
        if (model.getStream() != null && !StringUtil.isNullOrEmptyString(imagePostURL)) {
            textureVideoView.setVisibility(View.VISIBLE);
            rlTextureVideoView.setVisibility(View.VISIBLE);
            onOffVolume.setVisibility(View.VISIBLE);
            btnLive.setVisibility(View.VISIBLE);
            tvContentPost.setVisibility(View.GONE);
            //progressVideo.setVisibility(View.GONE);
        } else if (model.getPost() != null && model.getPost().getMediaType() == CommonDefine.TYPE_QUOTES) {
            textureVideoView.setVisibility(View.GONE);
            rlTextureVideoView.setVisibility(View.GONE);
            onOffVolume.setVisibility(View.GONE);
            btnLive.setVisibility(View.GONE);
            tvContentPost.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);
            tv_ViewCount.setVisibility(View.GONE);
            imgViewCount.setVisibility(View.GONE);
            //progressVideo.setVisibility(View.GONE);
            updateMediaViewHeightWidth(false);
        } else if (model.getPost() != null && model.getPost().getMediaType() == CommonDefine.TYPE_VIDEO) {
            textureVideoView.setVisibility(View.VISIBLE);
            muteUnMuteVideo(true);
            GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    muteUnMuteVideo();
                    return true;
                }

                // event when double tap occurs
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    linkeLayout.performClick();
                    return true;
                }
            });
            rlTextureVideoView.setOnTouchListener((v, event) -> {
                return gestureDetector.onTouchEvent(event);
            });

            rlTextureVideoView.setVisibility(View.VISIBLE);
            onOffVolume.setVisibility(View.VISIBLE);
            btnLive.setVisibility(View.VISIBLE);
            btnLive.setText(mContext.getString(R.string.videos_tag));
            btnLive.setBackground(ContextCompat.getDrawable(mContext, R.drawable.home_btn_recorded));
            tvContentPost.setVisibility(View.GONE);
            imagePostURL = model.getPost().getMediaImage();
            updateMediaViewHeightWidth(true);
        } else if (model.getPost() != null && model.getPost().getMediaType() == CommonDefine.TYPE_IMAGE) {
            //progressVideo.setVisibility(View.GONE);
            textureVideoView.setVisibility(View.GONE);
            rlTextureVideoView.setVisibility(View.GONE);
            onOffVolume.setVisibility(View.GONE);
            btnLive.setVisibility(View.GONE);
            tvContentPost.setVisibility(View.GONE);
            imagePostURL = model.getPost().getMediaImage();
            tv_ViewCount.setVisibility(View.GONE);
            imgViewCount.setVisibility(View.GONE);
            updateMediaViewHeightWidth(true);
        }

        if (StringUtil.isNullOrEmptyString(imagePostURL)) {
            mediaImage.setVisibility(View.GONE);
        } else {
            mediaImage.setVisibility(View.VISIBLE);
            ImageLoaderUtil.displayUserImage(mContext, imagePostURL, mediaImage);
        }

        // For Share Click All The same behavior

        imgShare.setOnClickListener(v -> onItemClick.OnclickItem(imgShare, model, WallFeedItemViewHolder.this, position));

        // for Click Dialog option

        lnMenuDailog.setOnClickListener(v -> {
            AppsterUtility.temporaryLockView(v);
            onItemClick.OnclickItem(lnMenuDailog, model, WallFeedItemViewHolder.this, position);
        });
        onOffVolume.setBackgroundResource(R.drawable.volume_off);
        onOffVolume.setOnClickListener(v -> muteUnMuteVideo());

        if (commentCount > 0) {
            space_header.setVisibility(View.GONE);
        } else {
            space_header.setVisibility(View.VISIBLE);
        }


    }

    private void handleDataStream(StreamModel stream, OnItemClickListenerRecyclerView<UserPostModel> onItemClick, int position) {
        if (stream == null) {
            return;
        }

        linkeLayout.setVisibility(View.GONE);
        imgComment.setVisibility(View.GONE);
        commentListLayout.setVisibility(View.GONE);
        // For Count view
        long countview = stream.getViewCount();
        handleViewCountView(countview);


        // For Like count
        int countLike = stream.getLikeCount();
        handleLikeCountView(countLike);


        // For status button
//            if (stream.isStreamBeingLive()) {
        btnLive.setText(mContext.getString(R.string.streaming_live));
        btnLive.setBackground(ContextCompat.getDrawable(mContext, R.drawable.home_btn_live));
        onOffVolume.setVisibility(View.VISIBLE);
//            } else {
//                btnLive.setText(mContext.getString(R.string.streaming_recorded));
//                btnLive.setBackground(ContextCompat.getDrawable(mContext, R.drawable.home_btn_recorded));
//                onOffVolume.setVisibility(View.VISIBLE);
//            }

        tv_ViewMoreLl.setVisibility(View.GONE);


        mediaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.OnclickItem(mediaImage, model, WallFeedItemViewHolder.this, position);
            }
        });
        // for handle in post view it touch return true click event can not execude
        mediaImage.setOnTouchListener((v, event) -> false);

        if (!StringUtil.isNullOrEmptyString(stream.getTitle())) {
            String formattedTitle = StringUtil.decodeString(stream.getTitle());
            tvTitle.setVisibility(View.VISIBLE);
            setTitle(formattedTitle, position);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        rlTextureVideoView.setOnClickListener(v -> mediaImage.performClick());

        updateMediaViewHeightWidth(true);

    }

    private void handleRecordedStream(StreamModel stream, OnItemClickListenerRecyclerView<UserPostModel> onItemClick, int position) {
        if (stream == null) {
            return;
        }
        linkeLayout.setVisibility(View.GONE);
        imgComment.setVisibility(View.VISIBLE);
        imgComment.setOnClickListener(v -> onItemClick.OnclickItem(imgComment, model, WallFeedItemViewHolder.this, position));


        // For Count view
        long countview = stream.getViewCount();
        handleViewCountView(countview);


        // For Like count
        int countLike = stream.getLikeCount();
        handleLikeCountView(countLike);


        // For status button
        btnLive.setText(mContext.getString(R.string.streaming_recorded));
        btnLive.setBackground(ContextCompat.getDrawable(mContext, R.drawable.home_btn_recorded));
        //temp solution for xmas theme
        onOffVolume.setVisibility(View.VISIBLE);

        tv_ViewMoreLl.setVisibility(View.VISIBLE);


        mediaImage.setOnClickListener(v -> onItemClick.OnclickItem(mediaImage, model, WallFeedItemViewHolder.this, position));
        // for handle in post view it touch return true click event can not execude
        mediaImage.setOnTouchListener((v, event) -> false);

        if (!StringUtil.isNullOrEmptyString(stream.getTitle())) {
            tvTitle.setVisibility(View.VISIBLE);
            String formattedTitle = StringUtil.decodeString(stream.getTitle());
            setTitle(formattedTitle, position);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        rlTextureVideoView.setOnClickListener(v -> mediaImage.performClick());

        //comment section
        clearCommentList();
        setViewMoreView(stream.getTotalCommentCount());
        bindCommentList(stream.getCommentList());
        tv_ViewMoreLl.setOnClickListener(v -> imgComment.performClick());
        updateMediaViewHeightWidth(true);
    }

    private void handleDataPost(ItemModelClassNewsFeed newfeed, OnItemClickListenerRecyclerView<UserPostModel> onItemClick, int position) {
        int mediaType = newfeed.getMediaType();
        linkeLayout.setVisibility(View.VISIBLE);
        imgComment.setVisibility(View.VISIBLE);

        if (newfeed.getIsLike() == CommonDefine.NEWS_FEED_LIKE) {
            imvLike.setImageResource(R.drawable.ic_heart_like_25dp_selected);
        } else {
            imvLike.setImageResource(R.drawable.ic_heart_like_25dp_default);
        }
        linkeLayout.setOnClickListener(v -> onItemClick.OnclickItem(linkeLayout, model, WallFeedItemViewHolder.this, position));

        // For Like Count
        tvLikeCount.setVisibility(View.GONE);
        int countLike = newfeed.getLikeCount();
        handleLikeCountView(countLike);

        // For View Count
        handleViewCountView(newfeed.getViewCount());

        //comment section
        clearCommentList();
        setViewMoreView(newfeed.getCommentCount());
        bindCommentList(newfeed.getCommentList());

        imgComment.setOnClickListener(v -> onItemClick.OnclickItem(imgComment, model, WallFeedItemViewHolder.this, position));
        tv_ViewMoreLl.setOnClickListener(v -> imgComment.performClick());


        if (!StringUtil.isNullOrEmptyString(newfeed
                .getTitle())) {
            tvTitle.setVisibility(View.VISIBLE);
            String title = StringUtil.decodeString(newfeed.getTitle());
            setTitleWithAutoLinkText(title, position);
            setContentPost(title, position);

        } else {
            tvTitle.setVisibility(View.GONE);
        }

        if (mediaType == CommonDefine.TYPE_QUOTES) {
            tvTitle.setVisibility(View.GONE);
        }

        GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                boolean result = false;
                if (mediaType == CommonDefine.TYPE_VIDEO) {
                    muteUnMuteVideo();
                    result = true;
                }
                return result;
            }

            // event when double tap occurs
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                linkeLayout.performClick();
                return true;
            }
        });
        mediaImage.setOnTouchListener((v, event) -> {
            return gestureDetector.onTouchEvent(event);
        });
    }

    private void bindCommentList(List<ItemClassComments> commentList) {
        int commentListSize = commentList.size();
        if (commentListSize <= 0) {
            commentListLayout.setVisibility(View.GONE);
            return;
        }

        commentListLayout.setVisibility(View.VISIBLE);

        int firstPositionIsShown = 0;
        if (commentListSize > Constants.NUMBER_COMMENT_SHOW) {
            firstPositionIsShown = commentListSize - Constants.NUMBER_COMMENT_SHOW;
        }
        for (int i = firstPositionIsShown; i < commentListSize && i >= 0; i++) {
            final ItemClassComments comment = commentList.get(i);
            final ExpandableTextView tvCommentUserName = (ExpandableTextView) LayoutInflater.from(mContext).inflate(R.layout.comment_newfeed_row, null);
            AutoLinkUtil.addAutoLinkMode(tvCommentUserName);
            tvCommentUserName.setAutoLinkOnClickListener(AutoLinkUtil.newListener((BaseActivity) mContext));
            String contentShow = StringUtil.decodeString(comment.getNameShowUI()) + " " + StringUtil.decodeString(comment.getMessage());
            int start = contentShow.indexOf(comment.getNameShowUI());
            int end = contentShow.indexOf(comment.getNameShowUI()) + comment.getNameShowUI().length();
            TouchableSpan clickUSerName = new TouchableSpan() {
                @Override
                public void onClick(View widget) {
                    clickUserComment.onClickComment(comment.getUser_id(), comment.getNameShowUI());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#58585B"));
                }
            };

//            TouchableSpan clickComment = new TouchableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    imgComment.performClick();
//                }
//
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setColor(Color.parseColor("#000000"));
//                }
//            };
            SpannableString commentsContentSpan = new SpannableString(contentShow);
            commentsContentSpan.setSpan(new RelativeSizeSpan(1.0f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            commentsContentSpan.setSpan(new CustomTypefaceSpan("sans-serif", mTypeFaceOpensansBold), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            commentsContentSpan.setSpan(clickUSerName, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            commentsContentSpan.setSpan(clickComment, end, contentShow.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvCommentUserName.setAutoLinkText(commentsContentSpan);
            tvCommentUserName.setOnClickListener(v -> imgComment.performClick());
            commentListLayout.addView(tvCommentUserName);
        }

    }

    private void clearCommentList() {
        if (commentListLayout.getChildCount() > 0) {
            commentListLayout.removeAllViews();
        }
    }

    private void setViewMoreView(int totalCommentCount) {
        if (totalCommentCount > Constants.NUMBER_COMMENT_SHOW) {
            tv_ViewMoreLl.setText(mContext.getString(R.string.view_all_for, String.valueOf(totalCommentCount)));
            tv_ViewMoreLl.setVisibility(View.VISIBLE);
            tv_ViewMoreLl.setOnClickListener(v -> onItemClick.OnclickItem(itemView, model, WallFeedItemViewHolder.this, position));
        } else {
            tv_ViewMoreLl.setVisibility(View.GONE);
        }
    }

    private void setTitle(String formattedTitle, int position) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(formattedTitle);
    }

    private void setTitleWithAutoLinkText(String formattedTitle, int position) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setAutoLinkText(formattedTitle);
        tvTitle.setAutoLinkOnClickListener(AutoLinkUtil.newListener((BaseActivity) mContext));
        tvTitle.setOnLongClickListener(v -> {
            if (onItemLongClick != null) {
                onItemLongClick.OnLongClickItem(tvTitle, model, WallFeedItemViewHolder.this, position);
            }
            return true;
        });
    }

    private void setContentPost(String formattedTitle, int position) {
        tvContentPost.setAutoLinkText(formattedTitle);
        tvContentPost.setAutoLinkOnClickListener(AutoLinkUtil.newListener((BaseActivity) mContext));
        tvContentPost.setOnLongClickListener(v -> {
            if (onItemLongClick != null) {
                onItemLongClick.OnLongClickItem(tvContentPost, model, WallFeedItemViewHolder.this, position);
            }
            return true;
        });

    }

    private void initVideoView(String urlVideo) {
        if (textureVideoView.isAttachedToWindow()) {
//            onOffVolume.setVisibility(View.VISIBLE);
//            AppsterApplication.mAppPreferences.setIsMuteVideos(true);
//            textureVideoView.toggleMute(true);

//            Log.d(TAG, "debug playVideos start loading -----" + urlVideo);
            //mVideoPlayerManager.playVideos(textureVideoView, urlVideo, mediaPlayerCallback);

            initPlayer();
            mVideoPlayerManager.playVideos(mKsyMediaPlayer, urlVideo, () -> {
                if (mediaImage != null) {
                    mediaImage.setVisibility(View.VISIBLE);
                }
            });

        }
    }

    private Subscription mSubscriptionInCreateViewCount;

    private void handleInCreateViewCount() {
        if (model.isStreamItem()) {
            return;
        }

        RxUtils.unsubscribeIfNotNull(mSubscriptionInCreateViewCount);
        mSubscriptionInCreateViewCount = null;
        mSubscriptionInCreateViewCount = AppsterWebServices.get().viewVideos(AppsterApplication.mAppPreferences.getUserTokenRequest(), new ViewVideosRequestModel(model.getPost().getPostId()))
                .subscribe(new Subscriber<BaseResponse<VideosCountModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onNext(BaseResponse<VideosCountModel> viewVideosCountResponse) {
                        if (viewVideosCountResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                            long countView = viewVideosCountResponse.getData().mViewCount;
                            model.getPost().setViewCount(countView);
                            if (countView <= 0) {
                                tv_ViewCount.setVisibility(View.GONE);
                                imgViewCount.setVisibility(View.GONE);
                            } else {
                                tv_ViewCount.setVisibility(View.VISIBLE);
                                imgViewCount.setVisibility(View.VISIBLE);
                                String likeCountString = "";
                                if (countView > 1) {
                                    likeCountString = String.format(mContext.getString(R.string.views_count), StringUtil.handleThousandNumber(countView));
                                } else {
                                    likeCountString = String.format(mContext.getString(R.string.view_count), countView + "");
                                }
                                tv_ViewCount.setText(likeCountString);
                            }
                            createViewCountEvent((int) countView);
                            model.getPost().setViewCount(countView);
//                            WallFeedItemViewHolder.this.notify();
                            Timber.e("countView " + countView);
                        }
                    }
                });
    }

    private void createViewCountEvent(int viewCount) {
        if (!model.isStreamItem()) {
            ViewVideosEvent viewVideosEvent = new ViewVideosEvent();
            viewVideosEvent.setPostId(model.getPost().getPostId());
            viewVideosEvent.setViewCount(viewCount);
            ListenerEventModel listenerEventModel = new ListenerEventModel();
            listenerEventModel.setTypeEvent(ListenerEventModel.TypeEvent.EVENT_VIEW_VIDEOS);
            listenerEventModel.setViewVideosEvent(viewVideosEvent);
            ((BaseActivity) mContext).eventChange(listenerEventModel);
        }

    }

    public void muteUnMuteVideo() {
        muteUnMuteVideo(null);
    }

    public void muteUnMuteVideo(Boolean isMute) {
        if (isMute == null) {
            isMute = AppsterApplication.mAppPreferences.getIsMuteVideos();
        }

//        if (isMute) {
        if (mKsyMediaPlayer != null) {
            mKsyMediaPlayer.setPlayerMute(isMute ? 1 : 0);
        }
        onOffVolume.setBackgroundResource(isMute ? R.drawable.volume_off : R.drawable.volume_on);
        AppsterApplication.mAppPreferences.setIsMuteVideos(!isMute);
//        } else {
//            mKsyMediaPlayer.setPlayerMute(0);
//            onOffVolume.setBackgroundResource(R.drawable.volume_on);
//            AppsterApplication.mAppPreferences.setIsMuteVideos(true);
//        }
    }

    private void handleVideoLoading(final String videosServerURL, WallFeedItemViewHolder wallFeedItemViewHolder) {
        LogUtils.logV("NCS", " 2.handleVideoLoading");
        FileUtility.deleteVideoCacheFile();
        DownloadVideos.getInstance().isVideoAlreadyDownloaded(videosServerURL, new DownloadVideos.IFileAlreadyDownloadedListener() {

            @Override
            public void needToDownload(final boolean isNeedToDownload, final String fileName) {
                ((Activity) mContext).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (!isNeedToDownload) {
                            wallFeedItemViewHolder.initVideoView(fileName);
                        } else {

                            DownloadVideos.getInstance().downloadVideoFile(videosServerURL, new DownloadVideos.IDownloadListener() {
                                @Override
                                public void successful(final String filePath) {
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            wallFeedItemViewHolder.initVideoView(filePath);
                                        }
                                    });
                                }

                                @Override
                                public void fail() {
                                    String ui = "";
                                }
                            });
                        }
                    }
                });
            }
        });

    }

    public UserPostModel getPostData() {
        return model;
    }

    public void handleLikeCountView(int countLike) {
        if (countLike <= 0) {
            tvLikeCount.setVisibility(View.GONE);
            imgLiKeCount.setVisibility(View.GONE);
        } else {
            String likeCountString = "";
            tvLikeCount.setVisibility(View.VISIBLE);
            imgLiKeCount.setVisibility(View.VISIBLE);
            if (countLike > 1) {
                likeCountString = String.format(mContext.getString(R.string.likes_count), Utils.shortenNumber(countLike));
            } else {
                likeCountString = String.format(mContext.getString(R.string.like_count), countLike + "");
            }
            tvLikeCount.setText(likeCountString);
            tvLikeCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.OnclickItem(tvLikeCount, model, WallFeedItemViewHolder.this, position);
                }
            });
        }
    }

    public void handleViewCountView(long totalViewCount) {
        if (totalViewCount <= 0) {
            tv_ViewCount.setVisibility(View.GONE);
            imgViewCount.setVisibility(View.GONE);
        } else {
            tv_ViewCount.setVisibility(View.VISIBLE);
            imgViewCount.setVisibility(View.VISIBLE);
            String likeCountString;
            if (totalViewCount > 1) {
                likeCountString = String.format(mContext.getString(R.string.views_count), Utils.shortenNumber(totalViewCount));
            } else {
                likeCountString = String.format(mContext.getString(R.string.view_count), String.valueOf(totalViewCount));
            }
            tv_ViewCount.setText(likeCountString);
        }
    }

    public void autoPlayVideo(String url) {
        isFirstPlay = true;
        playVideo.setVisibility(View.GONE);
        textureVideoView.setVisibility(View.VISIBLE);
        //progressVideo.setVisibility(View.VISIBLE);
//        mediaImage.setClickable(false);
//        onOffVolume.setVisibility(View.GONE);
//        handleVideoLoading(url);
        if (model.isStreamItem()) {
            initVideoView(url);
        } else {
            handleVideoLoading(url, this);
        }

    }

    public void stopThreadIncreateViewCount() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        RxUtils.unsubscribeIfNotNull(mSubscription);
        Timber.d("stop timer which call api each 5s");
    }

    private void updateMediaViewHeightWidth(boolean isMedia) {
//        int moreWidth = 100;
        if (isMedia) {
            mediaImageFl.setVisibility(View.VISIBLE);
            tvContentPost.setVisibility(View.GONE);
//            LinearLayout.LayoutParams frame = (LinearLayout.LayoutParams) mediaImageFl.getLayoutParams();
//            frame.width = AppsterApplication.mAppPreferences.getScreenWidth() + moreWidth;
//            frame.height = AppsterApplication.mAppPreferences.getScreenWidth();
//            mediaImageFl.setLayoutParams(frame);
//            FrameLayout.LayoutParams frameParrent = (FrameLayout.LayoutParams) rlTextureVideoView.getLayoutParams();
//            frameParrent.width = AppsterApplication.mAppPreferences.getScreenWidth() + moreWidth;
//            frameParrent.height = AppsterApplication.mAppPreferences.getScreenWidth();
//            rlTextureVideoView.setLayoutParams(frameParrent);
//
//            FrameLayout.LayoutParams lpc = (FrameLayout.LayoutParams) textureVideoView.getLayoutParams();
//            lpc.width = AppsterApplication.mAppPreferences.getScreenWidth() + moreWidth;
//            lpc.height = AppsterApplication.mAppPreferences.getScreenWidth();
//            textureVideoView.setLayoutParams(lpc);
        } else {
            mediaImageFl.setVisibility(View.GONE);
            tvContentPost.setVisibility(View.VISIBLE);
        }
    }


    public void animateLikeAction() {
        //only show like animation if this is an image item.
        if (model.getPost().getMediaType() != CommonDefine.TYPE_IMAGE) {
            return;
        }
        AnimationHelper.animateLikeAction(vBgLike, ivLike);
    }

    boolean isRecord() {
        return model != null && model.isStreamItem();
    }
}
