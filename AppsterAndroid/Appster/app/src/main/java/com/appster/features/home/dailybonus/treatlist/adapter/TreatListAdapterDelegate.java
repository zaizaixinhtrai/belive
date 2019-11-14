package com.appster.features.home.dailybonus.treatlist.adapter;

import androidx.annotation.Nullable;

import com.appster.core.adapter.ListDisplayableDelegationAdapter;
import com.apster.common.BaseDiffCallback;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatListAdapterDelegate extends ListDisplayableDelegationAdapter {
    public TreatListAdapterDelegate(@Nullable BaseDiffCallback diffCallback) {
        super(diffCallback);
        this.delegatesManager.addDelegate(new TreatTitleItemDelegate());
        this.delegatesManager.addDelegate(new TreatBigItemDelegate());
        this.delegatesManager.addDelegate(new TreatMiniItemDelegate());
        this.delegatesManager.addDelegate(new TreatUltimateItemDelegate());
    }
}
