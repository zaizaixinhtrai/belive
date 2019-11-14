package com.appster.features.regist;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityOptionsCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.customview.CircleImageView;
import com.appster.customview.CustomFontEditText;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.CustomTypefaceSpan;
import com.appster.features.friend_suggestion.FriendSuggestionActivity;
import com.appster.main.MainActivity;
import com.appster.manager.AppsterChatManger;
import com.appster.models.UserModel;
import com.appster.tracking.EventTracker;
import com.appster.tracking.EventTrackingName;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.DeviceInfo;
import com.appster.utility.SocialManager;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.RegisterWithFacebookRequestModel;
import com.appster.webservice.request_models.RegisterWithGoogleRequestModel;
import com.appster.webservice.request_models.RegisterWithInstagramRequestModel;
import com.appster.webservice.request_models.RegisterWithPhoneNumberRequestModel;
import com.appster.webservice.request_models.RegisterWithTwitterRequestModel;
import com.appster.webservice.request_models.RegisterWithWeChatRequestModel;
import com.appster.webservice.request_models.RegisterWithWeiboRequestModel;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.DialogbeLiveConfirmation;
import com.apster.common.DownloadFile;
import com.apster.common.Utils;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.pack.utility.CheckNetwork;
import com.pack.utility.EmailUtil;
import com.pack.utility.StringUtil;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.apster.common.FileUtility.MEDIA_TYPE_IMAGE_CROPPED;

/**
 * Created by ThanhBan on 11/14/2016.
 */

public class RegisterActivity extends BaseActivity implements RegisterContract.RegisterView {

    public static final String EXPECTED_USER_ID = "EXPECTED_USER_ID";
    private static final String ARG_COUNTRY_CODE = "ARG_COUNTRY_CODE";
    private static final String ARG_PHONE_NUMBER = "ARG_PHONE_NUMBER";
    private static final String ARG_PASSWORD = "ARG_PASSWORD";

