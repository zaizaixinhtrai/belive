package com.appster.features.category_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.features.home.ChildHomeFragment;
import com.appster.main.MainActivity;
import com.apster.common.Constants;
import com.pack.utility.StringUtil;

import timber.log.Timber;


public class CategoryDetailActivity extends BaseToolBarActivity {

    static final String CATEGORY_ID = "CategoryId";
    static final String CATEGORY_NAME = "CategoryName";

    ChildHomeFragment mChildHomeFragment;
    int mCategoryId = -1;
    String mCategoryName = "";

    boolean isFirstTime = true;

    public static Intent createIntent(Context context, int categoryId, String categoryName) {
        Intent intent = new Intent(context, CategoryDetailActivity.class);
        intent.putExtra(CATEGORY_ID, categoryId);
        intent.putExtra(CATEGORY_NAME, categoryName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryId = getIntent().getExtras().getInt(CATEGORY_ID, 0);
        mCategoryName = getIntent().getExtras().getString(CATEGORY_NAME);
        if (savedInstanceState == null) {
            if (mChildHomeFragment == null) {
                mChildHomeFragment = ChildHomeFragment.newInstance(mCategoryId);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mChildHomeFragment, mCategoryName)
                    .commit();
            //after transaction you must call the executePendingTransaction
            getSupportFragmentManager().executePendingTransactions();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        setTopBarTitleWithFont(mCategoryName,getString(R.string.font_helveticaneuebold), 12,"#bbbbbb");
        setTopBarTile(mCategoryName);
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
        goneNotify(true);
        handleTurnoffMenuSliding();
        if (mChildHomeFragment != null && !isFirstTime) {
            mChildHomeFragment.onRefreshData();
        }
        isFirstTime=false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case Constants.REQUEST_MEDIA_PLAYER_STREAM:
                    if (data == null) return; /*only happen if user back by popup stream has been removed on MediaPlayerFragment*/
                    String userId = data.getStringExtra(Constants.USER_PROFILE_ID);
                    String userDisplayname = data.getStringExtra(Constants.USER_PROFILE_ID);

                    if (!StringUtil.isNullOrEmptyString(userId) &&
                            AppsterApplication.mAppPreferences.isUserLogin() &&
                            AppsterApplication.mAppPreferences.getUserModel().getUserId().equals(userId)) {
                        //go back to main activity for handle this
                        Timber.e("requestCode %d",requestCode);
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.replaceExtras(data);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        if (!StringUtil.isNullOrEmptyString(userId)) {
                            startActivityProfile(userId, userDisplayname);
                        }
                    }
            }
        }
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_category;
    }

    @Override
    public void init() {

    }
}
