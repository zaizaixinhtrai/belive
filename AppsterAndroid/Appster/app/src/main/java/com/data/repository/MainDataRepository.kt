package com.data.repository

import com.apster.common.Constants
import com.data.exceptions.BeLiveServerException
import com.data.repository.datasource.MainDataSource
import com.domain.repository.MainRepository
import rx.Observable
import javax.inject.Inject

/**
 * Created by Ngoc on 5/17/2018.
 */
class MainDataRepository @Inject constructor(@Remote private val mainDataSource: MainDataSource) : MainRepository {

    override fun checkHasLiveVideo(): Observable<Boolean> {
        return mainDataSource.checkHasLiveVideo()
                .flatMap { t ->
                    when (t.code) {
                        Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(t.data)
                        else -> Observable.error(BeLiveServerException(t.message, t.code))
                    }
                }
    }
}