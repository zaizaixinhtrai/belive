package com.appster.features.notification

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentPagerAdapter
import com.appster.AppsterApplication
import com.appster.R
import com.appster.activity.BaseToolBarActivity
import com.appster.activity.UserProfileActivity
import com.appster.utility.ConstantBundleKey
import com.appster.webservice.AppsterWebServices
import com.appster.webservice.request_models.CreditsRequestModel
import com.apster.common.Constants
import com.pack.utility.CheckNetwork
import com.pack.utility.StringUtil
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_notify.*
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by User on 11/2/2015.
 */
class NotificationActivity : BaseToolBarActivity(), HasSupportFragmentInjector {

    private var isEditProfile: Boolean = false

    @Inject
    internal lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    private var mTabsPagerAdapter: TabsPagerNewsAdapter? = null

    internal var notifyYou: NotifyFragment? = null
    internal var notifyFollowing: NotifyFragment? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutContentId(): Int {
        return R.layout.fragment_notify
    }

    override fun init() {
        setFragment()
        goneNotify(true)
//        updateCreditsAfterReceiveGift()
        updateCreditsAfterReceiveGiftForLeftMenu()
    }

    private fun setFragment() {
        mTabsPagerAdapter = TabsPagerNewsAdapter(supportFragmentManager)
        pagerNotify.adapter = mTabsPagerAdapter
        tabsNotify.shouldExpand = true
        tabsNotify.setViewPager(pagerNotify)
        pagerNotify.offscreenPageLimit = 2
    }

    override fun onResume() {
        super.onResume()
        setTopBarTile(getString(R.string.notification))
        useAppToolbarBackButton()
        eventClickBack.setOnClickListener { onBackPressed() }
        handleTurnoffMenuSliding()
    }

    override fun onBackPressed() {
        if (isEditProfile) {
            val intent = Intent()
            intent.putExtra(ConstantBundleKey.BUNDLE_EDIT_ABLE_POST, true)
            setResult(RESULT_OK, intent)

        } else {
            setResult(RESULT_CANCELED)
        }

        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) return
        if (data != null) {
            isEditProfile = data.getBooleanExtra(ConstantBundleKey.BUNDLE_EDIT_ABLE_POST, true)
        }

        when (requestCode) {

            Constants.REQUEST_VIEW_NOTIFY -> {

                if (pagerNotify == null) return

                if (pagerNotify.currentItem == 0) {
                    notifyYou?.onActivityResult(requestCode, resultCode, data)
                } else if (pagerNotify.currentItem == 1) {
                    notifyFollowing?.onActivityResult(requestCode, resultCode, data)
                }
            }

            Constants.REQUEST_CODE_VIEW_USER_PROFILE -> if (data != null && data.getBooleanExtra(UserProfileActivity.ARG_USER_BLOCKED, false)) {
                refreshData()
                val intent = intent
                intent.putExtra(UserProfileActivity.ARG_USER_BLOCKED, true)
                setResult(Activity.RESULT_OK, intent)
            }

            Constants.REQUEST_MEDIA_PLAYER_STREAM -> {
                var userId: String? = ""
                if (data != null) {
                    userId = data.getStringExtra(Constants.USER_PROFILE_ID)
                    val viewMeProfile = data.getBooleanExtra(ConstantBundleKey.BUNDLE_GO_PROFILE, false)
                    if (viewMeProfile) {
                        val intent = Intent()
                        intent.putExtra(ConstantBundleKey.BUNDLE_GO_PROFILE, true)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                        return
                    }
                }
                if (!StringUtil.isNullOrEmptyString(userId))
                    startActivityProfile(userId, "")
            }
            else -> {
            }
        }
    }

    private fun refreshData() {
        notifyYou?.refreshData()
        notifyFollowing?.refreshData()
    }

    private fun updateCreditsAfterReceiveGift() {

        if (!AppsterApplication.mAppPreferences.isUserLogin || !CheckNetwork.isNetworkAvailable(this)) return

        mCompositeSubscription.add(AppsterWebServices.get().getUserCredits("Bearer " + AppsterApplication.mAppPreferences.userToken, CreditsRequestModel())
                .observeOn(Schedulers.newThread())
                .subscribe({ creditsResponseModel ->
                    creditsResponseModel?.apply {
                        if (code == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                            AppsterApplication.mAppPreferences.userModel.totalGold = data.total_gold
                            AppsterApplication.mAppPreferences.userModel.totalBean = data.total_bean
                            AppsterApplication.mAppPreferences.userModel.totalGoldFans = data.totalGoldFans

                        }
                    }
                }, { Timber.e(it) }))
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, NotificationActivity::class.java)
        }
    }

    private inner class TabsPagerNewsAdapter(fm: androidx.fragment.app.FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(index: Int): androidx.fragment.app.Fragment? {

            when (index) {
                0 -> {
                    if (notifyYou == null) {
                        notifyYou = NotifyFragment.newInstance(NotifyFragment.NotifyType.You)
                    }
                    return notifyYou
                }
                1 -> {
                    if (notifyFollowing == null) {
                        notifyFollowing = NotifyFragment.newInstance(NotifyFragment.NotifyType.Following)
                    }
                    return notifyFollowing
                }
                else -> {
                }
            }

            return null
        }

        override fun getCount(): Int {
            // get item count - equal to number of tabs
            return 2
        }

        // Returns the page title for the top indicator
        override fun getPageTitle(position: Int): CharSequence? {
            val tabs = arrayOf(getString(R.string.notification_you), getString(R.string.notification_following))
            return tabs[position]
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> {
        return fragmentDispatchingAndroidInjector
    }
}
