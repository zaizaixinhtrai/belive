package com.appster.features.login

import com.data.di.LoginRepositoryModule
import dagger.Module
import dagger.Provides

/**
 * Created by thanhbc on 5/10/18.
 */
@Module (includes = [LoginRepositoryModule::class])
class LoginPresenterModule{
    @Provides
    fun provideView(loginActivity: LoginActivity) : LoginContract.LoginView = loginActivity

    @Provides
    fun providePresenter(loginPresenter: LoginPresenter) : LoginContract.LoginActions = loginPresenter

}