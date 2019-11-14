package com.appster.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.appster.R;

/**
 * Created by User on 8/19/2016.
 */
public class ExchangeRateDialog {
    Dialog lDialogresume;

    public static ExchangeRateDialog getInstance() {
        return new ExchangeRateDialog();
    }

    public void show(Context context, String title, View.OnClickListener click) {

        lDialogresume = new Dialog(context);
        lDialogresume.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lDialogresume.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        lDialogresume.setContentView(R.layout.dialog_exchange_rate);

        (lDialogresume.findViewById(R.id.ok))
                .setOnClickListener(click);

        lDialogresume.show();
    }

    public void dismiss() {
        lDialogresume.dismiss();
    }
}
