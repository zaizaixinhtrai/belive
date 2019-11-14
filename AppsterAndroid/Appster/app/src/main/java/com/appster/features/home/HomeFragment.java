package com.appster.features.home;

import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.activity.BaseToolBarActivity;
import com.appster.fragment.BaseFragment;
import com.appster.models.TagListLiveStreamModel;
import com.appster.models.event_bus_models.EventBusRefreshHomeTab;
import com.appster.tracking.EventTracker;
import com.appster.tracking.EventTrackingName;
import com.appster.utility.AppsterUtility;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.response.MaintenanceModel;
import com.apster.common.Constants;
import com.apster.common.DialogManager;
import com.apster.common.LogUtils;
import com.apster.common.view.PagerSlidingTabStrip;
import com.pack.utility.CheckNetwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by User on 6/27/2016.
 */
public class HomeFragment extends BaseFragment implements ChildHomeFragment.PagerChangeRequestListener {

    public static final String NEW_TAB = "New";
    public static final String NEAR_BY_TAB = "Nearby";
    public static final String HOT_TAB = "Hot";
    @Bind(R.id.tabsNewHome)
    PagerSlidingTabStrip tabsNewHome;
    @Bind(R.id.most_pager_new_home)
    ViewPager mostPagerNewHome;
    @Bind(R.id.btnTryAgain)
    Button tryAgain;
    @Bind(R.id.tvMaintenanceMessage)
    TextView tvMaintenanceMessage;

    private View rootView;
    private MyPagerAdapter mTabsPagerAdapter;
    ArrayList<TagListLiveStreamModel> arrTagListLiveStreamModel = new ArrayList<>();
    SparseIntArray mCategorySparseIntArray = new SparseIntArray();
    ChildHomeFragment mHotTab;
    LatestLiveFragment mLatestLiveTab;
    NearByFragment mNearbyTab;

    private boolean isFirstTimeResumed = true;//whether the fragment has just created or resumed.

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        ((BaseToolBarActivity) getActivity()).setTxtTitleAsAppName();
        bindEvent();

        if (CheckNetwork.isNetworkAvailable(getActivity())) {
            getTagListLiveStream();
        } else {
            ((BaseActivity) getActivity()).utility.showMessage(getString(R.string.app_name),
                    getString(R.string.no_internet_connection), getActivity());
            tryAgain.setVisibility(View.VISIBLE);

        }
        EventBus.getDefault().register(this);
        handleVisibleMaintenanceMessage(AppsterApplication.mAppPreferences.getMaintenanceModel());

        EventTracker.trackEvent(EventTrackingName.EVENT_HOME);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //refresh the home screen every time it is resumed.
        //home button tab clicked
        //resume from phone's home button
        if (!isFirstTimeResumed) {
            if (!AppsterApplication.mAppPreferences.getIsNotNeedRefreshHome()) {
                EventBus.getDefault().post(new EventBusRefreshHomeTab());
                Timber.e("fragment home new resumed");
            }
        } else {
            isFirstTimeResumed = false;
        }
        AppsterApplication.mAppPreferences.setIsNotNeedRefreshHome(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void bindEvent() {
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isNetworkAvailable(getActivity())) {
                    getTagListLiveStream();
                } else {
                    ((BaseActivity) getActivity()).utility.showMessage(getString(R.string.app_name),
                            getString(R.string.no_internet_connection), getActivity());
                    tryAgain.setVisibility(View.VISIBLE);

                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            EventBus.getDefault().post(new EventBusRefreshHomeTab());
            if (getUserVisibleHint()) {
                ((BaseToolBarActivity) getActivity()).setTxtTitleAsAppName();
                ((BaseToolBarActivity) getActivity()).handleToolbar(true);
                ((BaseToolBarActivity) getActivity()).handleNewPushNotification(0);
            }
        }
    }

    void setFragment() {
        // Add Fragment
        tabsNewHome.setAllCaps(true);
//        tabsNewHome.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_on_tapbar));
//        tabsNewHome.setSelectColor(ContextCompat.getColor(getContext(), R.color.text_color_selected_on_tapbar));
//        tabsNewHome.setTextSize(13);
//        tabsNewHome.setSelectTextSize(14);

        mTabsPagerAdapter = new MyPagerAdapter(getFragmentManager(), this);
        mostPagerNewHome.setAdapter(mTabsPagerAdapter);
        mostPagerNewHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabsNewHome.setViewPager(mostPagerNewHome);
        // Implement OnPageChangeListener for tracking event
        tabsNewHome.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position < 0 || position >= arrTagListLiveStreamModel.size()) {

                    AppsterApplication.mAppPreferences.setCurrentTagOnHome(arrTagListLiveStreamModel.get(position).getTagId());
                    return;
                }

