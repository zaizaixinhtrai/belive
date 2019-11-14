package com.appster.features.login.phoneLogin.fogotPassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.appster.R;
import com.appster.customview.CustomFontButton;
import com.appster.customview.CustomFontEditText;
import com.appster.customview.CustomFontTextView;
import com.appster.features.login.phoneLogin.BasePhoneSignInActivity;
import com.appster.features.login.phoneLogin.countrypicker.Country;
import com.appster.features.login.phoneLogin.newPassword.PhoneSignInNewPasswordActivity;
import com.appster.features.login.phoneLogin.newPassword.PhoneSignInNewPasswordActivity.NewPassWordMode;
import com.apster.common.Constants;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by linh on 24/10/2017.
 */

public class PhoneSignInForgotPasswordActivity extends BasePhoneSignInActivity implements PhoneSignInForgotPasswordContract.View {
    private static final String ARG_COUNTRY = "ARG_COUNTRY";
    private static final String ARG_PHONE_NUMBER = "ARG_PHONE_NUMBER";
    private static final String ARG_OTP = "ARG_OTP";
    private static final int COUNT_DOWN_TIME = 60;

    @Bind(R.id.txt_country_code)
    CustomFontTextView mTxtCountryCode;
    @Bind(R.id.edt_phone_number)
    CustomFontTextView mTxtPhoneNumber;
    @Bind(R.id.edt_verification_code)
    CustomFontEditText mEdtVerificationCode;
    @Bind(R.id.btn_resend)
    CustomFontButton mBtnResend;

    private Country mCountry;
    private String mPhoneNumber;
    private String mOtp;
    private boolean mShouldStopCountDown;

    PhoneSignInForgotPasswordContract.UserActions mPresenter;

    public static Intent createIntent(Context context, Country country, String phoneNumber, String otp){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_COUNTRY, country);
        bundle.putString(ARG_PHONE_NUMBER, phoneNumber);
        bundle.putString(ARG_OTP, otp);
        Intent intent = new Intent(context, PhoneSignInForgotPasswordActivity.class);
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
            mOtp = bundle.getString(ARG_OTP);
            bindView();
        }
        mPresenter = new PhoneSignInFogotPasswordPresenter();
        mPresenter.attachView(this);
//        mPresenter.requestVerificationCode(mRequest);
        setupWatcher();
        setupCountDown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected int setContentLayout() {
        return R.layout.activity_phone_signin_reset_password;
    }

    @Override
    protected String setTitle() {
        return getString(R.string.reset_password);
    }

    //=========== event handlers ===================================================================
    @OnClick(R.id.btn_resend)
    void onSendVerificationCodeClicked(){
        mShouldStopCountDown = false;
        mEdtVerificationCode.setCustomDrawableEnd(0);
        mEdtVerificationCode.setText("");
        mPresenter.requestVerificationCode(mOtp);
        setupCountDown();
    }

    //======== mvp callbacks =======================================================================
    @Override
    public void onRequestVerificationCodeSuccessfully(Boolean data) {
    }

    @Override
    public void onRequestVerificationCodeFailed(String message) {
        loadError(message);
    }

    @Override
    public void onRequestVerificationReachedLimited(String message) {
        mShouldStopCountDown = true;
        mBtnResend.setEnabled(false);
        loadError(message);
    }

    @Override
    public void onVerifyVerificationCodeSuccessfully() {
        mEdtVerificationCode.setCustomDrawableEnd(0);
        gotoResetNewPassword();
    }

    @Override
    public void onVerifyVerificationCodeFailed(String message) {
        mEdtVerificationCode.setCustomDrawableEnd(R.drawable.ic_wrong);
        loadError(message);
    }

    //=========== inner methods ====================================================================
    private void bindView() {
        String str = mCountry.getIsoCode() + " " + mCountry.getDialingCode();
        mTxtCountryCode.setText(str);
        mTxtPhoneNumber.setText(mPhoneNumber);
    }

    private void setupWatcher(){
        mEdtVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= Constants.VERIFICATION_CODE_LENGTH) {
                    mPresenter.verifyVerificationCode(mOtp, s.toString().trim());
                }
            }
        });
    }

    private void setupCountDown() {
        mBtnResend.setEnabled(false);
        mCompositeSubscription.add(Observable.interval(1, TimeUnit.SECONDS)
                .takeUntil(aLong -> aLong >= COUNT_DOWN_TIME || mShouldStopCountDown)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mShouldStopCountDown){
                        mBtnResend.setEnabled(false);
                        mBtnResend.setText(getString(R.string.resend));
                        return;
                    }
                    int timeLeft = (int) (COUNT_DOWN_TIME - aLong);
                    if (timeLeft <= 0) {
                        mBtnResend.setEnabled(true);
                        mBtnResend.setText(getString(R.string.resend));
                    } else {
                        String str = String.format(getString(R.string.resend_in_count_down), timeLeft);
                        mBtnResend.setText(str);
                    }
                }));
    }


    private void gotoResetNewPassword(){
        Intent intent = PhoneSignInNewPasswordActivity.createIntent(this, mCountry, mPhoneNumber, mOtp, NewPassWordMode.MODE_RESET);
        ActivityCompat.startActivity(this, intent, translateAnimation());
    }
}
