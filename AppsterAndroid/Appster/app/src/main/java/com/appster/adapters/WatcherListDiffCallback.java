package com.appster.adapters;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.apster.common.BaseDiffCallback;

/**
 * Created by linh on 31/03/2017.
 */

public class WatcherListDiffCallback extends BaseDiffCallback<String> {
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        String oldUserName = mOldList.get(oldItemPosition);
        String newUserName = mNewList.get(newItemPosition);
        return !TextUtils.isEmpty(oldUserName) && oldUserName.equals(newUserName);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        String oldWatcher = mOldList.get(oldItemPosition);
        String newWatcher = mNewList.get(newItemPosition);

        return !TextUtils.isEmpty(oldWatcher) && oldWatcher.equals(newWatcher);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
