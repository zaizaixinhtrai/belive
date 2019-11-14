package com.appster.features.login.phoneLogin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.core.BeLiveDefaultTheme;
import com.appster.core.BeLiveThemeHelper;
import com.appster.customview.CustomFontTextView;
import com.appster.features.mvpbase.BaseContract;
import com.appster.manager.ShowErrorManager;
import com.appster.utility.RxUtils;
import com.apster.common.Constants;
import com.apster.common.Utils;
import com.pack.progresshud.ProgressHUD;
import com.pack.utility.DialogInfoUtility;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by linh on 23/10/2017.
 */

public abstract class BasePhoneSignInActivity extends AppCompatActivity implements BaseContract.View{
    @Bind(R.id.txt_title)
    CustomFontTextView mTxtTitle;

    public ProgressHUD dialog;
    public DialogInfoUtility utility;

    protected CompositeSubscription mCompositeSubscription;
    private BeLiveThemeHelper mBeLiveThemeHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_phone_signin);
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        ViewGroup root = (ViewGroup) findViewById(R.id.root);
        root.addView(LayoutInflater.from(this).inflate(setContentLayout(), root, false));
        root.setOnClickListener(v -> Utils.hideSoftKeyboard((Activity) v.getContext()));
        ButterKnife.bind(this);
        mBeLiveThemeHelper = new BeLiveDefaultTheme();
        if(mBeLiveThemeHelper.isTransparentStatusBarRequired()) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        mTxtTitle.setText(setTitle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_back)
    void onBackButtonClicked(){
        Utils.hideSoftKeyboard(this);
        onBackPressed();
    }

    //======== mvp callbacks =======================================================================
    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void showProgress() {
        if (dialog == null) {
            dialog = new ProgressHUD(this,
                    R.style.ProgressHUD);
            dialog.setTitle("");
            dialog.setContentView(R.layout.progress_hudd);
//            String message = getString(R.string.connecting_msg);
//            if (message == null || message.length() == 0) {
            dialog.findViewById(R.id.message).setVisibility(View.VISIBLE);
//            } else {
//                TextView txt = (TextView) dialog.findViewById(R.id.message);
//                txt.setText(message);
//            }
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.2f;
            dialog.getWindow().setAttributes(lp);
        }
        dialog.show();
    }

    @Override
    public void hideProgress() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (IllegalArgumentException error) {
                Timber.e(error);
            }
        }
    }

    @Override
    public void loadError(String errorMessage, int code) {
        if (this.isFinishing()) {
            return;
        }
        if (utility == null) utility = new DialogInfoUtility();

        if (code == Constants.RETROFIT_ERROR) {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                utility.showMessage(getString(R.string.app_name), getString(R.string.check_your_connection), this);
            } else {
                utility.showMessage(getString(R.string.app_name), getString(R.string.activity_sign_unknown_error), this);
            }
        } else if (code == ShowErrorManager.account_deactivate_or_invalid_email ||
                code == ShowErrorManager.account_deactivated_or_suspended ||
                code == ShowErrorManager.authentication_error) {
            View.OnClickListener mclick = v -> AppsterApplication.logout(v.getContext());

            utility.showMessage(getString(R.string.app_name), errorMessage, this, mclick);

        } else {
            utility.showMessage(getString(R.string.app_name), errorMessage, this);
        }
    }

    //======= inner methods ========================================================================
    protected abstract @LayoutRes
    int setContentLayout();

    protected abstract String setTitle();

    public void loadError(String errorMessage) {
        loadError(errorMessage, 0);
    }

    protected Bundle translateAnimation(){
        return ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left).toBundle();
    }
}
