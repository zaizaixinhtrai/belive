package com.appster.search;

import com.appster.features.mvpbase.BaseContract;
import com.appster.models.FollowStatusChangedEvent;
import com.domain.models.SearchUserModel;

import java.util.List;

/**
 * Created by User on 11/30/2016.
 */

public interface SearchContract {

    interface SearchView extends BaseContract.View {

        void showResult(List<SearchUserModel> listUsers);

        void getPagingResult(int nextPage, boolean isEnd);

        void addFooterListView();

        void hideFooterListView();

        void handleTextNoData();

        void changeFollowUser(int position, boolean isFollow);

        void updateFollowUser(FollowStatusChangedEvent event);

        void onFacebookMutualFriendsReceived(int numOfFriends);
        void onContactMutualFriendsReceived(int numOfFriends);
        void onInstagramMutualFriendsReceived(int numOfFriends);
        void onTwitterMutualFriendsReceived(int numOfFriends);
        void onFollowRequirePass(int position, SearchUserModel itemModelClass);
    }

    interface UserActions extends BaseContract.Presenter<SearchContract.SearchView> {

        void searchUser(String textInput,int nextIndex);

        void followUser(int position, SearchUserModel itemModelClass);
        void followUserWithPass(int position, SearchUserModel itemModelClass,String pass);

        void getSocialFriends();
    }
}
