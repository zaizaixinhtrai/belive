package com.data.repository;

import com.appster.domain.FriendSuggestionModel;
import com.apster.common.Constants;
import com.data.entity.mapper.SocialFriendsDataMapper;
import com.data.entity.requests.ContactRequestEntity;
import com.data.exceptions.BeLiveServerException;
import com.data.repository.datasource.SocialFriendsDataSource;
import com.domain.models.SocialFriendsNumModel;
import com.domain.repository.SocialFriendsRepository;

import java.util.List;

import rx.Observable;

/**
 * Created by thanhbc on 12/26/17.
 */

public class SocialFriendsDataRepository implements SocialFriendsRepository {
    final SocialFriendsDataSource mSocialFriendsDataSource;
    final SocialFriendsDataMapper mFriendsMapper;

    public SocialFriendsDataRepository(SocialFriendsDataSource socialFriendsDataSource) {
        mSocialFriendsDataSource = socialFriendsDataSource;
        mFriendsMapper = new SocialFriendsDataMapper();
    }

    @Override
    public Observable<List<FriendSuggestionModel>> getMutualFriends(List<ContactRequestEntity> contacts) {
        return mSocialFriendsDataSource.getMutualFriends(contacts)
                .flatMap(listBaseResponse -> {
                    switch (listBaseResponse.getCode()) {
                        case Constants.RESPONSE_FROM_WEB_SERVICE_OK:
                            return Observable.just(listBaseResponse.getData());
                        default:
                            return Observable.error(new BeLiveServerException(listBaseResponse.getMessage(),listBaseResponse.getCode()));
                    }
                })
                .flatMap(mutualFriendEntities -> Observable.just(mFriendsMapper.transform(mutualFriendEntities)));
    }

    @Override
    public Observable<SocialFriendsNumModel> getSocialsFriendNum() {
        return mSocialFriendsDataSource.getSocialsFriendNum()
                .flatMap(baseResponse -> {
                    switch (baseResponse.getCode()) {
                        case Constants.RESPONSE_FROM_WEB_SERVICE_OK:
                            return Observable.just(baseResponse.getData());
                        default:
                            return Observable.error(new BeLiveServerException(baseResponse.getMessage(),baseResponse.getCode()));
                    }
                })
                .map(mFriendsMapper::transform);
    }
}
