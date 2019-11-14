package com.appster.features.login.phoneLogin.signIn;

import com.appster.features.mvpbase.BaseContract;
import com.appster.webservice.request_models.PhoneLoginForgotPasswordRequestModel;
import com.appster.webservice.request_models.PhoneLoginRequestModel;
import com.appster.webservice.response.PhoneLoginForgotPasswordResponse;

/**
 * Created by linh on 26/10/2017.
 */

public interface PhoneSignInContract {
    interface View extends BaseContract.View{
        void onLoginSuccessfully();
        void onUserNotFound(String message);
        void onAccountSuspended();
        void onAdminBlocked(String message);
        void onPasswordInvalid();
        void onLoginFailed();
        void onRequestVerificationCodeSuccessfully(PhoneLoginForgotPasswordResponse response);
        void onRequestVerificationCodeFailed(String message);
        void onRequestVerificationReachedLimited(String message);
    }

    interface UserActions extends BaseContract.Presenter<View>{
        void loginAppsterServerWithPhoneNumber(PhoneLoginRequestModel request);
        void requestVerificationCode(PhoneLoginForgotPasswordRequestModel request);
    }
}
