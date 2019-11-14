package com.data.repository

import com.appster.core.adapter.DisplayableItem
import com.appster.features.points.DailyBonus
import com.appster.features.points.MysteryBoxData
import com.appster.features.points.Prize
import com.appster.pocket.CreditsModel
import com.appster.webservice.response.BaseResponse
import com.apster.common.Constants
import com.data.entity.DailyBonusEntity
import com.data.entity.MysteryBoxEntity
import com.data.entity.mapper.PointSystemMapper
import com.data.entity.requests.SubmitRedemptionEntity
import com.data.exceptions.BeLiveServerException
import com.data.repository.datasource.PointSystemDataSource
import com.domain.models.EarnPointsModel
import com.domain.models.PrizeCollectModel
import com.domain.models.TreatCollectModel
import com.domain.repository.PointSystemRepository
import rx.Observable
import java.lang.IllegalArgumentException
import javax.inject.Inject

/**
 *  Created by DatTN on 10/24/2018
 */
class PointSystemDataRepository @Inject constructor(private val mDataSource: PointSystemDataSource,
                                                    private val mPointSystemMapper: PointSystemMapper) : PointSystemRepository {


    override fun earnPoints(actionType: String, slug: String, mode: Int): Observable<EarnPointsModel> {
        return mDataSource.earnPoints(actionType, slug, mode)
                .flatMap { t ->
                    when (t.code) {
                        Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(mPointSystemMapper.map(t.data))
                        else -> Observable.error(BeLiveServerException(t.message, t.code))
                    }
                }
    }

    override fun loadMysteryBoxData(): Observable<MysteryBoxData> {
        // Merge data from 2 observable
        return Observable.zip(getDailyBonus(), getBoxList(), loadUserPoint()) { dailyBonus, mysteryBoxes, creditModel ->
            val data = MysteryBoxData()
            data.dailyBonus = dailyBonus
            data.mysteryBoxes = mysteryBoxes
            creditModel?.apply {
                data.userPoint = totalPoint
                if (pointInfoUrl != null)
                    data.pointInfoUrl = pointInfoUrl
                data.creditsModel = creditModel
            }
            data
        }
    }

    private fun getBoxList(): Observable<List<DisplayableItem>> {
        return mDataSource.loadBoxList().flatMap {
            when (it.code) {
                Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(mPointSystemMapper.map(it.data))
                else -> Observable.error(BeLiveServerException(it.message, it.code))
            }
        }
    }

    private fun getDailyBonus(): Observable<DailyBonus> {
//        return mDataSource.loadDailyBonus().map { mPointSystemMapper.map(it.data) }

        return Observable.zip(mDataSource.loadDailyBonusPrizeItems(), mDataSource.loadDailyBonusCountDown()) { dailyBonusList, countDownInfo ->
            val dailyBonusItem = mPointSystemMapper.map(dailyBonusList.data)
            dailyBonusItem.countDown = countDownInfo.data.nextTimeSeconds
            dailyBonusItem
        }
    }

    override fun loadUserPoint(): Observable<CreditsModel> {
//        return mDataSource.loadUserPoint().map { it.data }
        return mDataSource.loadUserPoint().flatMap {
            when (it.code) {
                Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(it.data)
                else -> Observable.error(BeLiveServerException(it.message, it.code))
            }
        }
    }

    override fun loadPrizeList(boxType: Int, boxId: Int): Observable<List<Prize>> {
        return when (boxType) {
            MysteryBoxEntity.TYPE_DAILY_BONUS -> loadDailyBonusItems()
            MysteryBoxEntity.TYPE_MYSTERY_BOX -> loadPrizeItems(boxId)
            else -> Observable.error(IllegalArgumentException("Box Type not supported: $boxType"))
        }
    }

    override fun loadUserPrizeCount(): Observable<Int> {
        return mDataSource.loadUserPrizeInfo().map {
            it?.data?.prizeCount ?: 0
        }
    }

    private fun loadPrizeItems(boxId: Int): Observable<List<Prize>> {
        return mDataSource.loadPrizeItems(boxId).flatMap {
            when (it.code) {
                Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(mPointSystemMapper.mapPrizes(it.data))
                else -> Observable.error(BeLiveServerException(it.message, it.code))
            }
        }
    }

    override fun loadPrizeBagList(): Observable<List<DisplayableItem>> {
        return mDataSource.loadPrizeBag()
                .flatMap { t ->
                    when (t.code) {
                        Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(t.data)
                        else -> Observable.error(BeLiveServerException(t.message, t.code))
                    }
                }
                .map { response -> mPointSystemMapper.transform(response) }
    }

    override fun submitRedemption(bagItemId: Int, name: String, email: String): Observable<Boolean> {
        return mDataSource.submitRedemption(bagItemId, name, email)
                .flatMap { t ->
                    when (t.code) {
                        Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(t.data)
                        else -> Observable.error(BeLiveServerException(t.message, t.code))
                    }
                }
    }

    override fun pickPrizeBagItem(idPrize: Int): Observable<String> {
        return mDataSource.pickPrizeBagItem(idPrize)
                .flatMap { t ->
                    when (t.code) {
                        Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(t.data)
                        else -> Observable.error(BeLiveServerException(t.message, t.code))
                    }
                }
    }

    private fun loadDailyBonusItems(): Observable<List<Prize>> {
        return mDataSource.loadDailyBonusPrizeItems().flatMap {
            when (it.code) {
                Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(mPointSystemMapper.mapDailyBonusPrizes(it.data))
                else -> Observable.error(BeLiveServerException(it.message, it.code))
            }
        }
    }

    override fun loadDailyBonusCountDown(): Observable<Int> {
        return mDataSource.loadDailyBonusCountDown().map {
            it.data?.nextTimeSeconds ?: 0
        }
    }

    override fun openMysteryBox(boxId: Int): Observable<PrizeCollectModel> {
        return pickMysteryBox(boxId).flatMap {
            Observable.zip(Observable.just(it), loadUserPoint()) { collectModel, creditsModel ->
                PrizeCollectModel(collectModel.id, collectModel.title, collectModel.description, collectModel.image, creditsModel.totalPoint, collectModel.amount)
            }
        }
    }

    private fun pickMysteryBox(boxId: Int): Observable<TreatCollectModel> {
        return mDataSource.openMysteryBox(boxId).flatMap {
            when (it.code) {
                Constants.RESPONSE_FROM_WEB_SERVICE_OK -> Observable.just(mPointSystemMapper.mapPickedPrize(it.data))
                else -> Observable.error(BeLiveServerException(it.message, it.code))
            }
        }
    }
}