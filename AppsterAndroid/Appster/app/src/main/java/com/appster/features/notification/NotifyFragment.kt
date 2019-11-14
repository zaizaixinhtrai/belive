package com.appster.features.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.appster.R
import com.appster.activity.BaseActivity
import com.appster.adapters.NotifyRecyclerviewAdapter
import com.appster.extensions.inflate
import com.appster.fragment.BaseFragment
import com.appster.models.NotificationItemModel
import com.appster.webservice.request_models.NotificationRequestModel
import com.apster.common.Constants
import com.apster.common.UiUtils
import com.pack.utility.CheckNetwork
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_notify_you.*
import java.util.*
import javax.inject.Inject

/**
 * Created by USER on 10/8/2015.
 */
class NotifyFragment : BaseFragment(), NotifyContract.View {


    private var notifyType = NotifyType.You
    private var arrListNotify = ArrayList<NotificationItemModel>()
    private var notifyAdapter: NotifyRecyclerviewAdapter? = null

    private var nextIndex: Int = 0
    private var isTheEnd = false
    @Inject
    lateinit var presenter: NotifyContract.UserActions

    enum class NotifyType {
        You,
        Following
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_notify_you)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notifyType = if (arguments != null) {
            NotifyType.values()[arguments!!.getInt(BUNDLE_NOTIFY_TYPE)]
        } else {
            NotifyType.You
        }
        setEventListView()
    }

    private fun setEventListView() {

        lvNotifyYou.layoutManager = LinearLayoutManager(context)
        UiUtils.setColorSwipeRefreshLayout(swiperefresh)
        lvNotifyYou.addItemDecoration(UiUtils.ListSpacingItemDecoration(resources.getDimension(R.dimen.chat_list_divider).toInt(), false))

        notifyAdapter = if (notifyType == NotifyType.You) {
            NotifyRecyclerviewAdapter(activity!!, lvNotifyYou, arrListNotify, Constants.NOTIFYCATION_TYPE_YOU)
        } else {
            NotifyRecyclerviewAdapter(activity!!, lvNotifyYou, arrListNotify, Constants.NOTIFYCATION_TYPE_FOLLOWING)
        }

        notifyAdapter?.setOnLoadMoreListener {
            if (!isTheEnd) {
                notifyAdapter?.addProgressItem()
                val handler = Handler()
                handler.postDelayed({ loadData(false) }, notifyAdapter!!.timeDelay.toLong())
            }
        }

        lvNotifyYou.adapter = notifyAdapter
        swiperefresh?.setOnRefreshListener { this.refreshData() }
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !_areLecturesLoaded && isResumed) {

            if (CheckNetwork.isNetworkAvailable(activity)) {
                loadData(true)
            } else {
                (activity as BaseActivity).utility.showMessage("", activity!!.getString(R.string.no_internet_connection), activity)
            }
            _areLecturesLoaded = true
        }

    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint && !_areLecturesLoaded) {
            userVisibleHint = true
        }
    }


    internal fun refreshData() {
        if (!CheckNetwork.isNetworkAvailable(activity)) {
            (activity as BaseActivity).toastTextWhenNoInternetConnection("")
        } else {
            nextIndex = 0
            loadData(false)
        }
    }

    private fun loadData(isReload: Boolean) {

        val notificationRequestModel = NotificationRequestModel()
        notificationRequestModel.nextId = nextIndex

        if (notifyType == NotifyType.You) {
            notificationRequestModel.notification_status = 0
        } else {
            notificationRequestModel.notification_status = 1
        }
        presenter.getNotificationList(notificationRequestModel, isReload)

    }

    override fun onHandleUiAfterApiReturn() {
        swiperefresh?.isRefreshing = false
        dismissDilaog()
        notifyAdapter?.removeProgressItem()
        notifyAdapter?.setLoaded()
    }

    override fun setDataForListView(data: List<NotificationItemModel>, isEnded: Boolean, nextIndex: Int) {

        if (this.nextIndex == 0) arrListNotify.clear()
        if (data.isNotEmpty()) arrListNotify.addAll(data)
        notifyAdapter?.notifyDataSetChanged()

        if (arrListNotify.isNotEmpty()) {
            no_data_view?.visibility = View.GONE
        } else {
            no_data_view?.visibility = View.VISIBLE
        }

        this.nextIndex = nextIndex
        isTheEnd = isEnded
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            Constants.REQUEST_VIEW_NOTIFY ->

                if (isFragmentUIActive) {
                    nextIndex = 0
                    loadData(true)
                }
            else -> {
            }
        }

    }

    override fun getViewContext(): Context? {
        return context
    }

    override fun loadError(errorMessage: String?, code: Int) {
        if (isFragmentUIActive) onErrorWebServiceCall(errorMessage, code)
    }

    override fun showProgress() {
        showDialog()
    }

    override fun hideProgress() {
        dismissDilaog()
    }

    companion object {

        private const val BUNDLE_NOTIFY_TYPE = "BUNDLE_NOTIFY_TYPE"

        @JvmStatic
        fun newInstance(notifyType: NotifyType): NotifyFragment {

            val notifyFragment = NotifyFragment()
            val args = Bundle()
            args.putInt(BUNDLE_NOTIFY_TYPE, notifyType.ordinal)
            notifyFragment.arguments = args
            return notifyFragment
        }
    }
}
