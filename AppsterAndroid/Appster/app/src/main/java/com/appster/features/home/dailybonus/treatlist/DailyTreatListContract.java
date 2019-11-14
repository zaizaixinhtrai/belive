package com.appster.features.home.dailybonus.treatlist;

import com.appster.core.adapter.DisplayableItem;
import com.appster.features.mvpbase.BaseContract;

import java.util.List;

/**
 * Created by thanhbc on 11/10/17.
 */

public interface DailyTreatListContract {
    interface View extends BaseContract.View {
        void onTreatsListReceived(List<DisplayableItem> displayableItems);
    }

    interface UserActions extends BaseContract.Presenter<DailyTreatListContract.View> {
        void getThisWeekTreatList();
    }
}