                final TagListLiveStreamModel model = arrTagListLiveStreamModel.get(position);
                if (model != null) {
                    EventTracker.trackSelectCategory(model.getTagName());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onRequestPagerChange(String pageId) {
        if (mCategorySparseIntArray != null) {
//            Timber.e(mCategorySparseIntArray.toString());
            int position = mCategorySparseIntArray.get(Integer.parseInt(pageId), -1);
            if (position != -1) mostPagerNewHome.setCurrentItem(position, true);
        }
    }

    void getTagListLiveStream() {

        if (!AppsterApplication.mAppPreferences.getFlagNewlyUser()) {
            DialogManager.getInstance().showDialog(getActivity(), getResources().getString(R.string.connecting_msg));
        }
        mCompositeSubscription.add(AppsterWebServices.get().getAppConfigs(AppsterUtility.getAuth())
                .filter(appConfigModelBaseResponse -> appConfigModelBaseResponse != null && isFragmentUIActive())
                .subscribe(appConfig -> {
                    DialogManager.getInstance().dismisDialog();

                    if (appConfig.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {

                        tryAgain.setVisibility(View.GONE);
                        TagListLiveStreamModel tagListLiveStreamModel = new TagListLiveStreamModel();
                        tagListLiveStreamModel.setTagName(getString(R.string.tag_hot));
                        tagListLiveStreamModel.setTagId(Constants.HOME_TAG_ID_HOT);
                        arrTagListLiveStreamModel.add(tagListLiveStreamModel);
                        if (appConfig.getData().enableNewTab) {
                            TagListLiveStreamModel tagLastestStreamModel = new TagListLiveStreamModel();
                            tagLastestStreamModel.setTagName(NEW_TAB);
                            tagLastestStreamModel.setTagId(0);
                            arrTagListLiveStreamModel.add(tagLastestStreamModel);
                        }
                        if (appConfig.getData().enableNearbyTab) {
                            TagListLiveStreamModel tagNearbyStreamModel = new TagListLiveStreamModel();
                            tagNearbyStreamModel.setTagName(NEAR_BY_TAB);
                            tagNearbyStreamModel.setTagId(0);
                            arrTagListLiveStreamModel.add(tagNearbyStreamModel);
                        }



/*//                        uncomment this for future release
                        arrTagListLiveStreamModel.addAll(tagListLiveStreamDataResponse.getData());*/
                        storeAllCategoriesTagPosition(arrTagListLiveStreamModel);
                        setFragment();
                    } else {
                        tryAgain.setVisibility(View.VISIBLE);
                        onErrorWebServiceCall(appConfig.getMessage(), appConfig.getCode());
                    }
                }, error -> {
                    DialogManager.getInstance().dismisDialog();
                    onErrorWebServiceCall(error.getMessage(), Constants.RETROFIT_ERROR);
                    if (isFragmentUIActive()) {
                        tryAgain.setVisibility(View.VISIBLE);
                    }
                }));
    }

    private void storeAllCategoriesTagPosition(@NonNull ArrayList<TagListLiveStreamModel> tags) {
        for (int i = 0; i < tags.size(); i++) {
            mCategorySparseIntArray.put(tags.get(i).getTagId(), i);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private final ChildHomeFragment.PagerChangeRequestListener mPagerChangeRequestListener;

        public MyPagerAdapter(FragmentManager fm, ChildHomeFragment.PagerChangeRequestListener pagerChangeRequestListener) {
            super(fm);
            mPagerChangeRequestListener = pagerChangeRequestListener;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return arrTagListLiveStreamModel.get(position).getTagName();
        }

        @Override
        public int getCount() {
            return arrTagListLiveStreamModel.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }


        @Override
        public Fragment getItem(int position) {
            LogUtils.logE("adapter", "** get item" + position);
            if (position == 0) {
                if (mHotTab == null) {
                    mHotTab = ChildHomeFragment.newInstance(arrTagListLiveStreamModel.get(position).getTagId());
                }
                return mHotTab;
            }
            switch (arrTagListLiveStreamModel.get(position).getTagName()) {
                case NEW_TAB:
                    if (mLatestLiveTab == null) {
                        mLatestLiveTab = LatestLiveFragment.newInstance(arrTagListLiveStreamModel.get(position).getTagId());
                    }
                    return mLatestLiveTab;
                case NEAR_BY_TAB:
                    if (mNearbyTab == null) {
                        mNearbyTab = NearByFragment.newInstance(arrTagListLiveStreamModel.get(position).getTagId());
                    }
                    return mNearbyTab;
                default:
                    break;
            }
            return ChildHomeFragment.newInstance(0);// Prevent return null fragment
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            LogUtils.logE("adapter", "** remove item" + position);
        }
    }

    public Fragment getFragment(int position) {
        if (mostPagerNewHome == null || mTabsPagerAdapter == null) {
            return null;
        }
        // based on the current position you can then cast the page to the correct
        // class and call the method:
        return getFragmentManager().findFragmentByTag("android:switcher:" + R.id.most_pager_new_home + ":" + position);
    }

    public void refreshToHotTab() {
        if (isFragmentUIActive() && arrTagListLiveStreamModel != null) {
            if (mostPagerNewHome.getCurrentItem() != 0) {
                mostPagerNewHome.setCurrentItem(0);

                if (mHotTab != null) {
                    mHotTab.onRefreshDataForTabHot();
                }
            }

            ((BaseToolBarActivity) getActivity()).setTxtTitleAsAppName();
            ((BaseToolBarActivity) getActivity()).handleToolbar(true);
            ((BaseToolBarActivity) getActivity()).handleNewPushNotification(0);
        }
    }

    public void refreshViewPager() {
        int fragmentCount = mTabsPagerAdapter.getCount();
        for (int i = 0; i < fragmentCount; i++) {
            Fragment fragment = getFragment(i);
            if (fragment != null) {
                ((ChildHomeFragment) fragment).onRefreshData();
            }
        }
    }

    public void refreshHome() {
//        if (isFragmentUIActive() && arrTagListLiveStreamModel != null) {
//            if (mHotTab != null && mostPagerNewHome.getCurrentItem() == 0) {
//                mHotTab.onRefreshDataForTabHot();
//            }
//        }
    }

//    public void forceRefresh(){
    // add broad cast update wall feed Stream
//        EventBus.getDefault().post(new EventBusRefreshFragment());
//        for (int i = 0; i< mTabsPagerAdapter.getCount(); i++){
//            Fragment fragment = getFragmentManager().findFragmentByTag("android:switcher:" + mostPagerNewHome.getId() + ":" + mostPagerNewHome.getCurrentItem());
//            if (fragment != null){
//                ChildHomeFragment gamingFragment = (ChildHomeFragment) fragment;
//                if (i == mostPagerNewHome.getCurrentItem()){
//                    gamingFragment.onRefreshData();
//                }else{
//                    gamingFragment.setShouldRefresh(true);
//                }
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MaintenanceModel model) {
        handleVisibleMaintenanceMessage(model);
    }

    void handleVisibleMaintenanceMessage(MaintenanceModel model) {
        if (model != null && isFragmentUIActive()) {
            switch (model.maintenanceMode) {

                case Constants.MAINTENANCE_MODE_STOP:
                    tvMaintenanceMessage.setVisibility(View.GONE);
                    break;
                case Constants.MAINTENANCE_MODE_STANDBY:
                    tvMaintenanceMessage.setText(model.message);
                    tvMaintenanceMessage.setVisibility(View.VISIBLE);
                    break;

                case Constants.MAINTENANCE_MODE_START:
                    break;
            }
        }
    }

    public void onScrollUpListView() {
        if (isFragmentUIActive()) {
            if (mostPagerNewHome.getCurrentItem() == 0 && mHotTab != null) {
                mHotTab.onScrollUpListView();
            } else if (mostPagerNewHome.getCurrentItem() == 1 && mLatestLiveTab != null) {
                mLatestLiveTab.onScrollUpListView();
            } else if (mostPagerNewHome.getCurrentItem() == 2 && mNearbyTab != null) {
                mNearbyTab.onScrollUpListView();
            }
        }
    }
}
