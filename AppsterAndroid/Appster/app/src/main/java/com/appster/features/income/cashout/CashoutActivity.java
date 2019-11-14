package com.appster.features.income.cashout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.core.util.PatternsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.domain.CashItemModel;
import com.appster.domain.WithdrawnAccountItemModel;
import com.appster.features.mvpbase.RecyclerItemCallBack;
import com.appster.utility.CustomTabUtils;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.Utils;
import com.pack.utility.StringUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.appster.R.id.switcher;
import static com.pack.utility.StringUtil.formatImage;

/**
 * Created by thanhbc on 2/13/17.
 */

public class CashoutActivity extends BaseToolBarActivity implements CashoutContract.CashoutView, RecyclerItemCallBack<CashItemModel> {

    public static Intent createIntent(Context context) {
        return new Intent(context, CashoutActivity.class);
    }

    public static final int NOT_ENOUGH_STARS = 0;
    public static final int ALL_FIELD_REQUIRED = 1;
    public static final int INVALID_EMAIL = 2;
    public static final int INVALID_PHONE = 3;

    @Bind(switcher)
    ViewSwitcher mViewSwitcher;

    @Bind(R.id.vsPaypal)
    ViewStub vsPaypal;

    @Bind(R.id.rcvWithdrawnRates)
    RecyclerView rcvWithdrawnRates;

    @Bind(R.id.tvUserCredit)
    TextView tvUserCredit;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.tvAvailableGems)
    TextView tvAvailableGems;
    CashoutAdapter mCashoutAdapter;


    CashoutContract.UserAction mPresenter;
    Bitmap starIcon;
    Typeface openSansemibold;
    Typeface openSanRegular;

    String mCashAmount;
    private PayPalScreenLayout mPaypalScreenLayout;

    private boolean mIsPaypalScreenShowing = false;

    private boolean isAbleToCashout = false;
    @Override
    public int getLayoutContentId() {
        return R.layout.cashout_activity;
    }

    @Override
    public void init() {
//        ButterKnife.bind(this);
        mPresenter = new CashoutPresenterImpl(this, AppsterWebServices.get());
        mCashoutAdapter = new CashoutAdapter(new ArrayList<>(), this, this);
        starIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_gift_currency);
        openSansemibold = Typeface.createFromAsset(getAssets(), "fonts/opensanssemibold.ttf");
        openSanRegular = Typeface.createFromAsset(getAssets(), "fonts/opensansregular.ttf");
        rcvWithdrawnRates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvWithdrawnRates.setAdapter(mCashoutAdapter);
        tvAvailableGems.setText(String.valueOf(AppsterApplication.mAppPreferences.getUserModel().getTotalBean()));


//
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvUserCredit.setText(String.valueOf(AppsterApplication.mAppPreferences.getUserModel().getTotalGold()));
        handleTurnoffMenuSliding();
        setTopBarTile(getString(R.string.income_start_to_gem));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(v -> onBackPressed());
        if(mPresenter!=null) {
            mPresenter.getPaymentRates();
        }
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void loadError(String errorMessage, int code) {
        DialogbeLiveConfirmation.Builder builder = getErrorDialogBuilder();
        switch (code) {
            case NOT_ENOUGH_STARS:
                builder.message(notEnoughStarsMessage());
                break;
            case INVALID_EMAIL:
                builder.message(invalidEmailMessage());
                break;
            case INVALID_PHONE:
                builder.message(invalidPhoneMessage());
                break;
            case ALL_FIELD_REQUIRED:
                builder.message(allFieldRequiredMessage());
                break;
            default:
                builder.message(errorMessage);
                break;
        }

        builder.build().show(this);
    }

    private CharSequence allFieldRequiredMessage() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(getString(R.string.all_fields_required));

        return builder;
    }

    private CharSequence invalidPhoneMessage() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(getString(R.string.enter_valid_phone_number));

        return builder;

    }

    private CharSequence invalidEmailMessage() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(getString(R.string.enter_valid_paypal_email));

        return builder;
    }

    private CharSequence notEnoughStarsMessage() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(getString(R.string.cashout_not_enough_stars));
