package com.appster.features.login.phoneLogin.newPassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.ImageButton;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.appster.R;
import com.appster.customview.CustomFontButton;
import com.appster.customview.CustomFontEditText;
import com.appster.features.login.phoneLogin.BasePhoneSignInActivity;
import com.appster.features.login.phoneLogin.countrypicker.Country;
import com.appster.features.login.phoneLogin.signIn.PhoneSignInActivity;
import com.appster.features.regist.RegisterActivity;
import com.appster.tracking.EventTracker;
import com.appster.tracking.EventTrackingName;
import com.appster.utility.ConstantBundleKey;
import com.appster.webservice.request_models.PhoneLoginResetPasswordRequest;
import com.apster.common.Constants;
import com.apster.common.Utils;

import butterknife.Bind;
import butterknife.OnClick;

import static com.appster.features.login.phoneLogin.newPassword.PhoneSignInNewPasswordActivity.NewPassWordMode.MODE_NEW;
import static com.appster.features.login.phoneLogin.newPassword.PhoneSignInNewPasswordActivity.NewPassWordMode.MODE_RESET;

/**
 * Created by linh on 24/10/2017.
 */

public class PhoneSignInNewPasswordActivity extends BasePhoneSignInActivity implements PhoneSignInNewPasswordContract.View {
    private static final String ARG_MODE = "ARG_PASSWORD_MODE";
    private static final String ARG_COUNTRY = "ARG_COUNTRY";
    private static final String ARG_PHONE_NUMBER = "ARG_PHONE_NUMBER";
    private static final String ARG_OTP = "ARG_OTP";

    @Bind(R.id.btn_show_hide_password)
    ImageButton mBtnShowHidePassword;
    @Bind(R.id.edt_password)
    CustomFontEditText mEdtPassword;
    @Bind(R.id.btn_create_account)
    CustomFontButton mBtnCreateAccount;

    @NewPassWordMode
    private int mMode = MODE_NEW;

    @IntDef({MODE_NEW, MODE_RESET})
    public @interface NewPassWordMode{
        int MODE_NEW = 0;
        int MODE_RESET = 1;
    }

    private Country mCountry;
    private String mPhoneNumber;
    private String mOtp;

    PhoneSignInNewPasswordContract.UserActions mPresenter;

    public static Intent createIntent(Context context, Country country, String phoneNumber) {
        return createIntent(context, country, phoneNumber, "", MODE_NEW);
    }

    public static Intent createIntent(Context context, Country country, String phoneNumber, String otp, @NewPassWordMode int mode){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_COUNTRY, country);
        bundle.putString(ARG_PHONE_NUMBER, phoneNumber);
        bundle.putString(ARG_OTP, otp);
        bundle.putInt(ARG_MODE, mode);
        Intent intent = new Intent(context, PhoneSignInNewPasswordActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent createIntent(Context context, @NewPassWordMode int mode) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_MODE, mode);
        Intent intent = new Intent(context, PhoneSignInNewPasswordActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        extractBundleData();
        super.onCreate(savedInstanceState);
        mPresenter = new PhoneSignInNewPasswordPresenter();
        mPresenter.attachView(this);
        setupPasswordWatcher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected int setContentLayout() {
        return (mMode == MODE_NEW) ? R.layout.activity_phone_signup_new_password : R.layout.activity_phone_signin_new_password;
    }

    @Override
    protected String setTitle() {
        return getString((mMode == MODE_NEW) ? R.string.phone_login_signup : R.string.reset_password);
    }

    //====== event handlers ========================================================================
    @OnClick(R.id.btn_show_hide_password)
    void onShowHidePassword(){
        mBtnShowHidePassword.setSelected(!mBtnShowHidePassword.isSelected());
        if (mBtnShowHidePassword.isSelected()){
            mEdtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else{
            mEdtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        if (mEdtPassword.getText().length() > 0) {
            mEdtPassword.setSelection(mEdtPassword.getText().length());
        }
    }

    @OnClick(R.id.btn_create_account)
    void onCreateAccountClicked(){
        if (mMode == MODE_NEW) {
            gotoCreateProfileScreen();

        }else{//MODE_RESET
            PhoneLoginResetPasswordRequest request = new PhoneLoginResetPasswordRequest();
            request.otpToken = mOtp;
            request.password = mEdtPassword.getText().toString();
            mPresenter.resetPassword(request);
        }
    }

    //========= mvp callbacks ======================================================================
    @Override
    public void onResetPasswordSuccessfully() {
        gotoPhoneLoginScreen();
    }

    @Override
    public void onResetPasswordFailed(String message) {
        loadError(message);
    }

    //======== inner methods =======================================================================
    private void extractBundleData(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            //noinspection WrongConstant
            mMode = bundle.getInt(ARG_MODE, MODE_NEW);
            mCountry = bundle.getParcelable(ARG_COUNTRY);
            mPhoneNumber = bundle.getString(ARG_PHONE_NUMBER);
            mOtp = bundle.getString(ARG_OTP, "");
        }
    }

    private void setViewByMode(){
        if (mMode == MODE_NEW) return;

    }

    void setupPasswordWatcher(){
        mEdtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                boolean passwordInvalid = length >= Constants.MIN_LENGTH_USER_PASSWORD
                        && length <= Constants.MAX_LENGTH_USER_PASSWORD;
                mBtnCreateAccount.setEnabled(passwordInvalid);
            }
        });
    }

    public void gotoCreateProfileScreen() {
        Utils.hideSoftKeyboard(this);
        EventTracker.trackEvent(EventTrackingName.EVENT_LOGIN_SNS_CLICK,EventTrackingName.SNS_CLICK_PHONE,EventTrackingName.SNS_CLICK_PHONE);
        Intent intent = RegisterActivity.createIntent(this, ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_PHONE, mCountry.getDialingCodeWithoutPlusSign(), mPhoneNumber, mEdtPassword.getText().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(this, intent, translateAnimation());
        finish();
    }

    public void gotoPhoneLoginScreen(){
        Utils.hideSoftKeyboard(this);
        Intent intent = PhoneSignInActivity.createIntent(this, mCountry, mPhoneNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(this, intent, translateAnimation());
        finish();
    }
}
