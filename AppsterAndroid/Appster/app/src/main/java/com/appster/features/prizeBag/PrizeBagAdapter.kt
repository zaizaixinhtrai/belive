package com.appster.features.prizeBag

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.EndlessDelegateAdapter
import com.appster.features.prizeBag.delegates.PrizeBagDelegate
import com.appster.features.prizeBag.viewholders.PrizeBagViewHolder
import com.apster.common.BaseDiffCallback

class PrizeBagAdapter(diffCallback: BaseDiffCallback<*>?, items: List<DisplayableItem>,
                      listener: PrizeBagViewHolder.OnClickListener?) : EndlessDelegateAdapter(diffCallback) {
    init {
        this.delegatesManager.addDelegate(PrizeBagDelegate(listener))
        setItems(items)
    }
}