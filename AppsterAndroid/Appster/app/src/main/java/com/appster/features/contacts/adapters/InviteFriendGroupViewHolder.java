package com.appster.features.contacts.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.appster.R;
import com.appster.core.expanableadapter.ParentViewHolder;
import com.appster.models.InviteFriendParent;
import com.spacenavigationview.BadgeHelper;
import com.spacenavigationview.BadgeItem;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 12/22/17.
 */

public class InviteFriendGroupViewHolder extends ParentViewHolder {
    private static final float INITIAL_POSITION = 360f;
    private static final float ROTATED_POSITION = 180f;

    @Bind(R.id.ivArrowExpand)
    ImageView ivArrowExpand;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.badge_container)
    RelativeLayout mBadgeContainer;

    BadgeItem mBadgeItem;
    @Bind(R.id.divider)
    View divider;

    private int numOfFriends;

    public InviteFriendGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static InviteFriendGroupViewHolder create(ViewGroup parent) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_invite_parent_item, parent, false);
        return new InviteFriendGroupViewHolder(itemView);
    }

    public void bindTo(InviteFriendParent inviteFriendParent,boolean showTopDivider) {
        ivArrowExpand.setVisibility(View.GONE);
        mBadgeItem = new BadgeItem(0,numOfFriends, ContextCompat.getColor(itemView.getContext(), R.color.color_ff5167));
        /**
         * Set circle background to badge view
         */
        mBadgeContainer.setBackground(BadgeHelper.makeShapeDrawableWithoutBorder(mBadgeItem.getBadgeColor()));
        itemView.setEnabled(false);
        if(showTopDivider) divider.setVisibility(View.VISIBLE);
        tvTitle.setText(inviteFriendParent.getName());
        numOfFriends = inviteFriendParent.getChildItemList().size();
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);

        if (expanded) {
            if(numOfFriends!=0) BadgeHelper.hideBadge(mBadgeContainer);
        } else {
            if(numOfFriends!=0) BadgeHelper.showBadge(
                    mBadgeContainer,
                    mBadgeItem,
                    true);
        }

    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        RotateAnimation rotateAnimation = new RotateAnimation(expanded ? INITIAL_POSITION : ROTATED_POSITION,
                expanded ? ROTATED_POSITION : INITIAL_POSITION,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        ivArrowExpand.setAnimation(rotateAnimation);

    }
}
