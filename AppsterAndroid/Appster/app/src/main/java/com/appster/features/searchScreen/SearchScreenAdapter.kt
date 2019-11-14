package com.appster.features.searchScreen

import com.appster.adapters.delegate.StreamsRecentDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.EndlessDelegateAdapter
import com.apster.common.BaseDiffCallback

/**
 * Created by thanhbc on 5/17/17.
 */

class SearchScreenAdapter(diffCallback: BaseDiffCallback<*>?, items: List<DisplayableItem>, itemCallBack: SearchScreenOnClickListener) : EndlessDelegateAdapter(diffCallback) {

    init {
        // Delegates
        this.delegatesManager.addDelegate(STREAMS_RECENT, StreamsRecentDelegate(itemCallBack))
        setItems(items)
    }

    companion object {
        const val STREAMS_RECENT = 0
    }
}
