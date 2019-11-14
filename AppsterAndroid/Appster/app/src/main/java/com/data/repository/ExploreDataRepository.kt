package com.data.repository

import com.appster.core.adapter.DisplayableItem
import com.apster.common.Constants
import com.data.entity.mapper.ExploreEntityMapper
import com.data.exceptions.BeLiveServerException
import com.data.repository.datasource.ExploreDataSource
import com.domain.models.BasePagingModel
import com.domain.repository.ExploreStreamRepository
import rx.Observable
import javax.inject.Inject

/**
 * Created by thanhbc on 6/13/18.
 */
class ExploreDataRepository @Inject constructor(@Remote val exploreDataSource: ExploreDataSource) : ExploreStreamRepository {

    private val exploreEntityMapper: ExploreEntityMapper by lazy { ExploreEntityMapper() }
    override fun getStreams(pageId: Int): Observable<BasePagingModel<DisplayableItem>> {
        return exploreDataSource.getStreams(pageId)
                .flatMap { t ->
                    when (t.code) {
                        Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(exploreEntityMapper.transform(t.data))
                        else -> Observable.error(BeLiveServerException(t.message, t.code))
                    }
                }
    }
}