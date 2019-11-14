package com.appster.features.login.phoneLogin.phoneSignInSignUp;

import com.appster.domain.PhoneVerification;
import com.appster.features.mvpbase.BaseContract;

/**
 * Created by linh on 25/10/2017.
 */

public class PhoneSignInSignUpContract {
    interface View extends BaseContract.View{
        void onVerifyPhoneNumberSuccessfully(PhoneVerification verification);
        void onVerifyPhoneNumberFailed(String message);
    }

    interface UserActions extends BaseContract.Presenter<View>{
        void verifyPhoneNumber(String countryCode, String phoneNumber);
    }
}
