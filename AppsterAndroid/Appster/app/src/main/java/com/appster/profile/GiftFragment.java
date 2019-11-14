package com.appster.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.fragment.BaseFragment;
import com.appster.layout.WrapContentHeightViewPager;
import com.appster.models.UserModel;
import com.appster.refill.ActivityRefill;
import com.appster.sendgift.GiftItemModel;
import com.appster.utility.AppsterUtility;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.SendGiftRequestModel;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.data.repository.GiftStoreDataRepository;
import com.data.repository.datasource.GiftStoreDataSource;
import com.data.repository.datasource.cloud.CloudGiftStoreDataSource;
import com.domain.interactors.giftstore.GetGiftStoreUseCase;
import com.domain.repository.GiftStoreRepository;
import com.pack.utility.DialogInfoUtility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by linh on 14/12/2016.
 */

public class GiftFragment extends BaseFragment {

    private View rootView;
    @Bind(R.id.viewPager)
    WrapContentHeightViewPager viewPager;
    @Bind(R.id.txt_total_gem)
    TextView txtTotalGem;// ~ bean
    @Bind(R.id.viewPagerCountDots)
    LinearLayout dotsLayout;
    @Bind(R.id.btn_send)
    ImageButton btn_send;
    @Bind(R.id.btnRefill)
    ImageButton btn_refill;

    ImageView[] dots;

    private GiftPagerAdapter myViewPagerAdapter;
    // page change listener
    private ViewPager.OnPageChangeListener viewPagerPageChangeListener;
//    private Dialog dialogSendGift;

    Context context;
    DialogInfoUtility utility;

    GiftRecyclerViewAdapter.CompleteSendGift completeSendGift;

    UserModel appOwner;
    private UserModel userProfileDetails;
    private String userID;

    private String gift_id = "";
    long totalGem;
    int dotsCount;
    private int streamID;
    boolean isShowAlertSendSucessFull = true;


    private boolean itemTransparent = true;
    private GetGiftStoreUseCase mGetGiftStoreUseCase;
    //================ inherited methods ===========================================================
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        appOwner = AppsterApplication.mAppPreferences.getUserModel();
        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            totalGem = appOwner.getTotalBean();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.dialog_send_gif, container, false);
        ButterKnife.bind(this, rootView);
        handleButtonSend();
        initUseCase();
