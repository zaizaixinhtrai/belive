package com.appster.features.login.phoneLogin.phoneSignInSignUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.appster.R;
import com.appster.customview.CustomFontEditText;
import com.appster.customview.CustomFontTextView;
import com.appster.domain.PhoneVerification;
import com.appster.features.login.phoneLogin.BasePhoneSignInActivity;
import com.appster.features.login.phoneLogin.countrypicker.Country;
import com.appster.features.login.phoneLogin.countrypicker.CountryPickerDialog;
import com.appster.features.login.phoneLogin.countrypicker.Utils;
import com.appster.features.login.phoneLogin.phoneSignUp.PhoneSignUpActivity;
import com.appster.features.login.phoneLogin.signIn.PhoneSignInActivity;
import com.apster.common.DialogbeLiveConfirmation;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by linh on 23/10/2017.
 */

public class PhoneSignInSignUpActivity extends BasePhoneSignInActivity implements PhoneSignInSignUpContract.View {

    private final static int MIN_LENGTH_PHONE_NUMBER = 8;
    private final static int MAX_LENGTH_PHONE_NUMBER = 15;

    @Bind(R.id.txt_country_code)
    CustomFontTextView mTxtCountryCode;
    @Bind(R.id.edt_phone_number)
    CustomFontEditText mEdtPhoneNumber;
    @Bind(R.id.btn_next)
    Button mBtnNext;

    CountryPickerDialog mCountryPickerDialog;
    Country mSelectedCountry;
    String mPhoneNumber;

    PhoneSignInSignUpContract.UserActions mPresenter;

    public static Intent createIntent(Context context){
        return new Intent(context, PhoneSignInSignUpActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setCountryCode(mSelectedCountry);
        setupPhoneNumberWatcher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected int setContentLayout() {
        return R.layout.activity_phone_signin_signup;
    }

    @Override
    protected String setTitle() {
        return getString(R.string.phone_login_sigin_signup);
    }

    //======== event handlers ======================================================================
    @OnClick(R.id.txt_country_code)
    void onCountryCodeClicked(){
        if (mCountryPickerDialog == null){
            mCountryPickerDialog = new CountryPickerDialog(this, (country, flagResId) -> {
                mSelectedCountry = country;
                setCountryCode(mSelectedCountry);
            });
        }

        mCountryPickerDialog.show();
    }

    @OnClick(R.id.btn_next)
    void onNextButtonClicked(){
        mPresenter.verifyPhoneNumber(mSelectedCountry.getDialingCodeWithoutPlusSign(), mPhoneNumber);
    }

    //========= mvp callbacks ======================================================================
    @Override
    public void onVerifyPhoneNumberSuccessfully(PhoneVerification verification) {
//        if (BuildConfig.DEBUG){// TODO: 26/10/2017 this hard code should be removed later
//            gotoPhoneSignInScreen();
//            return;
//        }
        if (verification.isExists){
            gotoPhoneSignInScreen();
        }else{
            showConfirmDialog(verification.otpToken);
        }
    }

    @Override
    public void onVerifyPhoneNumberFailed(String message) {

    }

    //======= inner methods ========================================================================
    private void init(){
        mPhoneNumber = "";
        mSelectedCountry = Utils.getDefaultCountry(this);
        mPresenter = new PhoneSignInSignUpPresenter();
        mPresenter.attachView(this);
    }

    private void setCountryCode(Country selectedCountry){
        String str = selectedCountry.getIsoCode() + " " + selectedCountry.getDialingCode();
        mTxtCountryCode.setText(str);
    }

    private void setupPhoneNumberWatcher(){
        mEdtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean invalid = s.length() >= MIN_LENGTH_PHONE_NUMBER && s.length() <= MAX_LENGTH_PHONE_NUMBER;
                mBtnNext.setEnabled(invalid);
                String str = s.toString();
                mPhoneNumber = (str.startsWith("0")) ? str.substring(1) : str;
            }
        });
    }

    private void showConfirmDialog(final String otpToken){
        String phoneNumber = mSelectedCountry.getDialingCode() + mPhoneNumber;
        String title = String.format(getString(R.string.phone_verify_title), phoneNumber);
        new DialogbeLiveConfirmation.Builder().title(title)
                .message(getString(R.string.phone_signin_verify_message))
                .confirmText(getString(R.string.verify))
                .singleAction(false)
                .onConfirmClicked(() -> gotoPhoneSignUpScreen(otpToken))
                .build().show(this);
    }

    private void gotoPhoneSignUpScreen(String otpToken){
        ActivityCompat.startActivity(this,
                PhoneSignUpActivity.createIntent(this, mSelectedCountry, mPhoneNumber,
                        otpToken), translateAnimation());
    }

    private void gotoPhoneSignInScreen(){
        ActivityCompat.startActivity(this,
                PhoneSignInActivity.createIntent(this, mSelectedCountry, mPhoneNumber),
                translateAnimation());
    }
}
