package com.appster.features.messages

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appster.R
import com.appster.activity.BaseToolBarActivity
import com.appster.activity.UserProfileActivity
import com.appster.adapters.OnItemClickListener
import com.appster.features.messages.adapter.MessageListAdapter
import com.appster.features.messages.chat.ChatActivity
import com.appster.interfaces.OnLoadMoreListenerRecyclerView
import com.appster.layout.recyclerSwipeUtil.Attributes
import com.appster.models.event_bus_models.NewMessageEvent
import com.apster.common.Constants
import com.apster.common.DialogManager
import com.apster.common.DialogbeLiveConfirmation
import com.apster.common.UiUtils
import com.pack.utility.DialogInfoUtility
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_message_list.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class MessageListActivity : BaseToolBarActivity(),
        MessageListContract.View,
        OnItemClickListener<MessageItemModelClass>, OnLoadMoreListenerRecyclerView {

    private companion object {
        val LOAD_MORE_DELAY = 2000L
    }

    @Inject
    internal lateinit var presenter: MessageListContract.UserActions

    // Currently, this adapter is never modified, so it is better to make it val (instead of var)
    private val mMessageAdapter: MessageListAdapter by lazy {
        MessageListAdapter(mClickEvent = this)
    }
    private var mUtility: DialogInfoUtility? = null

    /**
     * Substitute for our onScrollListener for RecyclerView
     */
    private var onScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            mMessageAdapter.closeAllItems()
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            mMessageAdapter.closeAllItems()
        }
    }

    //region -------activity life cycle-------
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setTopBarTile(getString(R.string.messages))
        useAppToolbarBackButton()
        eventClickBack.setOnClickListener { onBackPressed() }
        handleTurnoffMenuSliding()
        goneNotify(true)

        presenter.attachView(this)
        presenter.loadMessages(true)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        // Event bus unregister/ register is handled by the BaseToolBarActivity already
    }
    //endregion -------activity life cycle-------

    //region -------inheritance methods-------
    override fun getLayoutContentId(): Int {
        return R.layout.activity_message_list
    }

    override fun init() {
        mUtility = DialogInfoUtility()

        UiUtils.setColorSwipeRefreshLayout(lo_swipe_refresh)
        lo_swipe_refresh.setOnRefreshListener { presenter.refreshMessages() }

        recycler_view_list.layoutManager = LinearLayoutManager(this)
        val space = resources.getDimension(R.dimen.chat_list_divider).toInt()
        recycler_view_list.addItemDecoration(UiUtils.ListSpacingItemDecoration(space, false))
        recycler_view_list.addOnScrollListener(onScrollListener)

        mMessageAdapter.mode = Attributes.Mode.Single
        recycler_view_list.adapter = mMessageAdapter
        recycler_view_list.setOnLoadMoreListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        when (requestCode) {
            // From The Ngoc.
            // The current behaviour: Always refresh the message list when user return from the Chat screen
            Constants.CONVERSATION_REQUEST -> presenter.loadMessages(true)
            Constants.REQUEST_CODE_VIEW_USER_PROFILE -> {
            }
        }

        if (data != null && data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
            presenter.refreshMessages()
            intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true)
            setResult(Activity.RESULT_OK, intent)
        }
    }
    //endregion -------inheritance methods-------

    //region -------implement methods-------
    override fun getViewContext(): Context {
        return this
    }

    override fun loadError(errorMessage: String?, code: Int) {
        handleError(errorMessage, code)
    }

    override fun showProgress() {
        mMessageAdapter.addLoadMoreItem()
    }

    override fun hideProgress() {
        mMessageAdapter.removeLoadingItem()
    }

    override fun showDialog(titleRes: Int, contentRes: Int) {
        mUtility?.showMessage(getString(titleRes), getString(contentRes), this)
    }

    override fun showLoadingDialog(contentRes: Int) {
        if (!DialogManager.isShowing()) {
            DialogManager.getInstance().showDialog(this, getString(contentRes))
        }
    }

    override fun hideLoadingDialog() {
        DialogManager.getInstance().dismisDialog()
    }

    override fun showMessages(messages: List<MessageItemModelClass>, clearOldData: Boolean) {
        if (clearOldData) {
            mMessageAdapter.clearItemOnly()
        }
        mMessageAdapter.updateItems(messages)
    }

    override fun showEmptyView() {
        tv_empty_holder.visibility = View.VISIBLE
    }

    override fun hideEmptyView() {
        tv_empty_holder.visibility = View.GONE
    }

    override fun hideRefreshLayoutIfNeeded() {
        if (lo_swipe_refresh.isRefreshing) {
            lo_swipe_refresh.isRefreshing = false
        }
    }

    override fun onItemClick(view: View?, data: MessageItemModelClass?, position: Int) {
        val viewId = view?.id ?: return
        when (viewId) {
            R.id.iv_user_mage -> {
                mMessageAdapter.closeAllItems()
                startActivityProfile(data?.msg_user_id, data?.msg_display_name)
            }
            R.id.lo_perform_history_chat -> presenter.openMessage(data!!)
            R.id.lo_time -> presenter.openMessage(data!!)
            R.id.btn_delete -> {
                if ((mMessageAdapter.itemCount) <= 0) {
                    return
                }
                showDeleteMessageConfirmDialog(data!!, position)
            }
        }
    }

    override fun openMessage(messageItemModelClass: MessageItemModelClass) {
        val options = ActivityOptionsCompat
                .makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left)
        val intent = ChatActivity.createIntent(this, messageItemModelClass.msg_user_id,
                messageItemModelClass.msg_user_name,
                messageItemModelClass.msg_display_name,
                messageItemModelClass.msg_profile_pic, -1)
        // the startActivityForResult is restricted to its library group, so we stick with this
        ActivityCompat.startActivityForResult(this, intent, Constants.CONVERSATION_REQUEST, options.toBundle())
    }

    override fun notifyMessageUpdated(messageItemModelClass: MessageItemModelClass) {
        mMessageAdapter.closeAllItems()
        mMessageAdapter.notifyDatasetChanged()
    }

    override fun notifyMessageDeleted(messageItemModelClass: MessageItemModelClass, position: Int) {
        mMessageAdapter.removeItemAt(position)
    }

    override fun notifyDataLoaded() {
        recycler_view_list.setLoading(false)
    }

    override fun onLoadMore() {
        presenter.loadMoreMessages(LOAD_MORE_DELAY)
    }
    //endregion -------implement methods-------

    //region -------inner methods-------
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: NewMessageEvent) {
        presenter.receiveNewMessage(event)
    }

    private fun showDeleteMessageConfirmDialog(messageItemModelClass: MessageItemModelClass, position: Int) {
        DialogbeLiveConfirmation.Builder().title(getString(R.string.app_name))
                .message(getString(R.string.message_do_you_want_to_delete_message))
                .confirmText(getString(R.string.btn_text_ok))
                .singleAction(false)
                .onConfirmClicked {
                    mMessageAdapter.closeAllItems()
                    presenter.deleteMessage(messageItemModelClass, position)
                }
                .build().show(this)
    }
    //endregion -------inner methods-------

    //region -------inner class-------
    //endregion -------inner class-------
}
