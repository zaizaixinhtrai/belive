package com.appster.features.points.prizelist.adapter.holder

import android.view.View
import com.appster.adapters.OnItemClickListener
import com.appster.extensions.loadImg
import com.appster.features.points.Prize
import kotlinx.android.synthetic.main.holder_prize_header.view.*

/**
 *  Created by DatTN on 10/29/2018
 */
class PrizeHeaderHolder(view: View, onItemClickListener: OnItemClickListener<Prize>) : PrizeHolder(view, onItemClickListener) {

    override fun loadImage(prize: Prize) {
        itemView?.apply {
            iv_prize_thumb.loadImg(prize.thumbUrl)
        }
    }

}