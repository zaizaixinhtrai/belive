package com.appster.features.login.phoneLogin.signIn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.customview.CustomFontButton;
import com.appster.customview.CustomFontEditText;
import com.appster.customview.CustomFontTextView;
import com.appster.features.login.LoginHelper;
import com.appster.features.login.phoneLogin.BasePhoneSignInActivity;
import com.appster.features.login.phoneLogin.countrypicker.Country;
import com.appster.features.login.phoneLogin.fogotPassword.PhoneSignInForgotPasswordActivity;
import com.appster.main.MainActivity;
import com.appster.tracking.EventTracker;
import com.appster.tracking.EventTrackingName;
import com.appster.utility.DeviceInfo;
import com.appster.webservice.request_models.PhoneLoginForgotPasswordRequestModel;
import com.appster.webservice.request_models.PhoneLoginRequestModel;
import com.appster.webservice.response.PhoneLoginForgotPasswordResponse;
import com.apster.common.Constants;
import com.apster.common.Utils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by linh on 24/10/2017.
 */

public class PhoneSignInActivity extends BasePhoneSignInActivity implements PhoneSignInContract.View {
    private static final String ARG_COUNTRY = "ARG_COUNTRY";
    private static final String ARG_PHONE_NUMBER = "ARG_PHONE_NUMBER";

    @Bind(R.id.txt_country_code)
    CustomFontTextView mTxtCountryCode;
    @Bind(R.id.edt_phone_number)
    CustomFontTextView mTxtPhoneNumber;
    @Bind(R.id.edt_password)
    CustomFontEditText mEdtPassword;
    @Bind(R.id.btn_show_hide_password)
    ImageButton mBtnShowHidePassword;
    @Bind(R.id.btn_signin)
    CustomFontButton mBtnSignIn;
    @Bind(R.id.txt_error_message)
    CustomFontTextView mTxtErrorMessage;
    @Bind(R.id.txt_forgot_password)
    CustomFontTextView mTxtForgotPassword;

    private Country mCountry;
    private String mPhoneNumber;

    private PhoneSignInContract.UserActions mPresenter;

    public static Intent createIntent(Context context, Country country, String phoneNumber){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_COUNTRY, country);
        bundle.putString(ARG_PHONE_NUMBER, phoneNumber);
        Intent intent = new Intent(context, PhoneSignInActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mCountry = bundle.getParcelable(ARG_COUNTRY);
            mPhoneNumber = bundle.getString(ARG_PHONE_NUMBER);
            bindView();
        }
        mPresenter = new PhoneSignPresenter();
        mPresenter.attachView(this);
        mTxtForgotPassword.setPaintFlags(mTxtForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        setupPasswordWatcher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected int setContentLayout() {
        return R.layout.acitivty_phone_signin;
    }

    @Override
    protected String setTitle() {
        return getString(R.string.phone_signin);
    }

    //======== event handlers ======================================================================
    @OnClick(R.id.btn_show_hide_password)
    void onShowHidePassword(){
        mBtnShowHidePassword.setSelected(!mBtnShowHidePassword.isSelected());
        if (mBtnShowHidePassword.isSelected()){
            mEdtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else{
            mEdtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        mEdtPassword.setCustomFont(this, getString(R.string.font_helveticaneuelight));
        if (mEdtPassword.getText().length() > 0) {
            mEdtPassword.setSelection(mEdtPassword.getText().length());
        }
    }

    @OnClick(R.id.btn_signin)
    void onSignInButtonClicked(){
        PhoneLoginRequestModel request = new PhoneLoginRequestModel();
        request.countryCode = mCountry.getDialingCodeWithoutPlusSign();
        request.phoneNumber = mPhoneNumber;
        request.passWord = mEdtPassword.getText().toString();
        AppsterApplication.mAppPreferences.setDevicesUDID(DeviceInfo.getDeviceDetail(this));
        request.setDevice_udid(AppsterApplication.mAppPreferences.getDevicesUDID());
        request.setDevice_token(AppsterApplication.mAppPreferences.getDevicesToken());
        mPresenter.loginAppsterServerWithPhoneNumber(request);
    }

    @OnClick(R.id.txt_forgot_password)
    void onForgotPasswordClicked(){
        PhoneLoginForgotPasswordRequestModel request =
                new PhoneLoginForgotPasswordRequestModel(mCountry.getDialingCodeWithoutPlusSign(), mPhoneNumber);
        mPresenter.requestVerificationCode(request);
    }

    //======= mvp methods ==========================================================================
    @Override
    public void onLoginSuccessfully() {
        gotoMainScreen();
    }

    @Override
    public void onUserNotFound(String message) {
        loadError(message);
    }

    @Override
    public void onAccountSuspended() {
        LoginHelper.INSTANCE.showSuspendedDialog(this);
    }

    @Override
    public void onAdminBlocked(String message) {
        LoginHelper.INSTANCE.showBlockedDialog(this, message);
    }

    @Override
    public void onPasswordInvalid() {
        mEdtPassword.setCustomDrawableEnd(R.drawable.ic_wrong);
        mEdtPassword.setPadding(Utils.dpToPx(30), 0, 0, 0);
        mTxtErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoginFailed() {

    }

    @Override
    public void onRequestVerificationCodeSuccessfully(PhoneLoginForgotPasswordResponse response) {
        gotoResetPasswordScreen(response.otp);
    }

    @Override
    public void onRequestVerificationCodeFailed(String message) {
        loadError(message);
    }

    @Override
    public void onRequestVerificationReachedLimited(String message) {
        loadError(message);
    }

    //======= inner methods ========================================================================
    private void bindView(){
        String str = mCountry.getIsoCode() + " " + mCountry.getDialingCode();
        mTxtCountryCode.setText(str);
        mTxtPhoneNumber.setText(mPhoneNumber);
    }

    private void setupPasswordWatcher(){
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
                boolean validPassword = length >= Constants.MIN_LENGTH_USER_PASSWORD && length <= Constants.MAX_LENGTH_USER_PASSWORD;
                mBtnSignIn.setEnabled(validPassword);

                mEdtPassword.setCustomDrawableEnd(0);
                mEdtPassword.setPadding(Utils.dpToPx(30), 0, Utils.dpToPx(45), 0);
                mTxtErrorMessage.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void goToCreateProfileScreen(){
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this,
                R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(this, intent, options.toBundle());
        finish();
    }

    private void gotoResetPasswordScreen(String otp){
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this,
                R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = PhoneSignInForgotPasswordActivity.createIntent(this, mCountry, mPhoneNumber, otp);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    private void gotoMainScreen(){
        EventTracker.trackEvent(EventTrackingName.EVENT_LOGIN_SNS_CLICK,EventTrackingName.SNS_CLICK_PHONE,EventTrackingName.SNS_CLICK_PHONE);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this,
                R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(this, intent, options.toBundle());
        finish();
    }
}
