package com.appster.features.login

import android.content.Context
import com.appster.AppsterApplication
import com.appster.R
import com.appster.manager.AppsterChatManger
import com.appster.tracking.EventTracker
import com.appster.utility.CrashlyticsUtil
import com.appster.utility.OneSignalUtil
import com.appster.webservice.AppsterWebServices
import com.appster.webservice.response.LoginResponseModel
import com.apster.common.DialogbeLiveConfirmation
import rx.Observable

/**
 * Created by linh on 26/10/2017.
 */

object LoginHelper {
    fun updateUserProfile(context: Context, loginResponse: LoginResponseModel): Observable<Any> {
        val userInfoModel = loginResponse.userInfo
        // update crashlytics user data
        CrashlyticsUtil.setUser(userInfoModel)
        // update amplitude user data
        EventTracker.setUser(userInfoModel)
        // OneSignal
        OneSignalUtil.setUser(userInfoModel)

        return Observable.fromCallable {
            AppsterApplication.mAppPreferences.saveUserInforModel(userInfoModel)
            AppsterApplication.mAppPreferences.saveUserToken(loginResponse.access_token)
            AppsterApplication.mAppPreferences.numberUnreadMessage = loginResponse.userInfo.unreadMessageCount
            AppsterApplication.mAppPreferences.numberUnreadNotification = loginResponse.userInfo.unreadNotificationCount
            AppsterApplication.mAppPreferences.userLoginType = loginResponse.loginType
            AppsterApplication.mAppPreferences.isLoginFacebook = true

            AppsterChatManger.getInstance(context).destroy()
            AppsterChatManger.getInstance(context).disConnectXMPP()
            AppsterChatManger.getInstance(context).reconnectIfNeed()
            AppsterWebServices.resetAppsterWebserviceAPI()
            null
        }
    }

    fun showSuspendedDialog(context: Context) {
        val builder = DialogbeLiveConfirmation.Builder()
        builder.title(context.getString(R.string.app_name))
                .message(context.getString(R.string.please_contact_appsters_team))
                .confirmText(context.getString(R.string.btn_text_ok))
                .singleAction(true)
                .build().show(context)
    }

    fun showBlockedDialog(context: Context, message: String) {
        val builder = DialogbeLiveConfirmation.Builder()
        builder.title(context.getString(R.string.app_name))
                .message(message)
                .confirmText(context.getString(R.string.btn_text_ok))
                .singleAction(true)
                .build().show(context)
    }
}
