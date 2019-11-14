package com.domain.repository

import com.appster.core.adapter.DisplayableItem
import com.appster.features.points.MysteryBoxData
import com.appster.features.points.Prize
import com.data.entity.requests.SubmitRedemptionEntity
import com.appster.pocket.CreditsModel
import com.domain.models.EarnPointsModel
import com.domain.models.PrizeCollectModel
import rx.Observable

/**
 *  Created by DatTN on 10/24/2018
 */
interface PointSystemRepository {
    fun loadMysteryBoxData(): Observable<MysteryBoxData>
    fun loadPrizeBagList(): Observable<List<DisplayableItem>>
    fun submitRedemption(bagItemId: Int, name: String, email: String): Observable<Boolean>
    fun pickPrizeBagItem(idPrize: Int): Observable<String>
    fun loadPrizeList(boxType: Int, boxId: Int): Observable<List<Prize>>
    fun loadUserPrizeCount(): Observable<Int>
    fun loadDailyBonusCountDown(): Observable<Int>
    fun loadUserPoint(): Observable<CreditsModel>
    fun openMysteryBox(boxId: Int): Observable<PrizeCollectModel>
    fun earnPoints(actionType: String, slug: String, mode: Int): Observable<EarnPointsModel>
}