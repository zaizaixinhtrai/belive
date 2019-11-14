package com.appster.features.friend_suggestion.adapter;

import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.ListDelegationAdapter;
import com.appster.features.friend_suggestion.delegate.SuggestedFriendDelegate;
import com.appster.features.friend_suggestion.viewholder.SuggestedFriendViewHolder;

import java.util.List;

/**
 * Created by linh on 18/05/2017.
 */

public class SuggestedFriendListAdapter extends ListDelegationAdapter<List<DisplayableItem>> {
    public static final int SUGGESTED_FRIEND = 11;

    public SuggestedFriendListAdapter(List<DisplayableItem> items, SuggestedFriendViewHolder.OnClickListener itemCallBack) {
        this.delegatesManager.addDelegate(SUGGESTED_FRIEND, new SuggestedFriendDelegate(itemCallBack));
        setItems(items);
    }
}
