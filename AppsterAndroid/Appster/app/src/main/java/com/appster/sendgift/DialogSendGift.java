package com.appster.sendgift;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.customview.CustomFontTextView;
import com.appster.interfaces.DiaLogDismissListener;
import com.appster.layout.WrapContentHeightViewPager;
import com.appster.models.UserModel;
import com.appster.profile.GiftPagerAdapter;
import com.appster.profile.GiftRecyclerViewAdapter;
import com.appster.refill.ActivityRefill;
import com.appster.utility.AppsterUtility;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.SendGiftRequestModel;
import com.appster.webservice.response.SendGiftResponseModel;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.Utils;
import com.data.repository.GiftStoreDataRepository;
import com.data.repository.datasource.GiftStoreDataSource;
import com.data.repository.datasource.cloud.CloudGiftStoreDataSource;
import com.domain.interactors.giftstore.GetGiftStoreUseCase;
import com.domain.repository.GiftStoreRepository;
import com.pack.utility.DialogInfoUtility;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by User on 8/21/2015.
 */
public class DialogSendGift implements DialogInterface.OnDismissListener {


    @Bind(R.id.viewPager)
    WrapContentHeightViewPager viewPager;
    @Bind(R.id.btn_send)
    ImageButton btn_send;
    @Bind(R.id.txt_total_gem)
    CustomFontTextView txt_total_gems;
    @Bind(R.id.btnRefill)
    ImageButton btn_refill;
    @Bind(R.id.viewPagerCountDots)
    LinearLayout dotsLayout;
    @Bind(R.id.llGiftContainer)
    LinearLayout llGiftContainer;

    private Dialog dialogSendGift;
    private Activity activity;
    private ArrayList<GiftItemModel> arrayList_sendGiftGridView = new ArrayList<GiftItemModel>();
    private DialogInfoUtility utility;


    GiftPagerAdapter myViewPagerAdapter;

    private ImageView[] dots;

    int dotsCount;

    private int totalGems;
    private String userID;
    // for send gif in stream
    private int streamID;

    private boolean isShowAlertSendSucessFull = true;
    private AtomicBoolean dismissEnable = new AtomicBoolean(true);
    private GiftRecyclerViewAdapter.CompleteSendGift completeSendGift;
    private DiaLogDismissListener mDialogDismisListener;
    private boolean mIsPrivateChat;
    private CompositeSubscription mCompositeSubscription;
    private GetGiftStoreUseCase mGetGiftStoreUseCase;

    public DialogSendGift(final Activity activity, UserModel userProfileDetails) {
        this.activity = activity;
        this.userID = userProfileDetails.getUserId();
        dialogSendGift = new Dialog(activity, R.style.DialogSlideToUpAnim);
        dialogSendGift.setCanceledOnTouchOutside(true);
        dialogSendGift.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialogSendGift.getWindow();
        if (window != null) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams layoutParams = dialogSendGift.getWindow().getAttributes();
            layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(layoutParams);
        }
        mIsPrivateChat = true;
        dialogSendGift.setContentView(R.layout.dialog_send_gif);

        ButterKnife.bind(this, dialogSendGift.getWindow().getDecorView());
        mCompositeSubscription = new CompositeSubscription();
        ViewGroup.LayoutParams params = llGiftContainer.getLayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        llGiftContainer.setLayoutParams(params);
        utility = new DialogInfoUtility();
        isShowAlertSendSucessFull = true;

