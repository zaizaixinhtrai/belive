package com.appster.features.home.triviaRanking.delegates

import com.appster.core.adapter.DisplayableItem
import com.appster.core.adapter.EndlessDelegateAdapter
import com.appster.features.mvpbase.RecyclerItemCallBack
import com.apster.common.BaseDiffCallback
import com.domain.models.WinnerModel
/**
 * Created by thanhbc on 5/18/18.
 */
class TopTriviaRankingAdapter(diffCallback: BaseDiffCallback<*>?, items: List<DisplayableItem>, recyclerItemCallBack: RecyclerItemCallBack<WinnerModel>) : EndlessDelegateAdapter(diffCallback) {

    init {
        this.delegatesManager.addDelegate(TopTriviaRankingDelegate(recyclerItemCallBack))
        setItems(items)
    }
}