package com.data.repository.datasource;

import com.appster.webservice.response.BaseResponse;
import com.data.entity.MutualFriendEntity;
import com.data.entity.SocialFriendsNumEntity;
import com.data.entity.requests.ContactRequestEntity;

import java.util.List;

import rx.Observable;

/**
 * Created by thanhbc on 12/26/17.
 */

public interface SocialFriendsDataSource {
    Observable<BaseResponse<List<MutualFriendEntity>>> getMutualFriends(List<ContactRequestEntity> contacts);

    Observable<BaseResponse<SocialFriendsNumEntity>> getSocialsFriendNum();
}
