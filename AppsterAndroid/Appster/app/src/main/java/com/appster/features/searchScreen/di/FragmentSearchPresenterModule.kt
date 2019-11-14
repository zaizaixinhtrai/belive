package com.appster.features.searchScreen.di

import com.appster.features.searchScreen.FragmentSearch
import com.appster.features.searchScreen.SearchScreenContract
import com.appster.features.searchScreen.SearchScreenPresenter
import com.data.di.ExploreRepositoryModule
import dagger.Module
import dagger.Provides


@Module(includes = [ExploreRepositoryModule::class])
class FragmentSearchPresenterModule {

    @Provides
    fun provideView(searchView: FragmentSearch): SearchScreenContract.SearchView = searchView

    @Provides
    fun providePresenter(presenter: SearchScreenPresenter): SearchScreenContract.UserActions = presenter
}
