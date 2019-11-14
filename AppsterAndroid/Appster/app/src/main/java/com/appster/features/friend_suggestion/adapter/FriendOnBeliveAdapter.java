package com.appster.features.friend_suggestion.adapter;

import android.content.Context;

import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.EndlessDelegateAdapter;
import com.appster.features.friend_suggestion.delegate.EmptyDelegate;
import com.appster.features.friend_suggestion.delegate.FriendOnBeliveDelegate;
import com.appster.features.friend_suggestion.viewholder.FriendOnBeliveViewHolder;
import com.appster.features.searchScreen.delegates.TitleItemDelegate;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by linh on 16/05/2017.
 */

public class FriendOnBeliveAdapter extends EndlessDelegateAdapter{
    public static final int VIEW_HEADER =0;
    private static final int VIEW_ITEM_FRIEND_LIST_ON_BELIVE = 2;
    private static final int VIEW_ITEM_EMPTY = 3;

    private WeakReference<Context> mContextWeakReference;

    public FriendOnBeliveAdapter(Context context, List<DisplayableItem> items, FriendOnBeliveViewHolder.FriendOnBeLiveViewHolderListener listener) {
        super(null);
        this.delegatesManager.addDelegate(VIEW_HEADER, new TitleItemDelegate(true));
        this.delegatesManager.addDelegate(VIEW_ITEM_FRIEND_LIST_ON_BELIVE, new FriendOnBeliveDelegate(listener));
        this.delegatesManager.addDelegate(VIEW_ITEM_EMPTY, new EmptyDelegate());
        setItems(items);
    }
}
