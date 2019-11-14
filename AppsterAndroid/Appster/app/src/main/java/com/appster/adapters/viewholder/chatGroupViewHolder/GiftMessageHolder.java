package com.appster.adapters.viewholder.chatGroupViewHolder;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.adapters.ChatGroupDelegateAdapter;
import com.appster.message.ChatItemModelClass;
import com.appster.utility.ChatGroupUtil;
import com.appster.utility.ImageLoaderUtil;
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

public class GiftMessageHolder extends RecyclerView.ViewHolder {
    private static final int GIFT_MESSAGE_TEXT_SIZE = Utils.dpToPx(13);
    private static final int GIFT_IMAGE_SIZE = Utils.dpToPx(20);
    private static final int GIFT_MESSAGE_COLOR = Color.parseColor("#FFFAA1");

    @Bind(R.id.txt_comment_item)
    SpannableTextView mTxtMessage;
    @Bind(R.id.txt_combo)
    SpannableTextView mTxtCombo;
    @Bind(R.id.imv_gift)
    ImageView imvGift;
    private ChatGroupUtil mChatGroupUtil;

    public GiftMessageHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mChatGroupUtil = getInstance(itemView.getContext());
    }

    public static GiftMessageHolder create(ViewGroup parent) {
        return new GiftMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_chat, parent, false));
    }

    public void bindTo(ChatItemModelClass item, ChatGroupDelegateAdapter.ChatGroupClickListener chatGroupClickListener) {
        itemView.setBackgroundResource(getItemBackground(item.rank));
        ImageLoaderUtil.displayMediaImage(itemView.getContext(), item.getGiftImage(), imvGift, GIFT_IMAGE_SIZE, GIFT_IMAGE_SIZE);
        final String messageGift = !item.getGiftName().isEmpty() ?
                String.format(itemView.getResources().getQuantityString(R.plurals.gifts,item.getGiftCombo()), item.getGiftName()) :
                item.getMsg();

        CharSequence formattedMessage = mChatGroupUtil.formatGiftMessage(item.getChatDisplayName(), messageGift, GIFT_MESSAGE_COLOR, new ClickableSpanNoUnderline() {
            @Override
            public void onClick(View widget) {
                if (chatGroupClickListener != null) {
                    chatGroupClickListener.onDisplayNameClicked(item);
                }
            }
        }, new ClickableSpanNoUnderline() {
            @Override
            public void onClick(View widget) {
                if (chatGroupClickListener != null) {
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
        textLayoutBuilder.setTextSize(GIFT_MESSAGE_TEXT_SIZE).setText(formattedMessage);
        strokeLayoutBuilder.setTextSize(GIFT_MESSAGE_TEXT_SIZE).setText(mChatGroupUtil.setStrokeSpan(formattedMessage));
        this.mTxtMessage.setLayout(textLayoutBuilder.build(), strokeLayoutBuilder.build());

        if (item.getGiftCombo() > 1) {
            SpannableString combo = mChatGroupUtil.formatString("x" + " " + item.getGiftCombo() + " !", GIFT_MESSAGE_COLOR, Typeface.NORMAL, mChatGroupUtil.mTypefaceSpanOpenSanssemiBold);
            TextLayoutBuilder comboTextLayoutBuilder = mChatGroupUtil.createLayoutBuilder();
            TextLayoutBuilder comboStrokeLayoutBuilder = mChatGroupUtil.createStrokeLayoutBuilder();
            comboTextLayoutBuilder.setTextSize(GIFT_MESSAGE_TEXT_SIZE).setText(combo);
            comboStrokeLayoutBuilder.setTextSize(GIFT_MESSAGE_TEXT_SIZE).setText(mChatGroupUtil.setStrokeSpan(combo));
            this.mTxtCombo.setLayout(comboTextLayoutBuilder.build(), comboStrokeLayoutBuilder.build());
            mTxtCombo.setVisibility(View.VISIBLE);
        } else {
            mTxtCombo.setVisibility(View.GONE);
        }
    }

    private @DrawableRes
    int getItemBackground(int rank){
        switch (rank){
            case 0:
                return R.drawable.bg_gift_message_topfan1;
            case 1:
                return R.drawable.bg_gift_message_topfan2;
            case 2:
                return R.drawable.bg_gift_message_topfan3;
            default:
                return R.drawable.bg_gift_chat_message;
        }
    }
}
