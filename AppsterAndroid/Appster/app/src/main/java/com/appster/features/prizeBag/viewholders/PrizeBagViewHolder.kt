package com.appster.features.prizeBag.viewholders

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.appster.R
import com.appster.extensions.inflate
import com.appster.extensions.loadImg
import com.appster.utility.AppsterUtility
import com.domain.models.PrizeBagModel
import kotlinx.android.synthetic.main.priza_bag_item.view.*

class PrizeBagViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): PrizeBagViewHolder {
            return PrizeBagViewHolder(parent.inflate(R.layout.priza_bag_item))
        }
    }

    fun bindTo(item: PrizeBagModel, listener: OnClickListener?) {
        with(item) {
            val background = itemView.btCheck.background

            @ColorRes var textColor: Int
            var btText: String
            when (status) {
                0 -> {
                    textColor = R.color.color_ff5167
                    btText = itemView.context.getString(R.string.prize_bt_redeem)
                }
                1 -> {
                    textColor = R.color.color_17cfdc
                    btText = itemView.context.getString(R.string.prize_bt_check)
                }
                2 -> {
                    textColor = R.color.color_17cfdc
                    btText = itemView.context.getString(R.string.prize_bt_sent)
                }
                3 -> {
                    textColor = R.color.color_ff5167
                    btText = itemView.context.getString(R.string.prize_bt_rejected)
                }
                else -> {
                    textColor = R.color.color_ff5167
                    btText = itemView.context.getString(R.string.prize_bt_redeem)
                }
            }

            itemView.btCheck.text = btText
            if (background is GradientDrawable) {
                background.setColor(ContextCompat.getColor(itemView.context.applicationContext, textColor))
            }

            itemView.btCheck.setOnClickListener {
                when (status) {
                    0 -> listener?.onRedeemClick(item)
                    1 -> listener?.onCheckClick(item)
                    2 -> listener?.onNextClick(item)
                    3 -> listener?.onRejectedClick(item)
                }
                AppsterUtility.temporaryLockView(it)
            }

            itemView.setOnClickListener { listener?.onItemClick(item) }
            itemView.imvBranchImage.loadImg(prizeItem?.image)
            itemView.tvVoucherTitle.text = prizeItem?.title
            itemView.tvName.text = prizeItem?.name
        }
    }

    interface OnClickListener {
        fun onRedeemClick(item: PrizeBagModel)
        fun onCheckClick(item: PrizeBagModel)
        fun onItemClick(item: PrizeBagModel)
        fun onNextClick(item: PrizeBagModel)
        fun onRejectedClick(item: PrizeBagModel)
    }
}