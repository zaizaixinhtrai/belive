package com.appster.features.banner_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.customview.CustomFontButton;
import com.appster.utility.ImageLoaderUtil;
import com.apster.common.DialogManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BannerDetailActivity extends BaseToolBarActivity implements BannerDetailContract.View {


    private static final String DETAIL_URL = "banner_detail_url";
    private static final String EVENT_USER_ID = "event_user_id";
    private static final String EVENT_TITLE = "event_title";
    BannerDetailContract.UserActions presenter;

    @Bind(R.id.ivBannerDetail)
    ImageView mIvBannerDetail;
    @Bind(R.id.btnFollow)
    CustomFontButton mBtnFollow;


    private String mDetailUrl;
    private String mUserId;
    private String mEventTitle;

    public static Intent createIntent(Context context, String title, String detailUrl, String userId) {
        Intent intent = new Intent(context, BannerDetailActivity.class);
        intent.putExtra(EVENT_TITLE, title);
        intent.putExtra(DETAIL_URL, detailUrl);
        intent.putExtra(EVENT_USER_ID, userId);
        return intent;
    }

    //region-------activity life cycle-------
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBarTileNoCap(TextUtils.isEmpty(mEventTitle) ? "Event title" : mEventTitle);
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> finish());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    //endregion -------activity life cycle-------

    //region -------inheritance methods-------
    @Override
    public int getLayoutContentId() {
        return R.layout.banner_detail;
    }

    @Override
    public void init() {
        ButterKnife.bind(this);
        mDetailUrl = getIntent().getStringExtra(DETAIL_URL);
        mUserId = getIntent().getStringExtra(EVENT_USER_ID);
        mEventTitle = getIntent().getStringExtra(EVENT_TITLE);
        presenter = new BannerDetailPresenter();
        presenter.attachView(this);
        if (!TextUtils.isEmpty(mDetailUrl)) {
            ImageLoaderUtil.displayMediaImage(this, mDetailUrl, true, mIvBannerDetail);

        }
    }

    //endregion -------inheritance methods-------

    //region -------implement methods-------

    @Override
    public Context getViewContext() {
        return null;
    }

    @Override
    public void loadError(String errorMessage, int code) {
        handleError(errorMessage, code);
    }

    @Override
    public void showProgress() {
        DialogManager.getInstance().showDialog(this, getResources().getString(R.string.connecting_msg));
    }

    @Override
    public void hideProgress() {
        DialogManager.getInstance().dismisDialog();
    }

    @OnClick(R.id.btnFollow)
    public void onViewClicked() {
        // TODO: 4/26/17 go to profile screen
        if (!TextUtils.isEmpty(mUserId)) {
//            startActivityProfile(mUserId, "");
            presenter.followUser(mUserId);
        }
    }
    //endregion -------implement methods-------

    //region -------inner view methods-------
    @Override
    public void followUserResult() {
        startActivityProfile(mUserId, "");
    }
    //endregion -------inner view methods-------


    //region -------inner class-------

    //endregion -------inner class-------
}
