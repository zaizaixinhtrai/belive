package com.appster.features.regist;

import com.appster.models.UserModel;
import com.appster.webservice.request_models.RegisterWithFacebookRequestModel;
import com.appster.webservice.request_models.RegisterWithGoogleRequestModel;
import com.appster.webservice.request_models.RegisterWithInstagramRequestModel;
import com.appster.webservice.request_models.RegisterWithPhoneNumberRequestModel;
import com.appster.webservice.request_models.RegisterWithTwitterRequestModel;
import com.appster.features.mvpbase.BaseContract;
import com.appster.webservice.request_models.RegisterWithWeChatRequestModel;
import com.appster.webservice.request_models.RegisterWithWeiboRequestModel;

/**
 * Created by ThanhBan on 11/14/2016.
 */

public interface RegisterContract {
    interface RegisterView extends BaseContract.View {
        void userIdAvailable();
        void userIdInAvailable();
        void onGetUserIdSuggestionSuccessfully(String suggestedUserId);

        void enableBeginButton(boolean enable);
        void onUserRegisterCompleted(UserModel userInforModel,
                                     String userToken);
        void onAdminBlocked(String message);
    }

    interface UserActions extends BaseContract.Presenter<RegisterView> {
        void checkUserIdAvailable(String userId);
        void getUserIdSuggestion(String expectedUserId);
        void getUserIdSuggestion();

        void register(RegisterWithGoogleRequestModel requestModel);

        void register(RegisterWithFacebookRequestModel requestModel);

        void register(RegisterWithInstagramRequestModel requestModel);

        void register(RegisterWithTwitterRequestModel requestModel);

        void register(RegisterWithWeChatRequestModel requestModel);

        void register(RegisterWithWeiboRequestModel requestModel);

        void register(RegisterWithPhoneNumberRequestModel requestModel);
    }
}
