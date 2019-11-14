package com.appster.features.points.di

import com.appster.base.FragmentScope
import com.appster.features.points.PointsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *  Created by DatTN on 10/23/2018
 */
@Module
interface PointsBuilderModule {

    @ContributesAndroidInjector(modules = [PointsFragmentModule::class])
    @FragmentScope
    fun bindPointsFragment(): PointsFragment

}