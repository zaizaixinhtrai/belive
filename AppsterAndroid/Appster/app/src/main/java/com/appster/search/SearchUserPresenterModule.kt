package com.appster.search

import com.data.di.SearchUserRepositoryModule
import dagger.Module
import dagger.Provides

/**
 * Created by Ngoc on 5/28/2018.
 */
@Module(includes = [SearchUserRepositoryModule::class])
class SearchUserPresenterModule {
    @Provides
    fun provideView(searchActivity: SearchActivity): SearchContract.SearchView = searchActivity

    @Provides
    fun providePresenter(searchUserPresenter: SearchPresenter): SearchContract.UserActions = searchUserPresenter
}
