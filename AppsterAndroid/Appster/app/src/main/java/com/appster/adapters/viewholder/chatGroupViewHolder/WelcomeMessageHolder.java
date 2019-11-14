package com.appster.adapters.viewholder.chatGroupViewHolder;

import android.content.Context;
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

/**
 * Created by Ngoc on 11/9/2017.
 */

public class WelcomeMessageHolder extends RecyclerView.ViewHolder {
    private static final int USER_TEXT_SIZE = Utils.dpToPx(13);
    @Bind(R.id.txt_comment_item)
    SpannableTextView txtWelcomeMessag;
    private String welcomeMessage;
    ChatGroupUtil mChatGroupUtil;
    Context mContext;

    public WelcomeMessageHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mChatGroupUtil = ChatGroupUtil.getInstance(itemView.getContext());
        mContext = itemView.getContext();
    }

    public static WelcomeMessageHolder create(ViewGroup parent) {
        return new WelcomeMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_welcome_text_chat, parent, false));
    }

    public void bindTo(ChatItemModelClass item) {
        welcomeMessage = String.format(itemView.getContext().getString(R.string.welcome_stream_message), item.getMsg());
        itemView.setBackgroundResource(R.drawable.bg_welcome_chat_message);
        CharSequence formattedMessage = mChatGroupUtil.formatWelcomeMessage(mContext, welcomeMessage, item.getMsg());
        TextLayoutBuilder textLayoutBuilder = mChatGroupUtil.createLayoutBuilder();
        TextLayoutBuilder strokeLayoutBuilder = mChatGroupUtil.createStrokeLayoutBuilder();
        textLayoutBuilder.setTextSize(USER_TEXT_SIZE).setText(formattedMessage);
        strokeLayoutBuilder.setTextSize(USER_TEXT_SIZE).setText(mChatGroupUtil.setStrokeSpan(formattedMessage));
        this.txtWelcomeMessag.setLayout(textLayoutBuilder.build(), strokeLayoutBuilder.build());
    }
}
