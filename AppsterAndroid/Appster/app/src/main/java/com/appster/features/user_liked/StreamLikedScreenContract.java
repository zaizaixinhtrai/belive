package com.appster.features.user_liked;

import com.appster.domain.LikedUsersItemModel;
import com.appster.features.mvpbase.BaseContract;

import java.util.List;

/**
 * Created by ThanhBan on 9/26/2016.
 */

public interface StreamLikedScreenContract {
    interface StreamLikedScreenView extends BaseContract.View {
        void likedUsers(List<LikedUsersItemModel> likedUsers);
        void showLoadingItem();
        void dismissLoadingItem();
        void followChanged(boolean isFollow, int position);
    }

    interface UserActions extends BaseContract.Presenter<StreamLikedScreenView> {

        void getLikedUsers(int delayTime,int postId);
        void refreshData(int postId);

        void followButtonClicked(LikedUsersItemModel itemModel, int position);


    }
}
