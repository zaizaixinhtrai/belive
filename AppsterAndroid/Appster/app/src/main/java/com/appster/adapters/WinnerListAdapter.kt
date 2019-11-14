package com.appster.adapters

import com.appster.adapters.delegate.WinnerListDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.EndlessDelegateAdapter
import com.appster.features.mvpbase.RecyclerItemCallBack
import com.apster.common.BaseDiffCallback
import com.domain.models.WinnerModel

/**
 * Created by Ngoc on 3/14/2018.
 */

class WinnerListAdapter(diffCallback: BaseDiffCallback<*>?, items: List<DisplayableItem>, recyclerItemCallBack: RecyclerItemCallBack<WinnerModel>?) : EndlessDelegateAdapter(diffCallback) {

    init {
        this.delegatesManager.addDelegate(USER, WinnerListDelegate(recyclerItemCallBack))
        setItems(items)
    }

    companion object {

        private const val USER = 1
    }

}
