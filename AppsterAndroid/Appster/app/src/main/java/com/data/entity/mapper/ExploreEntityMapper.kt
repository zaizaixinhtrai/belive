package com.data.entity.mapper

import com.appster.core.adapter.DisplayableItem
import com.appster.extensions.then
import com.appster.webservice.response.StreamsRecentResponse
import com.domain.models.BasePagingModel
import com.domain.models.ExploreStreamModel

class ExploreEntityMapper {
    fun transform(data: StreamsRecentResponse?): BasePagingModel<DisplayableItem>? {
        return data?.streamsData?.let {
            return BasePagingModel<DisplayableItem>().apply {
                isEnd = it.isEnd
                nextId = it.nextId
                this.data = it.result.orEmpty().map {
                    ExploreStreamModel(it.streamUrl, it.slug, !it.coverImage.isNullOrEmpty()
                    then it.coverImage
                            ?: it.publisher?.run { it.publisher.userImage }, it.isRecorded, it.publisher?.userId, it.publisher?.userName, it.title, it.viewCount)
                }
            }
        }

    }


}
