package com.appster.features.points

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.appster.AppsterApplication
import com.appster.R
import com.appster.activity.BaseActivity
import com.appster.activity.BaseToolBarActivity
import com.appster.adapters.OnItemClickListener
import com.appster.core.adapter.DisplayableItem
import com.appster.features.home.dialog.DailyTreatRevealPrizeDialog
import com.appster.features.points.adapter.PointsAdapter
import com.appster.features.points.adapter.holder.MysteryHolder
import com.appster.features.points.prizelist.PrizeListActivity
import com.appster.fragment.BaseFragment
import com.appster.main.MainActivity
import com.appster.tracking.EventTracker
import com.appster.tracking.EventTrackingName
import com.appster.utility.AppsterUtility
import com.appster.webview.ActivityViewWeb
import com.apster.common.CommonDefine
import com.apster.common.DialogManager
import com.apster.common.DialogbeLiveConfirmation
import com.apster.common.UiUtils
import com.data.entity.MysteryBoxEntity
import com.domain.models.PrizeCollectModel
import com.domain.models.TreatCollectModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_points.*
import java.text.DecimalFormat
import javax.inject.Inject

/**
 *  Created by DatTN on 10/23/2018
 */
class PointsFragment : BaseFragment(),
        PointsContract.View,
        OnItemClickListener<MysteryBox>,
        PointsAdapter.OnPrizeItemClicked {

    @Inject
    internal lateinit var presenter: PointsContract.UserActions
    private var mPointInfoUrl: String? = null

    private val mPointAdapter: PointsAdapter by lazy {
        PointsAdapter(mutableListOf(), this, this)
    }

    //region -------lifecycle methods-------
    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_points, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        presenter.attachView(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (userVisibleHint)
            getData(true)
    }

    //endregion -------lifecycle methods-------

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            getData(true)
        } else {
            mPointAdapter.onViewInVisible()
        }
    }
    //endregion -------inheritance methods-------

    //region -------implement methods-------

    private fun getData(isShowLoading: Boolean) {
        if (isShowLoading) tv_total_point.text = AppsterApplication.mAppPreferences.userModel.points.toString()
        presenter.apply {
            loadMysteryBoxes(isShowLoading)
            loadUserPrizeCount()
        }
        (activity as? BaseToolBarActivity)?.apply {
            removeToolbarTitle()
        }
    }

    override fun getViewContext(): Context? {
        return activity
    }

    override fun loadError(errorMessage: String?, code: Int) {
        activity?.let { (activity as BaseActivity?)?.handleError(errorMessage, code) }
    }

    override fun showProgress() {
        context?.let {
            DialogManager.getInstance().showDialog(it, resources.getString(R.string.connecting_msg), false)
        }
    }

    override fun hideProgress() {
        context?.let {
            DialogManager.getInstance().dismisDialog()
            refreshPoints?.isRefreshing = false
        }
    }

    override fun showMysteryBoxes(mysteryBoxes: List<DisplayableItem>) {
        mPointAdapter.updateItems(mysteryBoxes)
    }

    override fun onDailyBonusCountUpdated(countDown: Int) {
        mPointAdapter.onDailyBonusCountDownUpdated(countDown)
        (activity as? MainActivity)?.apply {
            onVisiblePointsDot(countDown <= 0)
        }
    }

    override fun onUserPrizeUpdated(numOfPrize: Int) {
        (activity as? MainActivity)?.apply {
            secondaryBadgeView.textNumber = numOfPrize.toString()
        }
    }

    override fun onUserPointUpdated(numOfPoint: Int, infoUrl: String?) {
        val myFormatter = DecimalFormat("#,###")
        tv_total_point.text = myFormatter.format(numOfPoint).replace(".", ",")
        if (infoUrl != null) {
            mPointInfoUrl = infoUrl
        }
    }

    override fun onItemClick(view: View?, data: MysteryBox?, position: Int) {
        if (view == null) {
            return
        }
        data?.let {
            when (view.id) {
                R.id.tv_view_all_prize -> {
                    if (data is DailyBonus) {
                        openPrizeList(MysteryBoxEntity.TYPE_DAILY_BONUS, data.id)
                    } else {
                        openPrizeList(MysteryBoxEntity.TYPE_MYSTERY_BOX, data.id)
                    }
                }
                R.id.lo_price -> {
                    if (data is DailyBonus) {
                        presenter.openDailyBonus()
                        EventTracker.trackPointsOpenBox(AppsterApplication.mAppPreferences.userModel?.userId, EventTrackingName.EVENT_POINT_FREE_BOX)

                    } else {
                        // open premium box
                        presenter.openMysteryBox(data.id)
                        EventTracker.trackPointsOpenBox(AppsterApplication.mAppPreferences.userModel?.userId, EventTrackingName.EVENT_POINT_PREMIUM_BOX)
                    }
                }
                else -> {
                }
            }
        }

        AppsterUtility.temporaryLockView(view)
    }


    private fun openPrizeList(boxType: Int, boxId: Int) {
        val intent = Intent(activity, PrizeListActivity::class.java)
        intent.putExtra(CommonDefine.KEY_MYSTERY_BOX_ID, boxId)
        intent.putExtra(CommonDefine.KEY_MYSTERY_BOX_TYPE, boxType)
        startActivity(intent)
    }

    override fun onPrizeItemClicked(viewHolder: MysteryHolder, prize: Prize) {

    }

    override fun onDailyBonusCollected(model: TreatCollectModel) {
        openDailyTreatPrize(model)
        presenter.loadUserPrizeCount()
    }

    override fun onMysteryBoxOpened(model: PrizeCollectModel) {
        openDailyTreatPrize(model)
        presenter.loadUserPrizeCount()
    }

    override fun showNotEnoughtPointDialog() {
        val dialog = DialogbeLiveConfirmation.Builder()
                .confirmText(getString(R.string.how_mark))
                .cancelText(getString(R.string.ok))
                .message(getString(R.string.need_more_point_content))
                .title(getString(R.string.need_more_point))
                .singleAction(false)
                .onViewClickedCallback {
                    openPointInfoScreen()
                }
                .build()
        dialog.show(context)

    }
//endregion -------implement methods-------

//region -------inner methods-------

    private fun openDailyTreatPrize(treatModel: TreatCollectModel) {
        val dialog = DailyTreatRevealPrizeDialog.newInstance(treatModel)
        dialog.show(fragmentManager, DailyTreatRevealPrizeDialog::class.java.name)
        dialog.setDaylyTreatPrizeListner {
            activity?.let {
                if (activity is BaseToolBarActivity) {
                    (activity as BaseToolBarActivity).openPrizeBag()
                }
            }
        }
    }

    private fun initViews() {

        ib_point_info.setOnClickListener {
            openPointInfoScreen()
        }
        recycler_view.layoutManager = LinearLayoutManager(activity)
        recycler_view.adapter = mPointAdapter
        UiUtils.setColorSwipeRefreshLayout(refreshPoints)
        refreshPoints.setOnRefreshListener { getData(false) }
    }

    private fun openPointInfoScreen() {
        context?.run {
            if (!TextUtils.isEmpty(mPointInfoUrl)) {
                startActivity(ActivityViewWeb.createIntent(context!!, mPointInfoUrl!!, true))
            }
        }
    }
//endregion -------inner methods-------


    //region -------inner class-------
    companion object {
        @JvmStatic
        fun newInstance(): PointsFragment {
            return PointsFragment()
        }
    }
//endregion -------inner class-------

}