package com.appster.features.home;

import com.appster.core.adapter.DisplayableItem;
import com.appster.models.HomeCurrentEventModel;

import java.util.List;

/**
 * Created by thanhbc on 5/30/17.
 */

public class BannerModel implements DisplayableItem {

    public List<HomeCurrentEventModel> bannerItems;

    public BannerModel(List<HomeCurrentEventModel> bannerItems) {
        this.bannerItems = bannerItems;
    }

}
