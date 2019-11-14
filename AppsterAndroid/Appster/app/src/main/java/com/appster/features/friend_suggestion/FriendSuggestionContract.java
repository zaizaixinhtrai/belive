package com.appster.features.friend_suggestion;

import com.appster.domain.FriendSuggestionModel;
import com.appster.features.mvpbase.BaseContract;
import com.appster.models.AppConfigModel;
import com.appster.models.SearchModel;

import java.util.List;

public interface FriendSuggestionContract {

    interface View extends BaseContract.View {
        void onGetFriendListOnBeliveSucessfully(List<FriendSuggestionModel> friendListOnBelive, boolean isRefresh);

        void onGetFriendListOnBeliveSucessError();

        void onGetSuggestedFriendSuccessfully(List<SearchModel> suggest);

        void onGetSuggestedFriendError();

        void onGetAppConfigSuccessfully(CharSequence formattedRewardMessage);

        void onGetAppConfigFailed();

        void onFollowAllUsersSuccess();

        void onFollowAllUsersFail(String errorMessage,int errorCode);

        void onUnfollowAllUsersSuccess();

        void onUnfollowAllUsersFail(String errorMessage,int errorCode);
    }

    interface UserActions extends BaseContract.Presenter<FriendSuggestionContract.View> {
        /**
         * @param token facebook token
         * @return true if hasn't gotten the end of list. false otherwise
         */
        boolean getFriendListOnBelive(String token, boolean isRefresh);

        void getSuggestedFriend();

        void getAppConfigFromServer();

        void followAllUsers(String userIds, boolean isFirstCall);

        void unFollowAllUsers(String userIds);

        void setIsEndFriendListOnBelive(boolean isEndFriendListOnBelive);
    }
}
