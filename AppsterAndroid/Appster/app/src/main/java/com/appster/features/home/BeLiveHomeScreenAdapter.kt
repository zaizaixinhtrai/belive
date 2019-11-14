package com.appster.features.home

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.EndlessDelegateAdapter
import com.appster.features.home.delegates.LiveShowMultipleDelegate
import com.appster.features.home.delegates.LiveShowSingleDelegate
import com.appster.features.home.viewholders.LiveShowSingleViewHolder
import com.appster.features.home.viewholders.LiveShowMultiViewHolder
import com.apster.common.BaseDiffCallback

/**
 * Created by thanhbc on 5/16/18.
 */

class BeLiveHomeScreenAdapter(diffCallback: BaseDiffCallback<*>?, items: List<DisplayableItem>,
                              val listener: LiveShowMultiViewHolder.OnClickListener?,
                              private val singleListener: LiveShowSingleViewHolder.OnClickListener?) : EndlessDelegateAdapter(diffCallback) {
    init {
        this.delegatesManager.addDelegate(LiveShowMultipleDelegate(listener))
        this.delegatesManager.addDelegate(LiveShowSingleDelegate(singleListener))
        setItems(items)
    }
}
