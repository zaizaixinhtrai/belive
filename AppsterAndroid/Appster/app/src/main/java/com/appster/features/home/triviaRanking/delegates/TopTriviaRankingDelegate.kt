package com.appster.features.home.triviaRanking.delegates

import android.view.ViewGroup
import com.appster.core.adapter.AbsListItemAdapterDelegate
import com.appster.core.adapter.DisplayableItem
import com.appster.features.home.triviaRanking.viewholders.TopTriviaRankingHolder
import com.appster.features.mvpbase.RecyclerItemCallBack
import com.domain.models.WinnerModel

/**
 * Created by thanhbc on 5/18/18.
 */
class TopTriviaRankingDelegate(private val recyclerItemCallBack: RecyclerItemCallBack<WinnerModel>) : AbsListItemAdapterDelegate<WinnerModel, DisplayableItem, TopTriviaRankingHolder>() {

    override fun isForViewType(item: DisplayableItem, items: List<DisplayableItem>, position: Int): Boolean {
        return item is WinnerModel
    }

    override fun onCreateViewHolder(parent: ViewGroup): TopTriviaRankingHolder {
        return TopTriviaRankingHolder.create(parent)
    }

    override fun onBindViewHolder(item: WinnerModel, viewHolder: TopTriviaRankingHolder, payloads: List<Any>) {
        viewHolder.bindTo(item, recyclerItemCallBack)
    }
}