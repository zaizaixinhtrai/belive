package com.domain.interactors.dailybonus;

import androidx.annotation.NonNull;

import com.domain.interactors.UseCase;
import com.domain.models.TreatItemModel;
import com.domain.repository.DailyBonusRepository;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by thanhbc on 11/13/17.
 */

public class GetClaimedTreatUseCase extends UseCase<TreatItemModel,Void> {

    private final DailyBonusRepository mDailyBonusRepository;
    public GetClaimedTreatUseCase(@NonNull DailyBonusRepository repository, Scheduler uiThread, Scheduler executorThread) {
        super(uiThread, executorThread);
        this.mDailyBonusRepository = repository;
    }
    @Override
    public Observable<TreatItemModel> buildObservable(Void aVoid) {
        return mDailyBonusRepository.getClaimedTreat();
    }
}
