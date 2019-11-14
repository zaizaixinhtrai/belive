package com.appster.refill;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.features.invite_friend.InviteFriendActivity;
import com.appster.iap_manager.InAppBillHelper;
import com.appster.layout.GridViewWithHeaderAndFooter;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.PixelUtil;
import com.pack.utility.CheckNetwork;
import com.pack.utility.DialogInfoUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class ActivityRefill extends BaseToolBarActivity implements InAppBillHelper.BillListener {
    private GridViewWithHeaderAndFooter topup_gridview;
    private List<RefillListItem> mListItems = new ArrayList<>();
    private RefillClassAdapter topUpClassAdapter;
    private long totalBean;
    private String transactionId;
    private TextView txt_txt_bean;
    private Button btnInviteFriend;
    private ViewGroup mHeader;
    private View mFooterView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CheckNetwork.isNetworkAvailable(getApplication())) {
            InAppBillHelper.init(mActivity);
            InAppBillHelper.register(this);
            InAppBillHelper.registerSuccessPurchasedBean(totalBean1 -> txt_txt_bean.setText(totalBean1 + ""));
            CheckTopUp();
        } else {
            utility.showMessage(getString(R.string.app_name), getResources()
                    .getString(R.string.no_internet_connection), ActivityRefill.this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBarTile(getString(R.string.refill_title));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
        handleTurnoffMenuSliding();
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.top_up;
    }

    @Override
    public void init() {
        intId();
    }

    private void intId() {

        utility = new DialogInfoUtility();

        topup_gridview = (GridViewWithHeaderAndFooter) findViewById(R.id.topup_gridview);

        goneNotify(true);
    }

    private void addHeaderAndFooter() {
        LayoutInflater inflater = getLayoutInflater();
        mHeader = (ViewGroup) inflater.inflate(R.layout.topup_header_listview, topup_gridview, false);
        txt_txt_bean = (TextView) mHeader.findViewById(R.id.txt_bean);
        btnInviteFriend = (Button) mHeader.findViewById(R.id.btnInviteFriend);
        SpannableStringBuilder builder = new SpannableStringBuilder(mActivity.getResources().getString(R.string.refill_invite_and_get));

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.refill_gem_icon);
        drawable.setBounds(0, 0, PixelUtil.dpToPx(this, 15), PixelUtil.dpToPx(this, 15));
        builder.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE),
                builder.length() - 1, builder.length(), 0);
        builder.append(" 100");
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#a4f475")), builder.length() - 3, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        btnInviteFriend.setOnClickListener(v -> {
            Intent intent1 = new Intent(ActivityRefill.this, InviteFriendActivity.class);
            startActivity(intent1);
        });
        btnInviteFriend.setGravity(Gravity.CENTER);
        btnInviteFriend.setText(builder);

        txt_txt_bean.setText(totalBean + "");
        topup_gridview.addHeaderView(mHeader, null, false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        mFooterView = layoutInflater.inflate(R.layout.topup_footer_listview, null);
        topup_gridview.addFooterView(mFooterView);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void CheckTopUp() {
        DialogManager.getInstance().showDialog(ActivityRefill.this, getResources().getString(R.string.connecting_msg));
        mCompositeSubscription.add(AppsterWebServices.get().getRefillList(AppsterApplication.mAppPreferences.getUserTokenRequest())
                .subscribe(refillDataResponseModel -> {
                    DialogManager.getInstance().dismisDialog();
                    if (refillDataResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        totalBean = refillDataResponseModel.getData().getTotal_bean();
                        addHeaderAndFooter();
                        mListItems = refillDataResponseModel.getData().getTopUpList();
                        InAppBillHelper.setListSku(mListItems);
                        topUpClassAdapter = new RefillClassAdapter(ActivityRefill.this, mListItems);
                        topup_gridview.setAdapter(topUpClassAdapter);
                    } else if (refillDataResponseModel.getCodeDetails() != null && refillDataResponseModel.getCodeDetails().size() > 0) {
                        handleError(refillDataResponseModel.getMessage(), refillDataResponseModel.getCodeDetails().get(0).getCode());
                    } else {
                        handleError(refillDataResponseModel.getMessage(), refillDataResponseModel.getCode());
                    }
                }, error -> {
                    DialogManager.getInstance().dismisDialog();
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DialogManager.getInstance().showDialog(ActivityRefill.this, getResources().getString(R.string.connecting_msg));
        InAppBillHelper.handleActivityResult(requestCode, resultCode, data);
        Timber.e("onActivityResult");
        updateGem();
    }

    @Override
    public void onBackPressed() {
        mCompositeSubscription.add(Observable.just(true).throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(b -> !DialogManager.isShowing())
                .subscribe(action -> finish(), Timber::e));
    }

    @Override
    public void onChange() {

    }

    @Override
    public void onDestroy() {
        InAppBillHelper.deregister(this);
        InAppBillHelper.deRegisterSuccessPurchasedBean();
        InAppBillHelper.destroy();
        super.onDestroy();

    }

    private void updateGem() {
        if (txt_txt_bean != null) {
            txt_txt_bean.setText(String.valueOf(AppsterApplication.mAppPreferences.getUserModel().getTotalBean()));
            Timber.e("gem =" + AppsterApplication.mAppPreferences.getUserModel().getTotalBean());
        }
    }
}
