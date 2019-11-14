package com.appster.features.income.history.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.customview.CustomFontTextView;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.CenteredImageSpan;
import com.apster.common.Utils;
import com.domain.models.TransactionHistoryModel;
import com.pack.utility.SetDateTime;
import com.pack.utility.StringUtil;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 10/24/17.
 */

public class TransactionItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvTransactionTime)
    CustomFontTextView tvTransactionTime;
    @Bind(R.id.tvStarsDeducted)
    CustomFontTextView tvStarsDeducted;
    @Bind(R.id.tvConvertedValue)
    CustomFontTextView tvConvertedValue;
    @Bind(R.id.tvStatus)
    CustomFontTextView tvStatus;
    @Bind(R.id.ivStatus)
    ImageView ivStatus;

    private TransactionItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static TransactionItemViewHolder create(ViewGroup parent) {
        return new TransactionItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false));
    }

    public void bindTo(TransactionHistoryModel item) {
        if (item.type == TransactionHistoryModel.TransactionType.MASTER_BRAIN) {
            tvConvertedValue.setText(setTextColorWallet(String.format("SGD %s", cleanCurrencyValue(item.convertedValue)), getTextColorByType(item.type)));
            tvStarsDeducted.setText(addImageToText(itemView.getContext(), R.drawable.transaction_master_brain, 15, 15));
        } else {
            CharSequence convertedValue = formatContentWithColor(Color.parseColor(getTextColorByType(item.type)),
                    String.format(Locale.US, "%s %s", item.currency, cleanCurrencyValue(item.convertedValue)));
            tvStarsDeducted.setText(replaceStarsIcon(itemView.getContext(), String.format(Locale.US, "- :stars: %,d", item.starsDeducted), 12f, 12f));
            tvConvertedValue.setText(replaceGemIcon(itemView.getContext(), convertedValue, 7f, 11f));
        }

//        CharSequence statusValue =  getContentByStatus(item.status);
//        tvStatus.setText(statusValue);
        ImageLoaderUtil.displayMediaImage(itemView.getContext(), getImageByStatus(item.status), ivStatus);
//        ivStatus.setImageResource(getImageByStatus(item.status));
        tvTransactionTime.setText(SetDateTime.parseTimeForTransactionItem(item.transactionTime));
    }

    private int getImageByStatus(@TransactionHistoryModel.TransactionStatus int status) {
        switch (status) {
            case TransactionHistoryModel.TransactionStatus.ACCEPTED:
                return R.drawable.ic_cashout_hist_succeed;
            case TransactionHistoryModel.TransactionStatus.PENDING:
                return R.drawable.ic_cashout_hist_pending;
            case TransactionHistoryModel.TransactionStatus.REJECTED:
                return R.drawable.ic_cashout_hist_rejected;
        }
        return 0;
    }

    private String getTextColorByType(@TransactionHistoryModel.TransactionType int type) {
        switch (type) {
            case TransactionHistoryModel.TransactionType.GEMS:
                return "#67DFE5";
            case TransactionHistoryModel.TransactionType.CASH:
                return "#FFB000";
            case TransactionHistoryModel.TransactionType.MASTER_BRAIN:
                return "#2B4CFF";
            default:
                return "#67DFE5";
        }
    }


    private CharSequence getContentByStatus(@TransactionHistoryModel.TransactionStatus int status) {
        switch (status) {
            case TransactionHistoryModel.TransactionStatus.ACCEPTED:
                return formatContentWithColor(Color.parseColor("#6ECC56"), itemView.getContext().getString(R.string.accepted));

            case TransactionHistoryModel.TransactionStatus.PENDING:
                return formatContentWithColor(Color.parseColor("#aaaaaa"), itemView.getContext().getString(R.string.pending));

            case TransactionHistoryModel.TransactionStatus.REJECTED:
                return formatContentWithColor(Color.parseColor("#FF5167"), itemView.getContext().getString(R.string.rejected));
        }
        return "";
    }

    private CharSequence formatContentWithColor(int color, String content) {
        if (TextUtils.isEmpty(content)) {
            return new SpannableString("");
        }
        final int textLength = content.length();
        SpannableStringBuilder spannableString = new SpannableStringBuilder(content);

        spannableString.setSpan(new ForegroundColorSpan(color), 0, textLength, 0);
        return spannableString;
    }

    private CharSequence replaceStarsIcon(Context context, CharSequence source, float iconWidth, float iconHeight) {
        return replaceAllMatchedTextWithIconForHevelticalLightFont(context, ":stars:", source, R.drawable.icon_gift_currency, iconWidth, iconHeight);
    }

    private CharSequence replaceGemIcon(Context context, CharSequence source, float iconWidth, float iconHeight) {
        return replaceAllMatchedTextWithIcon(context, ":gem:", source, R.drawable.ic_gem, iconWidth, iconHeight);
    }

    private SpannableStringBuilder addImageToText(Context context, int drawableId, int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        drawable.setBounds(0, 0, Utils.dpToPx(width), Utils.dpToPx(height));

        SpannableStringBuilder ssb = new SpannableStringBuilder(" ");
        ssb.setSpan(new ImageSpan(drawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ssb;
    }

    private CharSequence replaceAllMatchedTextWithIcon(Context context, String patternToMatch, CharSequence source, @DrawableRes int iconResource, float iconWidth, float iconHeight) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder(source);
        Pattern pattern = Pattern.compile(patternToMatch);
        Matcher matcher = pattern.matcher(source);

        Bitmap gem = null;
        int sizeH = Utils.dpToPx(iconHeight);
        int sizeW = Utils.dpToPx(iconWidth);
        while (matcher.find()) {
            if (gem == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), iconResource);
                gem = Bitmap.createScaledBitmap(bitmap, sizeW, sizeH, true);
                bitmap.recycle();
            }
            ImageSpan span = new CenteredImageSpan(context, gem, ImageSpan.ALIGN_BASELINE) {
                @Override
                public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
                    Drawable drawable = getDrawable();
                    canvas.save();
                    Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                    int fontHeight = fmPaint.descent - fmPaint.ascent;
                    int centerY = y + fmPaint.descent - fontHeight / 2;
                    int transY = centerY - (drawable.getBounds().bottom - drawable.getBounds().top) / 2;
                    canvas.translate(x, transY);
                    drawable.draw(canvas);
                    canvas.restore();
                }

            };
            spannableString.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    private CharSequence replaceAllMatchedTextWithIconForHevelticalLightFont(Context context, String patternToMatch, CharSequence source, @DrawableRes int iconResource, float iconWidth, float iconHeight) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder(source);
        Pattern pattern = Pattern.compile(patternToMatch);
        Matcher matcher = pattern.matcher(source);

        Bitmap gem = null;
        int sizeH = Utils.dpToPx(iconHeight);
        int sizeW = Utils.dpToPx(iconWidth);
        while (matcher.find()) {
            if (gem == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), iconResource);
                gem = Bitmap.createScaledBitmap(bitmap, sizeW, sizeH, true);
                bitmap.recycle();
            }
            ImageSpan span = new CenteredImageSpan(context, gem, ImageSpan.ALIGN_BOTTOM);
            spannableString.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    private CharSequence setTextColorWallet(String text, String color) {
        if (text.isEmpty()) return null;
        SpannableStringBuilder spannableString = new SpannableStringBuilder(text);
        spannableString.setSpan(
                new ForegroundColorSpan(Color.parseColor(color)),
                0, text.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    private String cleanCurrencyValue(Double value) {
        return StringUtil.replaceCurrencyString(itemView.getContext().getString(((value % 1 == 0.0) ? R.string.dollar_amount_non_floating_point_with_non_currency
                : R.string.dollar_amount_with_non_currency), value));
    }
}
