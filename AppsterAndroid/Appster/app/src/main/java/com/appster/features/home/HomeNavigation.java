package com.appster.features.home;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.appster.features.home.HomeNavigation.ACTIONS;
import static com.appster.features.home.HomeNavigation.HOME;
import static com.appster.features.home.HomeNavigation.POINTS;
import static com.appster.features.home.HomeNavigation.SEARCH;
import static com.appster.features.home.HomeNavigation.WALL_FEED;

/**
 * Created by thanhbc on 6/3/17.
 */

@IntDef({HOME, WALL_FEED, SEARCH, POINTS,ACTIONS})
@Retention(RetentionPolicy.SOURCE)
public @interface HomeNavigation {
    int HOME = 0;
    int SEARCH = 1;
    int WALL_FEED = 2;
    int POINTS = 3;
    int ACTIONS = 4;
}
