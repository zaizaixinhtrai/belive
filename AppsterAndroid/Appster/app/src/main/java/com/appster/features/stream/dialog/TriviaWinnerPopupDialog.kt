package com.appster.features.stream.dialog

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.appster.AppsterApplication
import com.appster.R
import com.appster.dialog.ImmersiveDialogFragment
import com.appster.extensions.inflate
import com.appster.extensions.loadImg
import com.appster.models.UserModel
import com.appster.utility.AppsterUtility
import com.appster.utility.SocialManager
import com.apster.common.BranchIoUtil
import com.apster.common.Constants
import com.apster.common.Utils
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.pack.utility.CheckNetwork
import com.pack.utility.StringUtil
import kotlinx.android.synthetic.main.popup_for_winner_dialog.*
import kotlinx.android.synthetic.main.trivia_share_item.view.*

/**
 * Created by Ngoc on 4/20/2018.
 */
class TriviaWinnerPopupDialog : ImmersiveDialogFragment() {

    private val mUserModel: UserModel? by lazy { AppsterApplication.mAppPreferences.userModel }
    private var mSharableBranchIoUrl: String? = null
    private val fbCallbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    var message: String? = null
    private var winnerCash: String? = null
    private var prizeMessage: String? = null
    var title: String? = null
    private var countryCode: String? = null

    companion object {
        private const val REQUEST_CODE_FB_SHARE = 1
        const val BUNDLE_WINNER_CASH = "winner_cash"
        const val BUNDLE_ADMIN_MESSAGE = "admin_message"
        const val BUNDLE_TITLE = "title"
        const val BUNDLE_PRIZE_MESSAGE = "PrizeMessage"
        const val BUNDLE_COUNTRY_CODE = "country_code"
        @JvmStatic
        fun newInstance(title: String?, prizeMessage: String?, message: String?, winnerCash: String?, countryCode: String?): TriviaWinnerPopupDialog {
            val triviaWinnerPopupDialog = TriviaWinnerPopupDialog()
            triviaWinnerPopupDialog.arguments = Bundle().apply {
                putString(BUNDLE_TITLE, title)
                putString(BUNDLE_PRIZE_MESSAGE, prizeMessage)
                putString(BUNDLE_ADMIN_MESSAGE, message)
                putString(BUNDLE_WINNER_CASH, winnerCash)
                putString(BUNDLE_COUNTRY_CODE, countryCode)
            }

            return triviaWinnerPopupDialog
        }
    }

