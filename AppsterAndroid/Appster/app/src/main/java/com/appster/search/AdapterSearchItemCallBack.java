package com.appster.search;

import com.domain.models.SearchUserModel;

/**
 * Created by User on 12/1/2016.
 */

public interface AdapterSearchItemCallBack {
    void showUserProfile(int position, SearchUserModel itemModelClass);

    void followUser(int position, SearchUserModel itemModelClass);

    void onClickLoadMore();
}