//        builder.append(getString(R.string.not_enought_stars_pre));
//        builder.append(" ")
//                .append(formatImage(getViewContext(),"img",Bitmap.createScaledBitmap(starIcon, Utils.dpToPx(14),Utils.dpToPx(14),false)))
//                .append(" ")
//                .append(getString(R.string.not_enought_stars_post));

        return builder;
    }

    private DialogbeLiveConfirmation.Builder getErrorDialogBuilder() {
        return new DialogbeLiveConfirmation.Builder()
                .title(getString(R.string.exchange_oops))
                .singleAction(true);
    }


    @Override
    public void showProgress() {
        DialogManager.getInstance().showDialog(this, mIsPaypalScreenShowing ? getString(R.string.processing_msg) : getString(R.string.connecting_msg));
    }

    @Override
    public void hideProgress() {
        DialogManager.getInstance().dismisDialog();
    }

    @Override
    public void displayPaymentRates(ArrayList<CashItemModel> cashItemModels) {
        progressBar.setVisibility(View.GONE);
        rcvWithdrawnRates.setVisibility(View.VISIBLE);
        mCashoutAdapter.swapItems(cashItemModels);
    }

    @Override
    public void autoFillPaymentAccount(WithdrawnAccountItemModel accountItemModel) {
        mPaypalScreenLayout.fillData(accountItemModel.email, accountItemModel.firstName, accountItemModel.lastName, accountItemModel.mobile);
    }

    @Override
    public void cashoutAvailable(String starsAmount, String cashAmount,String paymentUrl) {
        if(!isAbleToCashout) {
            Toast.makeText(this, R.string.cashout_once_per_month,Toast.LENGTH_LONG).show();
            return;
        }
        mCashAmount = cashAmount;
        new DialogbeLiveConfirmation.Builder()
                .confirmText(getString(R.string.btn_text_confirm))
                .cancelText(getString(R.string.btn_text_cancel))
                .message(getContentMessage(starsAmount, cashAmount))
                .onConfirmClicked(() -> openGoogleCashoutForm(paymentUrl))
                .title(getString(R.string.exchange_confirm_title))
                .build().show(getViewContext());
    }

    @Override
    public void onWithdrawnSuccessfully(String amountMoney) {
        new DialogbeLiveConfirmation.Builder()
                .message(getWithdrawnCompletedMessage(amountMoney))
                .singleAction(true)
                .onConfirmClicked(this::onBackPressed)
                .title(getString(R.string.withdrawn_successfully))
                .build().show(getViewContext());
    }

    private CharSequence getWithdrawnCompletedMessage(String amountMoney) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(applyOpenSanRegular("You should receive", "#58585b"));

        builder.append(" ")
                .append(applyOpenSanSemibold(amountMoney, "#f05c56"))
                .append(" ")
                .append(applyOpenSanRegular("within 5 working days.", "#58585b"));

        return builder;
    }

    @Override
    public void displayUserCredit(String currentCredit) {
        tvUserCredit.setText(currentCredit);
    }

    @Override
    public void onCashoutAvailableResult(boolean isAbleToCashOut) {
        this.isAbleToCashout = isAbleToCashOut;
    }

    private void openPaypal() {

        if (vsPaypal.getParent() != null) {
            View paypalView = vsPaypal.inflate();
            mPaypalScreenLayout = new PayPalScreenLayout(paypalView);
        }
        mIsPaypalScreenShowing = true;
        mPresenter.getAccountLists();
        mViewSwitcher.setInAnimation(this, R.anim.slide_in_right);
        mViewSwitcher.setOutAnimation(this, R.anim.slide_out_left);
        mViewSwitcher.showNext();

    }


    void openGoogleCashoutForm(String paymentUrl) {
        if(TextUtils.isEmpty(paymentUrl)) return;
        CustomTabUtils.openChromeTab(this, paymentUrl);
    }

    @Override
    public void onBackPressed() {
        if (mIsPaypalScreenShowing) {
            mIsPaypalScreenShowing = false;
            mPaypalScreenLayout.clearInputFields();
            mPresenter.getPaymentRates();
            mViewSwitcher.setInAnimation(this, R.anim.slide_in_left);
            mViewSwitcher.setOutAnimation(this, R.anim.slide_out_right);
            mViewSwitcher.showPrevious();

        } else {
            super.onBackPressed();
        }

    }

    private CharSequence getContentMessage(String starsAmount, String cashAmount) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(applyOpenSanRegular(getString(R.string.cashout_exchange), "#58585b"));

        builder.append(" ")
                .append(formatImage(getViewContext(), "img", Bitmap.createScaledBitmap(starIcon, Utils.dpToPx(14), Utils.dpToPx(14), false)))
                .append(" ")
                .append(applyOpenSanSemibold(starsAmount, "#ffd460"));

        builder.append(" ")
                .append(applyOpenSanRegular(getString(R.string.exchange_confirm_to), "#58585b"))
                .append(" ")
                .append(applyOpenSanSemibold(cashAmount, "#000000"))
                .append("?");
        return builder;
    }

    SpannableStringBuilder applyOpenSanRegular(String content, String colorCode) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(content);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor(colorCode)), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new CustomTypefaceSpan("", openSanRegular), 0, builder.length(), 0);
        return builder;
    }

    SpannableStringBuilder applyOpenSanSemibold(String content, String colorCode) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(content);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor(colorCode)), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new CustomTypefaceSpan("", openSansemibold), 0, builder.length(), 0);
        return builder;
    }

    @Override
    public void onItemClicked(CashItemModel item, int position) {
        mPresenter.cashout(item);
    }

    class PayPalScreenLayout {
        @Bind(R.id.etFirstName)
        EditText etFirstName;
        @Bind(R.id.etLastName)
        EditText etLastName;
        @Bind(R.id.etPayPalEmail)
        EditText etPayPalEmail;
        @Bind(R.id.etMobileNumber)
        EditText etMobileNumber;

        PayPalScreenLayout(View view) {
            ButterKnife.bind(this, view);
            trackingText();
        }

        @OnTextChanged(value = R.id.etFirstName,
                callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
        void onFirstNameAfterTextChange(Editable editable) {
            if (StringUtil.isNullOrEmptyString(String.valueOf(editable))) {
                setNewTrackingText(etFirstName);
            } else {
                setDefaultTrackingText(etFirstName);
            }
        }

        @OnTextChanged(value = R.id.etLastName,
                callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
        void onLastNameAfterTextChange(Editable editable) {
            if (StringUtil.isNullOrEmptyString(String.valueOf(editable))) {
                setNewTrackingText(etLastName);
            } else {
                setDefaultTrackingText(etLastName);
            }
        }

        @OnTextChanged(value = R.id.etPayPalEmail,
                callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
        void onPayPalAfterTextChange(Editable editable) {
            if (StringUtil.isNullOrEmptyString(String.valueOf(editable))) {
                setNewTrackingText(etPayPalEmail);
            } else {
                setDefaultTrackingText(etPayPalEmail);
            }
        }

        @OnTextChanged(value = R.id.etMobileNumber,
                callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
        void onMobileAfterTextChange(Editable editable) {
            if (StringUtil.isNullOrEmptyString(String.valueOf(editable))) {
                setNewTrackingText(etMobileNumber);
            } else {
                setDefaultTrackingText(etMobileNumber);
            }
        }


        private void trackingText() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                etFirstName.setLetterSpacing(0.15f);
                etLastName.setLetterSpacing(0.15f);
                etPayPalEmail.setLetterSpacing(0.15f);
                etMobileNumber.setLetterSpacing(0.15f);

                if (StringUtil.isNullOrEmptyString(String.valueOf(etPayPalEmail.getText()))) {
                    etPayPalEmail.setLetterSpacing(0.15f);
                }
            }
        }

        private void setDefaultTrackingText(EditText editText) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                editText.setLetterSpacing(0.00f);
            }
        }

        private void setNewTrackingText(EditText editText) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                editText.setLetterSpacing(0.15f);
            }
        }

        void clearInputFields() {
            etMobileNumber.setText(null);
            etFirstName.setText(null);
            etLastName.setText(null);
            etPayPalEmail.setText(null);
        }


        @OnClick(R.id.btnSubmit)
        void submit() {
            int errorCode = checkWithdrawCondition();
            if (errorCode == -1) {
                cashoutConfirmation(mCashAmount, String.valueOf(etPayPalEmail.getText()));
            } else {
                loadError("", errorCode);
            }
        }

        void cashoutConfirmation(String amountMoney, String email) {
            new DialogbeLiveConfirmation.Builder()
                    .confirmText(getString(R.string.btn_text_confirm))
                    .cancelText(getString(R.string.btn_text_cancel))
                    .message(getConfirmCashMessage(amountMoney, email))
                    .onConfirmClicked(this::withdrawn)
                    .title(getString(R.string.confirm_cashout))
                    .build().show(getViewContext());
        }

        void withdrawn() {
            mPresenter.withdrawn(String.valueOf(etPayPalEmail.getText()), String.valueOf(etFirstName.getText()), String.valueOf(etLastName.getText()), String.valueOf(etMobileNumber.getText()));
        }

        private CharSequence getConfirmCashMessage(String amountMoney, String email) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(applyOpenSanRegular("Transfer", "#58585b"));

            builder.append(" ")
                    .append(applyOpenSanSemibold(amountMoney, "#f05c56"));

            builder.append(" ")
                    .append(applyOpenSanRegular(getString(R.string.exchange_confirm_to), "#58585b"))
                    .append(" ")
                    .append(applyOpenSanSemibold(email, "#f05c56"))
                    .append("?");
            return builder;
        }

        @OnClick(R.id.btnCreatePayPalAccount)
        void createPayPalAccount() {
            Intent intentViewWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_PAYPAL));
            startActivity(intentViewWeb);
        }

        private int checkWithdrawCondition() {
            boolean emailBlank = TextUtils.isEmpty(String.valueOf(etPayPalEmail.getText()));
            boolean firstNameBlank = TextUtils.isEmpty(String.valueOf(etFirstName.getText()));
            boolean lastNameBlank = TextUtils.isEmpty(String.valueOf(etLastName.getText()));
            boolean mobileBlank = TextUtils.isEmpty(String.valueOf(etMobileNumber.getText()));

            if (emailBlank || firstNameBlank || lastNameBlank || mobileBlank) {
                return ALL_FIELD_REQUIRED;
            }

            boolean validEmail = PatternsCompat.EMAIL_ADDRESS.matcher(String.valueOf(etPayPalEmail.getText())).matches();
            if (!validEmail) {
                return INVALID_EMAIL;
            }

            boolean validPhone = etMobileNumber.length() >= 8;
            if (!validPhone) {
                return INVALID_PHONE;
            }


            return -1;
        }

        void fillData(String email, String firstName, String lastName, String mobile) {
            etMobileNumber.setText(mobile);
            etFirstName.setText(firstName);
            etLastName.setText(lastName);
            etPayPalEmail.setText(email);
        }
    }

}
