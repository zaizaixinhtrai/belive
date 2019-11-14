package com.appster.features.income.cashout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.domain.CashItemModel;
import com.appster.features.mvpbase.RecyclerItemCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by thanhbc on 2/13/17.
 */
class CashoutAdapter extends RecyclerView.Adapter<CashoutAdapter.CashoutItemViewHolder> {
    private final Context context;
    private List<CashItemModel> items;

    private RecyclerItemCallBack<CashItemModel> mRecyclerItemCallBack;

    CashoutAdapter(List<CashItemModel> items, Context context, RecyclerItemCallBack<CashItemModel> itemCallBack) {
        this.items = items;
        this.context = context;
        mRecyclerItemCallBack = itemCallBack;
    }

    @Override
    public CashoutItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.cash_item_adapter, parent, false);
        return new CashoutItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CashoutItemViewHolder holder, int position) {
        CashItemModel item = items.get(position);
        holder.bindView(item, mRecyclerItemCallBack);
        holder.itemView.setTag(holder);

    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    void swapItems(ArrayList<CashItemModel> cashItemModels) {
//        final RateDiffCallback rateDiffCallback = new RateDiffCallback(this.items, cashItemModels);
//        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(rateDiffCallback);
        items.clear();
        items.addAll(cashItemModels);
//        diffResult.dispatchUpdatesTo(this);
        notifyDataSetChanged();
    }


    static class CashoutItemViewHolder extends RecyclerView.ViewHolder {
        CashItemModel mCashItemModel;

        @Bind(R.id.tvSGD)
        TextView tvSGD;

        @Bind(R.id.tvStar)
        TextView tvStar;

        @Bind(R.id.llStarsContainer)
        LinearLayout llStarsContainer;
        RecyclerItemCallBack<CashItemModel> mCallBack;

        CashoutItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(CashItemModel item, RecyclerItemCallBack<CashItemModel> recyclerItemCallBack) {
            this.mCashItemModel = item;
            mCallBack = recyclerItemCallBack;
            int currentStars = (int) AppsterApplication.mAppPreferences.getUserModel().getTotalGold();
//            int adapterPosition = getAdapterPosition();
            llStarsContainer.setBackground(ContextCompat.getDrawable(itemView.getContext(), currentStars < item.gold ? R.drawable.income_exchange_btn_grey : R.drawable.income_exchange_btn));
            tvSGD.setText(String.format(Locale.US, "%s %,d", item.currentcy, item.money));
            tvStar.setText(String.format(Locale.US, "%,d", item.gold));
        }

        @OnClick(R.id.tvStar)
        void onStartItemClicked() {
            mCallBack.onItemClicked(mCashItemModel, getAdapterPosition());
        }

//        public CashItemModel getCashItemModel() {
//            return mCashItemModel;
//        }
    }
}