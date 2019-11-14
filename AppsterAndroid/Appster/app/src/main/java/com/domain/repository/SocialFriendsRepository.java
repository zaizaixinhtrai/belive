package com.domain.repository;

import com.appster.domain.FriendSuggestionModel;
import com.data.entity.requests.ContactRequestEntity;
import com.domain.models.SocialFriendsNumModel;

import java.util.List;

import rx.Observable;


/**
 * Created by thanhbc on 12/26/17.
 */

public interface SocialFriendsRepository {
    Observable<List<FriendSuggestionModel>> getMutualFriends(List<ContactRequestEntity> contacts);

    Observable<SocialFriendsNumModel> getSocialsFriendNum();
}
