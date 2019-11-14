package com.appster.features.points.prizelist.adapter.holder

import android.view.View
import com.appster.R
import com.appster.adapters.BaseRecyclerViewHolder
import com.appster.adapters.OnItemClickListener
import com.appster.extensions.loadImg
import com.appster.features.points.Prize
import kotlinx.android.synthetic.main.holder_prize.view.*

/**
 *  Created by DatTN on 10/29/2018
 */
open class PrizeHolder(view: View, onItemClickListener: OnItemClickListener<Prize>) : BaseRecyclerViewHolder<Prize>(view, onItemClickListener) {

    override fun onBind(model: Prize?) {
        itemView?.setOnClickListener(null)
        super.onBind(model)
        if (model == null) {
            // Null prize item should not be sent to the prize info screen
            return
        }
        itemView?.findViewById<View>(R.id.lo_prize)?.setOnClickListener(this)
        itemView?.apply {
            tv_prize_title.text = model.title
            tv_prize_desc.text = model.desc
            loadImage(model)
        }
    }

    open fun loadImage(prize: Prize) {
        itemView?.apply {
            iv_prize_thumb.loadImg(prize.thumbUrl)
        }
    }
}