package com.appster.adapters.viewholder

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.appster.R
import com.appster.extensions.decodeEmoji
import com.appster.extensions.inflate
import com.appster.extensions.loadImg
import com.appster.features.mvpbase.RecyclerItemCallBack
import com.domain.models.WinnerModel
import kotlinx.android.synthetic.main.trivia_winner_item.view.*

/**
 * Created by Ngoc on 3/9/2018.
 */

class RankingListHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {


    fun bindTo(item: WinnerModel, recyclerItemCallBack: RecyclerItemCallBack<WinnerModel>?) {
        with(item){
            itemView.ivUserImage.loadImg(userAvatar)
            itemView.tvDisplayName.text = displayName?.decodeEmoji()
            itemView.tvUserName.text = String.format("@%s", userName)
            itemView.tvAmount.text = prizeString
            itemView.tvOder.text = orderIndex.toString()
            setOrderIndexBackground(orderIndex)
            itemView.tvOder.setTextColor(getTextColorByIndex(orderIndex))
            itemView.ivUserImage.setOnClickListener {
                recyclerItemCallBack?.onItemClicked(this, adapterPosition)
            }
            itemView.tvUserName.setOnClickListener {
                recyclerItemCallBack?.onItemClicked(this, adapterPosition)
            }
            itemView.tvDisplayName.setOnClickListener {
                recyclerItemCallBack?.onItemClicked(this, adapterPosition)
            }
        }

    }

    private fun getTextColorByIndex(orderIndex: Int): Int {
        return when (orderIndex) {
            1, 2, 3 -> ContextCompat.getColor(itemView.context, R.color.white)
            else -> ContextCompat.getColor(itemView.context, R.color.ranking_order_index_normal)
        }
    }

    private fun setOrderIndexBackground(index: Int) {
        val background = itemView.tvOder.background
        if (background is GradientDrawable) {
            @ColorRes val colorRes = when (index) {
                1 -> R.color.ranking_order_index_1
                2 -> R.color.ranking_order_index_2
                3 -> R.color.ranking_order_index_3

                else -> R.color.transparent
            }
            background.setColor(ContextCompat.getColor(itemView.context, colorRes))
        }
    }

    companion object {

        @JvmStatic
        fun create(parent: ViewGroup): RankingListHolder {
            return RankingListHolder(parent.inflate(R.layout.trivia_winner_item))
        }
    }
}
