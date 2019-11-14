package com.appster.features.home.dailybonus.treatlist.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.customview.CustomFontTextView;
import com.appster.layout.SquareImageView;
import com.appster.utility.ImageLoaderUtil;
import com.domain.models.TreatUltimateItem;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatUltimateItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.ivPrizeImage)
    SquareImageView ivPrizeImage;
    @Bind(R.id.tvPrizeAmount)
    CustomFontTextView tvPrizeAmount;
    @Bind(R.id.tvPrizeName)
    CustomFontTextView tvPrizeName;
    @Bind(R.id.tvPrizeDesc)
    CustomFontTextView tvPrizeDesc;

    public TreatUltimateItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static TreatUltimateItemViewHolder create(ViewGroup parent) {
        return new TreatUltimateItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_treat_ultimate_item, parent, false));
    }

    public void bindTo(TreatUltimateItem item) {
//        Picasso.with(itemView.getContext())
//                .load(item.prizeImgUrl)
//                .into(ivPrizeImage);
        ImageLoaderUtil.displayUserImage(itemView.getContext(),item.prizeImgUrl, ivPrizeImage, false, null);
        if (!String.valueOf(item.prizeDesc).isEmpty()) {
            tvPrizeDesc.setText(item.prizeDesc);
            tvPrizeDesc.setVisibility(View.VISIBLE);
        } else {
            tvPrizeDesc.setVisibility(View.GONE);
        }
        tvPrizeAmount.setText(item.value);
        tvPrizeName.setText(item.prizeName);
    }
}
