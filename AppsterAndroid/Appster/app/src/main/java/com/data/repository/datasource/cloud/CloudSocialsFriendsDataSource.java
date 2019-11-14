package com.data.repository.datasource.cloud;

import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.response.BaseResponse;
import com.data.entity.MutualFriendEntity;
import com.data.entity.SocialFriendsNumEntity;
import com.data.entity.requests.ContactRequestEntity;
import com.data.entity.requests.ContactRequestWrapperEntity;
import com.data.repository.datasource.SocialFriendsDataSource;

import java.util.List;

import rx.Observable;

/**
 * Created by thanhbc on 12/26/17.
 */

public class CloudSocialsFriendsDataSource implements SocialFriendsDataSource {

    private final AppsterWebserviceAPI mService;
    private final String mAuthen;

    public CloudSocialsFriendsDataSource(AppsterWebserviceAPI service, String authen) {
        mService = service;
        mAuthen = authen;
    }


    @Override
    public Observable<BaseResponse<List<MutualFriendEntity>>> getMutualFriends(List<ContactRequestEntity> contacts) {
        return mService.getMutualFriends(mAuthen, new ContactRequestWrapperEntity(contacts));
    }

    @Override
    public Observable<BaseResponse<SocialFriendsNumEntity>> getSocialsFriendNum() {
        return mService.getSocialFriends(mAuthen);
    }
}
