package com.appster.adapters.viewholder


import android.view.View
import android.view.ViewGroup
import com.appster.R
import com.appster.extensions.*
import com.appster.features.searchScreen.SearchScreenOnClickListener
import com.domain.models.ExploreStreamModel
import kotlinx.android.synthetic.main.search_adapter_row.view.*

/**
 * Created by Ngoc on 5/23/2018.
 */
class StreamsRecentHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {


    fun bindTo(item: ExploreStreamModel, adapterCallBack: SearchScreenOnClickListener) {
        with(item) {
            itemView.setOnClickListener({ adapterCallBack.onItemUserImageClicked(item, adapterPosition) })

            itemView.userImage.loadImg(streamImage)
            itemView.imvLiveTag.visibility = isRecorded then View.GONE ?: View.VISIBLE
            itemView.viewExtra.visibility = isRecorded then View.GONE ?: View.VISIBLE
            itemView.tvTitleStream.text = streamTitle?.decodeEmoji()
            itemView.tvUserName.text = userName?.toUserName()
            itemView.llInfoContainer.setOnClickListener({ adapterCallBack.onItemUserNameClicked(item, adapterPosition) })
            itemView.tvNumberView.text = viewCount.toString()
        }
    }

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): StreamsRecentHolder {
            return StreamsRecentHolder(parent.inflate(R.layout.search_adapter_row))
        }
    }
}