package com.appster.adapters;

import android.annotation.SuppressLint;

import com.appster.adapters.delegate.AdminMessageDelegate;
import com.appster.adapters.delegate.FollowHostSuggestionMessageDelegate;
import com.appster.adapters.delegate.FollowMessageDelegate;
import com.appster.adapters.delegate.GiftMessageDelegate;
import com.appster.adapters.delegate.JoinedMessageDelegate;
import com.appster.adapters.delegate.LikeMessageDelegate;
import com.appster.adapters.delegate.LiteralTextDelegate;
import com.appster.adapters.delegate.LiveCommerceAnnouncementDelegate;
import com.appster.adapters.delegate.LiveCommerceSuggestionDelegate;
import com.appster.adapters.delegate.SharingMessageDelegate;
import com.appster.adapters.delegate.WarningMessageDelegate;
import com.appster.adapters.delegate.WelcomeDelegate;
import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.ListDelegationAdapter;
import com.appster.message.ChatItemModelClass;
import com.apster.common.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by linh on 05/06/2017.
 */

public class ChatGroupDelegateAdapter extends ListDelegationAdapter<List<DisplayableItem>> {
    private static final int WARNING_MESSAGE_ITEM = 1;
    private static final int ADMIN_MESSAGE_ITEM = 2;
    private static final int FOLLOW_MESSAGE_ITEM = 3;
    private static final int LIKE_MESSAGE_ITEM = 4;
    private static final int GIFT_MESSAGE_ITEM = 5;
    private static final int JOINED_MESSAGE_ITEM = 6;
    private static final int SHARING_MESSAGE_ITEM = 7;
    private static final int LITERAL_MESSAGE_ITEM = 8;
    private static final int FOLLOW_HOST_SUGGESTION_MESSAGE_ITEM = 9;
    private static final int WELCOME_MESSAGE_ITEM = 10;
    private static final int LIVE_COMMERCE_SUGGESTION = 11;
    private static final int LIVE_COMMERCE_ANNOUNCEMENT = 12;

    private List<String> mJoinedUserList;//username / color

    public ChatGroupDelegateAdapter(List<DisplayableItem> items, ChatGroupClickListener chatGroupClickListener, String hostName) {
        LiteralTextDelegate literalTextDelegate = new LiteralTextDelegate(chatGroupClickListener);
        this.delegatesManager.addDelegate(WARNING_MESSAGE_ITEM, new WarningMessageDelegate());
        this.delegatesManager.addDelegate(ADMIN_MESSAGE_ITEM, new AdminMessageDelegate());
        this.delegatesManager.addDelegate(FOLLOW_MESSAGE_ITEM, new FollowMessageDelegate(chatGroupClickListener,hostName));
        this.delegatesManager.addDelegate(SHARING_MESSAGE_ITEM, new SharingMessageDelegate(chatGroupClickListener));
        this.delegatesManager.addDelegate(LIKE_MESSAGE_ITEM, new LikeMessageDelegate(chatGroupClickListener));
        this.delegatesManager.addDelegate(GIFT_MESSAGE_ITEM, new GiftMessageDelegate(chatGroupClickListener));
        this.delegatesManager.addDelegate(JOINED_MESSAGE_ITEM, new JoinedMessageDelegate(chatGroupClickListener));
        this.delegatesManager.addDelegate(FOLLOW_HOST_SUGGESTION_MESSAGE_ITEM, new FollowHostSuggestionMessageDelegate(chatGroupClickListener));
        this.delegatesManager.addDelegate(LITERAL_MESSAGE_ITEM, literalTextDelegate);
        this.delegatesManager.addDelegate(WELCOME_MESSAGE_ITEM, new WelcomeDelegate());
        this.delegatesManager.addDelegate(LIVE_COMMERCE_SUGGESTION, new LiveCommerceSuggestionDelegate(chatGroupClickListener));
        this.delegatesManager.addDelegate(LIVE_COMMERCE_ANNOUNCEMENT, new LiveCommerceAnnouncementDelegate());
        this.delegatesManager.setFallbackDelegate(literalTextDelegate);

        mJoinedUserList = new ArrayList<>();

        setItems(items);
    }

    @SuppressLint({"RxSubscribeOnError", "RxLeakedSubscription"})
    public void removeFollowHostSuggestionItem(){
        Observable.fromCallable(() -> {
            for (int i=0; i< items.size(); i++){
                String type = ((ChatItemModelClass)items.get(i)).getType();
                if (ChatItemModelClass.CHAT_TYPE_FOLLOW_HOST_SUGGESTION.equals(type)){
                    return i;
                }
            }
            return -1;
        }).observeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(integer -> integer > -1)
                .subscribe(integer -> {
                    items.remove(integer.intValue());
                    notifyItemRemoved(integer.intValue());
                }, Timber::e);
    }

    public void newChatItem(ChatItemModelClass newMessage) {
        items.add(newMessage);
        notifyItemInserted(items.size() - 1);
        LogUtils.logD("message box", newMessage.getMsg());
    }

    public void newListChat(List<ChatItemModelClass> listMessages) {
        if(items!=null){
            items.clear();
            items.addAll(listMessages);
            notifyDataSetChanged();
        }

    }

    public void newListChatNotClear(List<ChatItemModelClass> listMessages) {
        if (!listMessages.isEmpty()) {
            int currentSize = items.size();
            items.addAll(listMessages);
            notifyItemRangeChanged(currentSize, listMessages.size());
        }
    }

    /**
     * determine whether a user has joined stream or not
     * if not the store that use into cached list
     */
    public boolean isUserHasJoined(String username) {
        int index = mJoinedUserList.indexOf(username);
        if (index == -1){
            mJoinedUserList.add(username);
            return false;
        }else {
            return true;
        }
    }

    public void putJoinedUserList(List<String> joinedUserList){
        if(mJoinedUserList!=null){
            Iterator<String> it = mJoinedUserList.iterator();
            while (it.hasNext()){
                it.next();
                it.remove();
            }
            mJoinedUserList.addAll(joinedUserList);
//            Timber.e("putJoinedUserList %s",mJoinedUserList.toString());
        }
    }

    //== inner classes =============================================================================
    public interface ChatGroupClickListener {
        void onDisplayNameClicked(ChatItemModelClass item);
        void onMessageClicked(ChatItemModelClass item);
        void onFollowHostSuggestionItemClicked(ChatItemModelClass item);
        void onLiveCommerceSuggestionItemClicked();
    }
}
