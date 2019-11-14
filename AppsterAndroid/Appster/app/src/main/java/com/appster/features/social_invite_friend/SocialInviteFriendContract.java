package com.appster.features.social_invite_friend;

import com.appster.features.mvpbase.BaseContract;

public interface SocialInviteFriendContract {

    interface View extends BaseContract.View {
        void onFacebookMutualFriendsReceived(int numOfFriends);
        void onContactMutualFriendsReceived(int numOfFriends);
        void onInstagramMutualFriendsReceived(int numOfFriends);
        void onTwitterMutualFriendsReceived(int numOfFriends);
    }

    interface UserActions extends BaseContract.Presenter<SocialInviteFriendContract.View> {
        void getSocialFriends();
    }
}