//        getListGift();
        utility = new DialogInfoUtility();
        return rootView;
    }
    private void initUseCase() {
        GiftStoreDataSource giftStoreDataSource = new CloudGiftStoreDataSource(AppsterWebServices.get(), AppsterUtility.getAuth());
        GiftStoreRepository giftStoreRepository = new GiftStoreDataRepository(giftStoreDataSource);
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        mGetGiftStoreUseCase = new GetGiftStoreUseCase(uiThread, ioThread, giftStoreRepository);
    }
    @Override
    public void onResume() {
        super.onResume();
        refresh();
        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            txtTotalGem.setText(String.valueOf(totalGem));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    //=========== implemented methods ==============================================================
    @OnClick(R.id.btn_send)
    void onSendButtonClick() {
        if (myViewPagerAdapter == null) {
            return;
        }
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ((BaseToolBarActivity) getActivity()).goingLoginScreen();

        } else if (myViewPagerAdapter.getSelectedItem() == null) {
            showDialognotChoseGift();

        }else if (myViewPagerAdapter.getSelectedItem().getAmount() <= 0 && myViewPagerAdapter.getSelectedItem().getGiftType() != 0) {
            showToastOutOfUnpurchaseGift();
        } else if (myViewPagerAdapter.getSelectedItem().getAmount() > 0 || appOwner.getTotalBean() - myViewPagerAdapter.getSelectedItem().getCostBean() >= 0) {
            sendGift(myViewPagerAdapter.getSelectedItem());

        } else {
            showMessageNotEnoughStart();
        }
    }
    private void showToastOutOfUnpurchaseGift() {
        if(getActivity()==null) return;
        Toast.makeText(getActivity(), getActivity().getString(R.string.out_of_unpurchasable_gift), Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.btnRefill)
    void onRefillButtonClick() {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ((BaseToolBarActivity) getActivity()).goingLoginScreen();
            return;
        }
        openRefillScreen();
    }

    //=========== inner methods ====================================================================
    public void setUserProfileDetails(UserModel userProfileDetails) {
        this.userProfileDetails = userProfileDetails;
        userID = userProfileDetails.getUserId();
    }

    public void setBackgroundTransparent(boolean itemTransparent) {
        this.itemTransparent = itemTransparent;
    }

    private void handleButtonSend() {
        if (AppsterApplication.mAppPreferences.isUserLogin() && appOwner.getUserId().equals(userID)) {
            btn_send.setVisibility(View.GONE);
        } else {
            btn_send.setVisibility(View.VISIBLE);
        }
    }

    private void showDialognotChoseGift() {

        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title(getString(R.string.app_name))
                .message(getString(R.string.sendgift_no_choose_gift))
                .confirmText(getString(R.string.btn_text_ok))
                .singleAction(true)
                .onConfirmClicked(() -> {
                    //action
                })
                .build().show(getContext());
    }

    private void showMessageNotEnoughStart() {
//        utility.showMessagePurchasedMore(context.getString(R.string.app_name),
//                context.getString(R.string.sendgift_no_choose_gift), context, v -> openRefillScreen());

        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title(getString(R.string.app_name))
                .message(getString(R.string.sendgift_no_enough_bean))
                .confirmText(getString(R.string.btn_text_ok))
                .singleAction(true)
                .onConfirmClicked(this::openRefillScreen)
                .build().show(getContext());
    }

    private void openRefillScreen() {
        Intent i = new Intent(context, ActivityRefill.class);
        getActivity().startActivityForResult(i, Constants.REQUEST_CODE_REFILL_SCREEN);
    }

    public void setCompleteSendGift(GiftRecyclerViewAdapter.CompleteSendGift completeSendGift) {
        this.completeSendGift = completeSendGift;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onGiftSent(GemChangeEvent event) {
        totalGem = event.gemCount;
        txtTotalGem.setText(String.valueOf(totalGem));
    }

    private void sendGift(GiftItemModel item) {
        if (null == item) return;

        DialogManager.getInstance().showDialog(context, context.getResources().getString(R.string.connecting_msg));
        SendGiftRequestModel request = new SendGiftRequestModel();
        request.setReceiver_user_id(userID);
        request.setGift_id(item.getGiftId());

        if (item.getAmount() != 0) {
            request.setUsingGiftInInventory(true);
        }

        if (streamID > 0) {
            request.setStream_id(streamID);
        }

        mCompositeSubscription.add(AppsterWebServices.get().sendGift("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .doOnNext(sendGiftDataResponse -> DialogManager.getInstance().dismisDialog())
                .filter(sendGiftDataResponse -> sendGiftDataResponse != null)
                .subscribe(sendGiftResponseModel -> {
                    if (sendGiftResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        if (item.getAmount() > 0) {
                            item.updateAmount(sendGiftResponseModel.getData().getSender().getAmount());
                            myViewPagerAdapter.notifySelectedAdapteItemChanged();
                        }
                        appOwner.setTotalBean(sendGiftResponseModel.getData().getSender().getTotalBean());
                        appOwner.setTotalGold(sendGiftResponseModel.getData().getSender().getTotalGold());
                        appOwner.setTotalGoldFans(sendGiftResponseModel.getData().getSender().getTotalGoldFans());
                        if (completeSendGift != null) {
                            completeSendGift.onSendSuccess(item, sendGiftResponseModel.getData().getSender().getTotalBean(),
                                    sendGiftResponseModel.getData().getSender().getTotalGoldFans(),
                                    sendGiftResponseModel.getData().getReceiver().getTotalBean(),
                                    sendGiftResponseModel.getData().getReceiver().getTotalGoldFans(),
                                    sendGiftResponseModel.getData().getVotingScores(),
                                    sendGiftResponseModel.getData().topFanList, sendGiftResponseModel.getData().dailyTopFans);
                        }
                        if (isShowAlertSendSucessFull) {
                            Toast.makeText(getContext().getApplicationContext(), R.string.gift_has_been_sent_successfully, Toast.LENGTH_SHORT).show();
                        }
                        EventBus.getDefault().post(new GemChangeEvent(appOwner.getTotalBean()));

                    } else {
                        ((BaseActivity) getActivity()).handleError(sendGiftResponseModel.getMessage(), sendGiftResponseModel.getCode());
                    }
                }, error -> DialogManager.getInstance().dismisDialog()));
    }

    private void getListGift() {
        DialogManager.getInstance().showDialog(context, context.getResources().getString(R.string.connecting_msg));

        mCompositeSubscription.add(mGetGiftStoreUseCase.execute(null)
                .subscribe(giftStoreModel -> {
                    DialogManager.getInstance().dismisDialog();
                    if (giftStoreModel == null) return;
                    if (!giftStoreModel.getGiftItems().isEmpty()) {
                            totalGem = giftStoreModel.getTotalGem();

                            setViewPagerItemsWithAdapter(giftStoreModel.getGiftItems());
                            setViewpagerIndicator();
                            if (AppsterApplication.mAppPreferences.isUserLogin()) {
                                appOwner.setTotalBean(totalGem);
                                txtTotalGem.setText(String.valueOf(totalGem));
                            }
                        }
                }, error -> DialogManager.getInstance().dismisDialog()));
    }


    void refresh() {
        getListGift();
    }

    void setViewPagerItemsWithAdapter(List<GiftItemModel> giftList) {

        if (myViewPagerAdapter == null && viewPagerPageChangeListener == null) {

            int residual = giftList.size() % 8;
            if (residual == 0) {
                dotsCount = giftList.size() / 8;
            } else {
                dotsCount = giftList.size() / 8 + 1;
            }

            viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < dotsCount; i++) {
                        dots[i].setBackgroundResource(R.drawable.circle_dot_gift);
                    }
                    dots[position].setBackgroundResource(R.drawable.circle_dot_gift_red);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    //don't need to implement
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    //don't need to implement
                }
            };


        }

        initViewPager(giftList);
    }

    private void initViewPager(List<GiftItemModel> giftList) {
        myViewPagerAdapter = new GiftPagerAdapter(getContext(), dotsCount, giftList);
        myViewPagerAdapter.setBackgroudTransparent(itemTransparent);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setOffscreenPageLimit(dotsCount);
    }

    void setViewpagerIndicator() {
        dotsLayout.removeAllViews();
        dots = new ImageView[dotsCount];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8, 0, 0, 0);

        for (int i = 0; i < dotsCount; i++) {

            dots[i] = new ImageView(context);
            dots[i].setLayoutParams(lp);
            dots[i].setBackgroundResource(R.drawable.circle_dot_gift);
            dotsLayout.addView(dots[i]);
        }

        dots[0].setBackgroundResource(R.drawable.circle_dot_gift_red);
    }

    //region inner classes
    class GemChangeEvent {
        long gemCount;

        GemChangeEvent(long gemCount) {
            this.gemCount = gemCount;
        }
    }
    //endregion
}
