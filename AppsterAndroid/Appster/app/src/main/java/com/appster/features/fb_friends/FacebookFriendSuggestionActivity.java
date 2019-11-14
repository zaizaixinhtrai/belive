package com.appster.features.fb_friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.StringRes;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.activity.UserProfileActivity;
import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.recyclerview.LoadMoreRecyclerView;
import com.appster.customview.CustomFontTextView;
import com.appster.domain.FriendSuggestionModel;
import com.appster.features.friend_suggestion.adapter.FriendOnBeliveAdapter;
import com.appster.features.friend_suggestion.viewholder.FriendOnBeliveViewHolder;
import com.appster.interfaces.OnLoadMoreListenerRecyclerView;
import com.appster.models.FollowStatusChangedEvent;
import com.appster.models.UserModel;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DialogUtil;
import com.appster.utility.SocialManager;
import com.appster.utility.SpannableUtil;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.BranchIoUtil;
import com.apster.common.Constants;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.apster.common.Constants.IS_FOLLOWING_USER;

/**
 * Created by linh on 20/09/2017.
 */

public class FacebookFriendSuggestionActivity extends BaseToolBarActivity implements FbFriendSuggestionContract.FbFriendSuggestionView, FriendOnBeliveViewHolder.FriendOnBeLiveViewHolderListener, OnLoadMoreListenerRecyclerView {

    @Bind(R.id.rcv_friend_suggestion)
    LoadMoreRecyclerView mRcvFriendOnBelive;
    @Bind(R.id.txt_friend_invitation_suggestion)
    CustomFontTextView mTxtInvitationSuggestion;
    @Bind(R.id.ll_invitation)
    LinearLayout mLlInvitation;

    FriendOnBeliveAdapter mFriendOnBeliveAdapter;

    FbFriendSuggestionContract.UserActions mPresenter;
    String facebookToken;
    List<DisplayableItem> mFriendListOnBelive;
    UserModel userInfoModel;

    public static Intent newIntent(Context context) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(context, FacebookFriendSuggestionActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    //=========== lifecycle ========================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTopBarTile(getString(R.string.facebok_friends));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
        setupRecyclerView();
//        mPresenter.reset();
//        mFriendListOnBelive.clear();
//        getFbFriendsOnBelive();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode){
//            case Constants.REQUEST_CODE_VIEW_USER_PROFILE:
//                onResultFromProfileScreen(data);
//                break;
//
//            default:
//                break;
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
            mPresenter.reset();
            mFriendListOnBelive.clear();
            getFbFriendsOnBelive();
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_fb_friend_suggestion;
    }

    @Override
    public void init() {
        userInfoModel = AppsterApplication.mAppPreferences.getUserModel();
        mPresenter = new FbFriendSuggestionPresenter(this, AppsterWebServices.get());
    }

    //========== MVP callbacks =====================================================================
    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void loadError(String errorMessage, int code) {
        handleError(errorMessage, code);
    }

    @Override
    public void showProgress() {
        showDialog(this, getResources().getString(R.string.connecting_msg));
    }

    @Override
    public void hideProgress() {
        dismisDialog();
    }

    @Override
    public void onGetFriendListOnBeliveSucessfully(List<FriendSuggestionModel> friendListOnBelive) {
        Timber.d("friend list current size %d", mFriendListOnBelive.size());
        mFriendOnBeliveAdapter.removeLoadingItem();
        if (!friendListOnBelive.isEmpty()) {
            mFriendListOnBelive.addAll(friendListOnBelive);
        }

        if (mFriendListOnBelive.isEmpty()) {
            updateRewardMessage(R.string.txt_friend_invitation_suggestion_non_fb);
        }else{
            updateRewardMessage(R.string.txt_friend_invitation_suggestion);
        }

        mFriendOnBeliveAdapter.updateItems(mFriendListOnBelive);
        mRcvFriendOnBelive.setLoading(false);
    }

    @Override
    public void onGetFriendListOnBeliveError() {

    }

    @Override
    public void onChangeFollowStatusSuccessfully(String userId, int status) {
        changeFollowStatus(userId, status);
    }

    @Override
    public void onChangeFollowStatusError(int code, String message) {

    }

    @Override
    public void onChangeUnFollowStatusSuccessfully(String userId, int status) {
        changeFollowStatus(userId, status);
    }

