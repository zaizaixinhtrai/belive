package com.appster.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.appster.R;
import com.appster.viewholder.WatcherViewHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by sonnguyen on 9/11/16.
 */
public class AdapterListWatcher extends RecyclerView.Adapter<WatcherViewHolder> {
    private WatcherListDiffCallback mWatcherListDiffCallback;
    private List<String> arrWatcher;
    private WeakReference<Context> mContextWeakRef;

    public AdapterListWatcher(Context context) {
        this.arrWatcher = new ArrayList<>();
        this.mContextWeakRef = new WeakReference<>(context);
        mWatcherListDiffCallback = new WatcherListDiffCallback();
    }


    @Override
    public WatcherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WatcherViewHolder(LayoutInflater.from(mContextWeakRef.get()).inflate(R.layout.viewer_picture, parent, false));
    }

    @Override
    public void onBindViewHolder(WatcherViewHolder holder, final int position) {
        String userName = arrWatcher.get(position);
        if (userName != null) {

            holder.bindViewHolder(userName);

//            String avatarUrl = watcher.getWatcher_image();
//            if (TextUtils.isEmpty(avatarUrl) && allBotImages != null) {
//                avatarUrl = allBotImages.get(watcher.getWatcher_userName());
//            }
//            if (!StringUtil.isNullOrEmptyString(avatarUrl)) {
//                watcher.setWatcher_image(avatarUrl);
//                holder.bindViewHolder(avatarUrl);
//                LogUtils.logE("CALL_API_NO_NEED", watcher.getWatcher_userName());
//            } else {
//                getUserImage(watcher);
//                LogUtils.logE("CALL_API_NEED_", watcher.getWatcher_userName());
//            }
        }
    }

    public String getItemAt(int position){
        if (arrWatcher != null && arrWatcher.size() > position){
            return arrWatcher.get(position);
        }else{
            return null;
        }
    }

    public void updateList(RecyclerView recyclerView, List<String> watcherList){
        mWatcherListDiffCallback.setOldList(arrWatcher).setNewList(watcherList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(mWatcherListDiffCallback);
        arrWatcher.clear();
        arrWatcher.addAll(watcherList);
        diffResult.dispatchUpdatesTo(this);
        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            recyclerView.post(() -> recyclerView.smoothScrollToPosition(getItemCount()));
        }
        Timber.d("update list watcher");
    }

    @Override
    public int getItemCount() {
        if (arrWatcher == null) {
            return 0;
        }
        return arrWatcher.size();

    }
}
