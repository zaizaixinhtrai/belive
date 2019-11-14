package com.appster.adapters.viewholder.chatGroupViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.utility.ChatGroupUtil;
import com.apster.common.Utils;
import com.apster.common.view.SpannableTextView;
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by linh on 05/06/2017.
 */

public class WarningMessageHolder extends RecyclerView.ViewHolder {
    private static final int USER_TEXT_SIZE = Utils.dpToPx(13);

    @Bind(R.id.txt_comment_item)
    SpannableTextView mTxtWarning;
    private String warningMessage;
    ChatGroupUtil mChatGroupUtil;

    public WarningMessageHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        warningMessage = itemView.getContext().getString(R.string.stream_disclaimer);
        mChatGroupUtil = ChatGroupUtil.getInstance(itemView.getContext());
    }

    public static WarningMessageHolder create(ViewGroup parent){
        return new WarningMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_litter_text_chat, parent, false));
    }

    public void bindTo(){
        itemView.setBackgroundResource(R.drawable.bg_anoucement_chat_message);
        CharSequence formattedMessage = mChatGroupUtil.formatWaningMessage(warningMessage);
        TextLayoutBuilder textLayoutBuilder = mChatGroupUtil.createLayoutBuilder();
        TextLayoutBuilder strokeLayoutBuilder = mChatGroupUtil.createStrokeLayoutBuilder();
        textLayoutBuilder.setTextSize(USER_TEXT_SIZE).setText(formattedMessage);
        strokeLayoutBuilder.setTextSize(USER_TEXT_SIZE).setText(mChatGroupUtil.setStrokeSpan(formattedMessage));
        this.mTxtWarning.setLayout(textLayoutBuilder.build(), strokeLayoutBuilder.build());
    }
}
