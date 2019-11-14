package com.appster.features.invite_friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.customview.CustomFontButton;
import com.appster.customview.CustomFontEditText;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.OnlyDrawableClickableTextView;
import com.appster.data.AppPreferences;
import com.appster.models.AppConfigModel;
import com.appster.models.UserModel;
import com.appster.utility.SocialManager;
import com.appster.utility.SpannableUtil;
import com.apster.common.BranchIoUtil;
import com.apster.common.CopyTextUtils;
import com.apster.common.UiUtils;
import com.apster.common.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.pack.utility.CheckNetwork;
import com.pack.utility.DialogInfoUtility;
import com.pack.utility.StringUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import timber.log.Timber;

import static com.appster.AppsterApplication.mAppPreferences;

/**
 * Created by User on 9/28/2015.
 */
public class InviteFriendActivity extends BaseToolBarActivity implements InviteFriendContract.InviteFriendView {

    public static String TAG = InviteFriendActivity.class.getSimpleName();
    private static final int REQUEST_CODE_FB_SHARE = 1;

    @Bind(R.id.txt_invitation_description)
    CustomFontTextView mTxtDescription;
    @Bind(R.id.txt_referral_id)
    CustomFontTextView mTxtReferralId;
    @Bind(R.id.txt_facebook)
    OnlyDrawableClickableTextView mTxtFacebook;
    @Bind(R.id.txt_whatsapp)
    OnlyDrawableClickableTextView mTxtWhatsapp;
    @Bind(R.id.txt_twitter)
    OnlyDrawableClickableTextView mTxtTwitter;
    @Bind(R.id.txt_email)
    OnlyDrawableClickableTextView mTxtEmail;
    @Bind(R.id.txt_others)
    OnlyDrawableClickableTextView mTxtOthers;
    @Bind(R.id.tv_bonus_referral)
    CustomFontTextView tvBonusReferral;

    @Bind(R.id.ll_input_referral_code)
    LinearLayout llInputReferralCode;
    @Bind(R.id.btn_enter_code)
    CustomFontButton btnEnterCode;
    @Bind(R.id.fm_enter_code)
    FrameLayout fmEnterCode;

    UserModel userInfoModel;
    String mSharableBranchIoUrl;
    CallbackManager fbCallbackManager;

