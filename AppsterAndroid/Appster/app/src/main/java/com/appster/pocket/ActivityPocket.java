package com.appster.pocket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.utility.ConstantBundleKey;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.CreditsRequestModel;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.pack.utility.CheckNetwork;

/**
 * Created by User on 10/15/2015.
 */
public class ActivityPocket extends BaseToolBarActivity implements View.OnClickListener {

    private RelativeLayout rlt_beans;
    private RelativeLayout rlt_usd;


    private TextView txt_usd;
    private TextView txt_bean;

    private long totalBean;
    private long totalUSD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setTopBarTile(getString(R.string.pocket_title));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
        handleTurnoffMenuSliding();

    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_pocket;
    }

    @Override
    public void init() {

        rlt_beans = (RelativeLayout) findViewById(R.id.rlt_beans);
        rlt_usd = (RelativeLayout) findViewById(R.id.rlt_usd);

        rlt_beans.setOnClickListener(this);
        rlt_usd.setOnClickListener(this);

        txt_usd = (TextView) findViewById(R.id.txt_usd);
        txt_bean = (TextView) findViewById(R.id.txt_bean);

        if (CheckNetwork.isNetworkAvailable(ActivityPocket.this)) {
            getUserCredits();
        } else {
            utility.showMessage(getString(R.string.app_name),
                    getString(R.string.no_internet_connection), ActivityPocket.this);
        }

        goneNotify(true);
    }

    private void getUserCredits() {
        DialogManager.getInstance().showDialog(ActivityPocket.this, getString(R.string.connecting_msg));

        CreditsRequestModel request = new CreditsRequestModel();
        AppsterWebServices.get().getUserCredits("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(creditsResponseModel -> {
                    DialogManager.getInstance().dismisDialog();
                    if (creditsResponseModel == null) return;
                    if (creditsResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        totalBean = creditsResponseModel.getData().getTotal_bean();
                        totalUSD = creditsResponseModel.getData().getTotal_gold();
                        txt_bean.setText(totalBean + "");
                        txt_usd.setText(totalUSD + "");

                    } else {
                        handleError(creditsResponseModel.getMessage(), creditsResponseModel.getCode());
                    }
                }, error -> {
                    DialogManager.getInstance().dismisDialog();
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                });
    }

    @Override
    public void onClick(View v) {

        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(mActivity,
                R.anim.push_in_to_right, R.anim.push_in_to_left);

        switch (v.getId()) {

            case R.id.rlt_beans:

                Intent intent = new Intent(mActivity, ActivityBeans.class);
                intent.putExtra(ConstantBundleKey.BUNDLE_POCKET_TYPE, ActivityBeans.Pocket_Type_Bean);
                intent.putExtra(ConstantBundleKey.BUNDLE_POCKET_VALUE, totalBean);
                startActivity(intent, options.toBundle());

                break;

            case R.id.rlt_usd:

                Intent inten = new Intent(mActivity, ActivityBeans.class);
                inten.putExtra(ConstantBundleKey.BUNDLE_POCKET_TYPE, ActivityBeans.Pocket_Type_USD);
                inten.putExtra(ConstantBundleKey.BUNDLE_POCKET_VALUE, totalUSD);
                startActivity(inten, options.toBundle());

                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
