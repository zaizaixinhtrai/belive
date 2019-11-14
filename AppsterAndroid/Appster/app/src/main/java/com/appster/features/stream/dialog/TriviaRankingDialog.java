package com.appster.features.stream.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appster.R;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.SwipeLeftRightDismissTouchListener;
import com.appster.dialog.ImmersiveDialogFragment;
import com.appster.features.mvpbase.RecyclerItemCallBack;
import com.appster.features.stream.TriviaRankingLayout;
import com.apster.common.Constants;
import com.apster.common.Utils;
import com.apster.common.view.PagerSlidingTabStrip;
import com.domain.models.WinnerModel;
import com.pack.utility.StringUtil;

import org.jetbrains.annotations.NotNull;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by Ngoc on 3/8/2018.
 */

public class TriviaRankingDialog extends ImmersiveDialogFragment {

    @Bind(R.id.rankingTabStrip)
    PagerSlidingTabStrip rankingTabStrip;
    @Bind(R.id.rankingViewPager)
    ViewPager rankingViewPager;
    @Bind(R.id.tvWinnerText)
    CustomFontTextView tvWinnerText;

    private static final String COUNTRY_CODE = "country_code";
    private String triviaCountryCode;

    RecyclerItemCallBack<WinnerModel> mRecyclerItemCallBack;

    public static TriviaRankingDialog newInstance(String triviaCountryCode) {
        TriviaRankingDialog fragment = new TriviaRankingDialog();
        Bundle args = new Bundle();
        if (!StringUtil.isNullOrEmptyString(triviaCountryCode))
            args.putString(COUNTRY_CODE, triviaCountryCode);
        fragment.setArguments(args);
        return fragment;
    }


    public void setRecyclerItemCallBack(RecyclerItemCallBack<WinnerModel> recyclerItemCallBack) {
        mRecyclerItemCallBack = recyclerItemCallBack;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            Timber.e("onStart");
            getDialog().getWindow().setLayout(Utils.dpToPx(335), Utils.dpToPx(470));
            getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) triviaCountryCode = bundle.getString(COUNTRY_CODE);
        setCancelable(false);
        setFragment();
        if (isVNTrivia(triviaCountryCode))
            tvWinnerText.setText(getString(R.string.top_winner_title_vi));
    }

    @Override
    protected int getRootLayoutResource() {
        return R.layout.dialog_top_winner;
    }

    @Override
    protected boolean isDimDialog() {
        return false;
    }

    @OnClick(R.id.imClose)
    public void closeDialog() {
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        View decorView = window != null ? window.getDecorView() : null;
        if (decorView != null) {
            decorView.setOnTouchListener(new SwipeLeftRightDismissTouchListener(decorView, null, new SwipeLeftRightDismissTouchListener.DismissCallbacks() {
                @Override
                public boolean canDismiss(@NotNull Object token) {
                    return true;
                }

                @Override
                public void onDismiss(@NotNull View view, @NotNull Object token) {
                    dismiss();
                }
            }));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Window window = getDialog().getWindow();
        View decorView = window != null ? window.getDecorView() : null;
        if (decorView != null) {
            decorView.setOnTouchListener(null);
        }
    }

    private boolean isVNTrivia(String countryCOde) {
        return !StringUtil.isNullOrEmptyString(countryCOde) && Constants.COUNTRY_CODE_VN_FROM_SERVER_RETURN.equals(countryCOde);
    }

    void setFragment() {
        if (getContext() != null) {
            rankingTabStrip.setAllCaps(true);
            rankingTabStrip.setShouldExpand(true);
            PagerAdapter pagerAdapter = new RankingPagerAdapter();
            rankingViewPager.setAdapter(pagerAdapter);
            rankingTabStrip.setViewPager(rankingViewPager);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    final class RankingPagerAdapter extends PagerAdapter {


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TriviaRankingLayout item = new TriviaRankingLayout(getContext(), position, mRecyclerItemCallBack);
            container.addView(item);
            return item;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return isVNTrivia(triviaCountryCode)? getString(R.string.trivia_ranking_this_week_vi): getString(R.string.trivia_ranking_this_week);
                case 1:
                    return isVNTrivia(triviaCountryCode)? getString(R.string.trivia_ranking_all_time_vi): getString(R.string.trivia_ranking_all_time);
            }
            return "This week";
        }
    }
}
