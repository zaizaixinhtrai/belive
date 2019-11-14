package com.appster.features.searchScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import butterknife.ButterKnife
import com.appster.AppsterApplication
import com.appster.R
import com.appster.activity.BaseActivity
import com.appster.activity.BaseToolBarActivity
import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.EndlessDelegateAdapter
import com.appster.extensions.inflate
import com.appster.features.searchScreen.viewholders.SearchListener
import com.appster.fragment.BaseFragment
import com.appster.interfaces.OnLoadMoreListenerRecyclerView
import com.appster.models.event_bus_models.EventBusRefreshFragment
import com.appster.models.event_bus_models.EventBusRefreshSearchTab
import com.appster.search.SearchActivity
import com.apster.common.Constants
import com.apster.common.DiffCallBaseUtils
import com.apster.common.UiUtils
import com.domain.models.ExploreStreamModel
import com.pack.utility.CheckNetwork
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_search_new.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * Created by User on 9/13/2016.
 */
class FragmentSearch : BaseFragment(), SearchScreenContract.SearchView, SearchScreenOnClickListener, OnLoadMoreListenerRecyclerView {

    private var mSearchScreenAdapter: SearchScreenAdapter? = null
    private val COLUMN = 2
    private var mIsEnd = false
    @Inject
    lateinit var presenter: SearchScreenContract.UserActions
    private var searchListener: SearchListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_search_new)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UiUtils.setColorSwipeRefreshLayout(swipeRefreshLayout)
        initRecyclerView()
        bindEvents()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !_areLecturesLoaded && isResumed) {
            if (CheckNetwork.isNetworkAvailable(activity)) {
                _areLecturesLoaded = true
            } else {
                (activity as BaseActivity?)?.utility?.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection), activity)
            }
        }

        if (isVisibleToUser && isResumed) {
            (activity as BaseToolBarActivity?)?.setTxtTitleAsAppName()
        }
    }


    override fun onResume() {
        super.onResume()
        if (userVisibleHint && !_areLecturesLoaded) {
            userVisibleHint = true
        }

        if (userVisibleHint) {
            (activity as BaseToolBarActivity?)?.run {
                handleToolbar(true)
                setTxtTitleAsAppName()
                handleNewPushNotification(0)
            }
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: EventBusRefreshFragment) {

        if (isFragmentUIActive && AppsterApplication.mAppPreferences.currentTagOnHome == Constants.ID_FOR_SEARCH_FRAGMENT) {
            onRefreshData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshData(eventbusData: EventBusRefreshSearchTab) {
        onRefreshData()
    }

    private fun initRecyclerView() {
        mSearchScreenAdapter = SearchScreenAdapter(DiffCallBaseUtils(), ArrayList(), this)
        rcvExploreStream.adapter = mSearchScreenAdapter
        val manager = GridLayoutManager(context, COLUMN)
        manager.spanSizeLookup = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                return when (mSearchScreenAdapter?.getItemViewType(position)) {
                    EndlessDelegateAdapter.LOAD_MORE -> manager.spanCount
                    else -> 1
                }
            }
        }

        rcvExploreStream.layoutManager = manager
        rcvExploreStream.addItemDecoration(UiUtils.GridSpacingItemDecoration(COLUMN, 5, true))
        rcvExploreStream.setOnLoadMoreListener(this)
    }

    private fun bindEvents() {

        swipeRefreshLayout.setOnRefreshListener { onRefreshData() }
        rlt_search_user.setOnClickListener { _ ->
            activity?.run {
                val options = ActivityOptionsCompat.makeCustomAnimation(this,
                        R.anim.slide_in_up_haft_animation, R.anim.keep_view_animation)
                val intent = Intent(this, SearchActivity::class.java)
                ActivityCompat.startActivityForResult(activity!!, intent, Constants.REQUEST_SEARCH_ACTIVITY, options.toBundle())
            }
        }
    }

    fun setSearchListener(searchListener: SearchListener) {
        this.searchListener = searchListener
    }

    private fun onRefreshData() {
        if (!CheckNetwork.isNetworkAvailable(activity)) {
            (activity as BaseActivity).utility.showMessage(getString(R.string.app_name), getString(R.string.no_internet_connection), activity)
            swipeRefreshLayout?.isRefreshing = false
            return
        }
        searchListener?.onRefreshSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ButterKnife.unbind(this)
        presenter.detachView()
        searchListener = null
    }


    override fun displayStreamsRecent(streamsRecent: List<DisplayableItem>, isEndedList: Boolean) {
        if (!isFragmentUIActive) return
        mIsEnd = isEndedList
        mSearchScreenAdapter?.updateItems(streamsRecent)
        swipeRefreshLayout?.isRefreshing = false
    }

    override fun scrollTopForForceRefresh() {
        scrollTopUpRecyclerView(rcvExploreStream, false)
    }

    override fun getViewContext(): Context? {
        return context
    }

    override fun loadError(errorMessage: String, code: Int) {
        (activity as BaseActivity?)?.handleError(errorMessage, code)
        swipeRefreshLayout?.isRefreshing = false
    }

    override fun showProgress() {
        (activity as BaseActivity?)?.showDialog(activity, getString(R.string.connecting_msg))
    }

    override fun hideProgress() {
        (activity as BaseActivity?)?.dismisDialog()
    }

    fun onScrollUpListView() {
        appBarLayout?.setExpanded(true)
        scrollTopUpRecyclerView(rcvExploreStream, false)
    }

    override fun onLoadMore() {
        if (mIsEnd) {
            return
        }
        rcvExploreStream?.post { mSearchScreenAdapter?.addLoadMoreItem() }
        presenter.getStreamsRecent(false, false)
    }


    override fun onItemUserImageClicked(model: ExploreStreamModel, position: Int) {
        (context as BaseToolBarActivity?)?.openViewLiveStream(model.streamUrl, model.slug, model.streamImage, model.isRecorded)
    }

    override fun onItemUserNameClicked(model: ExploreStreamModel, position: Int) {
        if (context != null &&
                AppsterApplication.mAppPreferences.userModel.userId != model.userId.toString()) {
            (context as BaseToolBarActivity?)?.startActivityProfile(model.userId.toString(),
                    model.userName)
        }
    }

    fun getLivesInfo() {
        appBarLayout?.setExpanded(true)
        presenter.getStreamsRecent(false, true)
    }

    companion object {
        @JvmStatic
        fun newInstance(): FragmentSearch {
            return FragmentSearch()
        }
    }
}
