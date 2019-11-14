package com.appster.core;

import com.appster.R;
import com.apster.common.Utils;

/**
 * Created by thanhbc on 11/28/17.
 */

public class BeLiveXMasTheme extends BeLiveDefaultTheme {
    @Override
    public int getAppMenuIcon() {
        return R.drawable.menu_icon_xmas;
    }

    @Override
    public int getAppNotificationBellIcon() {
        return R.drawable.btn_notification_xmas;
    }

    @Override
    public int getNavHomeIcon() {
        return R.drawable.nav_home_xmas_selector;
    }

    @Override
    public int getToolbarBackIcon() {
        return R.drawable.icon_back_btn_white_xmas;
    }

    @Override
    public int getNavNewFeedsIcon() {
        return R.drawable.nav_newfeeds_xmas_selector;
    }

    @Override
    public int getNavSearchIcon() {
        return R.drawable.nav_search_xmas_selector;
    }

    @Override
    public int getNavProfileIcon() {
        return R.drawable.nav_profile_xmas_selector;
    }

    @Override
    public int getAppEditProfileIcon() {
        return R.drawable.ic_edit_profile_xmas;
    }

    @Override
    public int getAppOptionMenuIcon() {
        return R.drawable.ic_option_menu_xmas;
    }

    @Override
    public int getToolbarTopPadding() {
        return Utils.dpToPx(24);
    }

    @Override
    public boolean isTransparentStatusBarRequired() {
        return true;
    }
}
