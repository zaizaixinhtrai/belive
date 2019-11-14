package com.appster.features.edit_profile;

import com.appster.models.SettingResponse;
import com.appster.webservice.request_models.EditProfileRequestModel;
import com.appster.features.mvpbase.BaseContract;

/**
 * Created by linh on 06/12/2016.
 */

public interface EditProfileContract {
    interface EditProfileView extends BaseContract.View{
        void onUpdateCompleted(SettingResponse userInfo);
    }

    interface UserActions extends BaseContract.Presenter<EditProfileView>{
        void updateProfile(EditProfileRequestModel request);
    }
}
