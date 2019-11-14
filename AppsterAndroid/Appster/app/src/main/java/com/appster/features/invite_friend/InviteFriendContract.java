package com.appster.features.invite_friend;

import com.appster.features.mvpbase.BaseContract;
import com.appster.models.AppConfigModel;
import com.domain.models.EditReferralModel;

/**
 * Created by Ngoc on 5/16/2017.
 */

public interface InviteFriendContract {

    interface InviteFriendView extends BaseContract.View {
        void onUpdateLayoutCompleted();
        void errorHasRefId(String error);
        void onGetAppConfigSuccessfully(AppConfigModel appConfig);
        void onGetAppConfigFailed();

    }

    interface UserActions extends BaseContract.Presenter<InviteFriendContract.InviteFriendView> {
        void updateRefId(String refId);
        void getUserInfo();
        void getAppConfigFromServer();
        void trackingReferralCode(EditReferralModel model);
    }
}
