package com.appster.utility;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.LineHeightSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.appster.R;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.customview.StrokeSpan;
import com.apster.common.ClickableImageSpan;
import com.apster.common.Utils;
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder;
import com.facebook.fbui.textlayoutbuilder.glyphwarmer.GlyphWarmerImpl;
import com.pack.utility.StringUtil;

import static androidx.core.text.TextDirectionHeuristicsCompat.LOCALE;
import static com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder.MEASURE_MODE_AT_MOST;

/**
 * Created by linh on 05/06/2017.
 */

public class ChatGroupUtil {
    private static ChatGroupUtil mInstance;
    private static final int TEXT_CHAT_SHADOW_RADIUS = Utils.dpToPx(1f);
    private static final int TEXT_CHAT_SHADOW_DX = -Utils.dpToPx(0.5f);
    private static final int TEXT_CHAT_SHADOW_DY = Utils.dpToPx(0.5f);
    private static final int WARNING_MESSAGE_COLOR = Color.parseColor("#FFFFFF");
    private static final int TEXT_CHAT_SHADOW_COLOR = Color.parseColor("#B3000093");
    private static final int DISPLAY_NAME_COLOR = Color.parseColor("#98d7de");
    public static final int LIKE_MESSAGE_COLOR = Color.parseColor("#FFFFFF");
    private static final int SHARING_MESSAGE_COLOR = Color.parseColor("#FFFFFF");
    private static final int ADMIN_MESSAGE_COLOR = Color.parseColor("#FF0000");
    private static final int JOIN_MESSAGE_COLOR = LIKE_MESSAGE_COLOR;
    private static final int LIVE_COMMERCE_ANNOUNCEMENT_MESSAGE = Color.parseColor("#FFFAA1");
    private static final int LIVE_COMMERCE_ANNOUNCEMENT_COUNT_VIEWER = Color.parseColor("#FFFAA1");
    public static final int TOP_FAN_CHAT_MESSAGE_COLOR = Color.parseColor("#FFE757");
    private static final int TOP_FAN_LIKE_MESSAGE_COLOR = TOP_FAN_CHAT_MESSAGE_COLOR;
    private static final int TOP_FAN_DISPLAY_NAME_COLOR = Color.parseColor("#88f28f");

    private CustomTypefaceSpan mTypefaceSpanOpenSansBold;
    public CustomTypefaceSpan mTypefaceSpanOpenSanssemiBold;
    private CustomTypefaceSpan mTypefaceSpanOpenExtraBold;

    public Drawable[] mTopFanDrawable;

