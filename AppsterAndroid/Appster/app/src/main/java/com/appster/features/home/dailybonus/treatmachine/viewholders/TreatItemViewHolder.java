package com.appster.features.home.dailybonus.treatmachine.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.domain.models.TreatItemModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanhbc on 11/8/17.
 */

public class TreatItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.ivTreatIcon)
    ImageView ivTreatIcon;

    private TreatItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static TreatItemViewHolder create(ViewGroup parent) {
        return new TreatItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_treat_item, parent, false));
    }

    public void bindTo(TreatItemModel item) {
        ivTreatIcon.setEnabled(!item.isClaimed);
        ivTreatIcon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), getTreatRes(item.treatColor)));
        ivTreatIcon.setVisibility(item.isClaimed ? View.INVISIBLE : View.VISIBLE);
    }

    private int getTreatRes(int treatColor) {
        switch (treatColor) {
            case 1:
                return R.drawable.ic_daily_treat_yellow;
            case 2:
                return R.drawable.ic_daily_treat_purle;
            default:
                return R.drawable.ic_daily_treat_green;

        }
    }
}
