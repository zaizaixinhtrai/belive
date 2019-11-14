package com.appster.features.home;

import com.appster.core.adapter.DisplayableItem;
import com.appster.models.TagListLiveStreamModel;

import java.util.List;

/**
 * Created by thanhbc on 6/2/17.
 */

public class StreamCategory implements DisplayableItem {
    public List<TagListLiveStreamModel> categories;

    public StreamCategory(List<TagListLiveStreamModel> categories) {
        this.categories = categories;
    }
}
