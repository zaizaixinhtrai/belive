package com.appster.features.home;

import android.os.Bundle;

import com.appster.webservice.AppsterWebServices;

/**
 * Created by thanhbc on 6/29/17.
 */

public class LatestLiveFragment extends ChildHomeFragment {

    public static LatestLiveFragment newInstance(int categoryId) {
        LatestLiveFragment f = new LatestLiveFragment();
        Bundle b = new Bundle();
        b.putInt(CATEGORY_ID, categoryId);
        f.setArguments(b);
        return f;
    }

    @Override
    protected HomeContract.UserActions getPresenter() {
        return new LatestLivePresenter(AppsterWebServices.get(), mAuthen);
    }

    @Override
    public boolean shouldGetCategories() {
        return false;
    }

    @Override
    public boolean shouldShowCategoryTag() {
        return false;
    }
}
