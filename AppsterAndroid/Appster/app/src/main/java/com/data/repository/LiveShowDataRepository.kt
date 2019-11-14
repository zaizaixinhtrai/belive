package com.data.repository

import com.appster.core.adapter.DisplayableItem
import com.apster.common.Constants
import com.data.entity.mapper.LiveShowsEntityMapper
import com.data.exceptions.BeLiveServerException
import com.data.repository.datasource.LiveShowDataSource
import com.domain.models.LiveShowFriendNumberModel
import com.domain.models.LiveShowStatus
import com.domain.repository.LiveShowRepository
import rx.Observable
import rx.functions.Func1
import javax.inject.Inject

/**
 * Created by thanhbc on 5/18/18.
 */
class LiveShowDataRepository @Inject constructor(@Remote private val liveShowDataSource: LiveShowDataSource) : LiveShowRepository {

    var liveShowsEntityMapper: LiveShowsEntityMapper = LiveShowsEntityMapper()
    override fun getLiveShows(): Observable<List<DisplayableItem>> {
        return liveShowDataSource.fetchLiveShow().flatMap {
            when (it.code) {
                Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(liveShowsEntityMapper.transform(it.data))
                else -> Observable.error(BeLiveServerException(it.message, it.code))
            }
        }
    }

    override fun checkShows(showId: Int): Observable<LiveShowStatus> {
        return liveShowDataSource.checkStatus(showId).flatMap {
            when (it.code) {
                Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(liveShowsEntityMapper.transform(it.data))
                else -> Observable.error(BeLiveServerException(it.message, it.code))
            }
        }
    }

    override fun getFriendNumber(showId: Int): Observable<LiveShowFriendNumberModel> {
        return liveShowDataSource.getFriendNumber(showId)
                .flatMap {
                    when (it.code) {
                        Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(liveShowsEntityMapper.transform(it.data))
                        else -> Observable.error(BeLiveServerException(it.message, it.code))
                    }
                }
    }
}