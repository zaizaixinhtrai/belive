package com.appster.features.home;

import com.appster.features.mvpbase.BaseContract;
import com.appster.models.HomeCurrentEventModel;
import com.appster.models.HomeItemModel;
import com.appster.models.TagListLiveStreamModel;

import java.util.List;

/**
 * Created by thanhbc on 6/28/17.
 */

public interface HomeContract {
    interface View extends BaseContract.View {
        void onCategoriesReceived(List<TagListLiveStreamModel> categories);
        void onEventsReceived(List<HomeCurrentEventModel> events);
        void onStreamsReceived(List<HomeItemModel> streams,int nextId, boolean isEnd);
        void refreshCompleted();
        boolean isRunning();
    }

    interface UserActions extends BaseContract.Presenter<HomeContract.View> {
        void getCategoriesByTag(int type);
        void getEventsByTag(int type);
        void getStreamsByTag(int type, int nextId);

    }

}
