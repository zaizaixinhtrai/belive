package com.appster.features.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import butterknife.OnClick
import com.appster.AppsterApplication
import com.appster.R
import com.appster.activity.BaseActivity
import com.appster.customview.ScalableVideo.ScalableType
import com.appster.features.login.phoneLogin.phoneSignInSignUp.PhoneSignInSignUpActivity
import com.appster.features.maintenance.MaintenanceActivity
import com.appster.features.regist.RegisterActivity
import com.appster.main.MainActivity
import com.appster.manager.ShowErrorManager
import com.appster.services.GCMIntentServices
import com.appster.tracking.EventTracker
import com.appster.tracking.EventTrackingName
import com.appster.utility.AppsterUtility
import com.appster.utility.ConstantBundleKey
import com.appster.utility.SocialManager
import com.appster.webservice.AppsterWebServices
import com.appster.webservice.request_models.*
import com.appster.webservice.response.BaseResponse
import com.appster.webservice.response.LoginResponseModel
import com.appster.webservice.response.MaintenanceModel
import com.appster.webview.ActivityViewWeb
import com.apster.common.Constants
import com.apster.common.CountryCode
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.pack.utility.CheckNetwork
import com.pack.utility.SetDateTime
import com.pack.utility.StringUtil
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login_new.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Created by User on 1/20/2016.
 */
class LoginActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener, LoginContract.LoginView {
    private val context: Activity by lazy { this }
    private var active = false
    private val mLoginHandler: Handler by lazy { LoginHandler(this) }


    @Inject
    lateinit var loginPresenter: LoginContract.LoginActions

    private val mTwitterAuthClient: TwitterAuthClient by lazy { TwitterAuthClient() }
//    private val mWelcomeHelper: WelcomeHelper? = null

    private var isPause: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login_new)

        if (AppsterApplication.mAppPreferences.userCountryCode != CountryCode.CHINA) {

            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                val intent = Intent(this, GCMIntentServices::class.java)
                startService(intent)
            }
        }
