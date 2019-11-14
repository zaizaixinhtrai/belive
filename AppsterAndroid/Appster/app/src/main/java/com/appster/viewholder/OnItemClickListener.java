package com.appster.viewholder;

import android.view.View;

/**
 * Created by lanna on 11/6/15.
 */
public interface OnItemClickListener<E> {

    void onItemClick(View view, E data, int position);

}
