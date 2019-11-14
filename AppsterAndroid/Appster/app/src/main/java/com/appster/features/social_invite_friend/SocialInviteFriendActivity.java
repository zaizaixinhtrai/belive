package com.appster.features.social_invite_friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.features.contacts.ContactFriendsActivity;
import com.appster.features.fb_friends.FacebookFriendSuggestionActivity;
import com.appster.webservice.AppsterWebServices;
import com.spacenavigationview.BadgeHelper;
import com.spacenavigationview.BadgeItem;

import butterknife.Bind;
import butterknife.OnClick;

public class SocialInviteFriendActivity extends BaseToolBarActivity implements SocialInviteFriendContract.View {

    SocialInviteFriendContract.UserActions presenter;
    @Bind(R.id.badgeContainerFacebook)
    RelativeLayout mBadgeFacebookContainer;
    @Bind(R.id.badgeContainerContact)
    RelativeLayout mBadgeContactContainer;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SocialInviteFriendActivity.class);
        return intent;
    }

    //region-------activity life cycle-------
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new SocialInviteFriendPresenter(AppsterWebServices.get());
        presenter.attachView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleTurnoffMenuSliding();
        setTopBarTile(getString(R.string.invite_friends));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
        if(presenter!=null) presenter.getSocialFriends();
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
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_social_friend_invite;
    }

    @Override
    public void init() {

    }
    //endregion -------activity life cycle-------
    @OnClick(R.id.llFacebookFriendContainer)
    void onFbButtonClicked() {
        startActivity(FacebookFriendSuggestionActivity.newIntent(this));
    }

    @OnClick(R.id.llContactFriendContainer)
    void onContactsClicked(){
        startActivity(ContactFriendsActivity.createIntent(this));
    }
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

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onFacebookMutualFriendsReceived(int numOfFriends) {
        if(numOfFriends!=0 && mBadgeFacebookContainer!=null) {
            BadgeItem badgeItem = new BadgeItem(0,numOfFriends, ContextCompat.getColor(this, R.color.color_ff5167));
            mBadgeFacebookContainer.setBackground(BadgeHelper.makeShapeDrawableWithoutBorder(badgeItem.getBadgeColor()));
            BadgeHelper.showBadge(
                    mBadgeFacebookContainer,
                    new BadgeItem(0,numOfFriends, ContextCompat.getColor(this, R.color.color_ff5167)),
                    true,R.id.tvFacebookFriendsNum);
        }
    }

    @Override
    public void onContactMutualFriendsReceived(int numOfFriends) {
        if(numOfFriends!=0 && mBadgeContactContainer!=null) {
            BadgeItem badgeItem = new BadgeItem(0,numOfFriends, ContextCompat.getColor(this, R.color.color_ff5167));
            mBadgeContactContainer.setBackground(BadgeHelper.makeShapeDrawableWithoutBorder(badgeItem.getBadgeColor()));
            BadgeHelper.showBadge(
                    mBadgeContactContainer,
                    badgeItem,
                    true,R.id.tvContactFriendsNum);
        }
    }

    @Override
    public void onInstagramMutualFriendsReceived(int numOfFriends) {

    }

    @Override
    public void onTwitterMutualFriendsReceived(int numOfFriends) {

    }

    //endregion -------implement methods-------

    //region -------inner methods-------
    //endregion -------inner methods-------


    //region -------inner class-------

    //endregion -------inner class-------
}
