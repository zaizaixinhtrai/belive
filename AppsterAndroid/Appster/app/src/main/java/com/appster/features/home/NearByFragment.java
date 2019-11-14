package com.appster.features.home;

import android.os.Bundle;

import com.appster.webservice.AppsterWebServices;

/**
 * Created by thanhbc on 6/29/17.
 */

public class NearByFragment extends ChildHomeFragment {
    public static NearByFragment newInstance(int categoryId) {
        NearByFragment f = new NearByFragment();
        Bundle b = new Bundle();
        b.putInt(CATEGORY_ID, categoryId);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected HomeContract.UserActions getPresenter() {
        return new NearByPresenter(AppsterWebServices.get(), mAuthen);
    }

    @Override
    public boolean shouldGetCategories() {
        return false;
    }

    @Override
    public boolean shouldShowCategoryTag() {
        return false;
    }

    @Override
    public boolean shouldShowDistance() {
        return true;
    }
}
