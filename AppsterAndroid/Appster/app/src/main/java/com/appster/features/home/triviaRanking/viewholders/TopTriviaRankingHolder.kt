package com.appster.features.home.triviaRanking.viewholders

import android.view.View
import android.view.ViewGroup
import com.appster.R
import com.appster.extensions.inflate
import com.appster.extensions.loadImg
import com.appster.extensions.toUserName
import com.appster.features.mvpbase.RecyclerItemCallBack
import com.domain.models.WinnerModel
import kotlinx.android.synthetic.main.trivia_winner_item.view.*

/**
 * Created by thanhbc on 5/18/18.
 */
class TopTriviaRankingHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): TopTriviaRankingHolder {
            return TopTriviaRankingHolder(parent.inflate(R.layout.trivia_home_ranking_item))
        }
    }

    fun bindTo(item: WinnerModel, recyclerItemCallBack: RecyclerItemCallBack<WinnerModel>?){
        with(item){
            itemView.ivUserImage.loadImg(userAvatar)
            itemView.tvUserName.text = item.userName?.toUserName()
            itemView.tvAmount.text = prizeString
            itemView.ivUserImage.setOnClickListener {
                recyclerItemCallBack?.onItemClicked(this, adapterPosition)
            }
            itemView.tvUserName.setOnClickListener {
                recyclerItemCallBack?.onItemClicked(this, adapterPosition)
            }
        }
    }
}