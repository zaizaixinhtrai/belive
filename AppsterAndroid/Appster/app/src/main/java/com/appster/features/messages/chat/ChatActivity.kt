package com.appster.features.messages.chat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.appster.AppsterApplication
import com.appster.R
import com.appster.activity.BaseToolBarActivity
import com.appster.activity.UserProfileActivity
import com.appster.activity.ViewImageActivity
import com.appster.customview.CustomFontTextView
import com.appster.features.messages.chat.adapter.ChatAdapter
import com.appster.message.ChatItemModelClass
import com.appster.models.UserModel
import com.appster.models.event_bus_models.NewMessageEvent
import com.appster.sendgift.DialogSendGift
import com.appster.utility.ConstantBundleKey
import com.apster.common.*
import com.apster.common.FileUtility.MEDIA_TYPE_IMAGE_CROPPED
import com.jakewharton.rxbinding.view.RxView
import com.pack.utility.BitmapUtil
import com.pack.utility.StringUtil
import com.tbruyelle.rxpermissions.RxPermissions
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.chat_actions_containter.*
import kotlinx.android.synthetic.main.layout_chat_content.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jxmpp.stringprep.XmppStringprepException
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChatActivity : BaseToolBarActivity(), ChatContract.View, ChatAdapter.OnMediaClickListener {

    @Inject
    internal lateinit var presenter: ChatContract.UserActions

    private var mChattingUserId: String = ""
    private var mChattingUserUserName: String = ""
    private var mChattingUserDisplayName: String = ""
    private var mChattingUserAvatar: String = ""

    private lateinit var mRxPermission: RxPermissions
    private var mPopupControls: PopupWindow? = null

    private var mDialogSendGift: DialogSendGift? = null
    private var mIsKeyboardVisible = false
    private var mBitmapUtil: BitmapUtil? = null

    private val mChatAdapter: ChatAdapter by lazy {
        ChatAdapter(mutableListOf(),
                AppsterApplication.mAppPreferences.userModel.userImage ?: "",
                mChattingUserAvatar,
                mChattingUserDisplayName, this)
    }
    private var mTimeStamp = ""
    private var mPreviousHeightDiff = 0
    private lateinit var mSmoothScroller: MySmoothScroller

    private val mOnScrollChangeListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy < 0) {
                val layoutManager = recycler_view_chat.layoutManager
                when (layoutManager) {
                    is LinearLayoutManager -> {
                        if (layoutManager.findFirstVisibleItemPosition() == 0) {
                            presenter.loadPreviousChat()
                        }
                    }
                    is GridLayoutManager -> {
                        if (layoutManager.findFirstVisibleItemPosition() == 0) {
                            presenter.loadPreviousChat()
                        }
                    }
                }
            }
        }
    }

    //region-------activity life cycle-------
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        initVars()
        super.onCreate(savedInstanceState)

        setTopBarTileNoCap(mChattingUserDisplayName)
        useAppToolbarBackButton()
        eventClickBack.setOnClickListener { onBackPressed() }
        handleTurnoffMenuSliding()
        goneNotify(true)

        initControlsPopup(AppsterApplication.mAppPreferences.getIntPreference(Constants.KEYBOARD_HEIGHT, 80))

        presenter.attachView(this)
        presenter.initUserData(mChattingUserId, mChattingUserUserName, mChattingUserDisplayName, mChattingUserAvatar)
        presenter.loadLastChat()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        presenter.reconnectIfNeeded()
        mDialogSendGift?.resume()
        calcKeyboardHeight()
    }

    override fun onPause() {
        super.onPause()
        Utils.hideSoftKeyboard(this)
        hideControlsPopup()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
    //endregion -------activity life cycle-------

    //region -------inheritance methods-------
    override fun getLayoutContentId(): Int {
        return R.layout.layout_chat_content
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        val imageCroppedURI: Uri
        try {
            imageCroppedURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE_CROPPED)
        } catch (e: NullPointerException) {
            Timber.d(e)
            return
        }

        when (requestCode) {
            Constants.PICK_VIDEO_REQUEST -> loadVideoAfterPickFromGallery(data!!.data)
            Constants.RECORD_VIDEO_REQUEST -> {
                fileUri = data!!.data
                val videosPath = getRealPathFromURI(fileUri, mActivity)
                presenter.sendVideoToServer(videosPath)
            }
            Constants.VIDEO_TRIMMED_REQUEST -> {
                val trimVideosPath = data!!.getStringExtra(Constants.VIDEO_PATH)
                presenter.sendVideoToServer(trimVideosPath)
            }
            Constants.REQUEST_PIC_FROM_LIBRARY -> {
                fileUri = data!!.data
                if (fileUri == null) {
                    return
                }
                performCrop(fileUri, imageCroppedURI)
            }
            Constants.REQUEST_PIC_FROM_CAMERA -> {
                fileUri = data!!.data
                val bitmap = Utils.getBitmapFromURi(this, fileUri)
                presenter.sendImageToServer(bitmap)
            }
            Constants.REQUEST_PIC_FROM_CROP -> {
                val bitmapSend = Utils.getBitmapFromURi(this, imageCroppedURI)
                presenter.sendImageToServer(bitmapSend)
            }

            Constants.REQUEST_CODE_VIEW_USER_PROFILE -> if (data != null && data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
                val intent = intent
                intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        presenter.leaveChat()
        hideControlsPopup()
        Utils.hideSoftKeyboard(this)
        mIsKeyboardVisible = false
        setResult(Activity.RESULT_OK)
        finish()
    }
    //endregion -------inheritance methods-------

    //region -------implement methods-------
    override fun getViewContext(): Context {
        return this
    }

    override fun loadError(errorMessage: String?, code: Int) {
        handleError(errorMessage, code)
    }

    /**
     * This activity uses this method to show the load previous holder
     */
    override fun showProgress() {
        mChatAdapter.addLoadPreviousItem()
    }

    /**
     * This activity uses this method to hide the load previous holder
     */
    override fun hideProgress() {
        mChatAdapter.removeLoadPreviousItem()
    }

    override fun init() {
        mBitmapUtil = BitmapUtil()

        recycler_view_chat.layoutManager = LinearLayoutManager(this)
        val space = resources.getDimensionPixelSize(R.dimen.chat_list_divider)
        recycler_view_chat.addItemDecoration(UiUtils.ListSpacingItemDecoration(space, false))
        recycler_view_chat.adapter = mChatAdapter
        recycler_view_chat.addOnScrollListener(mOnScrollChangeListener)

        etInputMessage.requestFocus()
        tv_empty_holder.setOnClickListener {
            hideControlsPopup()
            Utils.hideSoftKeyboard(this)
        }

        flChatAttachment.setOnClickListener {
            if (mPopupControls != null && mPopupControls!!.isShowing) {
                hideControlsPopup()
            } else {
                showControlsPopup()
            }
        }
        // Send Message
        flChatSendMessage.setOnClickListener {
            try {
                presenter.sendChat(etInputMessage.text.toString().trim())
            } catch (e: XmppStringprepException) {
                Timber.e(e)
            }
        }

        mTimeStamp = (System.currentTimeMillis() / 1000L).toString()

        if (mBeLiveThemeHelper != null && mBeLiveThemeHelper.isTransparentStatusBarRequired) {
            val window = window
            window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        preventDownLineFirst()
    }

    override fun notifyUserSuspended() {
        val builder = DialogbeLiveConfirmation.Builder()
        builder.title(getString(R.string.app_name))
                .message(getString(R.string.user_has_been_suspended))
                .confirmText(getString(R.string.btn_text_ok))
                .singleAction(true)
                .onConfirmClicked {
                }
                .build().show(this)
    }

    override fun showDialog(titleRes: Int, contentRes: Int) {
        utility.showMessage(getString(titleRes), getString(contentRes), this)
    }

    override fun notifyUserBlockedYou(removeLastItem: Boolean) {
        tv_you_are_blocked.visibility = View.VISIBLE
        Utils.hideSoftKeyboard(this)
        if (removeLastItem) {
            mChatAdapter.removeLastItem()
        }
        clearComposer()
    }

    override fun showLoadingDialog(contentRes: Int) {
        if (!DialogManager.isShowing()) {
            DialogManager.getInstance().showDialog(this, resources.getString(contentRes))
        }
    }

    override fun hideLoadingDialog() {
        DialogManager.getInstance().dismisDialog()
    }

    override fun showChat(chatItems: List<ChatItemModelClass>) {
        if (mChatAdapter.itemCount < 1 && chatItems.isEmpty()) {
            showEmptyHolder()
        } else {
            hideEmptyHolder()
            mChatAdapter.addItemsAtFirst(chatItems)
        }
    }

    override fun scrollToTheLatestChatItem(fastScroll: Boolean) {
        if (mChatAdapter.itemCount > 0) {
            mSmoothScroller.targetPosition = mChatAdapter.itemCount - 1
            mSmoothScroller.fastScroll = fastScroll
            recycler_view_chat.layoutManager?.startSmoothScroll(mSmoothScroller)
        }
    }

    override fun onClickViewImage(chatItem: ChatItemModelClass) {
        val message = chatItem.msg
        if (StringUtil.isNullOrEmptyString(message)) {
            return
        }

        val arrMessage = message.split(CommonDefine.KEY_USER_SEND_IMAGE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (arrMessage.size < 2) {
            return
        }

        val arrImage = arrMessage[1].split(CommonDefine.KEY_USER_SEND_THUMBIMAGE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (arrImage.isEmpty()) {
            return
        }

        showFullImage(arrImage[0])
    }

    override fun onClickViewVideo(chatItem: ChatItemModelClass) {
        val message = chatItem.msg
        if (StringUtil.isNullOrEmptyString(message)) {
            return
        }

        val arrMessage = message.split(CommonDefine.KEY_USER_SEND_VIDEO.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (arrMessage.size < 2) {
            return
        }

        val arrVideo = arrMessage[1].split(CommonDefine.KEY_USER_SEND_THUMBIMAGE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (arrVideo.isEmpty()) {
            return
        }

        // Start Activity
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left)
        val intent = MediaViewActivity.createIntent(this, 2, arrVideo[1], arrVideo[0])
        startActivity(intent, options.toBundle())
    }

    override fun onSenderAvatarClick() {
        startActivityProfile(mChattingUserId, mChattingUserDisplayName)
    }

    override fun onItemClicked() {
        hideControlsPopup()
        Utils.hideSoftKeyboard(this)
    }

    override fun notifyNewChatItem(chatItem: ChatItemModelClass) {
        hideEmptyHolder()
        mChatAdapter.addChatItem(chatItem)
    }

    override fun notifyChatSyncToServer() {
        tv_you_are_blocked.visibility = View.GONE
    }

    override fun clearImagePhoto(bitmap: Bitmap) {
        mBitmapUtil?.clearImagePhoto(bitmap)
    }

    override fun clearComposer() {
        etInputMessage.setText("")
    }
    //endregion -------implement methods-------

    //region -------inner methods-------
    /**
     * Initialize variables which need to be initialized at the very first of this activity
     */
    private fun initVars() {
        val extras = intent.extras
        if (extras != null) {
            mChattingUserId = extras.getString(ConstantBundleKey.BUNDLE_CHAT_CHAT_WITH_USER_ID)
            mChattingUserUserName = extras.getString(ConstantBundleKey.BUNDLE_CHAT_CHAT_WITH_USER_NAME)
            mChattingUserDisplayName = extras.getString(ConstantBundleKey.BUNDLE_CHAT_CHAT_WITH_DISPLAY_NAME)
            mChattingUserAvatar = extras.getString(ConstantBundleKey.BUNDLE_CHAT_CHAT_WITH_AVATAR)
        }
        mRxPermission = RxPermissions(this)
        mSmoothScroller = MySmoothScroller(this)
    }

    private fun initControlsPopup(keyboardHeight: Int) {
        if (keyboardHeight == 0) {
            return
        }

        val viewPopup = View.inflate(this, R.layout.layout_medias_popup, null)
        mPopupControls = PopupWindow(viewPopup, ViewGroup.LayoutParams.MATCH_PARENT, POPUP_HEIGHT, false)
        initMediaButtons(viewPopup)
    }

    private fun initMediaButtons(mediaView: View) {
        val tvCamera = mediaView.findViewById(R.id.tvCamera) as CustomFontTextView
        val tvChoosePhoto = mediaView.findViewById(R.id.tvPhoto) as CustomFontTextView
        val tvVideo = mediaView.findViewById(R.id.tvVideo) as CustomFontTextView
        val tvSendGift = mediaView.findViewById(R.id.tvGift) as CustomFontTextView

        mCompositeSubscription.add(RxView.clicks(tvCamera)
                .compose(mRxPermissions.ensure(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe({ granted ->
                    if (granted!!) {
                        if (presenter.isUserBlocked()) {
                            notifyUserSuspended()
                            return@subscribe
                        }
                        Utils.hideSoftKeyboard(this@ChatActivity)
                        takePictureFromCamera()
                    }
                }, { Timber.e(it) }))

        mCompositeSubscription.add(RxView.clicks(tvChoosePhoto)
                .compose(mRxPermissions.ensure(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe({ granted ->
                    if (granted!!) {
                        if (presenter.isUserBlocked()) {
                            notifyUserSuspended()
                            return@subscribe
                        }
                        takePictureFromGallery()
                    }
                }, { Timber.e(it) }))

        mCompositeSubscription.add(RxView.clicks(tvVideo)
                .compose(mRxPermissions.ensure(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe({ granted ->
                    if (granted!!) {
                        if (presenter.isUserBlocked()) {
                            notifyUserSuspended()
                            return@subscribe
                        }
                        Utils.hideSoftKeyboard(this@ChatActivity)
                        showVideosPopUp()
                    }
                }, { Timber.e(it) }))

        etInputMessage.setOnClickListener {
            if (mPopupControls != null && mPopupControls!!.isShowing) {
                hideControlsPopup()
            }
        }

        tvSendGift.setOnClickListener {
            if (presenter.isUserBlocked()) {
                notifyUserSuspended()
                return@setOnClickListener
            }
            hideControlsPopup()
            val user = UserModel()
            user.userId = mChattingUserId
            val dialogSendGift = DialogSendGift(this, user)
            dialogSendGift.setOnDismissListener {
                recycler_view_chat.animate().translationY(0f).start()
            }
            dialogSendGift.setCompleteSendGift { ItemSend, senderTotalBean, senderTotalGold, receiverTotalBean, receiverTotalGoldFans, votingScores, topFanList, dailyTopFans ->
                presenter.completeSendGift(ItemSend)
            }

            dialogSendGift.show()

            if (mChatAdapter.itemCount >= 7) {
                recycler_view_chat.animate().translationYBy((-Utils.dpToPx(200f)).toFloat()).setStartDelay(300).start()
            } else {
                val dependedViewLoc = Utils.locateView(recycler_view_chat)
                var translationToMoveUp = 0
                if (dependedViewLoc != null) {
                    translationToMoveUp = dependedViewLoc.right - getItemHeightofListView(recycler_view_chat, mChatAdapter.itemCount) - llPrivateChatBottomContainer.height
                }
                if (translationToMoveUp < 0) {
                    recycler_view_chat.animate().translationYBy(translationToMoveUp.toFloat()).setStartDelay(300).start()
                }
            }
        }
    }

    private fun hideControlsPopup() {
        if (mPopupControls != null && mPopupControls!!.isShowing) {
            if (!mIsKeyboardVisible) {
                llChatScreenContainer.animate().translationY(0f).start()
            }
            mPopupControls!!.dismiss()
        }
    }

    private fun showControlsPopup() {
        val popupControl = mPopupControls ?: return
        if (!popupControl.isShowing) {
            if (!mIsKeyboardVisible) {
                llChatScreenContainer.animate().translationY(-POPUP_HEIGHT_WITHOUT_SHADOW.toFloat()).setDuration(100).start()
                popupControl.height = POPUP_HEIGHT_WITHOUT_SHADOW
            } else {
                popupControl.height = POPUP_HEIGHT
            }

            mCompositeSubscription.add(Observable.just(1).delay(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe({ integer ->
                val dependedViewLoc = Utils.locateView(llPrivateChatBottomContainer)
                if (dependedViewLoc != null) {
                    popupControl.showAtLocation(llPrivateChatBottomContainer, Gravity.TOP, dependedViewLoc.left, dependedViewLoc.bottom)
                }
            }, { throwable -> Timber.e(throwable.message) }))

            scrollToTheLatestChatItem(true)
        }
    }

    private fun preventDownLineFirst() {
        etInputMessage.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                Timber.e("text= %s", editable)
                val i = editable.toString().indexOf("\n")
                if (i == 0) {
                    editable.replace(i, i + 1, "")
                }
            }
        })
    }

    private fun calcKeyboardHeight() {
        llPrivateChatBottomContainer.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            llPrivateChatBottomContainer.getWindowVisibleDisplayFrame(r)
            val screenHeight = llPrivateChatBottomContainer.rootView.height
            val keyboardHeight = screenHeight - r.bottom

            if (mPreviousHeightDiff - keyboardHeight > 50) {
                hideControlsPopup()
            }
            mPreviousHeightDiff = keyboardHeight

            // IF height diff is more then 150, consider keyboard as visible.
            //                Log.d("ui", "Keyboard Height: " + keyboardHeight);
            if (keyboardHeight > 150) {
                if (!mIsKeyboardVisible) { // first action show keyboard
                    mIsKeyboardVisible = true

                    mCompositeSubscription.add(Observable.just(1)
                            .delay(100, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ scrollToTheLatestChatItem(true) }, { Timber.e(it.message) }))

                    // Creating a pop window for media-controls keyboard
                    if (mPopupControls == null) {
                        initControlsPopup(keyboardHeight)
                        AppsterApplication.mAppPreferences.setIntPreferenceData(Constants.KEYBOARD_HEIGHT, keyboardHeight)
                    } else if (mPopupControls!!.height != keyboardHeight) {
                        mPopupControls!!.height = POPUP_HEIGHT
                        AppsterApplication.mAppPreferences.setIntPreferenceData(Constants.KEYBOARD_HEIGHT, keyboardHeight)
                    }
                }
            } else {
                mIsKeyboardVisible = false
            }
        }
    }

    // To calculate the total height of all items in recycler view
    private fun getItemHeightofListView(recyclerView: RecyclerView, itemCount: Int): Int {
        var grossElementHeight = 0
        val viewCount = recycler_view_chat.childCount
        for (i in 0 until viewCount) {
            val childView = recycler_view_chat.getChildAt(i)
            childView.measure(UNBOUNDED, UNBOUNDED)
            grossElementHeight += childView.getMeasuredHeight()
        }
        return grossElementHeight
    }

    private fun showFullImage(url: String) {
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left)
        val intent = Intent(this, ViewImageActivity::class.java)
        intent.putExtra(ViewImageActivity.key_image_link, url)
        startActivity(intent, options.toBundle())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: NewMessageEvent) {
        presenter.receiveNewMessage(event)
    }

    /**
     * Show the empty holder and hide the recycler view as well
     */
    private fun showEmptyHolder() {
        tv_empty_holder.visibility = View.VISIBLE
        recycler_view_chat.visibility = View.GONE
    }

    /**
     * Hide the empty holder and show the recycler view as well
     */
    private fun hideEmptyHolder() {
        tv_empty_holder.visibility = View.GONE
        recycler_view_chat.visibility = View.VISIBLE
    }

    //endregion -------inner methods-------

    //region -------inner class-------
    companion object {

        private val POPUP_HEIGHT = Utils.dpToPx(88f) /* 80 for content and 8 for shadow*/
        private val POPUP_HEIGHT_WITHOUT_SHADOW = Utils.dpToPx(80f) /* 80 for content */
        private val UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        fun createIntent(context: Context, userId: String, userName: String, displayName: String, profilePic: String, message: Int): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(ConstantBundleKey.BUNDLE_CHAT_CHAT_WITH_USER_ID, userId)
            intent.putExtra(ConstantBundleKey.BUNDLE_CHAT_CHAT_WITH_USER_NAME, userName)
            intent.putExtra(ConstantBundleKey.BUNDLE_CHAT_CHAT_WITH_DISPLAY_NAME, displayName)
            intent.putExtra(ConstantBundleKey.BUNDLE_CHAT_CHAT_WITH_AVATAR, profilePic)
            if (message != -1) {
                intent.putExtra(ConstantBundleKey.BUNDLE_CHAT_USER_MESSAGING, message)
            }
            return intent
        }
    }

    /**
     * The default animation implementation by {@link RecyclerView#smoothScrollToPosition} looks ugly and nature
     */
    class MySmoothScroller(context: Context) : LinearSmoothScroller(context) {
        var fastScroll = true

        override fun calculateTimeForScrolling(dx: Int): Int {
            if (fastScroll) {
                return super.calculateTimeForScrolling(dx)
            }
            // the default value is quite small that makes the anim scroll too fast.
            // doubling the def value give me a pretty nice anim result
            return super.calculateTimeForScrolling(dx) * 2
        }
    }
    //endregion -------inner class-------
}