        viewPager.setOnClickListener(v -> dialogSendGift.dismiss());
        dialogSendGift.setOnDismissListener(this);

        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            txt_total_gems.setText(String.valueOf(AppsterApplication.mAppPreferences.getUserModel().getTotalBean()));
        }
        initUseCase();
    }


    public DialogSendGift(final Activity activity, String userID, int StreamID) {
        this.activity = activity;
        this.userID = userID;
        this.streamID = StreamID;
        initDialogFullScreen();

        utility = new DialogInfoUtility();
        isShowAlertSendSucessFull = false;

        dialogSendGift.setOnDismissListener(this);
        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            txt_total_gems.setText(String.valueOf(AppsterApplication.mAppPreferences.getUserModel().getTotalBean()));
        }
        initUseCase();
    }

    public DialogSendGift(final Activity activity, String userID, boolean isOnMe) {
        this.activity = activity;
        this.userID = userID;
        initDialogFullScreen();

        utility = new DialogInfoUtility();
        isShowAlertSendSucessFull = false;

        dialogSendGift.setOnDismissListener(this);

        if (AppsterApplication.mAppPreferences.isUserLogin()) {
            txt_total_gems.setText(String.valueOf(AppsterApplication.mAppPreferences.getUserModel().getTotalBean()));
        }
        initUseCase();
    }

    private void initUseCase() {
        GiftStoreDataSource giftStoreDataSource = new CloudGiftStoreDataSource(AppsterWebServices.get(), AppsterUtility.getAuth());
        GiftStoreRepository giftStoreRepository = new GiftStoreDataRepository(giftStoreDataSource);
        Scheduler uiThread = AndroidSchedulers.mainThread();
        Scheduler ioThread = Schedulers.io();
        mGetGiftStoreUseCase = new GetGiftStoreUseCase(uiThread, ioThread, giftStoreRepository);
    }

    //region on dialog item clicked
    @OnClick(R.id.btn_send)
    public void onSendClicked(View v) {
        if (myViewPagerAdapter == null) return;
//        AppsterUtility.temporaryLockView(v);
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ((BaseToolBarActivity) activity).goingLoginScreen();
        } else if (myViewPagerAdapter.getSelectedItem() == null) {
            showDialognotChoseGift();
        } else if (myViewPagerAdapter.getSelectedItem().getAmount() <= 0 && myViewPagerAdapter.getSelectedItem().getGiftType() != 0) {
            showToastOutOfUnpurchaseGift();
        } else if (myViewPagerAdapter.getSelectedItem().getAmount() > 0 || AppsterApplication.mAppPreferences.getUserModel().getTotalBean() - myViewPagerAdapter.getSelectedItem().getCostBean() >= 0) {

            sendGift(myViewPagerAdapter.getSelectedItem());

        } else {
            showMessageNotEnoughStart();
        }
    }

    private void showToastOutOfUnpurchaseGift() {
        if(activity==null) return;
        Toast.makeText(activity, activity.getString(R.string.out_of_unpurchasable_gift), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btnRefill)
    public void onRefillClicked() {
        if (!AppsterApplication.mAppPreferences.isUserLogin()) {
            ((BaseToolBarActivity) activity).goingLoginScreen();
            return;
        }
        openRefillScreen();
    }

    @OnClick(R.id.llGiftContainer)
    public void onllGiftContainerClicked() {
        closeGiftDialog();
    }

    private void closeGiftDialog() {
        if (!dismissEnable.get()) return;
        arrayList_sendGiftGridView.clear();
        if (dialogSendGift != null) dialogSendGift.dismiss();
    }

    @OnClick(R.id.root_view)
    public void onOutSideClicked() {
        closeGiftDialog();
    }
    //endregion


    public void initDialogFullScreen() {
        dialogSendGift = new Dialog(activity, R.style.DialogSlideToUpAnim);
        dialogSendGift.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialogSendGift.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogSendGift.setCanceledOnTouchOutside(true);
        dialogSendGift.setContentView(R.layout.dialog_send_gif);

        dialogSendGift.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams layoutParams = dialogSendGift.getWindow().getAttributes();
        layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(layoutParams);
        ButterKnife.bind(this, dialogSendGift.getWindow().getDecorView());
        mCompositeSubscription = new CompositeSubscription();
        dialogSendGift.findViewById(R.id.llGiftContainer).setBackgroundColor(ContextCompat.getColor(activity, R.color.color_242424_80));
        ViewGroup.LayoutParams params = llGiftContainer.getLayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        llGiftContainer.setLayoutParams(params);
        txt_total_gems.setTextColor(ContextCompat.getColor(activity, R.color.white));
    }


    private void openRefillScreen() {
        Intent i = new Intent(activity, ActivityRefill.class);
        activity.startActivityForResult(i, Constants.REQUEST_CODE_REFILL_SCREEN);
    }

    public void setCompleteSendGift(GiftRecyclerViewAdapter.CompleteSendGift completeSendGift) {
        this.completeSendGift = completeSendGift;
    }

    private void showDialognotChoseGift() {
        utility.showMessage(activity.getString(R.string.app_name),
                activity.getString(R.string.sendgift_no_choose_gift),
                activity);
    }

    private void showMessageNotEnoughStart() {
        utility.showMessagePurchasedMore(activity, v -> openRefillScreen());
    }

    public void show() {

        dialogSendGift.show();
        getListGift();

    }

    public void show(boolean hideButtonSend) {

        if (hideButtonSend) {
            btn_send.setVisibility(View.GONE);
        }

        dialogSendGift.show();
        getListGift();

    }


    // page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener;
//            = new ViewPager.OnPageChangeListener() {
//
//        @Override
//        public void onPageSelected(int position) {
//            for (int i = 0; i < dotsCount; i++) {
//                dots[i].setBackgroundResource(R.drawable.circle_dot_gift);
//            }
//            dots[position].setBackgroundResource(R.drawable.circle_dot_gift_red);
//        }
//
//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {
//
//        }
//    };

    private void setViewPagerItemsWithAdapter() {

        if (myViewPagerAdapter == null && viewPagerPageChangeListener == null) {

            int residual = arrayList_sendGiftGridView.size() % 8;
            if (residual == 0) {
                dotsCount = arrayList_sendGiftGridView.size() / 8;
            } else {
                dotsCount = arrayList_sendGiftGridView.size() / 8 + 1;
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

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            };
        }
        myViewPagerAdapter = new GiftPagerAdapter(activity, dotsCount, arrayList_sendGiftGridView, mIsPrivateChat);
        if (mIsPrivateChat) myViewPagerAdapter.setBackgroudTransparent(false);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setOffscreenPageLimit(dotsCount);
    }

    private void setUiPageViewController() {
        dots = new ImageView[dotsCount];

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        params.height = 5;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(Utils.dpToPx(5), 0, 0, 0);

        for (int i = 0; i < dotsCount; i++) {

            dots[i] = new ImageView(activity);
            dots[i].setLayoutParams(lp);
            dots[i].setBackgroundResource(R.drawable.circle_dot_gift);
            dotsLayout.addView(dots[i]);
        }

        dots[0].setBackgroundResource(R.drawable.circle_dot_gift_red);
    }


    private void getListGift() {
        if (mGetGiftStoreUseCase == null) return;
        DialogManager.getInstance().showDialog(activity, activity.getResources().getString(R.string.connecting_msg));

        mCompositeSubscription.add(mGetGiftStoreUseCase.execute(null)
                .subscribe(giftStoreModel -> {
                    DialogManager.getInstance().dismisDialog();
                    if (giftStoreModel == null) return;
                    if (!giftStoreModel.getGiftItems().isEmpty()) {
                        totalGems = giftStoreModel.getTotalGem();
                        arrayList_sendGiftGridView.clear();
                        arrayList_sendGiftGridView.addAll(giftStoreModel.getGiftItems());
                        setViewPagerItemsWithAdapter();
                        setUiPageViewController();

                        if (AppsterApplication.mAppPreferences.isUserLogin()) {
                            AppsterApplication.mAppPreferences.getUserModel().setTotalBean(totalGems);
                            txt_total_gems.setText(String.valueOf(totalGems));
                        }
                    }
                }, error -> DialogManager.getInstance().dismisDialog()));
    }

    public void dimissDialog() {
        dismissEnable.set(true);
        closeGiftDialog();
    }

    public void resume() {
        if (dialogSendGift != null && dialogSendGift.isShowing()) {
            Timber.e("call update bean");
            updatePurchaseBean();
        }
    }

    private void updatePurchaseBean() {
        if (mGetGiftStoreUseCase == null) return;
        mCompositeSubscription.add(mGetGiftStoreUseCase.execute(null)
                .subscribe(giftStoreModel -> {
                    if (giftStoreModel == null) return;

                    totalGems = giftStoreModel.getTotalGem();
                    if (AppsterApplication.mAppPreferences.isUserLogin()) {
                        AppsterApplication.mAppPreferences.getUserModel().setTotalBean(totalGems);
                        Timber.e("total bean %d", totalGems);
                        if (txt_total_gems != null) {
                            Timber.e("total bean update %d", totalGems);
                            txt_total_gems.setText(String.valueOf(totalGems));
                        }

                    }

                }, error -> Timber.e(error.getMessage())));
    }

    public void handleErrorCode(int code, String message) {
        if (activity != null && code == 603) {
            new DialogbeLiveConfirmation.Builder()
                    .title(activity.getString(R.string.app_name))
                    .singleAction(true)
                    .message(message)
                    .onConfirmClicked(() -> AppsterApplication.logout(activity))
                    .build().show(activity);
//            utility.showMessage(getString(R.string.app_name), errorMessage, BaseActivity.this, mclick);
        }
    }

    private void sendGift(GiftItemModel item) {

        if (item == null) return;
//        DialogManager.getInstance().showDialog(activity, activity.getResources().getString(R.string.connecting_msg));
        if (btn_send != null) btn_send.setEnabled(false);
        dismissEnable.set(false);
        SendGiftRequestModel request = new SendGiftRequestModel();
        request.setReceiver_user_id(userID);
        request.setGift_id(item.getGiftId());

        if (item.getAmount() != 0) request.setUsingGiftInInventory(true);

        if (streamID > 0) {
            request.setStream_id(streamID);
        }


        mCompositeSubscription.add(AppsterWebServices.get().sendGift("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(sendGiftResponseModel -> {
//                    DialogManager.getInstance().dismisDialog();
                    if (btn_send != null) btn_send.setEnabled(true);
                    if (sendGiftResponseModel == null) return;

                    if (sendGiftResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        SendGiftResponseModel sendGiftResponse = sendGiftResponseModel.getData();
                        SendGiftResponseModel.SenderBean senderBean = sendGiftResponse.getSender();
                        SendGiftResponseModel.ReceiverBean receiverBean = sendGiftResponse.getReceiver();
                        UserModel appOwnerProfile = AppsterApplication.mAppPreferences.getUserModel();

                        appOwnerProfile.setTotalBean(senderBean.getTotalBean());
                        appOwnerProfile.setTotalGold(senderBean.getTotalGold());
                        appOwnerProfile.setTotalGoldFans(senderBean.getTotalGoldFans());
                        if (item.getAmount() > 0) {
                            item.updateAmount(senderBean.getAmount());
                            myViewPagerAdapter.notifySelectedAdapteItemChanged();
                        }
                        if (txt_total_gems != null)
                            txt_total_gems.setText(String.valueOf(appOwnerProfile.getTotalBean()));
                        if (completeSendGift != null) {
                            item.setGiftColor(sendGiftResponseModel.getData().gift.giftColor);
                            item.setGiftImage(sendGiftResponseModel.getData().gift.giftImage);
                            completeSendGift.onSendSuccess(item, senderBean.getTotalBean(),
                                    senderBean.getTotalGoldFans(),
                                    receiverBean.getTotalBean(),
                                    receiverBean.getTotalGoldFans(),
                                    sendGiftResponse.getVotingScores(),
                                    sendGiftResponse.topFanList,
                                    sendGiftResponse.dailyTopFans);
                        }
                        if (isShowAlertSendSucessFull) {
                            Toast.makeText(activity.getApplicationContext(), R.string.gift_has_been_sent_successfully, Toast.LENGTH_SHORT).show();
//                                utility.showMessage(activity.getString(R.string.app_name), activity.getString(R.string.gift_has_been_sent_successfully),
//                                        activity);
                        }
                        dismissEnable.set(true);
//                            else {
//                                if (dialogSendGift != null) {
//                                    dialogSendGift.dismiss();
//                                }
//                            }

                    } else {
                        ((BaseActivity) activity).handleError(sendGiftResponseModel.getMessage(), sendGiftResponseModel.getCode());
                        dismissEnable.set(true);
                    }
                }, error -> {
                    if (btn_send != null) btn_send.setEnabled(true);
                    dismissEnable.set(true);
//                    DialogManager.getInstance().dismisDialog();
                }));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mDialogDismisListener != null) mDialogDismisListener.onDiaLogDismissed();
        arrayList_sendGiftGridView.clear();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        ButterKnife.unbind(this);
    }


    public void setOnDismissListener(DiaLogDismissListener dismissListener) {
        this.mDialogDismisListener = dismissListener;
    }

}
