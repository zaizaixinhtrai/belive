package com.appster.features.home.dailybonus.treatmachine.adapters;

import androidx.annotation.Nullable;

import com.appster.core.adapter.ListDisplayableDelegationAdapter;
import com.apster.common.BaseDiffCallback;

/**
 * Created by thanhbc on 11/8/17.
 */

public class TreatsAdapterDelegate extends ListDisplayableDelegationAdapter {

    public TreatsAdapterDelegate(@Nullable BaseDiffCallback diffCallback) {
        super(diffCallback);
        this.delegatesManager.addDelegate(new TreatItemDelegate());
    }
}