    override fun getRootLayoutResource(): Int {
        return R.layout.popup_for_winner_dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(Utils.dpToPx(280f), ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(R.color.transparent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            message = getString(BUNDLE_ADMIN_MESSAGE, "")
            winnerCash = getString(BUNDLE_WINNER_CASH, "")
            prizeMessage = getString(BUNDLE_PRIZE_MESSAGE, "")
            title = getString(BUNDLE_TITLE, "")
            countryCode = getString(BUNDLE_COUNTRY_CODE, "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        addShareSocialView()
        intData()
    }


    private fun intData() {
        tvPrizeMessage.text = prizeMessage
        tvWinCash.text = winnerCash
        tvTitle.text = title
        tvMessage.text = message?.let { StringUtil.fromHtml(message) }
        if (isVNTrivia()) tv_share.text = getString(R.string.winner_share_title_vi)
        ibClose.setOnClickListener { dismissAllowingStateLoss() }
    }

    private fun isVNTrivia(): Boolean {
        countryCode?.let { return Constants.COUNTRY_CODE_VN_FROM_SERVER_RETURN.equals(countryCode) }
        return false
    }

    private fun addShareSocialView() {
//        addView(R.drawable.belive_icon,
//                getString(R.string.app_name),
//                View.OnClickListener {
//
//                }
//        )

        addView(R.drawable.icon_share_facebook,
                getString(R.string.share_live_facebook),
                View.OnClickListener {
                    AppsterUtility.temporaryLockView(it)
                    getBranchIoUrl(BranchIoUtil.OnBranchIoCallback {
                        if (isFragmentUIActive) {
                            mSharableBranchIoUrl = it
                            SocialManager.getInstance().shareURLToFacebook(context, it,
                                    REQUEST_CODE_FB_SHARE,
                                    fbCallbackManager,
                                    object : FacebookCallback<Sharer.Result> {
                                        override fun onSuccess(result: Sharer.Result) {
                                        }

                                        override fun onCancel() {}
                                        override fun onError(error: FacebookException) {}
                                    })
                        }
                    })
                }
        )

        addView(R.drawable.icon_share_whatsapp,
                getString(R.string.share_live_whatsapp),
                View.OnClickListener {
                    AppsterUtility.temporaryLockView(it)
                    getBranchIoUrl(BranchIoUtil.OnBranchIoCallback {
                        if (isFragmentUIActive) {
                            val content = String.format(getString(R.string.invite_sns_trivia_message), mUserModel?.referralId)
                            SocialManager.getInstance().shareVideoToWhatsapp(context, content, it)
                        }
                    })
                }
        )

        addView(R.drawable.icon_share_twitter,
                getString(R.string.share_live_twitter),
                View.OnClickListener {
                    AppsterUtility.temporaryLockView(it)
                    getBranchIoUrl(BranchIoUtil.OnBranchIoCallback {
                        if (isFragmentUIActive) {
                            val content = String.format(getString(R.string.invite_sns_trivia_message), mUserModel?.referralId)
                            SocialManager.getInstance().ShareFeedQuotesToTwitter(context, content, it)
                        }
                    })
                }
        )

        addView(R.drawable.icon_share_email,
                getString(R.string.share_live_email),
                View.OnClickListener {
                    AppsterUtility.temporaryLockView(it)
                    getBranchIoUrl(BranchIoUtil.OnBranchIoCallback {
                        if (isFragmentUIActive) {
                            val content = String.format(getString(R.string.invite_sns_trivia_message), mUserModel?.referralId)
                            val subject = getString(R.string.invite_mail_subject)
                            SocialManager.getInstance().shareURLToEmail(context, content, subject, it)
                        }
                    })
                }
        )

        addView(R.drawable.icon_share_others,
                getString(R.string.share_live_others),
                View.OnClickListener {
                    AppsterUtility.temporaryLockView(it)
                    getBranchIoUrl(BranchIoUtil.OnBranchIoCallback {
                        if (isFragmentUIActive) {
                            val content = String.format(getString(R.string.invite_sns_trivia_message), mUserModel?.referralId) + "\n" + it
                            val sharingIntent = Intent(Intent.ACTION_SEND)
                            sharingIntent.type = "text/plain"
                            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "")
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, content)
                            startActivity(Intent.createChooser(sharingIntent, "Share via"))
                        }
                    })
                }
        )
    }

    fun getBranchIoUrl(callback: BranchIoUtil.OnBranchIoCallback) {
        if (!CheckNetwork.isNetworkAvailable(context)) {
            return
        }

        if (!mSharableBranchIoUrl.isNullOrEmpty()) {
            callback.onComplete(mSharableBranchIoUrl)
            return
        }

        BranchIoUtil.generateBranchIoUrl(context, mUserModel?.userImage, mUserModel?.referralId, callback)
    }

    fun addView(drawableId: Int, text: String, clickListener: View.OnClickListener?) {
        val categoryView = llShareItemContainer.inflate(R.layout.trivia_share_item)
        llShareItemContainer.addView(categoryView)
        categoryView.apply {
            ivCategoryImage.loadImg(drawableId)
            tvCategoryTitle.text = text
            tvCategoryTitle.setTextColor(Color.parseColor("#a500b7"))
            if (clickListener != null) {
                categoryView.setOnClickListener(clickListener)
            }
        }
    }

    override fun isDimDialog(): Boolean {
        return false
    }


}