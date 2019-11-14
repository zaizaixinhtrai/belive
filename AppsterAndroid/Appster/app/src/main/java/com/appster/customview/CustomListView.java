package com.appster.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by USER on 9/29/2015.
 */
public class CustomListView extends PullToRefreshListView {

    private OnListViewLoadMoreListener mOnLoadMoreListener;
    // To know if the list is loading more items
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;
    private int nextIndex = 0;


    public CustomListView(Context context) {
        super(context);
    }



    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, Mode mode) {
        super(context, mode);
    }

    public CustomListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        //view.getChildAt()
        ListAdapter adapter=getAdapter();
        if (mOnLoadMoreListener != null) {
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If its still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;

            }

            // If it isn�t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            //testfff
            boolean loadMore = firstVisibleItem + visibleItemCount + visibleThreshold >= totalItemCount;
            if (!loading && loadMore) {
                onLoadMore();
                loading = true;
            }
        }
    }

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
    public interface OnListViewLoadMoreListener {

        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnListViewLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }
//    private void stopMediaAtPostion(AdapterNewsFeed.AdapterNewsFeedHolder holder){
//        if(holder!=null )
//        {
//            if(holder.mediaController!=null) {
//            if (holder.textureVideoView.isPlaying()) {
//                if(holder.mediaController.isShowing()){
//                    holder.mediaController.hide();
//                }
//                holder.textureVideoView.pause();
//
//                holder.textureVideoView.setVisibility(View.GONE);
//                holder.imageView_play_icon.setVisibility(View.VISIBLE);
//                holder.image_media.setVisibility(View.VISIBLE);
//            }else {
//                holder.textureVideoView.setVisibility(View.GONE);
//                holder.imageView_play_icon.setVisibility(View.VISIBLE);
//                holder.image_media.setVisibility(View.VISIBLE);
//            }
//        }
//        }
//    }
}
