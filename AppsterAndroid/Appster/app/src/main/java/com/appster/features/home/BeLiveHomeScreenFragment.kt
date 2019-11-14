package com.appster.features.home

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Spanned
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.appster.AppsterApplication
import com.appster.R
import com.appster.activity.BaseActivity
import com.appster.activity.BaseToolBarActivity
import com.appster.activity.MediaPlayerActivity
import com.appster.core.adapter.DisplayableItem
import com.appster.extensions.inflate
import com.appster.extensions.then
import com.appster.features.home.triviaRanking.TopTriviaRankingActivity
import com.appster.features.home.viewholders.LiveShowMultiViewHolder
import com.appster.features.home.viewholders.LiveShowSingleViewHolder
import com.appster.features.income.masterBrainWallet.MasterBrainCashoutActivity
import com.appster.fragment.BaseFragment
import com.appster.interfaces.OnSetFollowUserListener
import com.appster.manager.ShowErrorManager
import com.appster.models.FollowUser
import com.appster.models.UserModel
import com.appster.models.event_bus_models.EventBusRefreshFragment
import com.appster.models.event_bus_models.EventBusRefreshHomeTab
import com.appster.tracking.EventTracker
import com.appster.tracking.EventTrackingName
import com.appster.utility.RxUtils
import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.response.MaintenanceModel
import com.appster.webview.ActivityViewWeb
import com.apster.common.*
import com.domain.models.*
import com.facebook.FacebookSdk.getApplicationContext
import com.pack.utility.CheckNetwork
import com.pack.utility.StringUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.belive_home_screen.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rx.Observable
import rx.Subscription
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * Created by thanhbc on 5/15/18.
 */

