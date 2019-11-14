package com.appster.features.points.adapter.holder

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.NonNull
import com.appster.R
import com.appster.adapters.BaseRecyclerViewHolder
import com.appster.adapters.OnItemClickListener
import com.appster.customview.RoundRectCornerImageView
import com.appster.extensions.loadImg
import com.appster.features.points.MysteryBox
import com.appster.features.points.Prize
import com.appster.features.points.adapter.PointsAdapter
import kotlinx.android.synthetic.main.points_daily_bonus_holder.view.*

/**
 *  Created by DatTN on 10/23/2018
 */
open class MysteryHolder(view: View, onItemClickListener: OnItemClickListener<out MysteryBox>,
                         private val mOnPrizeItemClickListener: PointsAdapter.OnPrizeItemClicked) : BaseRecyclerViewHolder<MysteryBox>(view, onItemClickListener) {

    init {
        itemView?.apply {
            tv_view_all_prize.setOnClickListener(this@MysteryHolder)
        }
    }

    override fun onBind(model: MysteryBox?) {
        super.onBind(model)
        if (model == null) {
            itemView.visibility = View.GONE
            return
        }
        itemView.visibility = View.VISIBLE
        itemView?.apply {
            tv_box_title.text = model.title
            iv_thumb.setBackgroundResource(R.drawable.icon_premium_thumb)
            lo_prize_list_content.removeAllViews()
            model.prizes.forEach {
                lo_prize_list_content.addView(createPrizeView(it))
            }
            handlePriceView(model)
            contr_parent.setBackgroundColor(Color.parseColor("#2527DF"))
        }
    }

    @NonNull
    private fun createPrizeView(prize: Prize): View {
        val view = RoundRectCornerImageView(itemView.context)
        val itemHeight = itemView.context.resources.getDimensionPixelSize(R.dimen.mystery_box_prize_item_height)
        val layoutParams = LinearLayout.LayoutParams(itemHeight, itemHeight)
        layoutParams.marginEnd = itemView.context.resources.getDimensionPixelSize(R.dimen.mystery_box_prize_item_margin)
        view.layoutParams = layoutParams
        view.setRadius(itemView.context.resources.getDimensionPixelSize(R.dimen.mystery_box_prize_item_radius).toFloat())
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        view.setOnClickListener { mOnPrizeItemClickListener }
        view.loadImg(prize.thumbUrl)
        return view
    }

    open fun handlePriceView(mysteryBox: MysteryBox) {
        itemView?.apply {
            lo_price.setBackgroundResource(R.drawable.open_foreground_selector)
            iv_price_thumb.visibility = View.VISIBLE
            tv_price.text = mysteryBox.pointUse.toString()
            lo_price.setOnClickListener(this@MysteryHolder)
//            imCover.loadImg(mysteryBox.coverImage)

        }
    }
}