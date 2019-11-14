package com.appster.models;

import com.appster.core.expanableadapter.ParentListItem;

import java.util.List;

/**
 * Created by thanhbc on 12/22/17.
 */

public class InviteFriendParent implements ParentListItem {

    private String mName;
    private List<?> mContactModelList;

    public InviteFriendParent(String name, List<?> contactModelList) {
        mName = name;
        mContactModelList = contactModelList;
    }

    public String getName() {
        return mName;
    }

    @Override
    public List<?> getChildItemList() {
        return mContactModelList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }
}