    public static ChatGroupUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ChatGroupUtil(context);
        }
        return mInstance;
    }

    public synchronized TextLayoutBuilder createLayoutBuilder() {
        return new TextLayoutBuilder()
                .setShouldWarmText(true)
                .setGlyphWarmer(new GlyphWarmerImpl())
                .setShadowLayer(TEXT_CHAT_SHADOW_RADIUS, TEXT_CHAT_SHADOW_DX, TEXT_CHAT_SHADOW_DY, TEXT_CHAT_SHADOW_COLOR)
                .setSingleLine(false)
                .setWidth(Utils.getScreenWidth() - Utils.dpToPx(30), MEASURE_MODE_AT_MOST)
                .setTextDirection(LOCALE);
    }

    public synchronized TextLayoutBuilder createStrokeLayoutBuilder() {
        return new TextLayoutBuilder()
                .setShouldWarmText(true)
                .setGlyphWarmer(new GlyphWarmerImpl())
                .setSingleLine(false)
                .setWidth(Utils.getScreenWidth() - Utils.dpToPx(30), MEASURE_MODE_AT_MOST)
                .setTextDirection(LOCALE);
    }

    private ChatGroupUtil(Context context) {
        mTypefaceSpanOpenSansBold = new CustomTypefaceSpan("", Typeface.createFromAsset(context.getAssets(), "fonts/opensansbold.ttf"));
        mTypefaceSpanOpenSanssemiBold = new CustomTypefaceSpan("", Typeface.createFromAsset(context.getAssets(), "fonts/opensanssemibold.ttf"));
        mTypefaceSpanOpenExtraBold = new CustomTypefaceSpan("", Typeface.createFromAsset(context.getAssets(), "fonts/opensansextrabold.ttf"));
        initTopFanImageSpan(context);
    }

    private void initTopFanImageSpan(Context context) {
        mTopFanDrawable = new Drawable[3];
        mTopFanDrawable[0] = getTopFanDrawableByResource(context, R.drawable.ic_topfan_1);
        mTopFanDrawable[1] = getTopFanDrawableByResource(context, R.drawable.ic_topfan_2);
        mTopFanDrawable[2] = getTopFanDrawableByResource(context, R.drawable.ic_topfan_3);
    }

    private Drawable getTopFanDrawableByResource(Context context, @DrawableRes int drawableId) {
        Drawable d = ContextCompat.getDrawable(context, drawableId);
        d.setBounds(Utils.dpToPx(-3), 0, Utils.dpToPx(13), Utils.dpToPx(16));
        return d;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////                                            /////////////////////////
    //////////////////////////           LITERAL MESSAGE SECTION           /////////////////////////
    //////////////////////////                                             /////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public CharSequence formatAdminMessage(String displayName, String msg) {
        return formatStringMessage(displayName, DISPLAY_NAME_COLOR, msg, ADMIN_MESSAGE_COLOR, " ", null, null, null);
    }

    public CharSequence formatFollowMessage(String displayName, String message, @ColorInt int messageColor, ClickableSpanNoUnderline displayNameClickable, ClickableSpanNoUnderline messageClickable, ClickableImageSpan clickableImageSpan) {
        return formatStringMessage(displayName, message, messageColor, displayNameClickable, messageClickable, clickableImageSpan);
    }

    public CharSequence formatSharingMessage(String displayName, String message, ClickableSpanNoUnderline displayNameClickable, ClickableSpanNoUnderline messageClickable, ClickableImageSpan clickableImageSpan) {
        return formatStringMessage(displayName, message, SHARING_MESSAGE_COLOR, displayNameClickable, messageClickable, clickableImageSpan);
    }


    public CharSequence formatJoinMessage(String displayName, String message, ClickableSpanNoUnderline displayNameClickable, ClickableSpanNoUnderline messageClickable, ClickableImageSpan clickableImageSpan) {
        return formatStringMessage(displayName, message, JOIN_MESSAGE_COLOR, displayNameClickable, messageClickable, clickableImageSpan);
    }


    public CharSequence formatGiftMessage(String displayName, String message, @ColorInt int messageColor, ClickableSpanNoUnderline displayNameClickable, ClickableSpanNoUnderline messageClickable, ClickableImageSpan clickableImageSpan) {
        return formatStringMessage(displayName, message, messageColor, displayNameClickable, messageClickable, clickableImageSpan);
    }

    public CharSequence formatWaningMessage(String message) {
        SpannableString warningMessage = formatString(message, WARNING_MESSAGE_COLOR, Typeface.NORMAL, mTypefaceSpanOpenSanssemiBold);
        warningMessage.setSpan((LineHeightSpan) (text, start, end, spanstartv, v, fm) -> {
            fm.bottom += Utils.dpToPx(14);
            fm.descent += Utils.dpToPx(14);
        }, 0, "Welcome!".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return warningMessage.subSequence(0, message.length());
    }

    public CharSequence formatWelcomeMessage(Context context,String message, String titleStrem) {
        SpannableString warningMessage = formatString(message, WARNING_MESSAGE_COLOR, Typeface.NORMAL, mTypefaceSpanOpenSanssemiBold);
        warningMessage.setSpan(getCustomTypefaceSpanForName(context),
                message.indexOf(titleStrem), message.indexOf(titleStrem) + titleStrem.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return SpannableString.valueOf(warningMessage);
    }

    private CustomTypefaceSpan getCustomTypefaceSpanForName(Context context) {
        Typeface americanFont = Typeface.createFromAsset(context.getAssets(),
                "fonts/opensansextrabold.ttf");
        return new CustomTypefaceSpan("sans-serif", Typeface.create(americanFont, Typeface.BOLD));
    }

    public CharSequence formatLiveCommerceAnnouncementMessage(Context context, int count){
        String viewer = (count > 1) ? context.getString(R.string.viewers) : context.getString(R.string.viewer);
        viewer = String.valueOf(count) + viewer;
        String message = context.getString(R.string.live_commerce_announcement_message);;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(formatString(viewer, LIVE_COMMERCE_ANNOUNCEMENT_COUNT_VIEWER, Typeface.BOLD, mTypefaceSpanOpenExtraBold, null))
                .append(" ")
                .append(formatString(message, LIVE_COMMERCE_ANNOUNCEMENT_MESSAGE, Typeface.NORMAL, mTypefaceSpanOpenSanssemiBold, null));
        return builder.subSequence(0, builder.length());
    }

    public CharSequence formatLiveCommerceAnnouncementMessage(String message){
        return new SpannableStringBuilder(formatString(message, LIVE_COMMERCE_ANNOUNCEMENT_MESSAGE, Typeface.BOLD, mTypefaceSpanOpenExtraBold, null)).subSequence(0, message.length());
    }

    public CharSequence formatStringMessage(String displayName, String message, @ColorInt int messageColor, ClickableSpanNoUnderline displayNameClickable, ClickableSpanNoUnderline messageClickable, ClickableImageSpan clickableImageSpan) {
        return formatStringMessage(displayName, DISPLAY_NAME_COLOR, message, messageColor, " ", displayNameClickable, messageClickable, clickableImageSpan);
    }

    public CharSequence formatStringMessage(String displayName, @ColorInt int displayNameColor, String message, @ColorInt int messageColor, String divider, ClickableSpanNoUnderline displayNameClickable, ClickableSpanNoUnderline messageClickable, ClickableImageSpan clickableImageSpan) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (clickableImageSpan != null) {
            builder.append(divider)
                    .append(formatTopFanImage(clickableImageSpan))
                    .append(divider);
        }
        builder.append(formatString(StringUtil.subStringWithPresetMaxLength(displayName), clickableImageSpan != null ? TOP_FAN_DISPLAY_NAME_COLOR : displayNameColor, Typeface.BOLD, mTypefaceSpanOpenSansBold, displayNameClickable))
                .append(divider)
                .append(formatString(message, messageColor, Typeface.NORMAL, mTypefaceSpanOpenSanssemiBold, messageClickable))
                .append(divider);

        return builder.subSequence(0, builder.length());
    }

    /**
     * @param style        @param style  The style (normal, bold, italic) of the typeface.
     *                     e.g. {@link android.graphics.Typeface#NORMAL}, {@link android.graphics.Typeface#BOLD},
     *                     {@link android.graphics.Typeface#ITALIC}ITALIC, {@link android.graphics.Typeface#BOLD_ITALIC}
     * @param typefaceSpan font style
     */
    public SpannableString formatString(String text, @ColorInt int color, int style, TypefaceSpan typefaceSpan) {
        return formatString(text, color, style, typefaceSpan, null);
    }

    private SpannableString formatString(String text, @ColorInt int color, int style, TypefaceSpan typefaceSpan, ClickableSpanNoUnderline clickableSpan) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableString("");
        }
        final int textLength = text.length();
        SpannableString spannableString = new SpannableString(text);
        if (clickableSpan != null) {
            spannableString.setSpan(clickableSpan, 0, textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        spannableString.setSpan(new ForegroundColorSpan(color), 0, textLength, 0);
        spannableString.setSpan(new StyleSpan(style), 0, textLength, 0);
        spannableString.setSpan(typefaceSpan, 0, textLength, 0);
        return spannableString;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////                                            /////////////////////////
    //////////////////////////             IMAGE MESSAGE SECTION           /////////////////////////
    //////////////////////////                                             /////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public CharSequence formatLikeMessage(Context context, String displayName, int messageColor, ClickableSpanNoUnderline displayNameClickable, ClickableSpanNoUnderline messageClickable, ClickableImageSpan clickableImageSpan) {
        String divider = " ";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (clickableImageSpan != null) {
            builder.append(divider)
                    .append(formatTopFanImage(clickableImageSpan))
                    .append(divider);
        }
        builder.append(formatString(StringUtil.subStringWithPresetMaxLength(displayName), clickableImageSpan != null ? TOP_FAN_DISPLAY_NAME_COLOR : DISPLAY_NAME_COLOR, Typeface.BOLD, mTypefaceSpanOpenSansBold, displayNameClickable))
                .append(divider)
                .append(formatString(context.getString(R.string.message_sent), messageColor, Typeface.NORMAL, mTypefaceSpanOpenSanssemiBold, messageClickable))
                .append(divider)
                .append(formatImage(context, "image", R.drawable.icon_heart_mini))
                .append(divider);

        return builder.subSequence(0, builder.length());
    }

    private SpannableString formatTopFanImage(ClickableImageSpan clickableImageSpan) {
        SpannableString spannableString = new SpannableString("image");
//        CenteredImageSpan centeredImageSpan = new CenteredImageSpan(mTopFanDrawable[rank],ImageSpan.ALIGN_BASELINE);
        spannableString.setSpan(clickableImageSpan, 0, "image".length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private SpannableString formatImage(Context context, String text, @DrawableRes int drawableRes) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableString("");
        }
        ImageSpan imgSpan = new ImageSpan(context, BitmapFactory.decodeResource(context.getResources(), drawableRes), ImageSpan.ALIGN_BASELINE);
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(imgSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence setStrokeSpan(CharSequence charSequence) {
        SpannableString spannableString = new SpannableString(charSequence);
        spannableString.setSpan(new StrokeSpan(), 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public static abstract class ClickableSpanNoUnderline extends ClickableSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false); // set to false to remove underline
        }
    }
}