    @Override
    public void onChangeUnFollowStatusError(int code, String message) {

    }

    //========== event listeners ===================================================================
    @OnClick(R.id.btn_invite_friend)
    public void onInviteButtonClicked() {
        BranchIoUtil.generateBranchIoUrl(this, userInfoModel.getUserImage(), userInfoModel.getReferralId(), "facebook app invites", url -> {
            Timber.d("branch io url %s", url);
            String previewImageUrl;
            previewImageUrl = "https://s3-ap-southeast-1.amazonaws.com/production-appsters-clients/common/FbInviteFriend-Banner.png";
            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl(url)
                        .setPreviewImageUrl(previewImageUrl)
                        .build();
                AppInviteDialog.show(FacebookFriendSuggestionActivity.this, content);
            }
        });
    }

    //============ recycler view listeners =========================================================
    @Override
    public void onFollowButtonClicked(View v, FriendSuggestionModel userItemModel, int position) {
        if (userItemModel.getIsFollow() == IS_FOLLOWING_USER) {
            showConfirmDialog(userItemModel.getUserId(), userItemModel.getDisplayName());
        }else{
            mPresenter.followUser(userItemModel.getUserId());
        }

    }

    @Override
    public void onAvatarImgClicked(View v, FriendSuggestionModel userItemModel, int position) {
        gotoUserProfileScreen(userItemModel.getUserId(), userItemModel.getDisplayName());
    }

    @Override
    public void onItemClicked(View v, FriendSuggestionModel userItemModel, int position) {
        gotoUserProfileScreen(userItemModel.getUserId(), userItemModel.getDisplayName());
    }

    @Override
    public void onLoadMore() {
        Timber.d("on load more");
        if (mPresenter.getFriendListOnBelive(facebookToken)) {
            mFriendOnBeliveAdapter.addLoadMoreItem();
        }
    }

    //========== inner methods =====================================================================
    private void setupRecyclerView() {
        mFriendListOnBelive = new ArrayList<>();
        mFriendOnBeliveAdapter = new FriendOnBeliveAdapter(this, new ArrayList<>(), this);
        mRcvFriendOnBelive.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRcvFriendOnBelive.setOnLoadMoreListener(this);
        mRcvFriendOnBelive.setAdapter(mFriendOnBeliveAdapter);
    }

    private void getFbFriendsOnBelive() {
        if (TextUtils.isEmpty(facebookToken)) {
            SocialManager.getInstance().getFacebookAccessToken(this, new SocialManager.OnGetFBTokenListener() {
                @Override
                public void onSuccess(String accessToken) {
                    facebookToken = accessToken;
                    mPresenter.getFriendListOnBelive(facebookToken);
                    Timber.d("facebook token %s", facebookToken);
                }

                @Override
                public void onFailed() {

                }
            });
        } else {
            mPresenter.getFriendListOnBelive(facebookToken);
        }
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
    }

    private void updateRewardMessage(@StringRes int stringId) {
        if (mTxtInvitationSuggestion != null)
            mTxtInvitationSuggestion.setText(SpannableUtil.replaceGemIcon(this, getString(stringId), R.color.color_58585b));
    }

    private void gotoUserProfileScreen(String userId, String displayName){
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        startActivityForResult(UserProfileActivity.newIntent(this, userId, displayName),
                Constants.REQUEST_CODE_VIEW_USER_PROFILE, options.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            SocialManager.getInstance().onActivityResult(requestCode, resultCode, data);
        }catch (Exception e) {
            Timber.e(e);
        }
    }

    private void onResultFromProfileScreen(Intent data){
        if (data == null) {
            return;
        }

        FollowStatusChangedEvent followStatusChangedEvent = data.getParcelableExtra
                (ConstantBundleKey.BUNDLE_DATA_FOLLOW_USER_FROM_PROFILE_ACTIVITY);

        if (followStatusChangedEvent == null) return;
        changeFollowStatus(followStatusChangedEvent.getUserId(), followStatusChangedEvent.getFollowType());
    }

    private void showConfirmDialog(String userId, String displayName){
        DialogUtil.showConfirmUnFollowUser(this, displayName,
                () -> mPresenter.unFollowUser(userId));
    }
}