//        ButterKnife.bind(this)
        setText()
        calcKeyboardHeight()
        btn_facebook_login.setOnClickListener { onFacebookButtonClicked() }
        btn_twitter_login.setOnClickListener { loginTwitter() }
        btn_instagram_login.setOnClickListener { onInstagramClick() }
        btn_google_login.setOnClickListener { onGoogleButtonClicked() }
        btn_phone_login.setOnClickListener { onPhoneLoginButonClicked() }

        //        playVideo();
    }

    internal fun handleMessage(msg: Message) {
        when (msg.what) {
            Constants.MESSAGE_SHOW_DIALOG_PROGRESS -> showDialog(context.applicationContext, getString(R.string.connecting_msg))
            MESSAGE_GET_START_APP, Constants.MESSAGE_GET_CONFIG_SERVER_LIVE_STREAM_SUCCESS -> {
                dismisDialog()
                //                        navigateScreen();
                loginPresenter.checkMaintenance()
            }
            Constants.MESSAGE_GET_CONFIG_SERVER_LIVE_STREAM_ERROR -> {
                dismisDialog()
                Toast.makeText(context.applicationContext, getString(R.string.app_fail_get_config_amazon_live_Stream), Toast.LENGTH_LONG).show()
                //                        navigateScreen();
                loginPresenter.checkMaintenance()
            }
            Constants.MESSAGE_LOGIN_LIVE_STREAM_SUCCESS -> {
            }
            Constants.MESSAGE_LOGIN_LIVE_STREAM_ERROR -> {
                dismisDialog()
                Toast.makeText(context.applicationContext, getString(R.string.app_fail_login_live_Stream), Toast.LENGTH_LONG).show()
                //                        navigateScreen();
                loginPresenter.checkMaintenance()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        active = true
    }

    override fun onResume() {
        super.onResume()
        active = true

        if (isPause) {
            startMedia()
        } else {
            playVideo()
        }
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        pauseMedia()
        isPause = true
    }

    public override fun onStop() {
        super.onStop()
        active = false
    }

    override fun onDestroy() {

        releaseMedia()
        loginPresenter.detachView()

        SocialManager.releaseGoogleLogin(this)
        SocialManager.cancelInstance()
        super.onDestroy()
    }

    private fun releaseMedia() {
        try {
            video_view.stop()
            video_view.release()
            //                video_view = null;
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun pauseMedia() {
        video_view.pause()
    }

    private fun startMedia() {
        video_view.start()
    }

    @OnClick(R.id.ll_sign_late)
    fun onSignInLateButtonClicked() {
        //
        //        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(LoginActivity.this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        //        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        //        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //        ActivityCompat.startActivity(LoginActivity.this, intent, options.toBundle());
        //
        //        // Connect XMPP if as Guest
        //        AppsterChatManger.getInstance(context).destroy();
        //        AppsterChatManger.getInstance(context).disConnectXMPP();
        //        AppsterChatManger.getInstance(getApplicationContext()).connectIfGuest();
        //
        //        finish();
    }

    @OnClick(R.id.btn_twitter_login)
    fun loginTwitter() {

        if (preventMultiClicks()) {
            return
        }

        if (!CheckNetwork.isNetworkAvailable(this@LoginActivity)) {
            utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection), this@LoginActivity)
            return
        }
        loginPresenter.getTwitterInformation(mTwitterAuthClient)
    }

    //    @OnClick(R.id.btn_facebook_login)
    fun onFacebookButtonClicked() {
        // Preventing multiple clicks, using threshold of 1 second
        if (preventMultiClicks()) {
            return
        }

        if (!CheckNetwork.isNetworkAvailable(this@LoginActivity)) {
            utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection), this@LoginActivity)
            return
        }

        loginPresenter.loginWithFacebook()
    }

    //    @OnClick(R.id.btn_google_login)
    fun onGoogleButtonClicked() {
        // Preventing multiple clicks, using threshold of 1 second
        if (preventMultiClicks()) {
            return
        }

        if (!CheckNetwork.isNetworkAvailable(this@LoginActivity)) {
            utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection), this@LoginActivity)
            return
        }

        SocialManager.loginWithGoogle(this, LoginActivity.GG_REQUEST_SIGN_IN, this)
    }

    //    @OnClick(R.id.btn_phone_login)
    internal fun onPhoneLoginButonClicked() {
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left)
        val intent = PhoneSignInSignUpActivity.createIntent(this)
        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    //    @OnClick(R.id.btn_instagram_login)
    internal fun onInstagramClick() {
        loginPresenter.loginInstagram()
    }

    private fun setText() {
        val bySignUp = getString(R.string.login_by_signing_up) + " "
        val terms = getString(R.string.login_terms)
        val and = " " + getString(R.string.login_and) + " "
        val privacy = getString(R.string.login_privacy_policy)
        val dots = getString(R.string.login_dot)

        val text = bySignUp + terms + and + privacy + dots

        val styledString = SpannableString(text)
        // underline text
        styledString.setSpan(UnderlineSpan(), bySignUp.length,
                bySignUp.length + terms.length, 0)
        styledString.setSpan(UnderlineSpan(),
                bySignUp.length + terms.length + and.length,
                bySignUp.length + terms.length + and.length + privacy.length, 0)

        val clickableTerms = object : ClickableSpan() {

            override fun onClick(widget: View) {
                val options = ActivityOptionsCompat.makeCustomAnimation(this@LoginActivity, R.anim.push_in_to_right, R.anim.push_in_to_left)
                val intentTerms = ActivityViewWeb.createIntent(this@LoginActivity, Constants.URL_TERMS_CONDITION, false)
                ActivityCompat.startActivity(this@LoginActivity, intentTerms, options.toBundle())
            }
        }

        styledString.setSpan(clickableTerms, bySignUp.length, bySignUp.length + terms.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val clickablePrivacy = object : ClickableSpan() {

            override fun onClick(widget: View) {
                val options = ActivityOptionsCompat.makeCustomAnimation(this@LoginActivity, R.anim.push_in_to_right, R.anim.push_in_to_left)
                val intentTerms = Intent(this@LoginActivity, ActivityViewWeb::class.java)
                intentTerms.putExtra(ConstantBundleKey.BUNDLE_URL_FOR_WEBVIEW, Constants.URL_PRIVACY_POLICY)
                ActivityCompat.startActivity(this@LoginActivity, intentTerms, options.toBundle())
            }
        }
        styledString.setSpan(clickablePrivacy, bySignUp.length + terms.length + and.length,
                bySignUp.length + terms.length + and.length + privacy.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        styledString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.white)),
                0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv_term.text = styledString
        tv_term.movementMethod = LinkMovementMethod.getInstance()

        //        SpannableString styledSignLate
        //                = new SpannableString(getString(R.string.login_Sign_in_later));
        //        styledSignLate.setSpan(new UnderlineSpan(), 0,
        //                styledSignLate.length(), 0);
        //        llSignLate.setText(styledSignLate);
    }

    private fun playVideo() {

        //        releaseMedia();
        //        if (animation == null) {
        //            animation = new TranslateAnimation(0.0f, 200.0f,
        //                    0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        //            animation.setDuration(6000);  // animation duration
        //            animation.setRepeatCount(Animation.INFINITE);  // animation repeat count
        //            animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
        //            //animation.setFillAfter(true);
        //        }
        //
        //        video_view.startAnimation(animation);  // start animation

        try {

            video_view.setRawData(R.raw.video_login)
            video_view.setVolume(0f, 0f)
            video_view.isLooping = true
            video_view.prepare { it.start() }
        } catch (ioe: Exception) {
            Timber.e(ioe)
        }

    }

    private fun calcKeyboardHeight() {
        val root = findViewById<View>(R.id.root_view)
        root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val r = Rect()
                root.getWindowVisibleDisplayFrame(r)
                val screenHeight = root.rootView.height
                val keyboardHeight = screenHeight - r.bottom

                // IF height diff is more then 150, consider keyboard as visible.
                if (keyboardHeight > 150) {
                    AppsterApplication.mAppPreferences.setIntPreferenceData(Constants.KEYBOARD_HEIGHT, keyboardHeight)
                    root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
                video_view.setScalableType(ScalableType.CENTER_CROP)
                video_view.invalidate()
            }
        })
    }

    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show()
            } else {
                finish()
            }
            return false
        }
        return true
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == GG_REQUEST_SIGN_IN) {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                loginPresenter.onGoogleLoginResponse(result)
            }
            //        playVideo();
            SocialManager.getInstance().onActivityResult(requestCode, resultCode, data)
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data)
        } catch (e: Exception) {
            Timber.e(e)
        }

    }

    override fun onAdminBlocked(message: String) {
        LoginHelper.showBlockedDialog(this, message)
    }

    override fun onAccountSuspended() {
        LoginHelper.showSuspendedDialog(this)
    }

    override fun onLoginSuccessfully(request: BaseLoginRequestModel, loginResponse: BaseResponse<LoginResponseModel>) {
        if (loginResponse.data == null || loginResponse.code == ShowErrorManager.user_not_found) {
            dismisDialog()
            goToCreateProfile(request)

            return
        }

        val userInfoModel = loginResponse.data.userInfo
        if (userInfoModel != null && StringUtil.isNullOrEmptyString(userInfoModel.userImage)) {
            userInfoModel.userImage = request.profile_Pic
        }

        if (userInfoModel != null && !StringUtil.isNullOrEmptyString(userInfoModel.fbId)) {
            SocialManager.getInstance().isCreatedAccount = true
        }

        if (userInfoModel != null) {
            mCompositeSubscription.add(LoginHelper.updateUserProfile(this, loginResponse.data)
                    .flatMap<Any> { _ ->
                        val userModel = loginResponse.data.userInfo
                        userModel?.gender?.isEmpty().run {
                            var refIdInput = "0"
                            if (refIdInput == userModel.refId) {
                                refIdInput = "0"
                            }
                            val editRequest = EditProfileRequestModel(request.gender, userModel.displayName, SetDateTime.getDOB(userModel.doB, this@LoginActivity), userModel.email, userModel.nationality,
                                    "", "", null, refIdInput, StringUtil.encodeString(userModel.about), AppsterApplication.mAppPreferences.devicesUDID)

                            LoginHelper.updateUserProfile(this@LoginActivity, loginResponse.data)
                                    .flatMap { AppsterWebServices.get().editProfile(AppsterUtility.getAuth(), editRequest.build()) }
                        }

                        Observable.just(1)
                    }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        mLoginHandler.sendEmptyMessage(MESSAGE_GET_START_APP)
                        EventTracker.setOptOut(userInfoModel)
                    }, Timber::e))
        } else {
            dismisDialog()
            SocialManager.getInstance().logOut()
            Toast.makeText(applicationContext, loginResponse.message, Toast.LENGTH_SHORT).show()
        }
        dismisDialog()
    }

    override fun onForceMaintenance(model: MaintenanceModel) {
        MaintenanceActivity.startMaintenanceActivity(this, model)
    }

    override fun onNavigateMainScreen() {

        //        if (!AppsterApplication.mAppPreferences.getHasShowBoarding()) {
        //            Intent navigationIntent = OnBoardingActivity.createIntent(this, true, null);
        //            mWelcomeHelper = new WelcomeHelper(this, OnBoardingActivity.class, navigationIntent);
        //            mWelcomeHelper.forceShowAndCloseActivity();
        //        } else {
        val options = ActivityOptionsCompat.makeCustomAnimation(this,
                R.anim.push_in_to_right, R.anim.push_in_to_left)
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        ActivityCompat.startActivity(this@LoginActivity, intent, options.toBundle())
        finish()
        //        }
    }

    private fun goToCreateProfile(requestModel: BaseLoginRequestModel) {
        val options = ActivityOptionsCompat.makeCustomAnimation(this@LoginActivity, R.anim.push_in_to_right, R.anim.push_in_to_left)
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        intent.putExtra(ConstantBundleKey.BUNDLE_TYPE_KEY, Constants.LoginType.SOCIAL_TYPE)
        if (!requestModel.display_name.isNullOrEmpty()) intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_DISPLAY_NAME, requestModel.display_name)
        var id = ""
        var loginFrom = ""
        EventTracker.trackEvent(EventTrackingName.EVENT_CREATE_PROFILE)
        when (requestModel) {
            is LoginFacebookRequestModel -> {
                id = requestModel.fb_id
                loginFrom = ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_FACEBOOK
            }
            is TwitterLoginRequestModel -> {

                id = requestModel.twitterId
                loginFrom = ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_TWITTER
            }
            is GoogleLoginRequestModel -> {
                id = requestModel.googleId
                loginFrom = ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_GOOGLE

            }
            is InstagramLoginRequestModel -> {
                id = requestModel.instagramId
                loginFrom = ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_INSTAGRAM
            }
            is WeChatLoginRequestModel -> {
                id = requestModel.weChatId
                loginFrom = ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_WECHAT
            }
            is WeiboLoginRequestModel -> {
                id = requestModel.weiboId
                loginFrom = ConstantBundleKey.LOGIN_FROM.ARG_LOGIN_WEIBO
            }
        }
        var expectedUserId = if (!requestModel.display_name.isNullOrEmpty() && TextUtils.isDigitsOnly(requestModel.display_name)) requestModel.email else requestModel.display_name
        if (TextUtils.isEmpty(expectedUserId)) expectedUserId = requestModel.userName
        intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_ID, id)
        intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_FROM, loginFrom)
        intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_PROFILE_PIC, requestModel.profile_Pic)
        intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_EMAIL, requestModel.email)
        intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_GENDER, requestModel.gender)
        if (!requestModel.display_name.isNullOrEmpty()) intent.putExtra(ConstantBundleKey.BUNDLE_LOGIN_DISPLAY_NAME, requestModel.display_name)
        intent.putExtra(RegisterActivity.EXPECTED_USER_ID, expectedUserId)

        ActivityCompat.startActivity(this@LoginActivity, intent, options.toBundle())
    }


    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        //do nothing here
    }


    override fun getViewContext(): Context {
        return this
    }

    override fun loadError(errorMessage: String, code: Int) {
        if (!isFinishing || !isDestroyed) {
            utility.showMessage(
                    getString(R.string.app_name),
                    errorMessage,
                    context)
        }
    }

    override fun showProgress() {
        if (active) {
            showDialog(context, resources.getString(R.string.connecting_msg))
        }
    }

    override fun hideProgress() {
        dismisDialog()
    }

    internal class LoginHandler(activity: LoginActivity) : Handler() {
        private val mLoginActivityWeakReference: WeakReference<LoginActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val activity = mLoginActivityWeakReference.get()
            activity?.handleMessage(msg)
        }
    }

    companion object {
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        private const val MESSAGE_GET_START_APP = 999999
        const val GG_REQUEST_SIGN_IN = 9009
    }

}
