package com.appster.features.points.di

import com.appster.base.FragmentScope
import com.appster.features.points.PointsContract
import com.appster.features.points.PointsPresenter
import com.data.repository.DailyBonusDataRepository
import com.data.repository.datasource.DailyBonusDataSource
import com.data.repository.datasource.cloud.CloudDailyBonusDataSource
import com.domain.interactors.dailybonus.TreatCollectUseCase
import com.domain.interactors.pointsystem.GetDailyBonusCountDownUseCase
import com.domain.interactors.pointsystem.GetMysteryBoxesUseCase
import com.domain.interactors.pointsystem.GetUserPrizeCountUseCase
import com.domain.interactors.pointsystem.OpenMysteryBoxUseCase
import com.domain.repository.DailyBonusRepository
import dagger.Module
import dagger.Provides

@Module
class PointsFragmentModule {

    @Provides
    @FragmentScope
    fun provideDailyBonusDataSource(repo: CloudDailyBonusDataSource): DailyBonusDataSource = repo

    @Provides
    @FragmentScope
    fun provideDailyBonusRepo(repo: DailyBonusDataRepository): DailyBonusRepository = repo

    @Provides
    @FragmentScope
    fun providePresenter(mysteryBoxesUseCase: GetMysteryBoxesUseCase,
                         treatUseCase: TreatCollectUseCase,
                         userPrizeCountUseCase: GetUserPrizeCountUseCase,
                         dailyBonusCountDownUseCase: GetDailyBonusCountDownUseCase,
                         openMysteryBoxUseCase: OpenMysteryBoxUseCase): PointsContract.UserActions =
            PointsPresenter(mysteryBoxesUseCase, treatUseCase, userPrizeCountUseCase, dailyBonusCountDownUseCase, openMysteryBoxUseCase)

}