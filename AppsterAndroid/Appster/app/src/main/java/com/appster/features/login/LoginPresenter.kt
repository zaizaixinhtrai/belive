package com.appster.features.login

import android.app.Activity
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.appster.AppsterApplication
import com.appster.AppsterApplication.mAppPreferences
import com.appster.BuildConfig
import com.appster.R
import com.appster.base.ActivityScope
import com.appster.location.GPSTClass
import com.appster.manager.ShowErrorManager
import com.appster.models.TwitterInformationModel
import com.appster.tracking.EventTracker
import com.appster.tracking.EventTrackingName
import com.appster.utility.DeviceInfo
import com.appster.utility.RxUtils
import com.appster.utility.SocialManager
import com.appster.utility.instagram.InstagramHelper
import com.appster.utility.instagram.InstagramModel
import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.request_models.*
import com.appster.webservice.response.BaseResponse
import com.appster.webservice.response.LoginResponseModel
import com.apster.common.Constants
import com.domain.interactors.login.*
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.pack.utility.StringUtil
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.User
import rx.Observable
import rx.Subscriber
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by User on 12/7/2016.
 */
@ActivityScope
class LoginPresenter @Inject constructor(private var loginView: LoginContract.LoginView?,
                                         val googleLoginUseCase: GoogleLoginUseCase,
                                         val twitterLoginUseCase: TwitterLoginUseCase,
                                         val instagramLoginUseCase: InstagramLoginUseCase,
                                         val facebookLoginUseCase: FacebookLoginUseCase,
                                         val weiboLoginUseCase: WeiboLoginUseCase,
                                         val services: AppsterWebserviceAPI) : LoginContract.LoginActions, SocialManager.SocialLoginListener, InstagramHelper.OAuthAuthenticationListener {

    private var instagramHelper: InstagramHelper?
    private val mCompositeSubscription: CompositeSubscription by lazy { CompositeSubscription() }
    internal var latitude = 0.0
    internal var longitude = 0.0
//    private val service by lazy { AppsterWebServices.get() }

    init {
        instagramHelper = InstagramHelper(loginView?.viewContext, Constants.INSTAGRAM_CLIENT_ID,
                Constants.INSTAGRAM_CLIENT_SECRET, Constants.INSTAGRAM_CALLBACK_URL)
        instagramHelper?.setListener(this)
    }

    override fun attachView(view: LoginContract.LoginView) {

    }

    override fun detachView() {
        instagramHelper?.setListener(null)
        instagramHelper = null
        loginView = null
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription)
    }

    //#region facebook login
    override fun loginWithFacebook() {
        val request = LoginFacebookRequestModel()
        EventTracker.trackEvent(EventTrackingName.EVENT_LOGIN_SNS_CLICK, EventTrackingName.SNS_CLICK_FB, EventTrackingName.SNS_CLICK_FB)
        request.device_udid = DeviceInfo.getDeviceDetail(loginView?.viewContext)
        SocialManager.getInstance().login(loginView?.viewContext, this, request)
    }

    override fun onStartingAuthentication() {

    }

    override fun onLoginFail(message: String) {
        loginView?.hideProgress()
    }

    override fun onAuthentSuccess() {
        loginView?.hideProgress()
    }

    override fun loginWithFacebookInfo(requestLogin: LoginFacebookRequestModel) {
        loginView?.let {
            it.showProgress()
            //        AppsterApplication.mAppPreferences.setDevicesUDID(DeviceInfo.getDeviceDetail(context));
            //        requestLogin.setDevice_token(AppsterApplication.mAppPreferences.getDevicesToken());
            //        requestLogin.setDevice_udid(AppsterApplication.mAppPreferences.getDevicesUDID());
            setBaseLoginRequest(requestLogin)
            mCompositeSubscription.add(facebookLoginUseCase.execute(requestLogin)
                    .filter { _ -> loginView != null }
                    .subscribe({ loginDataResponse -> onAppsterLoginResponse(requestLogin, loginDataResponse) }
                    ) { _ ->
                        it.loadError(loginView?.viewContext?.resources?.getString(R.string.activity_sign_unknown_error), 0)
                        it.hideProgress()
                    })
        }

    }

    override fun onCompleteLogin() {

    }
    //#endregion facebook login ====================================================================

    //#region Google login
    override fun onGoogleLoginResponse(result: GoogleSignInResult) {
        if (result.isSuccess) {
            // Signed in successfolly, show authenticated UI.
            EventTracker.trackEvent(EventTrackingName.EVENT_LOGIN_SNS_CLICK, EventTrackingName.SNS_CLICK_GOOGLE, EventTrackingName.SNS_CLICK_GOOGLE)
            val acct = result.signInAccount
            val requestLogin = GoogleLoginRequestModel()
            requestLogin.device_udid = DeviceInfo.getDeviceDetail(loginView?.viewContext)
            val email = acct!!.email
            requestLogin.userName = StringUtil.extractUserNameFromEmail(email)
            requestLogin.email = email
            requestLogin.googleId = acct.id
            requestLogin.display_name = acct.displayName
            AppsterApplication.mAppPreferences.facebookDisplayName = acct.displayName
            if (acct.photoUrl != null)
                requestLogin.profile_Pic = acct.photoUrl!!.toString()

            setBaseLoginRequest(requestLogin)
            loginAppstersWithGoogleInfo(requestLogin)
            logoutGoogle()
        }
    }

    private fun loginAppstersWithGoogleInfo(request: GoogleLoginRequestModel) {
        if (loginView != null) loginView!!.showProgress()
        mCompositeSubscription.add(googleLoginUseCase.execute(request)
                .subscribe({ loginDataResponse -> onAppsterLoginResponse(request, loginDataResponse) }, { t ->
                    Timber.e(t.message)
                    loginView?.apply {
                        loadError(loginView?.viewContext?.resources?.getString(R.string.activity_sign_unknown_error), 0)
                        hideProgress()
                    }
                }))

    }

    private fun logoutGoogle() {
        SocialManager.logoutGoogle(loginView?.viewContext as AppCompatActivity?, null)
    }
    //#endregion Google login ======================================================================

    //region Twitter login
    override fun getTwitterInformation(twitterAuthClient: TwitterAuthClient) {
        twitterAuthClient.authorize((loginView?.viewContext as Activity?)!!, object : com.twitter.sdk.android.core.Callback<TwitterSession>() {

            override fun success(result: Result<TwitterSession>) {
                handleTwitterResult(result)
                EventTracker.trackEvent(EventTrackingName.EVENT_LOGIN_SNS_CLICK, EventTrackingName.SNS_CLICK_TWITTER, EventTrackingName.SNS_CLICK_TWITTER)
            }

            override fun failure(e: TwitterException) {
                loginView?.hideProgress()
                e.message?.contains("Authorization failed, request was canceled.").let {
                    loginView?.loadError(e.message, Constants.UN_KNOW_ERROR)
                }
            }
        })
    }

    private fun handleTwitterResult(result: Result<TwitterSession>) {

        val informationModel = TwitterInformationModel()

        //Creating a twitter session with result's data
        val session = result.data

        //Getting the username from session
        val username = session.userName

        //This code will fetch the profile image URL
        //Getting the account service of the user logged in
        Twitter.getApiClient(session).accountService
                .verifyCredentials(true, false)
                .enqueue(object : Callback<User>() {
                    override fun success(result: Result<User>) {
                        //If it succeeds creating a User object from userResult.data
                        val user = result.data

                        //Getting the profile image url
                        informationModel.twitterImage = user.profileImageUrl
                        informationModel.twitterId = user.id.toString() + ""
                        informationModel.twitterUsername = username
                        informationModel.twitterDisplayName = user.name


                        val authClient = TwitterAuthClient()
                        authClient.requestEmail(session, object : Callback<String>() {
                            override fun success(result: Result<String>) {

                                informationModel.twitterEmail = result.data
                                getTwitterInformationSuccess(informationModel)

                            }

                            override fun failure(exception: TwitterException) {
                                getTwitterInformationSuccess(informationModel)
                            }
                        })

                    }


                    override fun failure(e: TwitterException) {
                        //If any error occurs handle it here
                        loginView?.apply {
                            loadError(e.message, Constants.UN_KNOW_ERROR)
                            hideProgress()
                        }
                    }
                })
    }

    private fun getTwitterInformationSuccess(informationModel: TwitterInformationModel) {
        val loginRequestModel = TwitterLoginRequestModel()
        loginRequestModel.twitterId = informationModel.twitterId
        loginRequestModel.email = informationModel.twitterEmail
        loginRequestModel.display_name = informationModel.twitterDisplayName
        loginRequestModel.userName = informationModel.twitterUsername
        loginRequestModel.profile_Pic = informationModel.twitterImage

        setBaseLoginRequest(loginRequestModel)
        loginAppsteriWthTwitterInfo(loginRequestModel)
    }

    override fun loginAppsteriWthTwitterInfo(loginRequestModel: TwitterLoginRequestModel) {
        loginView?.showProgress()
        mCompositeSubscription.add(twitterLoginUseCase.execute(loginRequestModel)
                .onErrorResumeNext({ Observable.error(it) })
                .subscribe(object : Subscriber<BaseResponse<LoginResponseModel>>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        loginView?.apply {
                            loadError(e.message, Constants.RETROFIT_ERROR)
                            hideProgress()
                        }
                    }

                    override fun onNext(loginTwitterDataResponse: BaseResponse<LoginResponseModel>) {
                        onAppsterLoginResponse(loginRequestModel, loginTwitterDataResponse)
                    }
                }))
    }

    //#endregion Twitter Login ======================================================================

    //region instagram login
    override fun loginInstagram() {
        EventTracker.trackEvent(EventTrackingName.EVENT_LOGIN_SNS_CLICK, EventTrackingName.SNS_CLICK_IG, EventTrackingName.SNS_CLICK_IG)
        instagramHelper?.authorize()
    }

    override fun onInstagramLoginSuccessfully(instagramSession: InstagramModel) {
        // userInfoHashmap = instagramHelper.
        val requestLogin = InstagramLoginRequestModel()
        requestLogin.device_udid = DeviceInfo.getDeviceDetail(loginView?.viewContext)

        requestLogin.instagramId = instagramSession.id
        requestLogin.userName = instagramSession.username
        requestLogin.display_name = instagramSession.fullName
        AppsterApplication.mAppPreferences.facebookDisplayName = instagramSession.fullName
        if (instagramSession.profilePicture != null)
            requestLogin.profile_Pic = instagramSession.profilePicture

        setBaseLoginRequest(requestLogin)
        loginAppstersWithInstagramInfo(requestLogin)
    }

    override fun onInstagramLoginFail(error: String) {
        loginView?.loadError(error, 0)
    }

    private fun loginAppstersWithInstagramInfo(request: InstagramLoginRequestModel) {
        loginView?.showProgress()
        mCompositeSubscription.add(instagramLoginUseCase.execute(request)
                .subscribe({ loginDataResponse -> onAppsterLoginResponse(request, loginDataResponse) }) { _ ->
                    loginView?.apply {
                        loadError(loginView?.viewContext?.resources?.getString(R.string.activity_sign_unknown_error), 0)
                        hideProgress()
                    }
                })
    }
    //#endregion instagram login


    internal fun onAppsterLoginResponse(request: BaseLoginRequestModel, loginResponse: BaseResponse<LoginResponseModel>?) {
        if (loginResponse == null) {
            loginView?.hideProgress()
            return
        }
        //
        if (loginResponse.code == ShowErrorManager.account_deactivated_or_suspended) {
            loginView?.apply {
                onAccountSuspended()
                hideProgress()
            }
            return

        } else if (loginResponse.code == ShowErrorManager.ADMIN_BLOCKED) {
            loginView?.apply {
                onAdminBlocked(loginResponse.message)
                hideProgress()
            }
            return
        }

        loginView?.onLoginSuccessfully(request, loginResponse)
    }

    private fun setBaseLoginRequest(loginRequest: BaseLoginRequestModel): BaseLoginRequestModel {
        AppsterApplication.mAppPreferences.devicesUDID = DeviceInfo.getDeviceDetail(loginView?.viewContext)//android_id
        loginRequest.device_token = AppsterApplication.mAppPreferences.devicesToken//GCM token
        loginRequest.device_udid = AppsterApplication.mAppPreferences.devicesUDID

        //        if (mRxPermissions.isGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION) && mRxPermissions.isGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
        val gpstClass = GPSTClass.getInstance()
        gpstClass.getLocation(loginView?.viewContext)

        // check if GPS enabled
        if (gpstClass.canGetLocation()) {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            latitude = gpstClass.latitude
            longitude = gpstClass.longitude

        }
        //        }

        loginRequest.longitude = longitude
        loginRequest.latitude = latitude
        return loginRequest
    }

    override fun checkMaintenance() {
        loginView?.showProgress()
        val time = SystemClock.currentThreadTimeMillis()
        mCompositeSubscription.add(services.checkMaintenance(BuildConfig.AWS_S3_SERVER_LINK + "maintenance/maintenance.json?t=" + time)
                .filter { loginView != null }
                .subscribe({
                    loginView?.hideProgress()
                    if (it != null && it.maintenanceMode == Constants.MAINTENANCE_MODE_START) {
                        loginView?.onForceMaintenance(it)
                    } else {
                        loginView?.onNavigateMainScreen()
                    }
                    if (it != null) {
                        mAppPreferences.saveMaintenanceModel(it)
                    }
                }) { error ->
                    Timber.e(error)
                    loginView?.apply {
                        hideProgress()
                        onNavigateMainScreen()
                    }
                })

    }
}
