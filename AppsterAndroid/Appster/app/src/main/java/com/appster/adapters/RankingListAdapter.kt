package com.appster.adapters

import com.appster.adapters.delegate.RankingListDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.EndlessDelegateAdapter
import com.appster.features.mvpbase.RecyclerItemCallBack
import com.apster.common.BaseDiffCallback
import com.domain.models.WinnerModel

/**
 * Created by Ngoc on 3/9/2018.
 */

class RankingListAdapter(diffCallback: BaseDiffCallback<*>?, items: List<DisplayableItem>, recyclerItemCallBack: RecyclerItemCallBack<WinnerModel>) : EndlessDelegateAdapter(diffCallback) {

    init {
        this.delegatesManager.addDelegate(TOP_USER, RankingListDelegate(recyclerItemCallBack))
        setItems(items)
    }

    companion object {

        private const val TOP_USER = 1
    }

}
