package com.data.repository.datasource

import com.appster.webservice.response.BaseResponse
import com.data.entity.DailyBonusEntity
import com.data.entity.MysteryBoxEntity
import com.data.entity.PrizeBagEntity
import com.data.entity.PrizeEntity
import com.data.entity.requests.SubmitRedemptionEntity
import com.appster.pocket.CreditsModel
import com.data.entity.*
import com.domain.models.NextBonusInformationModel
import rx.Observable

/**
 *  Created by DatTN on 10/24/2018
 */
interface PointSystemDataSource {

    fun loadPrizeBag(): Observable<BaseResponse<List<PrizeBagEntity>>>
    fun submitRedemption(bagItemId: Int, name: String, email: String): Observable<BaseResponse<Boolean>>
    fun pickPrizeBagItem(idPrize: Int): Observable<BaseResponse<String>>
    fun loadBoxList(): Observable<BaseResponse<List<MysteryBoxEntity>>>
    fun loadDailyBonus(): Observable<BaseResponse<DailyBonusEntity>>
    fun loadPrizeItems(mysteryBoxId: Int): Observable<BaseResponse<List<PrizeEntity>>>
    fun loadDailyPrizeItems(): Observable<BaseResponse<List<DailyPrizeEntity>>>
    fun loadUserPrizeInfo(): Observable<BaseResponse<UserPrizeBagInfoEntity>>
    fun loadDailyBonusCountDown(): Observable<BaseResponse<NextBonusInformationModel>>
    fun loadUserPoint(): Observable<BaseResponse<CreditsModel>>
    fun openMysteryBox(boxId: Int): Observable<BaseResponse<PrizeEntity>>
    fun earnPoints(actionType: String, slug: String, mode: Int): Observable<BaseResponse<EarnPointsEntity>>
    fun loadDailyBonusPrizeItems(): Observable<BaseResponse<List<TreatEntity>>>
}