    public static Intent createIntent(Context context, String loginFrom, String countryCode, String phoneNumber, String password){
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_FROM, loginFrom);
        intent.putExtra(ConstantBundleKey.BUNDLE_TYPE_KEY, Constants.LoginType.SOCIAL_TYPE);
        intent.putExtra(ARG_COUNTRY_CODE, countryCode);
        intent.putExtra(ARG_PHONE_NUMBER, phoneNumber);
        intent.putExtra(ARG_PASSWORD, password);
        return intent;
    }

    public static Intent createIntent(Context context, Constants.LoginType loginType, String displayName, String userIdSuggestion) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra(ConstantBundleKey.BUNDLE_TYPE_KEY, loginType);
        intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_DISPLAY_NAME, displayName);
        intent.putExtra(EXPECTED_USER_ID, userIdSuggestion);
        return intent;
    }

    private final int MESSAGE_REGISTERED_SUCCESSFULLY = 999999;

    @Bind(R.id.root_view)
    LinearLayout rootView;
    @Bind(R.id.ivUserImage)
    CircleImageView ivUserImage;
    @Bind(R.id.btnCreateProfile)
    AppCompatButton btnCreateProfile;
    @Bind(R.id.edtUserId)
    CustomFontEditText edtUserId;
    @Bind(R.id.tvPageTitle)
    TextView tvPageTitle;
    @Bind(R.id.txt_invalid_user_id_warning)
    CustomFontTextView mTxtInvalidUserIdWarning;


    private String email = "";
    private String picture = "";
    private Bitmap userAvatar;
    private Constants.LoginType type;
    private String userID = "";
    private String mExpectedUserId;
    /**
     * the id gotten from google, facebook, instagram, twitter
     */
    private String loginId = "";
    private String displayName = "";
    private String gender = Constants.GENDER_MALE;
    private String mCountryCode;
    private String mPhoneNumber;
    private String mPassWord;

    private String mReferralId;
    private Handler mRegisterHandler;
    private final long DELAY = 500; // in ms
    /**
     * one of
     * {@link ConstantBundleKey LOGIN_FROM#ARG_LOGIN_FACEBOOK}
     * {@link ConstantBundleKey LOGIN_FROM#ARG_LOGIN_GOOGLE}
     * {@link ConstantBundleKey LOGIN_FROM#ARG_LOGIN_INSTAGRAM}
     * {@link ConstantBundleKey LOGIN_FROM#ARG_LOGIN_TWITTER}
     * {@link ConstantBundleKey LOGIN_FROM#ARG_LOGIN_PHONE}
     */
    private String loginFrom;

    RegisterContract.UserActions mRegisterPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_info);
        ButterKnife.bind(this);
        if(mBeLiveThemeHelper!=null && mBeLiveThemeHelper.isTransparentStatusBarRequired()) {
            Window window = getWindow();
            if (window != null) window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        init();
        setupHandler();
        extractBundle();
        setEdtUserIdWatcher();
        setTouchEvent();
        trackingText();

        tvPageTitle.setText(makeSectionOfTextBold(getString(R.string.register_create_your_profile), getString(R.string.register_your_bold)));
        btnCreateProfile.setEnabled(false);

        if (picture != null && !picture.isEmpty()) {
            new downloadImageFB().execute();
        }

        if (ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_PHONE.equals(loginFrom)){
            mRegisterPresenter.getUserIdSuggestion();
        }else {
            mRegisterPresenter.getUserIdSuggestion(mExpectedUserId);
        }
    }

    private void init(){
        mReferralId = AppsterApplication.mAppPreferences.getReferralId();
        btnCreateProfile.setBackgroundColor(Color.parseColor("#D8D8D8"));
        edtUserId.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        mRegisterPresenter = new RegisterPresenter(this, AppsterWebServices.get());
    }

    private void setEdtUserIdWatcher() {
        mCompositeSubscription.add(RxTextView.textChangeEvents(edtUserId)
                .skip(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(textViewTextChangeEvent -> onUserIdChanged(textViewTextChangeEvent.text().toString()))
                .observeOn(Schedulers.computation())
                .debounce(800, TimeUnit.MILLISECONDS) // default Scheduler is Computation
                .filter(textViewTextChangeEvent -> isValidIdName(textViewTextChangeEvent.text().toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textViewTextChangeEvent -> verifyUserIdFromRemoteServer(textViewTextChangeEvent.text().toString()), Timber::e));
    }

    private void onUserIdChanged(String newUserId){
        if (StringUtil.isNullOrEmptyString(newUserId)) {
            setNewTrackingText(edtUserId);
        } else {
            setDefaultTrackingText(edtUserId);

        }

        mTxtInvalidUserIdWarning.setVisibility(View.INVISIBLE);
        edtUserId.replaceCustomDrawableEnd(0);
        btnCreateProfile.setBackgroundResource(R.color.color_d8d8d8);
        enableBeginButton(isValidIdName(newUserId));
    }

    private boolean isValidIdName(String idName){
        return !TextUtils.isEmpty(idName) && idName.length() >= Constants.MIN_LENGTH_INPUT_USER_ID
                && idName.length() <= Constants.MAX_LENGTH_INPUT_USER_ID;
    }

    private void setTouchEvent() {

        rootView.setOnTouchListener((v, event) -> {
            Utils.hideSoftKeyboard(RegisterActivity.this);
            return false;
        });
        edtUserId.setOnTouchListener((v, event) -> {
            if(event.getAction()== MotionEvent.ACTION_UP) {
                EventTracker.trackEvent(EventTrackingName.EVENT_INPUT_USERNAME);
            }
            return false;
        });
    }

    private void trackingText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tvPageTitle.setLetterSpacing(0.15f);
            edtUserId.setLetterSpacing(0.15f);
//            edtUserReferralCode.setLetterSpacing(0.15f);
            btnCreateProfile.setLetterSpacing(0.15f);

//            if (StringUtil.isNullOrEmptyString(email)) {
//                edtUserEmail.setLetterSpacing(0.15f);
//            }

//            if (StringUtil.isNullOrEmptyString(displayName)) {
//                edtUserDisplayName.setLetterSpacing(0.15f);
//            }
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


    private SpannableStringBuilder makeSectionOfTextBold(String text, String... textToBold) {

        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        for (String textItem :
                textToBold) {
            if (textItem.length() > 0 && !textItem.trim().equals("")) {
                //for counting start/end indexes
                int startingIndex = text.indexOf(textItem);
                int endingIndex = startingIndex + textItem.length();
                Typeface font_semibold = Typeface.createFromAsset(getAssets(), "fonts/opensanssemibold.ttf");
                Typeface font_extrabold = Typeface.createFromAsset(getAssets(), "fonts/opensansextrabold.ttf");
                builder.setSpan(new CustomTypefaceSpan("", font_semibold), 0, startingIndex, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                builder.setSpan(new CustomTypefaceSpan("", font_extrabold), startingIndex, endingIndex, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),startingIndex,endingIndex, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                builder.setSpan(new CustomTypefaceSpan("", font_semibold), endingIndex, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }

        return builder;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void userIdAvailable() {
        edtUserId.replaceCustomDrawableEnd(R.drawable.create_profile_reg_tick);
        mTxtInvalidUserIdWarning.setVisibility(View.INVISIBLE);
        enableBeginButton(true);
    }

    @Override
    public void userIdInAvailable() {
        edtUserId.replaceCustomDrawableEnd(R.drawable.ic_id_name_invalid);
        mTxtInvalidUserIdWarning.setVisibility(View.VISIBLE);
        enableBeginButton(false);
    }

    @Override
    public void onGetUserIdSuggestionSuccessfully(String suggestedUserId) {
        if (TextUtils.isEmpty(edtUserId.getText())){
            edtUserId.setText(suggestedUserId);
            edtUserId.setSelection(suggestedUserId.length());
        }
    }

    @Override
    public void enableBeginButton(boolean enabled) {
        btnCreateProfile.setEnabled(enabled);
        if (enabled) {
            btnCreateProfile.setBackgroundResource(R.drawable.selector_red_button_no_radius);
        } else {
            btnCreateProfile.setBackgroundResource(R.color.color_d8d8d8);
        }
    }

    @Override
    public void onUserRegisterCompleted(UserModel userInforModel, String userToken) {
        // Get Data
        if (userInforModel != null) {

            // Track register success
            EventTracker.trackRegisterSuccess();
            // Save data
            Thread t = new Thread(() -> {

                AppsterApplication.mAppPreferences.saveUserInforModel(userInforModel);
                AppsterApplication.mAppPreferences.saveUserToken(userToken);
                AppsterApplication.mAppPreferences.setLoginFacebook(true);

                AppsterChatManger.getInstance(this).reconnectIfNeed();

                mRegisterHandler.sendEmptyMessage(MESSAGE_REGISTERED_SUCCESSFULLY);

            });
            t.start();

        } else {
            dismisDialog();
            SocialManager.getInstance().logOut();
        }
    }

    @Override
    public void onAdminBlocked(String message) {
        DialogbeLiveConfirmation.Builder builder = new DialogbeLiveConfirmation.Builder();
        builder.title(getString(R.string.app_name))
                .message(message)
                .confirmText(getString(R.string.btn_text_ok))
                .singleAction(true)
                .build().show(this);
    }

    @Override
    public Context getViewContext() {
        return this;
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

    private class downloadImageFB extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return DownloadFile.downloadImageFromURL(picture);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                userAvatar = bitmap;
                ivUserImage.setImageBitmap(userAvatar);
            }
        }
    }

    @Override
    protected void onDestroy() {

        mRegisterPresenter.detachView();
        SocialManager.cancelInstance();
        super.onDestroy();
    }

    private void extractBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            type = (Constants.LoginType) intent.getSerializableExtra(ConstantBundleKey.BUNDLE_TYPE_KEY);

            if (type == null) {
                return;
            }
            Bundle bundle = getIntent().getExtras();
            if (type == Constants.LoginType.SOCIAL_TYPE) {
                email = bundle.getString(ConstantBundleKey.BUNDLE_LOGIN_EMAIL);
                picture = bundle.getString(ConstantBundleKey.BUNDLE_LOGIN_PROFILE_PIC);
                loginId = bundle.getString(ConstantBundleKey.BUNDLE_LOGIN_ID);
                displayName = bundle.getString(ConstantBundleKey.BUNDLE_LOGIN_DISPLAY_NAME);
                gender = bundle.getString(ConstantBundleKey.BUNDLE_LOGIN_GENDER);
                loginFrom = bundle.getString(ConstantBundleKey.BUNDLE_LOGIN_FROM);
                mExpectedUserId = bundle.getString(EXPECTED_USER_ID, "");
                mCountryCode = bundle.getString(ARG_COUNTRY_CODE);
                mPhoneNumber = bundle.getString(ARG_PHONE_NUMBER);
                mPassWord = bundle.getString(ARG_PASSWORD);

            } else if (type == Constants.LoginType.PLAYTOKEN_TYPE) {
                email = bundle.getString(ConstantBundleKey.BUNDLE_LOGIN_EMAIL);
                displayName = bundle.getString(ConstantBundleKey.BUNDLE_LOGIN_DISPLAY_NAME);
            }
        }
    }

    private void verifyUserIdFromRemoteServer(String userId) {
        mRegisterPresenter.checkUserIdAvailable(userId);
    }

    private void setupHandler() {

//        btnNext.setOnClickListener(this);

        mRegisterHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.MESSAGE_SHOW_DIALOG_PROGRESS:
                        showDialog(getBaseContext(), getString(R.string.connecting_msg));
                        break;
                    case Constants.MESSAGE_GET_CONFIG_SERVER_LIVE_STREAM_SUCCESS:
                        dismisDialog();
                        goingOnBroarding();
                        break;
                    case Constants.MESSAGE_GET_CONFIG_SERVER_LIVE_STREAM_ERROR:
                        dismisDialog();
                        Toast.makeText(getApplicationContext(), getString(R.string.app_fail_get_config_amazon_live_Stream), Toast.LENGTH_LONG).show();
                        goingOnBroarding();
                        break;
                    case Constants.MESSAGE_LOGIN_LIVE_STREAM_SUCCESS:
                        break;
                    case Constants.MESSAGE_LOGIN_LIVE_STREAM_ERROR:
                        dismisDialog();
                        Toast.makeText(getApplicationContext(), getString(R.string.app_fail_login_live_Stream), Toast.LENGTH_LONG).show();
                        goingOnBroarding();
                        break;

                    case MESSAGE_REGISTERED_SUCCESSFULLY:
                        AppsterWebServices.resetAppsterWebserviceAPI();
                        dismisDialog();
                        goToFriendSuggestionScreen();
//                        goingOnBroarding();
                        break;
                }

                super.handleMessage(msg);
            }
        };

    }


    @OnClick(R.id.btnCreateProfile)
    public void createProfile() {
        if (!CheckNetwork.isNetworkAvailable(this)) {
            utility.showMessage("", getString(R.string.no_internet_connection), this);
            return;
        }

        userID = edtUserId.getText().toString();

        if (StringUtil.isNullOrEmptyString(userID)) {
            edtUserId.setError(getString(R.string.user_id_required));
            edtUserId.requestFocus();
            return;
        }

        if (userID.length() < Constants.MIN_LENGTH_INPUT_USER_ID || userID.length() > Constants.MAX_LENGTH_INPUT_USER_ID) {
            edtUserId.setError(getString(R.string.characters_range, String.valueOf(Constants.MIN_LENGTH_INPUT_USER_ID), String.valueOf(Constants.MAX_LENGTH_INPUT_USER_ID)));
            edtUserId.requestFocus();
            return;
        }

        if (type == Constants.LoginType.SOCIAL_TYPE) {
            try {
                if (ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_FACEBOOK.equals(loginFrom)) {
                    registerWithFacebook();
                } else if (ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_GOOGLE.equals(loginFrom)) {
                    registerWithGoogle();
                } else if (ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_INSTAGRAM.equals(loginFrom)) {
                    registerWithInstagram();
                } else if (ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_TWITTER.equals(loginFrom)) {
                    registerWithTwitter();
                } else if (ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_WECHAT.equals(loginFrom)) {
                    registerWithWeChat();
                } else if (ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_WEIBO.equals(loginFrom)) {
                    registerWithWeibo();
                }else if(ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_PHONE.equals(loginFrom)){
                    registerWithPhoneNumber();

                }
            } catch (PackageManager.NameNotFoundException e) {
                Timber.e(e);
            }
        }

    }

    @Override
    public void onBackPressed() {
//        SocialManager.logoutGoogle(this, null);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        Uri imageCroppedURI;
        try {
            imageCroppedURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE_CROPPED);
        } catch (NullPointerException e) {
            Timber.d(e);
            return;
        }

        switch (requestCode) {

            case Constants.REQUEST_PIC_FROM_LIBRARY:

                fileUri = data.getData();

                if (fileUri != null) {
                    performCrop(fileUri, imageCroppedURI);
                }

                break;
            case Constants.REQUEST_PIC_FROM_CAMERA:

                fileUri = data.getData();
                if (fileUri != null) {
//                    performCrop(fileUri, imageCroppedURI);
                    handleImageProfile(fileUri);
                }

                break;

            case Constants.REQUEST_PIC_FROM_CROP:

                if (imageCroppedURI != null) {
                    handleImageProfile(imageCroppedURI);
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void handleImageProfile(Uri imageURI) {
        userAvatar = Utils.getBitmapFromURi(this, imageURI);
        ivUserImage.setImageBitmap(userAvatar);
    }


    public void goingOnBroarding() {

        Utils.hideSoftKeyboard(this);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ConstantBundleKey.BUNDLE_TEXT_FOR_NEWLY_USER, true);
        startActivity(intent, options.toBundle());

//        Intent navigationIntent = OnBoardingActivity.createIntent(this, false, loginFrom);
//        WelcomeHelper welcomeHelper = new WelcomeHelper(this, OnBoardingActivity.class, navigationIntent);
//        welcomeHelper.forceShowAndCloseActivity();

        clearImagePhoto();
        AppsterApplication.mAppPreferences.setReferralId("");
        finish();
    }

    private void goToFriendSuggestionScreen() {
        Intent intent = FriendSuggestionActivity.createIntent(this, loginFrom);
        startActivity(intent);
    }

    private void clearImagePhoto() {
        if (userAvatar != null && !userAvatar.isRecycled()) {
            userAvatar.recycle();
            userAvatar = null;
        }
    }

    private void registerWithTwitter() throws PackageManager.NameNotFoundException {
        EmailUtil checkEmail = new EmailUtil();
        if (!checkEmail.isEmail(email)) {
            email = "";
        }


        RegisterWithTwitterRequestModel request = new RegisterWithTwitterRequestModel(userID,
                displayName,
                loginId,
                Constants.ANDROID_DEVICE_TYPE,
                AppsterApplication.mAppPreferences.getDevicesUDID(), AppsterApplication.mAppPreferences.getDevicesToken(),
                0, 0, "",
                email,
                Utils.getFileFromBitMap(this, userAvatar),
                mReferralId,
                gender,
                AppsterApplication.getDeviceName(),
                String.valueOf(Build.VERSION.RELEASE),
                AppsterApplication.getCurrentVersionName(this));
        mRegisterPresenter.register(request);

    }

    private void registerWithFacebook() throws PackageManager.NameNotFoundException {

        EmailUtil checkEmail = new EmailUtil();
        if (!checkEmail.isEmail(email)) {
            email = "";
        }

        RegisterWithFacebookRequestModel request = new RegisterWithFacebookRequestModel(userID,
                displayName,
                loginId, Constants.ANDROID_DEVICE_TYPE, AppsterApplication.mAppPreferences.getDevicesUDID(), AppsterApplication.mAppPreferences.getDevicesToken(),
                0, 0, "", email, Utils.getFileFromBitMap(this, userAvatar),
                mReferralId, gender, AppsterApplication.getDeviceName(),
                String.valueOf(Build.VERSION.RELEASE),
                AppsterApplication.getCurrentVersionName(this));

        mRegisterPresenter.register(request);
    }

    private void registerWithGoogle() throws PackageManager.NameNotFoundException {

        EmailUtil checkEmail = new EmailUtil();
        if (!checkEmail.isEmail(email)) {
            email = "";
        }

        RegisterWithGoogleRequestModel request = new RegisterWithGoogleRequestModel(userID,
                displayName,
                loginId,
                Constants.ANDROID_DEVICE_TYPE,
                AppsterApplication.mAppPreferences.getDevicesUDID(), AppsterApplication.mAppPreferences.getDevicesToken(),
                0, 0, "",
                email,
                Utils.getFileFromBitMap(this, userAvatar),
                mReferralId,
                gender,
                AppsterApplication.getDeviceName(),
                String.valueOf(Build.VERSION.RELEASE),
                AppsterApplication.getCurrentVersionName(this));

        mRegisterPresenter.register(request);

    }

    private void registerWithInstagram() throws PackageManager.NameNotFoundException {
        EmailUtil checkEmail = new EmailUtil();
        if (!checkEmail.isEmail(email)) {
            email = "";
        }

        RegisterWithInstagramRequestModel request = new RegisterWithInstagramRequestModel(userID,
                displayName,
                loginId,
                Constants.ANDROID_DEVICE_TYPE,
                AppsterApplication.mAppPreferences.getDevicesUDID(), AppsterApplication.mAppPreferences.getDevicesToken(),
                0, 0, "",
                email,
                Utils.getFileFromBitMap(this, userAvatar),
                mReferralId,
                gender,
                AppsterApplication.getDeviceName(),
                String.valueOf(Build.VERSION.RELEASE),
                AppsterApplication.getCurrentVersionName(this));

        mRegisterPresenter.register(request);
    }

    private void registerWithWeChat() throws PackageManager.NameNotFoundException {
        EmailUtil checkEmail = new EmailUtil();
        if (!checkEmail.isEmail(email)) {
            email = "";
        }

        RegisterWithWeChatRequestModel request = new RegisterWithWeChatRequestModel(userID,
                displayName,
                loginId,
                Constants.ANDROID_DEVICE_TYPE,
                AppsterApplication.mAppPreferences.getDevicesUDID(), AppsterApplication.mAppPreferences.getDevicesToken(),
                0, 0, "",
                email,
                Utils.getFileFromBitMap(this, userAvatar),
                mReferralId,
                gender,
                AppsterApplication.getDeviceName(),
                String.valueOf(Build.VERSION.RELEASE),
                AppsterApplication.getCurrentVersionName(this));

        mRegisterPresenter.register(request);
    }

    private void registerWithPhoneNumber() throws PackageManager.NameNotFoundException {
        String deviceUUID = DeviceInfo.getDeviceDetail(this);
        AppsterApplication.mAppPreferences.setDevicesUDID(deviceUUID);
        RegisterWithPhoneNumberRequestModel model = new RegisterWithPhoneNumberRequestModel(
                userID,
                displayName,
                Constants.ANDROID_DEVICE_TYPE,
                deviceUUID,
                AppsterApplication.mAppPreferences.getDevicesToken(),
                0, 0, "",
                email,
                Utils.getFileFromBitMap(this, userAvatar),
                mReferralId,
                gender,
                AppsterApplication.getDeviceName(),
                String.valueOf(Build.VERSION.RELEASE),
                AppsterApplication.getCurrentVersionName(this)
                , mCountryCode,
                mPhoneNumber,
                mPassWord);
        mRegisterPresenter.register(model);
    }

    private void registerWithWeibo() throws PackageManager.NameNotFoundException {
        EmailUtil checkEmail = new EmailUtil();
        if (!checkEmail.isEmail(email)) {
            email = "";
        }

        RegisterWithWeiboRequestModel request = new RegisterWithWeiboRequestModel(userID,
                displayName,
                loginId,
                Constants.ANDROID_DEVICE_TYPE,
                AppsterApplication.mAppPreferences.getDevicesUDID(), AppsterApplication.mAppPreferences.getDevicesToken(),
                0, 0, "",
                email,
                Utils.getFileFromBitMap(this, userAvatar),
                mReferralId,
                gender,
                AppsterApplication.getDeviceName(),
                String.valueOf(Build.VERSION.RELEASE),
                AppsterApplication.getCurrentVersionName(this));

        mRegisterPresenter.register(request);
    }


    @OnClick(R.id.ivUserImage)
    public void onClickChooseImage(View view) {

        showPicPopUp();
    }

}
