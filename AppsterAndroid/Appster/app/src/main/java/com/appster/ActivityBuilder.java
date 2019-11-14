package com.appster;

import com.appster.activity.MediaPlayerActivity;
import com.appster.base.ActivityScope;
import com.appster.features.home.di.BeLiveHomeFragmentModule;
import com.appster.features.income.history.TransactionHistoryActivity;
import com.appster.features.income.history.TransactionPresenterModule;
import com.appster.features.income.masterBrainWallet.MasterBrainCashOutPresenterModule;
import com.appster.features.income.masterBrainWallet.MasterBrainCashoutActivity;
import com.appster.features.invite_friend.InviteFriendActivity;
import com.appster.features.invite_friend.InvitePresenterModule;
import com.appster.features.login.LoginActivity;
import com.appster.features.login.LoginPresenterModule;
import com.appster.features.messages.MessageListActivity;
import com.appster.features.messages.chat.ChatActivity;
import com.appster.features.messages.chat.di.ChatModule;
import com.appster.features.messages.di.MessageListModule;
import com.appster.features.notification.NotificationActivity;
import com.appster.features.notification.di.NotifyFragmentModule;
import com.appster.features.points.di.PointsBuilderModule;
import com.appster.features.points.prizelist.PrizeListActivity;
import com.appster.features.points.prizelist.di.PrizeListModule;
import com.appster.features.prizeBag.PrizeBagActivity;
import com.appster.features.prizeBag.di.PrizeBagPresenterModule;
import com.appster.features.prizeBag.di.RedemptionPresenterModule;
import com.appster.features.prizeBag.redemption.RedemptionActivity;
import com.appster.features.searchScreen.di.FragmentSearchModule;
import com.appster.features.stream.viewer.MediaPlayerFragmentModule;
import com.appster.main.MainActivity;
import com.appster.main.MainPresenterModule;
import com.appster.search.SearchActivity;
import com.appster.search.SearchUserPresenterModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by thanhbc on 4/2/18.
 */
@Module
public abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = {TransactionPresenterModule.class})
    @ActivityScope
    abstract TransactionHistoryActivity bindTransactionHistoryActivity();

    @ContributesAndroidInjector(modules = {InvitePresenterModule.class})
    @ActivityScope
    abstract InviteFriendActivity bindInviteFriendActivity();

    @ContributesAndroidInjector(modules = {MediaPlayerFragmentModule.class})
    @ActivityScope
    abstract MediaPlayerActivity bindMediaPlayerActivity();

    @ContributesAndroidInjector(modules = {LoginPresenterModule.class})
    @ActivityScope
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector(modules = {BeLiveHomeFragmentModule.class, MainPresenterModule.class, FragmentSearchModule.class, PointsBuilderModule.class})
    @ActivityScope
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = {SearchUserPresenterModule.class})
    @ActivityScope
    abstract SearchActivity bindSearchActivity();

    @ContributesAndroidInjector(modules = {MasterBrainCashOutPresenterModule.class})
    @ActivityScope
    abstract MasterBrainCashoutActivity bindMasterBrainCashoutActivity();

    @ContributesAndroidInjector(modules = {MessageListModule.class})
    @ActivityScope
    abstract MessageListActivity bindMessageListActivity();

    @ContributesAndroidInjector(modules = {ChatModule.class})
    @ActivityScope
    abstract ChatActivity bindChatActivity();

    @ContributesAndroidInjector(modules = {PrizeListModule.class})
    @ActivityScope
    abstract PrizeListActivity bindPrizeListActivity();

    @ContributesAndroidInjector(modules = {PrizeBagPresenterModule.class})
    @ActivityScope
    abstract PrizeBagActivity bindPrizeBagActivity();

    @ContributesAndroidInjector(modules = {RedemptionPresenterModule.class})
    @ActivityScope
    abstract RedemptionActivity bindRedemptionActivity();

    @ContributesAndroidInjector(modules = {NotifyFragmentModule.class})
    @ActivityScope
    abstract NotificationActivity bindNotificationActivity();

}
