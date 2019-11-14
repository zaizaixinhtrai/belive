package com.domain.interactors.dailybonus;

import androidx.annotation.NonNull;

import com.domain.interactors.UseCase;
import com.domain.repository.DailyBonusRepository;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by thanhbc on 11/14/17.
 */

public class GetDailyBonusShowedUseCase extends UseCase<Boolean,Void> {

    private final DailyBonusRepository mDailyBonusRepository;
    public GetDailyBonusShowedUseCase(@NonNull DailyBonusRepository repository, Scheduler uiThread, Scheduler executorThread) {
        super(uiThread, executorThread);
        this.mDailyBonusRepository = repository;
    }
    @Override
    public Observable<Boolean> buildObservable(Void aVoid) {
        return mDailyBonusRepository.checkBonusDisplayed();
    }
}
