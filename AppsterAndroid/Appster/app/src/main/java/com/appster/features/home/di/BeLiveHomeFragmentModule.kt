package com.appster.features.home.di

import com.appster.base.FragmentScope
import com.appster.features.home.BeLiveHomeScreenFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by thanhbc on 5/18/18.
 */
@Module
abstract class BeLiveHomeFragmentModule{
    @ContributesAndroidInjector(modules = [(BeLiveHomePresenterModule::class)])
    @FragmentScope
    internal abstract fun provideBeLiveHomeFragmentFactory(): BeLiveHomeScreenFragment
}