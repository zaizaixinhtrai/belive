package com.appster.features.home.triviaRanking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager.widget.PagerAdapter
import com.appster.AppsterApplication
import com.appster.R
import com.appster.activity.UserProfileActivity
import com.appster.features.mvpbase.RecyclerItemCallBack
import com.apster.common.Constants
import com.domain.models.WinnerModel
import kotlinx.android.synthetic.main.trivia_top_ranking_layout.*

/**
 * Created by thanhbc on 5/18/18.
 */
class TopTriviaRankingActivity : AppCompatActivity(),
        RecyclerItemCallBack<WinnerModel> {

    private var countryCode: String? = null

    companion object {
        private const val BUNDLE_TRIVIA_COUNTRY_CODE = "country_code"
        @JvmStatic
        fun createIntent(context: Context, triviaCountryCode: String?): Intent {
            val intent = Intent(context, TopTriviaRankingActivity::class.java)
            triviaCountryCode?.let { intent.putExtra(BUNDLE_TRIVIA_COUNTRY_CODE, triviaCountryCode) }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trivia_top_ranking_layout)
        getIntent()?.let { countryCode = intent.getStringExtra(BUNDLE_TRIVIA_COUNTRY_CODE) }
        setPager()
        ivClose.setOnClickListener { finish() }
        countryCode?.let {
            if (countryCode.equals(Constants.COUNTRY_CODE_VN_FROM_SERVER_RETURN)) {
//                tvWinnerText.text = getString(R.string.top_winner_title_vi)
                ivThisWeek.setImageResource(R.drawable.trivia_this_week_selector_vi)
                ivAllTime.setImageResource(R.drawable.trivia_all_time_selector_vi)
            }
        }
    }

    private fun setPager() {
        rankingTabStrip.setAllCaps(true)
        rankingTabStrip.shouldExpand = true
        val pagerAdapter = RankingPagerAdapter()
        rankingViewPager.adapter = pagerAdapter
        rankingTabStrip.setViewPager(rankingViewPager)
        ivThisWeek?.isSelected = true
        ivThisWeek?.setOnClickListener { rankingViewPager.setCurrentItem(0, true) }
        ivAllTime?.setOnClickListener { rankingViewPager.setCurrentItem(1, true) }
        rankingViewPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                ivThisWeek?.isSelected = position == 0
                ivAllTime?.isSelected = position == 1
            }


        })
    }

    override fun onItemClicked(item: WinnerModel?, position: Int) {
        if (AppsterApplication.mAppPreferences.isUserLogin && AppsterApplication.mAppPreferences.userModel.userId != item?.userId.toString()) {
            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left)
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(Constants.USER_PROFILE_ID, item?.userId)
            intent.putExtra(Constants.ARG_USER_NAME, item?.userName)
            intent.putExtra(Constants.USER_PROFILE_DISPLAYNAME, item?.displayName)
            startActivity(intent, options.toBundle())
        }
    }

    internal inner class RankingPagerAdapter : PagerAdapter() {


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item = HomeTriviaRankingLayout(this@TopTriviaRankingActivity, position, this@TopTriviaRankingActivity)
            container.addView(item)
            return item
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {
            return 2
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "This week"
                1 -> return "All Time"
            }
            return "This week"
        }
    }
}