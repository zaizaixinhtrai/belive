package com.appster.features.searchScreen.di

import com.appster.base.FragmentScope
import com.appster.features.searchScreen.FragmentSearch
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by thanhbc on 6/13/18.
 */

@Module
abstract class FragmentSearchModule {
    @ContributesAndroidInjector(modules = [(FragmentSearchPresenterModule::class)])
    @FragmentScope
    internal abstract fun provideFragmentSearchFactory(): FragmentSearch
}