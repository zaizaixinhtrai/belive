package com.appster.features.income.gem_exchange;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.ListView;
import android.widget.TextView;

import com.appster.AppsterApplication;
import com.appster.manager.ShowErrorManager;
import com.appster.models.StarsToGemsModel;
import com.appster.R;
import com.appster.models.UserModel;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.MakeExchangeRequestModel;
import com.appster.activity.BaseToolBarActivity;
import com.appster.adapters.StarsToGemsAdapter;
import com.apster.common.Constants;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.Utils;

import java.util.ArrayList;
import java.util.Locale;

import static com.pack.utility.StringUtil.formatImage;

/**
 * Created by User on 8/22/2016.
 */
public class StarsToGemActivity extends BaseToolBarActivity {

    private StarsToGemsAdapter adapter;
    private ArrayList<StarsToGemsModel> gemsModel;
    private ListView listView;
    private TextView txtGold;
    private TextView txtBean;
    private UserModel mUserProfile;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, StarsToGemActivity.class);
        return intent;
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.acitivity_stars_to_gems;
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleTurnoffMenuSliding();
        setTopBarTile(getString(R.string.income_start_to_cash));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void init() {
        listView = findViewById(R.id.listView);
        txtBean = findViewById(R.id.txt_bean);
        txtGold = findViewById(R.id.txt_gold);
        mUserProfile = AppsterApplication.mAppPreferences.getUserModel();
        txtBean.setText(String.valueOf(mUserProfile.getTotalBean()));
        txtGold.setText(String.valueOf(mUserProfile.getTotalGold()));

        getExchangeRate();

    }

    private void getExchangeRate() {
        showDialog(this, getString(R.string.connecting_msg));
        mCompositeSubscription.add(AppsterWebServices.get().getExchangeRate()
                .subscribe(exchangeRateDataResponse -> {
                    dismisDialog();
                    if (exchangeRateDataResponse == null)
                        return;

                    if (exchangeRateDataResponse.getCode() != Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        handleError(exchangeRateDataResponse.getMessage(), exchangeRateDataResponse.getCode());
                    } else {
                        gemsModel = new ArrayList<>();
                        gemsModel.addAll(exchangeRateDataResponse.getData());
                        setAdapter();
                    }
                },error -> {
                    dismisDialog();
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    private void setAdapter() {

        adapter = new StarsToGemsAdapter(this, 0, gemsModel);
        adapter.setStarsToGemsListener(position ->
                excutedExchange(gemsModel.get(position).getExchangeId(), gemsModel.get(position).getGold(), gemsModel.get(position).getBean()));
        listView.setAdapter(adapter);
    }


    private void excutedExchange(final int exchangeId, final long gold, final long bean) {

        if (mUserProfile.getTotalGold() < gold){
            showError();
            return;
        }

        Bitmap gemIcon= BitmapFactory.decodeResource(getResources(), R.drawable.refill_gem_icon);
        Bitmap starIcon= BitmapFactory.decodeResource(getResources(), R.drawable.icon_gift_price);
       SpannableStringBuilder builder = new SpannableStringBuilder();
        final String goldString = String.format(Locale.US,"%,d",gold);
        final String beanString = String.format(Locale.US,"%,d",bean);
        builder.append(getString(R.string.stars_to_gems_exchange_confirm_exchange))
        .append(" ")
        .append(formatImage(this,"img",Bitmap.createScaledBitmap(starIcon, Utils.dpToPx(14),Utils.dpToPx(14),false)))
        .append(" ")
        .append(goldString);
        builder.setSpan(new ForegroundColorSpan(0xFFA1773A), builder.length() - goldString.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        builder.append(" ")
                .append(getString(R.string.exchange_confirm_to))
                .append(" ")
                .append(formatImage(this,"img",Bitmap.createScaledBitmap(gemIcon, Utils.dpToPx(14),Utils.dpToPx(14),false)))
                .append(beanString);
        builder.setSpan(new ForegroundColorSpan(0xFFA1773A), builder.length() - beanString.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(".");


        DialogbeLiveConfirmation.Builder dialogBuilder = new DialogbeLiveConfirmation.Builder();
        dialogBuilder.title(getString(R.string.exchange_confirm_title))
                .message(builder)
                .confirmText(getString(R.string.exchange_dialog_sure))
                .onConfirmClicked(() -> callExchangeApi(exchangeId,gold,bean))
                .build().show(this);


//        final ConfirmExchangeDialog rateDialog = ConfirmExchangeDialog.getInstance();
//        rateDialog.show(this, "", gold, bean, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rateDialog.dismiss();
//
//
//            }
//        });
    }

    private void callExchangeApi(int exchangeId,final long gold, final long bean) {
        showDialog(StarsToGemActivity.this, getString(R.string.connecting_msg));
        mCompositeSubscription.add(AppsterWebServices.get().makeExchangeRate("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), new MakeExchangeRequestModel(exchangeId))
                .subscribe(makeExchangeDataResponse -> {
                    dismisDialog();
                    if (makeExchangeDataResponse.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        AppsterApplication.mAppPreferences.getUserModel().setTotalBean(makeExchangeDataResponse.getData().getTotalBean());
                        AppsterApplication.mAppPreferences.getUserModel().setTotalGold(makeExchangeDataResponse.getData().getTotalGold());
                        txtBean.setText(String.valueOf(AppsterApplication.mAppPreferences.getUserModel().getTotalBean()));
                        txtGold.setText(String.valueOf(AppsterApplication.mAppPreferences.getUserModel().getTotalGold()));
                        showSuccess(gold, bean);
                    } else if (makeExchangeDataResponse.getCode() == ShowErrorManager.not_enough_star) {
                        showError();
                    } else {
                        handleError(makeExchangeDataResponse.getMessage(), makeExchangeDataResponse.getCode());
                    }
                },error -> {
                    dismisDialog();
                    handleError(error.getMessage(), Constants.RETROFIT_ERROR);
                }));
    }

    private void showSuccess(long gold, long bean){
        Bitmap gemIcon= BitmapFactory.decodeResource(getResources(), R.drawable.refill_gem_icon);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(formatImage(this,"img",Bitmap.createScaledBitmap(gemIcon, Utils.dpToPx(14),Utils.dpToPx(14),false)))
                .append(" ")
                .append(String.valueOf(bean));
        builder.setSpan(new ForegroundColorSpan(0xFFA1773A), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(" ").append(getString(R.string.stars_to_gems_exchange_have_added));

        DialogbeLiveConfirmation.Builder dialogBuilder = new DialogbeLiveConfirmation.Builder();
        dialogBuilder.title(getString(R.string.exchange_success))
                .message(builder)
                .singleAction(true)
                .build().show(this);

//        final ExchangeInformDialog dialog = ExchangeInformDialog.getInstance();
//        dialog.showChangeSuccess(StarsToGemActivity.this, getString(R.string.stars_to_gems_exchange_success), gold, bean, v -> dialog.dismiss());
    }


    private void showError() {
        DialogbeLiveConfirmation.Builder dialogBuilder = new DialogbeLiveConfirmation.Builder();
        dialogBuilder.title(getString(R.string.exchange_oops))
                .message(getString(R.string.stars_to_gems_exchange_not_enough_gold))
                .singleAction(true)
                .build().show(this);
//        final ExchangeInformDialog dialog = ExchangeInformDialog.getInstance();
//        dialog.showChangeError(StarsToGemActivity.this, getString(R.string.stars_to_gems_exchange_oops), 0, 0, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
    }
}
