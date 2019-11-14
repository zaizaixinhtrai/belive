package com.appster.adapters

import com.appster.adapters.delegate.SearchUserDelegate
import com.appster.adapters.delegate.SearchUserFooterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.EndlessDelegateAdapter
import com.appster.search.AdapterSearchItemCallBack
import com.apster.common.BaseDiffCallback

/**
 * Created by Ngoc on 5/22/2018.
 */
class SearchUserAdapter(diffCallback: BaseDiffCallback<DisplayableItem>?, items: List<DisplayableItem>, adapterCallBack: AdapterSearchItemCallBack): EndlessDelegateAdapter(diffCallback) {
    init {
        this.delegatesManager.addDelegate(SearchUserDelegate(adapterCallBack))
        this.delegatesManager.addDelegate(SearchUserFooterDelegate(adapterCallBack))
        setItems(items)
    }
}