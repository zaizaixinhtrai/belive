package com.data.di;

import com.data.repository.InviteFriendDataRepository;
import com.data.repository.Remote;
import com.data.repository.datasource.InviteFriendDataSource;
import com.data.repository.datasource.cloud.CloudInviteFriendDataSource;
import com.domain.repository.InviteFriendRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ngoc on 4/13/2018.
 */

@Module
public class InviteFriendRepositoryModule {
    @Provides
    @Remote
    public InviteFriendDataSource provideRemoteDataSource(CloudInviteFriendDataSource cloudInviteFriendDataSource) {
        return cloudInviteFriendDataSource;
    }

    @Provides
    InviteFriendRepository provideTransactionRepository(InviteFriendDataRepository transactionDataRepository) {
        return transactionDataRepository;
    }
}
