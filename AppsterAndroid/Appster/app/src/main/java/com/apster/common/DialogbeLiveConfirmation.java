package com.apster.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appster.R;

/**
 * Created by thanhbc on 1/7/17.
 */

public class DialogbeLiveConfirmation {

    public interface OnBeLiveDialogClickedListener {
        void onConfirmationClicked();
    }

    public interface OnCancelBeLiveDialogClickedListener {
        void onCancellationClicked();
    }

    public interface OnBeLiveDialogClickedListenerViewCallback {
        void onViewClickedCallback(View view);
    }

    public interface OnBeLiveDialogEditTextCallback {
        void onEditTextContent(String value);
    }

    private Dialog mDialog;
    String title;
    CharSequence message;
    String cancelText;
    String confirmText;
    boolean isSingleAction;
    boolean alignRightMessage;
    private boolean showTopOfWindow;
    private boolean isTextIsSelectable;
    boolean isPasswordBox;
    OnBeLiveDialogClickedListener confirmActionCallback;
    OnBeLiveDialogClickedListenerViewCallback viewClickedCallback;
    OnCancelBeLiveDialogClickedListener cancelActionCallback;
    OnBeLiveDialogEditTextCallback textCallback;
    public DialogbeLiveConfirmation(Builder builder) {
        title = builder.title;
        message = builder.message;
        confirmText = builder.confirmText;
        cancelText = builder.cancelText;
        isSingleAction = builder.singleAction;
        confirmActionCallback = builder.confirmActionCallback;
        viewClickedCallback = builder.viewClickedCallback;
        cancelActionCallback = builder.cancelActionCallback;
        textCallback = builder.textCallback;
        alignRightMessage = builder.alignRightMessage;
        showTopOfWindow = builder.showTopOfWindow;
        isPasswordBox = builder.isPasswordBox;
        isTextIsSelectable = builder.isTextIsSelectable;
    }


    public void show(Context context) {

        mDialog = new Dialog(context);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_confirmation);
        TextView tvDialogTitle = mDialog.findViewById(R.id.dialog_title);
        TextView tvDialogContent = mDialog.findViewById(R.id.dialog_message);
        Button btnCancel = mDialog.findViewById(R.id.cancel);
        EditText editText = mDialog.findViewById(R.id.edtPassword);
        btnCancel.setOnClickListener(v -> dismiss());
        Button btnOK = (Button) mDialog.findViewById(R.id.ok);

        btnOK.setOnClickListener(view -> {
            if (confirmActionCallback != null) {
                confirmActionCallback.onConfirmationClicked();
            }
            if (viewClickedCallback != null) {
                viewClickedCallback.onViewClickedCallback(view);
            }
            if (textCallback!=null){
                textCallback.onEditTextContent(String.valueOf(editText.getText()));
            }
                dismiss();
        });

        btnCancel.setOnClickListener(view -> {
            if (cancelActionCallback != null) {
                cancelActionCallback.onCancellationClicked();
            }

            dismiss();
        });

        if (cancelText == null)
            cancelText = context.getString(R.string.btn_text_cancel);
        btnCancel.setText(cancelText);
        if (confirmText == null)
            confirmText = context.getString(R.string.btn_text_ok);
        btnOK.setText(confirmText);

        if (isSingleAction) btnCancel.setVisibility(View.GONE);
        tvDialogTitle.setText(title);
        tvDialogTitle.setVisibility((TextUtils.isEmpty(title)) ? View.GONE : View.VISIBLE);

        if (alignRightMessage) {
            tvDialogContent.setGravity(Gravity.LEFT | Gravity.CENTER);
        }

        if(isPasswordBox){
            mDialog.findViewById(R.id.llMessageContainer).setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
        }
        tvDialogContent.setText(message);
        tvDialogContent.setTextIsSelectable(isTextIsSelectable);

        if (showTopOfWindow) {
            mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    public boolean isShowing() {
        return mDialog != null && mDialog.isShowing();
    }

    public static final class Builder {
        private String title;
        private CharSequence message;
        private String confirmText;
        private String cancelText;
        private boolean singleAction;
        private boolean alignRightMessage;
        private boolean showTopOfWindow;
        private boolean isTextIsSelectable = false;
        private boolean isPasswordBox = false;
        private OnBeLiveDialogClickedListener confirmActionCallback = null;
        private OnBeLiveDialogClickedListenerViewCallback viewClickedCallback = null;
        private OnCancelBeLiveDialogClickedListener cancelActionCallback = null;
        private OnBeLiveDialogEditTextCallback textCallback = null;

        public Builder() {
        }

        public DialogbeLiveConfirmation build() {
            return new DialogbeLiveConfirmation(this);
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder message(CharSequence val) {
            message = val;
            return this;
        }

        public Builder isPasswordBox(boolean requirePass) {
            this.isPasswordBox = requirePass;
            return this;
        }

        public Builder alignRightMessage(boolean isRight) {
            alignRightMessage = isRight;
            return this;
        }

        public Builder showTopWindow(boolean isTop) {
            showTopOfWindow = isTop;
            return this;
        }


        public Builder confirmText(String val) {
            confirmText = val;
            return this;
        }

        public Builder cancelText(String val) {
            cancelText = val;
            return this;
        }

        public Builder singleAction(boolean isSingleAction) {
            singleAction = isSingleAction;
            return this;
        }

        public Builder setTextSelectable(boolean bool) {
            this.isTextIsSelectable = bool;
            return this;
        }

        public Builder onConfirmClicked(OnBeLiveDialogClickedListener beLiveDialogClickedListener) {
            this.confirmActionCallback = beLiveDialogClickedListener;
            return this;
        }

        public Builder onViewClickedCallback(OnBeLiveDialogClickedListenerViewCallback viewClickedCallback) {
            this.viewClickedCallback = viewClickedCallback;
            return this;
        }

        public Builder onCancelClicked(OnCancelBeLiveDialogClickedListener cancelActionCallback) {
            this.cancelActionCallback = cancelActionCallback;
            return this;
        }

        public Builder setPasswordBox(boolean passwordBox) {
            isPasswordBox = passwordBox;
            return this;
        }

        public Builder onEditTextValue(OnBeLiveDialogEditTextCallback textCallback) {
            this.textCallback = textCallback;
            return this;
        }

    }
}
