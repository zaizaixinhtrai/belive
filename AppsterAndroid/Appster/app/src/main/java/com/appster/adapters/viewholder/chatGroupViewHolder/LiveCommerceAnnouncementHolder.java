package com.appster.adapters.viewholder.chatGroupViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.message.ChatItemModelClass;
import com.appster.utility.ChatGroupUtil;
import com.apster.common.Utils;
import com.apster.common.view.SpannableTextView;
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.appster.utility.ChatGroupUtil.getInstance;

/**
 * Created by linh on 28/11/2017.
 */

public class LiveCommerceAnnouncementHolder extends RecyclerView.ViewHolder {
    private static final int USER_TEXT_SIZE = Utils.dpToPx(13);

    @Bind(R.id.txt_announcement)
    SpannableTextView mTxtAnnouncement;
    private ChatGroupUtil mChatGroupUtil;

    public LiveCommerceAnnouncementHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mChatGroupUtil = getInstance(itemView.getContext());
    }

    public static LiveCommerceAnnouncementHolder create(ViewGroup parent){
        return new LiveCommerceAnnouncementHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_commerce_announcement, parent, false));
    }

    public void bindTo(ChatItemModelClass item){
        CharSequence message = mChatGroupUtil.formatLiveCommerceAnnouncementMessage(item.getMsg());
        TextLayoutBuilder textLayoutBuilder = mChatGroupUtil.createLayoutBuilder();
        TextLayoutBuilder strokeLayoutBuilder = mChatGroupUtil.createStrokeLayoutBuilder();
        textLayoutBuilder.setTextSize(USER_TEXT_SIZE).setText(message);
        strokeLayoutBuilder.setTextSize(USER_TEXT_SIZE).setText(mChatGroupUtil.setStrokeSpan(message));
        this.mTxtAnnouncement.setLayout(textLayoutBuilder.build(), strokeLayoutBuilder.build());
    }
}