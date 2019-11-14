package com.domain.interactors.dailybonus;

import androidx.annotation.NonNull;

import com.appster.core.adapter.DisplayableItem;
import com.domain.interactors.UseCase;
import com.domain.repository.DailyBonusRepository;

import java.util.List;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by thanhbc on 11/13/17.
 */

public class DailyBonusTreatListUseCase extends UseCase<List<DisplayableItem>,Void> {
    private final DailyBonusRepository mBonusTreatListInfoRepository;
    public DailyBonusTreatListUseCase(@NonNull DailyBonusRepository repository, Scheduler uiThread, Scheduler executorThread) {
        super(uiThread, executorThread);
        this.mBonusTreatListInfoRepository = repository;
    }

    @Override
    public Observable<List<DisplayableItem>> buildObservable(Void aVoid) {
        return mBonusTreatListInfoRepository.getDailyTreatList();
    }
}
