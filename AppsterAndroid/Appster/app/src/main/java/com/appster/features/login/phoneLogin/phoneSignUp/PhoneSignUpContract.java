package com.appster.features.login.phoneLogin.phoneSignUp;

import com.appster.features.mvpbase.BaseContract;

/**
 * Created by linh on 25/10/2017.
 */

public class PhoneSignUpContract {
    interface View extends BaseContract.View{
        void onRequestVerificationCodeSuccessfully(Boolean data);
        void onRequestVerificationCodeFailed(String message);
        void onRequestVerificationReachedLimited(String message);
        void onVerifyVerificationCodeSuccessfully();
        void onVerifyVerificationCodeFailed(String message);
    }

    interface UserActions extends BaseContract.Presenter<View>{
        void requestVerificationCode(String otp);
        void verifyVerificationCode(String otp, String phoneVerificationCode);
    }
}
