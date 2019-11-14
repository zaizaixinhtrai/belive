package com.appster.interfaces;

import com.appster.models.FollowItemModel;

/**
 * Created by User on 10/28/2015.
 */
public interface FollowHolderListener {

    void viewUserDetail(FollowItemModel itemModel);

    void followUser(FollowItemModel itemModel, int position);

}
