package com.appster.features.home.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.adapters.EndlessPagerAdapter;
import com.appster.adapters.EventPagerAdapter;
import com.appster.customview.AutoScrollViewPager;
import com.appster.features.home.BannerModel;
import com.appster.models.HomeCurrentEventModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 5/30/17.
 */

public class HomeBannerItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.pager)
    AutoScrollViewPager pager;


    BannerModel mBannerModel;

    public static HomeBannerItemViewHolder create(ViewGroup parent) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.home_adapter_banner_row, parent, false);
        return new HomeBannerItemViewHolder(itemView);
    }

    public interface OnClickListener {
        void onBannerItemClicked(HomeCurrentEventModel eventModel);
    }


    private EventPagerAdapter mEventPagerAdapter;

    public HomeBannerItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindTo(BannerModel model, OnClickListener listener) {
//        if (mEventPagerAdapter == null) {
            this.mBannerModel = model;
            mEventPagerAdapter = new EventPagerAdapter(itemView.getContext().getApplicationContext(), model.bannerItems);
            mEventPagerAdapter.setEventClick(position -> {
                HomeCurrentEventModel item = getBannerItem(position);
                if (listener != null && item != null) listener.onBannerItemClicked(item);
            });

            if (model.bannerItems.size() > 1) {
                pager.setCycle(true);
                pager.startAutoScroll();
                pager.setScrollDuration(500);
                EndlessPagerAdapter endlessPagerAdapter = new EndlessPagerAdapter(mEventPagerAdapter);
                pager.setAdapter(endlessPagerAdapter);
                pager.setCurrentItem(model.bannerItems.size() * (Integer.MAX_VALUE / 2 / model.bannerItems.size()));
            }else{
                pager.setAdapter(mEventPagerAdapter);
            }
//        }else{
//            mEventPagerAdapter.setEventModels(model.bannerItems);
//            mEventPagerAdapter.notifyDataSetChanged();
//            if(pager!=null) pager.setAdapter(mEventPagerAdapter);
//            Timber.e("notify event pager");
//        }
    }

    @Nullable
    private HomeCurrentEventModel getBannerItem(int position) {
        if (mBannerModel != null) {
            return mBannerModel.bannerItems.get(position);
        }
        return null;
    }
}
