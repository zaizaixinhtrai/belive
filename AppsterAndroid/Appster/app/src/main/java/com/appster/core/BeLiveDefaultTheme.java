package com.appster.core;

import com.appster.R;
import com.apster.common.Utils;

/**
 * Created by thanhbc on 11/27/17.
 */

public class BeLiveDefaultTheme implements BeLiveThemeHelper {
    @Override
    public int getAppMenuIcon() {
        return R.drawable.ic_toolbar_menu_dark;
    }

    @Override
    public int getAppEditProfileIcon() {
        return R.drawable.ic_edit_profile;
    }

    @Override
    public int getAppOptionMenuIcon() {
        return R.drawable.ic_option_menu;
    }

    @Override
    public int getAppNotificationBellIconLight() {
        return R.drawable.ic_toolbar_notification;
    }

    @Override
    public int getAppMenuIconLight() {
        return R.drawable.ic_toolbar_menu;
    }

    @Override
    public int getAppNotificationBellIcon() {
        return R.drawable.ic_toolbar_notification_dark;
    }

    @Override
    public int getLiveTagIcon() {
        return R.drawable.home_btn_live;
    }

    @Override
    public int getRecordedTagIcon() {
        return R.drawable.home_btn_recorded;
    }

    @Override
    public int getNavHomeIcon() {
        return R.drawable.nav_home_selector;
    }

    @Override
    public int getNavNewFeedsIcon() {
        return R.drawable.nav_newfeeds_selector;
    }

    @Override
    public int getNavSearchIcon() {
        return R.drawable.nav_search_selector;
    }

    @Override
    public int getNavProfileIcon() {
        return R.drawable.nav_profile_selector;
    }

    @Override
    public int getToolbarBackIcon() {
        return R.drawable.icon_back_btn_white;
    }

    @Override
    public int getNavLiveIcon() {
         return R.drawable.nav_live_selector;
    }

    @Override
    public int getToolbarTopPadding(){
        return Utils.dpToPx(0);
    }

    @Override
    public boolean isTransparentStatusBarRequired() {
        return false;
    }

    @Override
    public int getNavPointsIcon() {
        return R.drawable.nav_points_selector;
    }
}
