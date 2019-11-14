package com.appster.features.friend_suggestion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.StringRes;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.recyclerview.LoadMoreRecyclerView;
import com.appster.customview.CustomFontButton;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.data.AppPreferences;
import com.appster.domain.FriendSuggestionModel;
import com.appster.features.friend_suggestion.adapter.FriendOnBeliveAdapter;
import com.appster.features.friend_suggestion.adapter.SuggestedFriendListAdapter;
import com.appster.features.friend_suggestion.viewholder.FriendOnBeliveViewHolder;
import com.appster.features.friend_suggestion.viewholder.SuggestedFriendViewHolder;
import com.appster.features.social_invite_friend.SocialInviteFriendActivity;
import com.appster.features.user_profile.DialogUserProfileFragment;
import com.appster.interfaces.OnLoadMoreListenerRecyclerView;
import com.appster.interfaces.OnSetFollowUserListener;
import com.appster.layout.WrapContentHeightViewPager;
import com.appster.main.MainActivity;
import com.appster.models.FollowUser;
import com.appster.models.SearchModel;
import com.appster.models.UserModel;
import com.appster.tracking.EventTracker;
import com.appster.tracking.EventTrackingName;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.SocialManager;
import com.appster.utility.SpannableUtil;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.Constants;
import com.apster.common.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.appster.features.user_profile.DialogUserProfileFragment.USER_DIALOG_TAG;
import static com.apster.common.Constants.IS_FOLLOWING_USER;

public class FriendSuggestionActivity extends BaseActivity implements FriendSuggestionContract.View,
        SocialManager.OnGetFBTokenListener,
        FriendOnBeliveViewHolder.FriendOnBeLiveViewHolderListener,
        OnLoadMoreListenerRecyclerView,
        SuggestedFriendViewHolder.OnClickListener {
    static double LINE_HEIGHT = 1.2;
    static int mInviteTextLineNum = 1;
    @Bind(R.id.iBtnLeftToolbar)
    ImageButton mIBtnLeftToolbar;
    @Bind(R.id.pageTitle)
    CustomFontTextView mTxtToolbarTitle;
    @Bind(R.id.btn_right_toolbar)
    CustomFontButton mBtnRightToolbar;
    @Bind(R.id.txt_friend_invitation_suggestion)
    CustomFontTextView mTxtInvitationSuggestion;
    //    @Bind(R.id.rcv_friend_on_belive)
//    RecyclerView mRcvSuggestedFriend;
    @Bind(R.id.rcv_friend_suggestion)
    LoadMoreRecyclerView mRcvFriendOnBelive;
    @Bind(R.id.flFriendOnBeLiveContainer)
    LinearLayout flFriendOnBeLiveContainer;
    @Bind(R.id.tv_follow_all)
    CustomFontTextView tvFollowAll;

    FriendSuggestionContract.UserActions mPresenter;
    FriendOnBeliveAdapter mFriendOnBeliveAdapter;
    SuggestedFriendListAdapter mSuggestedAdapter;
    String facebookToken;

    List<DisplayableItem> mSuggestedList;
    List<DisplayableItem> mFriendListOnBelive;

    UserModel userInfoModel;

    //pager
//    @Bind(R.id.vpRecommended)
    WrapContentHeightViewPager vpRecommended;
    @Bind(R.id.llCountDots)
    LinearLayout llCountDots;
    ImageView[] dots;

    int dotsCount;
    boolean isSkipButtonClicked;

    RecommendedPagerAdapter mRecommendedPagerAdapter;
    @Bind(R.id.rcv_friend_on_belive)
    RecyclerView rcvFriendOnBelive;

    // page change listener
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    /**
     * one of
     * {@link ConstantBundleKey}
     */
    String loginFrom;
    String mListUserIdsRecommended = "";
    String mListUserIdsFriendOnBilive = "";
    private AtomicBoolean mIsAbleFollowAllFriendFacebook = new AtomicBoolean(true);
    private SpacesItemDecoration mSpacesItemDecoration;

    public static Intent createIntent(Context context, String loginVia) {
        Intent intent = new Intent(context, FriendSuggestionActivity.class);
        intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_FROM, loginVia);
        return intent;
    }

    //region-------activity life cycle-------
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_suggestion);
        ButterKnife.bind(this);
        flFriendOnBeLiveContainer.setVisibility(View.GONE);
