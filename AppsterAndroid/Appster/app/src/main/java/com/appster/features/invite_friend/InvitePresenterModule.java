package com.appster.features.invite_friend;

import com.data.di.InviteFriendRepositoryModule;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ngoc on 4/13/2018.
 */

@Module( includes = {InviteFriendRepositoryModule.class})
public class InvitePresenterModule {

    @Provides
    public InviteFriendContract.InviteFriendView provideView(InviteFriendActivity inviteFriendActivity) {
        return inviteFriendActivity;
    }

    @Provides
    public InviteFriendContract.UserActions providePresenter(InviteFriendPresenter inviteFriendPresenter) {
        return inviteFriendPresenter;
    }
}
