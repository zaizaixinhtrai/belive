package com.appster.adapters.viewholder.chatGroupViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.message.ChatItemModelClass;
import com.appster.utility.ChatGroupUtil;
import com.apster.common.ClickableImageSpan;
import com.apster.common.Utils;
import com.apster.common.view.SpannableTextView;
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.appster.utility.ChatGroupUtil.ClickableSpanNoUnderline;
import static com.appster.utility.ChatGroupUtil.getInstance;

/**
 * Created by linh on 05/06/2017.
 */

public class JoinedMessageHolder extends RecyclerView.ViewHolder {
    private static final int JOIN_TEXT_SIZE = Utils.dpToPx(13);
    @Bind(R.id.txt_comment_item)
    SpannableTextView mTxtChatItem;
    private ChatGroupUtil mChatGroupUtil;

    public JoinedMessageHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mChatGroupUtil = getInstance(itemView.getContext());
    }

    public static JoinedMessageHolder create(ViewGroup parent) {
        return new JoinedMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_litter_text_chat, parent, false));
    }

    public void bindTo(ChatItemModelClass item, ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener){
        itemView.setBackgroundResource(getItemBackground(item.rank));
        CharSequence formattedMessage = mChatGroupUtil.formatJoinMessage(item.getChatDisplayName(), itemView.getContext().getString(R.string.message_joined), new ClickableSpanNoUnderline() {
            @Override
            public void onClick(View widget) {
                if (chatGroupClickListener != null){
                    chatGroupClickListener.onDisplayNameClicked(item);
                }
            }
        }, new ClickableSpanNoUnderline(){
            @Override
            public void onClick(View widget) {
                if (chatGroupClickListener != null){
                    chatGroupClickListener.onMessageClicked(item);
                }
            }
        },item.rank != -1 ? new ClickableImageSpan(mChatGroupUtil.mTopFanDrawable[item.rank]) {
            @Override
            public void onClick(View view) {
                if (chatGroupClickListener != null) {
                    chatGroupClickListener.onDisplayNameClicked(item);
                }
            }
        } : null);
        TextLayoutBuilder textLayoutBuilder = mChatGroupUtil.createLayoutBuilder();
        TextLayoutBuilder strokeLayoutBuilder = mChatGroupUtil.createStrokeLayoutBuilder();
        textLayoutBuilder.setTextSize(JOIN_TEXT_SIZE).setText(formattedMessage);
        strokeLayoutBuilder.setTextSize(JOIN_TEXT_SIZE).setText(mChatGroupUtil.setStrokeSpan(formattedMessage));
        this.mTxtChatItem.setLayout(textLayoutBuilder.build(), strokeLayoutBuilder.build());
    }

    private @DrawableRes
    int getItemBackground(int rank){
        switch (rank){
            case 0:
                return R.drawable.bg_join_message_topfan1;
            case 1:
                return R.drawable.bg_join_message_topfan2;
            case 2:
                return R.drawable.bg_join_message_topfan3;
            default:
                return R.drawable.bg_join_chat_group;
        }
    }
}
