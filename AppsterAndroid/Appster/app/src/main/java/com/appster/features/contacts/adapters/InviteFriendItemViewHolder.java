package com.appster.features.contacts.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.R;
import com.appster.core.expanableadapter.ChildViewHolder;
import com.appster.customview.CircleImageView;
import com.appster.customview.CustomFontButton;
import com.appster.customview.CustomFontTextView;
import com.appster.models.ContactModel;
import com.appster.utility.ImageLoaderUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 12/22/17.
 */

public class InviteFriendItemViewHolder extends ChildViewHolder {

    @Bind(R.id.ivUserImage)
    CircleImageView ivUserImage;
    @Bind(R.id.tvDisplayName)
    CustomFontTextView tvDisplayName;
    @Bind(R.id.tvPhoneNumber)
    CustomFontTextView tvPhoneNumber;
    @Bind(R.id.btnInvite)
    CustomFontButton btnInvite;

    InviteFriendItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static InviteFriendItemViewHolder create(ViewGroup parent) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_invite_child_item, parent, false);
        return new InviteFriendItemViewHolder(itemView);
    }

    public void bindTo(ContactModel contact, InviteFriendItemViewHolder.InviteFriendViewHolderListener listener) {
        ImageLoaderUtil.displayUserImage(itemView.getContext(), contact.photoUri, ivUserImage);
        tvDisplayName.setText(String.valueOf(contact.displayName));
        tvPhoneNumber.setText(String.valueOf(contact.getFirstNomalizedPhoneNum()));
        btnInvite.setOnClickListener(view -> {
            if (listener != null) listener.onInviteClicked(view, contact, getAdapterPosition());
        });
    }


    public interface InviteFriendViewHolderListener {
        void onInviteClicked(View v, ContactModel contact, int position);
    }

}