    @Inject InviteFriendContract.UserActions mPresenter;
    @Bind(R.id.edt_referall_code)
    CustomFontEditText edtReferallCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        mPresenter.getAppConfigFromServer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_FB_SHARE:
                getFbCallbackManager().onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBarTile(getString(R.string.invite_bold));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
        goneNotify(true);
        handleTurnoffMenuSliding();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_invite_friend;
    }

    @Override
    public void init() {
        utility = new DialogInfoUtility();
        AppPreferences myAppPreferences = new AppPreferences(InviteFriendActivity.this);
        userInfoModel = myAppPreferences.getUserModel();
        mTxtReferralId.setText(userInfoModel.getReferralId());
        mTxtReferralId.setOnLongClickListener(v -> {
            CopyTextUtils.showOptionCopyText(this, v, userInfoModel.getReferralId(), null);
            return true;
        });
        enableReferralLayout();
        eventInputReferralCode();
        if (mBeLiveThemeHelper != null && mBeLiveThemeHelper.isTransparentStatusBarRequired()) {
            Window window = getWindow();
            if (window != null) window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    void eventInputReferralCode() {
        edtReferallCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtReferallCode.getText().toString().matches("^0")) {
                    // Not allowed
                    edtReferallCode.setText("");
                }
                edtReferallCode.setCursorVisible(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupRewardMessage(String rewardMsgInvitationHeader, String rewardMsgInvitationFooter) {
        String headerMessage;
        String footerMessage;
        if (TextUtils.isEmpty(rewardMsgInvitationHeader)) {
            headerMessage = getString(R.string.invite_friend_award);
        } else {
            headerMessage = rewardMsgInvitationHeader;
        }
        if (TextUtils.isEmpty(rewardMsgInvitationFooter)) {
            footerMessage = getString(R.string.invite_friend_input_your_friend_referral_and_get_gem);
        } else {
            footerMessage = rewardMsgInvitationFooter;
        }
        mTxtDescription.setText(SpannableUtil.replaceGemIcon(this, headerMessage));
        tvBonusReferral.setText(SpannableUtil.replaceGemIcon(this, footerMessage));
    }

    void enableReferralLayout() {

        if (!StringUtil.isNullOrEmptyString(mAppPreferences.getUserModel().getRefId())
                && !mAppPreferences.getUserModel().getRefId().equals("0")) {
            Timber.e("getRefId=========" + mAppPreferences.getUserModel().getRefId());
            edtReferallCode.setText(mAppPreferences.getUserModel().getRefId());
            edtReferallCode.setEnabled(false);
            btnEnterCode.setBackground(ContextCompat.getDrawable(this, R.drawable.invite_friend_grey_btn));
            btnEnterCode.setClickable(false);
        }
    }

    public void onClickBack(View view) {
        Utils.hideSoftKeyboard(InviteFriendActivity.this);
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        Utils.hideSoftKeyboard(InviteFriendActivity.this);
        super.onBackPressed();
    }

    public void getBranchIoUrl(BranchIoUtil.OnBranchIoCallback callback) {
        if (!CheckNetwork.isNetworkAvailable(InviteFriendActivity.this)) {
            utility.showMessage(getString(R.string.app_name),
                    getResources().getString(
                            R.string.no_internet_connection),
                    InviteFriendActivity.this);
            return;
        }

        if (!TextUtils.isEmpty(mSharableBranchIoUrl)) {
            callback.onComplete(mSharableBranchIoUrl);
            return;
        }

        BranchIoUtil.generateBranchIoUrl(this, userInfoModel.getUserImage(), userInfoModel.getReferralId(), callback);
    }

    //#region click event handler ==================================================================
    @OnClick(R.id.txt_facebook)
    void onFacebookShareClicked() {
        if (preventMultiClicks()) {
            return;
        }

        getBranchIoUrl(url -> {
            mSharableBranchIoUrl = url;
            SocialManager.getInstance().shareURLToFacebook(this, url,
                    REQUEST_CODE_FB_SHARE,
                    getFbCallbackManager(),
                    new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {
                        }

                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onError(FacebookException error) {
                        }
                    });
        });
    }

    @OnClick(R.id.txt_whatsapp)
    void onWhatsappShareClicked() {
        if (preventMultiClicks()) {
            return;
        }
        getBranchIoUrl(url -> {
            String content = String.format(getString(R.string.invite_sns_message), userInfoModel.getReferralId());
            SocialManager.getInstance().shareVideoToWhatsapp(this, content, url);

        });
    }

    @OnClick(R.id.txt_twitter)
    void onTwitterShareClicked() {
        if (preventMultiClicks()) {
            return;
        }
        getBranchIoUrl(url -> {
            String content = String.format(getString(R.string.invite_sns_message), userInfoModel.getReferralId());
            SocialManager.getInstance().ShareFeedQuotesToTwitter(this, content, url);

        });
    }

    @OnClick(R.id.txt_email)
    void onEmailShareClicked() {
        if (preventMultiClicks()) {
            return;
        }
        getBranchIoUrl(url -> {
            String content = String.format(getString(R.string.invite_sns_message), userInfoModel.getReferralId());
            String subject = getString(R.string.invite_mail_subject);
            SocialManager.getInstance().shareURLToEmail(this, content, subject, url);

        });
    }

    @OnClick(R.id.txt_others)
    void onOthersShareClicked() {
        if (preventMultiClicks()) {
            return;
        }
        getBranchIoUrl(url -> {
            String content = String.format(getString(R.string.invite_sns_message), userInfoModel.getReferralId()) + "\n" + url;
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, content);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        });
    }

    @OnClick(R.id.btn_enter_code)
    void updateRefId() {

//        TriviaRankingDialog topWinnerDialog = new TriviaRankingDialog();
//        topWinnerDialog.show(getSupportFragmentManager(), "123456789");

        String refId = edtReferallCode.getText().toString();
        if (!StringUtil.isNullOrEmptyString(refId)) {
            int referralInt = 0;
            try {
                referralInt = Integer.parseInt(refId);
            } catch (Exception e) {
                referralInt = 0;
            }
            if (referralInt == 0) {
                utility.showMessage(getString(R.string.app_name), getString(R.string.invite_friend_refID_invalid), this);
            } else {
                mPresenter.updateRefId(refId);
            }

        } else {
            utility.showMessage(getString(R.string.app_name), getString(R.string.invite_friend_please_input_referral_code), this);
        }
    }

    @OnClick(R.id.root_view)
    void hideKeyboard() {
        UiUtils.hideSoftKeyboard(this);
    }

    @OnClick(R.id.edt_referall_code)
    void setCursorVisible() {
        edtReferallCode.setCursorVisible(true);
    }

    @Override
    public void onUpdateLayoutCompleted() {
        enableReferralLayout();
    }

    @Override
    public void errorHasRefId(String error) {
        enableReferralLayout();
    }

    @Override
    public void onGetAppConfigSuccessfully(AppConfigModel appConfig) {
        setupRewardMessage(appConfig.rewardMsgInvitationHeader, appConfig.rewardMsgInvitationFooter);
    }

    @Override
    public void onGetAppConfigFailed() {
        setupRewardMessage("", "");
    }

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
        showDialog(this, getString(R.string.connecting_msg));
    }

    @Override
    public void hideProgress() {
        dismisDialog();
    }

    //#endregion click event handler ===============================================================
    CallbackManager getFbCallbackManager() {
        if (fbCallbackManager == null) {
            fbCallbackManager = CallbackManager.Factory.create();
        }
        return fbCallbackManager;
    }
}