class BeLiveHomeScreenFragment : BaseFragment(),
        LiveShowMultiViewHolder.OnClickListener,
        BeLiveHomeContract.View,
        LiveShowSingleViewHolder.OnClickListener {


    private var isFirstTimeResumed = true//whether the fragment has just created or resumed.
    private var beLiveHomeScreenAdapter: BeLiveHomeScreenAdapter? = null
    internal var mIsRefresing = AtomicBoolean(false)
    val userModel: UserModel? by lazy { AppsterApplication.mAppPreferences.userModel }
    @Inject
    lateinit var presenter: BeLiveHomeContract.UserActions

    @Inject
    lateinit var service: AppsterWebserviceAPI
    protected var mShouldShowDialog: Boolean = false
    private var shouldRefresh: Boolean = false
    private var visibilityPercents: Float = 0F
    private var listTriviaId = SparseIntArray()
    private var getFriendSubscription: Subscription? = null
    private var getFriendNumberScheduled = false
    private var triviaPositionGetFriend: Int = -1

    companion object {
        @JvmStatic
        fun newInstance(): BeLiveHomeScreenFragment {
            return BeLiveHomeScreenFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        EventTracker.trackEvent(EventTrackingName.EVENT_HOME)
        return container?.inflate(R.layout.belive_home_screen)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseToolBarActivity?)?.apply {
            removeToolbarTitle()
        }
        if (CheckNetwork.isNetworkAvailable(activity)) {
        } else {
            (activity as BaseActivity?)?.utility?.showMessage(getString(R.string.app_name),
                    getString(R.string.no_internet_connection), activity)
            btnTryAgain?.visibility = View.VISIBLE
        }
        UiUtils.setColorSwipeRefreshLayout(swipeRefreshlayout)
        swipeRefreshlayout.setOnRefreshListener(this::onRefreshData)
        btnTryAgain.setOnClickListener {
            if (CheckNetwork.isNetworkAvailable(activity)) {
                pullData(true)
            } else {
                (activity as BaseActivity?)?.utility?.showMessage(getString(R.string.app_name),
                        getString(R.string.no_internet_connection), activity)
                btnTryAgain?.visibility = View.VISIBLE
            }
        }
        beLiveHomeScreenAdapter = BeLiveHomeScreenAdapter(DiffCallBaseUtils(), ArrayList(), this, this)
        rcvBeLiveShows.adapter = beLiveHomeScreenAdapter
        val startSnapHelper = StartSnapHelper()
        startSnapHelper.attachToRecyclerView(rcvBeLiveShows)
        handleVisibleMaintenanceMessage(AppsterApplication.mAppPreferences.maintenanceModel)
        lavMoreItem.setOnClickListener {
            val layoutManager = rcvBeLiveShows?.layoutManager as LinearLayoutManager
            val nextPos = layoutManager.findLastVisibleItemPosition()
            rcvBeLiveShows.smoothScrollToPosition(nextPos)
        }
        rcvBeLiveShows.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstPosition = layoutManager.findFirstVisibleItemPosition()
                val lastPosition = layoutManager.findLastVisibleItemPosition()

                val globalVisibleRect = Rect()
                val itemVisibleRect = Rect()

                recyclerView.getGlobalVisibleRect(globalVisibleRect)

//                for (pos in firstPosition..firstPosition) {
                val v = layoutManager.findViewByPosition(firstPosition)
                if (v != null && v.height > 0 && v.getGlobalVisibleRect(itemVisibleRect)) {
                    val visibilityExtent = if (itemVisibleRect.bottom >= globalVisibleRect.bottom) {
                        val visibleHeight = globalVisibleRect.bottom - itemVisibleRect.top
                        Math.min(visibleHeight.toFloat() / v.height, 1f)
                    } else {
                        val visibleHeight = itemVisibleRect.bottom - globalVisibleRect.top
                        Math.min(visibleHeight.toFloat() / v.height, 1f)
                    }
                    // if percentage is needed...
                    val percentage = visibilityExtent * 100
                    visibilityPercents = percentage
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(firstPosition)
//                        viewHolder.setVisibilityExtent(visibilityExtent)
                    if (viewHolder is LiveShowMultiViewHolder) {
                        viewHolder.showOptions((visibilityExtent >= 0.25F) then View.VISIBLE
                                ?: View.INVISIBLE)
                        if (percentage == 100F) {
                            viewHolder.showItem?.apply {
                                checkStatus(showId)
                                triviaPositionGetFriend = viewHolder.adapterPosition
                                if (isTrivia && streamId != null) addTriviaIdToList(showId, streamId)
                            }
                        }
                    }

                    if (viewHolder is LiveShowSingleViewHolder) {
                        viewHolder.showOptions((visibilityExtent >= 0.25F) then View.VISIBLE
                                ?: View.INVISIBLE)
                        if (percentage == 100F) {
                            viewHolder.showItem?.apply {
                                checkStatus(showId)
                                triviaPositionGetFriend = viewHolder.adapterPosition
                                if (isTrivia && streamId != null) addTriviaIdToList(showId, streamId)
                            }

                        }
                    }

                    val endHasBeenReached = !recyclerView.canScrollVertically(1)
                    (endHasBeenReached && percentage == 100F) then dismissArrowAnim()
                            ?: displayArrowAnim()
                    Timber.e("VisibilityPercents:$percentage")
                    if (percentage != 100F) {
                        hideFriendViewMasterBrain()
                        cancelGetFriendTimer()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
//                    RecyclerView.SCROLL_STATE_IDLE -> Timber.e("The RecyclerView is not scrolling")

                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        Timber.e("Scrolling now")
                        if (visibilityPercents < 100) {
                            hideFriendViewMasterBrain()
                            cancelGetFriendTimer()
                        }
                    }
//                    RecyclerView.SCROLL_STATE_SETTLING -> Timber.e("Scroll Settling")
                }
            }
        })
    }

    private fun addTriviaIdToList(showId: Int, streamId: Int) {
        if (listTriviaId.indexOfKey(showId) < 0) listTriviaId.put(showId, streamId)
    }

    private fun handleShowFriendNumber(streamId: Int?) {
        streamId?.let { presenter.getFriendNumber(streamId) }
    }

    private fun displayFriendViewMasterBrain(massage: Spanned) {

        if (parentContainer.visibility == View.INVISIBLE || parentContainer.visibility == View.GONE) {
            if (massage.isNotEmpty()) tvWatchingNumber.text = massage
            parentContainer.visibility = View.VISIBLE
            val slideLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.watching_master_brain_slide_in_right)
            parentContainer.startAnimation(slideLeft)
        } else {
            if (massage.isNotEmpty()) tvWatchingNumber.text = massage
        }
    }

    private fun hideFriendViewMasterBrain() {
        if (parentContainer.visibility == View.VISIBLE) {
//            val slideLeft = AnimationUtils.loadAnimation(getApplicationContext(),
//                    R.anim.watching_master_brain_slide_out_left)
//            parentContainer.startAnimation(slideLeft)
            parentContainer.visibility = View.INVISIBLE
        }
    }

    private fun displayArrowAnim() {
        if (!lavMoreItem.isAnimating) {
            lavMoreItem.resumeAnimation()
        }
        lavMoreItem.visibility = View.VISIBLE
    }

    private fun dismissArrowAnim() {
        if (lavMoreItem.isAnimating) {
            lavMoreItem.pauseAnimation()
        }
        lavMoreItem.visibility = View.GONE
    }

    fun onRefreshData() {
        if (!CheckNetwork.isNetworkAvailable(activity)) {
            (activity as BaseActivity).utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection),
                    activity)
            swipeRefreshlayout.isRefreshing = false
            return
        }
        pullData(false)
        hideFriendViewMasterBrain()
        cancelGetFriendTimer()
    }

    private fun pullData(isShowDialog: Boolean) {
        if (mIsRefresing.get()) return
        mIsRefresing.set(true)
        getCurrentEvent(isShowDialog)

    }

    private fun getCurrentEvent(isShowDialog: Boolean) {
        mShouldShowDialog = isShowDialog
        if (isShowDialog && !AppsterApplication.mAppPreferences.flagNewlyUser) {
            showProgress()
        }
        presenter.getLiveShows()
    }

    override fun onOptionClicked(option: LiveShowOption) {
        context?.let {
            val options = ActivityOptionsCompat.makeCustomAnimation(it, R.anim.push_in_to_right, R.anim.push_in_to_left)
            when (option.actionType) {
                1 -> option.action?.apply {
                    var url = this
                    if (option.optionType == 3 && userModel != null) {
                        url += "&refCode=${userModel?.referralId}"
                    }
                    val intent = ActivityViewWeb.createIntent(it, url, false)
                    ActivityCompat.startActivity(it, intent, options.toBundle())
                }
                else -> {
                    when (option.optionType) {
                        2 -> {
                            val intent = TopTriviaRankingActivity.createIntent(it, option.triviaCountryCode)
                            ActivityCompat.startActivity(it, intent, options.toBundle())
                        }
                        else -> Unit
                    }
                }

            }
        }
    }

    override fun onStampBalanceClicked(stampBalance: StampBalance) {
        context?.let {
            if (stampBalance.cashoutUrl != null) {
                val options = ActivityOptionsCompat.makeCustomAnimation(it, R.anim.push_in_to_right, R.anim.push_in_to_left)
                val intent = ActivityViewWeb.createIntent(it, stampBalance.cashoutUrl, false)
                ActivityCompat.startActivity(it, intent, options.toBundle())
                shouldRefresh = true
            }
        }
    }

    override fun onBalanceClicked(balance: Balance) {
        context?.let {
            val options = ActivityOptionsCompat.makeCustomAnimation(it, R.anim.push_in_to_right, R.anim.push_in_to_left)
            val intent = MasterBrainCashoutActivity.createIntent(it, balance.walletGroup)
            ActivityCompat.startActivity(it, intent, options.toBundle())
        }
    }

    override fun onFollowClicked(item: LiveShowModel) {
        context?.let {
            val followUser = FollowUser(it.applicationContext, item.userId.toString(), true)
            followUser.execute()
            followUser.setSetFollowUserListener(object : OnSetFollowUserListener {
                override fun onFinishFollow(isFollow: Boolean) {
                    NavigationUtil.gotoProfileScreen(it as BaseActivity, item.userName)
                }

                override fun onError(errorCode: Int, message: String) {
                    when (errorCode) {
                        ShowErrorManager.pass_word_required -> handleFollowWithPassword(item.userId.toString(), item.userName)
                        ShowErrorManager.follow_password_incorrect -> (activity as BaseToolBarActivity?)?.handleError(message, errorCode)
                        else -> NavigationUtil.gotoProfileScreen(it as BaseActivity, item.userName)
                    }
                }
            })

        }

    }

    private fun handleFollowWithPassword(userId: String, userName: String) {
        context?.let {
            DialogbeLiveConfirmation.Builder()
                    .title(getString(R.string.enter_password))
                    .setPasswordBox(true)
                    .confirmText(getString(R.string.verify))
                    .onEditTextValue { value -> followUserWithPassword(userId, userName, value) }
                    .build().show(it)
        }

    }

    private fun followUserWithPassword(userId: String, userName: String, pass: String) {
        context?.let {
            val followUser = FollowUser(it.applicationContext, userId, true)
            followUser.executeFollowWithPass(pass)
            followUser.setSetFollowUserListener(object : OnSetFollowUserListener {
                override fun onFinishFollow(isFollow: Boolean) {
                    NavigationUtil.gotoProfileScreen(it as BaseActivity, userName)
                }

                override fun onError(errorCode: Int, message: String) {
                    when (errorCode) {
                        ShowErrorManager.pass_word_required -> handleFollowWithPassword(userId, userName)
                        ShowErrorManager.follow_password_incorrect -> (activity as BaseToolBarActivity?)?.handleError(message, errorCode)
                        else -> NavigationUtil.gotoProfileScreen(it as BaseActivity, userName)
                    }
                }
            })
        }
    }

    override fun onShowActionButtonClicked(item: LiveShowModel) {
        context?.let {
            when (item.showStatus) {
                ShowStatus.PLAY, ShowStatus.WATCHING -> {
                    val playbackIntent = MediaPlayerActivity.createIntent(activity, item.slug, "", false, null)
                    activity?.startActivityForResult(playbackIntent, Constants.REQUEST_MEDIA_PLAYER_STREAM)
                }

                ShowStatus.FINISHED -> {
                    hideFriendViewMasterBrain()
                    cancelGetFriendTimer()
                }
                else -> Unit
            }
        }
        Timber.e(item.toString())
    }

    override fun checkStatus(showId: Int) {
        if (isFragmentUIActive) presenter.checkShowStatus(showId)
    }

    var subscription: Subscription? = null
    override fun checkShowWaitingTime(showId: Int, waitingTime: Int, showStatus: Int, streamId: Int?) {
        cancelCheckStatusTimer()
        subscription = Observable.just(showId).delay(waitingTime.toLong(), TimeUnit.SECONDS)
                .filter { _ -> isFragmentUIActive }
                .subscribe(this::checkStatus)

        when (showStatus) {
            ShowStatus.PLAY, ShowStatus.WATCHING -> {
                val position = listTriviaId.indexOfKey(showId)
                if (position >= 0 && streamId != null) {
                    handleShowFriendNumber(streamId)
                    listTriviaId.removeAt(position)
                    listTriviaId.put(showId, streamId)
                }
            }
            else -> Unit
        }

    }

    override fun onFollowClicked(item: LiveShowLastModel) {
        context?.let {
            val followUser = FollowUser(it.applicationContext, item.userId.toString(), true)
            followUser.execute()
            followUser.setSetFollowUserListener(object : OnSetFollowUserListener {
                override fun onFinishFollow(isFollow: Boolean) {
                    NavigationUtil.gotoProfileScreen(it as BaseActivity, item.userName)
                }

                override fun onError(errorCode: Int, message: String) {
                    when (errorCode) {
                        ShowErrorManager.pass_word_required -> handleFollowWithPassword(item.userId.toString(), item.userName)
                        ShowErrorManager.follow_password_incorrect -> (activity as BaseToolBarActivity?)?.handleError(message, errorCode)
                        else -> NavigationUtil.gotoProfileScreen(it as BaseActivity, item.userName)
                    }
                }
            })

        }
    }

    override fun onShowActionButtonClicked(item: LiveShowLastModel) {
        context?.let {
            when (item.showStatus) {
                ShowStatus.PLAY, ShowStatus.WATCHING -> {
                    val playbackIntent = MediaPlayerActivity.createIntent(activity, item.slug, "", false, null)
                    activity?.startActivityForResult(playbackIntent, Constants.REQUEST_MEDIA_PLAYER_STREAM)
                }
                else -> Unit
            }
        }
        Timber.e(item.toString())
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser && isResumed) {
            EventBus.getDefault().post(EventBusRefreshHomeTab())
            if (userVisibleHint) {
                (activity as BaseToolBarActivity?)?.apply {
                    removeToolbarTitle()
                    handleToolbar(true)
                    handleNewPushNotification(0)
                }
            }
        }

        if (!isVisibleToUser && isResumed) {
            hideFriendViewMasterBrain()
            cancelGetFriendTimer()
        }

        Timber.e("isVisibleToUser= %s", isVisibleToUser)
    }

    override fun displayFriendNumber(model: LiveShowFriendNumberModel, streamId: Int) {
        if (triviaPositionGetFriend == getRecyclerViewPositionVisible()) {
            if (model.massage.isNullOrEmpty())
                hideFriendViewMasterBrain()
            else
                displayFriendViewMasterBrain(StringUtil.fromHtml(model.massage))
        }
        if (!getFriendNumberScheduled) repeatGetFriendNumber(streamId, model)
    }

    private fun getRecyclerViewPositionVisible(): Int {
        val layoutManager = rcvBeLiveShows.getLayoutManager() as LinearLayoutManager
        val position = layoutManager.findFirstCompletelyVisibleItemPosition()
        Timber.e("findLastVisibleItemPosition %s", position)
        Timber.e("positionCurrent %s", triviaPositionGetFriend)
        return position
    }

    private fun repeatGetFriendNumber(streamId: Int, model: LiveShowFriendNumberModel) {

        cancelGetFriendTimer()
        model.waitingTimeSec?.let {
            getFriendSubscription = Observable.interval(model.waitingTimeSec, TimeUnit.SECONDS)
                    .filter { _ -> isFragmentUIActive }
                    .subscribe({
                        presenter.getFriendNumber(streamId)
                    }, {
                        Timber.e(it)
                    })
        }
        getFriendNumberScheduled = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(model: MaintenanceModel) {
        handleVisibleMaintenanceMessage(model)
    }

    private fun handleVisibleMaintenanceMessage(model: MaintenanceModel?) {
        if (isFragmentUIActive) {
            when (model?.maintenanceMode) {

                Constants.MAINTENANCE_MODE_STOP -> tvMaintenanceMessage?.visibility = View.GONE
                Constants.MAINTENANCE_MODE_STANDBY -> {
                    tvMaintenanceMessage?.text = model.message
                    tvMaintenanceMessage?.visibility = View.VISIBLE
                }

                Constants.MAINTENANCE_MODE_START -> {
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        //refresh the home screen every time it is resumed.
        //home button tab clicked
        //resume from phone's home button
        if (!isFirstTimeResumed) {
            if (!AppsterApplication.mAppPreferences.isNotNeedRefreshHome) {
                EventBus.getDefault().post(EventBusRefreshHomeTab())
            }
        } else {
            isFirstTimeResumed = false
        }
        if (userVisibleHint && !_areLecturesLoaded) {
            userVisibleHint = true
        }

        if (shouldRefresh) {
            onRefreshData()
            shouldRefresh = false
        }
        AppsterApplication.mAppPreferences.isNotNeedRefreshHome = true
    }

    override fun onPause() {
        cancelCheckStatusTimer()
        cancelGetFriendTimer()
        super.onPause()
    }


    fun cancelCheckStatusTimer() {
        RxUtils.unsubscribeIfNotNull(subscription)
    }

    fun cancelGetFriendTimer() {
        RxUtils.unsubscribeIfNotNull(getFriendSubscription)
        getFriendNumberScheduled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    fun refreshToHotTab() {

    }

    fun refreshHome() {

    }

    fun onScrollUpListView() {
        scrollTopUpRecyclerView(rcvBeLiveShows, true)
    }

    override fun displayLiveShows(shows: List<DisplayableItem>) {
        if (isFragmentUIActive) {
            beLiveHomeScreenAdapter?.updateItems(shows.toMutableList())
            swipeRefreshlayout?.isRefreshing = false
            mIsRefresing.set(false)
            btnTryAgain?.visibility = View.GONE
        }
//        isMultiShows = false
    }

    override fun checkVisibleShowStatus() {
        rcvBeLiveShows.post {
            val layoutManager = rcvBeLiveShows?.layoutManager as LinearLayoutManager
            val nextPos = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (nextPos != NO_POSITION) {
                val viewHolder = rcvBeLiveShows?.findViewHolderForAdapterPosition(nextPos)
                when (viewHolder) {
                    is LiveShowSingleViewHolder -> {
                        viewHolder.showItem?.let {
                            checkStatus(it.showId)
                        }
                    }
                    is LiveShowMultiViewHolder -> {
                        viewHolder.showItem?.let {
                            checkStatus(it.showId)
                        }
                    }
                    else -> Unit
                }
            }
            rcvBeLiveShows.canScrollVertically(1) then displayArrowAnim()
                    ?: dismissArrowAnim()
        }
    }

    override fun getViewContext(): Context? = context

    override fun loadError(errorMessage: String?, code: Int) {
        if (isFragmentUIActive) {
            onErrorWebServiceCall(errorMessage, code)
            btnTryAgain?.visibility = View.VISIBLE
            swipeRefreshlayout?.isRefreshing = false
            mIsRefresing.set(false)
        }
    }

    override fun showProgress() {
        if (mShouldShowDialog) showDialog()
    }

    override fun hideProgress() {
        if (mShouldShowDialog) dismissDilaog()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: EventBusRefreshFragment) {
        if (AppsterApplication.mAppPreferences.currentTagOnHome == 0) {
        } else {
            shouldRefresh = true
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshFragment(eventBusdata: EventBusRefreshHomeTab) {
        if (isFragmentUIActive) {
            dismissArrowAnim()
            onRefreshData()
        } else {
            shouldRefresh = true
        }
//        Timber.d("refresh date by event bus $typeFeed")
    }

}
