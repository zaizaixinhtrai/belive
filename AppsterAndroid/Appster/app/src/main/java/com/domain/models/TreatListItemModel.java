package com.domain.models;

import com.appster.core.adapter.DisplayableItem;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatListItemModel implements DisplayableItem {
    public String title;

    public TreatListItemModel(String title) {
        this.title = title;
    }
}
