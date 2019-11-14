package com.domain.interactors.contactInvite;

import com.appster.domain.FriendSuggestionModel;
import com.appster.models.ContactModel;
import com.data.entity.mapper.SocialFriendsDataMapper;
import com.domain.interactors.UseCase;
import com.domain.repository.SocialFriendsRepository;

import java.util.List;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by thanhbc on 12/26/17.
 */

public class GetMutualFriendsRepository extends UseCase<List<FriendSuggestionModel>,GetMutualFriendsRepository.Params> {
        private final SocialFriendsRepository mSocialFriendsRepository;
    private final SocialFriendsDataMapper mFriendsMapper;
    public GetMutualFriendsRepository(Scheduler uiThread, Scheduler executorThread,SocialFriendsRepository socialFriendsRepository) {
        super(uiThread, executorThread);
        this.mSocialFriendsRepository = socialFriendsRepository;
        mFriendsMapper = new SocialFriendsDataMapper();
    }

    @Override
    public Observable<List<FriendSuggestionModel>> buildObservable(Params params) {
        return mSocialFriendsRepository.getMutualFriends(mFriendsMapper.transformToRequestModel(params.contacts));
    }

    public static final class Params{
       final List<ContactModel> contacts;
        @SuppressWarnings("unchecked")
        private Params(List<?> contacts) {
            this.contacts = (List) contacts;
        }

        public static Params from(List<?> contacts){
            return new Params(contacts);
        }
    }
}
