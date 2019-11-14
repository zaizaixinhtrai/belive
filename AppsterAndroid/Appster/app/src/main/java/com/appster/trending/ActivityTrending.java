package com.appster.trending;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.apster.common.view.SlidingTabLayout;

/**
 * Created by User on 9/26/2015.
 */
public class ActivityTrending extends BaseToolBarActivity {

    private ViewPager viewPager;

    private SlidingTabLayout tabsStrip;
    // Tab titles
    private TabsPagerAdapter mTabsPagerAdapter;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ActivityTrending.class);
        return intent;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add Fragment
        mTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mTabsPagerAdapter);
        tabsStrip.setViewPager(viewPager);
//        tabsStrip.setIndicatorColor(Color.parseColor("#F8E598"));
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBarTile(getString(R.string.personal_leaderboard_slider));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        goneNotify(true);
        handleTurnoffMenuSliding();
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_trending;
    }

    @Override
    public void init() {
        intId();
    }

    private void intId() {
        tabsStrip = (SlidingTabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.pager);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {

            switch (index) {
                case 0:

                    return FragmentTrending.getInstance(FragmentTrending.TypeList.THIS_WEEK);

                case 1:

                    return FragmentTrending.getInstance(FragmentTrending.TypeList.THIS_MONTH);

                case 2:

                    return FragmentTrending.getInstance(FragmentTrending.TypeList.ALL_TIME);
            }

            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 3;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            String[] tabs = {getString(R.string.trending_this_week),
                    getString(R.string.trending_this_month),
                    getString(R.string.trending_all_time)};
            return tabs[position];
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }
}
