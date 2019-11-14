package com.appster.giftreceive;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appster.R;
import com.appster.activity.BaseToolBarActivity;
import com.apster.common.view.SlidingTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 11/3/2015.
 */
public class GiftStoreActivity extends BaseToolBarActivity {

    @Bind(R.id.tabs)
    SlidingTabLayout tabs;
    @Bind(R.id.pager)
    ViewPager pager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        // Add Fragment
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(tabsPagerAdapter);
        tabs.setViewPager(pager);
        pager.setOffscreenPageLimit(2);
    }

    @Override
    public int getLayoutContentId() {
        return R.layout.activity_gift_receive_store;
    }

    @Override
    public void init() {
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        goneNotify(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBarTile(getString(R.string.gift_title));
        useAppToolbarBackButton();
        getEventClickBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case 0:

                    return GiftStoreFragment.newInstance(GiftStoreFragment.GiftType.Received);

                case 1:

                    return GiftStoreFragment.newInstance(GiftStoreFragment.GiftType.Sent);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 2;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            String[] tabTitles = {getString(R.string.gift_receive_Received),
                    getString(R.string.gift_receive_Sent)};
            return tabTitles[position];
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
