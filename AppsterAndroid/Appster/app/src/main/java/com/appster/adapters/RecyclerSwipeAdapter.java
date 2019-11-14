package com.appster.adapters;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appster.layout.recyclerSwipeUtil.Attributes;
import com.appster.layout.recyclerSwipeUtil.SwipeAdapterInterface;
import com.appster.layout.recyclerSwipeUtil.SwipeItemMangerImpl;
import com.appster.layout.recyclerSwipeUtil.SwipeItemMangerInterface;
import com.appster.layout.recyclerSwipeUtil.SwipeLayout;

import java.util.List;

/**
 * Created by User on 2/2/2016.
 */
public abstract class RecyclerSwipeAdapter<T1 extends RecyclerView.ViewHolder, T2> extends BaseRecyclerViewLoadMore<T1, T2>
        implements SwipeItemMangerInterface, SwipeAdapterInterface {

    public SwipeItemMangerImpl mItemManger = new SwipeItemMangerImpl(this);

    public RecyclerSwipeAdapter(RecyclerView recyclerView, List<T2> mModels) {
        super(recyclerView, mModels);
    }

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void notifyDatasetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void openItem(int position) {
        mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mItemManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        mItemManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        mItemManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mItemManger.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return mItemManger.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        mItemManger.setMode(mode);
    }
}

