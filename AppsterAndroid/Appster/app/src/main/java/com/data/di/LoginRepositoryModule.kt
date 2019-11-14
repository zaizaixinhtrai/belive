package com.data.di

import com.data.repository.Remote
import com.data.repository.UserLoginDataRepository
import com.data.repository.datasource.LoginDataSource
import com.data.repository.datasource.cloud.CloudLoginDataSource
import com.domain.repository.UserLoginRepository
import dagger.Module
import dagger.Provides

/**
 * Created by thanhbc on 5/10/18.
 */
@Module
class LoginRepositoryModule{
    @Remote
    @Provides
    fun provideRemoteDataSource(loginDataSource: CloudLoginDataSource) : LoginDataSource = loginDataSource

    @Provides
    fun provideLoginRepository(loginDataRepository: UserLoginDataRepository) : UserLoginRepository = loginDataRepository
}