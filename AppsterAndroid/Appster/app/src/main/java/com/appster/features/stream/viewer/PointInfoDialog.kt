package com.appster.features.stream.viewer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import com.appster.AppsterApplication
import com.appster.R
import com.appster.dialog.NoTitleDialogFragment
import com.apster.common.CommonDefine
import com.apster.common.DialogManager
import com.apster.common.Utils
import com.pack.utility.CheckNetwork
import com.pack.utility.DialogInfoUtility
import kotlinx.android.synthetic.main.dialog_point_info.*
import java.text.DecimalFormat

/**
 *  Created by DatTN on 11/5/2018
 */
class PointInfoDialog : NoTitleDialogFragment() {

    override fun getRootLayoutResource(): Int {
        return R.layout.dialog_point_info
    }

    override fun isDimDialog(): Boolean {
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ib_close.setOnClickListener { dismissAllowingStateLoss() }
        arguments?.apply {
            val myFormatter = DecimalFormat("#,###")
            tv_user_point.text = myFormatter.format(AppsterApplication.mAppPreferences.userModel.points).replace(".", ",")
            setupWebView(getString(CommonDefine.KEY_POINT_INFO_URL, "http://belive.sg/"))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(pointUrlInfo: String) {
        if (!CheckNetwork.isNetworkAvailable(context)) {
            DialogInfoUtility().showMessage("", getString(R.string.network_error), context)
            return
        }
        wv_content.settings?.apply {
            javaScriptEnabled = true
        }
        wv_content.webViewClient = object : WebViewClient() {
        }
        wv_content.loadUrl(pointUrlInfo)
    }

    override fun onPause() {
        super.onPause()
        wv_content.onPause()
    }

    override fun onResume() {
        super.onResume()
        wv_content.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        wv_content.destroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(displayPoint: String, pointInfoUrl: String): PointInfoDialog {
            val dialog = PointInfoDialog()
            val bundle = Bundle()
            bundle.putString(CommonDefine.KEY_POINT_DISPLAY, displayPoint)
            bundle.putString(CommonDefine.KEY_POINT_INFO_URL, pointInfoUrl)
            dialog.arguments = bundle
            return dialog
        }
    }
}