package com.appster.features.home.dailybonus.treatlist.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.customview.CustomFontTextView;
import com.appster.layout.SquareImageView;
import com.appster.utility.ImageLoaderUtil;
import com.domain.models.TreatItemModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatBigItemViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.ivPrizeImage)
    SquareImageView ivPrizeImage;
    @Bind(R.id.tvPrizeName)
    CustomFontTextView tvPrizeName;
    @Bind(R.id.tvPrizeDesc)
    CustomFontTextView tvPrizeDesc;
    @Bind(R.id.tvValue)
    CustomFontTextView tvValue;

    private TreatBigItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static TreatBigItemViewHolder create(ViewGroup parent) {
        return new TreatBigItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_treat_big_item, parent, false));
    }

    public void bindTo(TreatItemModel item) {
        tvPrizeName.setText(item.prizeName);
        if (!item.prizeDesc.isEmpty()) {
            tvPrizeDesc.setVisibility(View.VISIBLE);
            tvPrizeDesc.setText(item.prizeDesc);
        }else{
            tvPrizeDesc.setVisibility(View.GONE);
        }
        ImageLoaderUtil.displayUserImage(itemView.getContext(),item.prizeImgUrl, ivPrizeImage, false, null);
        tvValue.setText(item.value);
    }
}
