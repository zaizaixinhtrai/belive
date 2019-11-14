package com.appster.adapters;

import android.view.View;

/**
 * Created by lanna on 2/5/15.
 *
 */
public interface OnItemClickListener<T> {

    void onItemClick(View view, T data, int position);

}
