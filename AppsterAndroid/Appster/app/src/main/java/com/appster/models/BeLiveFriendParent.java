package com.appster.models;

import com.appster.core.expanableadapter.ParentListItem;

import java.util.List;

/**
 * Created by thanhbc on 12/22/17.
 */

public class BeLiveFriendParent implements ParentListItem {

    private String mName;
    private List<?> mFacebookFriendList;

    public BeLiveFriendParent(String name, List<?> contactModelList) {
        mName = name;
        mFacebookFriendList = contactModelList;
    }

    public String getName() {
        return mName;
    }

    @Override
    public List<?> getChildItemList() {
        return mFacebookFriendList;
    }

    public int getNumOfFriends() {
        return mFacebookFriendList != null ? mFacebookFriendList.size() : 0;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }
}