//        disableScrollParent();
        loginFrom = getIntent().getStringExtra(ConstantBundleKey.BUNDLE_LOGIN_FROM);

        mPresenter = new FriendSuggestionPresenter(AppsterWebServices.get());
        mPresenter.attachView(this);
        mPresenter.getAppConfigFromServer();
        setupToolbar();
        initModelLists();
        setupRecyclerView();
        AppPreferences myAppPreferences = new AppPreferences(this);
        userInfoModel = myAppPreferences.getUserModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (mPresenter != null) {
            mPresenter.setIsEndFriendListOnBelive(false);
            mPresenter.getSuggestedFriend();
            getFacebookTokenAndRequestFriendList();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mPresenter.detachView();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_SOCIAL_INVITE_FRIEND_ACTIVITY:
                mIsAbleFollowAllFriendFacebook.set(false);
                break;
            default:
                break;
        }
    }

    //endregion -------activity life cycle-------

    //region -------inheritance methods-------

    //endregion -------inheritance methods-------

    //region -------implement methods-------
    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void loadError(String errorMessage, int code) {

    }

    @Override
    public void showProgress() {
        showDialog(this, getResources().getString(R.string.connecting_msg));
    }

    @Override
    public void hideProgress() {
        dismisDialog();
    }

    @OnClick(R.id.btn_invite_friend)
    public void onInviteButtonClicked() {
        if (isActivityRunning())
            startActivityForResult(SocialInviteFriendActivity.createIntent(this), Constants.REQUEST_SOCIAL_INVITE_FRIEND_ACTIVITY);
    }

    @OnClick(R.id.btn_start)
    public void onStartButtonClicked() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ConstantBundleKey.BUNDLE_TEXT_FOR_NEWLY_USER, true);
        startActivity(intent, options.toBundle());
        AppsterApplication.mAppPreferences.setReferralId("");
        EventTracker.trackEvent(EventTrackingName.EVENT_FRIEND_SUGGESTION_START);
        finish();
    }

    @OnClick(R.id.tv_follow_all)
    public void followAllUsers() {
        if (getString(R.string.recommended_unfollow_all).equals(tvFollowAll.getText().toString())) {
            mPresenter.unFollowAllUsers(mListUserIdsRecommended);
        } else {
            mPresenter.followAllUsers(mListUserIdsRecommended, false);
        }
    }

    @OnClick(R.id.btn_right_toolbar)
    public void onSkipButtonClicked() {
        isSkipButtonClicked = true;
        String str = mListUserIdsRecommended + mListUserIdsFriendOnBilive;
        mPresenter.unFollowAllUsers(str);
    }

    @Override
    public void onGetFriendListOnBeliveSucessfully(List<FriendSuggestionModel> friendListOnBelive, boolean isRefresh) {
        Timber.e("friend list current size = " + mFriendListOnBelive.size());
        mFriendOnBeliveAdapter.removeLoadingItem();
        if (isRefresh) {
            mFriendListOnBelive.clear();
        }

        if (!friendListOnBelive.isEmpty()) {
            if (mIsAbleFollowAllFriendFacebook.get()) {
                for (int i = 0; i < friendListOnBelive.size(); i++) {
                    friendListOnBelive.get(i).setIsFollow(Constants.IS_FOLLOWING_USER);
                    mListUserIdsFriendOnBilive += friendListOnBelive.get(i).getUserId() + ",";
                }
            }
            mFriendListOnBelive.addAll(friendListOnBelive);
            flFriendOnBeLiveContainer.setVisibility(View.VISIBLE);
        }

        if (mFriendListOnBelive.isEmpty()) {
            mFriendListOnBelive.add(new EmptyModel());
        }
        setHeightRecyclerView();
        mFriendOnBeliveAdapter.updateItems(mFriendListOnBelive);
        mRcvFriendOnBelive.setLoading(false);
        if (mIsAbleFollowAllFriendFacebook.get()) {
            mPresenter.followAllUsers(mListUserIdsFriendOnBilive, true);
        }
        mIsAbleFollowAllFriendFacebook.set(true);
    }

    @Override
    public void onGetFriendListOnBeliveSucessError() {
        mRcvFriendOnBelive.setLoading(false);
    }

    @Override
    public void onGetSuggestedFriendSuccessfully(List<SearchModel> suggest) {
        if (suggest != null && !suggest.isEmpty()) {
            for (int i = 0; i < suggest.size(); i++) {
                suggest.get(i).setIsFollow(Constants.IS_FOLLOWING_USER);
                addUserIdToList(suggest.get(i).getUserId());
            }
            mSuggestedList.clear();
            mSuggestedList.addAll(suggest);
        }

        showRecommendedList();
//        setViewPagerItemsWithAdapter(mSuggestedFriendList);
        setViewpagerIndicator();
//        mSuggestedFriendAdapter.setItems(mSuggestedFriendList);
//        mSuggestedFriendAdapter.notifyDataSetChanged();
        mPresenter.followAllUsers(mListUserIdsRecommended, true);
    }

    @Override
    public void onGetSuggestedFriendError() {
//        setupInvitationSuggestion("");
    }

    @Override
    public void onGetAppConfigSuccessfully(CharSequence formattedRewardMessage) {
        //don't need for this screen anymore
//        setupInvitationSuggestion(formattedRewardMessage);
    }

    @Override
    public void onGetAppConfigFailed() {

    }

    @Override
    public void onFollowAllUsersSuccess() {
        changeFollowStatusAllUsers(Constants.IS_FOLLOWING_USER);
        tvFollowAll.setText(R.string.recommended_unfollow_all);
        tvFollowAll.setTextColor(Color.parseColor("#9b9b9b"));
        changeFollowStatusFriendListOnBelive();
    }

    @Override
    public void onFollowAllUsersFail(String errorMessage, int errorCode) {
        handleError(errorMessage, errorCode);
    }

    @Override
    public void onUnfollowAllUsersSuccess() {
        changeFollowStatusAllUsers(Constants.UN_FOLLOW_USER);
        tvFollowAll.setText(R.string.recommended_follow_all);
        tvFollowAll.setTextColor(Color.parseColor("#ff5167"));
        changeFollowStatusFriendListOnBelive();
        if (isSkipButtonClicked) {
            onStartButtonClicked();
        }
    }

    @Override
    public void onUnfollowAllUsersFail(String errorMessage, int errorCode) {
        handleError(errorMessage, errorCode);
        if (isSkipButtonClicked) {
            onSkipButtonClicked();
        }
    }

    private void setHeightRecyclerView() {
        if (mFriendListOnBelive != null && !mFriendListOnBelive.isEmpty()) {
            if (mFriendListOnBelive.size() <= 4) {
                LinearLayout.LayoutParams params = new
                        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                mRcvFriendOnBelive.setLayoutParams(params);
            }
        }
    }

    private void showRecommendedList() {
        mSuggestedAdapter = new SuggestedFriendListAdapter(mSuggestedList, this);
        if (mSpacesItemDecoration == null) {
            mSpacesItemDecoration = new SpacesItemDecoration(Utils.dpToPx(15));
            rcvFriendOnBelive.addItemDecoration(mSpacesItemDecoration);
        }
        rcvFriendOnBelive.setAdapter(mSuggestedAdapter);
        rcvFriendOnBelive.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
    }

    private void changeFollowStatusFriendListOnBelive() {
        if (mSuggestedList != null
                && !mSuggestedList.isEmpty()
                && mFriendListOnBelive != null
                && !mFriendListOnBelive.isEmpty()) {
            mCompositeSubscription.add(Observable.from(mSuggestedList)
                    .map(displayableItemObservable -> (SearchModel) displayableItemObservable)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(friendSuggestionModel -> {
                        for (int i = 0; i < mFriendListOnBelive.size(); i++) {
                            FriendSuggestionModel suggestionModel = (FriendSuggestionModel) mFriendListOnBelive.get(i);
                            if (suggestionModel.getUserId().equals(friendSuggestionModel.getUserId())) {
                                ((FriendSuggestionModel) mFriendListOnBelive.get(i)).setIsFollow(friendSuggestionModel.getIsFollow());
                                mFriendOnBeliveAdapter.notifyItemChanged(mFriendListOnBelive.indexOf(suggestionModel));
                            }
                        }
                    }, Timber::e));
        }
    }

    private void changeFollowStatusAllUsers(int followType) {
        if (mSuggestedList != null && !mSuggestedList.isEmpty() && mSuggestedAdapter != null) {
            mCompositeSubscription.add(Observable.from(mSuggestedList)
                    .map(displayableItemObservable -> (SearchModel) displayableItemObservable)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(friendSuggestionModel -> {
                        friendSuggestionModel.setIsFollow(followType);
                        mSuggestedAdapter.notifyDataSetChanged();
//                        mSuggestedAdapter.notifyItemChanged(mSuggestedList.indexOf(friendSuggestionModel));
                    }, Timber::e));
        }
    }

    public static CharSequence replaceGemIcon(Context context, String source) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder(source);
        Pattern pattern = Pattern.compile(":gem:");
        Matcher matcher = pattern.matcher(source);

        Bitmap gem = null;
        int sizeH = Utils.dpToPx(13);//(int) (-tvInviteFriends.getPaint().ascent());
        int sizeW = Utils.dpToPx(9);
        while (matcher.find()) {
            if (gem == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_gem);
                gem = Bitmap.createScaledBitmap(bitmap, sizeW, sizeH, true);
                bitmap.recycle();
            }
            ImageSpan span = new ImageSpan(context, gem, ImageSpan.ALIGN_BOTTOM) {
                @Override
                public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                    Drawable b = getDrawable();
                    canvas.save();

                    int transY = bottom - b.getBounds().bottom;
                    // this is the key
                    transY -= paint.getFontMetricsInt().descent / 2;

                    // adjust it for the current line height
                    transY *= 1 - (LINE_HEIGHT - 1) * 2 / mInviteTextLineNum;

                    canvas.translate(x, transY);
                    b.draw(canvas);
                    canvas.restore();
                }
            };
            spannableString.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //remove text holder
        return makeBoldText(context, spannableString.subSequence(0, spannableString.length()));
