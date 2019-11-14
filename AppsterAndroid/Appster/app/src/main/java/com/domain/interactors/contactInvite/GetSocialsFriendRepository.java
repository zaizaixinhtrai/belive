package com.domain.interactors.contactInvite;

import com.domain.interactors.UseCase;
import com.domain.models.SocialFriendsNumModel;
import com.domain.repository.SocialFriendsRepository;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by thanhbc on 12/30/17.
 */

public class GetSocialsFriendRepository extends UseCase<SocialFriendsNumModel,Void> {
    private final SocialFriendsRepository mSocialFriendsRepository;
    public GetSocialsFriendRepository(Scheduler uiThread, Scheduler executorThread,SocialFriendsRepository socialFriendsRepository) {
        super(uiThread, executorThread);
        this.mSocialFriendsRepository = socialFriendsRepository;
    }

    @Override
    public Observable<SocialFriendsNumModel> buildObservable(Void aVoid) {
        return mSocialFriendsRepository.getSocialsFriendNum();
    }
}
