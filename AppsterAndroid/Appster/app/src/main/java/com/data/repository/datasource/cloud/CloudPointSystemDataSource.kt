package com.data.repository.datasource.cloud

import com.appster.pocket.CreditsModel
import com.appster.webservice.AppsterWebserviceAPI
import com.appster.webservice.request_models.CreditsRequestModel
import com.appster.webservice.response.BaseResponse
import com.data.di.ApiServiceModule
import com.data.entity.DailyBonusEntity
import com.data.entity.MysteryBoxEntity
import com.data.entity.PrizeBagEntity
import com.data.entity.PrizeEntity
import com.data.entity.requests.SubmitRedemptionEntity
import com.data.entity.*
import com.data.entity.requests.EarnPointsRequestEntity
import com.data.entity.requests.PickPrizeItemRequestEntity
import com.data.repository.datasource.PointSystemDataSource
import com.domain.models.NextBonusInformationModel
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

/**
 *  Created by DatTN on 10/24/2018
 */
class CloudPointSystemDataSource @Inject constructor(private val mService: AppsterWebserviceAPI,
                                                     @Named(ApiServiceModule.APP_AUTHEN) private val mAuthen: String) : PointSystemDataSource {


    override fun earnPoints(actionType: String, slug: String, mode: Int): Observable<BaseResponse<EarnPointsEntity>> {
        return mService.earnPoints(mAuthen, EarnPointsRequestEntity(actionType, slug, mode))
    }

    override fun loadBoxList(): Observable<BaseResponse<List<MysteryBoxEntity>>> {
        return mService.getMysteryBoxes(mAuthen)
    }

    override fun loadDailyBonus(): Observable<BaseResponse<DailyBonusEntity>> {
        return Observable.zip(loadDailyBonusData(), loadDailyBonusCountDown()) { dailyBonus, countDownInfo ->
            dailyBonus.countDown = countDownInfo.data.nextTimeSeconds
            val rp = BaseResponse<DailyBonusEntity>()
            rp.code = 1
            rp.data = dailyBonus
            rp
        }
    }

    override fun loadUserPoint(): Observable<BaseResponse<CreditsModel>> {
        return mService.getUserCredits(mAuthen, CreditsRequestModel())
    }

    override fun loadPrizeItems(mysteryBoxId: Int): Observable<BaseResponse<List<PrizeEntity>>> {
        return mService.getPrizeList(mAuthen, mysteryBoxId)
    }

    override fun loadDailyPrizeItems(): Observable<BaseResponse<List<DailyPrizeEntity>>> {
        return Observable.fromCallable {
            val rp = BaseResponse<List<DailyPrizeEntity>>()
            rp.code = 1
            rp.data = fakeDailyPrizes()
            rp
        }
    }

    private fun loadDailyBonusData(): Observable<DailyBonusEntity> {
        return Observable.fromCallable {
            fakeDailyBonus()
        }
    }

    override fun loadDailyBonusCountDown(): Observable<BaseResponse<NextBonusInformationModel>> {
        return mService.getBonusInformation(mAuthen)
    }

    override fun loadUserPrizeInfo(): Observable<BaseResponse<UserPrizeBagInfoEntity>> {
        return mService.getUserPrizeBagInfo(mAuthen)
    }

    override fun openMysteryBox(boxId: Int): Observable<BaseResponse<PrizeEntity>> {
        return mService.openMysteryBox(mAuthen, PickPrizeItemRequestEntity(boxId))
    }

    private fun fakeDailyBonus(): DailyBonusEntity {
        var prizes = mutableListOf<PrizeEntity>()
        prizes.add(PrizeEntity(10, "", "", "https://b3h2.scene7.com/is/image/BedBathandBeyond/118164161286924p?\$imagePLP\$&wid=256&hei=256", type = 0))
        prizes.add(PrizeEntity(11, "", "", "https://b3h2.scene7.com/is/image/BedBathandBeyond/62141043033546p?\$imagePLP\$&wid=256&hei=256", type = 0))
        prizes.add(PrizeEntity(12, "", "", "https://b3h2.scene7.com/is/image/BedBathandBeyond/105550816708143p?\$imagePLP\$&wid=256&hei=256", type = 0))
        return DailyBonusEntity(15, "Free mystery box", "http://3.imimg.com/data3/RS/NO/GLADMIN-12031/gift-box-500x500.jpg", "#ff0000", 10, prizes, "http://3.imimg.com/data3/RS/NO/GLADMIN-12031/gift-box-500x500.jpg")
    }

    override fun loadDailyBonusPrizeItems(): Observable<BaseResponse<List<TreatEntity>>> {
        return mService.getTreatList(mAuthen)
    }

    override fun loadPrizeBag(): Observable<BaseResponse<List<PrizeBagEntity>>> {
        return mService.getPrizeBagList(mAuthen)
    }

    override fun submitRedemption(bagItemId: Int, name: String, email: String): Observable<BaseResponse<Boolean>> {
        return mService.submitRedemption(mAuthen, SubmitRedemptionEntity(bagItemId, name, email))
    }

    override fun pickPrizeBagItem(idPrize: Int): Observable<BaseResponse<String>> {
        return mService.pickPrizeBagItem(mAuthen, idPrize)
    }

    private fun fakeDailyPrizes(): List<DailyPrizeEntity> {
        var prizes = mutableListOf<DailyPrizeEntity>()
        prizes.add(DailyPrizeEntity(10, "", "", "https://b3h2.scene7.com/is/image/BedBathandBeyond/118164161286924p?\$imagePLP\$&wid=256&hei=256", type = 0))
        prizes.add(DailyPrizeEntity(11, "", "", "https://b3h2.scene7.com/is/image/BedBathandBeyond/62141043033546p?\$imagePLP\$&wid=256&hei=256", type = 0))
        prizes.add(DailyPrizeEntity(12, "", "", "https://b3h2.scene7.com/is/image/BedBathandBeyond/105550816708143p?\$imagePLP\$&wid=256&hei=256", type = 0))
        return prizes
    }
}