package com.appster.features.login;

import com.appster.features.mvpbase.BaseContract;
import com.appster.webservice.request_models.BaseLoginRequestModel;
import com.appster.webservice.request_models.TwitterLoginRequestModel;
import com.appster.webservice.response.BaseResponse;
import com.appster.webservice.response.LoginResponseModel;
import com.appster.webservice.response.MaintenanceModel;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

/**
 * Created by User on 12/7/2016.
 */

public interface LoginContract {

    interface LoginView extends BaseContract.View {
        void onAdminBlocked(String message);

        void onAccountSuspended();

        void onLoginSuccessfully(BaseLoginRequestModel request, BaseResponse<LoginResponseModel> loginResponse);

        void onForceMaintenance(MaintenanceModel model);

        void onNavigateMainScreen();

    }

    interface LoginActions extends BaseContract.Presenter<LoginContract.LoginView> {

        void getTwitterInformation(TwitterAuthClient mTwitterAuthClient);

        void loginAppsteriWthTwitterInfo(TwitterLoginRequestModel loginRequestModel);

        void loginWithFacebook();

        void onGoogleLoginResponse(GoogleSignInResult result);

        void loginInstagram();

        void checkMaintenance();
    }
}
