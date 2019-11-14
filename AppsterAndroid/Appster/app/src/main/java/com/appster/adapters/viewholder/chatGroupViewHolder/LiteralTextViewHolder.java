package com.appster.adapters.viewholder.chatGroupViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
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
import timber.log.Timber;

import static com.appster.utility.ChatGroupUtil.ClickableSpanNoUnderline;
import static com.appster.utility.ChatGroupUtil.getInstance;

/**
 * Created by linh on 05/06/2017.
 */

public class LiteralTextViewHolder extends RecyclerView.ViewHolder {
    private static final int USER_TEXT_SIZE = Utils.dpToPx(13);
    private static final int DEFAULT_LITERAL_TEXT_CHAT_COLOR = Color.parseColor("#FFFFFF");

    @Bind(R.id.txt_comment_item)
    SpannableTextView mTxtChatItem;
    private ChatGroupUtil mChatGroupUtil;

    public LiteralTextViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mChatGroupUtil = getInstance(itemView.getContext());
    }

    public static LiteralTextViewHolder create(ViewGroup parent){
        return new LiteralTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_litter_text_chat, parent, false));
    }

    public void bindTo(ChatItemModelClass item, ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener){
        itemView.setBackgroundResource(getItemBackground(item.rank));
        int messageColor = getMessageColorByRank(itemView.getContext(),item.rank, item.getProfileColor());
        CharSequence message = mChatGroupUtil.formatStringMessage(item.getChatDisplayName(), item.getMsg(), messageColor, new ClickableSpanNoUnderline() {
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
        textLayoutBuilder.setTextSize(USER_TEXT_SIZE).setText(message);
        strokeLayoutBuilder.setTextSize(USER_TEXT_SIZE).setText(mChatGroupUtil.setStrokeSpan(message));
        this.mTxtChatItem.setLayout(textLayoutBuilder.build(), strokeLayoutBuilder.build());
    }

    private @ColorInt int parseProfileColor(String strColor){
        int intColor = 0;
        try{
            if (!TextUtils.isEmpty(strColor)) {
                intColor = Color.parseColor(strColor);
            }
        }catch (Exception e){
            Timber.e(e);
        }finally {
            if (intColor == 0){
                intColor = DEFAULT_LITERAL_TEXT_CHAT_COLOR;
            }
        }
        return intColor;
    }

    private @DrawableRes
    int getItemBackground(int rank){
        switch (rank){
            case 0:
                return R.drawable.bg_chat_message_topfan1;
            case 1:
                return R.drawable.bg_chat_message_topfan2;
            case 2:
                return R.drawable.bg_chat_message_topfan3;
            default:
                return R.drawable.bg_normal_chat_message;
        }
    }

    private int getMessageColorByRank(Context context, int rank, String profileColor){
        switch (rank){
            case 0:
                return ContextCompat.getColor(context, R.color.chat_message_color_top_fan1);
            case 1:
                return ContextCompat.getColor(context, R.color.chat_message_color_top_fan2);
            case 2:
                return ContextCompat.getColor(context, R.color.chat_message_color_top_fan3);
            default:
                return parseProfileColor(profileColor);
        }
    }
}
