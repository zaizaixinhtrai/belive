package com.data.repository

import com.data.entity.mapper.GiftStoreEntityMapper
import com.data.repository.datasource.GiftStoreDataSource
import com.domain.models.GiftStoreModel
import com.domain.repository.GiftStoreRepository
import rx.Observable

/**
 * Created by thanhbc on 3/27/18.
 */
class GiftStoreDataRepository(val giftStoreDataSource: GiftStoreDataSource) : GiftStoreRepository {
    val mapper: GiftStoreEntityMapper by lazy { GiftStoreEntityMapper() }
    override fun fetchGiftStore(): Observable<GiftStoreModel> {
        return giftStoreDataSource.fetchGiftStore()
                .map(mapper::transform)
    }
}