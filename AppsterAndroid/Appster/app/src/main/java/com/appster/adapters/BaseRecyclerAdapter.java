package com.appster.adapters;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lanna on 2/6/15.
 *
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {

    protected OnItemClickListener onItemClickListener;
    protected List<T> mModels;

    public BaseRecyclerAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // remind children have to return difficult ids if want recyclerView call onBindViewHolder
    // to update data on ViewHolder >"<
//    @Override
//    public long getItemId(int position) {
//        return getItem(position).getId().hashCode();
//    }

    public T getItem(int position) {
        return position < 0 || position >= getItemCount() ? null : mModels.get(position);
    }

    @Override
    public int getItemCount() {
        return mModels == null ? 0 : mModels.size();
    }

    public boolean isEmpty() {
        return mModels == null || mModels.size() == 0;
    }

    public List<T> getModels() {
        if (mModels == null)
            mModels = new ArrayList<>();
        return mModels;
    }

    public void setModels(List<T> models) {
        this.mModels = models;
    }

    public void setModelsAndNotify(List<T> models) {
        setModels(models);
        notifyDataSetChanged();
    }
}
