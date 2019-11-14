package com.appster.features.friend_suggestion;

import com.appster.core.adapter.DisplayableItem;
import com.appster.models.SearchModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linh on 18/05/2017.
 */

public class SuggestedFriendList implements DisplayableItem {
    private List<DisplayableItem> mSuggestedFriendList;

    public SuggestedFriendList() {
        mSuggestedFriendList = new ArrayList<>();
    }

    public List<DisplayableItem> getSuggestedFriendList() {
        return mSuggestedFriendList;
    }

    public void setSuggestedFriendList(List<DisplayableItem> suggestedFriendList) {
        mSuggestedFriendList = suggestedFriendList;
    }

    public void addAll(List<SearchModel> suggestedFriendList){
        mSuggestedFriendList.addAll(suggestedFriendList);
    }
}
