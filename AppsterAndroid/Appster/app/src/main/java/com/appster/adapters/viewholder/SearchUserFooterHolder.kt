package com.appster.adapters.viewholder

import android.view.View
import android.view.ViewGroup
import com.appster.R
import com.appster.extensions.inflate
import com.appster.search.AdapterSearchItemCallBack
import kotlinx.android.synthetic.main.footer_listview_search.view.*

/**
 * Created by Ngoc on 5/22/2018.
 */
class SearchUserFooterHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    fun bindTo(adapterCallBack: AdapterSearchItemCallBack) {
        itemView.viewMore.setOnClickListener { adapterCallBack.onClickLoadMore() }
    }

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): SearchUserFooterHolder {
            return SearchUserFooterHolder(parent.inflate(R.layout.footer_listview_search))
        }
    }
}