package com.appster.core;

import androidx.annotation.DrawableRes;

/**
 * Created by thanhbc on 11/27/17.
 */

public interface BeLiveThemeHelper {
    @DrawableRes
    int getAppMenuIcon();

    @DrawableRes
    int getAppEditProfileIcon();

    @DrawableRes
    int getAppOptionMenuIcon();

    @DrawableRes
    int getAppNotificationBellIcon();

    @DrawableRes
    int getAppNotificationBellIconLight();
    @DrawableRes
    int getAppMenuIconLight();

    @DrawableRes
    int getLiveTagIcon();

    @DrawableRes
    int getRecordedTagIcon();

    @DrawableRes
    int getNavHomeIcon();

    @DrawableRes
    int getNavNewFeedsIcon();

    @DrawableRes
    int getNavSearchIcon();

    @DrawableRes
    int getNavProfileIcon();

    @DrawableRes
    int getToolbarBackIcon();

    @DrawableRes
    int getNavLiveIcon();

    int getToolbarTopPadding();

    boolean isTransparentStatusBarRequired();

    @DrawableRes
    int getNavPointsIcon();
}
