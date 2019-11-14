package com.appster.features.login.phoneLogin.newPassword;

import com.appster.features.mvpbase.BaseContract;
import com.appster.webservice.request_models.PhoneLoginResetPasswordRequest;

/**
 * Created by linh on 25/10/2017.
 */

public interface PhoneSignInNewPasswordContract {
    interface View extends BaseContract.View{
        void onResetPasswordSuccessfully();
        void onResetPasswordFailed(String message);
    }
    interface UserActions extends BaseContract.Presenter<View>{
        void resetPassword(PhoneLoginResetPasswordRequest request);
    }
}
