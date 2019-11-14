package com.appster.features.contacts;

import com.appster.features.mvpbase.BaseContract;

import java.util.List;

public interface ContactFriendsContract {

    interface View extends BaseContract.View {
        void onMutualFriendsListReceived(List<?> mutualFriends);
        void onGuestFriendsListReceived(List<?> guestFriends);
        void onChangeFollowStatusSuccessfully(String userId, int status);
        void onChangeFollowStatusError(int code, String message);
        void onChangeUnFollowStatusSuccessfully(String userId, int status);
        void onChangeUnFollowStatusError(int code, String message);
    }

    interface UserActions extends BaseContract.Presenter<ContactFriendsContract.View> {
        void eliminateMutualFriends(List<?> contactModels);
        void followUser(String userId);
        void unFollowUser(String userId);
    }
}
