package com.appster.features.contacts.adapters;

import android.view.ViewGroup;

import com.appster.core.expanableadapter.ChildViewHolder;
import com.appster.core.expanableadapter.ExpandableRecyclerAdapter;
import com.appster.core.expanableadapter.MultiTypeExpandableRecyclerViewAdapter;
import com.appster.core.expanableadapter.ParentListItem;
import com.appster.core.expanableadapter.ParentViewHolder;
import com.appster.core.expanableadapter.ParentWrapper;
import com.appster.domain.FriendSuggestionModel;
import com.appster.features.friend_suggestion.viewholder.FriendOnBeliveViewHolder;
import com.appster.models.BeLiveFriendParent;
import com.appster.models.ContactModel;
import com.appster.models.InviteFriendParent;

import java.util.List;

/**
 * Created by thanhbc on 12/22/17.
 */

public class ContactGroupAdapter extends MultiTypeExpandableRecyclerViewAdapter<ParentViewHolder,ChildViewHolder> {
    private static final int PARENT_INVITE_FRIEND =0;
    private static final int PARENT_FACEBOOK_FRIEND =1;
    private static final int CHILD_INVITE_FRIEND =2;
    private static final int CHILD_FACEBOOK_FRIEND =3;
    private final boolean mShowTopDivider;
    InviteFriendItemViewHolder.InviteFriendViewHolderListener mInviteFriendViewHolderListener;
    FriendOnBeliveViewHolder.FriendOnBeLiveViewHolderListener mFriendOnBeLiveViewHolderListener;

    /**
     * Primary constructor. Sets up {@link #mParentItemList} and {@link #mItemList}.
     * <p>
     * Changes to {@link #mParentItemList} should be made through add/remove methods in
     * {@link ExpandableRecyclerAdapter}
     *
     * @param parentItemList List of all {@link ParentListItem} objects to be
     *                       displayed in the RecyclerView that this
     *                       adapter is linked to
     */
    public ContactGroupAdapter(List<? extends ParentListItem> parentItemList, boolean showTopDivider) {
        super(parentItemList);
        mShowTopDivider = showTopDivider;
    }

    public void setInviteFriendViewHolderListener(InviteFriendItemViewHolder.InviteFriendViewHolderListener inviteFriendViewHolderListener) {
        mInviteFriendViewHolderListener = inviteFriendViewHolderListener;
    }

    public void setFriendOnBeLiveViewHolderListener(FriendOnBeliveViewHolder.FriendOnBeLiveViewHolderListener friendOnBeLiveViewHolderListener) {
        mFriendOnBeLiveViewHolderListener = friendOnBeLiveViewHolderListener;
    }

    @Override
    public ParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup, int viewType) {
        switch (viewType){
            case PARENT_FACEBOOK_FRIEND:
                return BeLiveFriendGroupViewHolder.create(parentViewGroup);
            case PARENT_INVITE_FRIEND:
                return InviteFriendGroupViewHolder.create(parentViewGroup);
            default:
                break;
        }
        return null;
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
        switch (viewType){
            case CHILD_FACEBOOK_FRIEND:
                return FriendOnBeliveViewHolder.create(childViewGroup);
            case CHILD_INVITE_FRIEND:
                return InviteFriendItemViewHolder.create(childViewGroup);
            default:
                break;
        }
        return null;
    }

    @Override
    public int getItemViewType(Object item) {
        if(item instanceof ParentWrapper){
            Object parentType = ((ParentWrapper)item).getParentListItem();
            if(parentType instanceof  BeLiveFriendParent){
                return PARENT_FACEBOOK_FRIEND;
            }else if (parentType instanceof InviteFriendParent){
                return PARENT_INVITE_FRIEND;
            }
            return PARENT_INVITE_FRIEND;
        }else if (item instanceof ContactModel){
            return CHILD_INVITE_FRIEND;
        }else if( item instanceof FriendSuggestionModel){
            return CHILD_FACEBOOK_FRIEND;
        }
        return -1;
    }


    @Override
    public void onBindParentViewHolder(ParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        if(parentViewHolder instanceof InviteFriendGroupViewHolder && parentListItem instanceof InviteFriendParent){
            ((InviteFriendGroupViewHolder) parentViewHolder).bindTo((InviteFriendParent) parentListItem,mShowTopDivider);
        }else if (parentViewHolder instanceof BeLiveFriendGroupViewHolder && parentListItem instanceof BeLiveFriendParent){
            ((BeLiveFriendGroupViewHolder) parentViewHolder).bindTo((BeLiveFriendParent) parentListItem);
        }
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder childViewHolder, int position, Object childListItem) {
        if(childViewHolder instanceof InviteFriendItemViewHolder && childListItem instanceof ContactModel){
            ((InviteFriendItemViewHolder) childViewHolder).bindTo((ContactModel) childListItem,mInviteFriendViewHolderListener);
        }else if (childViewHolder instanceof FriendOnBeliveViewHolder && childListItem instanceof FriendSuggestionModel){
            ((FriendOnBeliveViewHolder) childViewHolder).bindTo((FriendSuggestionModel) childListItem, mFriendOnBeLiveViewHolderListener);
        }
    }

    @Override
    public boolean isGroup(int viewType) {
        return viewType == PARENT_INVITE_FRIEND || viewType == PARENT_FACEBOOK_FRIEND;
    }

    @Override
    public boolean isChild(int viewType) {
        return viewType ==  CHILD_FACEBOOK_FRIEND || viewType == CHILD_INVITE_FRIEND;
    }
}
