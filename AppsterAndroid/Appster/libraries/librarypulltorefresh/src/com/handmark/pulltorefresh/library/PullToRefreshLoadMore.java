package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * Created by USER on 9/22/2015.
 */
public class PullToRefreshLoadMore extends PullToRefreshGridView {
    private OnLoadMoreListener mOnLoadMoreListener;
    // To know if the list is loading more items
    private int visibleThreshold = 10;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;
    private int nextIndex=0;
    public int getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }
    public PullToRefreshLoadMore(Context context) {
        super(context);
    }

    public PullToRefreshLoadMore(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        if (mOnLoadMoreListener != null) {
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If it�s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
                currentPage = getNextIndex();
            }

            // If it isnt currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            boolean loadMore = firstVisibleItem + visibleItemCount +visibleThreshold >= totalItemCount;
            if (!loading && loadMore) {
                onLoadMore();
                loading = true;
            }
        }
    }

    // Defines the process for actually loading more data based on page
    public void onLoadMore() {

        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    /**
     * Notify the loading more operation has finished
     */


    /**
     * Interface definition for a callback to be invoked when list reaches the
     * last item (the user load more items in the list)
     */
    public interface OnLoadMoreListener {

        void onLoadMore();
    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }
}
