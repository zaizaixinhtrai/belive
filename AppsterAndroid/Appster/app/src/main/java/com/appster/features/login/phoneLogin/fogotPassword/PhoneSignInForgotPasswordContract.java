package com.appster.features.login.phoneLogin.fogotPassword;

import com.appster.features.mvpbase.BaseContract;
import com.appster.webservice.request_models.PhoneLoginForgotPasswordRequestModel;
import com.appster.webservice.response.PhoneLoginForgotPasswordResponse;

/**
 * Created by linh on 26/10/2017.
 */

public interface PhoneSignInForgotPasswordContract {
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
