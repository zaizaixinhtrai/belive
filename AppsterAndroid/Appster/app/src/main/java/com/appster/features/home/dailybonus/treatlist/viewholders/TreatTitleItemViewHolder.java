package com.appster.features.home.dailybonus.treatlist.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.customview.CustomFontTextView;
import com.domain.models.TreatListItemModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 11/10/17.
 */

public class TreatTitleItemViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tvTreatRankTitle)
    CustomFontTextView tvTreatRankTitle;

    private TreatTitleItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static TreatTitleItemViewHolder create(ViewGroup parent) {
        return new TreatTitleItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_treat_title, parent, false));
    }

    public void bindTo(TreatListItemModel item) {
        tvTreatRankTitle.setText(item.title);
    }
}