//        return spannableString;
    }

    /**
     * make the text which between text holder ** ** bold.
     */
    private static CharSequence makeBoldText(Context context, CharSequence source) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(source);
//        Pattern boldPattern = Pattern.compile("(?<=\\*\\*)(.*)(?=\\*\\*)");
        Pattern boldPattern = Pattern.compile("(\\*\\*)(.+?)(\\*\\*)");
        Matcher boldMatcher = boldPattern.matcher(source);

        int indexDecreaseHop = 0;
        while (boldMatcher.find()) {//Get your friends inboard BeLive and BOTH of you will receive :gem: **100**
            int start = boldMatcher.start();
            int end = boldMatcher.end();
            String text = boldMatcher.group();
            String boldText = text.substring(2, text.length() - 2);
            spannableStringBuilder.replace(start - indexDecreaseHop, end - indexDecreaseHop, boldText);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_58585b)), start - indexDecreaseHop, end - indexDecreaseHop - 4, 0);
            spannableStringBuilder.setSpan(new CustomTypefaceSpan("", Typeface.createFromAsset(context.getAssets(), "fonts/helveticabold.ttf")), start - indexDecreaseHop, end - indexDecreaseHop - 4, 0);
            indexDecreaseHop += 4;
        }
        return spannableStringBuilder.subSequence(0, spannableStringBuilder.length());

    }

    private void addUserIdToList(String user) {
        mListUserIdsRecommended = mListUserIdsRecommended + user + ",";
    }

    //facebook
    @Override
    public void onSuccess(String accessToken) {
        facebookToken = accessToken;
        mPresenter.getFriendListOnBelive(facebookToken, false);
        Timber.d("facebook token %s", facebookToken);
    }

    @Override
    public void onFailed() {

    }
    //end facebook

    @Override
    public void onFollowButtonClicked(View v, FriendSuggestionModel userItemModel, int position) {
//        if (userItemModel.getIsFollow() == IS_FOLLOWING_USER) {
//            return;
//        }

        updateFollowStatus(userItemModel.getUserId(), userItemModel.getIsFollow() != IS_FOLLOWING_USER);
    }

    private void updateFollowStatus(final String userId, boolean isFollow) {
        showProgress();
        FollowUser followUser = new FollowUser(this, userId, isFollow);
        followUser.execute();
        followUser.setSetFollowUserListener(new OnSetFollowUserListener() {
            @Override
            public void onFinishFollow(boolean isFollow) {
                hideProgress();
                changeFollowStatus(userId, isFollow ? IS_FOLLOWING_USER : Constants.UN_FOLLOW_USER);
            }

            @Override
            public void onError(int errorCode, String message) {
                hideProgress();
                handleError(message, errorCode);
            }
        });
    }

    /**
     * friend-on-belive's callback
     */
    @Override
    public void onAvatarImgClicked(View v, FriendSuggestionModel userItemModel, int position) {
        showUserProfileDialog(userItemModel.getUserName(), userItemModel.getUserImage());
    }

    @Override
    public void onItemClicked(View v, FriendSuggestionModel userItemModel, int position) {

    }

    /**
     * suggested-friend-list's callback
     */
    @Override
    public void onSuggestedItemClicked(SearchModel item, int position) {
        showUserProfileDialog(item.getUserName(), item.getUserImage());
    }

    @Override
    public void onFollowUserClicked(SearchModel item, int position) {
//        if (item.getIsFollow() == IS_FOLLOWING_USER) return;

        updateFollowStatus(item.getUserId(), item.getIsFollow() != IS_FOLLOWING_USER);
    }

    //load more facebook friends who using belive
    @Override
    public void onLoadMore() {
        Timber.d("on load more");
        if (mPresenter.getFriendListOnBelive(facebookToken, false)) {
            mFriendOnBeliveAdapter.addLoadMoreItem();
        }
    }
    //endregion -------implement methods-------

    //region -------inner methods-------
    private void setupToolbar() {
        setTextWithFontOnView(mTxtToolbarTitle, getString(R.string.friend_suggestion_title), getString(R.string.font_helveticaneuebold), 14, "#6b6c6e");
        mTxtToolbarTitle.setText(getString(R.string.friend_suggestion_title));
        mIBtnLeftToolbar.setVisibility(View.INVISIBLE);
//        setTextWithFontOnView(mBtnRightToolbar, getString(R.string.skip), getString(R.string.font_helveticaneuelight), 13, "#9b9b9b");
        mBtnRightToolbar.setText(getString(R.string.skip));
    }

    public void setTextWithFontOnView(CustomFontTextView view, String title, String font, int textSize, String color) {
        if (view != null) {
            view.setText(title);
//            view.setTextColor(Color.parseColor(color));
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            view.setVisibility(View.VISIBLE);
            view.setCustomFont(this, font);
        }
    }

    public void setTextWithFontOnView(CustomFontButton view, String title, String font, int textSize, String color) {
        if (view != null) {
            view.setText(title);
            view.setTextColor(Color.parseColor(color));
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            view.setVisibility(View.VISIBLE);
            view.setCustomFont(this, font);
        }
    }

    private void setupInvitationSuggestion(CharSequence formattedRewardMessage) {
        if (TextUtils.isEmpty(formattedRewardMessage)) {
            formattedRewardMessage = SpannableUtil.replaceGemIcon(this, getString(R.string.txt_friend_invitation_suggestion));
        }
        mTxtInvitationSuggestion.setText(formattedRewardMessage);
    }

    private void setupRecyclerView() {
        mFriendOnBeliveAdapter = new FriendOnBeliveAdapter(this, new ArrayList<>(), this);
        mRcvFriendOnBelive.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRcvFriendOnBelive.setOnLoadMoreListener(this);
        mRcvFriendOnBelive.setAdapter(mFriendOnBeliveAdapter);

//        mSuggestedFriendAdapter = new SuggestedFriendListAdapter(new ArrayList<>(), this);
//        rcvFriendOnBelive.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
//        rcvFriendOnBelive.addItemDecoration(new SpacesItemDecoration(Utils.dpToPx(15)));
//        rcvFriendOnBelive.setAdapter(mSuggestedFriendAdapter);
    }

    private void getFacebookTokenAndRequestFriendList() {
        if (!ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_FACEBOOK.equals(loginFrom)) {
            Timber.d("didn't login by facebook ");
            LINE_HEIGHT = 1.2;
            mInviteTextLineNum = 2;
            updateRewardMessage(R.string.txt_friend_invitation_suggestion_non_fb);
            flFriendOnBeLiveContainer.setVisibility(View.GONE);

            return;
        } else {
            LINE_HEIGHT = 1.3;
            mInviteTextLineNum = 1;
            updateRewardMessage(R.string.txt_friend_invitation_suggestion);
        }
        if (TextUtils.isEmpty(facebookToken)) {
            SocialManager.getInstance().getFacebookAccessToken(this, this);
        } else {
            mPresenter.getFriendListOnBelive(facebookToken, true);
        }
    }

    private void updateRewardMessage(@StringRes int stringId) {
        if (mTxtInvitationSuggestion != null)
            mTxtInvitationSuggestion.setText(replaceGemIcon(this, getString(stringId)));
    }

    private void initModelLists() {
        mSuggestedList = new ArrayList<>();
        mFriendListOnBelive = new ArrayList<>();
    }

    void changeFollowStatus(int position, int status, boolean isFollowButtonClicked) {
        if (!isFollowButtonClicked || position >= mFriendListOnBelive.size()) {
            return;
        }
        FriendSuggestionModel model = (FriendSuggestionModel) mFriendListOnBelive.get(position);
        model.setIsFollow(status);
        mFriendOnBeliveAdapter.notifyItemChanged(position);
    }

    void changeFollowStatus(String userId, int status) {
        if (mFriendListOnBelive != null && !mFriendListOnBelive.isEmpty()) {
            mCompositeSubscription.add(Observable.from(mFriendListOnBelive)
                    .map(displayableItemObservable -> (FriendSuggestionModel) displayableItemObservable)
                    .filter(friendSuggestionModel -> friendSuggestionModel.getUserId().equalsIgnoreCase(userId) && friendSuggestionModel.getIsFollow() != status)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(friendSuggestionModel -> {
                        friendSuggestionModel.setIsFollow(status);
                        mFriendOnBeliveAdapter.notifyItemChanged(mFriendListOnBelive.indexOf(friendSuggestionModel));
                    }, Timber::e));
        }

        if (mSuggestedList != null && !mSuggestedList.isEmpty() && mSuggestedAdapter != null) {
            mCompositeSubscription.add(Observable.from(mSuggestedList)
                    .map(displayableItemObservable -> (SearchModel) displayableItemObservable)
                    .filter(searchModel -> searchModel.getUserId().equalsIgnoreCase(userId) && searchModel.getIsFollow() != status)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(suggestionModel -> {
                        suggestionModel.setIsFollow(status);
                        mSuggestedAdapter.notifyItemChanged(mSuggestedList.indexOf(suggestionModel));
//                        mSuggestedFriendAdapter.notifyItemChanged(mSuggestedFriendList.indexOf(suggestionModel));
//                        if (mRecommendedPagerAdapter != null) {
//                            int size = mRecommendedPagerAdapter.getAdapterSparseArray().size();
//                            for (int i = 0; i < size; i++) {
//                                mRecommendedPagerAdapter.getAdapterSparseArray().get(i).notifyDataSetChanged();
//                            }
//                        }
                    }, Timber::e));
        }
    }

    // variable to track event time
    private long mLastClickTime = 0;

    private void showUserProfileDialog(String userName, String avatarUrl) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        DialogUserProfileFragment userProfileFragment = DialogUserProfileFragment.newInstance(userName, avatarUrl, true, true);
        userProfileFragment.setCanGoProfileScreen(false);
        userProfileFragment.setUserProfileActionListener(new DialogUserProfileFragment.UserProfileActionListener() {
            @Override
            public void onReportUserClick(String userId) {

            }

            @Override
            public void onBlockUserClick(String userId, String displayName) {

            }

            @Override
            public void onMuteUserClick(String userId, String displayName) {

            }

            @Override
            public void onUnMuteUserClick(String userId, String displayName) {

            }

            @Override
            public void onFollowCountChanged(int count) {

            }

            @Override
            public void onDimissed() {

            }

            @Override
            public void onChangeFollowStatus(String userId, int status) {
//                changeFollowStatus(position, status, isFollowButtonClicked);
                changeFollowStatus(userId, status);
            }

            @Override
            public void onVideoCallClicked(String userId, String userName) {

            }
        });
        userProfileFragment.show(getSupportFragmentManager(), USER_DIALOG_TAG);
    }

    //region pager

    void setViewpagerIndicator() {
        llCountDots.removeAllViews();
        if (dotsCount <= 1) return;
        dots = new ImageView[dotsCount];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8, 0, 0, 0);

        for (int i = 0; i < dotsCount; i++) {

            dots[i] = new ImageView(this);
            dots[i].setLayoutParams(lp);
            dots[i].setBackgroundResource(R.drawable.circle_dot_suggested);
            llCountDots.addView(dots[i]);
        }

        dots[0].setBackgroundResource(R.drawable.circle_dot_suggested_selected);
    }

    void setViewPagerItemsWithAdapter(List<DisplayableItem> suggestedFriendList) {

        if (mRecommendedPagerAdapter == null && mOnPageChangeListener == null) {

            int residual = suggestedFriendList.size() % 4;
            if (residual == 0) {
                dotsCount = suggestedFriendList.size() / 4;
            } else {
                dotsCount = suggestedFriendList.size() / 4 + 1;
            }

            mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < dotsCount; i++) {
                        dots[i].setBackgroundResource(R.drawable.circle_dot_suggested);
                    }
                    dots[position].setBackgroundResource(R.drawable.circle_dot_suggested_selected);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    //don't need to implement
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    //don't need to implement
                }
            };
        }
        initViewPager(suggestedFriendList);
    }

    private void initViewPager(List<DisplayableItem> recommendedItems) {
        mRecommendedPagerAdapter = new RecommendedPagerAdapter(this, dotsCount, recommendedItems);
        vpRecommended.setAdapter(mRecommendedPagerAdapter);
        vpRecommended.setCurrentItem(0);
        vpRecommended.addOnPageChangeListener(mOnPageChangeListener);
        vpRecommended.setOffscreenPageLimit(dotsCount);
    }

    //endregion
    //endregion -------inner methods-------


    //region -------inner class-------

    static class RecommendedPagerAdapter extends PagerAdapter {

        private int countPager;
        private int orderItemRecommended = 0;
        private final WeakReference<FriendSuggestionActivity> mContextWeakReference;
        private List<DisplayableItem> mItemList;
        private int GRID_COLUMN_COUNT = 2;
        private SuggestedFriendListAdapter mSuggestedFriendAdapter;
        private static final int MAX_ITEM_PER_PAGE = 4;
        private int MAX_ITEM_POSITION_IN_PAGE = MAX_ITEM_PER_PAGE - 1;
        private SparseArray<SuggestedFriendListAdapter> mAdapterSparseArray;
//        mRcvSuggestedFriend.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
//        mRcvSuggestedFriend.addItemDecoration(new SpacesItemDecoration(Utils.dpToPx(15)));
//        mRcvSuggestedFriend.setAdapter(mSuggestedFriendAdapter);

        public RecommendedPagerAdapter(FriendSuggestionActivity context, int dotsCount, List<DisplayableItem> recommendedItems) {
            mContextWeakReference = new WeakReference<>(context);
            this.countPager = dotsCount;
            this.mItemList = recommendedItems;
            mAdapterSparseArray = new SparseArray<>();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mContextWeakReference.get()).inflate(R.layout.recommended_friends,
                    container, false);

            ArrayList<DisplayableItem> items = new ArrayList<>();
            for (int i = 0; i <= MAX_ITEM_POSITION_IN_PAGE; i++) {
                if (mItemList.size() > orderItemRecommended) {
                    items.add(mItemList.get(orderItemRecommended));
                    orderItemRecommended++;
                }
            }

            int pageNumber = (orderItemRecommended / MAX_ITEM_PER_PAGE) - 1;//start counting from 0
            if (orderItemRecommended % MAX_ITEM_PER_PAGE > 0) {
                pageNumber++;
            }

            RecyclerView mRcvSuggestedFriend = (RecyclerView) view.findViewById(R.id.rcv_friend_on_belive);
            mSuggestedFriendAdapter = new SuggestedFriendListAdapter(items, mContextWeakReference.get());
            mAdapterSparseArray.put(pageNumber, mSuggestedFriendAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContextWeakReference.get(), GRID_COLUMN_COUNT);
//            StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(GRID_COLUMN_COUNT, StaggeredGridLayoutManager.HORIZONTAL);
            mRcvSuggestedFriend.addItemDecoration(new ItemOffsetDecoration(GRID_COLUMN_COUNT, Utils.dpToPx(15), true));
            mRcvSuggestedFriend.setAdapter(mSuggestedFriendAdapter);
            mRcvSuggestedFriend.setLayoutManager(gridLayoutManager);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public int getCount() {
            return this.countPager;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        SparseArray<SuggestedFriendListAdapter> getAdapterSparseArray() {
            return mAdapterSparseArray;
        }


    }

    static class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;
        private boolean hasTopBanner;

        public void setHasTopBanner(boolean hasTopBanner) {
            this.hasTopBanner = hasTopBanner;
        }

        ItemOffsetDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position

            int column = position % spanCount; // item column
            if (hasTopBanner && position > 0) {
                column = (position - 1) % spanCount;
            }

            if (includeEdge) {
                if (position == 0 && hasTopBanner) {
                    outRect.left = 0;
                    outRect.right = 0;
                } else {
                    outRect.left = spacing - column * spacing / spanCount;
                    outRect.right = (column + 1) * spacing / spanCount;
                }

                // top edge
                if (position == 0 && hasTopBanner) {
                    outRect.top = 0;
                } else if (position < spanCount && !hasTopBanner) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing + Utils.dpToPx(3); // bottom edge
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing; // top edge
                }
            }
        }
    }

    static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int mSpace;

        public SpacesItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = mSpace / 2;
            outRect.left = mSpace / 2;
            int position = parent.getChildAdapterPosition(view);
//            parent.getAdapter().getItemCount()
//            Timber.e(String.valueOf(position));
            if (position % 2 == 0) {
                outRect.bottom = mSpace;
            }
//            if (position == parent.getAdapter().getItemCount() - 1) {
//                outRect.right = mSpace;
//            }
//            if (position == 0 || position == 1)
//                outRect.left = Utils.dpToPx(20);

//            outRect.right = mSpace;
//            outRect.bottom = mSpace;
//            outRect.top = mSpace;
//            // Add top margin only for the first item to avoid double space between items
//            if (parent.getChildAdapterPosition(view) == 0)
//                outRect.top = mSpace;
        }
    }
    //endregion -------inner class-------
}
