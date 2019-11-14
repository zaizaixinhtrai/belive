package com.appster.features.fb_friends;

import com.appster.domain.FriendSuggestionModel;
import com.appster.features.mvpbase.BaseContract;

import java.util.List;

/**
 * Created by linh on 20/09/2017.
 */

public class FbFriendSuggestionContract implements BaseContract {
    interface FbFriendSuggestionView extends BaseContract.View{
        void onGetFriendListOnBeliveSucessfully(List<FriendSuggestionModel> friendListOnBelive);
        void onGetFriendListOnBeliveError();
        void onChangeFollowStatusSuccessfully(String userId, int status);
        void onChangeFollowStatusError(int code, String message);
        void onChangeUnFollowStatusSuccessfully(String userId, int status);
        void onChangeUnFollowStatusError(int code, String message);
    }

    interface UserActions extends BaseContract.Presenter<FbFriendSuggestionView>{
        /**
         * @param token facebook token
         * @return true if hasn't gotten the end of list. false otherwise
         */
        boolean getFriendListOnBelive(String token);
        void followUser(String userId);
        void unFollowUser(String userId);
        void reset();
    }
}
