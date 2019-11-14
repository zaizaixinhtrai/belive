package com.appster.features.banner_detail;

import com.appster.features.mvpbase.BaseContract;

public interface BannerDetailContract {

    interface View extends BaseContract.View {
        void followUserResult();
    }

    interface UserActions extends BaseContract.Presenter<BannerDetailContract.View> {
        void followUser(String userId);
    }
}
