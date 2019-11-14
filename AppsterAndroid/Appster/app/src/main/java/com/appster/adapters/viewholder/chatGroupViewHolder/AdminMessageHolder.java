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

/**
 * Created by linh on 05/06/2017.
 */

public class AdminMessageHolder extends RecyclerView.ViewHolder{
    private static final int ADMIN_TEXT_SIZE = Utils.dpToPx(13);

    @Bind(R.id.txt_comment_item)
    SpannableTextView mTxtChatItem;

    private ChatGroupUtil mChatGroupUtil;

    public AdminMessageHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mChatGroupUtil = ChatGroupUtil.getInstance(itemView.getContext());
    }

    public static AdminMessageHolder create(ViewGroup parent){
        return new AdminMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_litter_text_chat, parent, false));
    }

    public void bindTo(ChatItemModelClass item){
        itemView.setBackgroundResource(R.drawable.bg_normal_chat_message);
        CharSequence formatMessage = mChatGroupUtil.formatAdminMessage(item.getChatDisplayName(), item.getMsg());
        TextLayoutBuilder textLayoutBuilder = mChatGroupUtil.createLayoutBuilder();
        TextLayoutBuilder strokeLayoutBuilder = mChatGroupUtil.createStrokeLayoutBuilder();
        textLayoutBuilder.setTextSize(ADMIN_TEXT_SIZE).setText(formatMessage);
        strokeLayoutBuilder.setTextSize(ADMIN_TEXT_SIZE).setText(mChatGroupUtil.setStrokeSpan(formatMessage));
        this.mTxtChatItem.setLayout(textLayoutBuilder.build(), strokeLayoutBuilder.build());
    }
}
