package com.pack.utility;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View.OnClickListener;

import com.appster.R;
import com.apster.common.DialogbeLiveConfirmation;

public class DialogInfoUtility {

    private DialogbeLiveConfirmation lDialogresume;

    private DialogInfoUtility dialogInfoUtility;

    public DialogInfoUtility getInstance() {
        dialogInfoUtility = new DialogInfoUtility();

        return dialogInfoUtility;
    }

    public void showMessage(String title, CharSequence msg,
                            Context context) {
//        mContext = context;

        lDialogresume = new DialogbeLiveConfirmation.Builder()
                .confirmText(context.getString(R.string.btn_text_ok))
                .message(msg)
                .title(title)
                .singleAction(true)
                .build();
        lDialogresume.show(context);
    }


    public void showMessagePurchasedMore(Context context, final OnClickListener mClick) {

        String title = context.getString(R.string.gift_refill_more_dialog_title);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(context.getResources().getString(R.string.gift_refill_more_dialog_message)).append("  ")
                .setSpan(new ImageSpan(context, R.drawable.icon_gift_price_gem),
                        builder.length() - 1, builder.length(), 0);
        builder.append(" ?");
        lDialogresume = new DialogbeLiveConfirmation.Builder()
                .confirmText(context.getString(R.string.btn_text_ok))
                .message(builder)
                .cancelText(context.getString(R.string.btn_text_no))
                .title(title)
                .onViewClickedCallback(mClick::onClick)
                .build();
        lDialogresume.show(context);

    }


    public void showMessage(String title, CharSequence msg,
                            Context context, OnClickListener listener) {
        lDialogresume = new DialogbeLiveConfirmation.Builder()
                .confirmText(context.getString(R.string.btn_text_ok))
                .message(msg)
                .title(title)
                .singleAction(true)
                .onViewClickedCallback(listener::onClick)
                .build();
        lDialogresume.show(context);
    }

    public void showMessageWithSelectable(String title, CharSequence msg,
                                          Context context, OnClickListener listener) {
        lDialogresume = new DialogbeLiveConfirmation.Builder()
                .confirmText(context.getString(R.string.btn_text_ok))
                .message(msg)
                .title(title)
                .singleAction(true)
                .onViewClickedCallback(listener::onClick)
                .setTextSelectable(true)
                .build();
        lDialogresume.show(context);
    }

    public void showStreamMessage(String title, String msg,
                                  Context context, OnClickListener mClick) {

//        if (lDialogresume.isShowing()) {
//            return;
//        }

        showMessage(title, msg, context, mClick);
    }

    public void showMessageWithClickButton(String title, String msg,
                                           Context context, OnClickListener mClick) {
        lDialogresume = new DialogbeLiveConfirmation.Builder()
                .confirmText(context.getString(R.string.btn_text_update))
                .message(msg)
                .title(title)
                .singleAction(true)
                .onViewClickedCallback(mClick::onClick)
                .build();
        lDialogresume.show(context);
    }

    public void dismissDialog() {
        if (lDialogresume != null) lDialogresume.dismiss();
    }

    public void showForceUpdateMessage(String title, String msg,
                                       Context context, OnClickListener mClick) {

        lDialogresume = new DialogbeLiveConfirmation.Builder()
                .confirmText(context.getString(R.string.btn_text_update))
                .message(msg)
                .title(title)
                .singleAction(true)
                .onViewClickedCallback(mClick::onClick)
                .build();
        lDialogresume.show(context);
    }

    /**
     * Show the confirm dialog with 2 buttons style
     *
     * @param context         the context
     * @param title           the dialog title
     * @param content         the dialog content
     * @param okText          the text for positive button
     * @param cancelText      the text for negative button
     * @param confirmListener the listener for positive button click event
     * @param cancelListener  the listener for negative button click event
     */
    public void showConfirmMessage(Context context, String title, String content, String okText, String cancelText,
                                   DialogbeLiveConfirmation.OnBeLiveDialogClickedListener confirmListener,
                                   DialogbeLiveConfirmation.OnCancelBeLiveDialogClickedListener cancelListener) {

        lDialogresume = new DialogbeLiveConfirmation.Builder()
                .confirmText(okText)
                .message(content)
                .cancelText(cancelText)
                .title(title)
                .onConfirmClicked(confirmListener)
                .onCancelClicked(cancelListener)
                .build();
        lDialogresume.show(context);
    }

    /**
     * Check if the current dialog is showing or not
     *
     * @return true if the current dialog is showing. False otherwise
     */
    public boolean isShowing() {
        return lDialogresume != null && lDialogresume.isShowing();
    }
}
