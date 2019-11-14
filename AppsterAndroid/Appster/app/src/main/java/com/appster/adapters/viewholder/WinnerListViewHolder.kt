package com.appster.adapters.viewholder

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.appster.R
import com.appster.extensions.decodeEmoji
import com.appster.extensions.inflate
import com.appster.extensions.loadImg
import com.appster.extensions.toUserName
import com.appster.features.mvpbase.RecyclerItemCallBack
import com.domain.models.WinnerModel
import kotlinx.android.synthetic.main.trivia_winner_item.view.*

/**
 * Created by Ngoc on 3/14/2018.
 */

class WinnerListViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    fun bindTo(item: WinnerModel, recyclerItemCallBack: RecyclerItemCallBack<WinnerModel>?) {
        with(item){
            itemView.ivUserImage.loadImg(userAvatar)
            itemView.tvDisplayName.text = displayName?.decodeEmoji()
            itemView.tvUserName.text = userName?.toUserName()
            itemView.tvUserName.setTextColor(Color.WHITE)
            itemView.tvAmount.text = prizeString
            itemView.tvAmount.visibility = View.GONE
            itemView.tvOder.visibility = View.GONE
            itemView.fmOrderIndex.visibility = View.GONE
            itemView.ivUserImage.setOnClickListener {
                recyclerItemCallBack?.onItemClicked(this, adapterPosition)
            }
            itemView.tvDisplayName.setOnClickListener {
                recyclerItemCallBack?.onItemClicked(this, adapterPosition)
            }
            itemView.tvUserName.setOnClickListener {
                recyclerItemCallBack?.onItemClicked(this, adapterPosition)
            }
        }
    }

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): WinnerListViewHolder {
            return WinnerListViewHolder(parent.inflate(R.layout.trivia_winner_item))
        }
    }
}
