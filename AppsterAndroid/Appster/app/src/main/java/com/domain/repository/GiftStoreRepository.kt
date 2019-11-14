package com.domain.repository

import com.domain.models.GiftStoreModel
import rx.Observable

/**
 * Created by thanhbc on 3/27/18.
 */
interface GiftStoreRepository{
    fun fetchGiftStore(): Observable<GiftStoreModel>
}
