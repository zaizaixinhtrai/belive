package com.appster.features.income.masterBrainWallet

import com.appster.features.home.BeLiveHomeContract
import com.appster.features.home.BeLiveHomePresenter
import com.appster.features.home.BeLiveHomeScreenFragment
import com.data.di.LiveShowRepositoryModule
import com.data.di.TransactionRepositoryModule
import dagger.Module
import dagger.Provides

/**
 * Created by thanhbc on 5/18/18.
 */
@Module (includes = [TransactionRepositoryModule::class])
class MasterBrainCashOutPresenterModule{
    @Provides
    fun provideView(masterBrainCashoutView: MasterBrainCashoutActivity): MasterBrainCashoutContract.View = masterBrainCashoutView


    @Provides
    fun providePresenter(presenter: MasterBrainCashoutPresenter): MasterBrainCashoutContract.UserActions = presenter

}