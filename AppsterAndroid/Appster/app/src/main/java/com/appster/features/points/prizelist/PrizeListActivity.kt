package com.appster.features.points.prizelist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appster.R
import com.appster.activity.BaseToolBarActivity
import com.appster.adapters.OnItemClickListener
import com.appster.features.points.Prize
import com.appster.features.points.prizelist.adapter.PrizeListAdapter
import com.appster.webview.ActivityViewWeb
import com.apster.common.CommonDefine
import com.apster.common.DialogManager
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_prize_list.*
import javax.inject.Inject

class PrizeListActivity : BaseToolBarActivity(), PrizeListContract.View, OnItemClickListener<Prize> {

    @Inject
    internal lateinit var presenter: PrizeListContract.UserActions

    private val mAdapter: PrizeListAdapter by lazy {
        PrizeListAdapter(mutableListOf(), this@PrizeListActivity)
    }


    //region-------activity life cycle-------
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val mysteryBoxId = intent.getIntExtra(CommonDefine.KEY_MYSTERY_BOX_ID, -1)
        val mysteryBoxType = intent.getIntExtra(CommonDefine.KEY_MYSTERY_BOX_TYPE, -1)
        if (mysteryBoxId == -1 || mysteryBoxType == -1) {
            // This should never happen
            finish()
            return
        }

        handleTurnoffMenuSliding()
        presenter.attachView(this)
        presenter.loadPrizeList(mysteryBoxType, mysteryBoxId)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
    //endregion -------activity life cycle-------

    //region -------inheritance methods-------

    //endregion -------inheritance methods-------

    //region -------implement methods-------
    override fun getLayoutContentId(): Int {
        return R.layout.activity_prize_list
    }

    override fun init() {
        setTopBarTile(getString(R.string.prize_list))
        goneNotify(true)
        useAppToolbarBackButton()
        eventClickBack.setOnClickListener {
            finish()
        }
        recycler_view_list.layoutManager = LinearLayoutManager(this)
        recycler_view_list.adapter = mAdapter
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun loadError(errorMessage: String?, code: Int) {
        handleError(errorMessage, code)
    }

    override fun showProgress() {
        DialogManager.getInstance().showDialog(this, resources.getString(R.string.connecting_msg), false)
    }

    override fun hideProgress() {
        DialogManager.getInstance().dismisDialog()
    }

    override fun showPrizeList(prizeList: List<Prize>) {
        mAdapter.updateItems(prizeList)
    }

    override fun onItemClick(view: View?, data: Prize?, position: Int) {
        data?.run {
            if (type == 3)
                openPrizeInfo(data.urlInfo)
        }
    }
    //endregion -------implement methods-------

    //region -------inner methods-------
    private fun openPrizeInfo(prizeUrlInfo: String) {
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left)
        ActivityCompat.startActivity(this, ActivityViewWeb.createIntent(this, prizeUrlInfo, true), options.toBundle())
    }
    //endregion -------inner methods-------


    //region -------inner class-------

    //endregion -------inner class-------
}
