package com.domain.repository

import com.appster.core.adapter.DisplayableItem
import com.domain.models.BasePagingModel
import rx.Observable

/**
 * Created by thanhbc on 6/13/18.
 */
interface ExploreStreamRepository{
    fun getStreams(pageId: Int = 0) : Observable<BasePagingModel<DisplayableItem>>
}