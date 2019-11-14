package com.appster.features.social_invite_friend;

import com.appster.features.mvpbase.BasePresenter;
import com.appster.utility.AppsterUtility;
import com.appster.webservice.AppsterWebserviceAPI;
import com.data.repository.SocialFriendsDataRepository;
import com.data.repository.datasource.SocialFriendsDataSource;
import com.data.repository.datasource.cloud.CloudSocialsFriendsDataSource;
import com.domain.interactors.contactInvite.GetSocialsFriendRepository;
import com.domain.repository.SocialFriendsRepository;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SocialInviteFriendPresenter extends BasePresenter<SocialInviteFriendContract.View> implements SocialInviteFriendContract.UserActions {
    final GetSocialsFriendRepository mSocialsFriendRepository;
    public SocialInviteFriendPresenter(AppsterWebserviceAPI service) {
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        SocialFriendsDataSource socialFriendsDataSource = new CloudSocialsFriendsDataSource(service, AppsterUtility.getAuth());
        SocialFriendsRepository repository = new SocialFriendsDataRepository(socialFriendsDataSource);
        mSocialsFriendRepository = new GetSocialsFriendRepository(uiThread,ioThread,repository);
    }

    @Override
    public void getSocialFriends() {
        addSubscription(mSocialsFriendRepository.execute(null)
                .filter(socialFriendsNumModel -> getView()!=null && socialFriendsNumModel!=null)
                .subscribe(socialFriendsNumModel -> {
                    getView().onContactMutualFriendsReceived(socialFriendsNumModel.contactFriends);
                    getView().onFacebookMutualFriendsReceived(socialFriendsNumModel.facebookFriends);
                    getView().onInstagramMutualFriendsReceived(socialFriendsNumModel.instagramFriends);
                    getView().onTwitterMutualFriendsReceived(socialFriendsNumModel.twitterFriends);
                },this::handleRetrofitError));
    }
}
